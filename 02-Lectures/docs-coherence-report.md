<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Coherence Report — Jakarta EE Course Slides
Generated: 2025-01-28

## Summary
**Overall status: 10/12 chapters fully instrumented, 17 issues found**

Two chapters (08-microservices, 09-security) have notable problems. Chapter 09-security is the most critical — it is entirely un-instrumented and written in French only.

---

## Chapter-by-Chapter Status

| Chapter | lang=en | Toggle btn | Sidebar toggle | Script tag | h1 data-en/data-fr | Notes |
|---------|---------|-----------|----------------|------------|-------------------|-------|
| 01-intro | ✅ | ✅ | ✅ | ✅ | ✅ | Fully instrumented |
| 02-servlets | ✅ | ✅ | ✅ | ✅ | ✅ | Fully instrumented |
| 02b-jsf | ✅ | ✅ | ✅ | ✅ | ✅ | Toggle btn order differs from 01/02 (after `<a>`, not before) |
| 03-jpa | ✅ | ✅ | ✅ | ✅ | ✅ | Footer has extra data-en/data-fr (unique to this chapter) |
| 04-cdi | ✅ | ✅ | ✅ | ✅ | ✅ | Fully instrumented |
| 04b-ejb | ✅ | ✅ | ✅ | ✅ | ✅ | Section title says "Chapter 04b" not "Course 04b" (inconsistent) |
| 05-rest | ✅ | ✅ | ✅ | ✅ | ✅ | Fully instrumented |
| 05b-jms | ✅ | ✅ | ✅ | ✅ | ✅ | Fully instrumented |
| 06-ddd | ✅ | ✅ | ✅ | ✅ | ✅ | Header `<a>` tag missing data-en/data-fr on name |
| 07-hexagonal | ✅ | ✅ | ✅ | ✅ | ✅ | Has extra 5th sidebar link (architecture.html) |
| 08-microservices | ✅ | ✅ | ✅ | ✅ | ✅ | Header `<a>` tag missing data-en/data-fr on name; has extra 5th sidebar link (patterns.html) |
| 09-security | ❌ (`fr`) | ❌ | ❌ | ❌ | ❌ | **CRITICAL: entirely un-instrumented, French-only** |

---

## Issues Found

### CRITICAL

**ISSUE-01** · `09-security/slides.html` line 3  
`<html lang="fr">` — should be `lang="en"` (or dynamically set by JS). This is the only chapter with `lang="fr"` hardcoded.

**ISSUE-02** · `09-security/slides.html` — No `<button class="lang-toggle-btn" id="lang-toggle">` in the header.  
The language toggle button is completely absent; users cannot switch language on this chapter.

**ISSUE-03** · `09-security/slides.html` line 22 (end of `<nav>`) — No `<a class="cds--side-nav__lang-toggle">` present before `</nav>`.  
The sidebar language toggle link is missing.

**ISSUE-04** · `09-security/slides.html` — No `<script src="../assets/language-toggle.js">` before `</body>` (line 342).  
The JS toggle script is not loaded; the page has no toggle functionality at all.

**ISSUE-05** · `09-security/slides.html` — The entire file has no `data-en` or `data-fr` attributes on any element.  
All content is hard-coded in French only. This chapter needs full bilingual instrumentation.

**ISSUE-06** · `09-security/slides.html` line 12 — Header link `<a class="cds--header__name">` and `<a class="cds--header__back">` have no `data-en`/`data-fr` attributes.  
The navigation header is not translatable.

**ISSUE-07** · `09-security/slides.html` lines 18–21 — All four `<a class="cds--side-nav__link">` elements have no `data-en`/`data-fr` attributes.  
The sidebar navigation is not translatable.

---

### MODERATE

