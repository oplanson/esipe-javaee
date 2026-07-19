# Plan — Site web des Cours Jakarta EE (02-Lectures/docs/)

## Objectif

Générer un site HTML statique complet pour les 12 cours du répertoire `02-Lectures/`,
en reprenant le design system IBM Carbon des labs, à partir du contenu Markdown (slides Marp)
et des images PNG pré-rendues. Un skill `/lecture-doc` sera créé pour automatiser la génération.

## Scope

- **Source :** `02-Lectures/*.md` + `02-Lectures/images/*.png`
- **Cible :** `02-Lectures/docs/` — index global + 1 dossier par cours
- **Design :** IBM Carbon (identique aux labs `03-Labs/*/solution/docs/`)
- **Langue :** Français pour les labels/titres, anglais pour le code
- **Pas de JS** — CSS-only pour l'interactivité (accordion, code highlight)

---

## Structure cible

```
02-Lectures/docs/
  assets/carbon.css           ← Design tokens Carbon commun (copie depuis les labs)
  index.html                  ← Index global — cartes par groupe thématique

  01-intro/
    index.html                ← Vue d'ensemble (objectifs, plan, tags)
    slides.html               ← Toutes les sections — accordion CSS-only
    concepts.html             ← Tableaux, définitions, spécifications
    code.html                 ← Blocs code Java avec highlight CSS-only

  02-servlets/         → 4 pages
  02b-jsf/             → 4 pages
  03-jpa/              → 4 pages
  04-cdi/              → 4 pages
  04b-ejb/             → 4 pages
  05-rest/             → 4 pages
  05b-jms/             → 4 pages
  06-ddd/              → 4 pages

  07-hexagonal/
    index.html / slides.html / concepts.html / code.html
    architecture.html   ← Extra: diagramme Ports & Adapters SVG inline

  08-microservices/
    index.html / slides.html / concepts.html / code.html
    patterns.html       ← Extra: patterns (Gateway, Circuit Breaker, Saga...)

  09-security/         → 4 pages
```

---

## Groupes thématiques (index global)

| Groupe | Cours |
|--------|-------|
| 🚀 Fondations Jakarta EE | 01-intro, 02-servlets, 02b-jsf |
| 💾 Data & Business Layer | 03-jpa, 04-cdi, 04b-ejb |
| 🌐 Services & Messagerie | 05-rest, 05b-jms |
| 🏛️ Architectures Avancées | 06-ddd, 07-hexagonal, 08-microservices |
| 🔐 Sécurité | 09-security |

---

## Pages par cours

| Page | Contenu |
|------|---------|
| `index.html` | Vue d'ensemble : titre du cours, objectifs pédagogiques, plan, tags technologies, nav |
| `slides.html` | Sections du cours en accordion `<details>/<summary>` — première section `open`, les autres fermées — avec images PNG inline |
| `concepts.html` | Tableaux comparatifs, définitions clés, spécifications extraites des sections théoriques |
| `code.html` | Blocs Java extraits avec titre de section — `<pre><code>` fond sombre Carbon `#161616`, monospace, pas de coloration syntaxique |
| `architecture.html` | (07 seulement) Diagramme Ports & Adapters SVG inline |
| `patterns.html` | (08 seulement) Patterns microservices avec SVG inline |

---

## Conventions de design (Carbon)

- **Header** : `background #161616`, `color #f4f4f4`, hauteur `3rem`, sticky
- **Side-nav** : `background #262626`, largeur `16rem`, liens avec `.active` en `#0f62fe`
- **Fond** : `#ffffff` (main), `#f4f4f4` (tiles/accordion headers)
- **Borders** : `#e0e0e0`
- **Tags** : blue / green / purple / teal / red / warm (même palette que les labs)
- **Code blocks** : fond `#161616`, texte `#f4f4f4`, tokens colorés CSS-only
- **Images** : path `../../images/{slug}-diagram-N.png` depuis chaque sous-dossier cours
- **Back-link** : dans chaque `index.html` de cours → `../index.html` (index global)
- L'`index.html` global n'a PAS de back-link (c'est le point d'entrée)

---

## Sub-tâches

### T1 — Créer le skill `/lecture-doc`
**Status :** [ ] pending

**Intent :** Documenter la logique de génération pour pouvoir régénérer ou étendre le site facilement.

**Expected Outcomes :**
- Fichier `.bob/skills/lecture-doc/SKILL.md` créé
- Le skill décrit : sources, cible, structure, règles de contenu par page, conventions de design

**Todo :**
1. Créer `.bob/skills/lecture-doc/SKILL.md` avec frontmatter + instructions complètes
2. Y décrire : comment parser le Markdown Marp, quoi mettre dans chaque page, règles CSS accordion

**Relevant Context :**
- Skill existant similaire : `.bob/skills/java-doc/SKILL.md`

---

### T2 — Créer le CSS partagé `assets/carbon.css`
**Status :** [ ] pending

**Intent :** Extraire les tokens CSS Carbon dans un fichier commun réutilisé par toutes les pages du site cours.

**Expected Outcomes :**
- `02-Lectures/docs/assets/carbon.css` créé
- Contient : CSS reset, tokens, header, side-nav, tiles, tags, data-table, code blocks, accordion

**Todo :**
1. Créer `02-Lectures/docs/assets/`
2. Créer `carbon.css` en reprenant les tokens des labs + ajout styles accordion et code highlight

