<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 5: JAX-RS and RESTful Web Services

## Banking Application - REST API Implementation

**Course:** Jakarta EE and Microservices  
**Lab Duration:** 3 hours  
**Difficulty:** Intermediate  
**Prerequisites:** Labs 1-4 completed

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Learning Objectives](#learning-objectives)
3. [Prerequisites](#prerequisites)
4. [Lab Structure](#lab-structure)
5. [Part 1: REST API Basics](#part-1-rest-api-basics)
6. [Part 2: Client REST Resources](#part-2-client-rest-resources)
7. [Part 3: Account REST Resources](#part-3-account-rest-resources)
8. [Part 4: Exception Handling](#part-4-exception-handling)
9. [Part 5: Validation](#part-5-validation)
10. [Part 6: MicroProfile Rest Client](#part-6-microprofile-rest-client)
11. [Part 7: Testing](#part-7-testing)
12. [Deployment](#deployment)
13. [Testing Guide](#testing-guide)
14. [Troubleshooting](#troubleshooting)
15. [Additional Challenges](#additional-challenges)

---

## Overview

In this lab, you will add a complete REST API to the banking application while keeping the existing web UI (JSP). This demonstrates how to build a hybrid application that serves both traditional web pages and modern REST APIs.

### What You'll Build

- **RESTful API** for clients and accounts
- **JSON-B** integration for automatic JSON conversion
- **Exception handling** with custom mappers
- **Bean validation** for input validation
- **MicroProfile Rest Client** for type-safe client
- **Comprehensive testing** with curl and scripts

### Architecture

```
┌─────────────────────────────────────────┐
│    Web Browser / REST Client / Mobile   │
└────────────┬────────────────────────────┘
             │
    ┌────────┴────────┐
    │                 │
┌───▼────┐      ┌────▼─────┐
│  JSP   │      │ REST API │
│  UI    │      │ (JAX-RS) │
└───┬────┘      └────┬─────┘
    │                │
    └────────┬───────┘
             │
      ┌──────▼──────┐
      │   Services  │
      │    (CDI)    │
      └──────┬──────┘
             │
      ┌──────▼──────┐
      │     JPA     │
      └──────┬──────┘
             │
      ┌──────▼──────┐
      │  PostgreSQL │
      └─────────────┘
```

---

## Learning Objectives

By completing this lab, you will be able to:

1. ✅ **Create RESTful resources** with JAX-RS annotations
2. ✅ **Handle JSON** with JSON-B
3. ✅ **Implement exception handling** with custom mappers
4. ✅ **Validate inputs** with Bean Validation
5. ✅ **Use MicroProfile Rest Client** for type-safe clients
6. ✅ **Test REST APIs** with curl and automated scripts
7. ✅ **Integrate REST with CDI** and existing services

---

## Prerequisites

### Knowledge Prerequisites

- Completed Labs 1-4
- Understanding of HTTP protocol
- Basic knowledge of REST principles
- Familiarity with JSON format

### Technical Prerequisites

- JDK 17 or later
- Maven 3.8+
- Open Liberty 23.0.0.12+
- PostgreSQL 16+
- Podman or Docker
- curl or Postman for testing

### Verify Prerequisites

```bash
# Check Java version
java -version  # Should be 17+

# Check Maven version
mvn -version   # Should be 3.8+

# Check Podman/Docker
podman --version  # or docker --version

# Check curl
curl --version
```

---

## Lab Structure

```
Lab05-REST/
├── README.md                    # This file
├── docker-test.sh              # Docker deployment script
├── podman-test.sh              # Podman deployment script
├── run-lab.sh                  # Local development script
├── test-lab.sh                 # Testing script
├── starter/                    # Starting point
│   ├── pom.xml
│   ├── src/
│   │   └── main/
│   │       ├── java/com/bank/
│   │       │   ├── config/
│   │       │   ├── model/
│   │       │   ├── service/
│   │       │   └── web/
│   │       ├── resources/
│   │       └── webapp/
│   └── docker-compose.yml
└── solution/                   # Complete solution
    ├── pom.xml
    ├── Containerfile
    ├── docker-compose.yml
    └── src/
        └── main/
            ├── java/com/bank/
            │   ├── api/        # REST resources
            │   │   ├── ClientResource.java
            │   │   ├── AccountResource.java
            │   │   └── TransferResource.java
            │   ├── client/     # Rest Client
            │   │   └── BankingRestClient.java
            │   ├── dto/        # Data Transfer Objects
            │   │   ├── ErrorResponse.java
            │   │   └── TransferRequest.java
            │   ├── exception/  # Custom exceptions
            │   │   ├── NotFoundException.java
            │   │   ├── ValidationException.java
            │   │   └── mapper/
            │   │       ├── NotFoundExceptionMapper.java
            │   │       ├── ValidationExceptionMapper.java
            │   │       └── GenericExceptionMapper.java
            │   ├── config/
            │   ├── model/
            │   ├── service/
            │   └── web/
            ├── resources/
            └── webapp/
```

---

## Part 1: REST API Basics

### Step 1.1: Update pom.xml

Add JAX-RS and JSON-B dependencies to your `pom.xml`:

```xml
<dependencies>
    <!-- Existing dependencies -->
    
    <!-- JAX-RS (included in webProfile-10.0) -->
    <dependency>
        <groupId>jakarta.ws.rs</groupId>
        <artifactId>jakarta.ws.rs-api</artifactId>
        <version>3.1.0</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- JSON-B (included in webProfile-10.0) -->
    <dependency>
        <groupId>jakarta.json.bind</groupId>
        <artifactId>jakarta.json.bind-api</artifactId>
        <version>3.0.0</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- Bean Validation (included in webProfile-10.0) -->
    <dependency>
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
        <version>3.0.2</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- MicroProfile Rest Client -->
    <dependency>
        <groupId>org.eclipse.microprofile.rest.client</groupId>
        <artifactId>microprofile-rest-client-api</artifactId>
        <version>3.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

**Note:** These dependencies are already provided by Open Liberty's `webProfile-10.0` feature.

### Step 1.2: Create REST Application Class

Create `src/main/java/com/bank/api/RestApplication.java`:

```java
package com.bank.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS Application configuration.
 * All REST resources will be available under /api path.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // No need to override methods
    // JAX-RS will auto-discover all @Path annotated classes
}
```

**What this does:**
- Defines `/api` as the base path for all REST endpoints
- Enables automatic discovery of REST resources
- No manual registration needed

### Step 1.3: Verify server.xml

Ensure your `server.xml` includes the necessary features:

```xml
<featureManager>
    <feature>webProfile-10.0</feature>
    <feature>mpConfig-3.0</feature>
    <feature>mpHealth-4.0</feature>
    <feature>mpMetrics-5.0</feature>
    <feature>mpRestClient-3.0</feature>
</featureManager>
```

**Note:** `webProfile-10.0` includes JAX-RS, JSON-B, and Bean Validation.

### Step 1.4: Create Root API Endpoint (Best Practice)

**Why a Root Endpoint?**

When accessing the root of your API (`/api`), it's a best practice to return useful information rather than a 404 error. This follows REST principles and improves developer experience.

**Benefits:**
- ✅ **API Discovery** - Clients can explore available endpoints
- ✅ **HATEOAS** - Hypermedia As The Engine Of Application State
- ✅ **Self-Documentation** - The API describes itself
- ✅ **Better DX** - Clear entry point for developers

#### Create ApiInfo DTO

Create `src/main/java/com/bank/dto/ApiInfo.java`:

```java
package com.bank.dto;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API Information DTO for the root endpoint.
 * Provides metadata about the API, available endpoints, and useful links.
 * 
 * This follows REST best practices for API discovery and HATEOAS principles.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
public class ApiInfo {
    
    private String name;
    private String version;
    private String description;
    private Map<String, String> endpoints;
    private Map<String, String> links;
    private LocalDateTime timestamp;
    
    public ApiInfo() {
        this.timestamp = LocalDateTime.now();
        this.endpoints = new LinkedHashMap<>();
        this.links = new LinkedHashMap<>();
    }
    
    public ApiInfo(String name, String version, String description) {
        this();
        this.name = name;
        this.version = version;
        this.description = description;
    }
    
    // Getters, setters, and helper methods
    // ... (see solution for complete implementation)
    
    public ApiInfo addEndpoint(String name, String url) {
        this.endpoints.put(name, url);
        return this;
    }
    
    public ApiInfo addLink(String name, String url) {
        this.links.put(name, url);
        return this;
    }
}
```

#### Create Root Resource

Create `src/main/java/com/bank/api/RootResource.java`:

```java
package com.bank.api;

import com.bank.dto.ApiInfo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.logging.Logger;

/**
 * Root REST resource for API information.
 * 
 * This resource provides metadata about the API, including:
 * - API name, version, and description
 * - Available endpoints (API discovery)
 * - Useful links (OpenAPI, health checks, metrics)
 * 
 * This follows REST best practices:
 * - API Discovery: Clients can explore available endpoints
 * - HATEOAS: Hypermedia As The Engine Of Application State
 * - Self-documentation: The API describes itself
 * 
 * Base URL: /api
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RootResource {
    
    private static final Logger LOGGER = Logger.getLogger(RootResource.class.getName());
    
    /**
     * Get API information.
     * 
     * This endpoint provides metadata about the API and available resources.
     * It uses UriInfo to dynamically generate absolute URLs based on the
     * current request context.
     * 
     * @param uriInfo JAX-RS context for building URIs
     * @return Response containing ApiInfo with metadata and links
     */
    @GET
    public Response getApiInfo(@Context UriInfo uriInfo) {
        LOGGER.fine("API root endpoint accessed");
        
        // Get base URI (e.g., http://localhost:9080/api)
        String baseUri = uriInfo.getBaseUri().toString();
        if (baseUri.endsWith("/")) {
            baseUri = baseUri.substring(0, baseUri.length() - 1);
        }
        
        // Get root URI (e.g., http://localhost:9080)
        String absolutePath = uriInfo.getAbsolutePath().toString();
        String rootUri = absolutePath.substring(0, absolutePath.indexOf("/api"));
        
        // Build API information
        ApiInfo apiInfo = new ApiInfo(
            "Banking REST API",
            "1.0.0",
            "RESTful API for banking operations with Jakarta EE"
        );
        
        // Add available endpoints
        apiInfo.addEndpoint("clients", baseUri + "/clients")
               .addEndpoint("accounts", baseUri + "/accounts");
        
        // Add useful links
        apiInfo.addLink("openapi", rootUri + "/openapi")
               .addLink("openapi-ui", rootUri + "/openapi/ui")
               .addLink("health", rootUri + "/health")
               .addLink("health-ready", rootUri + "/health/ready")
               .addLink("health-live", rootUri + "/health/live")
               .addLink("metrics", rootUri + "/metrics");
        
        LOGGER.info("API info returned with " + apiInfo.getEndpoints().size() + 
                   " endpoints and " + apiInfo.getLinks().size() + " links");
        
        return Response.ok(apiInfo).build();
    }
}
```

#### Test Root Endpoint

```bash
# Test the root endpoint
curl http://localhost:9080/api | jq

# Expected response:
# {
#   "name": "Banking REST API",
#   "version": "1.0.0",
#   "description": "RESTful API for banking operations with Jakarta EE",
#   "endpoints": {
#     "clients": "http://localhost:9080/api/clients",
#     "accounts": "http://localhost:9080/api/accounts"
#   },
#   "links": {
#     "openapi": "http://localhost:9080/openapi",
#     "openapi-ui": "http://localhost:9080/openapi/ui",
#     "health": "http://localhost:9080/health",
#     "health-ready": "http://localhost:9080/health/ready",
#     "health-live": "http://localhost:9080/health/live",
#     "metrics": "http://localhost:9080/metrics"
#   },
#   "timestamp": "2026-01-24T17:00:00"
# }
```

**What this provides:**
- **API Discovery** - Clients can find all available endpoints
- **Dynamic URLs** - URLs are generated based on the request context
- **Documentation Links** - Direct links to OpenAPI, health checks, and metrics
- **Version Information** - API version for compatibility tracking
- **Timestamp** - When the information was generated

**Alternative Approaches Considered:**

1. ❌ **Return 404** - Bad developer experience, no information provided
2. ❌ **Redirect to documentation** - Loses programmatic access to endpoint list
3. ✅ **Return API information** - Best practice, follows HATEOAS principles

**Standards Followed:**
- RESTful API Design principles
- HATEOAS Level 2 (Richardson Maturity Model)
- OpenAPI/Swagger integration ready
- MicroProfile compatibility

---

---

## Part 2: Client REST Resources

### Step 2.1: Create Client Resource

Create `src/main/java/com/bank/api/ClientResource.java`:

```java
package com.bank.api;

import com.bank.model.Client;
import com.bank.service.ClientService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST resource for Client operations.
 * Provides CRUD operations via HTTP methods.
 * 
 * Base URL: /api/clients
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {
    
    @Inject
    private ClientService clientService;
    
    @Inject
    private Logger logger;
    
    /**
     * Get all clients.
     * 
     * @return List of all clients
     * 
     * Example:
     * GET /api/clients
     * 
     * Response: 200 OK
     * [
     *   {"id": 1, "name": "John Doe", "email": "john@example.com"},
     *   {"id": 2, "name": "Jane Smith", "email": "jane@example.com"}
     * ]
     */
    @GET
    public List<Client> getAllClients() {
        logger.info("REST: Getting all clients");
        return clientService.findAll();
    }
    
    /**
     * Get client by ID.
     * 
     * @param id Client ID
     * @return Client object
     * 
     * Example:
     * GET /api/clients/1
     * 
     * Response: 200 OK
     * {"id": 1, "name": "John Doe", "email": "john@example.com"}
     */
    @GET
    @Path("/{id}")
    public Response getClient(@PathParam("id") Long id) {
        logger.info("REST: Getting client with ID: " + id);
        
        Client client = clientService.findById(id);
        
        if (client == null) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity("Client with ID " + id + " not found")
                .build();
        }
        
        return Response.ok(client).build();
    }
    
    /**
     * Create new client.
     * 
     * @param client Client data
     * @return Created client
     * 
     * Example:
     * POST /api/clients
     * Content-Type: application/json
     * 
     * {"name": "John Doe", "email": "john@example.com"}
     * 
     * Response: 201 Created
     * {"id": 1, "name": "John Doe", "email": "john@example.com"}
     */
    @POST
    public Response createClient(Client client) {
        logger.info("REST: Creating client: " + client.getName());
        
        Client created = clientService.create(client);
        
        return Response
            .status(Response.Status.CREATED)
            .entity(created)
            .build();
    }
    
    /**
     * Update existing client.
     * 
     * @param id Client ID
     * @param client Updated client data
     * @return Updated client
     * 
     * Example:
     * PUT /api/clients/1
     * Content-Type: application/json
     * 
     * {"name": "John Updated", "email": "john.updated@example.com"}
     * 
     * Response: 200 OK
     * {"id": 1, "name": "John Updated", "email": "john.updated@example.com"}
     */
    @PUT
    @Path("/{id}")
    public Response updateClient(@PathParam("id") Long id, Client client) {
        logger.info("REST: Updating client with ID: " + id);
        
        Client existing = clientService.findById(id);
        
        if (existing == null) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity("Client with ID " + id + " not found")
                .build();
        }
        
        client.setId(id);
        Client updated = clientService.update(client);
        
        return Response.ok(updated).build();
    }
    
    /**
     * Delete client.
     * 
     * @param id Client ID
     * @return No content
     * 
     * Example:
     * DELETE /api/clients/1
     * 
     * Response: 204 No Content
     */
    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id) {
        logger.info("REST: Deleting client with ID: " + id);
        
        boolean deleted = clientService.delete(id);
        
        if (!deleted) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity("Client with ID " + id + " not found")
                .build();
        }
        
        return Response.noContent().build();
    }
    
    /**
     * Search clients by name.
     * 
     * @param name Name to search for
     * @return List of matching clients
     * 
     * Example:
     * GET /api/clients/search?name=John
     * 
     * Response: 200 OK
     * [{"id": 1, "name": "John Doe", "email": "john@example.com"}]
     */
    @GET
    @Path("/search")
    public List<Client> searchClients(@QueryParam("name") String name) {
        logger.info("REST: Searching clients by name: " + name);
        
        if (name == null || name.trim().isEmpty()) {
            return clientService.findAll();
        }
        
        return clientService.findByName(name);
    }
}
```

### Step 2.2: Test Client Resource

**Test with curl:**

```bash
# Get all clients
curl http://localhost:9080/api/clients

# Get client by ID
curl http://localhost:9080/api/clients/1

# Create client
curl -X POST http://localhost:9080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'

# Update client
curl -X PUT http://localhost:9080/api/clients/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"John Updated","email":"john.updated@example.com"}'

# Delete client
curl -X DELETE http://localhost:9080/api/clients/1

# Search clients
curl "http://localhost:9080/api/clients/search?name=John"
```

---

## Part 3: Account REST Resources

### Step 3.1: Create Account Resource

Create `src/main/java/com/bank/api/AccountResource.java`:

```java
package com.bank.api;

import com.bank.model.Account;
import com.bank.service.AccountService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST resource for Account operations.
 * 
 * Base URL: /api/accounts
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
    
    @Inject
    private AccountService accountService;
    
    @Inject
    private Logger logger;
    
    /**
     * Get all accounts.
     */
    @GET
    public List<Account> getAllAccounts() {
        logger.info("REST: Getting all accounts");
        return accountService.findAll();
    }
    
    /**
     * Get account by ID.
     */
    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") Long id) {
        logger.info("REST: Getting account with ID: " + id);
        
        Account account = accountService.findById(id);
        
        if (account == null) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity("Account with ID " + id + " not found")
                .build();
        }
        
        return Response.ok(account).build();
    }
    
    /**
     * Create new account.
     */
    @POST
    public Response createAccount(Account account) {
        logger.info("REST: Creating account: " + account.getNumber());
        
        Account created = accountService.create(account);
        
        return Response
            .status(Response.Status.CREATED)
            .entity(created)
            .build();
    }
    
    /**
     * Update existing account.
     */
    @PUT
    @Path("/{id}")
    public Response updateAccount(@PathParam("id") Long id, Account account) {
        logger.info("REST: Updating account with ID: " + id);
        
        Account existing = accountService.findById(id);
        
        if (existing == null) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity("Account with ID " + id + " not found")
                .build();
        }
        
        account.setId(id);
        Account updated = accountService.update(account);
        
        return Response.ok(updated).build();
    }
    
    /**
     * Delete account.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteAccount(@PathParam("id") Long id) {
        logger.info("REST: Deleting account with ID: " + id);
        
        boolean deleted = accountService.delete(id);
        
        if (!deleted) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity("Account with ID " + id + " not found")
                .build();
        }
        
        return Response.noContent().build();
    }
    
    /**
     * Get accounts for a specific client.
     */
    @GET
    @Path("/client/{clientId}")
    public List<Account> getClientAccounts(@PathParam("clientId") Long clientId) {
        logger.info("REST: Getting accounts for client ID: " + clientId);
        return accountService.findByClientId(clientId);
    }
}
```

### Step 3.2: Test Account Resource

```bash
# Get all accounts
curl http://localhost:9080/api/accounts

# Get account by ID
curl http://localhost:9080/api/accounts/1

# Create account
curl -X POST http://localhost:9080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"number":"ACC001","balance":1000.00,"type":"CHECKING","clientId":1}'

# Get client's accounts
curl http://localhost:9080/api/accounts/client/1
```

---

## Part 4: Exception Handling

### Step 4.1: Create Custom Exceptions

Create `src/main/java/com/bank/exception/NotFoundException.java`:

```java
package com.bank.exception;

/**
 * Exception thrown when a resource is not found.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String message) {
        super(message);
    }
    
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

Create `src/main/java/com/bank/exception/ValidationException.java`:

```java
package com.bank.exception;

import java.util.List;

/**
 * Exception thrown when validation fails.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
public class ValidationException extends RuntimeException {
    
    private List<String> errors;
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public List<String> getErrors() {
        return errors;
    }
}
```

### Step 4.2: Create Error Response DTO

Create `src/main/java/com/bank/dto/ErrorResponse.java`:

```java
package com.bank.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response format for REST API.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
public class ErrorResponse {
    
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private List<String> details;
    
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(int status, String error, String message) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
    }
    
    public ErrorResponse(int status, String error, String message, List<String> details) {
        this(status, error, message);
        this.details = details;
    }
    
    // Getters and setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
}
```

### Step 4.3: Create Exception Mappers

Create `src/main/java/com/bank/exception/mapper/NotFoundExceptionMapper.java`:

```java
package com.bank.exception.mapper;

import com.bank.dto.ErrorResponse;
import com.bank.exception.NotFoundException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps NotFoundException to HTTP 404 response.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    
    @Override
    public Response toResponse(NotFoundException exception) {
        ErrorResponse error = new ErrorResponse(
            404,
            "Not Found",
            exception.getMessage()
        );
        
        return Response
            .status(Response.Status.NOT_FOUND)
            .entity(error)
            .build();
    }
}
```

Create `src/main/java/com/bank/exception/mapper/ValidationExceptionMapper.java`:

```java
package com.bank.exception.mapper;

import com.bank.dto.ErrorResponse;
import com.bank.exception.ValidationException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps ValidationException to HTTP 400 response.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    
    @Override
    public Response toResponse(ValidationException exception) {
        ErrorResponse error = new ErrorResponse(
            400,
            "Validation Failed",
            exception.getMessage(),
            exception.getErrors()
        );
        
        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(error)
            .build();
    }
}
```

Create `src/main/java/com/bank/exception/mapper/GenericExceptionMapper.java`:

```java
package com.bank.exception.mapper;

import com.bank.dto.ErrorResponse;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Maps all uncaught exceptions to HTTP 500 response.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    
    @Inject
    private Logger logger;
    
    @Override
    public Response toResponse(Exception exception) {
        logger.severe("Unexpected error: " + exception.getMessage());
        exception.printStackTrace();
        
        ErrorResponse error = new ErrorResponse(
            500,
            "Internal Server Error",
            "An unexpected error occurred. Please contact support."
        );
        
        return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(error)
            .build();
    }
}
```

### Step 4.4: Update Resources to Use Custom Exceptions

Update `ClientResource.java`:

```java
@GET
@Path("/{id}")
public Client getClient(@PathParam("id") Long id) {
    logger.info("REST: Getting client with ID: " + id);
    
    Client client = clientService.findById(id);
    
    if (client == null) {
        throw new NotFoundException("Client with ID " + id + " not found");
    }
    
    return client;
}
```

---

## Part 5: Validation

### Step 5.1: Add Validation Annotations to Models

Update `Client.java`:

```java
package com.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "is_premium", nullable = false)
    private boolean premium = false;
    
    // ... rest of the class
}
```

Update `Account.java`:

```java
package com.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Account number is required")
    @Size(min = 5, max = 20, message = "Account number must be between 5 and 20 characters")
    @Column(nullable = false, unique = true, length = 20)
    private String number;
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", message = "Balance cannot be negative")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    
    @NotNull(message = "Account type is required")
    @Column(nullable = false, length = 20)
    private String type;
    
    // ... rest of the class
}
```

### Step 5.2: Enable Validation in Resources

Update `ClientResource.java`:

```java
import jakarta.validation.Valid;

@POST
public Response createClient(@Valid Client client) {
    logger.info("REST: Creating client: " + client.getName());
    
    Client created = clientService.create(client);
    
    return Response
        .status(Response.Status.CREATED)
        .entity(created)
        .build();
}

@PUT
@Path("/{id}")
public Response updateClient(@PathParam("id") Long id, @Valid Client client) {
    logger.info("REST: Updating client with ID: " + id);
    
    Client existing = clientService.findById(id);
    
    if (existing == null) {
        throw new NotFoundException("Client with ID " + id + " not found");
    }
    
    client.setId(id);
    Client updated = clientService.update(client);
    
    return Response.ok(updated).build();
}
```

### Step 5.3: Create Constraint Violation Exception Mapper

Create `src/main/java/com/bank/exception/mapper/ConstraintViolationExceptionMapper.java`:

```java
package com.bank.exception.mapper;

import com.bank.dto.ErrorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps ConstraintViolationException to HTTP 400 response.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Provider
public class ConstraintViolationExceptionMapper 
    implements ExceptionMapper<ConstraintViolationException> {
    
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.toList());
        
        ErrorResponse error = new ErrorResponse(
            400,
            "Validation Failed",
            "Input validation failed",
            errors
        );
        
        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(error)
            .build();
    }
}
```

### Step 5.4: Test Validation

```bash
# Try to create client with invalid data
curl -X POST http://localhost:9080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"name":"J","email":"invalid-email"}'

# Expected response: 400 Bad Request
# {
#   "status": 400,
#   "error": "Validation Failed",
#   "message": "Input validation failed",
#   "details": [
#     "name: Name must be between 2 and 100 characters",
#     "email: Invalid email format"
#   ],
#   "timestamp": "2025-01-01T10:30:00"
# }
```

---

## Part 6: MicroProfile Rest Client

### Step 6.1: Create Rest Client Interface

Create `src/main/java/com/bank/client/BankingRestClient.java`:

```java
package com.bank.client;

import com.bank.model.Client;
import com.bank.model.Account;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Type-safe REST client for Banking API.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Path("/api")
@RegisterRestClient(configKey = "banking-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BankingRestClient {
    
    // Client operations
    
    @GET
    @Path("/clients")
    List<Client> getAllClients();
    
    @GET
    @Path("/clients/{id}")
    Client getClient(@PathParam("id") Long id);
    
    @POST
    @Path("/clients")
    Response createClient(Client client);
    
    @PUT
    @Path("/clients/{id}")
    Response updateClient(@PathParam("id") Long id, Client client);
    
    @DELETE
    @Path("/clients/{id}")
    Response deleteClient(@PathParam("id") Long id);
    
    // Account operations
    
    @GET
    @Path("/accounts")
    List<Account> getAllAccounts();
    
    @GET
    @Path("/accounts/{id}")
    Account getAccount(@PathParam("id") Long id);
    
    @POST
    @Path("/accounts")
    Response createAccount(Account account);
    
    @GET
    @Path("/accounts/client/{clientId}")
    List<Account> getClientAccounts(@PathParam("clientId") Long clientId);
}
```

### Step 6.2: Configure Rest Client

Add to `microprofile-config.properties`:

```properties
# Banking REST Client Configuration
banking-api/mp-rest/url=http://localhost:9080
banking-api/mp-rest/scope=jakarta.inject.Singleton
banking-api/mp-rest/connectTimeout=5000
banking-api/mp-rest/readTimeout=10000
```

### Step 6.3: Use Rest Client with CDI

Create a test resource to demonstrate usage:

```java
package com.bank.api;

import com.bank.client.BankingRestClient;
import com.bank.model.Client;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/**
 * Test resource demonstrating Rest Client usage.
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {
    
    @Inject
    @RestClient
    private BankingRestClient bankingClient;
    
    @GET
    @Path("/clients")
    public List<Client> testGetClients() {
        // This calls the REST API using the type-safe client
        return bankingClient.getAllClients();
    }
}
```

---

## Part 7: Testing

### Step 7.1: Manual Testing with curl

Create a test script `test-rest-api.sh`:

```bash
#!/bin/bash
# REST API Testing Script

BASE_URL="http://localhost:9080/api"

echo "=========================================="
echo "Banking REST API Tests"
echo "=========================================="
echo ""

# Test 1: Get all clients
echo "Test 1: GET /api/clients"
curl -s $BASE_URL/clients | jq .
echo ""

# Test 2: Create client
echo "Test 2: POST /api/clients"
curl -s -X POST $BASE_URL/clients \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Client","email":"test@example.com"}' | jq .
echo ""

# Test 3: Get client by ID
echo "Test 3: GET /api/clients/1"
curl -s $BASE_URL/clients/1 | jq .
echo ""

# Test 4: Update client
echo "Test 4: PUT /api/clients/1"
curl -s -X PUT $BASE_URL/clients/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Client","email":"updated@example.com"}' | jq .
echo ""

# Test 5: Search clients
echo "Test 5: GET /api/clients/search?name=Test"
curl -s "$BASE_URL/clients/search?name=Test" | jq .
echo ""

# Test 6: Get all accounts
echo "Test 6: GET /api/accounts"
curl -s $BASE_URL/accounts | jq .
echo ""

# Test 7: Create account
echo "Test 7: POST /api/accounts"
curl -s -X POST $BASE_URL/accounts \
  -H "Content-Type: application/json" \
  -d '{"number":"ACC001","balance":1000.00,"type":"CHECKING","clientId":1}' | jq .
echo ""

# Test 8: Get client's accounts
echo "Test 8: GET /api/accounts/client/1"
curl -s $BASE_URL/accounts/client/1 | jq .
echo ""

# Test 9: Test validation (should fail)
echo "Test 9: POST /api/clients (invalid data)"
curl -s -X POST $BASE_URL/clients \
  -H "Content-Type: application/json" \
  -d '{"name":"J","email":"invalid"}' | jq .
echo ""

# Test 10: Test not found (should return 404)
echo "Test 10: GET /api/clients/999"
curl -s $BASE_URL/clients/999 | jq .
echo ""

echo "=========================================="
echo "Tests completed!"
echo "=========================================="
```

Make it executable:

```bash
chmod +x test-rest-api.sh
```

### Step 7.2: Testing with Postman

1. **Import Collection:**
   - Create a Postman collection
   - Add requests for all endpoints
   - Save as `Banking-API.postman_collection.json`

2. **Example Requests:**

**Get All Clients:**
```
GET http://localhost:9080/api/clients
```

**Create Client:**
```
POST http://localhost:9080/api/clients
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "premium": true
}
```

**Update Client:**
```
PUT http://localhost:9080/api/clients/1
Content-Type: application/json

{
  "name": "John Updated",
  "email": "john.updated@example.com",
  "premium": false
}
```

---

## Deployment

### Option 1: Podman Deployment

```bash
cd solution
./podman-test.sh
```

This script will:
1. Start PostgreSQL with docker-compose
2. Build the application with Maven
3. Build Podman image
4. Start container with Open Liberty
5. Run automated tests

### Option 2: Docker Deployment

```bash
cd solution
./docker-test.sh
```

### Option 3: Local Development

```bash
cd solution
./run-lab.sh
```

This starts Liberty in dev mode with hot reload.

---

## Testing Guide

### Automated Testing

Run the complete test suite:

```bash
cd solution
./test-lab.sh
```

### Manual Testing

1. **Start the application:**
   ```bash
   ./podman-test.sh
   ```

2. **Test with curl:**
   ```bash
   ./test-rest-api.sh
   ```

3. **Test with browser:**
   - Web UI: http://localhost:9080/
   - REST API: http://localhost:9080/api/clients

4. **Check health:**
   ```bash
   curl http://localhost:9080/health
   ```

5. **Check metrics:**
   ```bash
   curl http://localhost:9080/metrics
   ```

---

## Troubleshooting

### Common Issues

**1. Port Already in Use**
```
Error: Port 9080 is already in use
```
**Solution:**
```bash
# Stop existing containers
podman stop banking-rest-lab05
podman rm banking-rest-lab05
```

**2. Database Connection Failed**
```
Error: Could not connect to database
```
**Solution:**
```bash
# Check PostgreSQL is running
docker ps | grep banking-db

# Restart PostgreSQL
docker-compose down
docker-compose up -d
```

**3. JSON Parsing Error**
```
Error: Unexpected character at position 0
```
**Solution:**
- Check Content-Type header is `application/json`
- Verify JSON syntax is valid
- Use `jq` to validate: `echo '{"test":"value"}' | jq .`

**4. 404 Not Found**
```
Error: HTTP 404 - Not Found
```
**Solution:**
- Verify URL path: `/api/clients` not `/clients`
- Check application is deployed
- Review server logs

**5. Validation Errors**
```
Error: Validation Failed
```
**Solution:**
- Check required fields are provided
- Verify email format
- Ensure name length is 2-100 characters

### Debugging Tips

1. **Check server logs:**
   ```bash
   podman logs -f banking-rest-lab05
   ```

2. **Verify REST endpoints:**
   ```bash
   curl http://localhost:9080/api/clients
   ```

3. **Test database connection:**
   ```bash
   docker exec banking-db psql -U bankuser -d bankdb -c "SELECT * FROM clients;"
   ```

4. **Check application health:**
   ```bash
   curl http://localhost:9080/health
   ```

---

## Additional Challenges

### Challenge 1: Add Transfer Endpoint

Create a transfer endpoint that moves money between accounts:

```java
@POST
@Path("/transfer")
public Response transfer(TransferRequest request) {
    // Implement transfer logic
    // Validate accounts exist
    // Check sufficient balance
    // Update both accounts
    // Return transfer confirmation
}
```

### Challenge 2: Add Pagination

Add pagination support to list endpoints:

```java
@GET
public Response getAllClients(
    @QueryParam("page") @DefaultValue("0") int page,
    @QueryParam("size") @DefaultValue("10") int size) {
    // Implement pagination
    // Return paginated results with metadata
}
```

### Challenge 3: Add HATEOAS Links

Add hypermedia links to responses:

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "_links": {
    "self": "/api/clients/1",
    "accounts": "/api/clients/1/accounts",
    "update": "/api/clients/1",
    "delete": "/api/clients/1"
  }
}
```

### Challenge 4: Add Filtering

Add advanced filtering to search:

```java
@GET
@Path("/search")
public List<Client> searchClients(
    @QueryParam("name") String name,
    @QueryParam("email") String email,
    @QueryParam("premium") Boolean premium,
    @QueryParam("minAccounts") Integer minAccounts) {
    // Implement complex filtering
}
```

### Challenge 5: Add API Versioning

Implement API versioning:

```java
@Path("/v1/clients")
public class ClientResourceV1 { }

@Path("/v2/clients")
public class ClientResourceV2 { }
```

---

## Summary

In this lab, you have:

✅ Created a complete REST API with JAX-RS
✅ Implemented JSON-B for automatic JSON conversion
✅ Added exception handling with custom mappers
✅ Implemented Bean Validation for input validation
✅ Created a type-safe Rest Client with MicroProfile
✅ Integrated REST API with existing CDI services
✅ Tested the API with curl and automated scripts

### Key Takeaways

1. **JAX-RS** makes REST API development simple with annotations
2. **JSON-B** handles JSON conversion automatically
3. **Exception mappers** provide consistent error responses
4. **Bean Validation** ensures data integrity
5. **MicroProfile Rest Client** provides type-safe clients
6. **CDI integration** allows sharing services between web UI and REST API

### Next Steps

- Complete Lab 6: Microservices Architecture
- Explore MicroProfile OpenAPI for API documentation
- Learn about API security with JWT
- Study distributed tracing with MicroProfile

---

## Resources

### Documentation
- [Jakarta RESTful Web Services](https://jakarta.ee/specifications/restful-ws/)
- [Jakarta JSON Binding](https://jakarta.ee/specifications/jsonb/)
- [MicroProfile Rest Client](https://microprofile.io/project/eclipse/microprofile-rest-client)
- [Bean Validation](https://jakarta.ee/specifications/bean-validation/)

### Tools
- [curl](https://curl.se/)
- [Postman](https://www.postman.com/)
- [jq](https://stedolan.github.io/jq/) - JSON processor

### Books
- "RESTful Web Services" by Leonard Richardson
- "REST API Design Rulebook" by Mark Masse

---

**© 2025 ESIPE - All Rights Reserved**

**Lab 5: JAX-RS and RESTful Web Services**