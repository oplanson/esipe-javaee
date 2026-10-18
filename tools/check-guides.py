#!/usr/bin/env python3
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
"""check-guides.py — contrôle des guides de labs (docs/labs/*.html).

Usage :  python3 tools/check-guides.py [--json] [lab-guide.html ...]
Sans argument : contrôle tous les guides `docs/labs/lab*.html` + `docs/labs/index.html`.
Code retour : 0 si aucune erreur, 1 sinon (les avertissements ne font pas échouer).

Contrôles :
  S  structure : sections normalisées, ordre, étapes numérotées
  L  langue    : parité EN/FR (blocs jumeaux lang="en"/lang="fr", même structure de balises)
  P  Pages     : ressources locales, pas de lien absolu, noms en minuscules, assets identiques
  C  code      : liens GitHub blob -> fichier existant dans le dépôt
  H  HTML      : balises équilibrées, pas de script inline / handler on*
  V  couverture: chaque fichier différent starter/solution et chaque TODO est cité (si manifeste)
"""
import json
import re
import subprocess
import sys
from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import unquote, urlparse

REPO = Path(__file__).resolve().parent.parent
LABS = REPO / "docs" / "labs"
GITHUB_BLOB = "https://github.com/oplanson/esipe-javaee/blob/master/"
SECTIONS = ["hero", "goal", "starter", "target", "setup", "steps", "run",
            "validate", "compare", "trouble", "bonus"]
VOID = {"meta", "link", "br", "hr", "img", "input", "source", "area", "base", "col", "embed", "wbr"}
REQUIRED_ASSETS = ["assets/slide.css", "assets/lab-guide.css", "assets/lab-guide.js"]

