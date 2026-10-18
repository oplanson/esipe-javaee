<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Faits partagés et glossaire — guides de labs

Document de référence pour les sous-agents et l'agent de contrôle. **Gabarit de référence : `docs/labs/lab01-first-servlet.html`** (pilote validé par `tools/check-guides.py`). Toute page doit reprendre sa structure, ses classes CSS et son niveau de détail.

## 1. Faits communs (vérifiés dans les `solution/`)

| Sujet | Valeur |
|---|---|
| Java | 17 pour les labs 01–08 ; **21 pour le lab 09** (`full-java21-openj9-ubi-minimal`) |
| Plateforme | Jakarta EE 10, MicroProfile 6, Open Liberty |
| Image de base | `icr.io/appcafe/open-liberty:full-java17-openj9-ubi` |
| Groupe Maven / package racine | `com.bank` / `com.bank.*` |
| Ports applicatifs | HTTP **9080**, HTTPS **9443** (`http.port` / `https.port` de `bootstrap.properties`) |
| Lab 08 | api-gateway 9080, client-service 9081, account-service 9082 ; bases PostgreSQL sur **5433** (client) et **5434** (account) |
| PostgreSQL (labs 03–07, 09) | port 5432, utilisateur `bankuser`, base `bankdb` ; **labs 04B et 05B : base `bankingdb`** ; lab 08 : `banking_client_db`, `banking_account_db` |
| Conteneurs de base | `banking-db` (03, 04, 05), `lab04b-postgres`, `banking-jms-db`, `lab06-postgres`, `lab07-postgres`, `banking-client-db`, `banking-account-db`, `bank-security-db` |
| Artifact / WAR par lab | 01 `banking-app` · 02 `banking-web-app` · 03 `banking-jpa-app` · 04 `banking-cdi-app` · 04B `banking-ejb-app` · 05 `banking-rest-app` · 05B `banking-jms-app` · 06 `banking-ddd-app` · 07 `banking-hexagonal-app` · 08 `client-service`, `account-service`, `api-gateway` · 09 `bank-security` (WAR `banking-security-app`) |
| Gestionnaire de conteneurs | Podman (les commandes Docker sont identiques) |
| Version Maven | 3.x, JDK 17+ |

**Les valeurs ci-dessus sont un point de départ : l'agent de chaque lab doit les revérifier dans ses propres fichiers (`pom.xml`, `server.xml`, `bootstrap.properties`, compose, `README.md`) et signaler tout écart dans son compte rendu.**

## 2. Règles de sécurité à appliquer dans les guides

- Toute commande `podman run` / `docker run` publie en **`127.0.0.1:HOST:CONT`** (ex. `-p 127.0.0.1:9080:9080`), jamais `-p 9080:9080`.
- Les fichiers compose des labs publient `5432:5432` / `9080:9080` sur toutes les interfaces : le guide **ne recopie pas** cette forme ; il écrit `127.0.0.1:` devant, ou signale l'écart dans un encadré `lg-callout--warn`.
- Aucun secret réel. Les identifiants de démonstration des fichiers du lab sont présentés **comme tels** (« identifiants de démo, jamais en production ») et le guide recommande de ne pas les réutiliser.
- Aucun `0.0.0.0`, aucun lien `http://` hors `localhost` / `127.0.0.1`.
- Ne jamais recommander de paquet EOL ; ne pas modifier les versions des `pom.xml` du starter.

## 3. Scripts fournis par les labs

Les `podman-test.sh` / `docker-test.sh` / `run-lab.sh` / `test-lab.sh` **arrêtent et suppriment tout conteneur qui utilise le port cible**. Chaque guide qui les mentionne ajoute l'encadré d'avertissement du Lab 01 (section `run`). Le guide ne dépend jamais d'eux : il donne les commandes explicites.

## 4. Règles de rédaction

1. Source de vérité = `diff -r starter solution` et le contenu réel des fichiers, pas le `README.md` (qualité variable).
2. Chaque fichier qui diffère entre `starter` et `solution`, et chaque `TODO` du starter, est cité dans au moins une étape (le manifeste `tools/manifests/<guide>.json` le vérifie).
3. Aucune étape ne suppose un fichier absent du starter sans dire de le créer.
4. Chaque étape : titre, durée, concept + lien slide, fichiers, consigne, `lg-hint--1`, `lg-hint--2`, lien « solution » (`lg-hint--sol`) vers l'URL GitHub blob, `lg-checkpoint` avec commande **et** résultat attendu.
5. Pas de code complet inline pour une classe de plus de ~40 lignes : indice 2 = signatures, annotations, API exactes ; le reste = lien vers la solution.
6. Les commandes proposées doivent avoir été **exécutées ou vérifiées** (au moins `mvn -q -DskipTests package` sur starter + fichiers solution copiés dans un dossier temporaire) ; noter dans le compte rendu ce qui n'a pas pu être exécuté.
7. Liens de navigation relatifs depuis `docs/labs/` : cours `../0X-xxx/index.html`, slides `../../02-Lectures/slides/0X-xxx/slides.html#slide-N`. Vérifier que l'ancre `slide-N` existe.
8. Durées : somme des étapes ≈ durée annoncée dans le hero ≈ durée du README ; si le README est incohérent, retenir la somme des étapes et signaler l'écart.
9. Le point de départ des labs 04, 05, 06, 07 est **la solution du lab précédent** ; le guide le dit en section `starter` et en `setup`, avec la commande exacte de copie.
10. Chaque guide se termine par un encadré « Suite » vers le lab suivant (identique à celui du Lab 01 en forme).
11. Un lab dont le starter est incomplet ou contradictoire avec son README → encadré `lg-callout--warn` dans la section `starter` + compte rendu.

## 5. Glossaire FR / EN (à respecter dans les deux langues)

| EN | FR | Remarque |
|---|---|---|
| servlet | servlet (f.) | invariable |
| starter | starter | jamais « démarreur » |
| solution | solution | |
| lab | lab (m.) | « TP » interdit pour rester homogène |
| step | étape | |
| hint | indice | |
| checkpoint | point de contrôle | |
| deploy / redeploy | déployer / redéployer | |
| container image | image de conteneur | |
| bean | bean (m.) | |
| dependency injection | injection de dépendances | |
| scope | portée (en prose) ; `scope` dans les noms Maven/CDI | |
| entity | entité | JPA |
| migration | migration | Flyway |
| transaction | transaction | |
| repository | repository | pas « dépôt » (ambigu avec Git) |
| endpoint | endpoint | |
| query | requête | |
| request / response | requête / réponse | HTTP |
| value object | objet-valeur | DDD |
| aggregate | agrégat | DDD |
| bounded context | bounded context | non traduit |
| port / adapter | port / adaptateur | hexagonal |
| fault tolerance | tolérance aux pannes | |
| gateway | API gateway | non traduit |
| audit trail | piste d'audit | |
| hardening | durcissement | |
| thread-safe | thread-safe | non traduit |

Le code, les chemins, les commandes, les noms de classes, d'annotations et de ports ne sont **jamais** traduits.
