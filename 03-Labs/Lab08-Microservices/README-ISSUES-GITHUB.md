<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Résumé des Issues GitHub pour Lab08-Microservices

Ce document récapitule les trois bugs identifiés et corrigés dans le Lab08-Microservices, avec les informations nécessaires pour créer les issues correspondantes sur GitHub.

## 📊 Vue d'ensemble

| # | Titre | Type | Priorité | Statut | Fichier |
|---|-------|------|----------|--------|---------|
| 1 | Compilation errors in AccountWebController | Bug | Haute | ✅ Corrigé | AccountWebController.java |
| 2 | ClientWebController returns 404 | Bug | Haute | ✅ Corrigé | ClientWebController.java |
| 3 | ClientWebController returns empty list | Bug | Haute | ✅ Corrigé | ClientWebController.java + JSPs |

## 🐛 Issue #1 : Erreurs de compilation

### Résumé rapide
18 erreurs de compilation dans `AccountWebController` dues à des appels à des méthodes getter inexistantes.

### Fichiers de référence
- **Description complète** : [`GITHUB-ISSUE-COMPILATION-ERROR.md`](./GITHUB-ISSUE-COMPILATION-ERROR.md)
- **Fichier corrigé** : `solution/api-gateway/src/main/java/com/bank/gateway/web/AccountWebController.java`

### Informations pour GitHub

**Titre :**
```
[BUG] Compilation errors in AccountWebController - Missing getter methods
```

**Labels :**
- `bug`
- `compilation`
- `Lab08-Microservices`
- `good first issue`

**Assignee :** (optionnel)

**Milestone :** `Lab08-Microservices` ou `v1.0`

**Description courte pour création rapide :**
```markdown
## Problème
Maven build échoue avec 18 erreurs de compilation dans `AccountWebController.java`.

## Cause
Le code appelle `getClientServiceClient()` et `getAccountServiceClient()` qui n'existent pas.

## Solution
Remplacer tous les appels aux getters par l'accès direct aux champs injectés par CDI :
- `getClientServiceClient()` → `clientServiceClient`
- `getAccountServiceClient()` → `accountServiceClient`

## Fichiers
- `AccountWebController.java` - 18 lignes modifiées

Voir [GITHUB-ISSUE-COMPILATION-ERROR.md](./GITHUB-ISSUE-COMPILATION-ERROR.md) pour les détails complets.
```

### Commande GitHub CLI
```bash
cd esipe-javaee/03-Labs/Lab08-Microservices

gh issue create \
  --title "[BUG] Compilation errors in AccountWebController - Missing getter methods" \
  --body-file "GITHUB-ISSUE-COMPILATION-ERROR.md" \
  --label "bug,compilation,Lab08-Microservices,good first issue"
```

---

## 🐛 Issue #2 : Erreur 404 sur /web/clients

### Résumé rapide
L'URL `/web/clients` retourne une erreur 404 car `ClientWebController` n'a pas d'annotation `@WebServlet`.

### Fichiers de référence
- **Description complète** : [`GITHUB-ISSUE-MISSING-WEBSERVLET.md`](./GITHUB-ISSUE-MISSING-WEBSERVLET.md)
- **Fichier corrigé** : `solution/api-gateway/src/main/java/com/bank/gateway/web/ClientWebController.java`

### Informations pour GitHub

**Titre :**
```
[BUG] ClientWebController returns 404 - Missing @WebServlet annotation
```

**Labels :**
- `bug`
- `configuration`
- `Lab08-Microservices`
- `good first issue`

**Assignee :** (optionnel)

**Milestone :** `Lab08-Microservices` ou `v1.0`

**Description courte pour création rapide :**
```markdown
## Problème
L'URL `/web/clients` retourne une erreur 404 alors que le contrôleur existe.

## Cause
`ClientWebController` n'a pas d'annotation `@WebServlet` pour mapper l'URL.

## Solution
Ajouter l'annotation :
```java
@WebServlet(urlPatterns = {"/web/clients", "/web/clients/*"})
```

## Fichiers
- `ClientWebController.java` - 1 annotation ajoutée

Voir [GITHUB-ISSUE-MISSING-WEBSERVLET.md](./GITHUB-ISSUE-MISSING-WEBSERVLET.md) pour les détails complets.
```

### Commande GitHub CLI
```bash
cd esipe-javaee/03-Labs/Lab08-Microservices

gh issue create \
  --title "[BUG] ClientWebController returns 404 - Missing @WebServlet annotation" \
  --body-file "GITHUB-ISSUE-MISSING-WEBSERVLET.md" \
  --label "bug,configuration,Lab08-Microservices,good first issue"
```

---

