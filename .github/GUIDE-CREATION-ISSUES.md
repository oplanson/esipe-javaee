# Guide de Création d'Issues GitHub

Ce guide vous explique comment créer et gérer les issues pour le projet Jakarta EE.

## 📋 Table des matières
1. [Créer une issue manuellement](#créer-une-issue-manuellement)
2. [Utiliser les templates](#utiliser-les-templates)
3. [Créer l'issue pour le bug de compilation](#créer-lissue-pour-le-bug-de-compilation)
4. [Bonnes pratiques](#bonnes-pratiques)

## 🚀 Créer une issue manuellement

### Via l'interface web GitHub

1. **Accéder à votre repository GitHub**
   - Allez sur `https://github.com/VOTRE-USERNAME/VOTRE-REPO`

2. **Ouvrir l'onglet Issues**
   - Cliquez sur l'onglet "Issues" en haut de la page

3. **Créer une nouvelle issue**
   - Cliquez sur le bouton vert "New issue"

4. **Choisir un template** (si disponible)
   - Sélectionnez le template approprié (Bug Report, Feature Request, Documentation)
   - Ou cliquez sur "Open a blank issue"

5. **Remplir les informations**
   - Titre
   - Description
   - Labels
   - Assignees (optionnel)
   - Projects (optionnel)
   - Milestone (optionnel)

6. **Soumettre**
   - Cliquez sur "Submit new issue"

### Via GitHub CLI (gh)

Si vous avez installé GitHub CLI :

```bash
# Créer une issue simple
gh issue create --title "Titre de l'issue" --body "Description"

# Créer une issue avec labels
gh issue create --title "Titre" --body "Description" --label "bug,Lab08"

# Créer une issue interactive
gh issue create
```

## 📝 Utiliser les templates

Les templates sont disponibles dans `.github/ISSUE_TEMPLATE/` :

1. **bug_report.md** - Pour signaler des bugs
2. **feature_request.md** - Pour proposer des fonctionnalités
3. **documentation.md** - Pour améliorer la documentation

Ces templates apparaîtront automatiquement quand vous créez une nouvelle issue sur GitHub.

## 🐛 Créer l'issue pour le bug de compilation

### Option 1 : Copier-coller depuis le fichier préparé

Le fichier [`GITHUB-ISSUE-COMPILATION-ERROR.md`](../03-Labs/Lab08-Microservices/GITHUB-ISSUE-COMPILATION-ERROR.md) contient toutes les informations nécessaires.

**Étapes :**

1. Ouvrez le fichier `03-Labs/Lab08-Microservices/GITHUB-ISSUE-COMPILATION-ERROR.md`
2. Copiez le contenu à partir de "## 📋 Titre"
3. Allez sur GitHub → Issues → New issue
4. Collez le contenu
5. Ajoutez les labels suggérés :
   - `bug`
   - `compilation-error`
   - `Lab08-Microservices`
6. Soumettez l'issue

### Option 2 : Utiliser GitHub CLI

```bash
cd esipe-javaee

# Créer l'issue avec le contenu du fichier
gh issue create \
  --title "Fix compilation errors in AccountWebController - Missing getter methods" \
  --body-file "03-Labs/Lab08-Microservices/GITHUB-ISSUE-COMPILATION-ERROR.md" \
  --label "bug,compilation-error,Lab08-Microservices"
```

### Option 3 : Format court pour création rapide

```bash
gh issue create \
  --title "[BUG] Compilation errors in AccountWebController" \
  --body "Le projet api-gateway ne compile pas. 18 erreurs liées à des appels de méthodes getter inexistantes (getClientServiceClient() et getAccountServiceClient()). 

Fichier: esipe-javaee/03-Labs/Lab08-Microservices/solution/api-gateway/src/main/java/com/bank/gateway/web/AccountWebController.java

Solution: Remplacer les appels aux getters par l'accès direct aux champs injectés par CDI.

Voir le fichier GITHUB-ISSUE-COMPILATION-ERROR.md pour les détails complets." \
  --label "bug,Lab08-Microservices"
```

## ✅ Bonnes pratiques

### Titres d'issues

**Format recommandé :**
```
[TYPE] Description courte et claire
```

**Exemples :**
- `[BUG] Compilation error in AccountWebController`
- `[FEATURE] Add Docker Compose support for Lab03`
- `[DOC] Update README with deployment instructions`

### Labels recommandés

**Par type :**
- `bug` - Problème à corriger
- `enhancement` - Amélioration
- `documentation` - Documentation
- `question` - Question
- `good first issue` - Bon pour les débutants
- `help wanted` - Aide recherchée

**Par lab :**
- `Lab01-FirstServlet`
- `Lab03-JPA`
- `Lab04-CDI`
- `Lab05-REST`
- `Lab06-DDD`
- `Lab07-Hexagonal`
- `Lab08-Microservices`
- `Lab09-Security`

**Par priorité :**
- `priority:critical` - Bloquant
- `priority:high` - Haute
- `priority:medium` - Moyenne
- `priority:low` - Basse

**Par statut :**
- `status:in-progress` - En cours
- `status:blocked` - Bloqué
- `status:needs-review` - Besoin de revue

### Description d'issue

**Structure recommandée :**

1. **Résumé** - Description courte du problème/demande
2. **Contexte** - Pourquoi c'est important
3. **Détails techniques** - Informations précises
4. **Reproduction** - Comment reproduire (pour les bugs)
5. **Solution proposée** - Si vous en avez une
6. **Références** - Liens utiles

### Assignation

- Assignez-vous si vous travaillez dessus
- Assignez quelqu'un d'autre si vous déléguez
- Laissez vide si c'est ouvert à tous

### Milestones

Créez des milestones pour organiser les issues :
- `v1.0 - Labs de base`
- `v2.0 - Labs avancés`
- `Documentation`
- `Infrastructure`

## 🔗 Liens utiles

- [Documentation GitHub Issues](https://docs.github.com/en/issues)
- [GitHub CLI Documentation](https://cli.github.com/manual/gh_issue)
- [Markdown Guide](https://guides.github.com/features/mastering-markdown/)

## 💡 Exemples d'issues complètes

### Exemple 1 : Bug Report

```markdown
## 🐛 Description
Le build Maven échoue pour le module api-gateway avec 18 erreurs de compilation.

## 📍 Localisation
- **Lab:** Lab08-Microservices
- **Module:** api-gateway
- **Fichier:** src/main/java/com/bank/gateway/web/AccountWebController.java
- **Lignes:** 143, 144, 151, 164, 165, 178, 205, 216, 231, 242, 257, 268, 269, 287, 298, 309, 320, 332

## 🔄 Reproduction
```bash
cd esipe-javaee/03-Labs/Lab08-Microservices/solution/api-gateway
mvn package
```

## ❌ Erreur
```
[ERROR] cannot find symbol
  symbol:   method getClientServiceClient()
  location: class com.bank.gateway.web.AccountWebController
```

## ✅ Solution
Remplacer `getClientServiceClient()` par `clientServiceClient` et `getAccountServiceClient()` par `accountServiceClient`.

## 💻 Environnement
- OS: macOS
- Java: OpenJDK 17
- Maven: 3.9.x
```

### Exemple 2 : Feature Request

```markdown
## 🚀 Fonctionnalité
Ajouter un support Docker Compose pour tous les labs.

## 💡 Motivation
Faciliter le déploiement et les tests pour les étudiants.

## 📝 Description
Créer des fichiers docker-compose.yml standardisés pour chaque lab incluant :
- Base de données PostgreSQL
- Application Liberty
- Configuration réseau

## 🎯 Cas d'usage
Un étudiant peut démarrer tout l'environnement avec une seule commande :
```bash
docker-compose up
```

## ✅ Critères d'acceptation
- [ ] docker-compose.yml pour chaque lab
- [ ] Documentation mise à jour
- [ ] Scripts de test fonctionnels
```

## 📞 Support

Si vous avez des questions sur la création d'issues, n'hésitez pas à :
1. Consulter la documentation GitHub
2. Créer une issue avec le label `question`
3. Contacter les mainteneurs du projet