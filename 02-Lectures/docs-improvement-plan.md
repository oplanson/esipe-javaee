<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Plan d'amélioration des docs/ — Jakarta EE & Microservices

## Objectif

Améliorer la qualité des pages `docs/` du cours Jakarta EE & Microservices sur cinq axes :

1. **Infrastructure bilingue EN/FR** — bouton toggle EN/FR côté client (sans rechargement) dans le header ET la sidebar, persisté via `localStorage`, avec attributs `data-en`/`data-fr` sur tous les textes.
2. **Sidebar rétractable** — la sidebar se replie automatiquement lorsqu'elle n'est pas survolée (CSS pur), conservant un rail de 3rem avec icônes visibles.
3. **Correction de contenu** — corriger les erreurs techniques factuelles, les exemples de code incomplets ou incorrects, et les claims non justifiés.
4. **Qualité des traductions FR** — remplacer les traductions malformées (mélange EN/FR) par des phrases idiomatiques françaises correctes.
5. **Skill local** — documenter la méthode de revue dans `.bob/skills/review-html-slides/SKILL.md`.

## Périmètre

- **Cible** : `esipe-javaee/02-Lectures/docs/` (12 dossiers, ~50 fichiers HTML)
- **Hors périmètre** : `slides/` (source générée), réorganisation des sections de contenu
- **Langue principale** : anglais (EN), français disponible via bouton (FR)
- **Stratégie** : 1 sous-tâche par groupe thématique + 1 agent chapeau + 1 skill

## Architecture de la solution bilingue + sidebar

```
docs/assets/carbon.css          ← sidebar rétractable + styles bouton toggle
docs/assets/language-toggle.js  ← NOUVEAU — logique EN/FR client-side
docs/index.html                 ← ajouter bouton + data-en/data-fr
docs/{chapitre}/*.html          ← ajouter bouton sidebar + data-en/data-fr sur tous les textes
```

### Mécanique du bouton EN/FR

- Bouton `<button class="lang-toggle-btn" id="lang-toggle">` dans le `<header>` (à droite, avant `.cds--header__back`)
- Un second élément `<a class="cds--side-nav__lang-toggle">` en bas de chaque `<nav class="cds--side-nav">`
- Au clic : bascule `document.documentElement.lang` entre `en` et `fr`
- Pour chaque élément `[data-en][data-fr]` : remplace `textContent` (ou `innerHTML` si HTML riche)
- Persistance : `localStorage.setItem('lang', 'en'|'fr')`
- Initialisation : `DOMContentLoaded`, fallback `'en'`
- Le bouton affiche l'état opposé : lang=`en` → bouton dit `FR`, lang=`fr` → bouton dit `EN`

### Mécanique de la sidebar rétractable (CSS pur)

État actuel :
- `.cds--side-nav` : `width: 16rem`, `position: sticky`, `top: 3rem`
- Mobile (`max-width: 768px`) : `display: none`

Comportement cible :
- Au repos : sidebar repliée à **3rem**, texte masqué (`overflow: hidden`), icônes (`⊙ ☰ ◈ ⟨/⟩`) visibles
- Au survol/focus : déplié à **16rem** via `transition: width 0.2s ease`
- Remplacement du `display: none` mobile par `width: 0` (cohérent avec la mécanique de repli)
- Aucun JS requis — CSS pur

```css
/* Changements dans carbon.css */
.cds--side-nav {
  width: 3rem;
  transition: width 0.2s ease;
  overflow: hidden;
  white-space: nowrap;
}
.cds--side-nav:hover,
.cds--side-nav:focus-within { width: 16rem; }
@media (max-width: 768px) {
  .cds--side-nav { width: 0; }   /* remplace display:none */
}
```

### Groupes thématiques pour l'exécution

| Groupe | Chapitres | Raison |
|--------|-----------|--------|
| **G1 — Fondations** | 01-intro, 02-servlets, 02b-jsf | Web layer Jakarta EE |
| **G2 — Data & Beans** | 03-jpa, 04-cdi, 04b-ejb | Persistence et injection |
| **G3 — Services** | 05-rest, 05b-jms | APIs et messagerie |
| **G4 — Architectures** | 06-ddd, 07-hexagonal, 08-microservices | Patterns avancés liés |
| **G5 — Sécurité** | 09-security | Chapitre final autonome |

