---
marp: true
theme: default
paginate: true
backgroundColor: #fff
header: 'Jakarta EE & MicroProfile Course'
footer: 'Lecture 8: Microservices Architecture | © 2026 Olivier Planson - All rights reserved. Reproduction prohibited.'
style: |
  section {
    font-size: 22px;
    padding: 40px 60px;
  }
  img {
    max-width: 85%;
    max-height: 380px;
    display: block;
    margin: 10px auto;
  }
  pre {
    font-size: 0.65em;
    margin: 10px 0;
    padding: 10px;
  }
  code {
    font-size: 0.7em;
  }
  ul, ol {
    font-size: 0.85em;
    line-height: 1.8;
    margin: 8px 0;
  }
  li {
    margin: 6px 0;
    line-height: 1.8;
  }
  li::marker {
    flex-shrink: 0;
  }
  h1 {
    font-size: 1.8em;
    margin-bottom: 20px;
    line-height: 1.3;
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: nowrap;
    white-space: nowrap;
  }
  h2 {
    font-size: 1.3em;
    margin: 15px 0 10px 0;
    line-height: 1.3;
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: nowrap;
    white-space: nowrap;
  }
  h3 {
    font-size: 1.1em;
    margin: 10px 0 8px 0;
    line-height: 1.3;
    display: flex;
    align-items: center;
    gap: 6px;
    flex-wrap: nowrap;
    white-space: nowrap;
  }
  table {
    font-size: 0.8em;
  }
  td {
    vertical-align: middle;
    white-space: nowrap;
  }
  th {
    white-space: nowrap;
  }
  p {
    margin: 8px 0;
    line-height: 1.6;
    white-space: nowrap;
  }
  strong {
    white-space: nowrap;
  }
  blockquote {
    font-size: 0.9em;
    margin: 10px 0;
    padding: 10px 15px;
  }
  .columns {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    align-items: start;
  }
  .columns-3 {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: 15px;
    align-items: start;
  }
  .columns-2-1 {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 20px;
    align-items: start;
  }
  .columns-1-2 {
    display: grid;
    grid-template-columns: 1fr 2fr;
    gap: 20px;
    align-items: start;
  }
---

<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->





# 🏗️ Lecture 8: Microservices Architecture

## From Monolith to Microservices

**Jakarta EE and MicroProfile Course**

---

## 📋 Lecture Overview

### Topics Covered
- Microservices fundamentals and principles
- Service decomposition strategies
- Inter-service communication patterns
- API Gateway pattern
- Service discovery and configuration
- Distributed tracing and monitoring
- Data consistency in distributed systems
- Deployment and DevOps practices

### Duration: 4 hours (2h lecture + 1h advanced topics + 1h lab intro)

---

## 🎯 Learning Objectives

By the end of this lecture, you will be able to:

1. ✅ Understand microservices architecture principles
2. ✅ Decompose monolithic applications into services
3. ✅ Implement inter-service communication
4. ✅ Apply API Gateway pattern
5. ✅ Configure service discovery and externalized configuration
6. ✅ Implement distributed tracing and monitoring
7. ✅ Handle data consistency in distributed systems
8. ✅ Deploy microservices with Docker Compose

---

## 📚 Prerequisites

### Required Knowledge
- ✅ Jakarta EE fundamentals (Lectures 1-5)
- ✅ Domain-Driven Design (Lecture 6)
- ✅ Hexagonal Architecture (Lecture 7)
- ✅ REST API design
- ✅ Docker basics

### Required Tools
- JDK 17+
- Maven 3.8+
- Docker or Podman
- PostgreSQL 16+

---

# Part 1: Microservices Fundamentals

## 🏛️ What are Microservices?

---

## Definition

> **Microservices** is an architectural style that structures an application as a collection of small, autonomous services modeled around a business domain.

### Key Characteristics
- **Small and Focused**: Each service does one thing well
- **Independently Deployable**: Can be deployed without affecting others
- **Loosely Coupled**: Services communicate via well-defined APIs
- **Owned by Small Teams**: Each team owns one or more services
- **Technology Agnostic**: Can use different technologies per service

---

## Monolith vs Microservices

<div class="columns">

### Monolithic Architecture
```
┌─────────────────────────────┐
│     Banking Application     │
│                             │
│  ┌─────────────────────┐   │
│  │   Presentation      │   │
│  └─────────────────────┘   │
│  ┌─────────────────────┐   │
│  │   Business Logic    │   │
│  │  - Client Mgmt      │   │
│  │  - Account Mgmt     │   │
│  │  - Transactions     │   │
│  └─────────────────────┘   │
│  ┌─────────────────────┐   │
│  │   Data Access       │   │
│  └─────────────────────┘   │
│                             │
└──────────┬──────────────────┘
           │
    ┌──────▼──────┐
    │  Database   │
    └─────────────┘
```

### Microservices Architecture
```
┌──────────────┐
│ API Gateway  │
└──────┬───────┘
       │
   ┌───┴────┬────────┐
   │        │        │
┌──▼──┐  ┌─▼──┐  ┌──▼───┐
│Client│  │Acct│  │Trans │
│Svc   │  │Svc │  │Svc   │
└──┬───┘  └─┬──┘  └──┬───┘
   │        │        │
┌──▼──┐  ┌─▼──┐  ┌──▼───┐
│DB 1 │  │DB 2│  │DB 3  │
└─────┘  └────┘  └──────┘
```

</div>

---

## Comparison Table

| Aspect | Monolith | Microservices |
|--------|----------|---------------|
| **Deployment** | Single unit | Multiple services |
| **Scaling** | Scale entire app | Scale services independently |
| **Technology** | Single stack | Polyglot possible |
| **Development** | Simpler initially | More complex |
| **Testing** | Easier | More challenging |
| **Resilience** | Single point of failure | Fault isolation |
| **Team Structure** | Large team | Small, autonomous teams |
| **Data Management** | Shared database | Database per service |

---

## Benefits of Microservices

### 1. **Independent Deployment**
- Deploy services independently
- Faster release cycles
- Reduced deployment risk