## 🐛 Issue #3 : Liste de clients vide (CDI manquant)

### Résumé rapide
La page `/web/clients` s'affiche mais la liste est vide car `ClientWebController` n'utilise pas l'injection CDI.

### Fichiers de référence
- **Description complète** : [`GITHUB-ISSUE-CDI-INJECTION.md`](./GITHUB-ISSUE-CDI-INJECTION.md)
- **Fichiers corrigés** : 
  - `solution/api-gateway/src/main/java/com/bank/gateway/web/ClientWebController.java`
  - `solution/api-gateway/src/main/webapp/WEB-INF/views/client-list.jsp`
  - `solution/api-gateway/src/main/webapp/WEB-INF/views/client-details.jsp`
  - `solution/api-gateway/src/main/webapp/WEB-INF/views/client-form.jsp`

### Informations pour GitHub

**Titre :**
```
[BUG] ClientWebController returns empty client list - Missing CDI injection
```

**Labels :**
- `bug`
- `cdi`
- `Lab08-Microservices`
- `high-priority`

**Assignee :** (optionnel)

**Milestone :** `Lab08-Microservices` ou `v1.0`

**Description courte pour création rapide :**
```markdown
## Problème
La page `/web/clients` s'affiche mais la liste est vide, alors que le service client contient des données.

## Cause
`ClientWebController` crée manuellement les clients REST dans `init()` au lieu d'utiliser l'injection CDI, ce qui empêche le bon fonctionnement de `BankingAggregationService`.

## Solution
- Remplacer la création manuelle par `@Inject` et `@RestClient`
- Supprimer la méthode `init()` et les getters
- Corriger les URLs dans les JSP et les redirections
- Corriger les paths dans le switch POST

## Fichiers
- `ClientWebController.java` - Injection CDI
- `client-list.jsp`, `client-details.jsp`, `client-form.jsp` - URLs corrigées

Voir [GITHUB-ISSUE-CDI-INJECTION.md](./GITHUB-ISSUE-CDI-INJECTION.md) pour les détails complets.
```

### Commande GitHub CLI
```bash
cd esipe-javaee/03-Labs/Lab08-Microservices

gh issue create \
  --title "[BUG] ClientWebController returns empty client list - Missing CDI injection" \
  --body-file "GITHUB-ISSUE-CDI-INJECTION.md" \
  --label "bug,cdi,Lab08-Microservices,high-priority"
```

---

## 🚀 Création des issues - Guide pas à pas

### Option 1 : Via l'interface web GitHub

1. **Accédez à votre repository GitHub**
   - Allez sur la page de votre projet
   - Cliquez sur l'onglet "Issues"

2. **Créez une nouvelle issue**
   - Cliquez sur "New issue"
   
3. **Remplissez les informations**
   a. **Titre** : Copiez le titre depuis ce document (voir section "Titre" de chaque issue)
   
   b. **Labels** : Ajoutez les labels appropriés
      - Issue #1 : `bug`, `compilation`, `Lab08-Microservices`, `good first issue`
      - Issue #2 : `bug`, `configuration`, `Lab08-Microservices`, `good first issue`
      - Issue #3 : `bug`, `cdi`, `Lab08-Microservices`, `high-priority`
   
   c. **Assignee** : Assignez-vous ou laissez vide
   
   d. **Copiez-collez** le contenu du fichier markdown correspondant :
      - Issue #1 : Contenu de `GITHUB-ISSUE-COMPILATION-ERROR.md`
      - Issue #2 : Contenu de `GITHUB-ISSUE-MISSING-WEBSERVLET.md`
      - Issue #3 : Contenu de `GITHUB-ISSUE-CDI-INJECTION.md`
   
   e. **Milestone** : Sélectionnez `Lab08-Microservices` ou `v1.0` si disponible

4. **Créez l'issue**
   - Cliquez sur "Submit new issue"

5. **Répétez** pour les autres issues

### Option 2 : Via GitHub CLI (plus rapide)