---

## Sous-tâche 0 — Infrastructure partagée (sidebar + toggle EN/FR)

**Intent** : Créer `language-toggle.js`, mettre à jour `carbon.css` (sidebar rétractable + styles toggle), et instrumenter `docs/index.html` comme page pilote validée avant de traiter les groupes.

**Expected Outcomes** :
- `docs/assets/language-toggle.js` créé : init `DOMContentLoaded`, `applyLang(lang)`, click handlers, `localStorage`
- `docs/assets/carbon.css` mis à jour :
  - `.cds--side-nav` : `width: 3rem` au repos, `transition: width 0.2s ease`, `16rem` au `:hover`/`:focus-within`
  - Mobile : `width: 0` (remplace `display: none`)
  - Style `.lang-toggle-btn` dans le header (position droite, avant `.cds--header__back`)
  - Style `.cds--side-nav__lang-toggle` (lien en bas de sidebar)
- `docs/index.html` instrumenté : bouton toggle header, `data-en`/`data-fr` sur tous les textes, `<script src="assets/language-toggle.js">`
- La page index bascule EN ↔ FR sans rechargement
- Note : `docs/index.html` n'a pas de sidebar — le repli ne s'applique qu'aux pages chapitres

**Todo List** :
- [ ] Créer `docs/assets/language-toggle.js` (init, `applyLang`, click, `localStorage`)
- [ ] Mettre à jour `.cds--side-nav` dans `carbon.css` : `width: 3rem`, transition, hover/focus-within
- [ ] Corriger le `@media (max-width: 768px)` : `width: 0` au lieu de `display: none`
- [ ] Ajouter style `.lang-toggle-btn` dans `carbon.css`
- [ ] Ajouter style `.cds--side-nav__lang-toggle` dans `carbon.css`
- [ ] Mettre à jour `docs/index.html` : bouton toggle, `data-en`/`data-fr`, script tag

**Relevant Context** :
- [`docs/assets/carbon.css`](esipe-javaee/02-Lectures/docs/assets/carbon.css) L53–125 — header et sidebar (aucune transition existante)
- [`docs/01-intro/index.html`](esipe-javaee/02-Lectures/docs/01-intro/index.html) L11–22 — structure HTML header + sidebar de référence
- [`docs/index.html`](esipe-javaee/02-Lectures/docs/index.html) — page pilote sans sidebar

**Status** : `[ ] pending`

---

## Sous-tâche G1 — Groupe Fondations (01-intro, 02-servlets, 02b-jsf)

**Intent** : Instrumenter les 12 fichiers des chapitres 1–3 avec le toggle EN/FR + sidebar rétractable, et corriger leur contenu.

**Expected Outcomes** :
- Script toggle + bouton header + lien sidebar sur tous les fichiers des 3 chapitres
- `data-en`/`data-fr` idiomatiques sur tous les textes (h1–h3, p, li, th, td, summary, alt)
- **01-intro** :
  - Imports Jakarta (`jakarta.enterprise.context.*`, `org.eclipse.microprofile.*`) ajoutés dans les exemples MicroProfile Config/Health/Metrics de `code.html`
  - Claim "Open Liberty has better MicroProfile support" qualifié ou supprimé dans `slides.html`
  - Traductions corrigées (ex. `"Enterprise Java Développement with Open Liberty"`)
- **02-servlets** :
  - Imports `jakarta.servlet.*` présents dans tous les exemples de `code.html`
  - Lifecycle Servlet (`init`, `service`, `destroy`) correctement décrit
- **02b-jsf** :
  - Distinction `@ManagedBean` (legacy JSF) vs `@Named` (CDI) clarifiée dans `concepts.html`
  - Scopes JSF vs CDI (`@ViewScoped` → `jakarta.faces.view` vs `jakarta.enterprise.context`) explicités

**Todo List** :
- [ ] Pour chaque fichier des 3 chapitres : ajouter `<script src="../assets/language-toggle.js">`, bouton toggle dans `<header>`, lien toggle en bas de `<nav class="cds--side-nav">`
- [ ] 01-intro : `data-en`/`data-fr` sur tous les textes ; corriger imports dans `code.html` ; qualifier claim dans `slides.html`
- [ ] 02-servlets : `data-en`/`data-fr` ; vérifier `@WebServlet`, imports, lifecycle dans `code.html`
- [ ] 02b-jsf : `data-en`/`data-fr` ; clarifier scopes dans `concepts.html`
- [ ] Corriger toutes les traductions FR malformées dans les 12 fichiers