class Doc(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.stack, self.errors = [], []
        self.ids, self.hrefs, self.srcs, self.links, self.scripts = [], [], [], [], []
        self.inline_scripts, self.handlers = 0, []
        self.lang_blocks = []          # (lang, signature, line)
        self._sig = []                 # signature en cours pour bloc lang
        self._sig_depth = None
        self._lang_cur = None
        self.steps = []                # ids des étapes
        self.text = []
        self.in_code = 0

    def handle_starttag(self, tag, attrs):
        a = dict(attrs)
        line = self.getpos()[0]
        if "id" in a:
            self.ids.append(a["id"])
        for k in a:
            if k.startswith("on"):
                self.handlers.append((tag, k, line))
        if tag == "a" and a.get("href"):
            self.hrefs.append((a["href"], line))
        if tag in ("img", "source") and a.get("src"):
            self.srcs.append((a["src"], line))
        if tag == "link" and a.get("href"):
            self.links.append((a["href"], line))
        if tag == "script":
            if a.get("src"):
                self.scripts.append((a["src"], line))
            else:
                self.inline_scripts += 1
        if a.get("class") and "lg-step" in a["class"].split() and a.get("id"):
            self.steps.append(a["id"])
        if tag not in VOID:
            self.stack.append((tag, line))
        # blocs de langue (hors <html> et <span> d'interface <button>)
        if a.get("lang") in ("en", "fr") and tag not in ("html",):
            if self._sig_depth is None:
                self._lang_cur = a["lang"]
                self._sig = [tag]
                self._sig_depth = len(self.stack)
                self._lang_line = line
        elif self._sig_depth is not None and tag not in VOID:
            self._sig.append(tag)
        if tag == "code" or tag == "pre":
            self.in_code += 1

    def handle_endtag(self, tag):
        if tag in VOID:
            return
        if not self.stack:
            self.errors.append(f"</{tag}> sans ouverture (ligne {self.getpos()[0]})")
            return
        top, line = self.stack[-1]
        if top != tag:
            self.errors.append(f"</{tag}> ligne {self.getpos()[0]} ferme <{top}> ouvert ligne {line}")
            # tentative de resynchronisation
            for i in range(len(self.stack) - 1, -1, -1):
                if self.stack[i][0] == tag:
                    del self.stack[i:]
                    break
        else:
            self.stack.pop()
        if self._sig_depth is not None and len(self.stack) < self._sig_depth:
            self.lang_blocks.append((self._lang_cur, tuple(self._sig), self._lang_line))
            self._sig_depth = None
        elif self._sig_depth is not None:
            self._sig.append("/" + tag)
        if tag in ("code", "pre") and self.in_code:
            self.in_code -= 1

    def handle_data(self, data):
        self.text.append(data)


def parse(path: Path) -> Doc:
    d = Doc()
    d.feed(path.read_text(encoding="utf-8"))
    for tag, line in d.stack:
        d.errors.append(f"<{tag}> ouvert ligne {line} jamais fermé")
    return d


def git_has(rel: str) -> bool:
    return (REPO / rel).exists()


def check_guide(path: Path):
    errs, warns = [], []
    raw = path.read_text(encoding="utf-8")
    d = parse(path)

    # --- H : HTML
    errs += [f"H: {e}" for e in d.errors]
    if d.inline_scripts:
        errs.append(f"H: {d.inline_scripts} <script> inline (interdit, utiliser assets/lab-guide.js)")
    for tag, attr, line in d.handlers:
        errs.append(f"H: attribut {attr} sur <{tag}> ligne {line}")
    if not raw.lstrip().startswith("<!--") or "Olivier Planson" not in raw[:300]:
        errs.append("H: commentaire de copyright manquant en 1re ligne")
    if "Made with IBM Bob" not in raw:
        errs.append("H: footer « Made with IBM Bob » manquant")
    dup = {i for i in d.ids if d.ids.count(i) > 1}
    if dup:
        errs.append(f"H: ids dupliqués : {sorted(dup)}")

    # --- S : structure
    ids = [i for i in d.ids if i in SECTIONS]
    for s in SECTIONS:
        if s not in d.ids:
            errs.append(f"S: section #{s} absente")
    if ids != [s for s in SECTIONS if s in ids]:
        errs.append(f"S: ordre des sections incorrect : {ids}")
    if not d.steps:
        errs.append("S: aucune étape (.lg-step avec id)")
    else:
        expected = [f"step-{i}" for i in range(1, len(d.steps) + 1)]
        if d.steps != expected:
            errs.append(f"S: étapes non séquentielles : {d.steps} (attendu {expected})")
    if '<html lang="fr"' not in raw and "<html lang='fr'" not in raw:
        warns.append("S: <html lang> initial attendu = fr (valeur par défaut)")

    # --- L : parité EN/FR
    en = [b for b in d.lang_blocks if b[0] == "en"]
    fr = [b for b in d.lang_blocks if b[0] == "fr"]
    if len(en) != len(fr):
        errs.append(f"L: {len(en)} blocs lang=en pour {len(fr)} blocs lang=fr")
    for i, (e, f) in enumerate(zip(en, fr)):
        if e[1] != f[1]:
            errs.append(f"L: bloc #{i + 1} EN (l.{e[2]}) et FR (l.{f[2]}) n'ont pas la même structure de balises")
            if len([x for x in errs if x.startswith('L: bloc')]) >= 8:
                errs.append("L: … (autres écarts de structure tronqués)")
                break
    if not en:
        errs.append("L: aucun bloc lang=en/lang=fr trouvé")
    # mots d'un seul côté : texte visible hors blocs de langue (hors code) – heuristique légère
    if "data-set-lang=\"en\"" not in raw or "data-set-lang=\"fr\"" not in raw:
        errs.append("L: boutons de bascule EN/FR (data-set-lang) manquants")

    # --- P : publiabilité Pages
    if not re.fullmatch(r"[a-z0-9\-]+\.html", path.name):
        errs.append(f"P: nom de fichier non conforme (minuscules/tirets) : {path.name}")
    for href, line in d.links:
        local = urlparse(href)
        if local.scheme or href.startswith("//") or href.startswith("/"):
            errs.append(f"P: <link> externe ou absolu ligne {line} : {href}")
    for src, line in d.scripts + d.srcs:
        if urlparse(src).scheme or src.startswith("/"):
            errs.append(f"P: ressource externe ou absolue ligne {line} : {src}")
    for need in REQUIRED_ASSETS:
        if need not in raw:
            errs.append(f"P: ressource {need} non chargée")
    for href, line in d.links + d.scripts + d.srcs:
        h = href.split("#")[0].split("?")[0]
        if h and not urlparse(h).scheme and not h.startswith("/") and not (LABS / h).resolve().exists():
            errs.append(f"P: ressource introuvable ligne {line} : {href}")
    for href, line in d.hrefs:
        if href.startswith("/") and not href.startswith("//"):
            errs.append(f"P: lien absolu interdit ligne {line} : {href}")
        if href.startswith("//"):
            errs.append(f"P: lien protocole-relatif interdit ligne {line} : {href}")
        if href.startswith("javascript:"):
            errs.append(f"P: lien javascript: ligne {line}")
        p = urlparse(href)
        if not p.scheme and not href.startswith(("#", "mailto:")) and href:
            target = (LABS / unquote(p.path)).resolve()
            if p.path and not target.exists():
                errs.append(f"P: lien relatif cassé ligne {line} : {href}")
            elif p.path and REPO not in target.parents and target != REPO:
                errs.append(f"P: lien sort du dépôt ligne {line} : {href}")
            elif p.path and p.fragment and target.suffix == ".html":
                if f'id="{p.fragment}"' not in target.read_text(encoding="utf-8"):
                    warns.append(f"P: ancre #{p.fragment} introuvable dans {p.path} (ligne {line})")
    for href, line in d.hrefs:
        if href.startswith("#") and len(href) > 1 and href[1:] not in d.ids:
            errs.append(f"P: ancre interne cassée ligne {line} : {href}")

    # --- C : liens vers le code
    for href, line in d.hrefs:
        if href.startswith(GITHUB_BLOB):
            rel = unquote(href[len(GITHUB_BLOB):].split("#")[0].split("?")[0])
            if not git_has(rel):
                errs.append(f"C: fichier GitHub introuvable ligne {line} : {rel}")
            if re.search(r"/(target)/|\.(war|jar)$", rel):
                errs.append(f"C: lien vers un fichier ignoré ligne {line} : {rel}")
        elif href.startswith("http://") and not re.match(r"http://(localhost|127\.0\.0\.1)[:/]", href):
            errs.append(f"P: lien http non chiffré ligne {line} : {href}")
        elif "github.com/oplanson/esipe-javaee" in href and "/blob/" in href:
            errs.append(f"C: lien GitHub hors master ou mal formé ligne {line} : {href}")

    # --- Traductions suspectes (mélange de langues)
    for bad in ("Utilisation de ", "Gestion des "):
        # ne s'applique qu'aux blocs EN : repérés via regex grossière sur lang="en"
        for m in re.finditer(r'<(\w+)[^>]*lang="en"[^>]*>(.*?)</\1>', raw, re.S):
            if bad in m.group(2):
                warns.append(f"L: texte français « {bad.strip()} » dans un bloc EN")
                break

    # --- V : couverture (manifeste optionnel)
    manifest = path.with_suffix(".manifest.json")
    if not manifest.exists():
        manifest = REPO / "tools" / "manifests" / (path.stem + ".json")
    if manifest.exists():
        errs += check_coverage(json.loads(manifest.read_text(encoding="utf-8")), raw, d)
    else:
        warns.append("V: aucun manifeste de couverture (tools/manifests/<guide>.json)")
    return errs, warns


def check_coverage(m, raw, d):
    errs = []
    cited = " ".join(d.text) + " " + raw
    for f in m.get("must_cite", []):
        if f not in cited:
            errs.append(f"V: fichier/TODO non cité : {f}")
    # chaque fichier doit apparaître dans au moins une étape (<article class="lg-step" id="step-N">)
    steps_html = re.findall(r'<article class="lg-step" id="step-\d+">(.*?)</article>', raw, re.S)
    steps_text = " ".join(steps_html)
    for f in m.get("must_cite_in_steps", []):
        if f not in steps_text:
            errs.append(f"V: fichier absent des étapes : {f}")
    return errs


def check_index():
    errs = []
    idx = LABS / "index.html"
    if not idx.exists():
        return ["I: docs/labs/index.html absent"]
    d = parse(idx)
    raw = idx.read_text(encoding="utf-8")
    errs += [f"H(index): {e}" for e in d.errors]
    for g in sorted(LABS.glob("lab*.html")):
        if f'href="{g.name}"' not in raw:
            errs.append(f"I: {g.name} non référencé dans index.html")
    for href, line in d.hrefs:
        p = urlparse(href)
        if not p.scheme and not href.startswith(("#", "mailto:")) and p.path and not (LABS / unquote(p.path)).resolve().exists():
            errs.append(f"I: lien cassé index.html ligne {line} : {href}")
    return errs


def check_assets():
    errs = []
    src = REPO / "02-Lectures" / "slides" / "assets" / "slide.css"
    dst = LABS / "assets" / "slide.css"
    if not dst.exists():
        errs.append("A: docs/labs/assets/slide.css absent")
    elif src.exists() and src.read_bytes() != dst.read_bytes():
        errs.append("A: docs/labs/assets/slide.css diffère de 02-Lectures/slides/assets/slide.css (recopier)")
    for n in ("lab-guide.css", "lab-guide.js"):
        if not (LABS / "assets" / n).exists():
            errs.append(f"A: assets/{n} absent")
    return errs


def main(argv):
    as_json = "--json" in argv
    args = [a for a in argv if not a.startswith("--")]
    files = [Path(a).resolve() for a in args] if args else sorted(LABS.glob("lab*.html"))
    report, failed = {}, False
    top = check_assets()
    if not args:
        top += check_index()
    for g in files:
        e, w = check_guide(g)
        report[g.name] = {"errors": e, "warnings": w}
        failed |= bool(e)
    failed |= bool(top)
    if as_json:
        print(json.dumps({"global": top, "guides": report}, indent=2, ensure_ascii=False))
    else:
        for t in top:
            print(f"[GLOBAL] ERREUR {t}")
        for name, r in report.items():
            status = "OK " if not r["errors"] else "KO "
            print(f"{status} {name}  ({len(r['errors'])} erreur(s), {len(r['warnings'])} avertissement(s))")
            for e in r["errors"]:
                print(f"      ERREUR  {e}")
            for w in r["warnings"]:
                print(f"      warn    {w}")
        print("\nRésultat :", "ÉCHEC" if failed else "SUCCÈS")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
