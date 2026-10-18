/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */
/* lab-guide.js — bascule EN/FR, sommaire (scroll-spy), boutons « copier », checklist persistante.
   Charger dans <head> (sans defer) pour appliquer la langue avant le premier affichage. */
(function () {
  'use strict';

  var LANG_KEY = 'preferred-lang';   // clé du site docs/ (défaut : fr)
  var SLIDE_KEY = 'slide-lang';      // clé des slides, tenue synchrone

  function read(key) {
    try { return window.localStorage.getItem(key); } catch (e) { return null; }
  }
  function write(key, value) {
    try { window.localStorage.setItem(key, value); } catch (e) { /* stockage indisponible */ }
  }
  function valid(l) { return l === 'en' || l === 'fr'; }

  var current = read(LANG_KEY);
  if (!valid(current)) { current = read(SLIDE_KEY); }
  if (!valid(current)) { current = 'fr'; }

  function applyLang(lang) {
    current = lang;
    write(LANG_KEY, lang);
    write(SLIDE_KEY, lang);
    var root = document.documentElement;
    root.setAttribute('data-lang', lang);
    root.setAttribute('lang', lang);

    var title = document.querySelector('title');
    if (title && title.getAttribute('data-title-' + lang)) {
      document.title = title.getAttribute('data-title-' + lang);
    }
    var buttons = document.querySelectorAll('[data-set-lang]');
    for (var i = 0; i < buttons.length; i++) {
      buttons[i].classList.toggle('active', buttons[i].getAttribute('data-set-lang') === lang);
    }
  }

  // Application immédiate (le script est dans <head>)
  applyLang(current);

  document.addEventListener('DOMContentLoaded', function () {
    applyLang(current);

    // Boutons EN / FR
    var langButtons = document.querySelectorAll('[data-set-lang]');
    for (var i = 0; i < langButtons.length; i++) {
      langButtons[i].addEventListener('click', function (ev) {
        applyLang(ev.currentTarget.getAttribute('data-set-lang'));
      });
    }

    // Boutons « copier » sur les blocs de code
    var blocks = document.querySelectorAll('pre.dark');
    for (var b = 0; b < blocks.length; b++) {
      (function (pre) {
        var btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'lg-copy';
        btn.innerHTML = '<span lang="en">Copy</span><span lang="fr">Copier</span>';
        btn.addEventListener('click', function () {
          var code = pre.querySelector('code');
          var text = (code ? code.textContent : pre.textContent) || '';
          var done = function () {
            btn.innerHTML = '<span lang="en">Copied</span><span lang="fr">Copié</span>';
            window.setTimeout(function () {
              btn.innerHTML = '<span lang="en">Copy</span><span lang="fr">Copier</span>';
            }, 1500);
          };
          if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(text).then(done, function () { /* refus du navigateur */ });
          }
        });
        pre.appendChild(btn);
      })(blocks[b]);
    }

    // Checklist persistante (par page)
    var boxes = document.querySelectorAll('.lg-checklist input[type="checkbox"]');
    for (var c = 0; c < boxes.length; c++) {
      (function (box, idx) {
        var key = 'lg-check:' + window.location.pathname + ':' + idx;
        box.checked = read(key) === '1';
        box.addEventListener('change', function () { write(key, box.checked ? '1' : '0'); });
      })(boxes[c], c);
    }

    // Sommaire : surbrillance de la section visible
    var links = document.querySelectorAll('.lg-toc a[href^="#"]');
    if (links.length && 'IntersectionObserver' in window) {
      var byId = {};
      for (var k = 0; k < links.length; k++) { byId[links[k].getAttribute('href').slice(1)] = links[k]; }
      var observer = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
          if (entry.isIntersecting && byId[entry.target.id]) {
            for (var id in byId) { byId[id].classList.remove('active'); }
            byId[entry.target.id].classList.add('active');
          }
        });
      }, { rootMargin: '-15% 0px -70% 0px' });
      for (var id in byId) {
        var target = document.getElementById(id);
        if (target) { observer.observe(target); }
      }
    }
  });
})();