**Relevant Context** :
- `docs/01-intro/`, `docs/02-servlets/`, `docs/02b-jsf/` — 4 fichiers HTML chacun
- ST0 doit être complétée avant G1 (CSS + JS déjà en place)
- Erreurs identifiées slides 10–12 (imports), slide 28 (claim), JSF scope confusion

**Status** : `[ ] pending`

---

## Sous-tâche G2 — Groupe Data & Beans (03-jpa, 04-cdi, 04b-ejb)

**Intent** : Instrumenter les 12 fichiers des chapitres 3–5 avec le toggle EN/FR + sidebar, et corriger leur contenu.

**Expected Outcomes** :
- Script toggle + bouton + sidebar toggle sur tous les fichiers
- `data-en`/`data-fr` sur tous les textes (y compris les `<th>`/`<td>` des tables Carbon)
- **03-jpa** :
  - `persistence.xml` conforme Jakarta Persistence 3.1 / Jakarta EE 10 dans `code.html`
  - Distinction JTA vs resource-local transaction correctement illustrée
  - Note sur les implémentations (EclipseLink/Open Liberty vs Hibernate) dans `concepts.html`
- **04-cdi** :
  - Durée de vie réelle de chaque scope précisée dans `concepts.html`
  - Events CDI synchrones vs asynchrones (`@Observes` vs `@ObservesAsync`) distincts
  - `@Dependent` vs `@ApplicationScoped` : cas d'usage clarifiés
- **04b-ejb** :
  - Note de contexte EJB vs CDI dans les architectures modernes Jakarta EE 10
  - Annotations `@Stateless`, `@Stateful`, `@Singleton`, `@TransactionAttribute` vérifiées dans `code.html`

**Todo List** :
- [ ] Pour chaque fichier des 3 chapitres : ajouter script, bouton, sidebar toggle
- [ ] 03-jpa : `data-en`/`data-fr` ; vérifier JPQL ; ajouter note implémentation ; corriger `persistence.xml`
- [ ] 04-cdi : `data-en`/`data-fr` ; préciser durée de vie scopes ; distinguer sync/async events
- [ ] 04b-ejb : `data-en`/`data-fr` ; ajouter note EJB vs CDI moderne ; vérifier `@TransactionAttribute`
- [ ] Corriger toutes les traductions FR malformées dans les 12 fichiers

**Relevant Context** :
- `docs/03-jpa/concepts.html` — structure de référence inspectée (tables Carbon `cds--data-table`)
- `docs/03-jpa/`, `docs/04-cdi/`, `docs/04b-ejb/` — 4 fichiers HTML chacun

**Status** : `[ ] pending`

---

## Sous-tâche G3 — Groupe Services (05-rest, 05b-jms)

**Intent** : Instrumenter les 8 fichiers des chapitres 5–5b avec le toggle EN/FR + sidebar, et corriger leur contenu.