**ISSUE-08** · `06-ddd/slides.html` line 12  
```html
<a class="cds--header__name" href="index.html">Domain-Driven Design<span ...>
```
The `<a>` tag itself has no `data-en`/`data-fr` — only the inner `<span>` does. Compare to chapters 07, 08 which correctly set `data-en`/`data-fr` on the `<a>` tag itself. When language switches, the text before the `<span>` ("Domain-Driven Design") does not translate.

**ISSUE-09** · `08-microservices/slides.html` line 12  
Same structural problem as ISSUE-08: `<a class="cds--header__name" href="index.html">Microservices<span ...>` — the `<a>` lacks `data-en`/`data-fr`.

**ISSUE-10** · `04b-ejb/slides.html` line 18  
`<p class="cds--side-nav__section-title" data-en="Chapter 04b" data-fr="Cours 04b">` — uses "Chapter" in English where all other chapters use "Course". All other chapters consistently use `data-en="Course XX"`.

**ISSUE-11** · `02b-jsf/slides.html` line 12–13  
The `<button class="lang-toggle-btn" id="lang-toggle">` is placed **after** the `<a cds--header__name>` element, while chapters 01-intro and 02-servlets place it **before**. This creates minor visual inconsistency in the header layout.

---

### MINOR

**ISSUE-12** · `06-ddd/slides.html` line 7  
`<title>Cours 06 — Contenu du cours</title>` — title is in French. All other chapters use English titles (e.g. "Course 03 — Course Content", "Course 04 — Course Content").

**ISSUE-13** · `07-hexagonal/slides.html` line 7  
`<title>Cours 07 — Contenu du cours</title>` — same French title issue as ISSUE-12.

**ISSUE-14** · `08-microservices/slides.html` line 7  
`<title>Cours 08 — Contenu du cours</title>` — same French title issue as ISSUE-12.

**ISSUE-15** · `09-security/slides.html` line 7  
`<title>Cours 09 — Contenu du cours</title>` — same French title issue as ISSUE-12 (expected, given the whole file is un-instrumented).

**ISSUE-16** · `07-hexagonal/slides.html` line 23 — Extra 5th sidebar link: `<a class="cds--side-nav__link" href="architecture.html">`. All other chapters have exactly 4 sidebar links. This is intentional content extension but differs from the standard 4-link pattern.

**ISSUE-17** · `08-microservices/slides.html` line 23 — Extra 5th sidebar link: `<a class="cds--side-nav__link" href="patterns.html">`. Same note as ISSUE-16.

---

## Terminology Consistency

### ✅ Consistent usage observed

| Term | Usage | Verdict |
|------|-------|---------|
| Jakarta EE | Used consistently in all instrumented chapters | ✅ Consistent |
| MicroProfile | Always written as "MicroProfile" (capital M and P) | ✅ Consistent |
| CDI | Always "CDI" (uppercase) | ✅ Consistent |
| JPA | Always "JPA" (uppercase) | ✅ Consistent |
| JAX-RS vs Jakarta REST | See note below | ⚠️ Mixed |
| Repository | Used consistently as "Repository" in 06-ddd | ✅ Consistent |

### ⚠️ Inconsistent terminology

**JAX-RS vs Jakarta REST:**  
- Chapter 05-rest uses "Jakarta REST" in the header and content (correct modern name post Jakarta EE 9).  
- Chapters 07-hexagonal (line 43, 75, 113), 08-microservices (line 130), and 06-ddd (line 221, 229) still use the legacy term **"JAX-RS"** in `data-en` attributes and visible text.  
- Best practice: standardise on "Jakarta REST (JAX-RS)" or "Jakarta REST" throughout to reflect the Jakarta EE 9+ rename.

**JMS vs Jakarta Messaging:**  
- Chapter 05b-jms uses "Jakarta Messaging" in the header and modern content (correct).  
- Chapters 07-hexagonal (line 115) and 08-microservices (line 130) use **"JMS"** in `data-en` text.  
- This is a less severe issue since JMS is still a widely used abbreviation, but for consistency with the course's Jakarta EE 9+ positioning, prefer "Jakarta Messaging (JMS)".

