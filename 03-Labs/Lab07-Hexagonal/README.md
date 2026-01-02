# Lab 07: Hexagonal Architecture (Ports and Adapters)

© Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited.

## Overview

This lab demonstrates the refactoring of a Domain-Driven Design (DDD) application into a clean Hexagonal Architecture using the Ports and Adapters pattern. You will learn how to separate domain logic from infrastructure concerns, making the application more testable, maintainable, and flexible.

**Duration**: 4 hours  
**Difficulty**: Advanced  
**Prerequisites**: Labs 01-06 completed (especially Lab 06 - DDD)

## 📚 Documentation

- **[HEXAGONAL-ARCHITECTURE.md](solution/HEXAGONAL-ARCHITECTURE.md)** - Complete guide to hexagonal architecture implementation:
  - Ports and Adapters pattern explained
  - Primary vs Secondary ports
  - Adapter implementations
  - Dependency Inversion Principle in practice
  - Testing strategies with mock adapters
  
- **[REFACTORING-GUIDE.md](solution/REFACTORING-GUIDE.md)** - Step-by-step refactoring from Lab06 to Lab07:
  - Package structure changes
  - Extracting ports from services
  - Creating adapters
  - Separating domain from infrastructure
  - Migration checklist

- **[SOLUTION-STATUS.md](solution/SOLUTION-STATUS.md)** - Complete verification that all Lab06 features are preserved:
  - Feature comparison matrix
  - Architecture improvements
  - Testing enhancements
  - Performance considerations

## Learning Objectives

By the end of this lab, you will be able to:

1. **Understand Hexagonal Architecture**:
   - Apply the Ports and Adapters pattern
   - Distinguish between primary and secondary ports
   - Implement the Dependency Inversion Principle
   - Create clean architecture with proper layer separation

2. **Implement Ports**:
   - Define primary ports (use case interfaces)
   - Define secondary ports (repository interfaces)
   - Use commands and queries for data transfer
   - Maintain clear boundaries between layers

3. **Create Adapters**:
   - Implement primary adapters (REST, Web)
   - Implement secondary adapters (JPA, Events)
   - Map between domain and infrastructure models
   - Handle multiple adapters for the same port

4. **Achieve Clean Architecture**:
   - Keep domain pure (no framework dependencies)
   - Enforce dependency rules (inward dependencies)
   - Separate concerns across layers
   - Make the application testable and flexible

## What's New in Lab 07

### From Lab 06 (DDD with Infrastructure)
```java
// Domain entity with JPA annotations
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "balance")
    private BigDecimal balance;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    public void deposit(Money amount) {
        this.balance = this.balance.add(amount.getAmount());
    }
}

// Service with mixed concerns
@ApplicationScoped
public class AccountService {
    @PersistenceContext
    private EntityManager em;
    
    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        Account account = em.find(Account.class, accountId);
        account.deposit(new Money(amount));
        em.merge(account);
    }
}
```

### To Lab 07 (Hexagonal Architecture)
```java
// Pure domain entity (no JPA)
public class Account {
    private Long id;
    private Money balance;
    private Client client;
    
    public void deposit(Money amount) {
        if (amount.isNegative()) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }
}

// Primary port (use case interface)
public interface DepositMoneyUseCase {
    void execute(DepositCommand command);
}

// Use case implementation
@ApplicationScoped
public class DepositMoneyService implements DepositMoneyUseCase {
    private final AccountRepository accountRepository;  // Secondary port
    
    @Inject
    public DepositMoneyService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    @Override
    @Transactional
    public void execute(DepositCommand command) {
        Account account = accountRepository.findById(command.accountId())
            .orElseThrow(() -> new AccountNotFoundException(command.accountId()));
        account.deposit(command.amount());
        accountRepository.save(account);
    }
}

// Secondary port (repository interface)
public interface AccountRepository {
    Optional<Account> findById(Long id);
    void save(Account account);
}

// Secondary adapter (JPA implementation)
@ApplicationScoped
public class JpaAccountAdapter implements AccountRepository {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Optional<Account> findById(Long id) {
        AccountEntity entity = em.find(AccountEntity.class, id);
        return Optional.ofNullable(entity).map(AccountMapper::toDomain);
    }
    
    @Override
    public void save(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);
        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }
}

// Infrastructure entity (separate from domain)
@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "balance")
    private BigDecimal balance;
    
    @Column(name = "currency")
    private String currency;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;
}

// Mapper between domain and infrastructure
public class AccountMapper {
    public static Account toDomain(AccountEntity entity) {
        return new Account(
            entity.getId(),
            new Money(entity.getBalance(), entity.getCurrency()),
            ClientMapper.toDomain(entity.getClient())
        );
    }
    
    public static AccountEntity toEntity(Account domain) {
        AccountEntity entity = new AccountEntity();
        entity.setId(domain.getId());
        entity.setBalance(domain.getBalance().getAmount());
        entity.setCurrency(domain.getBalance().getCurrency());
        entity.setClient(ClientMapper.toEntity(domain.getClient()));
        return entity;
    }
}
```

