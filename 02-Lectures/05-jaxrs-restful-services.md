<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

---
marp: true
theme: default
paginate: true
header: 'Jakarta EE - JAX-RS and RESTful Web Services'
footer: '© 2025 ESIPE - All Rights Reserved'
---

<!-- 
Lecture 5: JAX-RS and RESTful Web Services
Course: Jakarta EE and Microservices
Author: Course Development Team
Version: 1.0
Last Updated: January 2025
-->

# Lecture 5: JAX-RS and RESTful Web Services

## Jakarta EE and Microservices Course

**Session 5 of 8**

---

## 📋 Lecture Outline

1. **REST Principles and Architecture** (30 min)
2. **JAX-RS Annotations and Resources** (45 min)
3. **JSON-B for JSON Processing** (30 min)
4. **Exception Handling and Validation** (30 min)
5. **MicroProfile Rest Client** (30 min)
6. **CDI and REST Client Integration** (15 min)
7. **Lab 5 Preview** (10 min)

**Total Duration:** 3 hours

---

## 🎯 Learning Objectives

By the end of this lecture, you will be able to:

- ✅ Understand REST architectural principles
- ✅ Create RESTful resources with JAX-RS
- ✅ Handle JSON with JSON-B
- ✅ Implement exception handling and validation
- ✅ Use MicroProfile Rest Client
- ✅ Integrate REST with CDI

---

## 📚 Prerequisites

Before starting this lecture, you should:

- ✅ Understand HTTP protocol basics
- ✅ Be familiar with CDI (Lecture 4)
- ✅ Know JPA and database operations (Lecture 3)
- ✅ Have completed Labs 1-4

---

# Part 1: REST Principles and Architecture

## Understanding RESTful Services

---

## What is REST?

**REST** = **RE**presentational **S**tate **T**ransfer

- Architectural style for distributed systems
- Introduced by Roy Fielding in 2000
- Based on HTTP protocol
- Stateless client-server communication
- Resource-oriented architecture

**Key Concept:** Everything is a resource with a unique identifier (URI)

---

## REST Architectural Constraints

1. **Client-Server Architecture**
   - Separation of concerns
   - Independent evolution

2. **Stateless**
   - Each request contains all necessary information
   - No session state on server

3. **Cacheable**
   - Responses must define themselves as cacheable or not

---

## REST Architectural Constraints (cont.)


---

## Richardson Maturity Model

**Leonard Richardson's REST Maturity Model** (2008)

A model to evaluate how RESTful an API is, with 4 levels:

- **Level 0:** The Swamp of POX (Plain Old XML)
- **Level 1:** Resources
- **Level 2:** HTTP Verbs
- **Level 3:** Hypermedia Controls (HATEOAS)

**Goal:** Understand the progression toward true REST

---

## Level 0: The Swamp of POX

**Single endpoint, single HTTP method (usually POST)**

```
POST /api/service
{
  "action": "getClient",
  "id": 123
}

POST /api/service
{
  "action": "createClient",
  "name": "John Doe"
}
```

**Problems:**
- ❌ No resource identification
- ❌ No HTTP semantics
- ❌ RPC-style (Remote Procedure Call)
- ❌ Not RESTful at all

---

## Level 1: Resources

**Multiple endpoints, but still single HTTP method**

```
POST /api/clients/123
{
  "action": "get"
}

POST /api/clients
{
  "action": "create",
  "name": "John Doe"
}
```

**Improvements:**
- ✅ Resources identified by URIs
- ✅ Better organization

**Still Missing:**
- ❌ Proper HTTP verb usage
- ❌ HTTP status codes

---

## Level 2: HTTP Verbs

**Multiple endpoints + proper HTTP methods**

```
GET    /api/clients/123      → 200 OK
POST   /api/clients          → 201 Created
PUT    /api/clients/123      → 200 OK
DELETE /api/clients/123      → 204 No Content
```

**Improvements:**
- ✅ Proper HTTP verbs (GET, POST, PUT, DELETE)
- ✅ Meaningful HTTP status codes
- ✅ Idempotency and safety properties
- ✅ **This is what most call "RESTful"**

**Still Missing:**
- ❌ Hypermedia controls (HATEOAS)

---

