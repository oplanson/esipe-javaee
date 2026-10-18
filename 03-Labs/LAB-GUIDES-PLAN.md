<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Plan — Guides HTML bilingues (EN/FR) pour les 12 Labs

> Statut : **v2 — recommandations validées**. Ajout : les guides doivent être publiables sur GitHub Pages dans `docs/labs/` (voir §3bis).

## 1. Objectif

Pour chaque lab de `03-Labs/`, produire **une page HTML autonome** qui :

1. explique le **but pédagogique** du lab (et le lien avec le cours / les slides) ;
2. décrit **précisément ce que contient le `starter/`** (ce qui est fourni, ce qui manque) ;
3. donne des **consignes pas-à-pas** permettant à l'étudiant, en partant du `starter/`, d'arriver au code de `solution/` ;
4. propose des **points de contrôle** (commande + résultat attendu) et une **checklist de validation** ;
5. est **bilingue EN/FR** avec un bouton de bascule, et réutilise **le même CSS que les slides**.

## 2. Constats de l'analyse (starter vs solution)

Le point clé : **les starters ne sont pas homogènes**. Le contenu d'une page dépend de l'état réel du starter.

| Lab | Starter (état réel) | Solution (périmètre) | Stratégie de « point de départ » |
|---|---|---|---|
| **01 FirstServlet** | Config seule (pom, server.xml, web.xml, index.html, css). **0 classe Java** | 3 classes Java (334 LOC), `add-client.html`, `resources/` | Créer from scratch (Client, WelcomeServlet, ClientListServlet, formulaire) |
| **02 ServletsJSP** | 12 classes Java + 3 JSP en squelette, **297 TODO** | 12 Java (2 090 LOC), filtres, listeners, health | Compléter les TODO fichier par fichier |
| **02B JSF** | Seulement `templates/main.xhtml`. **Pas de pom.xml** | 25 fichiers, 8 Java (721 LOC) | Partir de la Lab 02 + créer pom/vues/beans |
| **03 JPA** | 7 Java, **117 TODO** ; manque persistence.xml, Flyway, repository, error pages | 9 Java (1 958 LOC), 26 fichiers | TODO + fichiers à créer (migrations SQL, persistence.xml, repository) |
| **04 CDI** | **Arborescence vide (0 fichier)** | 25 Java (3 959 LOC), 50 fichiers | Baseline = **solution Lab 03** → refactor vers CDI |
| **04B EJB** | 8 Java, **57 TODO** ; manque 3 EJB, enums, migrations, vues | 16 Java (2 921 LOC) | TODO + fichiers à créer |
| **05 REST** | 2 Java seulement (`ApiInfo`, `RootResource`) | 39 Java (5 498 LOC), 68 fichiers | Baseline = **solution Lab 04** + ajout couche JAX-RS |
| **05B JMS** | 11 Java, **44 TODO** ; manque package `util` | 12 Java (1 773 LOC) | TODO + fichiers à créer |
| **06 DDD** | **Répertoire vide** | 46 Java (7 247 LOC) | Baseline = **solution Lab 05** → refactor DDD |
| **07 Hexagonal** | **Répertoire vide** (le README annonce du contenu) | 44 Java (5 293 LOC) | Baseline = **solution Lab 06** → refactor hexagonal |
| **08 Microservices** | Squelette de 3 services, config seule, **0 Java**, 16 TODO config | 47 Java (5 555 LOC), 81 fichiers | Construire 3 services depuis la config |
| **09 Security** | **20 classes Java déjà complètes** (≈174 lignes de diff avec la solution) + un dossier `solution/` imbriqué | 21 Java (2 885 LOC) | ⚠️ à trancher (voir §8) |

### Anomalies à connaître avant de générer
- **Lab 02B, 04, 05** : starter sans `pom.xml` → le guide doit dire explicitement quoi créer/copier.
- **Lab 06, 07** : starters vides alors que les README décrivent des TODO → les guides doivent se baser sur la **diff solution N-1 → solution N**, pas sur le starter.
- **Lab 09** : `README-STARTER.md` indique « Complete Implementation » ; le starter contient déjà quasiment la solution. L'exercice réel est très réduit (SecurityHeadersServletFilter, correctifs JwtService/SecurityAudit/CorsFilter/persistence/server.xml).
- **Lab 07** : le README référence `solution/REFACTORING-GUIDE.md`, qui n'existe pas dans `solution/`.
- Les README font de 629 à 1 875 lignes et sont de qualité très variable : ils servent de **source d'intention**, mais la **vérité = `diff -r starter solution`**.