## Architecture Comparison

### Lab 06 (DDD) Package Structure
```
com.bank/
├── domain/
│   ├── model/              # Entities with JPA annotations
│   ├── valueobject/        # Value objects
│   ├── service/            # Domain services
│   ├── event/              # Domain events
│   └── repository/         # Repository interfaces (but not true ports)
├── application/
│   └── dto/                # DTOs
├── service/                # Application services (mixed concerns)
├── api/                    # REST resources
└── web/                    # Web controllers
```

**Issues:**
- Domain entities have JPA annotations (infrastructure leak)
- Services mix use cases with infrastructure concerns
- No clear separation between ports and adapters
- Difficult to test without database

### Lab 07 (Hexagonal) Package Structure
```
com.bank/
├── domain/                          # Pure domain (no dependencies)
│   ├── model/
│   │   ├── Account.java            # Pure aggregate root
│   │   └── Client.java             # Pure aggregate root
│   ├── valueobject/
│   │   ├── Money.java              # Value object
│   │   ├── AccountNumber.java      # Value object
│   │   └── Email.java              # Value object
│   └── service/
│       └── TransferService.java    # Domain service
│
├── application/                     # Use cases and ports
│   ├── port/
│   │   ├── in/                     # Primary ports (driving)
│   │   │   ├── AccountManagementUseCase.java
│   │   │   ├── ClientManagementUseCase.java
│   │   │   ├── DepositMoneyUseCase.java
│   │   │   ├── WithdrawMoneyUseCase.java
│   │   │   └── TransferMoneyUseCase.java
│   │   └── out/                    # Secondary ports (driven)
│   │       ├── AccountRepository.java
│   │       ├── ClientRepository.java
│   │       └── EventPublisher.java
│   ├── usecase/
│   │   ├── AccountManagementService.java
│   │   ├── ClientManagementService.java
│   │   ├── DepositMoneyService.java
│   │   ├── WithdrawMoneyService.java
│   │   └── TransferMoneyService.java
│   ├── command/
│   │   ├── OpenAccountCommand.java
│   │   ├── DepositCommand.java
│   │   ├── WithdrawCommand.java
│   │   └── TransferCommand.java
│   └── dto/
│       ├── AccountDTO.java
│       └── ClientDTO.java
│
└── infrastructure/                  # All technical concerns
    ├── adapter/
    │   ├── in/                     # Primary adapters (driving)
    │   │   ├── rest/
    │   │   │   ├── AccountRestAdapter.java
    │   │   │   ├── ClientRestAdapter.java
    │   │   │   └── RestApplication.java
    │   │   └── web/
    │   │       ├── AccountWebAdapter.java
    │   │       └── ClientWebAdapter.java
    │   └── out/                    # Secondary adapters (driven)
    │       ├── persistence/
    │       │   ├── JpaAccountAdapter.java
    │       │   ├── JpaClientAdapter.java
    │       │   ├── entity/
    │       │   │   ├── AccountEntity.java
    │       │   │   └── ClientEntity.java
    │       │   └── mapper/
    │       │       ├── AccountMapper.java
    │       │       └── ClientMapper.java
    │       └── event/
    │           └── CDIEventPublisherAdapter.java
    └── config/
        ├── DatabaseMigrationStartup.java
        └── EntityManagerProducer.java
```