## Level 3: Hypermedia Controls (HATEOAS)

**HATEOAS** = **H**ypermedia **A**s **T**he **E**ngine **O**f **A**pplication **S**tate

```json
GET /api/clients/123

{
  "id": 123,
  "name": "John Doe",
  "email": "john@example.com",
  "_links": {
    "self": { "href": "/api/clients/123" },
    "accounts": { "href": "/api/clients/123/accounts" },
    "update": { "href": "/api/clients/123", "method": "PUT" },
    "delete": { "href": "/api/clients/123", "method": "DELETE" }
  }
}
```

**Benefits:**
- ✅ Self-documenting API
- ✅ Client discovers available actions
- ✅ Loose coupling
- ✅ **True REST according to Roy Fielding**

---

## Richardson Model: Visual Summary

```
Level 3: HATEOAS
    ↑ Hypermedia controls
    │ Self-documenting
    │
Level 2: HTTP Verbs ← Most APIs stop here
    ↑ GET, POST, PUT, DELETE
    │ Status codes (200, 201, 404, etc.)
    │
Level 1: Resources
    ↑ Multiple URIs
    │ Resource identification
    │
Level 0: POX
    Single endpoint
    RPC-style
```

**Most production APIs:** Level 2
**True REST:** Level 3

---

## Richardson Model: Banking Example

**Level 0 (POX):**
```
POST /api/service
{ "action": "getClient", "id": 1 }
```

**Level 1 (Resources):**
```
POST /api/clients/1
{ "action": "get" }
```

**Level 2 (HTTP Verbs):**
```
GET /api/clients/1
→ 200 OK
```

**Level 3 (HATEOAS):**
```json
GET /api/clients/1
{
  "id": 1,
  "name": "John",
  "_links": {
    "accounts": "/api/clients/1/accounts"
  }
}
```

---

## Which Level Should You Target?

**Level 2 (HTTP Verbs)** is the **practical standard**:
- ✅ Good enough for most applications
- ✅ Easy to implement and understand
- ✅ Widely adopted
- ✅ What JAX-RS naturally supports

**Level 3 (HATEOAS)** is **ideal but complex**:
- ✅ True REST
- ❌ More complex to implement
- ❌ Requires client sophistication
- ❌ Less common in practice

**Our Lab 5:** We'll implement **Level 2** (standard RESTful API)

---

## REST Architectural Constraints (cont.)

4. **Uniform Interface**
4. **Uniform Interface**
   - Resource identification (URIs)
   - Resource manipulation through representations
   - Self-descriptive messages
   - HATEOAS (Hypermedia as the Engine of Application State)

5. **Layered System**
   - Client cannot tell if connected directly to end server

6. **Code on Demand** (optional)
   - Server can extend client functionality

---

## HTTP Methods and CRUD

| HTTP Method | CRUD Operation | Idempotent | Safe |
|-------------|----------------|------------|------|
| GET         | Read           | ✅ Yes     | ✅ Yes |
| POST        | Create         | ❌ No      | ❌ No  |
| PUT         | Update/Replace | ✅ Yes     | ❌ No  |
| PATCH       | Update/Modify  | ❌ No      | ❌ No  |
| DELETE      | Delete         | ✅ Yes     | ❌ No  |

**Idempotent:** Multiple identical requests have the same effect as a single request
**Safe:** Does not modify resource state

---

## HTTP Status Codes

### Success (2xx)
- **200 OK** - Request succeeded
- **201 Created** - Resource created
- **204 No Content** - Success, no response body

### Client Errors (4xx)
- **400 Bad Request** - Invalid request
- **404 Not Found** - Resource not found
- **409 Conflict** - Conflict with current state

### Server Errors (5xx)
- **500 Internal Server Error** - Server error
- **503 Service Unavailable** - Server temporarily unavailable

---

## RESTful Resource Design

**Good URI Design:**
```
✅ GET    /api/clients           - List all clients
✅ GET    /api/clients/123       - Get client 123
✅ POST   /api/clients           - Create new client
✅ PUT    /api/clients/123       - Update client 123
✅ DELETE /api/clients/123       - Delete client 123
✅ GET    /api/clients/123/accounts - Get accounts for client 123
```