## 3. Livrables et arborescence

**Source unique = `docs/labs/`** (pas de copie dans `03-Labs/`, pour éviter toute divergence).

```
docs/                                   # racine du site de cours déjà publié
├── index.html                          # MIS À JOUR : section/carte « Labs »
└── labs/                               # NOUVEAU — sous-répertoire publié
    ├── index.html                      #   sommaire des 12 labs (EN/FR)
    ├── assets/
    │   ├── slide.css                   #   copie autonome de 02-Lectures/slides/assets/slide.css
    │   ├── lab-guide.css               #   extensions (étapes, callouts, arbre de fichiers, TOC)
    │   ├── lab-guide.js                #   bascule EN/FR + TOC + copie de code
    │   └── LAB-GUIDE-TEMPLATE.html     #   gabarit de référence (non lié depuis le site)
    ├── lab01-first-servlet.html
    ├── lab02-servlets-jsp.html
    ├── lab02b-jsf.html
    ├── lab03-jpa.html
    ├── lab04-cdi.html
    ├── lab04b-ejb.html
    ├── lab05-rest.html
    ├── lab05b-jms.html
    ├── lab06-ddd.html
    ├── lab07-hexagonal.html
    ├── lab08-microservices.html
    └── lab09-security.html
03-Labs/
├── LAB-GUIDES-PLAN.md                  # ce document
├── LAB-GUIDES-REVIEW.md                # rapport de l'agent de contrôle
└── index.html                          # MIS À JOUR : lien « Guide » par carte
tools/check-guides.py                   # script de contrôle (hors site publié)
```

## 3bis. Contraintes de publication GitHub Pages

Constats (`.github/workflows/static.yml`) : déploiement de **tout le dépôt** (`path: '.'`) sur push `master`. Le site de cours est dans `docs/`, les slides dans `02-Lectures/slides/`, les labs dans `03-Labs/`.

