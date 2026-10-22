<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Revue de cohérence des guides de labs (Phase 2)

Résultat de `python3 tools/check-guides.py` : **SUCCÈS**, 12 guides, 0 erreur, 0 avertissement.

## Critères contrôlés

| Critère | Résultat |
|---|---|
| 11 sections normalisées (`hero` … `bonus`) dans l'ordre | 12/12 |
| Étapes `lg-step` numérotées, avec indices 1, 2, solution et point de contrôle | 12/12 |
| Blocs jumeaux EN/FR de même structure | 12/12 |
| Ports publiés sur `127.0.0.1:` uniquement, aucun `0.0.0.0` | 12/12 (Lab 09 cite `9080:9080` seulement dans l'avertissement « ne recopiez pas ») |
| Liens GitHub vers des fichiers existants | 12/12 |
| Manifeste de couverture dans `tools/manifests/` | 12/12 |
| Guide référencé dans `docs/labs/index.html` | 12/12 |

## Durées et nombre d'étapes

| Lab | Étapes | Durée annoncée |
|---|---|---|
| 01 | 9 | 2 h |
| 02 | 15 | 4 h 10 |
| 02B | 13 | 3 h |
| 03 | 11 | 3 h |
| 04 | 15 | 4 h 30 |
| 04B | 12 | 3 h 55 |
| 05 | 10 | 3 h |
| 05B | 8 | 2 h 30 |
| 06 | 10 | 4 h |
| 07 | 14 | 4 h 30 |
| 08 | 20 | 6 h |
| 09 | 16 | 4 h 15 |

## Écarts entre solutions de référence et guides

| Lab | Écart constaté | Traitement dans le guide |
|---|---|---|
| 07 | `AccountEntity` mappe `balance` au lieu de `balance_amount` ; `Account.getClientId()` renvoie null après reconstruction ; noms de propriétés JSP différents du DTO ; un e-mail en double renvoie 500 ; plusieurs README et `SOLUTION-STATUS.md` inexacts | Documentés, l'étudiant les corrige |
| 08 | Le `docker-compose.yml` de la solution ne démarre que les bases | Le guide démarre les services avec `podman run` |
| 09 | `createUser` avec `UserTransaction` provoque un 500 à l'inscription sous Liberty | Le guide garde `@Transactional` sur `createUser` et `unlockAccount` et applique `UserTransaction` aux deux méthodes de verrouillage seulement (testé : 201, puis 401×4 puis 403) |
| 09 | `allowCredentials="true"` dans `server.xml` de la solution : l'en-tête reste envoyé | Le guide met `false` (testé : 0 en-tête) |
| 09 | `README-STARTER.md` annonce une implémentation « complète » ; dossier `starter/solution/` périmé | Avertissement dans la section `starter` |
| 09 | Les ports de la solution sont publiés sur toutes les interfaces | Le guide impose `127.0.0.1:` |
| 09 | `countRecentFailedLogins` n'est appelée nulle part | Le correctif est vérifié à la compilation ; un exercice « Pour aller plus loin » l'appelle |
| 09 | `podman-test.sh` exécute `podman image prune -f` et `podman volume prune -f` | Avertissement dans la section `run` |

## Cohérence entre labs

- Chaîne de départ 04 → 05 → 06 → 07 → 08 : chaque guide part de la solution du lab précédent, comme le prévoit le plan.
- Ports : Labs 08 (9080/9081/9082, 5433/5434) et 09 (9080/9443/5432) utilisent des ports qui se recouvrent. Le dépannage de chacun demande d'arrêter la stack précédente, en ne touchant que ses propres conteneurs.
- Lab 09 est le dernier lab : son encart final renvoie vers `index.html` et `../index.html`.

## Limites connues

- Les tests de comportement ont été faits sur Podman pour le Lab 09 (étapes 9 à 16) et pour les labs précédents selon les rapports de chaque agent. Les durées sont des estimations.
- Lab 09 : l'origine CORS reste `*` à la fin du lab (exercice bonus).
- Le jeton JWT n'est pas révoqué à la déconnexion (exercice bonus).