**Bad URI Design:**
```
❌ GET /api/getClients
❌ POST /api/createClient
❌ GET /api/client?action=delete&id=123
```

---

## REST vs SOAP

| Aspect | REST | SOAP |
|--------|------|------|
| Protocol | HTTP | HTTP, SMTP, etc. |
| Message Format | JSON, XML | XML only |
| Complexity | Simple | Complex |
| Performance | Faster | Slower |
| Caching | Yes | No |
| State | Stateless | Can be stateful |
| Use Case | Web/Mobile APIs | Enterprise integration |

---

## REST Best Practices

1. **Use nouns, not verbs** in URIs
   - ✅ `/clients` not `/getClients`

2. **Use plural nouns** for collections
   - ✅ `/clients` not `/client`

3. **Use HTTP methods** correctly
   - GET for reading, POST for creating, etc.

4. **Version your API**
   - `/api/v1/clients`

5. **Use proper status codes**
   - 200, 201, 404, 500, etc.

---

## REST Best Practices (cont.)

6. **Provide filtering, sorting, pagination**
   ```
   GET /api/clients?status=active&sort=name&page=2&size=20
   ```

7. **Use HATEOAS** for discoverability
   ```json
   {
     "id": 123,
     "name": "John Doe",
     "_links": {
       "self": "/api/clients/123",
       "accounts": "/api/clients/123/accounts"
     }
   }
   ```

8. **Handle errors consistently**
   - Use standard error response format

---

# Part 2: JAX-RS Annotations and Resources

## Building RESTful Services with Jakarta

---

## What is JAX-RS?

**JAX-RS** = **J**akarta RESTful Web Services

- Standard API for building REST services in Java
- Part of Jakarta EE specification
- Annotation-based programming model
- Supports JSON and XML
- Integrated with CDI

**Current Version:** JAX-RS 3.1 (Jakarta EE 10)

---

## JAX-RS Key Annotations

### Resource Definition
- `@Path` - Defines URI path
- `@GET`, `@POST`, `@PUT`, `@DELETE` - HTTP methods
- `@Produces` - Response media type
- `@Consumes` - Request media type

### Parameters
- `@PathParam` - URI path parameter
- `@QueryParam` - Query string parameter
- `@HeaderParam` - HTTP header parameter
- `@FormParam` - Form parameter

---

## Simple JAX-RS Resource

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello() {
        return "Hello, REST!";
    }
    
    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHelloTo(@PathParam("name") String name) {
        return "Hello, " + name + "!";
    }
}
```

**Access:** `GET http://localhost:9080/api/hello/John`

---

## JAX-RS Application Configuration

```java
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
    // No need to override methods
    // JAX-RS will auto-discover @Path annotated classes
}
```

**Result:** All resources will be available under `/api` prefix

---

## Complete Client Resource Example

```java
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {
    
    @Inject
    private ClientService clientService;
    
    @GET
    public List<Client> getAllClients() {
        return clientService.findAll();
    }
    
    @GET
    @Path("/{id}")
    public Client getClient(@PathParam("id") Long id) {
        return clientService.findById(id);
    }
}
```

---

## Complete Client Resource (cont.)

```java
    @POST
    public Response createClient(Client client) {
        Client created = clientService.create(client);
        return Response
            .status(Response.Status.CREATED)
            .entity(created)
            .build();
    }
    
    @PUT
    @Path("/{id}")
    public Client updateClient(@PathParam("id") Long id, 
                               Client client) {
        client.setId(id);
        return clientService.update(client);
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id) {
        clientService.delete(id);
        return Response.noContent().build();
    }
```

---

## Query Parameters Example

```java
@GET
@Path("/search")
public List<Client> searchClients(
    @QueryParam("name") String name,
    @QueryParam("email") String email,
    @QueryParam("premium") @DefaultValue("false") boolean premium) {
    
    if (name != null) {
        return clientService.findByName(name);
    }
    if (email != null) {
        return clientService.findByEmail(email);
    }
    if (premium) {
        return clientService.findPremiumClients();
    }
    return clientService.findAll();
}
```

**Usage:** `GET /api/clients/search?name=John&premium=true`

---

## Response Building

