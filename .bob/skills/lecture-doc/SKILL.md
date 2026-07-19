---
name: lecture-doc
description: Use when the user asks to generate a course/lecture web site from Markdown slides (Marp format) — types "/lecture-doc", "génère le site des cours", "site cours jakartaee", "documenter les cours". Produces a multi-page IBM Carbon Design HTML site per lecture: overview, slides (accordion), concepts (tables), code (dark pre/code), with extras for Hexagonal (07) and Microservices (08). All content in French.
---

<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# lecture-doc — Jakarta EE Course Site Generator

Generate a complete static HTML site for the course lectures in `02-Lectures/`.
Each Markdown file (Marp slides) is transformed into a set of HTML pages following
the IBM Carbon Design System used in the lab documentation.

---

## Source & Target

| Item | Path |
|------|------|
| Markdown sources | `02-Lectures/*.md` |
| Images (PNG) | `02-Lectures/images/` |
| Output root | `02-Lectures/docs/` |
| Shared CSS | `02-Lectures/docs/assets/carbon.css` |
| Global index | `02-Lectures/docs/index.html` |
| Per-course dir | `02-Lectures/docs/{slug}/` |

**Slugs mapping:**

| MD file | Slug dir |
|---------|----------|
| `01-intro-jakartaee-microprofile.md` | `01-intro/` |
| `02-servlets-jsp-microprofile.md` | `02-servlets/` |
| `02b-jsf-javaserver-faces.md` | `02b-jsf/` |
| `03-jpa-database-integration.md` | `03-jpa/` |
| `04-cdi-dependency-injection.md` | `04-cdi/` |
| `04b-ejb-enterprise-java-beans.md` | `04b-ejb/` |
| `05-jaxrs-restful-services.md` | `05-rest/` |
| `05b-jms-enterprise-messaging.md` | `05b-jms/` |
| `06-domain-driven-design.md` | `06-ddd/` |
| `07-hexagonal-architecture.md` | `07-hexagonal/` |
| `08-microservices-architecture.md` | `08-microservices/` |
| `09-jakarta-ee-security.md` | `09-security/` |

**Skip:** If `02-Lectures/docs/{slug}/` already exists, skip generation for that course.

---

## Step 1 — Parse the Markdown file

Rules for parsing Marp Markdown:

1. **Strip the YAML frontmatter** — everything between the first `---` and the closing `---` (including the `style:` block).
2. **Slide separators** (`---` lines after frontmatter) — treat as section delimiters, ignore for HTML output.
3. **`# Title`** → main course title (first `#` heading).
4. **`## Section`** → accordion entry in `slides.html`, section header in `concepts.html`.
5. **Code fences ` ```java ... ``` `** → extract for `code.html`.
6. **Code fences ` ```bash ... ``` `** → include in `code.html` under "Commandes" section.
7. **`![...](images/foo.png)`** → `<img src="../../images/foo.png" alt="..."/>` (path relative from `docs/{slug}/`).
8. **`<details>...</details>` blocks** (Mermaid source comments) → **ignore entirely**.
9. **Markdown tables** → `<table class="cds--data-table">...</table>`.
10. **`**text**`** → `<strong>text</strong>`.
11. **`` `code` ``** → `<code>code</code>`.
12. **`- item` / `* item`** → `<ul><li>`.
13. **`| | |` tables with ✅ / ❌** (learning objectives) → render as styled objective table on `index.html`.
14. **Sections to identify as "concepts"** (for `concepts.html`): sections whose title contains comparison words like "vs", "Comparison", "Specifications", "Overview", "Architecture", "Patterns", "Types", "Strategies", "Components".

---

## Step 2 — Pages to generate per course

### `index.html` — Vue d'ensemble
- Carbon header + side-nav (link to `../index.html` in header as `← Tous les Cours`).
- Course title (from `# Title`).
- Subtitle (from `## subtitle` directly after the `#`).
- **Metadata tile**: Duration, Instructor (Olivier Planson), Date, Course name (from footer line in frontmatter).
- **Learning objectives table** (from the `## 📋 Learning Objectives` section — the `| ✅ | ... |` table).
- **Course plan** (list of all `## Section` headings as a numbered list).
- **Technology tags** (inferred from course topic: e.g. Jakarta EE, Java 17, MicroProfile, Open Liberty, etc.).

### `slides.html` — Contenu du cours
- Carbon header + side-nav.
- **One `<details>/<summary>` accordion per `## Section`.**
- The **first section is `open`**, all others are closed.
- Inside each accordion: paragraphs, bullet lists, tables, images (path `../../images/`).
- Skip `<details>...</details>` Mermaid source blocks entirely.
- Image `<img>` elements: `style="max-width:100%;margin:1rem 0;"`.

### `concepts.html` — Concepts clés
- Carbon header + side-nav.
- Filter only the sections identified as "concepts" (comparison/overview sections).
- Present each concept section as a `<h2>` with its full content (tables, lists, paragraphs).
- Add a "Glossaire" section at the bottom with key terms extracted from bold text (`**term**`) found in the course.