**"Lecture Content" vs "Course Content":**  
- Chapters 01-intro and 02-servlets use `data-en="Lecture Content"` and `data-fr="Contenu du cours"` on the `<h1>`.  
- Chapters 03-jpa, 04-cdi, 04b-ejb, 05-rest, 05b-jms, 06-ddd, 07-hexagonal, 08-microservices use `data-en="Course Content"`.  
- Chapters 01 and 02 use a different English label for the same page type — should be standardised to one term.

**Sidebar active link label:**  
- Chapters 01-intro and 02-servlets: `data-en="☰ Lecture Content"` on the active sidebar link.  
- All other chapters: `data-en="☰ Course Content"`.  
- Should be consistent with the `<h1>` text decision above.

---

## Assets Review

### `assets/language-toggle.js` — ✅ Correct
The script correctly:
- Reads `data-en` / `data-fr` attributes and updates `textContent` on toggle.
- Updates `img alt` attributes via `data-en`/`data-fr` on `<img>` elements.
- Updates all `#lang-toggle` and `.cds--side-nav__lang-toggle` button labels.
- Updates `<html lang>` dynamically.
- Persists preference in `localStorage`.

⚠️ **Known limitation**: The script uses `el.textContent = ...` which will strip inner HTML (nested `<strong>`, `<code>` tags etc.). In chapters where `data-en`/`data-fr` values contain HTML markup (e.g. `data-en="<strong>Jakarta EE</strong> is..."`), the tags will be rendered as visible literal text rather than parsed HTML after toggle. This affects many elements across all chapters. A robust fix would use `el.innerHTML = ...` conditionally (with XSS sanitization), or restrict `data-en`/`data-fr` to plain text only.

### `assets/carbon.css` — ✅ Present and well-structured
IBM Carbon Design tokens are defined. No issues detected in the reviewed portion.

---

## Recommendations

1. **[Priority 1] Fully instrument `09-security/slides.html`**: Add `lang="en"`, the toggle button, sidebar toggle, script tag, and bilingual `data-en`/`data-fr` attributes on all content elements. This is the only chapter entirely missing the EN/FR infrastructure.

2. **[Priority 2] Standardise `data-en` h1/sidebar label**: Decide between "Lecture Content" and "Course Content" and apply uniformly across all 12 chapters (currently 01-intro and 02-servlets are outliers with "Lecture Content").

3. **[Priority 3] Standardise technology names**: Prefer "Jakarta REST" over "JAX-RS" and "Jakarta Messaging" over "JMS" in visible text and `data-en` attributes, especially in chapters 06, 07, and 08.

4. **[Priority 4] Fix `04b-ejb` section title**: Change `data-en="Chapter 04b"` to `data-en="Course 04b"` to match every other chapter.

5. **[Priority 5] Add `data-en`/`data-fr` to header `<a>` tags in 06-ddd and 08-microservices**: The course name before the `<span>` inside the header link does not translate currently.

6. **[Priority 6] Fix `<html lang>` and `<title>` in chapters 06, 07, 08**: Chapters 06–08 have French `<title>` tags (`Cours 0X — Contenu du cours`) while all instrumented chapters should have English titles as the default.

7. **[Future improvement] Fix HTML-in-data-attributes rendering**: The `language-toggle.js` script uses `el.textContent = ...`, which does not parse HTML markup in attribute values. Many `data-en`/`data-fr` values contain `<strong>`, `<code>` or other inline elements. After toggling, these tags appear as literal text. Consider switching to `el.innerHTML` (with appropriate content trust validation) or auditing all attributes to ensure they are plain-text only.

8. **[Consistency] Normalise toggle button position in header**: Chapters 01-intro and 02-servlets place `<button class="lang-toggle-btn">` as the first element in the header; 02b-jsf and later chapters place it after the title `<a>`. Pick one convention and apply it consistently.