**Expected Outcomes** :
- Script toggle + bouton + sidebar toggle sur tous les fichiers
- `data-en`/`data-fr` idiomatiques sur tous les textes
- **05-rest** :
  - Placeholder `[course-forum-link]` supprimé de `slides.html`
  - Séquençage "Next lecture" corrigé (DDD vient avant Microservices dans l'ordre du cours)
  - Circular references : `@JsonbTransient` présenté comme solution principale (pas `@JsonbPropertyOrder`) dans `concepts.html`
  - `@RequiresPermissions` / `@PreAuthorize` étiquetés explicitement comme non-standard Jakarta EE (Shiro/Spring) dans `code.html`
- **05b-jms** :
  - Imports `jakarta.jms.*` corrects dans tous les exemples de `code.html`
  - Note sur JMS vs MicroProfile Reactive Messaging dans `concepts.html`

**Todo List** :
- [ ] Pour chaque fichier des 2 chapitres : ajouter script, bouton, sidebar toggle
- [ ] 05-rest : `data-en`/`data-fr` ; supprimer placeholder ; corriger séquençage ; corriger circular references ; étiqueter annotations non-standard
- [ ] 05b-jms : `data-en`/`data-fr` ; vérifier imports JMS 3.0 (Jakarta Messaging) ; ajouter note reactive messaging
- [ ] Corriger toutes les traductions FR malformées dans les 8 fichiers

**Relevant Context** :
- `docs/05-rest/`, `docs/05b-jms/` — 4 fichiers HTML chacun
- Erreurs identifiées : slides 46 (`@JsonbPropertyOrder`), 82 (claim stateful), 84 (séquençage), 86 (placeholder)

**Status** : `[ ] pending`

---

## Sous-tâche G4 — Groupe Architectures (06-ddd, 07-hexagonal, 08-microservices)

**Intent** : Instrumenter les 13 fichiers des chapitres architecturaux avec le toggle EN/FR + sidebar, en maintenant la cohérence cross-chapitres entre DDD, hexagonal et microservices.

**Expected Outcomes** :
- Script toggle + bouton + sidebar toggle sur tous les 13 fichiers
- `data-en`/`data-fr` idiomatiques sur tous les textes
- **06-ddd** :
  - Claim "Records cannot be `@Embeddable` in JPA" qualifié par version Jakarta Persistence et provider (EclipseLink/Open Liberty) dans `concepts.html`
  - Paragraphe context mapping (anti-corruption layer, conformist, customer/supplier) ajouté dans `concepts.html`
  - Traductions corrigées (`"Banking Exemple"` → `"Exemple bancaire"`, `"software Développement"` → `"développement logiciel"`)
- **07-hexagonal** :
  - Ports primaires vs secondaires clairement distingués dans `concepts.html`
  - Exemple de test JUnit sans infrastructure (mock de port) ajouté dans `code.html`
  - Lien croisé vers 06-ddd ajouté dans `index.html`
- **08-microservices** :
  - OpenTracing remplacé ou clairement marqué legacy/déprécié au profit d'OpenTelemetry dans `code.html` et `slides.html`
  - Lien vers la documentation OpenTelemetry ajouté dans `index.html`
  - Lien croisé DDD ↔ décomposition de services dans `concepts.html`
  - Traductions corrigées (`"Avancé topics"` → `"Sujets avancés"`, `"Comment Split a Monolith"`)

**Todo List** :
- [ ] Pour chaque fichier des 3 chapitres : ajouter script, bouton, sidebar toggle
- [ ] 06-ddd : `data-en`/`data-fr` ; corriger section Records + `@Embeddable` ; ajouter context mapping dans `concepts.html`
- [ ] 07-hexagonal : `data-en`/`data-fr` ; ajouter test JUnit dans `code.html` ; vérifier `architecture.html` ; lien croisé DDD
- [ ] 08-microservices : `data-en`/`data-fr` ; corriger/qualifier OpenTracing → OpenTelemetry ; lien OpenTelemetry ; relier DDD ; vérifier `patterns.html`
- [ ] Corriger toutes les traductions FR malformées dans les 13 fichiers

**Relevant Context** :
- `docs/06-ddd/` — 4 fichiers ; `docs/07-hexagonal/` — 5 fichiers (dont `architecture.html`) ; `docs/08-microservices/` — 5 fichiers (dont `patterns.html`)
- Erreur critique 06-ddd : slides 33+37 (Records/JPA `@Embeddable`)
- Erreur critique 08-microservices : slides 49–52 (`io.opentracing.Tracer` obsolète)

**Status** : `[ ] pending`

---

## Sous-tâche G5 — Groupe Sécurité (09-security)

**Intent** : Instrumenter les 4 fichiers du chapitre sécurité avec le toggle EN/FR + sidebar, et corriger son contenu — le plus lacunaire du cours.

**Expected Outcomes** :
- Script toggle + bouton + sidebar toggle sur les 4 fichiers
- `data-en`/`data-fr` idiomatiques sur tous les textes
- `@RequiresPermissions` et `@PreAuthorize` correctement étiquetés "non-standard Jakarta EE" dans `code.html`
- Exemple concret de `IdentityStore` Jakarta Security (avec `@DatabaseIdentityStoreDefinition` ou implémentation custom) ajouté dans `code.html`
- Exemple MicroProfile JWT (`@Claim`, `JsonWebToken`, `@RolesAllowed`) ajouté dans `code.html`
- Lab 9 aligné avec le contenu enseigné (réduire l'écart ambition/base dans `index.html`)
- Traductions FR corrigées : `"Pourquoi Sécurité Matters"` → `"Pourquoi la sécurité est essentielle"`, `"Common Sécurité Threats"` → `"Menaces de sécurité courantes"`, etc.

**Todo List** :
- [ ] Pour chaque fichier : ajouter `<script src="../assets/language-toggle.js">`, bouton toggle header, lien sidebar toggle
- [ ] `data-en`/`data-fr` sur tous les textes des 4 fichiers
- [ ] `code.html` : corriger les annotations non-standard (étiqueter Shiro/Spring) ; ajouter exemple `IdentityStore` complet ; ajouter exemple MicroProfile JWT
- [ ] `index.html` : réduire l'écart entre objectifs du Lab 9 et contenu enseigné
- [ ] Corriger toutes les traductions FR malformées

**Relevant Context** :
- `docs/09-security/` — 4 fichiers HTML
- Erreur critique : `@RequiresPermissions` / `@PreAuthorize` présentés comme Jakarta EE (slides 18–19)
- Écart Lab 9 : objectifs (IdentityStore, JWT, audit log) non couverts dans les slides actuels

**Status** : `[ ] pending`

---

## Sous-tâche 13 — Agent chapeau : cohérence globale

**Intent** : Vérifier la cohérence cross-chapitres après que tous les groupes ont été traités.

**Expected Outcomes** :
- Séquençage des chapitres cohérent dans tous les liens "Cours suivant/précédent" des `index.html`
- Terminologie EN unifiée (ex. `bounded context` — pas de mix `Bounded Context` / `bounded-context`)
- Terminologie FR unifiée (ex. `contexte délimité` utilisé de manière cohérente dans tous les `data-fr`)
- Bouton toggle identique sur toutes les pages (même classe, même position, même script path)
- Aucun placeholder `[xxx]` restant dans les 50+ fichiers
- Footer identique sur toutes les pages
- Sidebar rétractable visuellement cohérente sur tous les chapitres

**Todo List** :
- [ ] `grep -r "\[" docs/ --include="*.html"` — détecter les placeholders restants
- [ ] Vérifier les liens "Cours suivant/précédent" dans tous les `index.html` chapitre
- [ ] Vérifier l'uniformité du bouton toggle (même ID `lang-toggle`, même class `lang-toggle-btn`, même path `../assets/language-toggle.js`)
- [ ] Vérifier la cohérence terminologique EN et FR cross-chapitres (glossaire rapide)
- [ ] Tester que `docs/assets/language-toggle.js` gère correctement les deux niveaux de chemin (`assets/` pour `docs/index.html` vs `../assets/` pour `docs/*/`)

**Relevant Context** :
- Tous les fichiers `docs/*/index.html`
- [`docs/assets/language-toggle.js`](esipe-javaee/02-Lectures/docs/assets/language-toggle.js)
- [`docs/assets/carbon.css`](esipe-javaee/02-Lectures/docs/assets/carbon.css)

**Status** : `[ ] pending`

---

## Sous-tâche 14 — Skill local `.bob/skills/review-html-slides/`

**Intent** : Documenter la méthode de revue des docs HTML dans un skill local réutilisable pour de futures sessions.

**Expected Outcomes** :
- `SKILL.md` créé dans `.bob/skills/review-html-slides/`
- Skill décrit : 3 axes de revue (erreurs techniques, traductions FR, toggle EN/FR)
- Patterns de détection documentés (grep, attributs à vérifier)
- Structure type d'un `language-toggle.js` documentée
- Critères de qualité pour les traductions FR documentés

**Todo List** :
- [ ] Créer `.bob/skills/review-html-slides/SKILL.md` avec frontmatter complet (name, description, tags)
- [ ] Documenter les 3 axes de revue avec exemples concrets issus de ce projet
- [ ] Documenter les patterns grep pour détecter : placeholders, traductions malformées, annotations non-standard
- [ ] Documenter la structure type `language-toggle.js` (init, `applyLang`, handlers, `localStorage`)
- [ ] Documenter la mécanique CSS sidebar rétractable (avec snippet de référence)

**Relevant Context** :
- `.bob/skills/` — répertoire des skills locaux
- Ce plan et toutes les corrections effectuées dans ST0–G5 + ST13

**Status** : `[ ] pending`