### `code.html` — Exemples de code
- Carbon header + side-nav.
- Group all code blocks by their parent `## Section` heading.
- Each group: `<h2>` with section name, then `<pre><code>` blocks.
- Style: background `#161616`, text `#f4f4f4`, font `IBM Plex Mono` or monospace — **no syntax highlighting**.
- Add language badge (tag) before each block: `java`, `bash`, `xml`, etc.

### `architecture.html` — Architecture Hexagonale *(Cours 07 only)*
- Full Ports & Adapters diagram as **inline SVG**.
- Description of Primary Ports/Adapters (driving) and Secondary Ports/Adapters (driven).
- Extracted from the relevant `## 🔌 Ports and Adapters` sections.

### `patterns.html` — Patterns Microservices *(Cours 08 only)*
- One section per pattern: API Gateway, Circuit Breaker, Saga, Database per Service, etc.
- Inline SVG diagram for each major pattern.
- Extracted from the relevant pattern sections of the MD.

---

## Step 3 — Side-nav template (adapt `active` class per page)

```html
<header class="cds--header">
  <a class="cds--header__name" href="index.html">{COURSE_TITLE}<span>Cours Jakarta EE</span></a>
  <a href="../index.html" style="margin-left:auto;font-size:.75rem;color:#c6c6c6;text-decoration:none;border:1px solid #393939;padding:.2rem .75rem;border-radius:2px;white-space:nowrap;">← Tous les Cours</a>
</header>
<div class="cds--shell">
  <nav class="cds--side-nav">
    <p class="cds--side-nav__section-title">Cours</p>
    <a class="cds--side-nav__link [active]" href="index.html">⊙ Vue d'ensemble</a>
    <a class="cds--side-nav__link [active]" href="slides.html">☰ Contenu du cours</a>
    <a class="cds--side-nav__link [active]" href="concepts.html">◈ Concepts clés</a>
    <a class="cds--side-nav__link [active]" href="code.html">⟨/⟩ Exemples de code</a>
    <!-- 07 only: -->
    <a class="cds--side-nav__link [active]" href="architecture.html">⬡ Architecture Hexagonale</a>
    <!-- 08 only: -->
    <a class="cds--side-nav__link [active]" href="patterns.html">⧫ Patterns Microservices</a>
  </nav>
  <main class="cds--content">
    <!-- page content here -->
  </main>
</div>
```

---

## Step 4 — CSS file

Copy (or reference) `02-Lectures/docs/assets/carbon.css`.
This file contains IBM Carbon tokens + accordion styles for `<details>/<summary>`:

```css
/* Accordion for slides.html */
details { border: 1px solid #e0e0e0; border-radius: 2px; margin-bottom: .5rem; }
summary {
  background: #f4f4f4; padding: .75rem 1rem; cursor: pointer;
  font-weight: 600; font-size: .9375rem; list-style: none;
  border-left: 3px solid #0f62fe;
}
details[open] summary { background: #e8e8e8; }
details > *:not(summary) { padding: .75rem 1.25rem; }

/* Dark code blocks */
pre.dark { background: #161616; color: #f4f4f4; font-family: "IBM Plex Mono", monospace; padding: 1rem; border-radius: 2px; overflow-x: auto; margin: .75rem 0; font-size: .8em; }
```

---

## Step 5 — Global index `02-Lectures/docs/index.html`

### Structure
- Hero section: title "Cours Jakarta EE & Microservices", subtitle, badges.
- 5 thematic groups with course cards:

| Groupe | Cours |
|--------|-------|
| 🚀 Fondations Jakarta EE | 01-intro, 02-servlets, 02b-jsf |
| 💾 Data & Business Layer | 03-jpa, 04-cdi, 04b-ejb |
| 🌐 Services & Messagerie | 05-rest, 05b-jms |
| 🏛️ Architectures Avancées | 06-ddd, 07-hexagonal, 08-microservices |
| 🔐 Sécurité | 09-security |

- Each card: course number + title, 1-line description, technology tags, link to `{slug}/index.html`.
- No back-link in the index (it's the entry point).
- Carbon header with brand name only.

---

## Step 6 — Validation

After generating all files:
1. List `02-Lectures/docs/` to confirm the directory structure.
2. Report: ✅ files created, per course.
3. Tip: "Ouvre `02-Lectures/docs/index.html` dans un navigateur pour voir le site des cours."

---

## Design Rules (strictly enforced)

- **No JavaScript** — all interactivity via CSS (`<details>/<summary>`).
- **No external URLs** in `src`, `href` (except `<a href>` links).
- **No `<script>` tags**.
- **Inline SVG only** for diagrams — no `<img src="data:...">` base64 blobs.
- **Images** from `02-Lectures/images/`: path `../../images/{filename}` from any `docs/{slug}/` page.
- **All content in French** (labels, headings, descriptions) — code stays in English.
- **Footer**: `<footer class="cds--footer">Made with IBM Bob · © 2026 Olivier Planson</footer>`.
- **Copyright comment** at top of every file: `<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->`.