```java
@GET
@Path("/{id}")
public Response getClient(@PathParam("id") Long id) {
    Client client = clientService.findById(id);
    
    if (client == null) {
        return Response
            .status(Response.Status.NOT_FOUND)
            .entity("Client not found")
            .build();
    }
    
    return Response
        .ok(client)
        .header("X-Custom-Header", "value")
        .build();
}
```

---

## Sub-Resources

```java
@Path("/clients")
public class ClientResource {
    
    @Inject
    private AccountService accountService;
    
    @GET
    @Path("/{clientId}/accounts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getClientAccounts(
        @PathParam("clientId") Long clientId) {
        
        return accountService.findByClientId(clientId);
    }
    
    @POST
    @Path("/{clientId}/accounts")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(
        @PathParam("clientId") Long clientId, 
        Account account) {
        
        account.setClientId(clientId);
        Account created = accountService.create(account);
        return Response.status(201).entity(created).build();
    }
}
```

---

# Part 3: JSON-B for JSON Processing

## Jakarta JSON Binding

---

## What is JSON-B?

**JSON-B** = **JSON** **B**inding

- Standard API for JSON processing in Java
- Part of Jakarta EE specification
- Automatic Java ↔ JSON conversion
- Annotation-based customization
- Works seamlessly with JAX-RS

**Current Version:** JSON-B 3.0 (Jakarta EE 10)

---

## JSON-B Features

1. **Serialization** - Java object → JSON
2. **Deserialization** - JSON → Java object
3. **Customization** - Control JSON format
4. **Type Adapters** - Custom conversions
5. **Property Naming** - Naming strategies
6. **Date/Time Handling** - Built-in support

---

## Basic JSON-B Usage

```java
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class JsonExample {
    
    public static void main(String[] args) {
        Jsonb jsonb = JsonbBuilder.create();
        
        // Java to JSON (Serialization)
        Client client = new Client("John Doe", "john@example.com");
        String json = jsonb.toJson(client);
        System.out.println(json);
        // {"id":null,"name":"John Doe","email":"john@example.com"}
        
        // JSON to Java (Deserialization)
        Client fromJson = jsonb.fromJson(json, Client.class);
        System.out.println(fromJson.getName()); // John Doe
    }
}
```

---

## JSON-B Annotations

### Property Customization
```java
import jakarta.json.bind.annotation.*;

public class Client {
    
    @JsonbProperty("client_id")
    private Long id;
    
    @JsonbProperty("full_name")
    private String name;
    
    @JsonbTransient  // Exclude from JSON
    private String password;
    
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate birthDate;
    
    // getters/setters
}
```

---

## JSON-B Naming Strategies

```java
import jakarta.json.bind.config.PropertyNamingStrategy;

// Configure globally
JsonbConfig config = new JsonbConfig()
    .withPropertyNamingStrategy(
        PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES
    );
Jsonb jsonb = JsonbBuilder.create(config);

// Result: "full_name" instead of "fullName"
```

**Available Strategies:**
- `IDENTITY` - Keep as is
- `LOWER_CASE_WITH_DASHES` - `full-name`
- `LOWER_CASE_WITH_UNDERSCORES` - `full_name`
- `UPPER_CAMEL_CASE` - `FullName`
- `CASE_INSENSITIVE` - Case insensitive

---

## JSON-B with JAX-RS

**Automatic Integration:**

```java
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {
    
    @POST
    public Client createClient(Client client) {
        // JSON automatically converted to Client object
        // Return value automatically converted to JSON
        return clientService.create(client);
    }
}
```

**Request:**
```json
POST /api/clients
Content-Type: application/json

{"name": "John Doe", "email": "john@example.com"}
```

---

## Custom JSON-B Adapters

```java
import jakarta.json.bind.adapter.JsonbAdapter;

public class MoneyAdapter implements JsonbAdapter<BigDecimal, String> {
    
    @Override
    public String adaptToJson(BigDecimal money) {
        return "$" + money.setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public BigDecimal adaptFromJson(String json) {
        return new BigDecimal(json.replace("$", ""));
    }
}

// Usage in entity
public class Account {
    @JsonbTypeAdapter(MoneyAdapter.class)
    private BigDecimal balance;
}
```

---

## JSON-B Collections

