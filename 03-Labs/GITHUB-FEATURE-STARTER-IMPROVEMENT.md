<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# [FEATURE] Amélioration des projets starter pour les étudiants

## 📋 Type
Feature / Enhancement

## 🎯 Objectif
Restructurer tous les projets `starter/` des labs pour qu'ils soient directement utilisables par les étudiants avec une structure de projet complète et des instructions claires sous forme de TODOs et commentaires.

## 🔍 Problème actuel

Actuellement, les projets starter sont incomplets ou mal structurés :
- Structure de projet incomplète ou absente
- Classes manquantes ou mal organisées
- Pas d'instructions claires pour les étudiants
- Difficile pour les étudiants de savoir par où commencer
- Écart trop important entre starter et solution

## 💡 Solution proposée

### Principe général
Créer des starters qui :
1. **Ont la même structure** que la solution
2. **Contiennent toutes les classes** nécessaires avec les mêmes noms
3. **Incluent des TODOs** numérotés et progressifs
4. **Fournissent des commentaires** explicatifs
5. **Compilent sans erreur** (avec des implémentations vides ou par défaut)
6. **Guident l'étudiant** étape par étape

### Structure type d'une classe starter

```java
package com.bank.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import com.bank.model.Client;
import java.util.List;

/**
 * Service de gestion des clients.
 * 
 * Ce service utilise CDI pour l'injection de dépendances et JPA pour la persistance.
 * 
 * @see Client
 */
@ApplicationScoped
public class ClientService {

    // TODO 1: Injecter l'EntityManager
    // Utilisez l'annotation @Inject pour injecter l'EntityManager
    // Exemple: @Inject private EntityManager em;
    
    /**
     * Crée un nouveau client dans la base de données.
     * 
     * @param client Le client à créer
     * @return Le client créé avec son ID généré
     */
    public Client createClient(Client client) {
        // TODO 2: Implémenter la création d'un client
        // 1. Valider que le client n'est pas null
        // 2. Utiliser em.persist() pour persister le client
        // 3. Retourner le client créé
        
        throw new UnsupportedOperationException("TODO: Implémenter createClient()");
    }

    /**
     * Récupère tous les clients.
     * 
     * @return Liste de tous les clients
     */
    public List<Client> findAllClients() {
        // TODO 3: Implémenter la récupération de tous les clients
        // Utilisez une requête JPQL: "SELECT c FROM Client c"
        // Exemple: return em.createQuery("...", Client.class).getResultList();
        
        throw new UnsupportedOperationException("TODO: Implémenter findAllClients()");
    }

    /**
     * Recherche un client par son ID.
     * 
     * @param id L'identifiant du client
     * @return Le client trouvé ou null
     */
    public Client findClientById(Long id) {
        // TODO 4: Implémenter la recherche par ID
        // Utilisez em.find(Client.class, id)
        
        throw new UnsupportedOperationException("TODO: Implémenter findClientById()");
    }

    /**
     * Met à jour un client existant.
     * 
     * @param client Le client à mettre à jour
     * @return Le client mis à jour
     */
    public Client updateClient(Client client) {
        // TODO 5: Implémenter la mise à jour d'un client
        // Utilisez em.merge() pour fusionner les modifications
        
        throw new UnsupportedOperationException("TODO: Implémenter updateClient()");
    }

    /**
     * Supprime un client.
     * 
     * @param id L'identifiant du client à supprimer
     */
    public void deleteClient(Long id) {
        // TODO 6: Implémenter la suppression d'un client
        // 1. Récupérer le client avec findClientById()
        // 2. Si le client existe, utiliser em.remove()
        
        throw new UnsupportedOperationException("TODO: Implémenter deleteClient()");
    }
}
```

## 📦 Labs concernés

### Lab01-FirstServlet
**État actuel :** Starter minimal
**Améliorations nécessaires :**
- [ ] Créer la structure complète du projet
- [ ] Ajouter les classes servlet avec TODOs
- [ ] Inclure les fichiers de configuration (web.xml, server.xml)
- [ ] Ajouter les ressources statiques (CSS, HTML)