**Relevant Context :**
- Source design : `03-Labs/Lab01-FirstServlet/solution/docs/assets/carbon.css`

---

### T3 — Générer l'index global `02-Lectures/docs/index.html`
**Status :** [ ] pending

**Intent :** Page d'entrée du site des cours avec cartes Carbon groupées par thème.

**Expected Outcomes :**
- `02-Lectures/docs/index.html` créé
- 5 groupes thématiques en sections, 12 cartes cours avec titre, résumé, tags, lien

**Todo :**
1. Créer `index.html` avec header Carbon, hero section
2. Générer les 5 groupes avec cartes pour chaque cours
3. Chaque carte pointe vers `./{slug}/index.html`

---

### T4 — Générer les 4 pages pour chaque cours (01 à 09)
**Status :** [ ] pending

**Intent :** Générer `index.html`, `slides.html`, `concepts.html`, `code.html` pour les 12 cours.

**Expected Outcomes :**
- 12 dossiers cours créés, chacun avec 4 pages HTML
- Cours 07 a une 5e page `architecture.html`
- Cours 08 a une 5e page `patterns.html`

**Todo (par cours) :**
1. Lire `02-Lectures/{slug}.md`
2. Extraire : titre, objectifs (tableau), sections (## headings), blocs code java, images PNG
3. Générer `index.html` → titre + objectifs + plan (liste des sections) + tags + side-nav
4. Générer `slides.html` → accordion `<details>/<summary>` par section `## ...` — première section avec attribut `open`, les autres sans
5. Générer `concepts.html` → sections théoriques avec tableaux comparatifs extraits
6. Générer `code.html` → blocs `java` extraits, groupés par section mère
7. Générer pages extra si applicable (07, 08)

**Ordre de génération :** 01 → 02 → 02b → 03 → 04 → 04b → 05 → 05b → 06 → 07 → 08 → 09

**Relevant Context :**
- Sources MD : `02-Lectures/*.md`
- Images : `02-Lectures/images/` (path relatif `../../images/` depuis chaque page cours)
- Slugs :
  - `01-intro-jakartaee-microprofile.md` → `docs/01-intro/`
  - `02-servlets-jsp-microprofile.md` → `docs/02-servlets/`
  - `02b-jsf-javaserver-faces.md` → `docs/02b-jsf/`
  - `03-jpa-database-integration.md` → `docs/03-jpa/`
  - `04-cdi-dependency-injection.md` → `docs/04-cdi/`
  - `04b-ejb-enterprise-java-beans.md` → `docs/04b-ejb/`
  - `05-jaxrs-restful-services.md` → `docs/05-rest/`
  - `05b-jms-enterprise-messaging.md` → `docs/05b-jms/`
  - `06-domain-driven-design.md` → `docs/06-ddd/`
  - `07-hexagonal-architecture.md` → `docs/07-hexagonal/`
  - `08-microservices-architecture.md` → `docs/08-microservices/`
  - `09-jakarta-ee-security.md` → `docs/09-security/`

---

### T5 — Commit & push
**Status :** [ ] pending

**Intent :** Versionner le site complet sur `feature/code-optimization`.

**Expected Outcomes :**
- Commit contenant `02-Lectures/docs/**` + `.bob/skills/lecture-doc/SKILL.md`
- Branch `feature/code-optimization` à jour (push)
- Si le pre-commit hook ajoute des headers → re-stage + 2e commit

**Todo :**
1. `git add 02-Lectures/docs/ .bob/skills/lecture-doc/`
2. `git commit -m "docs(lectures): generate IBM Carbon HTML course site for all 12 lectures"`
3. Si hook échoue → `git add -A && git commit --no-edit`
4. `git push origin feature/code-optimization`

**Relevant Context :**
- Pre-commit hook ajoute `© Copyright 2026 Olivier Planson` — comportement connu, toujours faire 2 commits si nécessaire

---

## Règles de parsing Markdown Marp

- Ignorer le frontmatter YAML (entre `---` et `---` en début de fichier)
- Les séparateurs `---` (après le frontmatter) sont des séparateurs de slides — ignorer pour le web
- `# Titre` → titre principal du cours
- `## Section` → entrée d'accordion dans `slides.html`, section de concepts dans `concepts.html`
- Les blocs `\`\`\`java ... \`\`\`` → extraire pour `code.html`
- Les lignes `![...](images/...)` → transformer en `<img src="../../images/..."/>`
- Les blocs `<details>...</details>` (Mermaid source) → ignorer dans le HTML généré
- Les tableaux Markdown → convertir en `<table class="cds--data-table">`
- Le code inline \`...\` → `<code>...</code>`
- `**...**` → `<strong>...</strong>`

---

## Notes importantes

- Ne PAS utiliser de JavaScript dans les pages
- L'accordion utilise `<details>/<summary>` HTML natif (pas de checkbox-hack)
- Le highlight code est `<pre><code>` simple : fond `#161616`, texte `#f4f4f4`, police `IBM Plex Mono` ou monospace — **pas** de coloration syntaxique
- Les images `../../images/` sont référencées en relatif — elles restent dans `02-Lectures/images/`
- Toutes les pages ont le même side-nav (liens entre les 4-5 pages du cours actif)
- La page active dans le side-nav a la classe `.active`
