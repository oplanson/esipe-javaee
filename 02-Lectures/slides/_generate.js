#!/usr/bin/env node
// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
// Slide site generator — parses Marp .md → HTML
// v3: progress-bar hover reveal; EN/FR toggle with static translation table

const fs = require('fs');
const path = require('path');

// ── Course metadata ─────────────────────────────────────────────────────────
const COURSES = [
  { slug: '01-intro',        file: '01-intro-jakartaee-microprofile.md',  num: 'Lecture 1',  tags: ['Jakarta EE 10','MicroProfile 6','Open Liberty','Podman'] },
  { slug: '02-servlets',     file: '02-servlets-jsp-microprofile.md',     num: 'Lecture 2',  tags: ['Servlets','JSP','JSTL','MVC','MicroProfile Config'] },
  { slug: '02b-jsf',         file: '02b-jsf-javaserver-faces.md',         num: 'Lecture 2b', tags: ['JSF','Facelets','PrimeFaces','Component UI'] },
  { slug: '03-jpa',          file: '03-jpa-database-integration.md',      num: 'Lecture 3',  tags: ['JPA','Hibernate','PostgreSQL','JPQL'] },
  { slug: '04-cdi',          file: '04-cdi-dependency-injection.md',      num: 'Lecture 4',  tags: ['CDI','DI','Interceptors','Events'] },
  { slug: '04b-ejb',         file: '04b-ejb-enterprise-java-beans.md',    num: 'Lecture 4b', tags: ['EJB','Stateless','Stateful','Singleton','JTA'] },
  { slug: '05-rest',         file: '05-jaxrs-restful-services.md',        num: 'Lecture 5',  tags: ['JAX-RS','REST','JSON-B','MP Rest Client'] },
  { slug: '05b-jms',         file: '05b-jms-enterprise-messaging.md',     num: 'Lecture 5b', tags: ['JMS','MDB','ActiveMQ','Messaging'] },
  { slug: '06-ddd',          file: '06-domain-driven-design.md',          num: 'Lecture 6',  tags: ['DDD','Bounded Context','Aggregates','Domain Events'] },
  { slug: '07-hexagonal',    file: '07-hexagonal-architecture.md',        num: 'Lecture 7',  tags: ['Hexagonal','Ports & Adapters','Clean Architecture'] },
  { slug: '08-microservices',file: '08-microservices-architecture.md',    num: 'Lecture 8',  tags: ['Microservices','MP Health','MP Metrics','Fault Tolerance'] },
  { slug: '09-security',     file: '09-jakarta-ee-security.md',           num: 'Lecture 9',  tags: ['Security','JWT','OIDC','RBAC','MP JWT'] },
];

const LECTURES_DIR = path.join(__dirname, '..');
const OUT_DIR      = __dirname;