### Lab02-ServletsJSP
**État actuel :** Pas de starter
**Améliorations nécessaires :**
- [ ] Créer le projet starter complet
- [ ] Ajouter les servlets avec TODOs
- [ ] Inclure les JSP avec commentaires
- [ ] Configurer le projet Maven

### Lab03-JPA
**État actuel :** Starter partiel
**Améliorations nécessaires :**
- [ ] Compléter les entités JPA avec TODOs
- [ ] Ajouter les services avec instructions
- [ ] Inclure persistence.xml configuré
- [ ] Ajouter les migrations Flyway

### Lab04-CDI
**État actuel :** Pas de starter
**Améliorations nécessaires :**
- [ ] Créer la structure avec beans.xml
- [ ] Ajouter les classes avec annotations CDI à compléter
- [ ] Inclure les qualifiers et interceptors
- [ ] Documenter les patterns CDI

### Lab04B-EJB
**État actuel :** Pas de starter
**Améliorations nécessaires :**
- [ ] Créer les EJB avec TODOs
- [ ] Ajouter les interfaces locales/remote
- [ ] Documenter les types d'EJB
- [ ] Inclure les exemples de transactions

### Lab05-REST
**État actuel :** Starter minimal
**Améliorations nécessaires :**
- [ ] Compléter les ressources REST avec TODOs
- [ ] Ajouter les DTOs et mappers
- [ ] Inclure les exception mappers
- [ ] Documenter les annotations JAX-RS

### Lab05B-JMS
**État actuel :** Starter partiel
**Améliorations nécessaires :**
- [ ] Compléter les MDB avec TODOs
- [ ] Ajouter les producers JMS
- [ ] Documenter la configuration JMS
- [ ] Inclure les exemples de messages

### Lab06-DDD
**État actuel :** Pas de starter
**Améliorations nécessaires :**
- [ ] Créer la structure DDD complète
- [ ] Ajouter les entités du domaine
- [ ] Inclure les services du domaine
- [ ] Documenter les patterns DDD

### Lab07-Hexagonal
**État actuel :** Pas de starter
**Améliorations nécessaires :**
- [ ] Créer l'architecture hexagonale
- [ ] Ajouter les ports et adapters
- [ ] Documenter la séparation des couches
- [ ] Inclure les exemples d'implémentation

### Lab08-Microservices
**État actuel :** Starter incomplet
**Améliorations nécessaires :**
- [ ] Compléter les 3 microservices
- [ ] Ajouter les clients REST avec TODOs
- [ ] Inclure la configuration MicroProfile
- [ ] Documenter l'architecture microservices

### Lab09-Security
**État actuel :** Pas de starter
**Améliorations nécessaires :**
- [ ] Créer la configuration de sécurité
- [ ] Ajouter les annotations de sécurité
- [ ] Documenter JWT et OAuth2
- [ ] Inclure les exemples d'authentification

## 🎨 Template de classe starter

### 1. En-tête de classe
```java
/**
 * [Description de la classe]
 * 
 * Objectifs pédagogiques:
 * - [Objectif 1]
 * - [Objectif 2]
 * - [Objectif 3]
 * 
 * Technologies utilisées:
 * - [Techno 1]
 * - [Techno 2]
 * 
 * @see [Classes liées]
 */
```

### 2. TODOs numérotés
```java
// TODO 1: [Description courte]
// [Instructions détaillées sur plusieurs lignes]
// Exemple: [Code exemple si nécessaire]
// Astuce: [Conseil ou référence]
```

### 3. Méthodes avec documentation
```java
/**
 * [Description de la méthode]
 * 
 * @param [nom] [description]
 * @return [description du retour]
 * @throws [Exception] [quand elle est levée]
 */
public ReturnType methodName(ParamType param) {
    // TODO X: Implémenter cette méthode
    // [Instructions étape par étape]
    
    throw new UnsupportedOperationException("TODO: Implémenter methodName()");
}
```

### 4. Configuration avec commentaires
```xml
<!-- TODO: Configurer [élément] -->
<!-- 
  Instructions:
  1. [Étape 1]
  2. [Étape 2]
  
  Exemple:
  <element>
    <property>value</property>
  </element>
-->
```