```java
// List serialization
List<Client> clients = clientService.findAll();
String json = jsonb.toJson(clients);

// List deserialization
Type listType = new ArrayList<Client>(){}.getClass()
    .getGenericSuperclass();
List<Client> fromJson = jsonb.fromJson(json, listType);

// Map serialization
Map<String, Client> clientMap = new HashMap<>();
String mapJson = jsonb.toJson(clientMap);
```

---

## Handling Circular References with JSON-B

**Common Problem:** JPA entities with bidirectional relationships

```java
@Entity
public class Client {
    @OneToMany(mappedBy = "client")
    private List<Account> accounts;  // Client → Account
}

@Entity
public class Account {
    @ManyToOne
    private Client client;  // Account → Client
}
```

**Issue:** Infinite loop during JSON serialization
- Client → Accounts → Client → Accounts → ...

---

## Circular Reference Solutions

### Solution 1: Use `@JsonbTransient` (Recommended)

```java
@Entity
public class Account {
    
    @JsonbTransient  // Exclude from JSON serialization
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;
    
    // Helper method for JSON (returns ID only)
    public Long getClientId() {
        return client != null ? client.getId() : null;
    }
}
```

**Result:** Account JSON includes `clientId` but not full `client` object

---

## Circular Reference Solutions (cont.)

### Solution 2: Use DTOs (Data Transfer Objects)

```java
public class AccountDTO {
    private Long id;
    private String number;
    private double balance;
    private Long clientId;  // Only ID, not full object
    
    public static AccountDTO fromEntity(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setNumber(account.getNumber());
        dto.setBalance(account.getBalance());
        dto.setClientId(account.getClientId());
        return dto;
    }
}
```

---

## Circular Reference Solutions (cont.)

### Solution 3: Use `@JsonbPropertyOrder` with lazy loading

```java
@Entity
public class Client {
    
    @JsonbPropertyOrder({"id", "name", "email"})
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Account> accounts;
}
```

**Note:** Only works if accounts are not loaded when serializing

---

## Best Practices for Entity Serialization

1. **Use `@JsonbTransient`** on the "many" side of relationships
   - Mark `Account.client` as transient
   - Keep `Client.accounts` serializable

2. **Provide helper methods** for IDs
   ```java
   public Long getClientId() {
       return client != null ? client.getId() : null;
   }
   ```

3. **Use DTOs** for complex scenarios
   - Separate API models from database entities
   - Better control over JSON structure

4. **Configure fetch type** appropriately
   - Use `FetchType.LAZY` to avoid loading unnecessary data

---

# Part 4: Exception Handling and Validation

## Robust REST APIs

---

## Exception Handling in JAX-RS

**Problem:** Unhandled exceptions return generic 500 errors

**Solution:** Custom exception mappers

```java
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper 
    implements ExceptionMapper<NotFoundException> {
    
    @Override
    public Response toResponse(NotFoundException exception) {
        ErrorResponse error = new ErrorResponse(
            404,
            "Resource not found",
            exception.getMessage()
        );
        return Response.status(404).entity(error).build();
    }
}
```

---

## Custom Exception Classes

```java
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

public class ValidationException extends RuntimeException {
    private List<String> errors;
    
    public ValidationException(List<String> errors) {
        super("Validation failed");
        this.errors = errors;
    }
    
    public List<String> getErrors() {
        return errors;
    }
}
```

---

## Error Response DTO

```java
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    
    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    // getters/setters
}
```

**JSON Output:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Client with ID 999 not found",
  "timestamp": "2025-01-01T10:30:00"
}
```

---

## Using Custom Exceptions

```java
@Path("/clients")
public class ClientResource {
    
    @GET
    @Path("/{id}")
    public Client getClient(@PathParam("id") Long id) {
        Client client = clientService.findById(id);
        
        if (client == null) {
            throw new NotFoundException(
                "Client with ID " + id + " not found"
            );
        }
        
        return client;
    }
}
```

**Result:** Automatic conversion to proper error response

---

## Bean Validation (Jakarta Validation)

```java
import jakarta.validation.constraints.*;

public class Client {
    
    @NotNull(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    private String name;
    
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", 
             message = "Invalid phone number")
    private String phone;
    
