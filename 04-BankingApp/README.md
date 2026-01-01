<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Banking Application - Progressive Implementation
## Jakarta EE Course Final Project

**Purpose:** Complete banking application demonstrating all course concepts  
**Architecture:** Evolves from monolith to microservices  
**Technologies:** Jakarta EE 10, PostgreSQL, Docker

---

## 📋 Overview

This directory contains the complete banking application that students build throughout the course. The application evolves through Git branches, with each branch representing a major milestone in the course.

### Application Features

**Core Functionality:**
- Client management (CRUD operations)
- Account management (multiple accounts per client)
- Balance operations (deposit, withdrawal)
- Money transfers between accounts
- Transaction history
- Account statements

**Technical Features:**
- RESTful API
- JPA persistence
- CDI dependency injection
- Domain-Driven Design
- Hexagonal architecture
- Microservices decomposition

---

## 🌳 Git Branch Structure

The application is organized into progressive branches, each building on the previous:

### Branch 1: `01-basic-servlets`
**Session 1 - Week 1**

**Features:**
- Basic servlet implementation
- HTML forms for client management
- In-memory data storage
- Simple CRUD operations

**Key Files:**
- `WelcomeServlet.java`
- `ClientListServlet.java`
- `Client.java` (POJO)

**Learning Focus:**
- Servlet lifecycle
- HTTP request/response
- Form handling

---

### Branch 2: `02-jsp-mvc`
**Session 2 - Week 1**

**Features:**
- JSP views replacing servlet HTML generation
- JSTL for logic in views
- MVC pattern implementation
- Reusable components

**Key Files:**
- `ClientController.java`
- `client-list.jsp`
- `client-form.jsp`
- `header.jsp`, `footer.jsp`

**Learning Focus:**
- JSP and JSTL
- MVC separation
- View composition

---

### Branch 3: `03-jpa-database`
**Session 3 - Week 1**

**Features:**
- JPA entity mapping
- Database persistence (PostgreSQL)
- Repository pattern
- JPQL queries
- Transaction management

**Key Files:**
- `Client.java` (JPA entity)
- `Account.java` (JPA entity)
- `ClientRepository.java`
- `AccountRepository.java`
- `persistence.xml`

**Database Schema:**
```sql
CREATE TABLE client (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    account_type VARCHAR(20) NOT NULL,
    client_id BIGINT REFERENCES client(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transaction (
    id BIGSERIAL PRIMARY KEY,
    amount DECIMAL(15,2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    description TEXT,
    account_id BIGINT REFERENCES account(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Learning Focus:**
- ORM concepts
- Entity relationships
- JPQL
- Transactions

---

### Branch 4: `04-cdi-services`
**Session 4 - Week 1**

**Features:**
- Service layer with CDI
- Dependency injection
- Business logic separation
- Transaction interceptors
- Logging decorators

**Key Files:**
- `ClientService.java`
- `AccountService.java`
- `TransactionService.java`
- `TransactionalInterceptor.java`
- `LoggingDecorator.java`

**Architecture:**
```
Controller → Service → Repository → Database
    ↓         ↓          ↓
   CDI      CDI        JPA
```

**Learning Focus:**
- Dependency injection
- Service layer pattern
- CDI scopes
- Interceptors

---

### Branch 5: `05-rest-api`
**Session 5 - Week 2**

**Features:**
- RESTful API with JAX-RS
- JSON request/response
- HTTP method mapping
- Exception handling
- API documentation

**Key Files:**
- `ClientResource.java`
- `AccountResource.java`
- `TransactionResource.java`
- `ApplicationConfig.java`
- `ExceptionMapper.java`

**API Endpoints:**
```
GET    /api/clients           - List all clients
GET    /api/clients/{id}      - Get client by ID
POST   /api/clients           - Create new client
PUT    /api/clients/{id}      - Update client
DELETE /api/clients/{id}      - Delete client

GET    /api/accounts          - List all accounts
GET    /api/accounts/{id}     - Get account by ID
POST   /api/accounts          - Create new account
GET    /api/accounts/{id}/balance - Get balance
POST   /api/accounts/{id}/deposit - Deposit money
POST   /api/accounts/{id}/withdraw - Withdraw money
POST   /api/accounts/transfer - Transfer between accounts

GET    /api/transactions      - List transactions
GET    /api/transactions/{id} - Get transaction by ID
```

**Learning Focus:**
- REST principles
- JAX-RS annotations
- JSON processing
- API design

---

### Branch 6: `06-ddd-refactor`
**Session 6 - Week 2**

**Features:**
- Domain-Driven Design patterns
- Aggregates and aggregate roots
- Value objects
- Domain events
- Bounded contexts

**Key Files:**
- `ClientAggregate.java`
- `AccountAggregate.java`
- `Money.java` (Value Object)
- `AccountNumber.java` (Value Object)
- `TransferDomainEvent.java`
- `DomainEventPublisher.java`

**Domain Model:**
```
Client (Aggregate Root)
  ├── ClientId (Value Object)
  ├── Email (Value Object)
  └── Accounts (Collection)

Account (Aggregate Root)
  ├── AccountNumber (Value Object)
  ├── Money (Value Object)
  └── Transactions (Collection)
