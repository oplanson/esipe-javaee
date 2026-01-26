<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Guide de Publication des Features GitHub

Ce document récapitule les features à publier sur GitHub pour améliorer le projet de cours Jakarta EE.

## 📋 Liste des Features

| # | Titre | Priorité | Effort | Statut |
|---|-------|----------|--------|--------|
| 1 | Amélioration des projets starter | 🔴 Haute | 40-60h | 📝 Documenté |
| 2 | [À définir] | - | - | ⏳ En attente |

---

## 🎯 Feature #1 : Amélioration des projets starter

### Résumé
Restructurer tous les projets `starter/` des labs pour qu'ils soient directement utilisables par les étudiants avec une structure complète et des instructions claires sous forme de TODOs et commentaires.

### Fichier de référence
[`GITHUB-FEATURE-STARTER-IMPROVEMENT.md`](./GITHUB-FEATURE-STARTER-IMPROVEMENT.md)

### Informations pour GitHub

**Titre :**
```
[FEATURE] Amélioration des projets starter pour les étudiants
```

**Labels :**
- `enhancement`
- `documentation`
- `good first issue` (pour certaines parties)
- `help wanted`
- `pedagogy`

**Milestone :** `v2.0` ou `Amélioration pédagogique`

**Assignees :** (à définir selon l'équipe)

### Description courte pour GitHub

```markdown
## 🎯 Objectif

Améliorer l'expérience d'apprentissage en restructurant tous les projets starter des labs pour qu'ils soient :
- ✅ Directement utilisables
- ✅ Avec structure complète
- ✅ Avec TODOs numérotés et progressifs
- ✅ Avec commentaires explicatifs détaillés
- ✅ Compilables dès le départ

## 📦 Labs concernés

- [ ] Lab01-FirstServlet
- [ ] Lab02-ServletsJSP
- [ ] Lab03-JPA ⭐ Prioritaire
- [ ] Lab04-CDI
- [ ] Lab04B-EJB
- [ ] Lab05-REST ⭐ Prioritaire
- [ ] Lab05B-JMS
- [ ] Lab06-DDD
- [ ] Lab07-Hexagonal
- [ ] Lab08-Microservices ⭐ Prioritaire
- [ ] Lab09-Security

## 💡 Exemple de transformation

### Avant
```java
public class ClientService {
    // Classe vide - les étudiants ne savent pas quoi faire
}
```

### Après
```java
/**
 * Service de gestion des clients avec JPA.
 * 
 * Objectifs pédagogiques:
 * - Comprendre l'injection CDI
 * - Maîtriser les opérations CRUD avec JPA
 */
@ApplicationScoped
public class ClientService {

    // TODO 1: Injecter l'EntityManager
    // Utilisez @Inject pour l'injection
    // private EntityManager em;

    public Client createClient(Client client) {
        // TODO 2: Implémenter la création
        // 1. Valider que client != null
        // 2. Utiliser em.persist(client)
        // 3. Retourner le client
        
        throw new UnsupportedOperationException("TODO: Implémenter createClient()");
    }
}
```

## 📋 Plan de déploiement

### Phase 1 : Labs prioritaires (Semaine 1-2)
- Lab03-JPA
- Lab05-REST
- Lab08-Microservices

### Phase 2 : Labs intermédiaires (Semaine 3-4)
- Lab04-CDI
- Lab06-DDD
- Lab07-Hexagonal

### Phase 3 : Labs complémentaires (Semaine 5-6)
- Tous les autres labs

### Phase 4 : Validation (Semaine 7)
- Tests avec étudiants
- Ajustements
- Documentation finale

## 🎨 Template de classe starter

Chaque classe starter doit inclure :
- ✅ Documentation Javadoc complète
- ✅ TODOs numérotés et progressifs
- ✅ Commentaires explicatifs détaillés
- ✅ Imports nécessaires
- ✅ Annotations de base
- ✅ Méthodes avec signatures correctes
- ✅ Implémentations par défaut (throw UnsupportedOperationException)

## 📊 Métriques de succès

- 100% des labs ont un starter fonctionnel
- Tous les starters compilent sans erreur
- Moyenne de 10-15 TODOs par lab
- Temps de setup < 5 minutes par lab
- Feedback positif des étudiants

## 🔗 Documentation complète

Voir [`GITHUB-FEATURE-STARTER-IMPROVEMENT.md`](./GITHUB-FEATURE-STARTER-IMPROVEMENT.md) pour :
- Description détaillée du problème
- Solution complète avec exemples
- Checklist de création
- Scripts d'automatisation
- Timeline et ressources

## 👥 Contribution

Cette feature est ouverte aux contributions ! Vous pouvez :
- Prendre en charge un lab spécifique
- Créer des templates de classes
- Développer les scripts d'automatisation
- Tester avec des étudiants
- Améliorer la documentation

## 💬 Discussion

Vos retours sont les bienvenus ! N'hésitez pas à :
- Commenter cette issue
- Proposer des améliorations
- Partager votre expérience
- Suggérer d'autres labs à améliorer
```

### Commande GitHub CLI

```bash
cd esipe-javaee/03-Labs

gh issue create \
  --title "[FEATURE] Amélioration des projets starter pour les étudiants" \
  --body-file "GITHUB-FEATURE-STARTER-IMPROVEMENT.md" \
  --label "enhancement,documentation,help wanted,pedagogy" \
  --milestone "v2.0"
```

### Création de sous-issues (optionnel)

Pour faciliter la contribution, vous pouvez créer une issue par lab :

```bash
# Lab03-JPA
gh issue create \
  --title "[Lab03-JPA] Créer le projet starter avec TODOs" \
  --body "Partie de #[numéro de la feature principale]" \
  --label "enhancement,Lab03-JPA,good first issue" \
  --milestone "v2.0"

# Lab05-REST
gh issue create \
  --title "[Lab05-REST] Créer le projet starter avec TODOs" \
  --body "Partie de #[numéro de la feature principale]" \
  --label "enhancement,Lab05-REST,good first issue" \
  --milestone "v2.0"

# Lab08-Microservices
gh issue create \
  --title "[Lab08-Microservices] Créer le projet starter avec TODOs" \
  --body "Partie de #[numéro de la feature principale]" \
  --label "enhancement,Lab08-Microservices" \
  --milestone "v2.0"
```

---

## 🚀 Processus de création sur GitHub

### Étape 1 : Créer la feature principale

1. Allez sur GitHub → Issues → New issue
2. Titre : `[FEATURE] Amélioration des projets starter pour les étudiants`
3. Copiez le contenu de `GITHUB-FEATURE-STARTER-IMPROVEMENT.md`
4. Ajoutez les labels : `enhancement`, `documentation`, `help wanted`, `pedagogy`
5. Créez un milestone `v2.0` ou `Amélioration pédagogique`
6. Assignez si nécessaire
7. Créez l'issue

### Étape 2 : Créer un Project Board (optionnel mais recommandé)

1. Allez sur Projects → New project
2. Nom : "Amélioration des Starters"
3. Template : "Board"
4. Créez les colonnes :
   - 📋 To Do
   - 🏗️ In Progress
   - 👀 Review
   - ✅ Done

5. Ajoutez la feature principale au board
6. Créez les sous-issues pour chaque lab
7. Organisez-les dans les colonnes

### Étape 3 : Créer les sous-issues par lab

Pour chaque lab prioritaire, créez une issue :

**Template de sous-issue :**
```markdown
## Lab concerné
[Nom du lab]

## Objectif
Créer un projet starter complet avec TODOs et commentaires pour ce lab.

## Checklist
- [ ] Copier la structure de solution/
- [ ] Transformer les classes en ajoutant TODOs
- [ ] Ajouter la documentation Javadoc
- [ ] Vérifier que le projet compile
- [ ] Tester avec un étudiant
- [ ] Créer la PR

## Référence
Partie de #[numéro de la feature principale]

## Ressources
- [GITHUB-FEATURE-STARTER-IMPROVEMENT.md](./GITHUB-FEATURE-STARTER-IMPROVEMENT.md)
- Solution existante : `esipe-javaee/03-Labs/[Lab]/solution/`
```

### Étape 4 : Organiser les contributions

1. **Assignez les issues** aux contributeurs
2. **Définissez les priorités** (labels : `priority-high`, `priority-medium`, `priority-low`)
3. **Créez des branches** pour chaque lab : `feature/starter-lab03-jpa`
4. **Configurez les PR templates** pour standardiser les contributions

---

## 📝 Template de Pull Request

Créez `.github/PULL_REQUEST_TEMPLATE.md` :

```markdown
## Description
[Décrivez les changements apportés]

## Lab concerné
- [ ] Lab01-FirstServlet
- [ ] Lab02-ServletsJSP
- [ ] Lab03-JPA
- [ ] Lab04-CDI
- [ ] Lab04B-EJB
- [ ] Lab05-REST
- [ ] Lab05B-JMS
- [ ] Lab06-DDD
- [ ] Lab07-Hexagonal
- [ ] Lab08-Microservices
- [ ] Lab09-Security

## Type de changement
- [ ] Nouveau starter
- [ ] Amélioration de starter existant
- [ ] Documentation
- [ ] Scripts d'automatisation

## Checklist
- [ ] Le projet compile sans erreur
- [ ] Tous les TODOs sont numérotés
- [ ] La documentation Javadoc est complète
- [ ] Les commentaires sont clairs et détaillés
- [ ] La structure correspond à la solution
- [ ] Les imports sont corrects
- [ ] Les annotations de base sont présentes
- [ ] Testé avec un étudiant (si possible)

## Screenshots (si applicable)
[Ajoutez des captures d'écran]

## Issues liées
Closes #[numéro de l'issue]
Part of #[numéro de la feature principale]

## Notes additionnelles
[Informations supplémentaires]
```

---

## 🏷️ Labels recommandés

Créez ces labels sur GitHub :

### Par type
- `enhancement` - Amélioration
- `bug` - Bug
- `documentation` - Documentation
- `feature` - Nouvelle fonctionnalité

### Par priorité
- `priority-high` - Priorité haute
- `priority-medium` - Priorité moyenne
- `priority-low` - Priorité basse

### Par lab
- `Lab01-FirstServlet`
- `Lab02-ServletsJSP`
- `Lab03-JPA`
- `Lab04-CDI`
- `Lab04B-EJB`
- `Lab05-REST`
- `Lab05B-JMS`
- `Lab06-DDD`
- `Lab07-Hexagonal`
- `Lab08-Microservices`
- `Lab09-Security`

### Par statut
- `good first issue` - Bon pour débuter
- `help wanted` - Aide souhaitée
- `in progress` - En cours
- `needs review` - Besoin de revue

### Par domaine
- `pedagogy` - Pédagogie
- `compilation` - Compilation
- `configuration` - Configuration
- `cdi` - CDI
- `jpa` - JPA
- `rest` - REST
- `microservices` - Microservices

---

## 📊 Suivi de progression

### Dashboard GitHub Projects

Créez des vues personnalisées :

1. **Vue par priorité**
   - Grouper par : Priority
   - Trier par : Created date

2. **Vue par lab**
   - Grouper par : Lab
   - Trier par : Status

3. **Vue par assignee**
   - Grouper par : Assignee
   - Trier par : Due date

### Métriques à suivre

- Nombre de starters complétés / total
- Temps moyen par starter
- Nombre de TODOs par starter
- Feedback des étudiants (score sur 5)
- Taux de compilation réussie

---

## ✅ Checklist finale

### Avant de publier
- [ ] Feature documentée complètement
- [ ] Exemples de code fournis
- [ ] Timeline définie
- [ ] Ressources estimées
- [ ] Métriques de succès définies

### Lors de la publication
- [ ] Issue principale créée
- [ ] Labels ajoutés
- [ ] Milestone créé
- [ ] Project board configuré
- [ ] Sous-issues créées
- [ ] PR template créé

### Après la publication
- [ ] Annonce aux contributeurs
- [ ] Suivi régulier de la progression
- [ ] Réponse aux questions
- [ ] Validation des PRs
- [ ] Mise à jour de la documentation

---

## 🎓 Prochaines features à documenter

Voici quelques idées de features à ajouter :

1. **Tests automatisés pour les labs**
   - Tests unitaires pour chaque lab
   - Tests d'intégration
   - CI/CD avec GitHub Actions

2. **Documentation interactive**
   - Tutoriels vidéo
   - Diagrammes interactifs
   - Exemples en ligne

3. **Environnement de développement standardisé**
   - Devcontainer pour VS Code
   - Configuration Docker Compose
   - Scripts de setup automatique

4. **Système de validation automatique**
   - Vérification des TODOs complétés
   - Tests automatiques des solutions
   - Feedback instantané

5. **Plateforme d'évaluation**
   - Soumission automatique
   - Correction automatique
   - Tableau de bord étudiant

Souhaitez-vous que je documente l'une de ces features ?

---

## 📞 Contact et support

Pour toute question sur cette feature :
- Ouvrir une discussion sur GitHub
- Commenter l'issue principale
- Contacter les mainteneurs

---

**Dernière mise à jour :** 2026-01-25
**Version :** 1.0
**Auteur :** IBM Bob