    // getters/setters
}
```

---

## Validation in JAX-RS

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Path("/clients")
public class ClientResource {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createClient(@Valid Client client) {
        // If validation fails, ConstraintViolationException is thrown
        Client created = clientService.create(client);
        return Response.status(201).entity(created).build();
    }
    
    @GET
    @Path("/{id}")
    public Client getClient(
        @PathParam("id") 
        @NotNull(message = "ID is required")
        @Min(value = 1, message = "ID must be positive")
        Long id) {
        
        return clientService.findById(id);
    }
}
```

---

## Validation Exception Mapper

```java
@Provider
public class ValidationExceptionMapper 
    implements ExceptionMapper<ConstraintViolationException> {
    
    @Override
    public Response toResponse(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + ": " 
                            + violation.getMessage())
            .collect(Collectors.toList());
        
        ErrorResponse error = new ErrorResponse(
            400,
            "Validation Failed",
            errors.toString()
        );
        
        return Response.status(400).entity(error).build();
    }
}
```

---

## Global Exception Mapper

```java
@Provider
public class GenericExceptionMapper 
    implements ExceptionMapper<Exception> {
    
    @Inject
    private Logger logger;
    
    @Override
    public Response toResponse(Exception exception) {
        logger.severe("Unexpected error: " + exception.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            500,
            "Internal Server Error",
            "An unexpected error occurred"
        );
        
        return Response.status(500).entity(error).build();
    }
}
```

---

# Part 5: MicroProfile Rest Client

## Type-Safe REST Client

---

## What is MicroProfile Rest Client?

**MicroProfile Rest Client** provides:

- Type-safe REST client interface
- Automatic implementation generation
- CDI integration
- Fault tolerance support
- Configuration externalization

**Benefits:**
- No manual HTTP client code
- Compile-time type safety
- Easy to test and mock
- Consistent error handling

---

## Rest Client Interface

```java
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/clients")
@RegisterRestClient(configKey = "client-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ClientRestClient {
    
    @GET
    List<Client> getAllClients();
    
    @GET
    @Path("/{id}")
    Client getClient(@PathParam("id") Long id);
    
    @POST
    Client createClient(Client client);
    
    @PUT
    @Path("/{id}")
    Client updateClient(@PathParam("id") Long id, Client client);
    
    @DELETE
    @Path("/{id}")
    void deleteClient(@PathParam("id") Long id);
}
```

---

## Rest Client Configuration

**microprofile-config.properties:**
```properties
# Base URL for the client API
client-api/mp-rest/url=http://localhost:9080/api

# Connection timeout (milliseconds)
client-api/mp-rest/connectTimeout=5000

# Read timeout (milliseconds)
client-api/mp-rest/readTimeout=10000

# Scope (default is @Dependent)
client-api/mp-rest/scope=jakarta.inject.Singleton
```

---

## Using Rest Client with CDI

```java
import org.eclipse.microprofile.rest.client.inject.RestClient;
import jakarta.inject.Inject;

@ApplicationScoped
public class ClientFacade {
    
    @Inject
    @RestClient
    private ClientRestClient clientRestClient;
    
    public List<Client> getAllClients() {
        return clientRestClient.getAllClients();
    }
    
    public Client getClient(Long id) {
        return clientRestClient.getClient(id);
    }
    
    public Client createClient(Client client) {
        return clientRestClient.createClient(client);
    }
}
```

---

## Rest Client Exception Handling

```java
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@Provider
public class ClientRestClientExceptionMapper 
    implements ResponseExceptionMapper<RuntimeException> {
    
    @Override
    public RuntimeException toThrowable(Response response) {
        int status = response.getStatus();
        
        if (status == 404) {
            return new NotFoundException("Client not found");
        }
        if (status >= 400 && status < 500) {
            return new ClientException("Client error: " + status);
        }
        if (status >= 500) {
            return new ServerException("Server error: " + status);
        }
        
        return null; // No exception
    }
}
```

---

## Rest Client with Headers

```java
@Path("/clients")
@RegisterRestClient
public interface ClientRestClient {
    
    @GET
    @Path("/{id}")
    Client getClient(
        @PathParam("id") Long id,
        @HeaderParam("Authorization") String authToken
    );
    
    @POST
    @ClientHeaderParam(name = "X-API-Key", value = "${api.key}")
    Client createClient(Client client);
}
```

