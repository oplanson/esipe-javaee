/* Language toggle — EN / FR
   Reads data-en / data-fr attributes on all instrumented elements
   and swaps visible text on click. */
(function () {
  'use strict';

  var LANG_KEY = 'preferred-lang';
  var current = localStorage.getItem(LANG_KEY) || 'fr';

  function applyLang(lang) {
    current = lang;
    localStorage.setItem(LANG_KEY, lang);

    // Update all instrumented elements
    document.querySelectorAll('[data-en][data-fr]').forEach(function (el) {
      el.innerHTML = lang === 'en' ? el.getAttribute('data-en') : el.getAttribute('data-fr');
    });

    // Update img alt attributes
    document.querySelectorAll('img[data-en][data-fr]').forEach(function (el) {
      el.setAttribute('alt', lang === 'en' ? el.getAttribute('data-en') : el.getAttribute('data-fr'));
    });

    // Update all toggle buttons / links
    document.querySelectorAll('#lang-toggle, .cds--side-nav__lang-toggle').forEach(function (btn) {
      btn.textContent = lang === 'en' ? 'FR' : 'EN';
      btn.title = lang === 'en' ? 'Passer en français' : 'Switch to English';
    });

    // Update <html> lang attribute
    document.documentElement.setAttribute('lang', lang);
  }

  document.addEventListener('DOMContentLoaded', function () {
    applyLang(current);

    document.querySelectorAll('#lang-toggle, .cds--side-nav__lang-toggle').forEach(function (btn) {
      btn.addEventListener('click', function () {
        applyLang(current === 'en' ? 'fr' : 'en');
      });
    });
  });
})();