### 2. **Technology Flexibility**
- Choose best technology per service
- Easier to adopt new technologies
- Experiment with new approaches

### 3. **Scalability**
- Scale services independently
- Optimize resource usage
- Handle varying loads efficiently

---

## Benefits (Continued)

### 4. **Fault Isolation**
- Failures contained to single service
- System remains partially functional
- Easier to identify and fix issues

### 5. **Team Autonomy**
- Small teams own services
- Faster decision making
- Clear ownership and responsibility

### 6. **Easier to Understand**
- Smaller codebase per service
- Focused business logic
- Simpler mental model

---

## Challenges of Microservices

### 1. **Distributed System Complexity**
- Network latency and failures
- Partial failures
- Distributed transactions
- Data consistency

### 2. **Operational Overhead**
- More services to deploy and monitor
- Complex infrastructure
- Service discovery and configuration
- Distributed tracing

---

## Challenges (Continued)

### 3. **Testing Complexity**
- Integration testing across services
- End-to-end testing
- Contract testing
- Test data management

### 4. **Data Management**
- No ACID transactions across services
- Eventual consistency
- Data duplication
- Query complexity

### 5. **Organizational Changes**
- Team structure changes
- DevOps culture required
- Communication overhead
- Skill requirements

---

## When to Use Microservices?

### ✅ Good Fit When:
- Large, complex applications
- Multiple teams working independently
- Different parts need different scaling
- Rapid deployment required
- Technology diversity needed
- High availability critical

### ❌ Not Recommended When:
- Small, simple applications
- Single team
- Tight coupling between components
- Limited operational expertise
- Startup/MVP phase

---

## Conway's Law

> "Organizations which design systems are constrained to produce designs which are copies of the communication structures of these organizations."
> — Melvin Conway, 1967

### Implications for Microservices
- Service boundaries should match team boundaries
- Team structure influences architecture
- Inverse Conway Maneuver: Design teams around desired architecture
- Communication patterns affect system design

---

# Part 2: Service Decomposition Strategies

## 🔪 How to Split a Monolith

---

## Decomposition Strategies

### 1. **By Business Capability**
- Identify business capabilities
- Create service per capability
- Example: Client Management, Account Management, Transaction Processing

### 2. **By Subdomain (DDD)**
- Use Domain-Driven Design
- Identify bounded contexts
- Create service per bounded context
- Example: Client Context, Account Context

### 3. **By Transaction Boundaries**
- Group operations that must be atomic
- Minimize distributed transactions
- Example: Account + Transaction in same service

---

## Banking Application Decomposition

### Original Monolith (Lab 07)
```
Banking Application
├── Client Management
│   ├── Create/Update/Delete Client
│   └── Query Clients
├── Account Management
│   ├── Create/Update/Delete Account
│   ├── Query Accounts
│   └── Link to Client
└── Transaction Management
    ├── Deposit
    ├── Withdraw
    └── Transfer
```

---

## Proposed Microservices (Lab 08)

```
┌─────────────────────────────────────────────────────────┐
│                      API Gateway                         │
│                    (Port 9080)                          │
│  - Request routing                                       │
│  - Response aggregation                                  │
│  - Authentication                                        │
└────────────┬──────────────────────────┬─────────────────┘
             │                          │
             │ REST                     │ REST
             │                          │
    ┌────────▼────────┐        ┌───────▼────────┐
    │ Client Service  │        │ Account Service │
    │   (Port 9081)   │◄───────│   (Port 9082)   │
    │                 │  REST  │                  │
    │ - Client CRUD   │        │ - Account CRUD   │
    │ - Client Query  │        │ - Transactions   │
    │                 │        │ - Balance Ops    │
    └────────┬────────┘        └────────┬─────────┘
             │                          │
             │                          │
    ┌────────▼────────┐        ┌───────▼─────────┐
    │  PostgreSQL     │        │   PostgreSQL    │
    │ banking_client  │        │ banking_account │
    │      _db        │        │      _db        │
    └─────────────────┘        └─────────────────┘
```

---

## Service Responsibilities

### Client Service
- **Bounded Context**: Client Management
- **Responsibilities**:
  - Create, update, delete clients
  - Query client information
  - Validate client data
  - Manage client lifecycle
- **Database**: banking_client_db
- **Port**: 9081

---

## Service Responsibilities (Continued)

### Account Service
- **Bounded Context**: Account Management + Transactions
- **Responsibilities**:
  - Create, update, delete accounts
  - Query account information
  - Deposit and withdraw money
  - Transfer between accounts
  - Validate account operations
  - Verify client existence (via Client Service)
- **Database**: banking_account_db
- **Port**: 9082
- **Dependencies**: Client Service (for validation)

---

## Service Responsibilities (Continued)

### API Gateway
- **Responsibilities**:
  - Route requests to appropriate services
  - Aggregate responses from multiple services
  - Handle authentication and authorization
  - Provide unified API documentation
  - Implement rate limiting (optional)
  - Handle cross-cutting concerns
- **No Database**: Stateless
- **Port**: 9080
- **Dependencies**: Client Service, Account Service

---

## Database Per Service Pattern

### Principle
> Each microservice has its own database that only it can access directly.

### Benefits
- **Loose Coupling**: Services don't share database schema
- **Independent Evolution**: Change schema without affecting others
- **Technology Choice**: Use different databases per service
- **Scalability**: Scale databases independently

### Challenges
- **Data Consistency**: No ACID transactions across services
- **Queries**: Can't join across databases
- **Data Duplication**: May need to replicate data

---

## Shared Database Anti-Pattern

### Why to Avoid
```
┌──────────┐     ┌──────────┐     ┌──────────┐
│ Service  │     │ Service  │     │ Service  │
│    A     │     │    B     │     │    C     │
└────┬─────┘     └────┬─────┘     └────┬─────┘
     │                │                │
     └────────────────┼────────────────┘
                      │
              ┌───────▼────────┐
              │ Shared Database│
              └────────────────┘
```

### Problems
- ❌ Tight coupling through database schema
- ❌ Can't change schema without coordinating all services
- ❌ Can't scale services independently
- ❌ Can't use different database technologies
- ❌ Defeats purpose of microservices