## 📝 Checklist de création d'un starter

Pour chaque lab, le starter doit :

### Structure du projet
- [ ] Même structure de packages que la solution
- [ ] Tous les fichiers de configuration présents
- [ ] Dépendances Maven correctes dans pom.xml
- [ ] Fichiers de ressources (CSS, JS, images) inclus

### Classes Java
- [ ] Toutes les classes créées avec les bons noms
- [ ] Imports nécessaires ajoutés
- [ ] Annotations de base présentes
- [ ] TODOs numérotés et progressifs
- [ ] Commentaires explicatifs détaillés
- [ ] Méthodes avec signatures correctes
- [ ] Implémentations par défaut (throw UnsupportedOperationException)

### Documentation
- [ ] Javadoc complète sur les classes
- [ ] Javadoc sur les méthodes publiques
- [ ] Commentaires inline pour les parties complexes
- [ ] Références aux concepts Jakarta EE
- [ ] Exemples de code quand pertinent

### Compilation
- [ ] Le projet compile sans erreur
- [ ] Aucune dépendance manquante
- [ ] Configuration serveur valide
- [ ] Tests unitaires de base (optionnel)

### Pédagogie
- [ ] TODOs dans l'ordre logique d'implémentation
- [ ] Difficulté progressive
- [ ] Liens vers la documentation officielle
- [ ] Astuces et bonnes pratiques
- [ ] Erreurs courantes à éviter mentionnées

## 🔧 Outils et scripts

### Script de génération de starter
```bash
#!/bin/bash
# generate-starter.sh
# Génère un projet starter à partir de la solution

LAB_NAME=$1
SOLUTION_DIR="esipe-javaee/03-Labs/${LAB_NAME}/solution"
STARTER_DIR="esipe-javaee/03-Labs/${LAB_NAME}/starter"

# 1. Copier la structure
cp -r "$SOLUTION_DIR" "$STARTER_DIR"

# 2. Transformer les classes Java
find "$STARTER_DIR" -name "*.java" -exec ./transform-to-starter.py {} \;

# 3. Nettoyer les fichiers générés
rm -rf "$STARTER_DIR/target"
rm -rf "$STARTER_DIR/.settings"

echo "Starter généré pour $LAB_NAME"
```

### Script Python de transformation
```python
#!/usr/bin/env python3
# transform-to-starter.py
# Transforme une classe solution en classe starter

import re
import sys

def transform_method(method_content):
    """Transforme une méthode en ajoutant des TODOs"""
    # Extraire la signature
    signature = extract_signature(method_content)
    
    # Créer le TODO
    todo = generate_todo(signature)
    
    # Remplacer l'implémentation
    return f"{signature} {{\n{todo}\n    throw new UnsupportedOperationException(\"TODO\");\n}}"

def add_class_documentation(class_content):
    """Ajoute la documentation pédagogique"""
    # ... logique d'ajout de documentation
    pass

# ... reste du script
```

## 📊 Métriques de succès

### Quantitatif
- [ ] 100% des labs ont un starter fonctionnel
- [ ] Tous les starters compilent sans erreur
- [ ] Moyenne de 10-15 TODOs par lab
- [ ] Temps de setup < 5 minutes par lab

### Qualitatif
- [ ] Les étudiants comprennent par où commencer
- [ ] Réduction des questions sur la structure
- [ ] Meilleure progression pédagogique
- [ ] Feedback positif des étudiants

## 🚀 Plan de déploiement

### Phase 1 : Labs prioritaires (Semaine 1-2)
1. Lab03-JPA (le plus utilisé)
2. Lab05-REST (fondamental)
3. Lab08-Microservices (complexe)

### Phase 2 : Labs intermédiaires (Semaine 3-4)
4. Lab04-CDI
5. Lab06-DDD
6. Lab07-Hexagonal

### Phase 3 : Labs complémentaires (Semaine 5-6)
7. Lab01-FirstServlet
8. Lab02-ServletsJSP
9. Lab04B-EJB
10. Lab05B-JMS
11. Lab09-Security

