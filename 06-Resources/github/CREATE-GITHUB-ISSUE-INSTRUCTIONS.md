# Instructions pour Créer l'Issue GitHub

## 🔒 Issue de Sécurité: JMS ObjectMessage Deserialization Vulnerability

### Étape 1: Accéder à GitHub Issues

Ouvrez votre navigateur et allez sur:
```
https://github.com/oplanson/esipe-javaee/issues/new
```

### Étape 2: Titre de l'Issue

Copiez-collez ce titre:
```
🔒 [SECURITY] Fix JMS ObjectMessage Deserialization Vulnerability in Lab05B
```

### Étape 3: Corps de l'Issue

Copiez le contenu complet du fichier:
```
esipe-javaee/06-Resources/github/GITHUB-ISSUE-JMS-DESERIALIZATION-FIX.md
```

**OU** utilisez ce lien direct pour voir le contenu:
```
https://github.com/oplanson/esipe-javaee/blob/master/06-Resources/github/GITHUB-ISSUE-JMS-DESERIALIZATION-FIX.md
```

### Étape 4: Ajouter les Labels

Cliquez sur "Labels" et sélectionnez:
- `security` (créez-le s'il n'existe pas - couleur rouge #d73a4a)
- `critical` (créez-le s'il n'existe pas - couleur rouge foncé #b60205)
- `bug` (devrait déjà exister)
- `enhancement` (devrait déjà exister)

### Étape 5: Assigner

- **Assignees:** @oplanson (vous-même)

### Étape 6: Milestone (Optionnel)

Si vous avez des milestones, créez ou sélectionnez:
- `Lab05B-JMS Security Hardening`

### Étape 7: Créer l'Issue

Cliquez sur **"Submit new issue"**

---

## 📋 Référence Rapide du Commit

L'issue fait référence au commit:
```
Commit: 16e1373
URL: https://github.com/oplanson/esipe-javaee/commit/16e1373
```

---

## 🔗 Liens Utiles

- **Repository:** https://github.com/oplanson/esipe-javaee
- **Documentation:** `03-Labs/Lab05B-JMS/solution/SECURITY-FIX-JMS-DESERIALIZATION.md`
- **Template d'issue:** `06-Resources/github/GITHUB-ISSUE-JMS-DESERIALIZATION-FIX.md`

---

## ✅ Après Création de l'Issue

1. **Fermer l'issue immédiatement** car le fix est déjà implémenté
2. Dans le commentaire de fermeture, ajouter:
   ```
   Fixed in commit 16e1373
   
   Security fix implemented:
   - Replaced ObjectMessage with TextMessage + JSON
   - Eliminated Java deserialization vulnerability
   - All tests passing
   
   See SECURITY-FIX-JMS-DESERIALIZATION.md for complete documentation.
   ```

3. **Lier le commit à l'issue:**
   - Allez sur le commit: https://github.com/oplanson/esipe-javaee/commit/16e1373
   - Ajoutez un commentaire mentionnant l'issue: `Fixes #[ISSUE_NUMBER]`

---

## 🎯 Résultat Attendu

Une fois l'issue créée et fermée, vous aurez:
- ✅ Traçabilité complète de la vulnérabilité
- ✅ Documentation de la correction
- ✅ Historique de sécurité dans GitHub
- ✅ Référence pour audits futurs

---

**Note:** Si vous préférez créer l'issue via CLI, utilisez GitHub CLI:

```bash
gh issue create \
  --title "🔒 [SECURITY] Fix JMS ObjectMessage Deserialization Vulnerability in Lab05B" \
  --body-file esipe-javaee/06-Resources/github/GITHUB-ISSUE-JMS-DESERIALIZATION-FIX.md \
  --label security,critical,bug,enhancement \
  --assignee @me

# Puis fermez-la immédiatement
gh issue close [ISSUE_NUMBER] --comment "Fixed in commit 16e1373"