---

## Data Consistency Strategies

### 1. **Eventual Consistency**
- Accept that data may be temporarily inconsistent
- Use events to propagate changes
- Design for eventual consistency

### 2. **Saga Pattern**
- Sequence of local transactions
- Each transaction updates one service
- Compensating transactions for rollback

### 3. **Event Sourcing**
- Store events instead of current state
- Rebuild state by replaying events
- Natural audit trail

---

# Part 3: Inter-Service Communication

## 🔗 How Services Talk to Each Other

---

## Communication Patterns

### 1. **Synchronous Communication**
- Request/Response pattern
- Caller waits for response
- Examples: REST, gRPC
- **Use when**: Immediate response needed

### 2. **Asynchronous Communication**
- Fire and forget
- Caller doesn't wait
- Examples: Messaging, Events
- **Use when**: Response not immediately needed

---

## REST-Based Communication

### MicroProfile Rest Client

```java
@RegisterRestClient(baseUri = "http://client-service:9080")
@Path("/api/clients")
public interface ClientServiceClient {
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientDTO getClient(@PathParam("id") Long id);
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ClientDTO> getAllClients();
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientDTO createClient(ClientDTO client);
}
```

---

## Using Rest Client

### In Account Service

```java
@ApplicationScoped
public class AccountService {
    
    @Inject
    @RestClient
    ClientServiceClient clientService;
    
    public Account createAccount(CreateAccountCommand command) {
        // Verify client exists by calling Client Service
        try {
            ClientDTO client = clientService.getClient(command.clientId());
            if (client == null) {
                throw new ValidationException("Client not found");
            }
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new ValidationException("Client not found");
            }
            throw new ServiceUnavailableException("Client service unavailable");
        }
        
        // Create account
        Account account = new Account(command.clientId(), 
                                     command.accountType());
        accountRepository.save(account);
        return account;
    }
}
```

---

## Configuration with MicroProfile Config

### microprofile-config.properties

```properties
# Client Service URL
com.bank.client.ClientServiceClient/mp-rest/url=http://client-service:9080
com.bank.client.ClientServiceClient/mp-rest/scope=jakarta.inject.Singleton

# Timeouts
com.bank.client.ClientServiceClient/mp-rest/connectTimeout=2000
com.bank.client.ClientServiceClient/mp-rest/readTimeout=5000
```

### Environment Variables (Docker Compose)

```yaml
environment:
  CLIENT_SERVICE_URL: http://client-service:9080
```

---

## Fault Tolerance with MicroProfile

### Circuit Breaker Pattern

```java
@ApplicationScoped
public class AccountService {
    
    @Inject
    @RestClient
    ClientServiceClient clientService;
    
    @CircuitBreaker(
        requestVolumeThreshold = 4,
        failureRatio = 0.5,
        delay = 5000,
        successThreshold = 2
    )
    @Fallback(fallbackMethod = "getClientFallback")
    @Timeout(2000)
    public ClientDTO getClient(Long clientId) {
        return clientService.getClient(clientId);
    }
    
    public ClientDTO getClientFallback(Long clientId) {
        // Return cached data or default response
        return new ClientDTO(clientId, "Unknown", "unknown@example.com");
    }
}
```

---

## Circuit Breaker States

```
                    ┌─────────┐
                    │ CLOSED  │ (Normal operation)
                    └────┬────┘
                         │ Failures exceed threshold
                         │
                    ┌────▼────┐
                    │  OPEN   │ (Reject requests)
                    └────┬────┘
                         │ After delay
                         │
                    ┌────▼────┐
                    │HALF-OPEN│ (Test if recovered)
                    └────┬────┘
                         │
              ┌──────────┴──────────┐
              │                     │
         Success                 Failure
              │                     │
         ┌────▼────┐           ┌───▼────┐
         │ CLOSED  │           │  OPEN  │
         └─────────┘           └────────┘
```

---

## Retry Strategy

```java
@Retry(
    maxRetries = 3,
    delay = 1000,
    delayUnit = ChronoUnit.MILLIS,
    maxDuration = 5000,
    durationUnit = ChronoUnit.MILLIS,
    retryOn = {ServiceUnavailableException.class, TimeoutException.class}
)
@Timeout(2000)
public ClientDTO getClient(Long clientId) {
    return clientService.getClient(clientId);
}
```

### Retry Behavior
1. First attempt fails → Wait 1 second
2. Second attempt fails → Wait 1 second
3. Third attempt fails → Wait 1 second
4. Fourth attempt fails → Give up
5. Total max duration: 5 seconds

---

## Bulkhead Pattern

### Isolate Resources

```java
@Bulkhead(value = 5, waitingTaskQueue = 10)
public ClientDTO getClient(Long clientId) {
    return clientService.getClient(clientId);
}
```

### Purpose
- Limit concurrent calls to a service
- Prevent resource exhaustion
- Isolate failures
- Protect downstream services

---

# Part 4: API Gateway Pattern

## 🚪 Single Entry Point

---

## What is an API Gateway?

> An **API Gateway** is a server that acts as an API front-end, receiving API requests, enforcing throttling and security policies, passing requests to the back-end service, and then passing the response back to the requester.

### Responsibilities
- **Routing**: Direct requests to appropriate services
- **Aggregation**: Combine responses from multiple services
- **Authentication**: Verify user identity
- **Authorization**: Check permissions
- **Rate Limiting**: Prevent abuse
- **Caching**: Improve performance
- **Monitoring**: Track API usage

---

## API Gateway Architecture

```
┌─────────────────────────────────────────────────────────┐
│                      API Gateway                         │
│                                                          │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐       │
│  │   Routing  │  │Aggregation │  │    Auth    │       │
│  └────────────┘  └────────────┘  └────────────┘       │
│                                                          │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐       │
│  │Rate Limit  │  │  Caching   │  │ Monitoring │       │
│  └────────────┘  └────────────┘  └────────────┘       │
└────────────┬──────────────────────────┬─────────────────┘
             │                          │
             │                          │
    ┌────────▼────────┐        ┌───────▼────────┐
    │ Client Service  │        │ Account Service │
    └─────────────────┘        └─────────────────┘
```