**Configuration:**
```properties
api.key=my-secret-api-key
```

---

## Rest Client Programmatic Creation

```java
import org.eclipse.microprofile.rest.client.RestClientBuilder;

public class ClientExample {
    
    public void example() {
        ClientRestClient client = RestClientBuilder
            .newBuilder()
            .baseUrl(new URL("http://localhost:9080/api"))
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build(ClientRestClient.class);
        
        List<Client> clients = client.getAllClients();
    }
}
```

---

# Part 6: CDI and REST Client Integration

## Combining Technologies

---

## CDI Producers for Rest Clients

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@ApplicationScoped
public class RestClientProducer {
    
    @Produces
    @ApplicationScoped
    public ClientRestClient produceClientRestClient() {
        return RestClientBuilder
            .newBuilder()
            .baseUrl(new URL("http://localhost:9080/api"))
            .build(ClientRestClient.class);
    }
}
```

**Usage:**
```java
@Inject
private ClientRestClient clientRestClient;
```

---

## CDI Interceptors with REST

```java
@Logged
@Path("/clients")
public class ClientResource {
    
    @GET
    public List<Client> getAllClients() {
        // Method execution will be logged by interceptor
        return clientService.findAll();
    }
}
```

**Interceptor from Lecture 4:**
```java
@Interceptor
@Logged
@Priority(Interceptor.Priority.APPLICATION)
public class LoggingInterceptor {
    @AroundInvoke
    public Object logMethod(InvocationContext context) throws Exception {
        // Logging logic
    }
}
```

---

## CDI Events with REST

```java
@Path("/clients")
public class ClientResource {
    
    @Inject
    private Event<ClientCreatedEvent> clientCreatedEvent;
    
    @POST
    public Response createClient(Client client) {
        Client created = clientService.create(client);
        
        // Fire CDI event
        clientCreatedEvent.fire(
            new ClientCreatedEvent(created, "REST API")
        );
        
        return Response.status(201).entity(created).build();
    }
}
```

---

## CDI Qualifiers with REST Clients

```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Internal {}

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface External {}

// Producer
@Produces
@Internal
public ClientRestClient produceInternalClient() {
    return RestClientBuilder.newBuilder()
        .baseUrl(new URL("http://internal-api:9080/api"))
        .build(ClientRestClient.class);
}

// Usage
@Inject @Internal
private ClientRestClient internalClient;
```

---

## Complete REST + CDI Example

```java
@Path("/clients")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {
    
    @Inject
    private ClientService clientService;
    
    @Inject
    private Event<ClientCreatedEvent> clientCreatedEvent;
    
    @Inject
    private Logger logger;
    
    @POST
    @Logged
    @Transactional
    public Response createClient(@Valid Client client) {
        logger.info("Creating client: " + client.getName());
        
        Client created = clientService.create(client);
        clientCreatedEvent.fire(new ClientCreatedEvent(created, "REST"));
        
        return Response.status(201).entity(created).build();
    }
}
```

---

# Lab 5 Preview

## Building a Complete REST API

---

## Lab 5 Objectives

In Lab 5, you will:

1. **Create REST API** for banking application
   - Client CRUD operations
   - Account CRUD operations
   - Transfer operations

2. **Implement JSON-B** customization
   - Custom date formats
   - Property naming strategies

3. **Add Exception Handling**
   - Custom exception mappers
   - Validation

---

## Lab 5 Objectives (cont.)

4. **Use MicroProfile Rest Client**
   - Create client interfaces
   - Configure endpoints
   - Test with CDI

5. **Integrate with existing code**
   - Keep web UI (JSP)
   - Add REST API alongside
   - Share services and repositories

---

## Lab 5 Architecture

```
┌─────────────────────────────────────────┐
│         Web Browser / REST Client       │
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

## Lab 5 REST Endpoints

**Client Endpoints:**
```
GET    /api/clients           - List all clients
GET    /api/clients/{id}      - Get client by ID
POST   /api/clients           - Create client
PUT    /api/clients/{id}      - Update client
DELETE /api/clients/{id}      - Delete client
GET    /api/clients/search    - Search clients
```