### Phase 4 : Validation et ajustements (Semaine 7)
- Tests avec un groupe d'étudiants
- Collecte de feedback
- Ajustements basés sur les retours
- Documentation finale

## 📚 Documentation associée

### Pour les développeurs
- Guide de création de starter
- Templates de classes
- Scripts d'automatisation
- Checklist de validation

### Pour les étudiants
- Guide de démarrage rapide
- Explication des TODOs
- Ressources complémentaires
- FAQ par lab

## 🔗 Références

- [Jakarta EE Tutorial](https://jakarta.ee/learn/)
- [MicroProfile Documentation](https://microprofile.io/)
- [Open Liberty Guides](https://openliberty.io/guides/)
- [Best Practices for Teaching Java EE](https://www.oracle.com/technical-resources/)

## 💬 Exemples concrets

### Avant (Lab03-JPA starter actuel)
```java
// Fichier quasi vide ou inexistant
public class ClientService {
    // Les étudiants ne savent pas quoi faire
}
```

### Après (Lab03-JPA starter amélioré)
```java
package com.bank.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import com.bank.model.Client;
import java.util.List;

/**
 * Service de gestion des clients avec JPA.
 * 
 * Objectifs pédagogiques:
 * - Comprendre l'injection CDI
 * - Maîtriser les opérations CRUD avec JPA
 * - Gérer les transactions
 * 
 * @see Client
 * @see EntityManager
 */
@ApplicationScoped
public class ClientService {

    // TODO 1: Injecter l'EntityManager
    // L'EntityManager est produit par EntityManagerProducer
    // Utilisez @Inject pour l'injection
    // private EntityManager em;

    /**
     * Crée un nouveau client.
     * 
     * @param client Le client à créer
     * @return Le client créé avec son ID
     */
    public Client createClient(Client client) {
        // TODO 2: Implémenter la création
        // 1. Valider que client != null
        // 2. Utiliser em.persist(client)
        // 3. Retourner le client
        // Note: La transaction est gérée automatiquement
        
        throw new UnsupportedOperationException("TODO: Implémenter createClient()");
    }

    // ... autres méthodes avec TODOs similaires
}
```

## ⚠️ Points d'attention

### À faire
✅ Garder la même structure que la solution
✅ Fournir des TODOs clairs et progressifs
✅ Inclure des exemples de code
✅ Documenter les concepts Jakarta EE
✅ S'assurer que le projet compile

### À éviter
❌ Laisser des classes vides sans instructions
❌ Créer une structure différente de la solution
❌ Oublier les imports nécessaires
❌ Mettre trop de code (donner la solution)
❌ Utiliser des TODOs vagues ou ambigus

## 🎯 Résultat attendu

Un étudiant qui ouvre un projet starter doit :
1. **Comprendre immédiatement** la structure du projet
2. **Savoir par où commencer** grâce aux TODOs numérotés
3. **Avoir toutes les informations** nécessaires dans les commentaires
4. **Pouvoir compiler** le projet dès le départ
5. **Progresser étape par étape** vers la solution

## 📅 Timeline

- **Semaine 1-2** : Phase 1 (Labs prioritaires)
- **Semaine 3-4** : Phase 2 (Labs intermédiaires)
- **Semaine 5-6** : Phase 3 (Labs complémentaires)
- **Semaine 7** : Validation et ajustements
- **Semaine 8** : Déploiement final et documentation

## 👥 Ressources nécessaires

- 1 développeur senior (création des templates)
- 1 développeur junior (transformation des labs)
- 1 enseignant (validation pédagogique)
- Temps estimé : 40-60 heures au total

## 📈 Bénéfices attendus

### Pour les étudiants
- Meilleure compréhension de la structure
- Progression plus fluide
- Moins de frustration
- Apprentissage plus efficace

### Pour les enseignants
- Moins de questions répétitives
- Meilleure évaluation du niveau
- Support plus ciblé
- Feedback plus constructif

### Pour le projet
- Qualité pédagogique améliorée
- Meilleure adoption
- Réputation renforcée
- Contribution à la communauté