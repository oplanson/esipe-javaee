<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 08: Microservices Architecture

## From Monolith to Microservices

**Duration:** 4 hours  
**Difficulty:** Advanced  
**Prerequisites:** Labs 1-7 (especially Lab 7: Hexagonal Architecture)

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Learning Objectives](#learning-objectives)
3. [Architecture](#architecture)
4. [Prerequisites](#prerequisites)
5. [Lab Structure](#lab-structure)
6. [Part 1: Understanding the Decomposition](#part-1-understanding-the-decomposition)
7. [Part 2: Client Service](#part-2-client-service)
8. [Part 3: Account Service](#part-3-account-service)
9. [Part 4: API Gateway](#part-4-api-gateway)
10. [Part 5: Docker Compose Configuration](#part-5-docker-compose-configuration)
11. [Part 6: Testing](#part-6-testing)
12. [Part 7: Deployment](#part-7-deployment)
13. [Troubleshooting](#troubleshooting)
14. [Best Practices](#best-practices)
15. [Next Steps](#next-steps)

---

## Overview

In this lab, you will decompose the Lab 07 hexagonal architecture monolithic banking application into three microservices:

1. **Client Service** - Manages client information
2. **Account Service** - Manages accounts and transactions
3. **API Gateway** - Routes requests and aggregates responses

This lab demonstrates:
- Service decomposition strategies
- Inter-service communication with MicroProfile Rest Client
- Fault tolerance with circuit breakers and retries
- Service discovery and configuration
- Distributed tracing and monitoring
- Database per service pattern
- Deployment with Docker Compose

---

## Learning Objectives

By completing this lab, you will be able to:

1. ✅ Decompose a monolithic application into microservices
2. ✅ Implement the database per service pattern
3. ✅ Use MicroProfile Rest Client for inter-service communication
4. ✅ Apply fault tolerance patterns (circuit breaker, retry, timeout)
5. ✅ Configure services with MicroProfile Config
6. ✅ Implement health checks for each service
7. ✅ Collect metrics from distributed services
8. ✅ Deploy microservices with Docker Compose
9. ✅ Test microservices independently and as a system
10. ✅ Handle distributed data consistency

---

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   API Gateway                            │
│                  (Port 9080)                             │
│  ← Application principale                                │
│  - Request routing                                       │
│  - Response aggregation                                  │
│  - Authentication (basic)                                │
│  - Unified API documentation                             │
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
    │ - Validation    │        │ - Balance Ops    │
    │                 │        │ - Client Verify  │
    └────────┬────────┘        └────────┬─────────┘
             │                          │
             │                          │
    ┌────────▼────────┐        ┌───────▼─────────┐
    │  PostgreSQL     │        │   PostgreSQL    │
    │ banking_client  │        │ banking_account │
    │      _db        │        │      _db        │
    │   (Port 5433)   │        │   (Port 5434)   │
    └─────────────────┘        └─────────────────┘
```

### Architecture de déploiement

L'**API Gateway** (port 9080) est l'application principale et le point d'entrée unique pour tous les clients. Elle route les requêtes vers les microservices appropriés et agrège les réponses.

**Flux de communication:**
1. Client → API Gateway (9080)
2. API Gateway → Client Service (9081) ou Account Service (9082)
3. Account Service → Client Service (9081) pour validation
4. Services → Bases de données respectives

### Service Responsibilities

#### Client Service (Port 9081)
- **Bounded Context**: Client Management
- **Database**: banking_client_db
- **Endpoints**:
  - `GET /api/clients` - List all clients
  - `GET /api/clients/{id}` - Get client by ID
  - `POST /api/clients` - Create client
  - `PUT /api/clients/{id}` - Update client
  - `DELETE /api/clients/{id}` - Delete client
- **MicroProfile Features**:
  - Health checks (liveness, readiness)
  - Metrics
  - OpenAPI documentation
  - Config for database connection

#### Account Service (Port 9082)
- **Bounded Context**: Account Management + Transactions
- **Database**: banking_account_db
- **Endpoints**:
  - `GET /api/accounts` - List all accounts
  - `GET /api/accounts/{id}` - Get account by ID
  - `GET /api/accounts/client/{clientId}` - Get accounts by client
  - `POST /api/accounts` - Create account
  - `PUT /api/accounts/{id}` - Update account
  - `DELETE /api/accounts/{id}` - Delete account
  - `POST /api/accounts/{id}/deposit` - Deposit money
  - `POST /api/accounts/{id}/withdraw` - Withdraw money
  - `POST /api/accounts/transfer` - Transfer between accounts
- **Dependencies**: Client Service (for validation)
- **MicroProfile Features**:
  - Health checks (liveness, readiness)
  - Metrics
  - OpenAPI documentation
  - Rest Client to call Client Service
  - Fault Tolerance (circuit breaker, retry, timeout)
  - Config for database and client service URL

#### API Gateway (Port 9080)
- **Responsibilities**:
  - Route requests to appropriate services
  - Aggregate responses from multiple services
  - Handle authentication (basic implementation)
  - Provide unified API documentation
- **No Database**: Stateless
- **Endpoints**:
  - `GET /api/clients` - Proxy to Client Service
  - `GET /api/clients/{id}` - Proxy to Client Service
  - `GET /api/clients/{id}/accounts` - Aggregate client + accounts
  - `POST /api/clients` - Proxy to Client Service
  - `GET /api/accounts` - Proxy to Account Service
  - `POST /api/accounts` - Proxy to Account Service
  - `POST /api/accounts/{id}/deposit` - Proxy to Account Service
  - `POST /api/accounts/{id}/withdraw` - Proxy to Account Service
  - `POST /api/accounts/transfer` - Proxy to Account Service
- **MicroProfile Features**:
  - Health checks (aggregate from services)
  - Metrics
  - OpenAPI documentation
  - Rest Client for both services
  - Fault Tolerance

---

## Prerequisites

### Software Requirements

- **JDK**: OpenJDK 17 or later
- **Maven**: 3.8 or later
- **Docker or Podman**: Latest version
- **PostgreSQL**: 16 or later (via Docker)
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code
- **Git**: For version control

### Knowledge Requirements

- Completion of Labs 1-7
- Understanding of hexagonal architecture
- Basic Docker and Docker Compose knowledge
- REST API design principles
- Understanding of distributed systems concepts

### Verify Prerequisites

```bash
# Check Java version
java -version  # Should show 17+

# Check Maven version
mvn -version   # Should show 3.8+

# Check Docker/Podman
docker --version  # or podman --version

# Check PostgreSQL client (optional)
psql --version
```

---

## Lab Structure

```
Lab08-Microservices/
├── README.md                          # This file
├── SOLUTION-STATUS.md                 # Implementation status
├── docker-compose.yml                 # Multi-service orchestration
├── podman-test.sh                     # Podman deployment script
├── docker-test.sh                     # Docker deployment script
├── run-lab.sh                         # Development mode script
├── test-lab.sh                        # Build verification script
│
├── solution/                          # Complete solution
│   ├── client-service/               # Client microservice
│   │   ├── pom.xml
│   │   ├── Containerfile
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/com/bank/
│   │       │   │   ├── domain/       # Domain layer
│   │       │   │   ├── application/  # Application layer
│   │       │   │   └── infrastructure/ # Infrastructure layer
│   │       │   ├── resources/
│   │       │   │   ├── META-INF/
│   │       │   │   │   ├── persistence.xml
│   │       │   │   │   └── microprofile-config.properties
│   │       │   │   └── db/migration/ # Flyway migrations
│   │       │   └── liberty/config/
│   │       │       └── server.xml
│   │       └── test/
│   │
│   ├── account-service/              # Account microservice
│   │   ├── pom.xml
│   │   ├── Containerfile
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/com/bank/
│   │       │   │   ├── domain/       # Domain layer
│   │       │   │   ├── application/  # Application layer
│   │       │   │   ├── infrastructure/ # Infrastructure layer
│   │       │   │   └── client/       # Rest Client interfaces
│   │       │   ├── resources/
│   │       │   │   ├── META-INF/
│   │       │   │   │   ├── persistence.xml
│   │       │   │   │   └── microprofile-config.properties
│   │       │   │   └── db/migration/ # Flyway migrations
│   │       │   └── liberty/config/
│   │       │       └── server.xml
│   │       └── test/
│   │
│   ├── api-gateway/                  # API Gateway
│   │   ├── pom.xml
│   │   ├── Containerfile
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/com/bank/
│   │       │   │   ├── gateway/      # Gateway logic
│   │       │   │   ├── client/       # Rest Client interfaces
│   │       │   │   └── dto/          # DTOs
│   │       │   ├── resources/
│   │       │   │   └── META-INF/
│   │       │   │       └── microprofile-config.properties
│   │       │   └── liberty/config/
│   │       │       └── server.xml
│   │       └── test/
│   │
│   └── docker-compose.yml            # Service orchestration
│
└── starter/                          # Starter code for students
    ├── client-service/               # Partial implementation
    ├── account-service/              # Partial implementation
    └── api-gateway/                  # Partial implementation
```

---

## Part 1: Understanding the Decomposition

### From Lab 07 to Lab 08

#### Lab 07 (Monolith)
- Single application (WAR file)
- Single database
- All features in one deployment
- Hexagonal architecture with ports and adapters

#### Lab 08 (Microservices)
- Three separate applications
- Two databases (client_db, account_db)
- Independent deployment
- Same hexagonal architecture per service

### Decomposition Strategy

We use **Domain-Driven Design** to identify service boundaries:

1. **Client Bounded Context** → Client Service
   - Client entity and value objects
   - Client management use cases
   - Client repository

2. **Account Bounded Context** → Account Service
   - Account entity and value objects
   - Account management use cases
   - Transaction operations
   - Account repository

3. **API Gateway** → Cross-cutting concerns
   - Request routing
   - Response aggregation
   - Authentication
   - Unified API

### Database Per Service Pattern

Each service has its own database:

```
Client Service → banking_client_db
  Tables:
  - clients (id, name, email, phone, address, premium, created_at, updated_at)

Account Service → banking_account_db
  Tables:
  - accounts (id, account_number, balance, type, status, client_id, created_at, updated_at)
  
Note: client_id in accounts table is a reference, not a foreign key
```

**Why no foreign key?**
- Services are loosely coupled
- Client Service can be updated independently
- Account Service validates client existence via REST call

---

## Part 2: Client Service

### Step 1: Project Setup

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.bank</groupId>
    <artifactId>client-service</artifactId>
    <version>1.0.0</version>
    <packaging>war</packaging>

    <name>Banking Client Service</name>
    <description>Client microservice for banking application</description>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <liberty.var.http.port>9080</liberty.var.http.port>
        <liberty.var.https.port>9443</liberty.var.https.port>
    </properties>

    <dependencies>
        <!-- Jakarta EE -->
        <dependency>
            <groupId>jakarta.platform</groupId>
            <artifactId>jakarta.jakartaee-api</artifactId>
            <version>10.0.0</version>
            <scope>provided</scope>
        </dependency>

        <!-- MicroProfile -->
        <dependency>
            <groupId>org.eclipse.microprofile</groupId>
            <artifactId>microprofile</artifactId>
            <version>6.0</version>
            <type>pom</type>
            <scope>provided</scope>
        </dependency>

        <!-- PostgreSQL Driver -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.1</version>
        </dependency>

        <!-- Flyway -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
            <version>10.4.1</version>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
            <version>10.4.1</version>
        </dependency>
    </dependencies>

    <build>
        <finalName>client-service</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.4.0</version>
            </plugin>
            <plugin>
                <groupId>io.openliberty.tools</groupId>
                <artifactId>liberty-maven-plugin</artifactId>
                <version>3.10</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 2: Domain Layer

The domain layer remains pure (no framework dependencies):

#### Client.java

```java
package com.bank.domain.model;

import com.bank.domain.valueobject.Email;
import java.time.LocalDateTime;

public class Client {
    private Long id;
    private String name;
    private Email email;
    private String phone;
    private String address;
    private boolean premium;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor, getters, business methods
    
    public void upgradeToPremium() {
        if (!this.premium) {
            this.premium = true;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void downgradeToStandard() {
        if (this.premium) {
            this.premium = false;
            this.updatedAt = LocalDateTime.now();
        }
    }
}
```

#### Email.java (Value Object)

```java
package com.bank.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private final String value;
    
    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
```

### Step 3: Application Layer

#### Primary Port (Use Case Interface)

```java
package com.bank.application.port.in;

import com.bank.application.command.CreateClientCommand;
import com.bank.application.command.UpdateClientCommand;
import com.bank.application.dto.ClientDTO;
import java.util.List;

public interface ClientManagementUseCase {
    ClientDTO createClient(CreateClientCommand command);
    ClientDTO updateClient(Long id, UpdateClientCommand command);
    void deleteClient(Long id);
    ClientDTO getClient(Long id);
    List<ClientDTO> getAllClients();
    void upgradeToPremium(Long id);
    void downgradeToStandard(Long id);
}
```

#### Commands

```java
package com.bank.application.command;

public record CreateClientCommand(
    String name,
    String email,
    String phone,
    String address
) {}
```

```java
package com.bank.application.command;

public record UpdateClientCommand(
    String name,
    String email,
    String phone,
    String address,
    Boolean premium
) {}
```

#### DTOs

```java
package com.bank.application.dto;

import java.time.LocalDateTime;

public record ClientDTO(
    Long id,
    String name,
    String email,
    String phone,
    String address,
    boolean premium,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

#### Service Implementation

```java
package com.bank.application.service;

import com.bank.application.command.CreateClientCommand;
import com.bank.application.command.UpdateClientCommand;
import com.bank.application.dto.ClientDTO;
import com.bank.application.port.in.ClientManagementUseCase;
import com.bank.application.port.out.ClientRepository;
import com.bank.domain.model.Client;
import com.bank.domain.valueobject.Email;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class ClientManagementService implements ClientManagementUseCase {
    
    @Inject
    ClientRepository clientRepository;
    
    @Override
    public ClientDTO createClient(CreateClientCommand command) {
        Client client = new Client();
        client.setName(command.name());
        client.setEmail(new Email(command.email()));
        client.setPhone(command.phone());
        client.setAddress(command.address());
        client.setPremium(false);
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());
        
        clientRepository.save(client);
        
        return toDTO(client);
    }
    
    @Override
    public ClientDTO updateClient(Long id, UpdateClientCommand command) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Client not found: " + id));
        
        client.setName(command.name());
        client.setEmail(new Email(command.email()));
        client.setPhone(command.phone());
        client.setAddress(command.address());
        if (command.premium() != null) {
            client.setPremium(command.premium());
        }
        client.setUpdatedAt(LocalDateTime.now());
        
        clientRepository.save(client);
        
        return toDTO(client);
    }
    
    @Override
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
    
    @Override
    public ClientDTO getClient(Long id) {
        return clientRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new NotFoundException("Client not found: " + id));
    }
    
    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public void upgradeToPremium(Long id) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Client not found: " + id));
        client.upgradeToPremium();
        clientRepository.save(client);
    }
    
    @Override
    public void downgradeToStandard(Long id) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Client not found: " + id));
        client.downgradeToStandard();
        clientRepository.save(client);
    }
    
    private ClientDTO toDTO(Client client) {
        return new ClientDTO(
            client.getId(),
            client.getName(),
            client.getEmail().getValue(),
            client.getPhone(),
            client.getAddress(),
            client.isPremium(),
            client.getCreatedAt(),
            client.getUpdatedAt()
        );
    }
}
```

### Step 4: Infrastructure Layer

#### Secondary Port (Repository Interface)

```java
package com.bank.application.port.out;

import com.bank.domain.model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    void save(Client client);
    Optional<Client> findById(Long id);
    List<Client> findAll();
    void deleteById(Long id);
}
```

#### JPA Entity

```java
package com.bank.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
public class ClientEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String phone;
    
    private String address;
    
    @Column(nullable = false)
    private boolean premium = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Getters and setters
}
```

#### JPA Adapter

```java
package com.bank.infrastructure.persistence.adapter;

import com.bank.application.port.out.ClientRepository;
import com.bank.domain.model.Client;
import com.bank.infrastructure.persistence.entity.ClientEntity;
import com.bank.infrastructure.persistence.mapper.ClientMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class JpaClientAdapter implements ClientRepository {
    
    @Inject
    EntityManager entityManager;
    
    @Inject
    ClientMapper mapper;
    
    @Override
    public void save(Client client) {
        if (client.getId() == null) {
            ClientEntity entity = mapper.toEntity(client);
            entityManager.persist(entity);
            entityManager.flush();
            client.setId(entity.getId());
        } else {
            ClientEntity entity = entityManager.find(ClientEntity.class, client.getId());
            if (entity != null) {
                mapper.updateEntity(client, entity);
                entityManager.merge(entity);
            }
        }
    }
    
    @Override
    public Optional<Client> findById(Long id) {
        ClientEntity entity = entityManager.find(ClientEntity.class, id);
        return Optional.ofNullable(entity).map(mapper::toDomain);
    }
    
    @Override
    public List<Client> findAll() {
        return entityManager.createQuery("SELECT c FROM ClientEntity c", ClientEntity.class)
            .getResultList()
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(Long id) {
        ClientEntity entity = entityManager.find(ClientEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
}
```

#### Mapper

```java
package com.bank.infrastructure.persistence.mapper;

import com.bank.domain.model.Client;
import com.bank.domain.valueobject.Email;
import com.bank.infrastructure.persistence.entity.ClientEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClientMapper {
    
    public ClientEntity toEntity(Client client) {
        ClientEntity entity = new ClientEntity();
        entity.setId(client.getId());
        entity.setName(client.getName());
        entity.setEmail(client.getEmail().getValue());
        entity.setPhone(client.getPhone());
        entity.setAddress(client.getAddress());
        entity.setPremium(client.isPremium());
        entity.setCreatedAt(client.getCreatedAt());
        entity.setUpdatedAt(client.getUpdatedAt());
        return entity;
    }
    
    public Client toDomain(ClientEntity entity) {
        Client client = new Client();
        client.setId(entity.getId());
        client.setName(entity.getName());
        client.setEmail(new Email(entity.getEmail()));
        client.setPhone(entity.getPhone());
        client.setAddress(entity.getAddress());
        client.setPremium(entity.isPremium());
        client.setCreatedAt(entity.getCreatedAt());
        client.setUpdatedAt(entity.getUpdatedAt());
        return client;
    }
    
    public void updateEntity(Client client, ClientEntity entity) {
        entity.setName(client.getName());
        entity.setEmail(client.getEmail().getValue());
        entity.setPhone(client.getPhone());
        entity.setAddress(client.getAddress());
        entity.setPremium(client.isPremium());
        entity.setUpdatedAt(client.getUpdatedAt());
    }
}
```

#### REST Adapter

```java
package com.bank.infrastructure.rest.adapter;

import com.bank.application.command.CreateClientCommand;
import com.bank.application.command.UpdateClientCommand;
import com.bank.application.dto.ClientDTO;
import com.bank.application.port.in.ClientManagementUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientRestAdapter {
    
    @Inject
    ClientManagementUseCase clientManagement;
    
    @GET
    public Response getAllClients() {
        List<ClientDTO> clients = clientManagement.getAllClients();
        return Response.ok(clients).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getClient(@PathParam("id") Long id) {
        ClientDTO client = clientManagement.getClient(id);
        return Response.ok(client).build();
    }
    
    @POST
    public Response createClient(CreateClientCommand command) {
        ClientDTO client = clientManagement.createClient(command);
        return Response.status(Response.Status.CREATED).entity(client).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateClient(@PathParam("id") Long id, UpdateClientCommand command) {
        ClientDTO client = clientManagement.updateClient(id, command);
        return Response.ok(client).build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id) {
        clientManagement.deleteClient(id);
        return Response.noContent().build();
    }
    
    @POST
    @Path("/{id}/upgrade")
    public Response upgradeToPremium(@PathParam("id") Long id) {
        clientManagement.upgradeToPremium(id);
        return Response.ok().build();
    }
    
    @POST
    @Path("/{id}/downgrade")
    public Response downgradeToStandard(@PathParam("id") Long id) {
        clientManagement.downgradeToStandard(id);
        return Response.ok().build();
    }
}
```

### Step 5: Configuration

#### server.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<server description="Client Service">
    <featureManager>
        <feature>jakartaee-10.0</feature>
        <feature>microProfile-6.0</feature>
    </featureManager>

    <httpEndpoint id="defaultHttpEndpoint"
                  host="*"
                  httpPort="9080"
                  httpsPort="9443" />

    <dataSource id="clientDS" jndiName="jdbc/clientDS">
        <jdbcDriver libraryRef="PostgreSQLLib"/>
        <properties.postgresql
            serverName="${env.DB_HOST}"
            portNumber="${env.DB_PORT}"
            databaseName="${env.DB_NAME}"
            user="${env.DB_USER}"
            password="${env.DB_PASSWORD}"/>
    </dataSource>

    <library id="PostgreSQLLib">
        <fileset dir="${server.config.dir}/lib" includes="postgresql-*.jar"/>
    </library>

    <webApplication location="client-service.war" contextRoot="/">
        <classloader apiTypeVisibility="spec, ibm-api, third-party"/>
    </webApplication>
</server>
```

#### persistence.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="3.0"
             xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence 
             https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd">
    
    <persistence-unit name="clientPU" transaction-type="JTA">
        <jta-data-source>jdbc/clientDS</jta-data-source>
        <properties>
            <property name="jakarta.persistence.schema-generation.database.action" value="none"/>
            <property name="eclipselink.logging.level" value="INFO"/>
        </properties>
    </persistence-unit>
</persistence>
```

#### microprofile-config.properties

```properties
# Database Configuration
db.host=localhost
db.port=5432
db.name=banking_client_db
db.user=bankuser
db.password=bankpass

# Service Configuration
service.name=client-service
service.version=1.0.0
```

### Step 6: Database Migration

#### V1__create_clients_table.sql

```sql
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    address TEXT,
    premium BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_clients_premium ON clients(premium);
```

### Step 7: Health Checks

```java
package com.bank.infrastructure.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("client-service-live");
    }
}

@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {
    
    @Inject
    EntityManager entityManager;
    
    @Override
    public HealthCheckResponse call() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return HealthCheckResponse.up("client-service-ready");
        } catch (Exception e) {
            return HealthCheckResponse.down("client-service-not-ready");
        }
    }
}
```

### Step 8: Containerfile

```dockerfile
FROM icr.io/appcafe/open-liberty:full-java17-openj9-ubi

# Copy PostgreSQL driver
COPY --chown=1001:0 target/liberty/wlp/usr/servers/defaultServer/lib/*.jar /opt/ol/wlp/usr/servers/defaultServer/lib/

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

## Part 3: Account Service

The Account Service is similar to Client Service but includes:
- Inter-service communication with Client Service
- Fault tolerance (circuit breaker, retry, timeout)
- More complex business logic (transactions)

### Key Differences

#### 1. MicroProfile Rest Client

```java
package com.bank.client;

import com.bank.application.dto.ClientDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://client-service:9080")
@Path("/api/clients")
public interface ClientServiceClient {
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientDTO getClient(@PathParam("id") Long id);
}
```

#### 2. Fault Tolerance

```java
package com.bank.application.service;

import com.bank.application.dto.ClientDTO;
import com.bank.client.ClientServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ClientVerificationService {
    
    @Inject
    @RestClient
    ClientServiceClient clientService;
    
    @CircuitBreaker(
        requestVolumeThreshold = 4,
        failureRatio = 0.5,
        delay = 5000,
        successThreshold = 2
    )
    @Retry(
        maxRetries = 3,
        delay = 1000,
        delayUnit = ChronoUnit.MILLIS
    )
    @Timeout(2000)
    @Fallback(fallbackMethod = "getClientFallback")
    public ClientDTO verifyClient(Long clientId) {
        return clientService.getClient(clientId);
    }
    
    public ClientDTO getClientFallback(Long clientId) {
        // Return minimal client info or throw exception
        throw new ValidationException("Client service unavailable");
    }
}
```

#### 3. Configuration

```properties
# Client Service Configuration
com.bank.client.ClientServiceClient/mp-rest/url=${CLIENT_SERVICE_URL:http://localhost:9081}
com.bank.client.ClientServiceClient/mp-rest/scope=jakarta.inject.Singleton
com.bank.client.ClientServiceClient/mp-rest/connectTimeout=2000
com.bank.client.ClientServiceClient/mp-rest/readTimeout=5000

# Circuit Breaker Configuration
CircuitBreaker/requestVolumeThreshold=4
CircuitBreaker/failureRatio=0.5
CircuitBreaker/delay=5000
CircuitBreaker/successThreshold=2

# Retry Configuration
Retry/maxRetries=3
Retry/delay=1000

# Timeout Configuration
Timeout/value=2000
```

---

## Part 4: API Gateway

The API Gateway routes requests and aggregates responses.

### Key Components

#### 1. Gateway Resource

```java
package com.bank.gateway;

import com.bank.application.dto.ClientDTO;
import com.bank.application.dto.AccountDTO;
import com.bank.application.dto.ClientWithAccountsDTO;
import com.bank.client.ClientServiceClient;
import com.bank.client.AccountServiceClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
    public Response getClients() {
        List<ClientDTO> clients = clientService.getAllClients();
        return Response.ok(clients).build();
    }
    
    @GET
    @Path("/clients/{id}")
    public Response getClient(@PathParam("id") Long id) {
        ClientDTO client = clientService.getClient(id);
        return Response.ok(client).build();
    }
    
    // Aggregate client with accounts
    @GET
    @Path("/clients/{id}/accounts")
    public Response getClientWithAccounts(@PathParam("id") Long clientId) {
        ClientDTO client = clientService.getClient(clientId);
        List<AccountDTO> accounts = accountService.getAccountsByClient(clientId);
        
        ClientWithAccountsDTO response = new ClientWithAccountsDTO(
            client.id(),
            client.name(),
            client.email(),
            client.premium(),
            accounts
        );
        
        return Response.ok(response).build();
    }
    
    // Route to Account Service
    @GET
    @Path("/accounts")
    public Response getAccounts() {
        List<AccountDTO> accounts = accountService.getAllAccounts();
        return Response.ok(accounts).build();
    }
    
    @POST
    @Path("/accounts/{id}/deposit")
    public Response deposit(@PathParam("id") Long id, DepositCommand command) {
        AccountDTO account = accountService.deposit(id, command);
        return Response.ok(account).build();
    }
}
```

#### 2. Aggregated Health Check

```java
package com.bank.gateway.health;

import com.bank.client.ClientServiceClient;
import com.bank.client.AccountServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Readiness
@ApplicationScoped
public class ServicesHealthCheck implements HealthCheck {
    
    @Inject
    @RestClient
    ClientServiceClient clientService;
    
    @Inject
    @RestClient
    AccountServiceClient accountService;
    
    @Override
    public HealthCheckResponse call() {
        boolean clientServiceUp = checkService(() -> clientService.getAllClients());
        boolean accountServiceUp = checkService(() -> accountService.getAllAccounts());
        
        if (clientServiceUp && accountServiceUp) {
            return HealthCheckResponse.up("all-services-ready");
        } else {
            return HealthCheckResponse.down("some-services-not-ready")
                .withData("client-service", clientServiceUp)
                .withData("account-service", accountServiceUp)
                .build();
        }
    }
    
    private boolean checkService(Runnable check) {
        try {
            check.run();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

---

## Part 5: Docker Compose Configuration

### docker-compose.yml

```yaml
version: '3.8'

services:
  # Client Service Database
  client-db:
    image: postgres:16
    container_name: banking-client-db
    environment:
      POSTGRES_DB: banking_client_db
      POSTGRES_USER: bankuser
      POSTGRES_PASSWORD: bankpass
    ports:
      - "5433:5432"
    volumes:
      - client-db-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U bankuser -d banking_client_db"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - banking-network

  # Account Service Database
  account-db:
    image: postgres:16
    container_name: banking-account-db
    environment:
      POSTGRES_DB: banking_account_db
      POSTGRES_USER: bankuser
      POSTGRES_PASSWORD: bankpass
    ports:
      - "5434:5432"
    volumes:
      - account-db-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U bankuser -d banking_account_db"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - banking-network

  # Client Service
  client-service:
    build:
      context: ./solution/client-service
      dockerfile: Containerfile
    container_name: banking-client-service
    ports:
      - "9081:9080"
      - "9444:9443"
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
      start_period: 60s
    networks:
      - banking-network

  # Account Service
  account-service:
    build:
      context: ./solution/account-service
      dockerfile: Containerfile
    container_name: banking-account-service
    ports:
      - "9082:9080"
      - "9445:9443"
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
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9080/health/live"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    networks:
      - banking-network

  # API Gateway
  api-gateway:
    build:
      context: ./solution/api-gateway
      dockerfile: Containerfile
    container_name: banking-api-gateway
    ports:
      - "9080:9080"
      - "9443:9443"
    environment:
      CLIENT_SERVICE_URL: http://client-service:9080
      ACCOUNT_SERVICE_URL: http://account-service:9080
    depends_on:
      client-service:
        condition: service_healthy
      account-service:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9080/health/live"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    networks:
      - banking-network

volumes:
  client-db-data:
  account-db-data:

networks:
  banking-network:
    driver: bridge
```

---

## Part 6: Testing

### Test Scenarios

1. **Health Checks**
   - Client Service liveness
   - Client Service readiness
   - Account Service liveness
   - Account Service readiness
   - API Gateway liveness
   - API Gateway readiness (aggregate)

2. **Client Service**
   - Create client
   - Get client
   - Update client
   - Delete client
   - Upgrade to premium
   - Downgrade to standard

3. **Account Service**
   - Create account (with client verification)
   - Get account
   - Deposit money
   - Withdraw money
   - Transfer between accounts
   - Get accounts by client

4. **API Gateway**
   - Route to Client Service
   - Route to Account Service
   - Aggregate client with accounts

5. **Fault Tolerance**
   - Circuit breaker (stop Client Service, verify fallback)
   - Retry (temporary failure, verify retry)
   - Timeout (slow response, verify timeout)

### Test Script (podman-test.sh)

```bash
#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counter
TESTS_PASSED=0
TESTS_FAILED=0

# Function to print test result
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ PASS${NC}: $2"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}✗ FAIL${NC}: $2"
        ((TESTS_FAILED++))
    fi
}

echo "======================================"
echo "Lab 08: Microservices Testing"
echo "======================================"
echo ""

# Build and start services
echo "Building and starting services..."
podman-compose up -d --build

# Wait for services to be ready
echo "Waiting for services to start..."
sleep 60

# Test 1: Client Service Health Check
echo ""
echo "Test 1: Client Service Health Check"
curl -s http://localhost:9081/health/live | grep -q "UP"
print_result $? "Client Service is alive"

# Test 2: Account Service Health Check
echo ""
echo "Test 2: Account Service Health Check"
curl -s http://localhost:9082/health/live | grep -q "UP"
print_result $? "Account Service is alive"

# Test 3: API Gateway Health Check
echo ""
echo "Test 3: API Gateway Health Check"
curl -s http://localhost:9080/health/live | grep -q "UP"
print_result $? "API Gateway is alive"

# Test 4: Create Client via Gateway
echo ""
echo "Test 4: Create Client via Gateway"
CLIENT_RESPONSE=$(curl -s -X POST http://localhost:9080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john.doe@example.com","phone":"1234567890","address":"123 Main St"}')
CLIENT_ID=$(echo $CLIENT_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
[ -n "$CLIENT_ID" ]
print_result $? "Client created with ID: $CLIENT_ID"

# Test 5: Get Client via Gateway
echo ""
echo "Test 5: Get Client via Gateway"
curl -s http://localhost:9080/api/clients/$CLIENT_ID | grep -q "John Doe"
print_result $? "Client retrieved successfully"

# Test 6: Create Account via Gateway
echo ""
echo "Test 6: Create Account via Gateway"
ACCOUNT_RESPONSE=$(curl -s -X POST http://localhost:9080/api/accounts \
  -H "Content-Type: application/json" \
  -d "{\"clientId\":$CLIENT_ID,\"accountType\":\"CHECKING\"}")
ACCOUNT_ID=$(echo $ACCOUNT_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
[ -n "$ACCOUNT_ID" ]
print_result $? "Account created with ID: $ACCOUNT_ID"

# Test 7: Deposit Money via Gateway
echo ""
echo "Test 7: Deposit Money via Gateway"
curl -s -X POST http://localhost:9080/api/accounts/$ACCOUNT_ID/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount":1000.00}' | grep -q "1000"
print_result $? "Money deposited successfully"

# Test 8: Get Client with Accounts (Aggregation)
echo ""
echo "Test 8: Get Client with Accounts (Aggregation)"
curl -s http://localhost:9080/api/clients/$CLIENT_ID/accounts | grep -q "accounts"
print_result $? "Client with accounts retrieved successfully"

# Test 9: Circuit Breaker Test
echo ""
echo "Test 9: Circuit Breaker Test"
echo "Stopping Client Service..."
podman-compose stop client-service
sleep 5
# Try to create account (should fail gracefully)
curl -s -X POST http://localhost:9080/api/accounts \
  -H "Content-Type: application/json" \
  -d "{\"clientId\":999,\"accountType\":\"SAVINGS\"}" | grep -q "error"
print_result $? "Circuit breaker activated"
echo "Restarting Client Service..."
podman-compose start client-service
sleep 30

# Test 10: Metrics Endpoint
echo ""
echo "Test 10: Metrics Endpoint"
curl -s http://localhost:9080/metrics | grep -q "application"
print_result $? "Metrics endpoint accessible"

# Print summary
echo ""
echo "======================================"
echo "Test Summary"
echo "======================================"
echo -e "Tests Passed: ${GREEN}$TESTS_PASSED${NC}"
echo -e "Tests Failed: ${RED}$TESTS_FAILED${NC}"
echo ""

# Cleanup
echo "Cleaning up..."
podman-compose down -v

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed!${NC}"
    exit 1
fi
```

---

## Part 7: Deployment

### Local Development

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Production Considerations

1. **Security**
   - Use HTTPS
   - Implement proper authentication (OAuth2, JWT)
   - Secure service-to-service communication
   - Use secrets management

2. **Scalability**
   - Use Kubernetes for orchestration
   - Implement horizontal pod autoscaling
   - Use load balancers
   - Configure resource limits

3. **Monitoring**
   - Centralized logging (ELK stack)
   - Metrics collection (Prometheus + Grafana)
   - Distributed tracing (Jaeger)
   - Alerting (AlertManager)

4. **Resilience**
   - Multiple replicas per service
   - Circuit breakers configured
   - Retry policies defined
   - Graceful degradation

---

## Troubleshooting

### Common Issues

#### 1. Services Can't Connect

**Problem**: Account Service can't reach Client Service

**Solution**:
- Check Docker network: `docker network ls`
- Verify service names in docker-compose.yml
- Check environment variables
- Review logs: `docker-compose logs account-service`

#### 2. Database Connection Failed

**Problem**: Service can't connect to database

**Solution**:
- Verify database is running: `docker-compose ps`
- Check database credentials
- Verify database name
- Check health checks

#### 3. Circuit Breaker Not Working

**Problem**: Circuit breaker doesn't open

**Solution**:
- Check MicroProfile Fault Tolerance configuration
- Verify failure threshold
- Check logs for exceptions
- Test with actual failures

#### 4. Port Conflicts

**Problem**: Port already in use

**Solution**:
- Check running containers: `docker ps`
- Change ports in docker-compose.yml
- Stop conflicting services

---

## Best Practices

### 1. Service Design
- Keep services small and focused
- Design for failure
- Use asynchronous communication when possible
- Implement proper error handling

### 2. Data Management
- Database per service
- Avoid distributed transactions
- Use eventual consistency
- Implement compensation logic

### 3. Communication
- Use REST for synchronous communication
- Implement circuit breakers
- Use correlation IDs
- Handle timeouts and retries

### 4. Observability
- Implement health checks
- Collect metrics
- Use distributed tracing
- Centralize logs

### 5. Security
- Secure service-to-service communication
- Implement authentication at gateway
- Use HTTPS in production
- Validate all inputs

---

## Next Steps

### Advanced Topics

1. **Event-Driven Architecture**
   - Implement messaging with Kafka or RabbitMQ
   - Use events for inter-service communication
   - Implement event sourcing

2. **Service Mesh**
   - Deploy with Istio or Linkerd
   - Implement mTLS
   - Use service mesh for observability

3. **API Management**
   - Use Kong or Apigee
   - Implement rate limiting
   - Add API analytics

4. **Kubernetes Deployment**
   - Create Kubernetes manifests
   - Implement horizontal pod autoscaling
   - Use Helm charts

5. **Advanced Monitoring**
   - Set up Prometheus and Grafana
   - Implement distributed tracing with Jaeger
   - Configure alerting

---

## Conclusion

Congratulations! You have successfully:

✅ Decomposed a monolithic application into microservices  
✅ Implemented inter-service communication  
✅ Applied fault tolerance patterns  
✅ Configured service discovery  
✅ Implemented distributed tracing  
✅ Deployed with Docker Compose

You now understand the benefits and challenges of microservices architecture and can apply these patterns to real-world applications.

---

## Resources

### Books
- "Building Microservices" by Sam Newman
- "Microservices Patterns" by Chris Richardson
- "Release It!" by Michael Nygard

### Online
- [Microservices.io](https://microservices.io/)
- [MicroProfile.io](https://microprofile.io/)
- [12factor.net](https://12factor.net/)

### Documentation
- [Jakarta EE](https://jakarta.ee/)
- [MicroProfile](https://microprofile.io/)
- [Docker](https://docs.docker.com/)
- [Kubernetes](https://kubernetes.io/docs/)

---

**End of Lab 08**

**Next**: Deploy to production with Kubernetes!