---

## Request Routing

### Simple Routing

```java
@Path("/api")
@ApplicationScoped
public class ApiGateway {
    
    @Inject
    @RestClient
    ClientServiceClient clientService;
    
    @Inject
    @RestClient
    AccountServiceClient accountService;
    
    // Route to Client Service
    @GET
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClients() {
        List<ClientDTO> clients = clientService.getAllClients();
        return Response.ok(clients).build();
    }
    
    // Route to Account Service
    @GET
    @Path("/accounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts() {
        List<AccountDTO> accounts = accountService.getAllAccounts();
        return Response.ok(accounts).build();
    }
}
```

---

## Response Aggregation

### Combining Multiple Services

```java
@GET
@Path("/clients/{id}/accounts")
@Produces(MediaType.APPLICATION_JSON)
public Response getClientWithAccounts(@PathParam("id") Long clientId) {
    // Call Client Service
    ClientDTO client = clientService.getClient(clientId);
    if (client == null) {
        return Response.status(404).build();
    }
    
    // Call Account Service
    List<AccountDTO> accounts = accountService.getAccountsByClient(clientId);
    
    // Aggregate response
    ClientWithAccountsDTO response = new ClientWithAccountsDTO(
        client.id(),
        client.name(),
        client.email(),
        accounts
    );
    
    return Response.ok(response).build();
}
```

---

## Response Aggregation DTO

```java
public record ClientWithAccountsDTO(
    Long id,
    String name,
    String email,
    List<AccountDTO> accounts
) {}
```

### Response Example

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "accounts": [
    {
      "id": 101,
      "accountNumber": "ACC-001",
      "balance": 1000.00,
      "type": "CHECKING"
    },
    {
      "id": 102,
      "accountNumber": "ACC-002",
      "balance": 5000.00,
      "type": "SAVINGS"
    }
  ]
}
```

---

## Authentication at Gateway

### Basic Authentication Example

```java
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                Response.status(401)
                    .entity("Unauthorized")
                    .build()
            );
            return;
        }
        
        String token = authHeader.substring("Bearer ".length());
        if (!validateToken(token)) {
            requestContext.abortWith(
                Response.status(401)
                    .entity("Invalid token")
                    .build()
            );
        }
    }
    
    private boolean validateToken(String token) {
        // Validate JWT token or session token
        return true; // Simplified
    }
}
```

---

## Rate Limiting

### Simple Rate Limiter

```java
@Provider
@Priority(Priorities.USER)
public class RateLimitFilter implements ContainerRequestFilter {
    
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String clientId = getClientId(requestContext);
        RateLimiter limiter = limiters.computeIfAbsent(
            clientId, 
            k -> RateLimiter.create(100.0) // 100 requests per second
        );
        
        if (!limiter.tryAcquire()) {
            requestContext.abortWith(
                Response.status(429)
                    .entity("Too many requests")
                    .build()
            );
        }
    }
    
    private String getClientId(ContainerRequestContext ctx) {
        // Extract from token or IP address
        return ctx.getHeaderString("X-Client-ID");
    }
}
```

---

# Part 5: Service Discovery and Configuration

## 🔍 Finding Services Dynamically

---

## Service Discovery

### Problem
- Services need to find each other
- Service instances can change (scaling, failures)
- Hard-coded URLs don't work in dynamic environments

### Solution
- **Service Registry**: Central directory of services
- **Service Registration**: Services register themselves
- **Service Discovery**: Services query registry

---

## Service Discovery Patterns

### 1. **Client-Side Discovery**
```
┌────────┐
│ Client │
└───┬────┘
    │ 1. Query
    │
┌───▼────────┐
│  Registry  │
└───┬────────┘
    │ 2. Return addresses
    │
┌───▼────┐
│Service │
└────────┘
```

### 2. **Server-Side Discovery**
```
┌────────┐
│ Client │
└───┬────┘
    │ 1. Request
    │
┌───▼────────┐
│Load Balance│
└───┬────────┘
    │ 2. Query registry
    │ 3. Forward
┌───▼────┐
│Service │
└────────┘
```

---

## Service Discovery in Docker Compose

### Using Service Names

```yaml
services:
  client-service:
    image: client-service:latest
    ports:
      - "9081:9080"
    networks:
      - banking-network

  account-service:
    image: account-service:latest
    ports:
      - "9082:9080"
    environment:
      CLIENT_SERVICE_URL: http://client-service:9080
    networks:
      - banking-network

networks:
  banking-network:
    driver: bridge
```

### Docker's Built-in DNS
- Services can reference each other by name
- Docker resolves service names to IP addresses
- Automatic load balancing for scaled services

---

## MicroProfile Config

### Externalized Configuration

```java
@ApplicationScoped
public class AccountService {
    
    @Inject
    @ConfigProperty(name = "client.service.url", 
                    defaultValue = "http://localhost:9081")
    String clientServiceUrl;
    
    @Inject
    @ConfigProperty(name = "account.max.balance", 
                    defaultValue = "1000000")
    BigDecimal maxBalance;
    
    @Inject
    @ConfigProperty(name = "account.min.balance", 
                    defaultValue = "0")
    BigDecimal minBalance;
}
```

---

## Configuration Sources

### Priority Order (highest to lowest)

1. **System Properties**: `-Dclient.service.url=http://...`
2. **Environment Variables**: `CLIENT_SERVICE_URL=http://...`
3. **microprofile-config.properties**: In `src/main/resources/META-INF/`
4. **Default Values**: In `@ConfigProperty` annotation

### Example: microprofile-config.properties

```properties
# Client Service Configuration
client.service.url=http://localhost:9081
client.service.timeout=5000

# Account Service Configuration
account.max.balance=1000000
account.min.balance=0
account.overdraft.enabled=false

# Database Configuration
db.host=localhost
db.port=5432
db.name=banking_account_db
```

---

## Environment-Specific Configuration

### Development (docker-compose.yml)