**Benefits:**
- Pure domain model (no framework dependencies)
- Clear ports and adapters separation
- Easy to test with mock adapters
- Can swap implementations without changing business logic
- Explicit dependency flow (all inward)

## Key Concepts

### 1. Ports (Interfaces)

**Primary Ports (Driving)** - What the application can do:
```java
// Use case interface
public interface DepositMoneyUseCase {
    void execute(DepositCommand command);
}

// Command object
public record DepositCommand(
    Long accountId,
    Money amount
) {}
```

**Secondary Ports (Driven)** - What the application needs:
```java
// Repository interface
public interface AccountRepository {
    Optional<Account> findById(Long id);
    Optional<Account> findByNumber(AccountNumber number);
    List<Account> findByClientId(Long clientId);
    void save(Account account);
    void delete(Account account);
}
```

### 2. Adapters (Implementations)

**Primary Adapters (Driving)** - Receive requests from outside:
```java
// REST adapter
@Path("/api/v2/accounts")
@ApplicationScoped
public class AccountRestAdapter {
    private final DepositMoneyUseCase depositUseCase;
    
    @Inject
    public AccountRestAdapter(DepositMoneyUseCase depositUseCase) {
        this.depositUseCase = depositUseCase;
    }
    
    @POST
    @Path("/{id}/deposit")
    public Response deposit(@PathParam("id") Long id, DepositRequest request) {
        DepositCommand command = new DepositCommand(id, new Money(request.amount()));
        depositUseCase.execute(command);
        return Response.ok().build();
    }
}
```

**Secondary Adapters (Driven)** - Implement infrastructure:
```java
// JPA adapter
@ApplicationScoped
public class JpaAccountAdapter implements AccountRepository {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Optional<Account> findById(Long id) {
        AccountEntity entity = em.find(AccountEntity.class, id);
        return Optional.ofNullable(entity).map(AccountMapper::toDomain);
    }
    
    @Override
    public void save(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);
        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }
}
```

### 3. Dependency Inversion

**Traditional (Bad):**
```
Use Case → Concrete Repository → Database
```

**Hexagonal (Good):**
```
Use Case → Repository Interface ← JPA Adapter → Database
```

The use case depends on an interface (port) that it defines. The adapter implements that interface.

## Lab Structure

### Starter Code
The `starter/` directory contains:
- Lab 06 DDD code as starting point
- Instructions for refactoring
- TODO comments marking refactoring points
- Basic package structure to guide you

### Solution Code
The `solution/` directory contains:
- Complete hexagonal architecture implementation
- All Lab 06 features preserved
- Pure domain model
- Clear ports and adapters
- Comprehensive tests
- Full deployment configuration

## Step-by-Step Instructions

### Part 1: Understand the Current Architecture (30 minutes)

1. **Review Lab 06 Code**:
   - Examine the current package structure
   - Identify infrastructure dependencies in domain
   - Note where business logic is mixed with technical concerns

2. **Study Hexagonal Architecture**:
   - Review the lecture slides
   - Understand ports vs adapters
   - Learn the dependency inversion principle

3. **Plan the Refactoring**:
   - Identify which classes will become ports
   - Determine which classes will become adapters
   - Map out the new package structure

### Part 2: Create Domain Layer (45 minutes)

1. **Remove Infrastructure from Domain**:
   ```java
   // Before: Account with JPA
   @Entity
   public class Account { ... }
   
   // After: Pure Account
   public class Account { ... }
   ```

2. **Keep Value Objects Pure**:
   - Money, AccountNumber, Email remain unchanged
   - No JPA annotations
   - Immutable and self-validating

3. **Preserve Domain Services**:
   - TransferService remains in domain
   - No infrastructure dependencies

### Part 3: Define Ports (45 minutes)

