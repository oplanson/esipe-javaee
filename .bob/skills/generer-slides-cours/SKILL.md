---
name: generer-slides-cours
description: Use when the user asks to generate a slide-based course website from Marp Markdown files — types "/generer-slides-cours", "génère le site slides", "site de présentation des cours", "convertir les slides en web", "generer slides cours jakartaee". Reads 02-Lectures/*.md and produces a navigable presentation site in 02-Lectures/slides/ where each Marp slide is rendered faithfully as an individual HTML section, navigable with ← → buttons, CSS :target, and a sticky sidenav TOC. No JS. IBM Carbon Design.
---

<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# generer-slides-cours — Site de Présentation des Cours (Slides Web)

Génère un site HTML statique dans `02-Lectures/slides/` où chaque fichier Marp `.md` devient une
présentation navigable dans le navigateur : une slide = une section HTML, navigation ← / →,
barre de progression, sidenav TOC — sans JavaScript.

---

## Périmètre & Objectif

**Ce skill produit une expérience "PowerPoint dans le navigateur"** — le contenu des slides Marp est
reproduit fidèlement, slide par slide. Il ne génère PAS de documentation de référence (pas de page
concepts, code ou architecture séparée — c'est le rôle du skill `lecture-doc`).

---

## Source & Sortie

| Élément | Chemin |
|---------|--------|
| Sources Markdown | `02-Lectures/*.md` (Marp) |
| Images | `02-Lectures/images/` |
| CSS partagé | `02-Lectures/slides/assets/slide.css` |
| Index global | `02-Lectures/slides/index.html` |
| Par cours : index | `02-Lectures/slides/{slug}/index.html` |
| Par cours : slides | `02-Lectures/slides/{slug}/slides.html` |

**Fichiers .md à traiter** (exclure `IMPLEMENTATION-STATUS.md` et `THEME-USAGE.md`) :

| Fichier MD | Slug |
|------------|------|
| `01-intro-jakartaee-microprofile.md` | `01-intro` |
| `02-servlets-jsp-microprofile.md` | `02-servlets` |
| `02b-jsf-javaserver-faces.md` | `02b-jsf` |
| `03-jpa-database-integration.md` | `03-jpa` |
| `04-cdi-dependency-injection.md` | `04-cdi` |
| `04b-ejb-enterprise-java-beans.md` | `04b-ejb` |
| `05-jaxrs-restful-services.md` | `05-rest` |
| `05b-jms-enterprise-messaging.md` | `05b-jms` |
| `06-domain-driven-design.md` | `06-ddd` |
| `07-hexagonal-architecture.md` | `07-hexagonal` |
| `08-microservices-architecture.md` | `08-microservices` |
| `09-jakarta-ee-security.md` | `09-security` |

---

## Étape 1 — Parsing du fichier Marp `.md`

### 1.1 Supprimer le frontmatter YAML
Retirer tout entre le tout premier `---` et le `---` de fermeture (inclusif), y compris le bloc `style:`.
Extraire de ce frontmatter :
- `footer:` → utilisé pour le titre court du cours (ex. `'Lecture 7: Hexagonal Architecture | © …'`)

### 1.2 Découper en slides
Après suppression du frontmatter, chaque `---` seul sur sa ligne délimite une nouvelle slide.
Résultat : un tableau ordonné de blocs texte (slide 1, slide 2, slide N…).
Numéroter à partir de 1.

### 1.3 Identifier le titre du cours
La première ligne `# Titre` non vide dans l'ensemble du document = titre du cours.

### 1.4 Identifier la table des matières (TOC)
Collecter tous les titres `## Section` **dans l'ordre d'apparition** — ce sont les entrées de la
sidenav. Associer chaque `## Section` à son numéro de slide.

### 1.5 Règles de conversion Markdown → HTML (par slide)

| Pattern Markdown | HTML généré |
|------------------|-------------|
| `# Titre` | `<h1>Titre</h1>` |
| `## Titre` | `<h2>Titre</h2>` |
| `### Titre` | `<h3>Titre</h3>` |
| `**gras**` | `<strong>gras</strong>` |
| `` `code inline` `` | `<code>code inline</code>` |
| `- item` ou `* item` | `<ul><li>item</li></ul>` |
| `1. item` | `<ol><li>item</li></ol>` |
| `> blockquote` | `<blockquote>blockquote</blockquote>` |
| `` ```lang … ``` `` | `<pre class="dark"><code>` avec badge langue (fond clair `#f6f8fa`, texte `#1f2328`, police `"Courier New"`) |
| `![alt](images/foo.png)` | `<img src="../../images/foo.png" alt="alt" style="max-width:85%;max-height:380px;display:block;margin:.75rem auto;">` |
| Table Markdown `\| \| \|` | `<table class="slide-table"><thead>…</thead><tbody>…</tbody></table>` |
| `<table style="border: none">` | Convertir en `<div class="slide-columns">` — extraire chaque `<td>` et passer son contenu par `convertMarkdown()` récursivement |
| `<details>…</details>` (Mermaid) | **Ignoré entièrement** (le PNG correspondant est déjà présent) |
| Ligne vide | `<br>` entre blocs si nécessaire |
| `✅` `⚠️` `❌` dans les listes | Conservés tels quels dans le `<li>` |
| `**text:** value` en début de paragraphe | `<p><strong>text:</strong> value</p>` |

**Blocs `<div class="columns">` :** conserver le CSS Grid — convertir chaque `<div>` interne en
`<div class="slide-col">`, envelopper dans `<div class="slide-columns">`.

Règle de mapping des classes :
- `<div class="columns">` → `<div class="slide-columns">`
- `<div class="columns-3">` → `<div class="slide-columns slide-columns-3">`
- `<div class="columns-2-1">` → `<div class="slide-columns slide-columns-2-1">`
- `<div class="columns-1-2">` → `<div class="slide-columns slide-columns-1-2">`
- `<div>` à l'intérieur d'un bloc columns → `<div class="slide-col">`

**Slides de section** (`# Part N:` sans `##`) : rendre comme slide de titre de partie — `<h1>` en
grand, fond légèrement coloré (classe `slide--section`).

**Slide de titre du cours** (slide 1 contenant `# Titre` + méta-données) :
- Rendre avec classe `slide--title`
- Afficher `**Duration:**`, `**Instructor:**`, `**Date:**`, `**Course:**` dans un bloc `.slide-meta`

**Table des objectifs** (`| ✅ | … |` ou liste `- ✅ …`) : rendre dans un
`<table class="slide-table slide-table--objectives">` ou comme `<ul class="slide-objectives">`.

### 1.6 Commentaires HTML
Supprimer les commentaires HTML (`<!-- … -->`) des slides — ne pas les afficher.

---

## Étape 2 — Générer `02-Lectures/slides/assets/slide.css`

Ce fichier CSS est partagé par tous les fichiers du site. L'écrire **une seule fois** avant de
générer les HTML.

```css
/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */
/* slide.css — IBM Carbon Design + Slide engine */