```yaml
account-service:
  environment:
    CLIENT_SERVICE_URL: http://client-service:9080
    DB_HOST: account-db
    DB_PORT: 5432
    LOG_LEVEL: DEBUG
```

### Production (Kubernetes ConfigMap)

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: account-service-config
data:
  CLIENT_SERVICE_URL: "http://client-service.prod.svc.cluster.local"
  DB_HOST: "prod-postgres.database.azure.com"
  LOG_LEVEL: "INFO"
```

---

# Part 6: Distributed Tracing and Monitoring

## 📊 Observability in Microservices

---

## The Three Pillars of Observability

### 1. **Logging**
- Record events and errors
- Structured logging (JSON)
- Centralized log aggregation

### 2. **Metrics**
- Measure system behavior
- Performance indicators
- Resource utilization

### 3. **Tracing**
- Track requests across services
- Identify bottlenecks
- Understand dependencies

---

## Distributed Tracing

### Problem
```
Client → Gateway → Account Service → Client Service
                         ↓
                    Database
```

**Question**: Where is the slowness?

### Solution: Distributed Tracing
- Assign unique ID to each request (Trace ID)
- Track request through all services
- Measure time in each service
- Visualize request flow

---

## MicroProfile OpenTracing

### Automatic Tracing

```java
@Path("/api/accounts")
@ApplicationScoped
@Traced // Automatically trace all methods
public class AccountResource {
    
    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") Long id) {
        // Automatically traced
        Account account = accountService.getAccount(id);
        return Response.ok(account).build();
    }
}
```

### Manual Tracing

```java
@Inject
Tracer tracer;

public void processAccount(Long accountId) {
    Span span = tracer.buildSpan("processAccount").start();
    try {
        // Business logic
        span.setTag("account.id", accountId);
        span.log("Processing started");
        
        // ... processing ...
        
        span.log("Processing completed");
    } finally {
        span.finish();
    }
}
```

---

## Correlation IDs

### Propagating Request Context

```java
@Provider
@Priority(Priorities.USER)
public class CorrelationIdFilter implements ContainerRequestFilter {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String correlationId = requestContext.getHeaderString(CORRELATION_ID_HEADER);
        
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // Store in thread-local or MDC
        MDC.put("correlationId", correlationId);
        
        // Add to response
        requestContext.getHeaders().add(CORRELATION_ID_HEADER, correlationId);
    }
}
```

---

## Structured Logging

### With Correlation ID

```java
@ApplicationScoped
public class AccountService {
    
    private static final Logger logger = Logger.getLogger(AccountService.class.getName());
    
    public Account createAccount(CreateAccountCommand command) {
        logger.info(String.format(
            "{\"event\":\"account.create.start\",\"clientId\":%d,\"type\":\"%s\",\"correlationId\":\"%s\"}",
            command.clientId(),
            command.accountType(),
            MDC.get("correlationId")
        ));
        
        try {
            Account account = new Account(command.clientId(), command.accountType());
            accountRepository.save(account);
            
            logger.info(String.format(
                "{\"event\":\"account.create.success\",\"accountId\":%d,\"correlationId\":\"%s\"}",
                account.getId(),
                MDC.get("correlationId")
            ));
            
            return account;
        } catch (Exception e) {
            logger.severe(String.format(
                "{\"event\":\"account.create.error\",\"error\":\"%s\",\"correlationId\":\"%s\"}",
                e.getMessage(),
                MDC.get("correlationId")
            ));
            throw e;
        }
    }
}
```

---

## MicroProfile Metrics

### Automatic Metrics

```java
@Path("/api/accounts")
@ApplicationScoped
public class AccountResource {
    
    @GET
    @Path("/{id}")
    @Timed(name = "getAccountTimer", 
           description = "Time to get account",
           unit = MetricUnits.MILLISECONDS)
    @Counted(name = "getAccountCount",
             description = "Number of times account retrieved")
    public Response getAccount(@PathParam("id") Long id) {
        Account account = accountService.getAccount(id);
        return Response.ok(account).build();
    }
}
```

---

## Custom Metrics

```java
@ApplicationScoped
public class AccountService {
    
    @Inject
    @Metric(name = "account.balance.total")
    Gauge<Double> totalBalance;
    
    @Inject
    @Metric(name = "account.created")
    Counter accountsCreated;
    
    @Inject
    @Metric(name = "account.transaction.duration")
    Timer transactionTimer;
    
    public Account createAccount(CreateAccountCommand command) {
        Timer.Context context = transactionTimer.time();
        try {
            Account account = new Account(command.clientId(), command.accountType());
            accountRepository.save(account);
            accountsCreated.inc();
            return account;
        } finally {
            context.stop();
        }
    }
}
```

---

## Health Checks

### Liveness, Readiness, Startup

```java
@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {
    
    @Override
    public HealthCheckResponse call() {
        // Check if application is alive
        return HealthCheckResponse.up("account-service-live");
    }
}

@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {
    
    @Inject
    DataSource dataSource;
    
    @Override
    public HealthCheckResponse call() {
        try (Connection conn = dataSource.getConnection()) {
            return HealthCheckResponse.up("account-service-ready");
        } catch (SQLException e) {
            return HealthCheckResponse.down("account-service-not-ready");
        }
    }
}
```

---

## Health Check Endpoints

### Accessing Health Information

```bash
# Liveness probe
curl http://localhost:9082/health/live

# Readiness probe
curl http://localhost:9082/health/ready

# All health checks
curl http://localhost:9082/health
```

### Response Example

```json
{
  "status": "UP",
  "checks": [
    {
      "name": "account-service-live",
      "status": "UP"
    },
    {
      "name": "account-service-ready",
      "status": "UP"
    },
    {
      "name": "database-connection",
      "status": "UP",
      "data": {
        "connection": "active"
      }
    }
  ]
}
```

---

## Metrics Endpoint

### Accessing Metrics

```bash
# All metrics
curl http://localhost:9082/metrics

# Application metrics only
curl http://localhost:9082/metrics/application

# Base metrics (JVM, etc.)
curl http://localhost:9082/metrics/base