**Account Endpoints:**
```
GET    /api/accounts          - List all accounts
GET    /api/accounts/{id}     - Get account by ID
POST   /api/accounts          - Create account
PUT    /api/accounts/{id}     - Update account
DELETE /api/accounts/{id}     - Delete account
GET    /api/clients/{id}/accounts - Get client's accounts
```

---

## Lab 5 REST Endpoints (cont.)

**Transfer Endpoint:**
```
POST   /api/transfers         - Transfer between accounts
```

**Request Body:**
```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 100.00,
  "description": "Payment"
}
```

---

## Lab 5 Testing

**Tools you'll use:**
- `curl` - Command-line HTTP client
- Postman - GUI REST client
- REST Client VS Code extension
- Automated test scripts

**Example curl command:**
```bash
curl -X POST http://localhost:9080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'
```

---

## Lab 5 Deliverables

1. **Complete REST API** implementation
2. **Exception handlers** for all error cases
3. **Validation** on all inputs
4. **Rest Client** interface and tests
5. **Documentation** of all endpoints
6. **Test scripts** for automated testing

---

# Summary and Key Takeaways

---

## What We Learned Today

1. **REST Principles**
   - Architectural constraints
   - HTTP methods and status codes
   - Resource design best practices

2. **JAX-RS**
   - Annotations (@Path, @GET, @POST, etc.)
   - Resource implementation
   - Sub-resources and parameters

---

## What We Learned Today (cont.)

3. **JSON-B**
   - Automatic JSON conversion
   - Customization with annotations
   - Type adapters

4. **Exception Handling**
   - Custom exception mappers
   - Error response DTOs
   - Bean validation

5. **MicroProfile Rest Client**
   - Type-safe client interfaces
   - CDI integration
   - Configuration

---

## Best Practices Recap

✅ **Design RESTful URIs** - Use nouns, not verbs
✅ **Use HTTP methods correctly** - GET, POST, PUT, DELETE
✅ **Return proper status codes** - 200, 201, 404, 500, etc.
✅ **Handle exceptions gracefully** - Custom mappers
✅ **Validate inputs** - Bean Validation
✅ **Use JSON-B** - Automatic conversion
✅ **Leverage CDI** - Inject services and clients
✅ **Document your API** - Clear endpoint descriptions

---

## Common Pitfalls to Avoid

❌ **Using verbs in URIs** - `/getClient` instead of `/clients`
❌ **Ignoring HTTP methods** - Using only GET and POST
❌ **Generic error responses** - Always return 500
❌ **No input validation** - Accepting invalid data
❌ **Exposing internal errors** - Leaking stack traces
❌ **Stateful REST services** - Storing session data
❌ **Not versioning API** - Breaking changes without notice

---

## Next Steps

### Lecture 6: Microservices Architecture
- Microservices principles
- Service decomposition
- Inter-service communication
- API Gateway pattern

### Lab 6: Microservices Implementation
- Split monolith into microservices
- Implement service discovery
- Add API gateway
- Handle distributed transactions

---

## Additional Resources

### Documentation
- [Jakarta RESTful Web Services Specification](https://jakarta.ee/specifications/restful-ws/)
- [Jakarta JSON Binding Specification](https://jakarta.ee/specifications/jsonb/)
- [MicroProfile Rest Client](https://microprofile.io/project/eclipse/microprofile-rest-client)

### Books
- "RESTful Web Services" by Leonard Richardson
- "REST API Design Rulebook" by Mark Masse

### Online
- [REST API Tutorial](https://restfulapi.net/)
- [JAX-RS Tutorial](https://www.baeldung.com/jax-rs-spec-and-implementations)

---

## Questions?

**Contact Information:**
- Email: instructor@esipe.fr
- Office Hours: Monday/Wednesday 2-4 PM
- Discussion Forum: [course-forum-link]

**Lab 5 Materials:**
- Lab Guide: `03-Labs/Lab05-REST/README.md`
- Starter Code: `03-Labs/Lab05-REST/starter/`
- Solution: `03-Labs/Lab05-REST/solution/`

---

## Thank You!

### See you in Lab 5!

**Remember:**
- Complete Lab 5 before next session
- Review REST principles
- Practice with curl and Postman
- Ask questions on the forum

**Next Session:** Microservices Architecture

---

<!-- End of Lecture 5 -->