1. **Create Primary Ports (Use Cases)**:
   ```java
   // application/port/in/DepositMoneyUseCase.java
   public interface DepositMoneyUseCase {
       void execute(DepositCommand command);
   }
   ```

2. **Create Secondary Ports (Repositories)**:
   ```java
   // application/port/out/AccountRepository.java
   public interface AccountRepository {
       Optional<Account> findById(Long id);
       void save(Account account);
   }
   ```

3. **Define Commands and Queries**:
   ```java
   // application/command/DepositCommand.java
   public record DepositCommand(Long accountId, Money amount) {}
   ```

### Part 4: Implement Use Cases (60 minutes)

1. **Create Use Case Implementations**:
   ```java
   @ApplicationScoped
   public class DepositMoneyService implements DepositMoneyUseCase {
       private final AccountRepository accountRepository;
       
       @Inject
       public DepositMoneyService(AccountRepository accountRepository) {
           this.accountRepository = accountRepository;
       }
       
       @Override
       @Transactional
       public void execute(DepositCommand command) {
           Account account = accountRepository.findById(command.accountId())
               .orElseThrow(() -> new AccountNotFoundException(command.accountId()));
           account.deposit(command.amount());
           accountRepository.save(account);
       }
   }
   ```

2. **Implement All Use Cases**:
   - AccountManagementService
   - ClientManagementService
   - DepositMoneyService
   - WithdrawMoneyService
   - TransferMoneyService

### Part 5: Create Infrastructure Adapters (60 minutes)

1. **Create Infrastructure Entities**:
   ```java
   @Entity
   @Table(name = "accounts")
   public class AccountEntity {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       
       @Column(name = "balance")
       private BigDecimal balance;
       
       @Column(name = "currency")
       private String currency;
   }
   ```

2. **Create Mappers**:
   ```java
   public class AccountMapper {
       public static Account toDomain(AccountEntity entity) { ... }
       public static AccountEntity toEntity(Account domain) { ... }
   }
   ```

3. **Implement JPA Adapters**:
   ```java
   @ApplicationScoped
   public class JpaAccountAdapter implements AccountRepository {
       @PersistenceContext
       private EntityManager em;
       
       @Override
       public Optional<Account> findById(Long id) {
           AccountEntity entity = em.find(AccountEntity.class, id);
           return Optional.ofNullable(entity).map(AccountMapper::toDomain);
       }
   }
   ```

### Part 6: Create Primary Adapters (45 minutes)

1. **Create REST Adapters**:
   ```java
   @Path("/api/v2/accounts")
   @ApplicationScoped
   public class AccountRestAdapter {
       private final DepositMoneyUseCase depositUseCase;
       
       @Inject
       public AccountRestAdapter(DepositMoneyUseCase depositUseCase) {
           this.depositUseCase = depositUseCase;
       }
       
       @POST
       @Path("/{id}/deposit")
       public Response deposit(@PathParam("id") Long id, DepositRequest request) {
           DepositCommand command = new DepositCommand(id, new Money(request.amount()));
           depositUseCase.execute(command);
           return Response.ok().build();
       }
   }
   ```

2. **Create Web Adapters**:
   - Convert existing controllers to adapters
   - Inject use cases instead of services

### Part 7: Testing (30 minutes)

1. **Unit Test Domain Logic**:
   ```java
   @Test
   public void testAccountDeposit() {
       Account account = new Account(new Money(1000));
       account.deposit(new Money(500));
       assertEquals(1500, account.getBalance().getAmount().intValue());
   }
   ```

2. **Integration Test Use Cases**:
   ```java
   @Test
   public void testDepositMoneyUseCase() {
       AccountRepository mockRepo = mock(AccountRepository.class);
       Account account = new Account(new Money(1000));
       when(mockRepo.findById(1L)).thenReturn(Optional.of(account));
       
       DepositMoneyUseCase useCase = new DepositMoneyService(mockRepo);
       useCase.execute(new DepositCommand(1L, new Money(500)));
       
       verify(mockRepo).save(account);
       assertEquals(1500, account.getBalance().getAmount().intValue());
   }
   ```