# Vendor metrics
curl http://localhost:9082/metrics/vendor
```

### Prometheus Format

```
# TYPE application_account_created_total counter
application_account_created_total 42.0

# TYPE application_account_transaction_duration_seconds summary
application_account_transaction_duration_seconds_count 100.0
application_account_transaction_duration_seconds_sum 5.234
```

---

# Part 7: Data Consistency in Distributed Systems

## 🔄 Managing Distributed Data

---

## The CAP Theorem

### You Can Only Choose Two

- **C**onsistency: All nodes see the same data
- **A**vailability: Every request gets a response
- **P**artition Tolerance: System works despite network failures

```
        Consistency
            /\
           /  \
          /    \
         /  CA  \
        /        \
       /          \
      /____________\
Availability    Partition
                Tolerance
```

### In Practice
- Network partitions will happen (P is required)
- Choose between C and A
- Microservices typically choose AP (Availability + Partition Tolerance)

---

## Eventual Consistency

### Definition
> The system will eventually become consistent, but may be temporarily inconsistent.

### Example: Account Balance
1. Client Service updates client status to "Premium"
2. Event published: "ClientUpgradedToPremium"
3. Account Service receives event (after delay)
4. Account Service updates account benefits
5. **Temporary inconsistency**: Client is premium but doesn't have premium benefits yet

### Handling Eventual Consistency
- Design UI to handle stale data
- Use timestamps and versioning
- Implement conflict resolution
- Communicate delays to users

---

## Saga Pattern

### Distributed Transaction Alternative

> A **saga** is a sequence of local transactions where each transaction updates data within a single service. If a transaction fails, the saga executes compensating transactions to undo the changes.

### Example: Create Account with Client Validation

```
1. Account Service: Create account (local transaction)
2. Client Service: Verify client exists (local transaction)
3. If client doesn't exist:
   - Account Service: Delete account (compensating transaction)
```

---

## Saga Implementation: Choreography

### Event-Driven Approach

```
┌─────────────┐                    ┌─────────────┐
│   Account   │                    │   Client    │
│   Service   │                    │   Service   │
└──────┬──────┘                    └──────┬──────┘
       │                                  │
       │ 1. Create Account                │
       │────────────────────────────────► │
       │                                  │
       │ 2. AccountCreated Event          │
       │◄──────────────────────────────── │
       │                                  │
       │ 3. Verify Client                 │
       │────────────────────────────────► │
       │                                  │
       │ 4a. ClientVerified Event         │
       │◄──────────────────────────────── │
       │    (Success)                     │
       │                                  │
       │ 4b. ClientNotFound Event         │
       │◄──────────────────────────────── │
       │    (Failure)                     │
       │                                  │
       │ 5. Delete Account                │
       │    (Compensating Transaction)    │
       │                                  │
```

---

## Saga Implementation: Orchestration

### Centralized Coordinator

```
┌─────────────┐
│    Saga     │
│ Coordinator │
└──────┬──────┘
       │
       │ 1. Create Account
       ├────────────────────► Account Service
       │
       │ 2. Verify Client
       ├────────────────────► Client Service
       │
       │ 3a. Success: Confirm Account
       ├────────────────────► Account Service
       │
       │ 3b. Failure: Delete Account
       ├────────────────────► Account Service
       │
```

---

## Event Sourcing

### Store Events, Not State

**Traditional Approach**:
```sql
UPDATE accounts SET balance = 1500 WHERE id = 1;
```

**Event Sourcing Approach**:
```sql
INSERT INTO events (aggregate_id, event_type, data, timestamp)
VALUES (1, 'MoneyDeposited', '{"amount": 500}', NOW());
```

### Benefits
- Complete audit trail
- Temporal queries (state at any point in time)
- Event replay for debugging
- Natural fit for event-driven architecture

### Challenges
- More complex queries
- Event schema evolution
- Storage requirements
- Performance considerations

---

## CQRS Pattern

### Command Query Responsibility Segregation

```
┌─────────────────────────────────────────────────┐
│                  Application                     │
└────────┬────────────────────────────┬───────────┘
         │                            │
    Commands                      Queries
         │                            │
┌────────▼────────┐         ┌────────▼────────┐
│  Write Model    │         │   Read Model    │
│  (Normalized)   │────────►│  (Denormalized) │
│                 │ Events  │                 │
└─────────────────┘         └─────────────────┘
```

### Benefits
- Optimize read and write models separately
- Scale reads and writes independently
- Simpler queries
- Better performance

---

# Part 8: Deployment and DevOps

## 🚀 Running Microservices in Production

---

## Containerization with Docker

### Why Containers?

- **Consistency**: Same environment everywhere
- **Isolation**: Each service in its own container
- **Portability**: Run anywhere Docker runs
- **Efficiency**: Lightweight compared to VMs
- **Scalability**: Easy to scale up/down

### Dockerfile Example

```dockerfile
FROM icr.io/appcafe/open-liberty:full-java17-openj9-ubi

