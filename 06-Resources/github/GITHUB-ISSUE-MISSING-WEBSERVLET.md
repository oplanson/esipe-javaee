# Issue GitHub : ClientWebController non accessible (404)

## 🐛 Type
Bug - Configuration manquante

## 📋 Titre
ClientWebController returns 404 - Missing @WebServlet annotation

## 🏷️ Labels suggérés
- `bug`
- `configuration`
- `Lab08-Microservices`
- `good first issue`

## 📝 Description

### Problème
L'URL `http://localhost:9080/web/clients` retourne une erreur 404, alors que `http://localhost:9080/web/accounts` fonctionne correctement.

### Symptômes
- ✅ `http://localhost:9080/web/accounts` → Fonctionne
- ❌ `http://localhost:9080/web/clients` → Erreur 404 HTTP

### Cause
La classe `ClientWebController` n'a pas l'annotation `@WebServlet` nécessaire pour mapper les URLs, contrairement à `AccountWebController` qui l'a.

### Fichier concerné
`esipe-javaee/03-Labs/Lab08-Microservices/solution/api-gateway/src/main/java/com/bank/gateway/web/ClientWebController.java`

### Impact
- ❌ Impossible d'accéder à l'interface web de gestion des clients
- ❌ Toutes les fonctionnalités liées aux clients sont inaccessibles
- ⚠️ Incohérence avec `AccountWebController` qui fonctionne

## 🔍 Analyse

### Configuration actuelle

**web.xml (ligne 11):**
```xml
<!-- Servlets now use @WebServlet annotation with CDI injection -->
```

Le `web.xml` indique que les servlets utilisent l'annotation `@WebServlet`, mais `ClientWebController` ne l'a pas.

**AccountWebController (ligne 28) - ✅ Correct:**
```java
@WebServlet(urlPatterns = {"/web/accounts", "/web/accounts/*"})
public class AccountWebController extends HttpServlet {
```

**ClientWebController (ligne 27) - ❌ Manquant:**
```java
public class ClientWebController extends HttpServlet {
```

## ✅ Solution

### Changement nécessaire
Ajouter l'annotation `@WebServlet` à la classe `ClientWebController`.

**Avant:**
```java
package com.bank.gateway.web;

import com.bank.gateway.client.ClientServiceClient;
import com.bank.gateway.dto.ClientDTO;
import com.bank.gateway.dto.ClientWithAccountsDTO;
import com.bank.gateway.service.BankingAggregationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

/**
 * Web Controller for Client operations
 * Handles JSP-based web interface requests
 * Uses RestClientBuilder for programmatic REST client creation
 * Configured in web.xml
 */
public class ClientWebController extends HttpServlet {
```

**Après:**
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

/**
 * Web Controller for Client operations
 * Handles JSP-based web interface requests
 * Uses RestClientBuilder for programmatic REST client creation
 */
@WebServlet(urlPatterns = {"/web/clients", "/web/clients/*"})
public class ClientWebController extends HttpServlet {
```

### Modifications requises
1. Ajouter l'import : `import jakarta.servlet.annotation.WebServlet;`
2. Ajouter l'annotation : `@WebServlet(urlPatterns = {"/web/clients", "/web/clients/*"})`
3. Mettre à jour le commentaire JavaDoc (retirer "Configured in web.xml")

## 🔧 Commande pour tester

### Avant la correction
```bash
# Démarrer l'application
cd esipe-javaee/03-Labs/Lab08-Microservices/solution/api-gateway
mvn liberty:dev

# Tester (dans un autre terminal)
curl -I http://localhost:9080/web/clients
# Résultat: HTTP/1.1 404 Not Found
```

### Après la correction
```bash
# Recompiler et redéployer
mvn clean package

# Tester
curl -I http://localhost:9080/web/clients
# Résultat attendu: HTTP/1.1 200 OK (ou redirection 302)
```

## 📚 Contexte

### Architecture
L'API Gateway utilise deux contrôleurs web :
- **AccountWebController** - Gestion des comptes (✅ fonctionne)
- **ClientWebController** - Gestion des clients (❌ ne fonctionne pas)

### Pattern utilisé
Les deux contrôleurs suivent le même pattern :
- Annotation `@WebServlet` pour le mapping d'URL
- Utilisation de `RestClientBuilder` pour créer les clients REST
- Pas d'injection CDI (création programmatique dans `init()`)

### URLs attendues
Une fois corrigé, les URLs suivantes devraient fonctionner :
- `GET /web/clients` - Liste des clients
- `GET /web/clients/view?id={id}` - Détails d'un client
- `GET /web/clients/new` - Formulaire nouveau client
- `GET /web/clients/edit?id={id}` - Formulaire édition client
- `POST /web/clients/create` - Créer un client
- `POST /web/clients/update` - Mettre à jour un client
- `POST /web/clients/delete` - Supprimer un client

## 🔗 Références
- Jakarta Servlet Specification - @WebServlet annotation
- [Issue liée] Compilation errors in AccountWebController (déjà corrigée)

## 📋 Checklist pour la résolution
- [ ] Ajouter l'import `jakarta.servlet.annotation.WebServlet`
- [ ] Ajouter l'annotation `@WebServlet(urlPatterns = {"/web/clients", "/web/clients/*"})`
- [ ] Mettre à jour le commentaire JavaDoc
- [ ] Vérifier que le build Maven réussit
- [ ] Tester l'accès à `http://localhost:9080/web/clients`
- [ ] Vérifier que toutes les opérations CRUD fonctionnent

## 💡 Note pour les contributeurs
Cette correction est très simple - il suffit d'ajouter une annotation et un import. C'est une excellente première contribution pour comprendre le mapping des servlets Jakarta EE !

## 🔄 Relation avec d'autres issues
Cette issue est liée à l'issue de compilation dans `AccountWebController`. Les deux contrôleurs ont été affectés par des problèmes de configuration différents mais dans le même lab.