3. **Test with In-Memory Adapter**:
   ```java
   public class InMemoryAccountRepository implements AccountRepository {
       private Map<Long, Account> accounts = new HashMap<>();
       
       @Override
       public Optional<Account> findById(Long id) {
           return Optional.ofNullable(accounts.get(id));
       }
       
       @Override
       public void save(Account account) {
           accounts.put(account.getId(), account);
       }
   }
   ```

## Testing the Application

### Build and Deploy
```bash
# Build the application
mvn clean package

# Run with Podman
./podman-test.sh

# Or run with Docker
./docker-test.sh
```

### Test Endpoints

**Deposit Money:**
```bash
curl -X POST http://localhost:9080/banking-hexagonal/api/v2/accounts/1/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount": 500.00, "currency": "EUR"}'
```

**Transfer Money:**
```bash
curl -X POST http://localhost:9080/banking-hexagonal/api/v2/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "sourceAccountId": 1,
    "targetAccountId": 2,
    "amount": 200.00,
    "currency": "EUR"
  }'
```

**Get Account:**
```bash
curl http://localhost:9080/banking-hexagonal/api/v2/accounts/1
```

## Verification Checklist

### Architecture
- [ ] Domain layer has no infrastructure dependencies
- [ ] All dependencies point inward
- [ ] Ports are defined in application layer
- [ ] Adapters are in infrastructure layer
- [ ] Clear separation between domain and infrastructure entities

### Functionality
- [ ] All Lab 06 features work (CRUD, transfers, etc.)
- [ ] REST API endpoints respond correctly
- [ ] Web UI works as expected
- [ ] Database operations succeed
- [ ] Events are published correctly

### Testing
- [ ] Domain logic can be tested without infrastructure
- [ ] Use cases can be tested with mock repositories
- [ ] Integration tests pass
- [ ] All automated tests succeed

### Code Quality
- [ ] No circular dependencies
- [ ] Clear package structure
- [ ] Proper use of dependency injection
- [ ] Comprehensive error handling
- [ ] Good code documentation

## Common Pitfalls

### 1. Infrastructure Leaking into Domain
**❌ Wrong:**
```java
// Domain entity with JPA
@Entity
public class Account { ... }
```

**✅ Correct:**
```java
// Pure domain
public class Account { ... }

// Separate infrastructure entity
@Entity
public class AccountEntity { ... }
```

### 2. Use Cases Depending on Concrete Adapters
**❌ Wrong:**
```java
public class DepositMoneyService {
    private JpaAccountAdapter adapter;  // Concrete
}
```

**✅ Correct:**
```java
public class DepositMoneyService {
    private AccountRepository repository;  // Interface
}
```

### 3. Anemic Domain Model
**❌ Wrong:**
```java
public class Account {
    public void setBalance(Money balance) { this.balance = balance; }
}

// Logic in use case
useCase.execute() {
    account.setBalance(account.getBalance().add(amount));
}
```

**✅ Correct:**
```java
public class Account {
    public void deposit(Money amount) {
        this.balance = this.balance.add(amount);
    }
}

// Use case orchestrates
useCase.execute() {
    account.deposit(amount);
}
```

## API Versioning: Multiple Adapters for Same Ports

One of the key benefits of hexagonal architecture is the ability to have **multiple adapters for the same ports**. This lab demonstrates this with API versioning.

### Two API Versions Coexist

The solution includes both V1 (deprecated) and V2 (current) REST APIs:

```
infrastructure/rest/adapter/
├── v1/                          # Deprecated API
│   ├── AccountRestAdapter.java  # @Deprecated
│   └── ClientRestAdapter.java   # @Deprecated
└── v2/                          # Current API
    ├── AccountRestAdapterV2.java
    └── ClientRestAdapterV2.java
```

Both versions use the **same application ports** (use cases), demonstrating true adapter flexibility.

### API V1 - Simple Format (Deprecated) ⚠️

**Base Path**: `/api/v1/`  
**Status**: Deprecated - marked with `@Deprecated` annotation