Si vous avez installé [GitHub CLI](https://cli.github.com/), vous pouvez créer les trois issues en une seule commande :

```bash
cd esipe-javaee/03-Labs/Lab08-Microservices

# Créer l'issue #1 (Compilation errors)
gh issue create \
  --title "[BUG] Compilation errors in AccountWebController - Missing getter methods" \
  --body-file "GITHUB-ISSUE-COMPILATION-ERROR.md" \
  --label "bug,compilation,Lab08-Microservices,good first issue"

# Créer l'issue #2 (404 error)
gh issue create \
  --title "[BUG] ClientWebController returns 404 - Missing @WebServlet annotation" \
  --body-file "GITHUB-ISSUE-MISSING-WEBSERVLET.md" \
  --label "bug,configuration,Lab08-Microservices,good first issue"

# Créer l'issue #3 (Empty list - CDI)
gh issue create \
  --title "[BUG] ClientWebController returns empty client list - Missing CDI injection" \
  --body-file "GITHUB-ISSUE-CDI-INJECTION.md" \
  --label "bug,cdi,Lab08-Microservices,high-priority"
```

---

## ✅ Checklist de création

- [ ] Issue #1 créée sur GitHub
  - [ ] Titre correct
  - [ ] Labels ajoutés
  - [ ] Description complète
  - [ ] Milestone assigné (optionnel)

- [ ] Issue #2 créée sur GitHub
  - [ ] Titre correct
  - [ ] Labels ajoutés
  - [ ] Description complète
  - [ ] Milestone assigné (optionnel)

- [ ] Issue #3 créée sur GitHub
  - [ ] Titre correct
  - [ ] Labels ajoutés
  - [ ] Description complète
  - [ ] Milestone assigné (optionnel)

- [ ] Les trois issues sont liées entre elles (mentionner les unes dans les autres)

---

## 🔗 Liens entre les issues

Ces trois issues sont liées car elles concernent le même lab et ont été découvertes/corrigées ensemble. Vous pouvez les lier en ajoutant dans chaque issue :

**Dans l'issue #1 :**
```markdown
## Issues liées
- Voir aussi : #[numéro de l'issue #2] - ClientWebController returns 404
- Voir aussi : #[numéro de l'issue #3] - ClientWebController returns empty list
```

**Dans l'issue #2 :**
```markdown
## Issues liées
- Voir aussi : #[numéro de l'issue #1] - Compilation errors in AccountWebController
- Voir aussi : #[numéro de l'issue #3] - ClientWebController returns empty list
```

**Dans l'issue #3 :**
```markdown
## Issues liées
- Voir aussi : #[numéro de l'issue #1] - Compilation errors in AccountWebController
- Voir aussi : #[numéro de l'issue #2] - ClientWebController returns 404
```

---

## 📈 Statistiques des corrections

**Issue #1 - AccountWebController :**
- 18 lignes modifiées
- 18 appels de méthodes remplacés
- Temps de correction : ~5 minutes

**Issue #2 - ClientWebController :**
- 1 annotation ajoutée
- 1 import ajouté
- 1 commentaire mis à jour
- Temps de correction : ~2 minutes

**Issue #3 - ClientWebController + JSPs :**
- Suppression de la méthode `init()` (70 lignes)
- Ajout de 2 annotations `@Inject`
- Remplacement de 7 appels aux getters
- Correction de 15+ URLs dans 3 fichiers JSP
- Temps de correction : ~10 minutes

**Total :**
- 3 bugs majeurs corrigés
- 5 fichiers modifiés
- ~17 minutes de correction
- Build Maven : ✅ SUCCESS

---

## 📝 Notes importantes

1. **Ordre de correction** : Les trois bugs ont été découverts et corrigés dans l'ordre :
   - Bug #1 (compilation) → empêchait le build
   - Bug #2 (404) → découvert après le build réussi
   - Bug #3 (liste vide) → découvert après avoir résolu le 404

2. **Impact** : Ces bugs empêchaient complètement l'utilisation du module api-gateway

3. **Leçons apprises** :
   - Toujours utiliser l'injection CDI plutôt que la création manuelle
   - Vérifier que les annotations de mapping sont présentes
   - Tester les URLs après chaque modification

4. **Tests recommandés** après correction :
   ```bash
   # Build
   mvn clean package
   
   # Démarrer les services
   docker-compose up -d
   
   # Tester les URLs
   curl http://localhost:9080/web/clients
   curl http://localhost:9080/web/accounts
   ```

---

## 🎯 Prochaines étapes

- [x] Bugs identifiés (3 bugs majeurs)
- [x] Corrections appliquées (tous les fichiers)
- [x] Build vérifié (Maven BUILD SUCCESS)
- [x] Documentation créée (3 fichiers d'issues + guide)
- [ ] Issues créées sur GitHub
- [ ] Issues fermées avec référence aux commits
- [ ] Tests fonctionnels validés

---

## 📚 Ressources

- [Documentation Jakarta EE CDI](https://jakarta.ee/specifications/cdi/)
- [MicroProfile Rest Client](https://microprofile.io/project/eclipse/microprofile-rest-client)
- [Jakarta Servlet Specification](https://jakarta.ee/specifications/servlet/)
- [GitHub Issues Documentation](https://docs.github.com/en/issues)
- [GitHub CLI Documentation](https://cli.github.com/manual/)