```

**Learning Focus:**
- DDD tactical patterns
- Aggregates
- Value objects
- Domain events

---

### Branch 7: `07-hexagonal-arch`
**Session 7 - Week 2**

**Features:**
- Hexagonal architecture (Ports & Adapters)
- Domain layer isolation
- Port interfaces
- Adapter implementations
- Dependency inversion

**Architecture:**
```
┌─────────────────────────────────────┐
│         Application Layer           │
│  (Use Cases / Application Services) │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│          Domain Layer               │
│  (Entities, Value Objects, Events)  │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│      Infrastructure Layer           │
│  (JPA, REST, Database Adapters)     │
└─────────────────────────────────────┘
```

**Key Files:**
- `ClientPort.java` (interface)
- `AccountPort.java` (interface)
- `JpaClientAdapter.java`
- `JpaAccountAdapter.java`
- `RestClientAdapter.java`

**Learning Focus:**
- Hexagonal architecture
- Ports and adapters
- Dependency inversion
- Clean architecture

---

### Branch 8: `08-microservices`
**Session 8 - Week 2**

**Features:**
- Microservices decomposition
- Service-to-service communication
- API Gateway
- Service discovery
- Docker containerization

**Services:**
1. **Client Service** (Port 8081)
   - Client management
   - Client data persistence

2. **Account Service** (Port 8082)
   - Account management
   - Balance operations
   - Transaction history

3. **API Gateway** (Port 8080)
   - Request routing
   - Authentication
   - Rate limiting

**Architecture:**
```
                    ┌─────────────┐
                    │ API Gateway │
                    │   :8080     │
                    └─────────────┘
                          │
              ┌───────────┴───────────┐
              ↓                       ↓
      ┌──────────────┐        ┌──────────────┐
      │Client Service│        │Account Service│
      │    :8081     │        │    :8082      │
      └──────────────┘        └──────────────┘
              │                       │
              ↓                       ↓
      ┌──────────────┐        ┌──────────────┐
      │  Client DB   │        │  Account DB  │
      └──────────────┘        └──────────────┘
```

**Key Files:**
- `client-service/` (separate project)
- `account-service/` (separate project)
- `api-gateway/` (separate project)
- `docker-compose.yml`
- `Dockerfile` (for each service)

**Learning Focus:**
- Microservices patterns
- Service decomposition
- Inter-service communication
- Containerization

---

## 🚀 Getting Started

### Prerequisites
- JDK 17+
- Maven 3.8+
- PostgreSQL 14+
- WildFly 27+ (for monolith branches)
- Docker & Docker Compose (for microservices branch)

### Clone and Setup

```bash
# Clone repository
git clone <repository-url>
cd esipe-javaee/04-BankingApp/src

# List all branches
git branch -a

# Checkout specific branch
git checkout 01-basic-servlets
```

### Build and Run (Monolith Branches 1-7)

```bash
# Build
mvn clean package

# Deploy to WildFly
mvn wildfly:deploy

# Access application
open http://localhost:8080/banking-app
```

### Build and Run (Microservices Branch 8)

```bash
# Checkout microservices branch
git checkout 08-microservices

# Build all services
./build-all.sh

# Start with Docker Compose
docker-compose up -d

# Access API Gateway
open http://localhost:8080

# View logs
docker-compose logs -f
```

---

## 📊 Database Setup

### Create Database

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database and user
CREATE DATABASE bankingdb;
CREATE USER bankuser WITH ENCRYPTED PASSWORD 'bankpass';
GRANT ALL PRIVILEGES ON DATABASE bankingdb TO bankuser;
\q
```

### Initialize Schema

```bash
# Run initialization script
psql -U bankuser -d bankingdb -f sql/init-schema.sql

# Load sample data (optional)
psql -U bankuser -d bankingdb -f sql/sample-data.sql
```

---

## 🧪 Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn verify -P integration-tests
```

### API Tests (Postman)

Import collection: `postman/banking-api.json`

---

## 📚 Documentation

### API Documentation

- **Swagger UI:** http://localhost:8080/banking-app/swagger-ui
- **OpenAPI Spec:** http://localhost:8080/banking-app/openapi.json

### Architecture Diagrams

See `docs/architecture/` for:
- Component diagrams
- Sequence diagrams
- Database ERD
- Deployment diagrams

---

## 🎓 Learning Path

### For Students

1. **Start with branch 01:** Understand basic servlets
2. **Progress sequentially:** Each branch builds on previous
3. **Complete labs:** Hands-on practice with each concept
4. **Review commits:** See what changed between branches
5. **Experiment:** Try modifications and improvements

### For Instructors

1. **Demo each branch:** Show evolution of application
2. **Explain changes:** Highlight new concepts in each branch
3. **Code reviews:** Review student implementations
4. **Encourage exploration:** Let students experiment

---

## 🔍 Key Concepts by Branch

| Branch | Key Concepts |
|--------|-------------|
| 01 | Servlets, HTTP, Forms |
| 02 | JSP, JSTL, MVC |
| 03 | JPA, ORM, Transactions |
| 04 | CDI, DI, Services |
| 05 | REST, JAX-RS, JSON |
| 06 | DDD, Aggregates, Events |
| 07 | Hexagonal, Ports, Adapters |
| 08 | Microservices, Docker, API Gateway |

---

## 🆘 Troubleshooting

### Common Issues

**Database connection failed:**
```bash
# Check PostgreSQL is running
sudo systemctl status postgresql

# Verify credentials in persistence.xml
```

**Port already in use:**
```bash
# Find process using port
lsof -i :8080

# Kill process or change port
```

**Build fails:**
```bash
# Clean Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U
```

---

## 📝 Contributing

Students can contribute by:
- Reporting bugs
- Suggesting improvements
- Adding features
- Writing tests
- Improving documentation

---

## 📄 License

Educational use only. See course materials license.

---

**Happy coding! 🚀**