:root {
  --bg:           #ffffff;
  --layer:        #f4f4f4;
  --border:       #e0e0e0;
  --text:         #161616;
  --muted:        #525252;
  --interactive:  #0f62fe;
  --interactive-h:#0353e9;
  --header-bg:    #161616;
  --header-txt:   #f4f4f4;
  --sidenav-w:    200px;
  --tag-blue-bg:  #d0e2ff; --tag-blue-txt:  #0043ce;
  --tag-green-bg: #defbe6; --tag-green-txt: #0e6027;
  --tag-purple-bg:#e8daff; --tag-purple-txt:#491d8b;
  --tag-teal-bg:  #d9fbfb; --tag-teal-txt:  #004144;
  --tag-red-bg:   #ffd7d9; --tag-red-txt:   #750e13;
  --tag-warm-bg:  #f5e0c3; --tag-warm-txt:  #5d3b1e;
}
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
html { font-size: 16px; }
body {
  font-family: 'IBM Plex Sans','Helvetica Neue',Arial,sans-serif;
  background: var(--layer);
  color: var(--text);
  line-height: 1.6;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ── Header ── */
header.site-header {
  background: var(--header-bg);
  color: var(--header-txt);
  padding: 0 1.5rem;
  height: 3rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  position: sticky;
  top: 0;
  z-index: 200;
  border-bottom: 1px solid #393939;
  flex-shrink: 0;
}
.site-header .brand { font-size: .875rem; font-weight: 600; color: var(--header-txt); text-decoration: none; }
.site-header .brand span { color: #c6c6c6; font-weight: 400; margin-left: .5rem; }
.site-header .back-link {
  margin-left: auto;
  font-size: .75rem;
  color: #c6c6c6;
  text-decoration: none;
  border: 1px solid #393939;
  padding: .2rem .75rem;
  border-radius: 2px;
  white-space: nowrap;
}
.site-header .back-link:hover { background: #262626; }

/* ── Shell layout ── */
.shell {
  display: flex;
  flex: 1;
  min-height: 0;
  position: relative;
}

/* ── Side nav — hidden by default, slides in on hover ── */
.sidenav {
  width: var(--sidenav-w);
  background: var(--bg);
  border-right: 1px solid var(--border);
  position: fixed;
  top: 3rem;
  left: 0;
  height: calc(100vh - 3rem);
  overflow-y: auto;
  padding: 1rem 0 2rem;
  z-index: 100;
  transform: translateX(calc(-1 * var(--sidenav-w)));
  box-shadow: none;
  transition: transform .22s ease, box-shadow .22s ease;
}
.sidenav.sidenav--open {
  transform: translateX(0);
  box-shadow: 4px 0 16px rgba(0,0,0,.10);
}
/* Thin trigger strip — always visible at the left edge */
.sidenav-trigger {
  position: fixed;
  top: 3rem;
  left: 0;
  width: 6px;
  height: calc(100vh - 3rem);
  z-index: 101;
  cursor: pointer;
  background: linear-gradient(to right, rgba(15,98,254,.25), transparent);
}
.sidenav-section { font-size: .6875rem; font-weight: 700; color: var(--muted); text-transform: uppercase; letter-spacing: .06em; padding: .5rem 1rem .25rem; }
.sidenav a {
  display: block;
  padding: .4rem 1rem;
  font-size: .8125rem;
  color: var(--muted);
  text-decoration: none;
  border-left: 3px solid transparent;
  line-height: 1.4;
}
.sidenav a:hover { background: var(--layer); color: var(--text); }
.sidenav a.active { color: var(--interactive); border-left-color: var(--interactive); background: #e8f0fe; font-weight: 600; }
.sidenav hr { border: none; border-top: 1px solid var(--border); margin: .75rem 0; }

/* ── Main content — full width (sidenav is now overlaid) ── */
.content {
  flex: 1;
  padding: 1.5rem 2rem 3rem;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ── Progress bar (thumbnail strip) ── */
.progress-bar {
  display: flex;
  flex-wrap: wrap;
  gap: .35rem;
  margin-bottom: 1.25rem;
  max-width: 900px;
  width: 100%;
}
.progress-bar a {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 1.6rem;
  height: 1.5rem;
  padding: 0 .35rem;
  border: 1px solid var(--border);
  border-radius: 999px;
  font-size: .65rem;
  color: var(--muted);
  text-decoration: none;
  background: var(--bg);
  font-weight: 600;
}
.progress-bar a:hover { background: var(--layer); border-color: var(--interactive); color: var(--interactive); }
.progress-bar a.current { background: var(--interactive); color: #fff; border-color: var(--interactive); }

/* ── Slide display (CSS :target engine) ── */
.slide-wrapper {
  max-width: 900px;
  width: 100%;
}

/* All slides hidden by default */
.slide { display: none; }
/* Show slide targeted by URL anchor */
.slide:target { display: block; }
/* Show slide-1 when no anchor is set (first slide default) */
.slide-wrapper:not(:has(.slide:target)) .slide:first-of-type { display: block; }

/* Slide card */
.slide {
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 4px;
  padding: 2.5rem 3rem;
  min-height: 420px;
  position: relative;
  box-shadow: 0 1px 3px rgba(0,0,0,.06);
}
.slide--title {
  background: linear-gradient(135deg, #f4f4f4 0%, #e8f0fe 100%);
  border-color: var(--interactive);
}
.slide--section {
  background: #e8f0fe;
  border-left: 4px solid var(--interactive);
}
.slide-counter {
  position: absolute;
  bottom: 1rem;
  right: 1.25rem;
  font-size: .7rem;
  color: var(--muted);
}

/* Slide typography */
.slide h1 { font-size: 1.75rem; font-weight: 300; margin-bottom: .75rem; color: var(--text); }
.slide h2 { font-size: 1.2rem; font-weight: 600; margin: 1rem 0 .5rem; color: var(--text); }
.slide h3 { font-size: 1rem; font-weight: 600; margin: .75rem 0 .35rem; color: var(--muted); }
.slide p { margin: .4rem 0; font-size: .9375rem; }
.slide ul, .slide ol { margin: .4rem 0 .4rem 1.25rem; font-size: .9rem; }
.slide li { margin: .2rem 0; line-height: 1.6; }
.slide strong { font-weight: 600; }
.slide code { font-family: "Courier New",Courier,monospace; font-size: .85em; background: #f0f0f0; color: #1f2328; padding: .1em .3em; border-radius: 2px; }
.slide blockquote { border-left: 3px solid var(--interactive); padding: .5rem 1rem; color: var(--muted); font-style: italic; margin: .75rem 0; }
.slide img { max-width: 85%; max-height: 380px; display: block; margin: .75rem auto; }

/* Meta block on title slide */
.slide-meta { margin-top: 1.5rem; display: flex; flex-wrap: wrap; gap: .5rem 2rem; font-size: .875rem; color: var(--muted); }
.slide-meta span strong { color: var(--text); }

/* Light code blocks — black text on white/light-grey, always readable */
.slide pre.dark {
  background: #f6f8fa;
  color: #1f2328;
  font-family: "Courier New",Courier,monospace;
  border: 1px solid #d0d7de;
  padding: 1rem 1.25rem;
  border-radius: 4px;
  overflow-x: auto;
  margin: .75rem 0;
  font-size: .8em;
  line-height: 1.6;
}
.lang-badge {
  display: inline-block;
  padding: .1rem .5rem;
  border-radius: 3px;
  font-size: .65rem;
  font-weight: 700;
  background: #ddf4ff;
  color: #0550ae;
  margin-bottom: .35rem;
  font-family: "Courier New",Courier,monospace;
  border: 1px solid #b6e3ff;
}

/* Tables */
.slide-table { width: 100%; border-collapse: collapse; margin: .75rem 0; font-size: .875rem; border: 1px solid var(--border); border-radius: 4px; overflow: hidden; }
.slide-table thead { background: #e8f0fe; }
.slide-table th { padding: .5rem .85rem; text-align: left; border-bottom: 2px solid #c7d9fb; font-weight: 700; font-size: .8125rem; color: #0043ce; letter-spacing: .01em; }
.slide-table th:not(:last-child) { border-right: 1px solid #c7d9fb; }
.slide-table tbody tr:nth-child(even) { background: #f7f9ff; }
.slide-table tbody tr:hover { background: #eef3ff; }
.slide-table td { padding: .4rem .85rem; border-bottom: 1px solid var(--border); vertical-align: top; }
.slide-table td:not(:last-child) { border-right: 1px solid var(--border); }
.slide-table tbody tr:last-child td { border-bottom: none; }
.slide-table--objectives td:first-child { width: 2rem; text-align: center; font-size: 1rem; }

/* Objectives list */
.slide-objectives { list-style: none; margin-left: 0; }
.slide-objectives li { padding: .3rem 0 .3rem .25rem; border-bottom: 1px solid var(--border); font-size: .9rem; }
.slide-objectives li:last-child { border-bottom: none; }

/* Columns layout (from Marp) */
.slide-columns { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; align-items: start; margin: .5rem 0; }
.slide-columns.slide-columns-3 { grid-template-columns: 1fr 1fr 1fr; gap: 1rem; }
.slide-columns.slide-columns-2-1 { grid-template-columns: 2fr 1fr; }
.slide-columns.slide-columns-1-2 { grid-template-columns: 1fr 2fr; }
.slide-col { min-width: 0; }

/* ── Navigation buttons ── */
/* Nav bars live inside each .slide section — visible when the slide is :target */
.slide-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-top: 1.5rem;
  padding-top: .75rem;
  border-top: 1px solid var(--border);
}
.slide-nav a {
  display: inline-flex;
  align-items: center;
  gap: .35rem;
  padding: .4rem 1.1rem;
  background: var(--interactive);
  color: #fff;
  text-decoration: none;
  border-radius: 999px;
  font-size: .8125rem;
  font-weight: 600;
}
.slide-nav a:hover { background: var(--interactive-h); }
.slide-nav a.disabled { background: var(--border); color: var(--muted); pointer-events: none; cursor: default; }
.slide-nav .slide-info { font-size: .8rem; color: var(--muted); text-align: center; }

/* ── Index page (home) ── */
.hero {
  background: var(--bg);
  border-bottom: 1px solid var(--border);
  padding: 3rem 2rem 2.5rem;
  text-align: center;
}
.hero h1 { font-size: clamp(1.5rem,3vw,2.25rem); font-weight: 300; margin-bottom: .75rem; }
.hero p { font-size: .9375rem; color: var(--muted); max-width: 640px; margin: 0 auto 1.5rem; }
.hero .badge-row { display: flex; justify-content: center; flex-wrap: wrap; gap: .5rem; }
.badge { display: inline-block; padding: .2rem .65rem; border-radius: 1rem; font-size: .75rem; font-weight: 600; line-height: 1.4; }
.badge-blue   { background: var(--tag-blue-bg);   color: var(--tag-blue-txt); }
.badge-green  { background: var(--tag-green-bg);  color: var(--tag-green-txt); }
.badge-purple { background: var(--tag-purple-bg); color: var(--tag-purple-txt); }
.badge-teal   { background: var(--tag-teal-bg);   color: var(--tag-teal-txt); }
.badge-red    { background: var(--tag-red-bg);    color: var(--tag-red-txt); }
.badge-warm   { background: var(--tag-warm-bg);   color: var(--tag-warm-txt); }

.index-main { max-width: 960px; margin: 0 auto; padding: 2rem 2.5rem; flex: 1; }
.course-group { margin-bottom: 2.5rem; }
.course-group h2 { font-size: 1.125rem; font-weight: 600; margin-bottom: 1rem; padding-bottom: .5rem; border-bottom: 1px solid var(--border); }
.course-cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px,1fr)); gap: 1rem; }
.course-card {
  display: block;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 4px;
  padding: 1.25rem 1.5rem;
  text-decoration: none;
  color: var(--text);
  transition: border-color .15s, box-shadow .15s;
}
.course-card:hover { border-color: var(--interactive); box-shadow: 0 2px 8px rgba(15,98,254,.08); }
.course-card .num { font-size: .75rem; font-weight: 700; color: var(--interactive); margin-bottom: .4rem; text-transform: uppercase; letter-spacing: .05em; }
.course-card h3 { font-size: .9375rem; font-weight: 600; margin-bottom: .4rem; }
.course-card p { font-size: .8125rem; color: var(--muted); margin-bottom: .75rem; }
.tag-stack { display: flex; flex-wrap: wrap; gap: .3rem; }
.cds--tag { display: inline-block; padding: .15rem .5rem; border-radius: 1rem; font-size: .7rem; font-weight: 600; }
.cds--tag--blue   { background: var(--tag-blue-bg);   color: var(--tag-blue-txt); }
.cds--tag--green  { background: var(--tag-green-bg);  color: var(--tag-green-txt); }
.cds--tag--purple { background: var(--tag-purple-bg); color: var(--tag-purple-txt); }
.cds--tag--teal   { background: var(--tag-teal-bg);   color: var(--tag-teal-txt); }
.cds--tag--red    { background: var(--tag-red-bg);    color: var(--tag-red-txt); }
.cds--tag--warm   { background: var(--tag-warm-bg);   color: var(--tag-warm-txt); }

/* ── Course index page ── */
.course-overview { max-width: 860px; margin: 0 auto; padding: 2rem 2.5rem; }
.course-overview h1 { font-size: 1.75rem; font-weight: 300; margin-bottom: .4rem; }
.course-overview .subtitle { color: var(--muted); font-size: 1rem; margin-bottom: 2rem; }
.meta-tile { background: var(--layer); border: 1px solid var(--border); border-radius: 4px; padding: 1rem 1.5rem; margin-bottom: 1.5rem; display: flex; flex-wrap: wrap; gap: .5rem 2.5rem; font-size: .875rem; }
.meta-tile span strong { color: var(--text); }
.meta-tile span { color: var(--muted); }
.start-btn {
  display: inline-flex;
  align-items: center;
  gap: .5rem;
  background: var(--interactive);
  color: #fff;
  padding: .75rem 2rem;
  border-radius: 2px;
  text-decoration: none;
  font-size: .9375rem;
  font-weight: 600;
  margin-top: 1rem;
}
.start-btn:hover { background: var(--interactive-h); }
.plan-list { list-style: decimal; padding-left: 1.5rem; font-size: .9rem; }
.plan-list li { padding: .2rem 0; color: var(--text); }

/* ── Footer ── */
footer.site-footer {
  text-align: center;
  font-size: .75rem;
  color: var(--muted);
  padding: 1rem;
  border-top: 1px solid var(--border);
  background: var(--bg);
  margin-top: auto;
}

/* ── Responsive ── */
@media (max-width: 700px) {
  .sidenav { display: none; }
  .sidenav-trigger { display: none; }
  .slide { padding: 1.5rem 1.25rem; }
  .slide-columns, .slide-columns.slide-columns-3, .slide-columns.slide-columns-2-1, .slide-columns.slide-columns-1-2 { grid-template-columns: 1fr; }
}
```

---

## Étape 3 — Générer `{slug}/slides.html`

### Structure HTML du fichier `slides.html`

**Langue :** Tout le texte d'interface est en **anglais** (`lang="en"`, labels, boutons, sections).

```html
<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
  <title>ESIPE — {COURSE_TITLE} — Slides</title>
  <link rel="stylesheet" href="../assets/slide.css"/>
  <style>
/* Per-slide progress-bar highlight (generated, one rule per slide) */
body:has(.slide:target) .progress-bar a.current { background: var(--bg); color: var(--muted); border-color: var(--border); }
body:has(#slide-N:target) .progress-bar a[href="#slide-N"] { background: var(--interactive); color: #fff; border-color: var(--interactive); }
  </style>
</head>
<body>
<header class="site-header">
  <a class="brand" href="../index.html">ESIPE<span>Jakarta EE &amp; Microservices</span></a>
  <a class="back-link" href="../index.html">← All Courses</a>
</header>
<!-- Thin trigger strip: hover near left edge to reveal sidenav -->
<div class="sidenav-trigger" id="sidenav-trigger"></div>
<div class="shell">
  <nav class="sidenav" id="sidenav">
    <div class="sidenav-section">Course outline</div>
    <!-- One entry per ## Section in order of appearance -->
    <!-- <a href="#slide-N" class="active">🎯 Section title</a> -->
    <hr>
    <div class="sidenav-section">Navigation</div>
    <a href="index.html">⊙ Overview</a>
  </nav>
  <main class="content">
    <!-- Progress bar: one pill per slide -->
    <div class="progress-bar">
      <!-- <a href="#slide-1" class="current">1</a> <a href="#slide-2">2</a> … -->
    </div>
    <div class="slide-wrapper">
      <!-- One section per Marp slide, nav bar embedded inside each -->
      <!-- <section id="slide-N" class="slide"> … <div class="slide-nav">…</div> </section> -->
    </div>
  </main>
</div>
<footer class="site-footer">Made with IBM Bob &nbsp;·&nbsp; © 2026 Olivier Planson</footer>
<script>
(function() {
  var nav = document.getElementById('sidenav');
  var trigger = document.getElementById('sidenav-trigger');
  var hideTimer = null;
  function show() { clearTimeout(hideTimer); nav.classList.add('sidenav--open'); }
  function scheduleHide() { hideTimer = setTimeout(function() { nav.classList.remove('sidenav--open'); }, 300); }
  trigger.addEventListener('mouseenter', show);
  trigger.addEventListener('mouseleave', scheduleHide);
  nav.addEventListener('mouseenter', show);
  nav.addEventListener('mouseleave', scheduleHide);
})();
</script>
</body>
</html>
```

### Règles pour `slides.html`

1. **Toutes les slides dans un seul fichier** — pas de fichier séparé par slide.
2. Chaque slide = `<section id="slide-N" class="slide [slide--title|slide--section]">…</section>`.
3. **CSS `:target`** fait apparaître la slide ciblée. La première slide s'affiche par défaut (via
   le sélecteur `.slide-wrapper:not(:has(.slide:target)) .slide:first-of-type`).
4. **Barre de progression** : `<a href="#slide-N">N</a>` pour chaque slide ; ajouter `class="current"` sur la slide 1 (valeur statique — fallback quand aucune ancre n'est active).
   Injecter dans le `<head>` un bloc `<style>` avec N règles CSS `:has()` pour mettre en surbrillance la pastille active dynamiquement :
   ```css
   /* Effacer le .current statique dès qu'une slide est ciblée */
   body:has(.slide:target) .progress-bar a.current { background: var(--bg); color: var(--muted); border-color: var(--border); }
   /* Une règle par slide */
   body:has(#slide-N:target) .progress-bar a[href="#slide-N"] { background: var(--interactive); color: #fff; border-color: var(--interactive); }
   ```
5. **Sidenav TOC** : une entrée `<a href="#slide-N">## Section</a>` pour chaque titre `##` trouvé ;
   la première entrée porte `class="active"`.
6. **Boutons nav bas de page** : **un `.slide-nav` par slide** — chacun est affiché quand la slide
   correspondante est ciblée (voir CSS ci-dessous). Les boutons pointent vers `#slide-{N-1}` et
   `#slide-{N+1}`. Le bouton Previous de la slide 1 et le bouton Next de la dernière slide ont
   `class="disabled"` et pas de `href`.
7. **Compteur** : `<span class="slide-counter">N / TOTAL</span>` dans chaque section.
8. **Slides vides ou quasi-vides** (seulement commentaire HTML) : les inclure quand même comme
   slide vide avec juste le compteur.
9. **Langue** : tout le texte d'interface en **anglais** — "All Courses", "Course outline",
   "Overview", "Slide N / TOTAL", "← Previous", "Next →".
10. **Sidenav hover** : la sidenav est `position: fixed`, cachée hors-écran (`translateX(-200px)`).
    Un `<div class="sidenav-trigger" id="sidenav-trigger">` de `6px` de large est positionné fixe
    sur le bord gauche. Un bloc `<script>` en fin de `<body>` gère `mouseenter`/`mouseleave` sur le
    trigger et la nav — ajoute/retire la classe `sidenav--open` après un délai de 300 ms. Pas de JS
    framework, IIFE vanilla uniquement.

---

## Étape 4 — Générer `{slug}/index.html` (course overview)

**Langue :** Tout le texte d'interface en **anglais**.

```html
<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
  <title>ESIPE — {COURSE_TITLE}</title>
  <link rel="stylesheet" href="../assets/slide.css"/>
</head>
<body>
<header class="site-header">
  <a class="brand" href="../index.html">ESIPE<span>Jakarta EE &amp; Microservices</span></a>
  <a class="back-link" href="../index.html">← All Courses</a>
</header>
<main class="course-overview">
  <h1>{COURSE_TITLE}</h1>
  <p class="subtitle">{COURSE_SUBTITLE}</p>
  <div class="meta-tile">
    <span><strong>Duration:</strong> {DURATION}</span>
    <span><strong>Instructor:</strong> {INSTRUCTOR}</span>
    <span><strong>Date:</strong> {DATE}</span>
    <span><strong>Slides:</strong> {N} slides</span>
  </div>
  <h2 style="font-size:1rem;font-weight:600;margin:1.5rem 0 .75rem;">📚 Course outline</h2>
  <ol class="plan-list">
    <!-- One item per ## Section in order of appearance -->
  </ol>
  <a class="start-btn" href="slides.html#slide-1">▶ Start course</a>
</main>
<footer class="site-footer">Made with IBM Bob &nbsp;·&nbsp; © 2026 Olivier Planson</footer>
</body>
</html>
```

---

## Étape 5 — Générer `02-Lectures/slides/index.html` (global index)

Same structure as the docs site (hero + groups + cards) but links to `{slug}/index.html`.

**Langue :** Tout le texte d'interface en **anglais**.

Thematic groups:

| Group | Courses |
|-------|---------|
| 🚀 Jakarta EE Foundations | 01-intro, 02-servlets, 02b-jsf |
| 💾 Data &amp; Business Layer | 03-jpa, 04-cdi, 04b-ejb |
| 🌐 Services &amp; Messaging | 05-rest, 05b-jms |
| 🏛️ Advanced Architectures | 06-ddd, 07-hexagonal, 08-microservices |
| 🔐 Security | 09-security |

Structure HTML :
```html
<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
  <title>ESIPE — Jakarta EE &amp; Microservices — Slides</title>
  <link rel="stylesheet" href="assets/slide.css"/>
</head>
<body>
<header class="site-header">
  <a class="brand" href="index.html">ESIPE<span>Jakarta EE &amp; Microservices — Slides</span></a>
</header>
<div class="hero">
  <div>
    <h1>Course Slides</h1>
    <p>Interactive slides for Jakarta EE, MicroProfile and microservices architecture courses. Navigate slide by slide, just like a presentation.</p>
    <div class="badge-row">
      <span class="badge badge-blue">Jakarta EE 10</span>
      <span class="badge badge-blue">MicroProfile 6</span>
      <span class="badge badge-green">Open Liberty</span>
      <span class="badge badge-teal">Java 17</span>
      <span class="badge badge-purple">DDD</span>
      <span class="badge badge-purple">Hexagonal</span>
      <span class="badge badge-warm">Microservices</span>
      <span class="badge badge-red">Security</span>
    </div>
  </div>
</div>
<main class="index-main">
  <!-- 5 course-group sections, each with course-cards -->
</main>
<footer class="site-footer">Made with IBM Bob &nbsp;·&nbsp; © 2026 Olivier Planson</footer>
</body>
</html>
```

---

## Étape 6 — Ordre d'exécution

1. Lire les 12 fichiers `.md` avec `read_file` (lire par blocs de 200 lignes si nécessaire).
2. Parser chaque fichier selon les règles de l'Étape 1.
3. Créer le répertoire `02-Lectures/slides/assets/` si nécessaire (`mcp__filesystem__create_directory`).
4. Écrire `02-Lectures/slides/assets/slide.css`.
5. Pour chaque cours, dans l'ordre de la table des slugs :
   a. Créer le répertoire `02-Lectures/slides/{slug}/`.
   b. Écrire `02-Lectures/slides/{slug}/slides.html`.
   c. Écrire `02-Lectures/slides/{slug}/index.html`.
6. Écrire `02-Lectures/slides/index.html`.
7. Validation (Étape 7).

**Important :** Traiter un cours à la fois pour éviter de surcharger le contexte. Après avoir
traité 3-4 cours, écrire les fichiers immédiatement avant de continuer.

---

## Étape 7 — Validation

1. Lister `02-Lectures/slides/` pour confirmer la structure.
2. Reporter : `✅ {slug}/index.html`, `✅ {slug}/slides.html` pour chaque cours.
3. Indiquer le nombre total de slides parsées par cours.
4. Conseil final : "Ouvre `02-Lectures/slides/index.html` dans un navigateur pour voir le site."

---

## Règles de conception

- **JavaScript minimal autorisé** — uniquement pour : sidenav hover, progress bar hover, EN/FR toggle. IIFE vanilla, aucun framework.
- **Aucune URL externe** dans `src` ou `href` d'assets (les `<a href>` vers GitHub sont OK).
- **Pas de `@import` ni `url()` dans les CSS** (sauf `data:` URI).
- **Inline SVG uniquement** pour les diagrammes éventuels.
- **Images** : chemin `../../images/{filename}` depuis `slides/{slug}/`.
- **Interface en anglais** — bouton EN/FR pour basculer le contenu textuel des slides.
- **Commentaire copyright** en haut de chaque fichier généré.
- **`<details>…</details>`** (blocs Mermaid du source) : toujours ignorés.

## Fonctionnalités interactives (JS vanilla)

### 1. Sidenav hover (gauche)
- `<div class="sidenav-trigger" id="sidenav-trigger">` — bande fixe 6px sur le bord gauche
- `mouseenter` → classe `sidenav--open` sur `<nav id="sidenav">` (délai 0)
- `mouseleave` nav ou trigger → retire `sidenav--open` après 300 ms

### 2. Progress bar hover (haut)
- `<div class="progress-bar-trigger" id="pb-trigger">` — bande sticky 3px sous le header
- `<div class="progress-bar-wrap" id="pb-wrap">` — wrapper avec `max-height: 0 → 8rem` en transition CSS
- `mouseenter` trigger/wrap → classe `pb-open` ; `mouseleave` → retire après 400 ms

### 3. Bouton EN/FR
- Placé dans le header : `<div class="lang-toggle">` avec deux `<button id="btn-en">` / `<button id="btn-fr">` **sans `onclick`**
- Les clics sont câblés par `addEventListener('click', …)` dans le script, jamais par attributs inline
- `setLang(lang)` est déclarée en **portée globale** (hors IIFE) — pas à l'intérieur de `(function(){…})()`
- Préférence stockée dans `localStorage` clé `'slide-lang'`, rechargée au démarrage de la page
- Tous les éléments textuels (h1/h2/h3/p/li/blockquote) reçoivent `data-en` et `data-fr` à la génération :
  - `data-en` = `innerHtml` brut — **uniquement les `"` sont échappés en `&quot;`**, jamais `<` ni `>`
  - `data-fr` = texte traduit par `toFr()` — idem, uniquement `"`  échappés
  - `el.innerHTML = el.getAttribute('data-fr')` fonctionne car les attributs contiennent du HTML valide (balises `<strong>` réelles)
- Traduction à la compilation par le dictionnaire `FR_DICT` dans `_generate.js` — partielle (termes techniques conservés en anglais)
- **Règles d'ordre dans `FR_DICT`** : les phrases longues doivent précéder les mots courts (ex. `Development Environment Setup` avant `Development`, `Examples` avant `Example`)
- **`Building`** : `/^Building\b/gim` (début de ligne seulement) — évite "for building REST services" → "for Construction de REST services" en milieu de phrase
- **`Annotations`** : pas de règle — même mot en français, le no-op de `toFr()` s'applique correctement