// ── Escape HTML ──────────────────────────────────────────────────────────────
function esc(s) {
  return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

// ── French translation — phrase-level dictionary ─────────────────────────────
// Translates a plain-text string (no HTML) using a word/phrase substitution
// table. Applied at generation time to produce the `data-fr` attribute value.
const FR_DICT = [
  // ── Common technical patterns ──────────────────────────────────────────────
  [/\bLearning Objectives\b/gi, 'Objectifs pédagogiques'],
  [/\bKey Characteristics\b/gi, 'Caractéristiques clés'],
  [/\bKey Features\b/gi, 'Fonctionnalités clés'],
  [/\bKey Takeaways\b/gi, 'Points clés'],
  [/\bBest Practices?\b/gi, 'Bonnes pratiques'],
  [/\bBest Practice\b/gi, 'Bonne pratique'],
  [/\bCommon Questions\b/gi, 'Questions fréquentes'],
  [/\bAdditional Resources\b/gi, 'Ressources supplémentaires'],
  [/\bCourse Roadmap\b/gi, 'Programme du cours'],
  [/\bNext Steps?\b/gi, 'Prochaines étapes'],
  [/\bNext Lecture\b/gi, 'Prochaine leçon'],
  [/\bLab Preview\b/gi, 'Aperçu du TP'],
  [/\bObjectives\b/gi, 'Objectifs'],
  [/\bHomework\b/gi, 'Devoirs'],
  [/\bQuestions? & Discussion\b/gi, 'Questions & Discussion'],
  [/\bQuestions?\b/gi, 'Questions'],
  [/\bAppendix\b/gi, 'Annexe'],
  [/\bIntroduction to\b/gi, 'Introduction à'],
  [/\bWhat is\b/gi, "Qu'est-ce que"],
  [/\bWhy (use )?\b/gi, 'Pourquoi '],
  [/\bHow to\b/gi, 'Comment'],
  [/\bGetting Started\b/gi, 'Premiers pas'],
  [/\bOverview\b/gi, 'Vue d\'ensemble'],
  [/\bSummary\b/gi, 'Résumé'],
  [/\bConclusion\b/gi, 'Conclusion'],
  [/\bArchitecture\b/gi, 'Architecture'],
  [/\bConfiguration\b/gi, 'Configuration'],
  [/\bDevelopment Environment Setup\b/gi, 'Configuration de l\'environnement'],
  [/\bDevelopment\b/gi, 'Développement'],
  [/\bProject Structure\b/gi, 'Structure du projet'],
  [/\bExamples\b/gi, 'Exemples'],
  [/\bExample\b/gi, 'Exemple'],
  [/\bComparison\b/gi, 'Comparaison'],
  [/\bRuntime\b/gi, 'Environnement d\'exécution'],
  // Annotation — same in French; no rule needed (toFr no-op is fine)
  [/\bLifecycle\b/gi, 'Cycle de vie'],
  [/\bTesting\b/gi, 'Tests'],
  [/\bDeployment\b/gi, 'Déploiement'],
  [/\bException Handling\b/gi, 'Gestion des exceptions'],
  [/\bError Handling\b/gi, 'Gestion des erreurs'],
  [/\bDependency Injection\b/gi, 'Injection de dépendances'],
  [/\bEnterprise Java Beans\b/gi, 'Enterprise Java Beans'],
  [/\bMessage Driven\b/gi, 'Piloté par messages'],
  [/\bTransaction Management\b/gi, 'Gestion des transactions'],
  [/\bDatabase Integration\b/gi, 'Intégration base de données'],
  [/\bSecurity\b/gi, 'Sécurité'],
  [/\bPerformance\b/gi, 'Performance'],
  [/\bScalability\b/gi, 'Scalabilité'],
  [/\bMicroservices Architecture\b/gi, 'Architecture microservices'],
  [/\bDomain.Driven Design\b/gi, 'Conception pilotée par le domaine'],
  [/\bHexagonal Architecture\b/gi, 'Architecture hexagonale'],
  [/\bRESTful (Web )?Services?\b/gi, 'Services RESTful'],
  [/\bEnterprise Messaging\b/gi, 'Messagerie d\'entreprise'],
  [/\bCloud.Native\b/gi, 'Cloud-natif'],
  [/\bOpen Source\b/gi, 'Open source'],
  [/\bOpen Liberty\b/g, 'Open Liberty'],  // proper name — keep
  // ── Sentence connectors ───────────────────────────────────────────────────
  [/\bBy the end of this lecture, you will be able to\b/gi, 'À la fin de cette leçon, vous serez en mesure de'],
  [/\bis IBM's open-source\b/gi, 'est le runtime open source d\'IBM'],
  [/\bRunning on\b/gi, 'Exécuté sur'],
  [/\bAccess:\b/gi, 'Accès :'],
  [/\bNote:\b/gi, 'Note :'],
  [/\bWarning:\b/gi, 'Attention :'],
  [/\bUse case:\b/gi, 'Cas d\'usage :'],
  [/\bWhen called:\b/gi, 'Quand appelé :'],
  [/\bWhen called\b/gi, 'Quand appelé'],
  [/\bPurpose:\b/gi, 'Objectif :'],
  // ── Actions in headings ───────────────────────────────────────────────────
  [/\bCreating Your First\b/gi, 'Créer votre premier'],
  [/\bBuilding with\b/gi, 'Construire avec'],
  [/^Building\b/gim, 'Construction'],  // only at start of line/heading
  [/\bUsing\b/gi, 'Utilisation de'],
  [/\bImplementing\b/gi, 'Implémentation de'],
  [/\bIntegrating\b/gi, 'Intégration de'],
  [/\bDesigning\b/gi, 'Conception de'],
  // ── Course-specific terms ─────────────────────────────────────────────────
  [/\bComplete Example\b/gi, 'Exemple complet'],
  [/\bSimple\b/gi, 'Simple'],
  [/\bAdvanced\b/gi, 'Avancé'],
  [/\bLab \d+/gi, m => m],  // keep lab numbers as-is
  [/\bThank You!?\b/gi, 'Merci !'],
  [/\bReady to\b/gi, 'Prêt à'],
  [/\bSee you in\b/gi, 'À bientôt dans'],
  // ── Days / months (if present) ────────────────────────────────────────────
  [/\bMonday\b/gi, 'Lundi'], [/\bTuesday\b/gi, 'Mardi'],
  [/\bWednesday\b/gi, 'Mercredi'], [/\bThursday\b/gi, 'Jeudi'],
  [/\bFriday\b/gi, 'Vendredi'],
];

// Apply the French dictionary to a plain text string
function toFr(text) {
  let t = text;
  for (const [pat, rep] of FR_DICT) {
    t = t.replace(pat, rep);
  }
  return t;
}

// Wrap a text node inside an element with data-en / data-fr for JS toggle.
// tag: the wrapping element (h1, h2, h3, p, li…)
// Returns the full opening-tag + content + closing-tag string with data attrs.
// IMPORTANT:
//   data-en  = raw innerHTML (only " escaped to &quot; so the attribute is valid HTML)
//   data-fr  = plain French text from toFr() (only " escaped)
//   Both values are set as attribute strings; JS reads them with getAttribute()
//   and writes them back with innerHTML — so they must NOT have < > escaped.
function i18nWrap(tag, attrs, innerHtml) {
  // Extract plain text for translation input
  const plainText = innerHtml
    .replace(/<[^>]+>/g, '')
    .replace(/&amp;/g,'&').replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&quot;/g,'"').replace(/&#\d+;/g,'');
  const frText = toFr(plainText);
  if (frText !== plainText) {
    // Escape only double-quotes so the attribute value stays valid HTML
    const dataEn = innerHtml.replace(/"/g, '&quot;');
    const dataFr = frText.replace(/"/g, '&quot;');
    const attrStr = attrs ? ' ' + attrs : '';
    return `<${tag}${attrStr} data-en="${dataEn}" data-fr="${dataFr}">${innerHtml}</${tag}>`;
  }
  const attrStr = attrs ? ' ' + attrs : '';
  return `<${tag}${attrStr}>${innerHtml}</${tag}>`;
}

// ── Credential masking in code blocks ────────────────────────────────────────
// Matches HTTP header lines of the form:  HeaderName: SCHEME <credential>
// Replaces the credential with SCHEME_CODE.
// Rule: any line matching  ^<word(s)>: <SCHEME> <credential>$
//   where <credential> is a non-whitespace token of ≥4 chars (may end with …/...)
function maskCredentials(text) {
  // Pattern 1: Header: SCHEME credential  (two-token value, e.g. Authorization: Bearer xxx)
  text = text.replace(
    /^([A-Za-z][A-Za-z0-9\-]*):\s+([A-Za-z][A-Za-z0-9]*)\s+[A-Za-z0-9+/=._\-]{4,}\.{0,3}\s*$/gm,
    function(_, header, scheme) {
      return header + ': ' + scheme + ' ' + scheme.toUpperCase() + '_CODE';
    }
  );
  // Pattern 2: Header: credential  (single-token value)
  // Only apply when the header name itself suggests it's a credential header
  text = text.replace(
    /^((?:X-API-Key|X-Auth-Token|X-Secret|API-Key|Api-Key)):\s+[A-Za-z0-9+/=._\-]{4,}\.{0,3}\s*$/gim,
    function(_, header) {
      const slug = header.toUpperCase().replace(/-/g, '_');
      return header + ': ' + slug + '_CODE';
    }
  );
  return text;
}

// ── Parse one .md file ────────────────────────────────────────────────────────
function parseMd(mdPath) {
  let raw = fs.readFileSync(mdPath, 'utf8');

  // 1. Remove frontmatter (first --- to closing ---)
  let footer = '';
  const fmMatch = raw.match(/^---\n([\s\S]*?)\n---\n/);
  if (fmMatch) {
    const fm = fmMatch[1];
    const footerMatch = fm.match(/footer:\s*['"](.+?)['"]/);
    if (footerMatch) footer = footerMatch[1];
    raw = raw.slice(fmMatch[0].length);
  }

  // 2. Remove leading HTML comments (copyright, metadata block)
  raw = raw.replace(/^<!--[\s\S]*?-->\n*/m, '');

  // 3. Split into slides on bare ---
  const slideRaw = raw.split(/\n---\n/);

  // 4. Extract course title (first # heading in whole doc)
  let courseTitle = 'Course';
  for (const s of slideRaw) {
    const m = s.match(/^#\s+(.+)$/m);
    if (m) { courseTitle = m[1].trim(); break; }
  }

  // 5. Parse each slide
  const slides = slideRaw.map((s, i) => parseSlide(s.trim(), i + 1));

  return { courseTitle, footer, slides };
}

// ── Convert one slide block to HTML ──────────────────────────────────────────
function parseSlide(text, num) {
  // Remove HTML comments
  text = text.replace(/<!--[\s\S]*?-->/g, '');
  // Remove <details>...</details> (Mermaid blocks)
  text = text.replace(/<details[\s\S]*?<\/details>/gi, '');

  // Detect slide type
  let cls = 'slide';
  const firstH1 = text.match(/^#\s+(.+)$/m);
  const firstH2 = text.match(/^##\s+(.+)$/m);
  // Title slide = slide 1 with # heading
  if (num === 1 && firstH1) cls += ' slide--title';
  // Section slide = has # Part ... heading (no ##)
  else if (firstH1 && firstH1[1].match(/^Part\s+\d/i)) cls += ' slide--section';
  else if (firstH1 && !firstH2) cls += ' slide--section';

  const html = convertMarkdown(text, num);
  return { num, cls, html };
}

// ── Full Markdown → HTML converter ───────────────────────────────────────────
function convertMarkdown(md, slideNum) {
  const lines = md.split('\n');
  let out = '';
  let i = 0;
  let inList = null;    // 'ul' | 'ol'
  let inTable = false;
  let inPre = false;
  let preContent = '';
  let preLang = '';
  let inDivStack = []; // track open divs

  function flushList() {
    if (inList) { out += `</${inList}>\n`; inList = null; }
  }
  function flushTable() {
    if (inTable) { out += `</tbody></table>\n`; inTable = false; }
  }
  function flushAll() { flushList(); flushTable(); }

  // Handle meta block on title slide (slide 1)
  // Extract **Key:** Value pairs that form the meta-block
  const metaFields = {};
  if (slideNum === 1) {
    const metaRe = /^\*\*(Duration|Instructor|Date|Course):\*\*\s*(.+)$/gm;
    let mm;
    while ((mm = metaRe.exec(md)) !== null) {
      metaFields[mm[1]] = mm[2].trim();
    }
  }

  while (i < lines.length) {
    const line = lines[i];

    // ── Fenced code block ────────────────────────────────────────────────────
    if (!inPre && line.match(/^```/)) {
      flushAll();
      preLang = line.replace(/^```/, '').trim();
      preContent = '';
      inPre = true;
      i++; continue;
    }
    if (inPre) {
      if (line.match(/^```/)) {
        const badge = preLang ? `<span class="lang-badge">${esc(preLang)}</span>\n` : '';
        out += `<pre class="dark">${badge}<code>${esc(maskCredentials(preContent.trimEnd()))}</code></pre>\n`;
        inPre = false; preLang = ''; preContent = '';
      } else {
        preContent += line + '\n';
      }
      i++; continue;
    }

    // ── <div class="columns..."> blocks ─────────────────────────────────────
    if (line.match(/^<div\s+class="columns(-3|-2-1|-1-2)?"\s*>/i)) {
      flushAll();
      const variant = (line.match(/columns(-3|-2-1|-1-2)/) || ['',''])[1] || '';
      let extraCls = '';
      if (variant === '-3') extraCls = ' slide-columns-3';
      else if (variant === '-2-1') extraCls = ' slide-columns-2-1';
      else if (variant === '-1-2') extraCls = ' slide-columns-1-2';
      out += `<div class="slide-columns${extraCls}">\n`;
      inDivStack.push('columns');
      i++; continue;
    }
    // <div> inside columns → slide-col
    if (inDivStack.length > 0 && line.match(/^<div(\s[^>]*)?>$/i) && !line.match(/class="(slide-col|columns)/i)) {
      out += `<div class="slide-col">\n`;
      inDivStack.push('col');
      i++; continue;
    }
    // </div> handling
    if (line.trim() === '</div>') {
      flushAll();
      out += `</div>\n`;
      if (inDivStack.length > 0) inDivStack.pop();
      i++; continue;
    }

    // ── Raw HTML layout tables (<table style="border: none">) ────────────────
    // These are two-column layout wrappers containing Markdown inside <td>s.
    // Convert them to slide-columns divs and parse the inner Markdown.
    if (line.match(/^<table\b/i)) {
      flushAll();
      // Collect until </table>
      let block = '';
      while (i < lines.length) {
        block += lines[i] + '\n';
        if (lines[i].match(/<\/table>/i)) { i++; break; }
        i++;
      }
      // Extract <td> cell contents and convert each through Markdown
      const cellContents = [];
      const tdRe = /<td[^>]*>([\s\S]*?)<\/td>/gi;
      let m;
      while ((m = tdRe.exec(block)) !== null) {
        cellContents.push(m[1].trim());
      }
      if (cellContents.length >= 2) {
        // Render as slide-columns
        out += `<div class="slide-columns">\n`;
        for (const cell of cellContents) {
          out += `<div class="slide-col">\n${convertMarkdown(cell, slideNum)}\n</div>\n`;
        }
        out += `</div>\n`;
      } else {
        // Fallback: pass through as-is
        out += block;
      }
      continue;
    }
    // ── Pass-through other raw HTML (tr/td/th lines not inside a table tag) ──
    if (line.match(/^<(tr|td|th|thead|tbody|tfoot)\b/i)) {
      flushAll();
      let block = '';
      while (i < lines.length) {
        block += lines[i] + '\n';
        if (lines[i].match(/<\/(tr|table)>/i)) { i++; break; }
        i++;
      }
      out += block;
      continue;
    }

    // ── Markdown table ────────────────────────────────────────────────────────
    if (line.match(/^\|/)) {
      flushList();
      if (!inTable) {
        // Check if next line is separator
        const nextLine = lines[i+1] || '';
        if (nextLine.match(/^\|[\s\-|:]+\|/)) {
          // Header row
          const cells = parseCells(line);
          out += `<table class="slide-table"><thead><tr>`;
          cells.forEach(c => { out += `<th>${inline(c)}</th>`; });
          out += `</tr></thead><tbody>\n`;
          i += 2; // skip separator
          inTable = true;
          continue;
        }
      }
      // Body row
      const cells = parseCells(line);
      out += `<tr>`;
      cells.forEach(c => { out += `<td>${inline(c)}</td>`; });
      out += `</tr>\n`;
      i++; continue;
    } else {
      flushTable();
    }

    // ── Headings ──────────────────────────────────────────────────────────────
    const h1 = line.match(/^#\s+(.+)$/);
    const h2 = line.match(/^##\s+(.+)$/);
    const h3 = line.match(/^###\s+(.+)$/);

    if (h1) {
      flushAll();
      const inner = inline(h1[1].trim());
      out += i18nWrap('h1', '', inner) + '\n';
      i++; continue;
    }
    if (h2) {
      flushAll();
      const inner = inline(h2[1].trim());
      out += i18nWrap('h2', '', inner) + '\n';
      i++; continue;
    }
    if (h3) {
      flushAll();
      const inner = inline(h3[1].trim());
      out += i18nWrap('h3', '', inner) + '\n';
      i++; continue;
    }

    // ── Blockquote ────────────────────────────────────────────────────────────
    if (line.match(/^>\s*/)) {
      flushAll();
      const inner = inline(line.replace(/^>\s*/, '').trim());
      out += i18nWrap('blockquote', '', inner) + '\n';
      i++; continue;
    }

    // ── Meta block items (**Key:** Value) — skip if already captured ─────────
    if (slideNum === 1 && line.match(/^\*\*(Duration|Instructor|Date|Course):\*\*/)) {
      i++; continue; // will be rendered as meta-tile later
    }

    // ── Lists ─────────────────────────────────────────────────────────────────
    if (line.match(/^(\s*[-*])\s+/)) {
      flushTable();
      if (inList !== 'ul') { if (inList) out += `</${inList}>\n`; out += `<ul>\n`; inList = 'ul'; }
      const content = inline(line.replace(/^\s*[-*]\s+/, '').trim());
      out += i18nWrap('li', '', content) + '\n';
      i++; continue;
    }
    if (line.match(/^\d+\.\s+/)) {
      flushTable();
      if (inList !== 'ol') { if (inList) out += `</${inList}>\n`; out += `<ol>\n`; inList = 'ol'; }
      const content = inline(line.replace(/^\d+\.\s+/, '').trim());
      out += i18nWrap('li', '', content) + '\n';
      i++; continue;
    }

    // ── Empty line ────────────────────────────────────────────────────────────
    if (line.trim() === '') {
      flushAll();
      i++; continue;
    }

    // ── Plain paragraph ───────────────────────────────────────────────────────
    flushAll();
    const inner = inline(line.trim());
    out += i18nWrap('p', '', inner) + '\n';
    i++;
  }

  flushList();
  flushTable();

  // Inject meta block for title slide
  if (slideNum === 1 && Object.keys(metaFields).length > 0) {
    let meta = '<div class="slide-meta">';
    for (const [k,v] of Object.entries(metaFields)) {
      meta += `<span><strong>${esc(k)}:</strong> ${esc(v)}</span>`;
    }
    meta += '</div>';
    out += meta;
  }

  return out;
}

function parseCells(line) {
  return line.replace(/^\||\|$/g,'').split('|').map(c => c.trim());
}

// ── Inline Markdown → HTML ────────────────────────────────────────────────────
function inline(s) {
  if (!s) return '';
  // Escape HTML first (but don't double-escape)
  s = s
    .replace(/&(?!amp;|lt;|gt;|quot;|#)/g, '&amp;')
    // Code inline
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    // Bold
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    // Italic
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    // Images
    .replace(/!\[([^\]]*)\]\(images\/([^)]+)\)/g,
      '<img src="../../images/$2" alt="$1">')
    // Images with width modifier like ![width:70%](images/...)
    .replace(/!\[([^\]]*)\]\(images\/([^)]+)\)/g,
      '<img src="../../images/$2" alt="$1">')
    // Links
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2">$1</a>');
  return s;
}

// ── Generate TOC (## sections) ────────────────────────────────────────────────
function extractTOC(slides) {
  const toc = [];
  for (const slide of slides) {
    const m = slide.html.match(/<h2>(.+?)<\/h2>/);
    if (m) toc.push({ num: slide.num, title: m[1].replace(/<[^>]+>/g,'') });
  }
  return toc;
}

// ── Generate slides.html ──────────────────────────────────────────────────────
function generateSlidesHtml(course, courseTitle, slides, toc) {
  const total = slides.length;

  // pill 1 gets class="current" as static fallback for no-anchor state
  const progressBar = slides.map((s,idx) =>
    `<a href="#slide-${s.num}"${idx===0?' class="current"':''}>${s.num}</a>`
  ).join('\n      ');

  const sidenav = toc.map((t, idx) =>
    `<a href="#slide-${t.num}"${idx===0?' class="active"':''}>${t.title}</a>`
  ).join('\n    ');

  // Nav bar embedded inside each <section> — always visible when its slide is shown
  const slidesSections = slides.map(s => {
    const prevBtn = s.num === 1
      ? `<a class="disabled">&#8592; Previous</a>`
      : `<a href="#slide-${s.num - 1}">&#8592; Previous</a>`;
    const nextBtn = s.num === total
      ? `<a class="disabled">Next &#8594;</a>`
      : `<a href="#slide-${s.num + 1}">Next &#8594;</a>`;
    const nav = `<div class="slide-nav">
  ${prevBtn}
  <span class="slide-info">Slide ${s.num} / ${total} &nbsp;&middot;&nbsp; ${esc(courseTitle)}</span>
  ${nextBtn}
</div>`;
    return `<section id="slide-${s.num}" class="${s.cls}">
${s.html}
<span class="slide-counter">${s.num} / ${total}</span>
${nav}
</section>`;
  }).join('\n\n');

  // Per-slide :target rules — highlight the matching progress-bar pill
  // Uses :has() so the rule can reach "upward" from the targeted section
  // body:has(#slide-N:target) .progress-bar a[href="#slide-N"] { current style }
  // Also suppress the static .current highlight whenever ANY anchor is active
  const targetStyles = [
    `/* suppress static .current when a slide is targeted */`,
    `body:has(.slide:target) .progress-bar a.current { background: var(--bg); color: var(--muted); border-color: var(--border); }`,
    ...slides.map(s =>
      `body:has(#slide-${s.num}:target) .progress-bar a[href="#slide-${s.num}"] { background: var(--interactive); color: #fff; border-color: var(--interactive); }`
    )
  ].join('\n');

  return `<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
  <title>ESIPE — ${esc(courseTitle)} — Slides</title>
  <link rel="stylesheet" href="../assets/slide.css"/>
  <style>
${targetStyles}
  </style>
</head>
<body>
<header class="site-header">
  <a class="brand" href="../index.html">ESIPE<span>Jakarta EE &amp; Microservices</span></a>
  <!-- EN/FR language toggle -->
  <div class="lang-toggle" id="lang-toggle">
    <button id="btn-en" class="active">EN</button>
    <span class="sep">|</span>
    <button id="btn-fr">FR</button>
  </div>
  <a class="back-link" href="../index.html">&#8592; All Courses</a>
</header>
<!-- Thin trigger strip at top: hover to reveal progress bar -->
<div class="progress-bar-trigger" id="pb-trigger"></div>
<!-- Progress bar — hidden by default, revealed on hover -->
<div class="progress-bar-wrap" id="pb-wrap">
  <div class="progress-bar">
    ${progressBar}
  </div>
</div>
<!-- Thin trigger strip at left: hover to reveal sidenav -->
<div class="sidenav-trigger" id="sidenav-trigger"></div>
<div class="shell">
  <nav class="sidenav" id="sidenav">
    <div class="sidenav-section">Course outline</div>
    ${sidenav}
    <hr>
    <div class="sidenav-section">Navigation</div>
    <a href="index.html">&#9675; Overview</a>
  </nav>
  <main class="content">
    <div class="slide-wrapper">
${slidesSections}
    </div>
  </main>
</div>
<footer class="site-footer">Made with IBM Bob &nbsp;&middot;&nbsp; &copy; 2026 Olivier Planson</footer>
<script>
/* ── EN/FR language toggle — global scope so buttons can call it ── */
var _lang = localStorage.getItem('slide-lang') || 'en';
function setLang(lang) {
  _lang = lang;
  localStorage.setItem('slide-lang', lang);
  document.getElementById('btn-en').classList.toggle('active', lang === 'en');
  document.getElementById('btn-fr').classList.toggle('active', lang === 'fr');
  document.querySelectorAll('[data-en][data-fr]').forEach(function(el) {
    el.innerHTML = lang === 'fr' ? el.getAttribute('data-fr') : el.getAttribute('data-en');
  });
}

(function() {
  /* ── Sidenav hover ── */
  var nav     = document.getElementById('sidenav');
  var navTrig = document.getElementById('sidenav-trigger');
  var navTimer = null;
  function navShow() { clearTimeout(navTimer); nav.classList.add('sidenav--open'); }
  function navHide() { navTimer = setTimeout(function(){ nav.classList.remove('sidenav--open'); }, 300); }
  navTrig.addEventListener('mouseenter', navShow);
  navTrig.addEventListener('mouseleave', navHide);
  nav.addEventListener('mouseenter', navShow);
  nav.addEventListener('mouseleave', navHide);

  /* ── Progress bar hover ── */
  var pbWrap  = document.getElementById('pb-wrap');
  var pbTrig  = document.getElementById('pb-trigger');
  var pbTimer = null;
  function pbShow() { clearTimeout(pbTimer); pbWrap.classList.add('pb-open'); }
  function pbHide() { pbTimer = setTimeout(function(){ pbWrap.classList.remove('pb-open'); }, 400); }
  pbTrig.addEventListener('mouseenter', pbShow);
  pbTrig.addEventListener('mouseleave', pbHide);
  pbWrap.addEventListener('mouseenter', pbShow);
  pbWrap.addEventListener('mouseleave', pbHide);

  /* ── Wire EN/FR buttons via addEventListener (no inline onclick) ── */
  document.getElementById('btn-en').addEventListener('click', function() { setLang('en'); });
  document.getElementById('btn-fr').addEventListener('click', function() { setLang('fr'); });

  /* Apply stored preference on page load */
  if (_lang === 'fr') setLang('fr');
})();
</script>
</body>
</html>`;
}

// ── Generate {slug}/index.html ────────────────────────────────────────────────
function generateCourseIndex(course, courseTitle, slides, toc, footer) {
  const total = slides.length;

  // Extract meta from slide 1
  const slide1html = slides[0] ? slides[0].html : '';
  const metaMatch = slide1html.match(/<div class="slide-meta">([\s\S]*?)<\/div>/);
  let duration = '', instructor = '', date = '';
  if (metaMatch) {
    const dm = metaMatch[1].match(/<strong>Duration:<\/strong>\s*([^<]+)/); if (dm) duration = dm[1].trim();
    const im = metaMatch[1].match(/<strong>Instructor:<\/strong>\s*([^<]+)/); if (im) instructor = im[1].trim();
    const dtm = metaMatch[1].match(/<strong>Date:<\/strong>\s*([^<]+)/); if (dtm) date = dtm[1].trim();
  }
  // fallback from footer
  if (!duration && footer) {
    const dm = footer.match(/(\d+\s*h(?:ours?)?)/i); if (dm) duration = dm[1];
  }

  const subtitle = footer ? footer.split('|')[0].trim() : courseTitle;

  const planItems = toc.map(t =>
    `<li><a href="slides.html#slide-${t.num}">${t.title.replace(/<[^>]+>/g,'')}</a></li>`
  ).join('\n    ');

  return `<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
  <title>ESIPE — ${esc(courseTitle)}</title>
  <link rel="stylesheet" href="../assets/slide.css"/>
</head>
<body>
<header class="site-header">
  <a class="brand" href="../index.html">ESIPE<span>Jakarta EE &amp; Microservices</span></a>
  <a class="back-link" href="../index.html">&#8592; All Courses</a>
</header>
<main class="course-overview">
  <h1>${esc(courseTitle)}</h1>
  <p class="subtitle">${esc(subtitle)}</p>
  <div class="meta-tile">
    ${duration ? `<span><strong>Duration:</strong> ${esc(duration)}</span>` : ''}
    ${instructor ? `<span><strong>Instructor:</strong> ${esc(instructor)}</span>` : ''}
    ${date ? `<span><strong>Date:</strong> ${esc(date)}</span>` : ''}
    <span><strong>Slides:</strong> ${total} slides</span>
  </div>
  <h2 style="font-size:1rem;font-weight:600;margin:1.5rem 0 .75rem;">&#128218; Course outline</h2>
  <ol class="plan-list">
    ${planItems}
  </ol>
  <a class="start-btn" href="slides.html#slide-1">&#9654; Start course</a>
</main>
<footer class="site-footer">Made with IBM Bob &nbsp;&middot;&nbsp; &copy; 2026 Olivier Planson</footer>
</body>
</html>`;
}

// ── Generate global index.html ─────────────────────────────────────────────────
function generateGlobalIndex(results) {
  const groups = [
    {
      title: '🚀 Jakarta EE Foundations',
      slugs: ['01-intro','02-servlets','02b-jsf'],
    },
    {
      title: '💾 Data &amp; Business Layer',
      slugs: ['03-jpa','04-cdi','04b-ejb'],
    },
    {
      title: '🌐 Services &amp; Messaging',
      slugs: ['05-rest','05b-jms'],
    },
    {
      title: '🏛️ Advanced Architectures',
      slugs: ['06-ddd','07-hexagonal','08-microservices'],
    },
    {
      title: '🔐 Security',
      slugs: ['09-security'],
    },
  ];

  const tagColors = {
    'Jakarta EE 10':'blue','MicroProfile 6':'blue','Open Liberty':'green','Podman':'teal',
    'Servlets':'blue','JSP':'blue','JSTL':'teal','MVC':'purple','MicroProfile Config':'green',
    'JSF':'blue','Facelets':'teal','PrimeFaces':'purple','Component UI':'teal',
    'JPA':'blue','Hibernate':'green','PostgreSQL':'teal','JPQL':'purple',
    'CDI':'blue','DI':'teal','Interceptors':'purple','Events':'green',
    'EJB':'blue','Stateless':'teal','Stateful':'purple','Singleton':'green','JTA':'teal',
    'JAX-RS':'blue','REST':'green','JSON-B':'teal','MP Rest Client':'purple',
    'JMS':'blue','MDB':'teal','ActiveMQ':'green','Messaging':'purple',
    'DDD':'purple','Bounded Context':'blue','Aggregates':'teal','Domain Events':'green',
    'Hexagonal':'purple','Ports & Adapters':'blue','Clean Architecture':'teal',
    'Microservices':'blue','MP Health':'green','MP Metrics':'teal','Fault Tolerance':'warm',
    'Security':'red','JWT':'warm','OIDC':'blue','RBAC':'purple','MP JWT':'green',
  };

  const bySlug = {};
  results.forEach(r => { bySlug[r.slug] = r; });

  let groupsHtml = '';
  for (const grp of groups) {
    let cardsHtml = '';
    for (const slug of grp.slugs) {
      const r = bySlug[slug];
      const course = COURSES.find(c => c.slug === slug);
      if (!r || !course) continue;
      const tagsHtml = course.tags.map(t => {
        const color = tagColors[t] || 'blue';
        return `<span class="cds--tag cds--tag--${color}">${esc(t)}</span>`;
      }).join('');
      cardsHtml += `<a class="course-card" href="${slug}/index.html">
  <div class="num">${esc(course.num)}</div>
  <h3>${esc(r.courseTitle)}</h3>
  <p>${r.slides.length} slides</p>
  <div class="tag-stack">${tagsHtml}</div>
</a>\n`;
    }
    groupsHtml += `<div class="course-group">
  <h2>${grp.title}</h2>
  <div class="course-cards">
${cardsHtml}  </div>
</div>\n`;
  }

  return `<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
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
${groupsHtml}
</main>
<footer class="site-footer">Made with IBM Bob &nbsp;&middot;&nbsp; &copy; 2026 Olivier Planson</footer>
</body>
</html>`;
}

// ── Main ──────────────────────────────────────────────────────────────────────
const results = [];
let totalSlides = 0;

for (const course of COURSES) {
  const mdPath = path.join(LECTURES_DIR, course.file);
  if (!fs.existsSync(mdPath)) {
    console.warn(`⚠️  Skipping ${course.file} — not found`);
    continue;
  }
  console.log(`📖 Parsing ${course.file}...`);
  const { courseTitle, footer, slides } = parseMd(mdPath);
  const toc = extractTOC(slides);
  totalSlides += slides.length;

  // Write slides.html
  const slidesDir = path.join(OUT_DIR, course.slug);
  if (!fs.existsSync(slidesDir)) fs.mkdirSync(slidesDir, { recursive: true });

  const slidesHtml = generateSlidesHtml(course, courseTitle, slides, toc);
  fs.writeFileSync(path.join(slidesDir, 'slides.html'), slidesHtml, 'utf8');

  // Write index.html
  const indexHtml = generateCourseIndex(course, courseTitle, slides, toc, footer);
  fs.writeFileSync(path.join(slidesDir, 'index.html'), indexHtml, 'utf8');

  results.push({ slug: course.slug, courseTitle, slides, toc });
  console.log(`  ✅ ${course.slug}/ — ${slides.length} slides, ${toc.length} sections`);
}

// Write global index
const globalIndex = generateGlobalIndex(results);
fs.writeFileSync(path.join(OUT_DIR, 'index.html'), globalIndex, 'utf8');
console.log(`\n✅ Global index written`);
console.log(`\n🎉 Site generated: ${results.length} courses, ${totalSlides} total slides`);
console.log(`👉 Open 02-Lectures/slides/index.html in your browser`);