# Copy application
COPY --chown=1001:0 target/*.war /config/apps/

# Copy server configuration
COPY --chown=1001:0 src/main/liberty/config/server.xml /config/

# Expose ports
EXPOSE 9080 9443

# Run as non-root
USER 1001

# Start server
CMD ["/opt/ol/wlp/bin/server", "run", "defaultServer"]
```

---

## Docker Compose for Local Development

### Complete Stack

```yaml
version: '3.8'

services:
  # Databases
  client-db:
    image: postgres:16
    environment:
      POSTGRES_DB: banking_client_db
      POSTGRES_USER: bankuser
      POSTGRES_PASSWORD: bankpass
    ports:
      - "5433:5432"
    volumes:
      - client-db-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U bankuser"]
      interval: 10s
      timeout: 5s
      retries: 5

  account-db:
    image: postgres:16
    environment:
      POSTGRES_DB: banking_account_db
      POSTGRES_USER: bankuser
      POSTGRES_PASSWORD: bankpass
    ports:
      - "5434:5432"
    volumes:
      - account-db-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U bankuser"]
      interval: 10s
      timeout: 5s
      retries: 5
```

---

## Docker Compose (Continued)

```yaml
  # Services
  client-service:
    build:
      context: ./client-service
      dockerfile: Containerfile
    ports:
      - "9081:9080"
    environment:
      DB_HOST: client-db
      DB_PORT: 5432
      DB_NAME: banking_client_db
      DB_USER: bankuser
      DB_PASSWORD: bankpass
    depends_on:
      client-db:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9080/health/live"]
      interval: 30s
      timeout: 10s
      retries: 3

  account-service:
    build:
      context: ./account-service
      dockerfile: Containerfile
    ports:
      - "9082:9080"
    environment:
      DB_HOST: account-db
      DB_PORT: 5432
      DB_NAME: banking_account_db
      DB_USER: bankuser
      DB_PASSWORD: bankpass
      CLIENT_SERVICE_URL: http://client-service:9080
    depends_on:
      account-db:
        condition: service_healthy
      client-service:
        condition: service_healthy
```

---

## Docker Compose (Continued)

```yaml
  api-gateway:
    build:
      context: ./api-gateway
      dockerfile: Containerfile
    ports:
      - "9080:9080"
    environment:
      CLIENT_SERVICE_URL: http://client-service:9080
      ACCOUNT_SERVICE_URL: http://account-service:9080
    depends_on:
      - client-service
      - account-service
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9080/health/live"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  client-db-data:
  account-db-data:

networks:
  default:
    name: banking-network
```

---

## Running with Docker Compose

### Commands

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f account-service

# Check status
docker-compose ps

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Rebuild and restart
docker-compose up -d --build
```

---

## Kubernetes Introduction

### Why Kubernetes?

- **Orchestration**: Manage multiple containers
- **Self-Healing**: Restart failed containers
- **Scaling**: Horizontal pod autoscaling
- **Load Balancing**: Distribute traffic
- **Rolling Updates**: Zero-downtime deployments
- **Service Discovery**: Built-in DNS

### Basic Concepts

- **Pod**: Smallest deployable unit (one or more containers)
- **Deployment**: Manages pod replicas
- **Service**: Stable network endpoint
- **ConfigMap**: Configuration data
- **Secret**: Sensitive data
- **Ingress**: External access

---

## Kubernetes Deployment Example

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: account-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: account-service
  template:
    metadata:
      labels:
        app: account-service
    spec:
      containers:
      - name: account-service
        image: account-service:1.0.0
        ports:
        - containerPort: 9080
        env:
        - name: DB_HOST
          valueFrom:
            configMapKeyRef:
              name: account-config
              key: db.host
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: account-secrets
              key: db.password
        livenessProbe:
          httpGet:
            path: /health/live
            port: 9080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /health/ready
            port: 9080
          initialDelaySeconds: 10
          periodSeconds: 5
```

---

## Kubernetes Service Example

```yaml
apiVersion: v1
kind: Service
metadata:
  name: account-service
spec:
  selector:
    app: account-service
  ports:
  - protocol: TCP
    port: 80
    targetPort: 9080
  type: ClusterIP
---
apiVersion: v1
kind: Service
metadata:
  name: api-gateway
spec:
  selector:
    app: api-gateway
  ports:
  - protocol: TCP
    port: 80
    targetPort: 9080
  type: LoadBalancer
```

---

## CI/CD Pipeline

### Continuous Integration/Continuous Deployment

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│   Code   │────►│  Build   │────►│   Test   │────►│  Deploy  │
│  Commit  │     │          │     │          │     │          │
└──────────┘     └──────────┘     └──────────┘     └──────────┘
     │                │                │                │
     │                │                │                │
   GitHub          Maven           JUnit          Kubernetes
                                  Integration
                                    Tests
```

### Pipeline Stages

1. **Source**: Code commit triggers pipeline
2. **Build**: Compile code, run unit tests
3. **Test**: Integration tests, security scans
4. **Package**: Build Docker image
5. **Deploy**: Deploy to environment (dev/staging/prod)
6. **Verify**: Smoke tests, health checks

---

## Deployment Strategies

### 1. **Blue-Green Deployment**

```
┌─────────────┐
│Load Balancer│
└──────┬──────┘
       │
       │ Switch traffic
       │
   ┌───▼────┐        ┌─────────┐
   │ Blue   │        │ Green   │
   │(v1.0)  │        │ (v2.0)  │
   │Active  │───────►│ Standby │
   └────────┘        └─────────┘
```

**Benefits**: Instant rollback, zero downtime
**Drawbacks**: Requires double resources

---

## Deployment Strategies (Continued)

### 2. **Canary Deployment**

```
┌─────────────┐
│Load Balancer│
└──────┬──────┘
       │
       ├─────────────┐
       │             │
   ┌───▼────┐    ┌──▼─────┐
   │ v1.0   │    │ v2.0   │
   │  90%   │    │  10%   │
   └────────┘    └────────┘
```

**Benefits**: Gradual rollout, early problem detection
**Drawbacks**: Complex routing, longer deployment time

### 3. **Rolling Update**

```
Step 1: [v1] [v1] [v1] [v1]
Step 2: [v2] [v1] [v1] [v1]
Step 3: [v2] [v2] [v1] [v1]
Step 4: [v2] [v2] [v2] [v1]
Step 5: [v2] [v2] [v2] [v2]
```

**Benefits**: No downtime, no extra resources
**Drawbacks**: Slower rollback, mixed versions running

---

## Monitoring and Alerting

### Key Metrics to Monitor

**Service Metrics**:
- Request rate (requests/second)
- Error rate (errors/total requests)
- Response time (p50, p95, p99)
- Availability (uptime percentage)

**Infrastructure Metrics**:
- CPU usage
- Memory usage
- Disk I/O
- Network traffic

**Business Metrics**:
- Accounts created
- Transactions processed
- Revenue generated
- User activity

---

## Monitoring Stack

### Common Tools

```
┌─────────────────────────────────────────────┐
│              Grafana (Visualization)         │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│         Prometheus (Metrics Storage)         │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│     MicroProfile Metrics (Exporters)         │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│           Microservices                      │
└──────────────────────────────────────────────┘
```

---

## Logging Stack (ELK)

```
┌─────────────────────────────────────────────┐
│         Kibana (Visualization)               │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│      Elasticsearch (Storage & Search)        │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│       Logstash (Processing)                  │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│     Filebeat (Log Shipping)                  │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│           Microservices                      │
└──────────────────────────────────────────────┘
```

---

# Summary and Best Practices

## 📝 Key Takeaways

---

## Microservices Principles

### 1. **Single Responsibility**
- Each service does one thing well
- Clear boundaries
- Focused business capability

### 2. **Autonomy**
- Independent deployment
- Own database
- Minimal dependencies

### 3. **Resilience**
- Design for failure
- Circuit breakers
- Graceful degradation

---

## Best Practices

### Service Design
✅ Keep services small and focused
✅ Design for failure
✅ Use asynchronous communication when possible
✅ Implement proper error handling
✅ Version your APIs

### Data Management
✅ Database per service
✅ Avoid distributed transactions
✅ Use eventual consistency
✅ Implement compensation logic
✅ Consider event sourcing for audit trails

---

## Best Practices (Continued)

### Communication
✅ Use REST for synchronous communication
✅ Use messaging for asynchronous communication
✅ Implement circuit breakers
✅ Use correlation IDs
✅ Handle timeouts and retries

### Observability
✅ Implement health checks
✅ Collect metrics
✅ Use distributed tracing
✅ Centralize logs
✅ Monitor business metrics

---

## Best Practices (Continued)

### Security
✅ Secure service-to-service communication
✅ Implement authentication at gateway
✅ Use HTTPS in production
✅ Validate all inputs
✅ Keep secrets in secure storage

### Deployment
✅ Containerize services
✅ Use orchestration (Kubernetes)
✅ Implement CI/CD pipelines
✅ Use blue-green or canary deployments
✅ Automate everything

---

## Common Pitfalls to Avoid

### 1. **Too Many Microservices**
- Don't create nano-services
- Start with a monolith, split when needed
- Consider team size and complexity

### 2. **Shared Database**
- Defeats purpose of microservices
- Creates tight coupling
- Prevents independent scaling

### 3. **Ignoring Network Issues**
- Network is unreliable
- Implement retries and timeouts
- Use circuit breakers

---

## Common Pitfalls (Continued)

### 4. **Insufficient Monitoring**
- Can't debug what you can't see
- Implement comprehensive logging
- Use distributed tracing
- Monitor all layers

### 5. **Complex Distributed Transactions**
- Avoid if possible
- Use saga pattern
- Accept eventual consistency
- Design for compensation

### 6. **Not Testing Failure Scenarios**
- Test circuit breakers
- Test timeouts
- Test partial failures
- Use chaos engineering

---

## Migration Strategy

### From Monolith to Microservices

**Phase 1: Preparation**
1. Identify bounded contexts
2. Define service boundaries
3. Plan data migration
4. Set up infrastructure

**Phase 2: Strangler Pattern**
1. Route new features to microservices
2. Gradually migrate existing features
3. Keep monolith running
4. Reduce monolith over time

**Phase 3: Complete Migration**
1. Migrate remaining features
2. Decommission monolith
3. Optimize microservices
4. Continuous improvement

---

## When to Use Microservices

### ✅ Good Fit
- Large, complex applications
- Multiple teams
- Different scaling requirements
- Rapid deployment needed
- Technology diversity required
- High availability critical

### ❌ Not Recommended
- Small, simple applications
- Single team
- Tight coupling
- Limited operational expertise
- Startup/MVP phase
- Unclear requirements

---

## Lab 08 Preview

### What You'll Build

**Three Microservices**:
1. **Client Service** (Port 9081)
   - Client CRUD operations
   - PostgreSQL database

2. **Account Service** (Port 9082)
   - Account CRUD operations
   - Transaction operations
   - Calls Client Service
   - PostgreSQL database

3. **API Gateway** (Port 9080)
   - Request routing
   - Response aggregation
   - Authentication

---

## Lab 08 Preview (Continued)

### Technologies Used
- Jakarta EE 10
- MicroProfile (Config, Rest Client, Fault Tolerance, Health, Metrics, OpenAPI)
- PostgreSQL 16
- Docker Compose
- Open Liberty

### Learning Outcomes
- Decompose monolith into microservices
- Implement inter-service communication
- Apply fault tolerance patterns
- Configure service discovery
- Implement distributed tracing
- Deploy with Docker Compose

---

## Next Lecture

### Advanced Topics (Optional)

- **Event-Driven Architecture**: Kafka, RabbitMQ
- **Service Mesh**: Istio, Linkerd
- **API Management**: Kong, Apigee
- **Serverless**: AWS Lambda, Azure Functions
- **GraphQL**: Alternative to REST
- **gRPC**: High-performance RPC
- **Kubernetes Advanced**: Operators, Custom Resources
- **Security**: OAuth2, JWT, mTLS

---

## Resources

### Books
- "Building Microservices" by Sam Newman
- "Microservices Patterns" by Chris Richardson
- "Release It!" by Michael Nygard
- "The Phoenix Project" by Gene Kim

### Online
- [Microservices.io](https://microservices.io/) - Patterns catalog
- [MicroProfile.io](https://microprofile.io/) - Specifications
- [12factor.net](https://12factor.net/) - Best practices
- [Martin Fowler's Blog](https://martinfowler.com/) - Architecture articles

---

## Questions?

### Discussion Topics
- When would you choose microservices over a monolith?
- How do you handle data consistency across services?
- What are the biggest challenges in microservices?
- How do you test microservices effectively?
- What monitoring tools have you used?

---

## Thank You!

### Ready for Lab 08?

**Next Steps**:
1. Review lecture materials
2. Read Lab 08 instructions
3. Set up Docker environment
4. Start with Client Service
5. Build Account Service
6. Implement API Gateway
7. Test complete system

**Remember**: Microservices are about trade-offs. Choose wisely based on your requirements!

---

# End of Lecture 8

**See you in Lab 08!** 🚀
