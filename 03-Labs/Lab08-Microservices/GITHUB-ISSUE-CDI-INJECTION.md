<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Issue GitHub : ClientWebController - Liste de clients vide

## 🐛 Type
Bug - Injection CDI manquante

## 📋 Titre
ClientWebController returns empty client list - Missing CDI injection

## 🏷️ Labels suggérés
- `bug`
- `cdi`
- `Lab08-Microservices`
- `high-priority`

## 📝 Description

### Problème
L'URL `http://localhost:9080/web/clients` affiche une page vide (aucun client), alors que le service client contient des données et que `http://localhost:9080/web/accounts` fonctionne correctement.

### Symptômes
- ✅ Service client accessible : `http://localhost:9081/api/clients` retourne 3 clients
- ✅ Page web s'affiche : `http://localhost:9080/web/clients` (pas de 404)
- ❌ Liste vide : Aucun client n'apparaît dans la page
- ✅ Comptes fonctionnent : `http://localhost:9080/web/accounts` affiche les comptes

### Cause racine
`ClientWebController` crée manuellement les clients REST dans la méthode `init()` au lieu d'utiliser l'injection CDI. Cette approche empêche :
1. La configuration correcte des clients REST via MicroProfile Config
2. L'utilisation des annotations de fault tolerance
3. Le bon fonctionnement de `BankingAggregationService`

### Fichier concerné
`esipe-javaee/03-Labs/Lab08-Microservices/solution/api-gateway/src/main/java/com/bank/gateway/web/ClientWebController.java`

### Impact
- ❌ Impossible de voir la liste des clients
- ❌ Toutes les fonctionnalités de gestion des clients sont inutilisables
- ⚠️ Incohérence architecturale avec `AccountWebController` qui utilise CDI

## 🔍 Analyse

### Architecture incohérente

**AccountWebController (✅ Correct) :**
```java
@WebServlet(urlPatterns = {"/web/accounts", "/web/accounts/*"})
public class AccountWebController extends HttpServlet {
    
    @Inject
    @RestClient
    private AccountServiceClient accountServiceClient;
    
    @Inject
    @RestClient
    private ClientServiceClient clientServiceClient;
    
    // Pas de méthode init() - tout est injecté par CDI
}
```

**ClientWebController (❌ Incorrect) :**
```java
@WebServlet(urlPatterns = {"/web/clients", "/web/clients/*"})
public class ClientWebController extends HttpServlet {
    
    private ClientServiceClient clientServiceClient;
    private BankingAggregationService aggregationService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Création manuelle des clients REST
        clientServiceClient = RestClientBuilder.newBuilder()
            .baseUri(URI.create(clientServiceUrl))
            .build(ClientServiceClient.class);
        
        // Création manuelle du service d'agrégation
        aggregationService = new BankingAggregationService(
            clientServiceClient,
            RestClientBuilder.newBuilder()
                .baseUri(URI.create(accountServiceUrl))
                .build(AccountServiceClient.class)
        );
    }
    
    private ClientServiceClient getClientServiceClient() {
        return clientServiceClient;
    }
    
    private BankingAggregationService getAggregationService() {
        return aggregationService;
    }
}
```

### Pourquoi ça ne fonctionne pas

1. **Pas de configuration MicroProfile** : Les clients créés manuellement n'utilisent pas la configuration de `microprofile-config.properties`
2. **Pas de fault tolerance** : Les annotations `@Retry`, `@CircuitBreaker`, etc. ne fonctionnent pas
3. **Service d'agrégation mal initialisé** : `BankingAggregationService` est créé manuellement, ses propres injections CDI ne fonctionnent pas

## ✅ Solution

### Changements nécessaires

**1. Supprimer la création manuelle et utiliser CDI**

**Avant :**
```java
package com.bank.gateway.web;

import com.bank.gateway.client.ClientServiceClient;
import com.bank.gateway.dto.ClientDTO;
import com.bank.gateway.dto.ClientWithAccountsDTO;
import com.bank.gateway.service.BankingAggregationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/web/clients", "/web/clients/*"})
public class ClientWebController extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ClientWebController.class.getName());
    
    private ClientServiceClient clientServiceClient;
    private BankingAggregationService aggregationService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Get the client service URL from MicroProfile Config
        java.util.Optional<String> optClientUrl = ConfigProvider.getConfig()
            .getOptionalValue("client.service.url", String.class);
        String clientServiceUrl = optClientUrl.orElseGet(() ->
            ConfigProvider.getConfig().getOptionalValue(
                "com.bank.gateway.client.ClientServiceClient/mp-rest/url", String.class
            ).orElse("http://localhost:9081/api")
        );
        
        // Build REST client programmatically
        clientServiceClient = RestClientBuilder.newBuilder()
            .baseUri(URI.create(clientServiceUrl))
            .build(ClientServiceClient.class);
        
        // Create aggregation service manually
        java.util.Optional<String> optAccountUrl = ConfigProvider.getConfig()
            .getOptionalValue("account.service.url", String.class);
        String accountServiceUrl = optAccountUrl.orElseGet(() ->
            ConfigProvider.getConfig().getOptionalValue(
                "com.bank.gateway.client.AccountServiceClient/mp-rest/url", String.class
            ).orElse("http://localhost:9082/api")
        );
        
        aggregationService = new BankingAggregationService(
            clientServiceClient,
            RestClientBuilder.newBuilder()
                .baseUri(URI.create(accountServiceUrl))
                .build(com.bank.gateway.client.AccountServiceClient.class)
        );
        
        LOGGER.info("ClientWebController initialized with client service URL: " + clientServiceUrl);
    }
    
    private ClientServiceClient getClientServiceClient() {
        return clientServiceClient;
    }
    
    private BankingAggregationService getAggregationService() {
        return aggregationService;
    }
```