| Règle | Détail |
|---|---|
| **Autonomie** | Pour ses **ressources** (CSS/JS/images), une page `docs/labs/*.html` ne dépend que de `docs/labs/assets/` : aucun `<link>`/`<script>` vers `02-Lectures/` ni `docs/assets/`. Seuls des liens de **navigation** `<a>` pointent hors du dossier. `slide.css` est copié dans `docs/labs/assets/` (le contrôleur vérifie qu'il est identique à la source). |
| **Chemins relatifs uniquement** | Pas de `/` initial ni d'URL absolue du site → fonctionne sous `https://oplanson.github.io/esipe-javaee/` (sous-chemin du dépôt). |
| **Liens vers le code** | Les `.java/.xml/.sql` ne s'affichent pas bien via Pages (téléchargement). Liens de code = URL **GitHub blob** `https://github.com/oplanson/esipe-javaee/blob/master/03-Labs/…` ; le chemin local est affiché en texte `code`. Le contrôleur vérifie que la cible existe dans le dépôt. |
| **Liens inter-sites** | Vers les cours : `../03-jpa/index.html` (relatif depuis `docs/labs/`). Vers les slides : `../../02-Lectures/slides/03-jpa/index.html` (relatif, publié car dépôt entier). |
| **Casse / noms** | Noms de fichiers en minuscules, sans espace ni accent (Pages est sensible à la casse). |
| **URL publiée** | Le workflow publie la racine du dépôt : les guides seront servis sous `https://oplanson.github.io/esipe-javaee/docs/labs/` (à confirmer avec la config Pages du dépôt). |
| **Pas de build** | HTML/CSS/JS statiques, pas de Jekyll requis (ajout d'un `docs/labs/.nojekyll` non nécessaire car le workflow ne passe pas par Jekyll). |
| **Langue partagée** | Clé `localStorage` `preferred-lang` (celle du site `docs/`, défaut **fr**) ; le script écrit aussi `slide-lang` pour rester synchrone avec les slides. |
| **Test local** | `python3 -m http.server` à la racine du dépôt, puis ouvrir `/docs/labs/` ; le contrôleur teste aussi une simulation sous sous-chemin. |
| **Ignorés** | `.gitignore` exclut `target/`, `*.war`, `*.jar` : les guides ne doivent référencer aucun de ces fichiers. |

## 4. Charte graphique & technique (commune à toutes les pages)

- **CSS** : on charge `assets/slide.css` (copie autonome de `02-Lectures/slides/assets/slide.css`, mêmes variables Carbon `--interactive`, `--layer`, badges `.badge-*`, `.slide-table`, `pre.dark`, `.lang-badge`, header sombre, `.lang-toggle`, footer). Seules les classes manquantes (étape numérotée, callout, arbre de fichiers, checklist) vont dans `assets/lab-guide.css`, **construites avec les mêmes variables**.
- **Mise en page** : page unique avec défilement et sommaire latéral sticky (et non le moteur `:target` des slides, inadapté à des consignes longues).
- **En-tête** : `<header class="site-header">` identique aux slides + `.lang-toggle` + liens « ← Labs » (`index.html`) et « Site du cours » (`../index.html`).
- **Pied de page** : `Made with IBM Bob · © 2026 Olivier Planson` + commentaire de copyright en 1ʳᵉ ligne du fichier.
- **Bascule EN/FR** : clé `localStorage` `preferred-lang` (site `docs/`, défaut FR) ; écrit aussi `slide-lang` (slides). Même rendu de bouton `.lang-toggle` que les slides.
- **Mécanisme de traduction (recommandation)** : blocs jumeaux `<div lang="en">…</div><div lang="fr">…</div>` masqués par CSS selon `html[data-lang]`.
  Raison : l'approche des slides (`data-en`/`data-fr` + `innerHTML`) a produit des traductions mélangées (ex. « Write queries Utilisation de JPQL »), et impose d'échapper du HTML dans des attributs — fragile pour des blocs riches. Le **code, chemins, commandes et identifiants ne sont jamais traduits**.
- **Accessibilité / sécurité** : pas de dépendance externe, pas de `onclick` inline, pas de CDN.
- **Aucun secret réel** : les mots de passe de démo sont ceux des fichiers du lab, présentés comme tels.

## 5. Structure type d'une page (sections identiques pour les 12 labs)

Les `id` de sections sont **normalisés** pour permettre le contrôle automatique.

| # | `id` | Contenu |
|---|---|---|
| 0 | `hero` | Titre, n° de lab, durée, difficulté, badges techno, prérequis, lien vers slides du cours |
| 1 | `goal` | But pédagogique, ce qu'on construit, résultat visible final |
| 2 | `starter` | **Inventaire du starter** : tableau fichier ∣ état (✔ fourni / ✎ à compléter / ＋ à créer) |
| 3 | `target` | Arborescence cible (solution) avec fichiers nouveaux/modifiés signalés |
| 4 | `setup` | Prérequis & environnement (Java 17, Maven, Podman/Docker, BD…), commandes de démarrage |
| 5 | `steps` | **Étapes numérotées** (voir ci-dessous) |
| 6 | `run` | Build, déploiement, URLs/ports |
| 7 | `validate` | Tests `curl`/navigateur avec résultat attendu + checklist de validation |
| 8 | `compare` | Comparer son travail à `solution/` (`diff -r`), erreurs fréquentes |
| 9 | `trouble` | Dépannage |
| 10 | `bonus` | Pour aller plus loin |

**Format d'une étape** (bloc répété) :
1. *Titre + durée estimée + concept du cours visé*
2. *Fichier(s)* concernés (chemin relatif depuis la racine du projet)
3. *Consigne* — ce que doit faire l'étudiant, en termes précis (signature, annotations, comportement attendu)
4. *Indices à niveaux* : indice 1 (piste), indice 2 (API/annotation exacte), « Voir la solution » → lien vers le fichier de `solution/` (pas le code complet inline pour les classes longues)
5. *Point de contrôle* : commande + résultat attendu
6. *Règle de traçabilité* : l'étape cite les TODO (`TODO #n`) ou fichiers de diff qu'elle couvre.

**Règles de rédaction**
- Aucune étape ne suppose un fichier absent du starter sans dire de le créer.
- Chaque fichier qui diffère entre `starter` et `solution` est couvert par ≥ 1 étape.
- Les commandes/ports/identifiants viennent des fichiers du lab (compose, server.xml, bootstrap.properties), jamais de mémoire.
- Durées cohérentes avec le README du lab.

## 6. Plan d'exécution

### Phase 0 — Socle (par moi, avant tout sous-agent)
1. Créer `docs/labs/assets/` (`slide.css` copié, `lab-guide.css`, `lab-guide.js`, `LAB-GUIDE-TEMPLATE.html`) et `docs/labs/index.html`.
2. Rédiger un **glossaire de référence** commun (termes FR/EN, ex. *servlet, bean, injection, entité, migration…*) et la **liste de faits partagés** (versions Java 17, Jakarta EE 10, MicroProfile 6, Open Liberty, ports, noms de packages `com.bank.*`).
3. Écrire le **script de contrôle automatique** `tools/check-guides.py` (voir §7).
4. **Pilote** : générer le guide du **Lab 01** (le plus simple) → **revue par vous** avant de lancer les autres. Cela fige le gabarit.

### Phase 1 — Génération par sous-agents (un agent = un lab)
Chaque sous-agent reçoit : le gabarit, le glossaire, la charte, le périmètre de son lab, la sortie de `diff -r starter solution`, le README du lab, le lien slides. Il ne modifie **que** son `lab-guide.html`.

| Vague | Agents (en parallèle) | Remarque |
|---|---|---|
| **A** | Lab 02, Lab 02B, Lab 03 | starters à TODO / mixtes |
| **B** | Lab 04, Lab 04B, Lab 05B | 04 = refactor depuis solution 03 |
| **C** | Lab 05, Lab 06, Lab 07 | refactors depuis solutions N-1, gros volumes |
| **D** | Lab 08, Lab 09 | 08 très volumineux ; 09 selon décision §8 |

(Lab 01 = pilote, traité en phase 0.) Lab 08 peut être scindé en 3 sous-sections (client-service / account-service / api-gateway) dans la même page.

### Phase 2 — Agent de contrôle de cohérence (après chaque vague, puis global)
Voir §7.

### Phase 3 — Intégration
- Mise à jour de `docs/index.html` (carte « Labs » → `labs/index.html`) et de `03-Labs/index.html` (lien « Guide » par lab).
- Correction des écarts relevés par le contrôleur, puis 2ᵉ passe de contrôle.

## 7. Agent de contrôle — périmètre

**A. Contrôles automatisables (script `check-guides.py`, exécuté par l'agent)**
- Présence des 11 sections normalisées (`id`) dans chaque page, même ordre.
- **Parité EN/FR** : chaque bloc `lang="en"` a son jumeau `lang="fr"` (comptage + structure de balises identique).
- Feuille de style `slide.css` + `lab-guide.css` + script chargés ; commentaire de copyright et footer présents.
- Balises HTML équilibrées, pas de `<script>` inline externe, pas d'`onclick`.
- Tous les chemins référencés (fichiers du starter/solution, slides, liens) **existent**.
- **Publiabilité Pages** : aucun lien absolu `/…`, aucun `../` qui sort de `docs/` sauf vers `../../02-Lectures/slides/` et `../../03-Labs/` (publiés), `slide.css` identique à la source, noms en minuscules, aucun fichier ignoré (`target/`, `.war`) référencé, rendu testé via `http.server` + sous-chemin simulé.
- **Couverture** : chaque fichier de `diff -rq starter solution` et chaque `TODO` du starter est cité par une étape.

**B. Contrôles de cohérence inter-labs (revue par l'agent)**
- Continuité : le point de départ du Lab N+1 = la solution du Lab N, mêmes noms de classes/packages/tables.
- Ports, URLs, identifiants BD, noms de conteneurs/commandes identiques d'un lab à l'autre quand ils sont partagés.
- Terminologie conforme au glossaire (FR et EN).
- Niveau de détail et style des étapes homogènes (même nombre de niveaux d'indices, mêmes libellés de sections).
- Durées cumulées ≈ durée annoncée dans le README ; difficulté croissante cohérente.
- Pas de contradiction avec les slides du cours (annotations, API citées).

**C. Sortie**
Un rapport `03-Labs/LAB-GUIDES-REVIEW.md` : tableau lab × critère (OK/KO), liste des écarts avec fichier/ligne, corrections proposées. Les corrections sont ensuite appliquées par l'agent concerné (ou moi), puis re-contrôle.

## 8. Décisions (validées : « OK pour les recommandations »)

| # | Décision retenue |
|---|---|
| 1 | Lab 09 : guide « audit & durcissement » basé sur l'écart réel + avertissement dans le rapport |
| 2 | Labs 04–07 : point de départ = solution du lab précédent |
| 3 | Langue par défaut : **FR** |
| 4 | Emplacement : **`docs/labs/`** (publiable GitHub Pages) — remplace `Lab0X/lab-guide.html` |
| 5 | Indices progressifs + lien vers le fichier solution (pas de code complet inline pour les longues classes) |
| 6 | Pilote Lab 01 à valider avant les vagues A–D |
| 7 | Anomalies README/starter : chantier séparé (signalées dans le rapport de contrôle) |

## 9. Estimation

| Étape | Effort relatif |
|---|---|
| Socle + pilote Lab 01 | faible |
| Vagues A–D (11 guides) | élevé (Labs 05, 06, 07, 08 = plus gros) |
| Contrôle + corrections | moyen |
