---
name: java-doc
description: Use when the user asks to document a Jakarta EE or Java application — types "/java-doc", "fais une documentation", "génère la doc", "documenter l'application java". Produces a multi-page IBM Carbon Design HTML site with functional architecture, technical architecture, sequence diagrams (inline SVG), business rules (R1-RN), and optional DDD/Hexagonal sections when detected.
---

<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->


# java-doc — Jakarta EE Application Documentation

Generate a complete static HTML documentation site for a Jakarta EE / Java application.
The output follows the same structure as `generate-documentation` but adds Jakarta EE-specific
sections (layers, annotations, bounded contexts, ports & adapters) when relevant.

---

## Step 1 — Locate the application

Use `ask_followup_question` if no path is provided.
Otherwise resolve the Maven/Gradle project root automatically:
1. Use `glob` with `**/pom.xml` (or `**/build.gradle`) to find project roots.
2. If several are found, ask the user which one to document.
3. Note the project root as `$APP_ROOT`.

---

## Step 2 — Explore and analyse the codebase

Run ALL of these in parallel using independent tool calls:

| What to collect | Tool |
|---|---|
| Java source files | `glob "$APP_ROOT/src/**/*.java"` |
| Jakarta EE config | `glob "$APP_ROOT/src/**/web.xml"`, `persistence.xml`, `beans.xml`, `ejb-jar.xml` |
| REST resources | `grep "@Path\|@GET\|@POST\|@PUT\|@DELETE\|@PATCH"` in src |
| JPA entities | `grep "@Entity\|@Table\|@MappedSuperclass"` in src |
| CDI beans | `grep "@ApplicationScoped\|@RequestScoped\|@SessionScoped\|@Dependent\|@Named"` in src |
| EJB beans | `grep "@Stateless\|@Stateful\|@Singleton\|@MessageDriven"` in src |
| Servlets | `grep "@WebServlet\|HttpServlet"` in src |
| Security | `grep "@RolesAllowed\|@PermitAll\|@DenyAll\|@LoginConfig"` in src |
| DDD markers | `grep "domain\|application\|infrastructure\|ports\|adapters\|bounded"` (case-insensitive) in package paths |
| Hexagonal markers | `grep "port\|adapter\|usecase\|driven\|driving"` (case-insensitive) in package paths |
| `pom.xml` | `read_file` — extract `<artifactId>`, `<groupId>`, `<dependencies>` |

Build an in-memory model with:
- **Application name** (from `artifactId`)
- **Tech stack** (Jakarta EE version, Java version, key dependencies)
- **Layer map**: Presentation / Business / Persistence / Integration
- **Component list**: Servlets, REST resources, EJBs, CDI beans, JPA entities, JMS endpoints
- **DDD flag**: `true` if domain/application/infrastructure packages detected
- **Hexagonal flag**: `true` if port/adapter/usecase packages detected

---

## Step 3 — Determine the output directory

Default: `$APP_ROOT/docs/`
Ask the user only if they have specified a custom location.
Create the directory with `mcp__filesystem__create_directory`.

---

## Step 4 — Copy the Carbon CSS asset

Check if `$APP_ROOT/docs/assets/carbon.css` already exists.
If not, read the nearest existing `carbon.css` from the workspace
(e.g. `03-Labs/Lab01-FirstServlet/docs-java/assets/carbon.css`) and write a copy.

---

## Step 5 — Generate the HTML pages

Write all pages in parallel. Each page MUST:
- Be a fully self-contained HTML5 document.
- Link `assets/carbon.css` for styling.
- Include the shared header + side-nav (see template below).
- Use only inline SVG for diagrams — no external images, no `<script>`, no `<iframe>`.
- Be in the language the user is communicating in (French by default in this workspace).

### Shared nav template (adapt active class per page)

```html
<header class="cds--header">
  <a class="cds--header__name" href="index.html">
    {APP_NAME}<span>Documentation Technique</span>
  </a>
</header>
<div class="cds--shell">
  <nav class="cds--side-nav" aria-label="Navigation">
    <p class="cds--side-nav__section-title">Documentation</p>
    <a class="cds--side-nav__link [active]" href="index.html"><i class="icon">⊙</i> Vue d'ensemble</a>
    <a class="cds--side-nav__link [active]" href="functional.html"><i class="icon">⊞</i> Architecture Fonctionnelle</a>
    <a class="cds--side-nav__link [active]" href="technical.html"><i class="icon">⊟</i> Architecture Technique</a>
    <a class="cds--side-nav__link [active]" href="sequences.html"><i class="icon">⇄</i> Diagrammes de Séquences</a>
    <a class="cds--side-nav__link [active]" href="rules.html"><i class="icon">≡</i> Règles de Gestion</a>
    <!-- Add only if DDD flag = true: -->
    <a class="cds--side-nav__link [active]" href="ddd.html"><i class="icon">◈</i> Bounded Contexts (DDD)</a>
    <!-- Add only if Hexagonal flag = true: -->
    <a class="cds--side-nav__link [active]" href="hexagonal.html"><i class="icon">⬡</i> Architecture Hexagonale</a>
  </nav>
  <main class="cds--content">
    <!-- page content -->
  </main>
</div>
```

---

### Page specifications

#### `index.html` — Vue d'ensemble
- Project overview card: name, Java version, Jakarta EE version, packaging (WAR/JAR/EAR).
- Technology table: dependency → role.
- Quick links to all pages.

#### `functional.html` — Architecture Fonctionnelle
- One section per functional domain (inferred from package names / layer grouping).
- For each component: name, type (Servlet / REST / EJB / CDI / JMS), responsibility.
- Inline SVG block diagram showing domain groupings.

#### `technical.html` — Architecture Technique
- Layer diagram (inline SVG): Presentation → Business → Persistence → Integration.
- For each layer: Jakarta EE APIs used (`@WebServlet`, JAX-RS, JPA, EJB, CDI, JMS…).
- Infrastructure table: application server, database (from `persistence.xml`), messaging.
- Deployment view: WAR/EAR structure.

#### `sequences.html` — Diagrammes de Séquences
- One sequence diagram (inline SVG) per major use case (detected from REST endpoints or Servlet `doGet`/`doPost` methods).
- Show the flow: Browser/Client → Servlet or REST Resource → Service/EJB → Repository/DAO → DB.
- Minimum 3 diagrams; more if the app warrants it.

#### `rules.html` — Règles de Gestion
- Numbered rules: **R1**, **R2**, … **RN**.
- Extract from: validation annotations (`@NotNull`, `@Size`, etc.), security annotations, business logic in service methods.
- Each rule: ID | Description | Source class/method | Jakarta EE mechanism.

#### `ddd.html` — Bounded Contexts *(only if DDD flag = true)*
- One card per bounded context (inferred from package structure).
- Contents: aggregates, entities, value objects, domain services, domain events.
- Ubiquitous language table extracted from class names.
- Anti-Corruption Layer or shared kernel notes if detected.

#### `hexagonal.html` — Architecture Hexagonale *(only if Hexagonal flag = true)*
- Ports & Adapters diagram (inline SVG hexagon).
- Primary adapters (driving): REST controllers, Servlets, CLI.
- Secondary adapters (driven): JPA repositories, JMS producers/consumers, external clients.
- Use-case listing from application layer.

---

## Step 6 — Validate and report

After writing all files:
1. List the generated files with `list_files $APP_ROOT/docs/`.
2. Report to the user:
   - ✅ Files generated (with relative paths).
   - 📋 Sections included (mark DDD/Hexagonal as "(detected)" or "(not applicable)").
   - 💡 Tip: "Open `docs/index.html` in a browser to view the documentation."