**Après :**
```java
package com.bank.gateway.web;

import com.bank.gateway.client.ClientServiceClient;
import com.bank.gateway.dto.ClientDTO;
import com.bank.gateway.dto.ClientWithAccountsDTO;
import com.bank.gateway.service.BankingAggregationService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/web/clients", "/web/clients/*"})
public class ClientWebController extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ClientWebController.class.getName());
    
    @Inject
    @RestClient
    private ClientServiceClient clientServiceClient;
    
    @Inject
    private BankingAggregationService aggregationService;
```

**2. Remplacer les appels aux getters par l'accès direct**

```java
// Avant
List<ClientWithAccountsDTO> clientsWithAccounts = getAggregationService().getAllClientsWithAccounts();
ClientDTO client = getClientServiceClient().getClientById(id);

// Après
List<ClientWithAccountsDTO> clientsWithAccounts = aggregationService.getAllClientsWithAccounts();
ClientDTO client = clientServiceClient.getClientById(id);
```

**3. Corriger les URLs de redirection**

```java
// Avant
response.sendRedirect(request.getContextPath() + "/clients");

// Après
response.sendRedirect(request.getContextPath() + "/web/clients");
```

**4. Corriger les paths dans le switch POST**

```java
// Avant
String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
switch (path) {
    case "/clients/new":
    case "/clients/edit":

// Après
String path = request.getServletPath();
switch (path) {
    case "/web/clients/new":
    case "/web/clients/edit":
```

## 🔧 Commande pour tester

### Vérifier que le service client fonctionne
```bash
curl http://localhost:9081/api/clients
# Devrait retourner une liste de clients
```

### Avant la correction
```bash
# Accéder à la page web
open http://localhost:9080/web/clients
# Résultat: Page vide, aucun client affiché
```

### Après la correction
```bash
# Recompiler
cd esipe-javaee/03-Labs/Lab08-Microservices/solution/api-gateway
mvn clean package

# Redémarrer le serveur Liberty

# Tester
open http://localhost:9080/web/clients
# Résultat attendu: Liste des 3 clients avec leurs comptes
```

## 📚 Contexte

### Pattern BFF (Backend For Frontend)
L'API Gateway utilise le pattern BFF pour agréger les données de plusieurs microservices :
- **Client Service** (port 9081) - Gestion des clients
- **Account Service** (port 9082) - Gestion des comptes
- **BankingAggregationService** - Agrège les données des deux services

### Importance de CDI
L'injection CDI est essentielle pour :
1. **Configuration centralisée** via `microprofile-config.properties`
2. **Fault tolerance** avec `@Retry`, `@CircuitBreaker`, `@Fallback`
3. **Cohérence architecturale** entre tous les contrôleurs
4. **Testabilité** et injection de mocks

## 🔗 Références
- Jakarta EE CDI Specification
- MicroProfile Rest Client
- MicroProfile Fault Tolerance
- [Issue liée] Compilation errors in AccountWebController
- [Issue liée] ClientWebController returns 404

## 📋 Checklist pour la résolution
- [ ] Ajouter `@Inject` et `@RestClient` pour `ClientServiceClient`
- [ ] Ajouter `@Inject` pour `BankingAggregationService`
- [ ] Supprimer la méthode `init()` et toute création manuelle
- [ ] Supprimer les méthodes getter `getClientServiceClient()` et `getAggregationService()`
- [ ] Remplacer tous les appels aux getters par l'accès direct aux champs
- [ ] Corriger les URLs de redirection (`/clients` → `/web/clients`)
- [ ] Corriger les paths dans le switch POST
- [ ] Vérifier que le build Maven réussit
- [ ] Tester l'affichage de la liste des clients
- [ ] Vérifier que toutes les opérations CRUD fonctionnent

## 💡 Note pour les contributeurs
Cette correction démontre l'importance d'une architecture cohérente. Tous les contrôleurs doivent utiliser le même pattern d'injection pour garantir le bon fonctionnement des fonctionnalités Jakarta EE et MicroProfile.

## 🔄 Relation avec d'autres issues
Cette issue est liée aux deux autres bugs du Lab08 :
1. Compilation errors in AccountWebController (getters inexistants)
2. ClientWebController returns 404 (annotation manquante)

Les trois issues montrent différents aspects de problèmes de configuration et d'architecture dans le même contrôleur.