```bash
# Create account - simple balance format
curl -X POST http://localhost:9080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "initialBalance": 1000.00,
    "currency": "EUR",
    "accountType": "CHECKING"
  }'

# Response: {"balance": 1000.00, "currency": "EUR", ...}
```

**Characteristics**:
- Simple JSON format
- Balance as plain number
- Backward compatible
- Will be removed in future release

### API V2 - Rich Format (Current) ✅

**Base Path**: `/api/v2/`  
**Status**: Current - recommended version

```bash
# Create account - Money value object format
curl -X POST http://localhost:9080/api/v2/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "initialBalance": {
      "amount": 1000.00,
      "currency": "EUR"
    },
    "accountType": "CHECKING"
  }'

# Response: {"balance": {"amount": 1000.00, "currency": "EUR"}, ...}
```

**Characteristics**:
- Rich domain representation
- Money as value object
- Premium client features
- Better type safety

### V2 Exclusive Features

```bash
# Get premium clients only
curl http://localhost:9080/api/v2/clients/premium

# Upgrade client to premium
curl -X POST http://localhost:9080/api/v2/clients/1/upgrade

# Downgrade from premium
curl -X POST http://localhost:9080/api/v2/clients/1/downgrade
```

### Architecture Benefit

```
┌─────────────────────────────────────┐
│      Application Core (Ports)       │
│  - AccountManagementUseCase         │
│  - ClientManagementUseCase          │
│  - MoneyOperationsUseCase           │
└─────────────────────────────────────┘
           ▲              ▲
           │              │
    ┌──────┴──────┐  ┌───┴────────┐
    │  REST V1    │  │  REST V2   │
    │  @Deprecated│  │  Current   │
    │  /api/v1/*  │  │  /api/v2/* │
    └─────────────┘  └────────────┘
```

**Key Points**:
- ✅ Same business logic for both versions
- ✅ Domain unchanged
- ✅ Easy to add V3, V4, etc.
- ✅ Graceful deprecation strategy
- ✅ Parallel development possible

### Migration Path

1. **Current State**: V1 (deprecated), V2 (stable)
2. **Clients migrate** from V1 to V2
3. **Future**: V1 removed, V2 stable, V3 beta

See **[API-VERSIONING.md](solution/API-VERSIONING.md)** for complete documentation on:
- Detailed version comparison
- Code examples for both versions
- Migration strategy
- Testing both APIs
- Deprecation timeline

## Benefits Achieved

### 1. Testability
- Domain logic tested without database
- Use cases tested with mock adapters
- Fast, reliable tests

### 2. Flexibility
- Easy to swap database (PostgreSQL → MongoDB)
- Multiple interfaces (REST, Web, CLI)
- Technology independence

### 3. Maintainability
- Clear boundaries and responsibilities
- Easy to understand and modify
- Reduced coupling

### 4. Evolvability
- Add new features without breaking existing code
- Change infrastructure without touching domain
- Parallel development possible

## Further Reading

### Books
- "Hexagonal Architecture Explained" by Juan Manuel Garrido de Paz
- "Clean Architecture" by Robert C. Martin
- "Get Your Hands Dirty on Clean Architecture" by Tom Hombergs

### Articles
- Alistair Cockburn: "Hexagonal Architecture" (original)
- Martin Fowler: "Ports and Adapters"
- Netflix Tech Blog: "Ready for changes with Hexagonal Architecture"

### Online Resources
- Jakarta EE Tutorial: Enterprise Application Architecture
- Baeldung: Hexagonal Architecture with Spring Boot
- DZone: Clean Architecture with Java

## Next Steps

After completing this lab:
1. **Review** the solution code thoroughly
2. **Compare** with Lab 06 to understand the changes
3. **Experiment** with different adapter implementations
4. **Practice** writing tests with mock adapters
5. **Prepare** for Lab 08: Microservices Architecture

## Support

If you encounter issues:
1. Check the solution code for reference
2. Review the lecture slides
3. Consult the documentation files
4. Ask questions during lab sessions

---

**Good luck with your hexagonal architecture refactoring!**

Remember: The goal is not just to move code around, but to achieve a clean, testable, and maintainable architecture that separates business logic from technical concerns.