# Issue GitHub : Erreur de compilation dans AccountWebController

## 🐛 Type
Bug - Erreur de compilation

## 📋 Titre
Fix compilation errors in AccountWebController - Missing getter methods

## 🏷️ Labels suggérés
- `bug`
- `compilation-error`
- `Lab08-Microservices`
- `good first issue` (si vous voulez que d'autres contributeurs puissent le résoudre)

## 📝 Description

### Problème
Le projet `api-gateway` dans Lab08-Microservices ne compile pas en raison d'erreurs dans la classe `AccountWebController.java`. Le build Maven échoue avec 18 erreurs de compilation.

### Erreurs rencontrées
```
[ERROR] COMPILATION ERROR : 
[ERROR] cannot find symbol
  symbol:   method getClientServiceClient()
  location: class com.bank.gateway.web.AccountWebController
[ERROR] cannot find symbol
  symbol:   method getAccountServiceClient()
  location: class com.bank.gateway.web.AccountWebController
```

### Cause
Le code appelle des méthodes getter inexistantes (`getClientServiceClient()` et `getAccountServiceClient()`) au lieu d'utiliser directement les champs injectés par CDI.

### Fichier concerné
`esipe-javaee/03-Labs/Lab08-Microservices/solution/api-gateway/src/main/java/com/bank/gateway/web/AccountWebController.java`

### Lignes affectées
- Ligne 143, 144, 151, 164, 165, 178
- Ligne 205, 216, 231, 242, 257
- Ligne 268, 269, 287, 298, 309, 320, 332

### Impact
- ❌ Le build Maven échoue avec `BUILD FAILURE`
- ❌ Impossible de générer le fichier WAR
- ❌ Le projet ne peut pas être déployé

## ✅ Solution

### Changements nécessaires
Remplacer tous les appels aux méthodes getter par l'accès direct aux champs injectés :

**Avant :**
```java
ClientDTO client = getClientServiceClient().getClientById(clientId);
List<AccountDTO> accounts = getAccountServiceClient().getAccountsByClientId(clientId);
```

**Après :**
```java
ClientDTO client = clientServiceClient.getClientById(clientId);
List<AccountDTO> accounts = accountServiceClient.getAccountsByClientId(clientId);
```

### Détails techniques
Les champs sont déjà injectés via CDI aux lignes 33-39 :
```java
@Inject
@RestClient
private AccountServiceClient accountServiceClient;

@Inject
@RestClient
private ClientServiceClient clientServiceClient;
```

Il faut donc utiliser directement `accountServiceClient` et `clientServiceClient` sans passer par des getters.

## 🔧 Commande pour reproduire l'erreur
```bash
cd esipe-javaee/03-Labs/Lab08-Microservices/solution/api-gateway
mvn package
```

## ✨ Résultat attendu après correction
```
[INFO] BUILD SUCCESS
[INFO] Building war: .../target/api-gateway.war
```

## 📚 Contexte
Ce problème affecte le Lab08 sur l'architecture microservices. L'API Gateway utilise MicroProfile Rest Client pour communiquer avec les microservices backend (account-service et client-service).

## 🔗 Références
- Jakarta EE CDI (Contexts and Dependency Injection)
- MicroProfile Rest Client
- Maven Compiler Plugin

---

## 📋 Checklist pour la résolution
- [ ] Remplacer `getClientServiceClient()` par `clientServiceClient` (6 occurrences)
- [ ] Remplacer `getAccountServiceClient()` par `accountServiceClient` (12 occurrences)
- [ ] Vérifier que le build Maven réussit
- [ ] Tester le déploiement de l'application
- [ ] Mettre à jour la documentation si nécessaire

## 💡 Note pour les contributeurs
Cette correction est simple et ne nécessite que des remplacements de texte. C'est une bonne première contribution pour se familiariser avec le projet !