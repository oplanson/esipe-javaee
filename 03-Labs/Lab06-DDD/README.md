# Lab 06: Domain-Driven Design (DDD)

© Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited.

## Overview

This lab demonstrates the refactoring of an anemic domain model into a rich domain model using Domain-Driven Design (DDD) patterns and principles.

**Duration**: 4 hours
**Difficulty**: Advanced
**Prerequisites**: Labs 01-05 completed

## 📚 Documentation

- **[BOUNDED-CONTEXT.md](solution/BOUNDED-CONTEXT.md)** - Complete documentation of the Banking Bounded Context, including:
  - Bounded Context definition and scope
  - Ubiquitous Language glossary
  - Domain model (Aggregates, Value Objects, Services, Events)
  - Business rules and invariants
  - Context boundaries and integration points
  - Context Map showing relationships with other contexts
  - Package structure and architecture

- **[Repository Interfaces](solution/src/main/java/com/bank/domain/repository/)** - Explicit repository interfaces following DDD patterns:
  - `AccountRepository.java` - Repository for Account aggregate
  - `ClientRepository.java` - Repository for Client aggregate

- **[FEATURE-COMPARISON.md](FEATURE-COMPARISON.md)** - Complete verification that all Lab05 features are preserved:
  - Detailed comparison of dependencies, configurations, and code structure
  - Verification of all Jakarta EE and MicroProfile features
  - Documentation of new DDD enhancements
  - Quantitative and qualitative analysis

## Learning Objectives

By the end of this lab, you will be able to:

1. **Understand DDD Strategic Patterns**:
   - Identify Bounded Contexts
   - Define Ubiquitous Language
   - Apply Context Mapping

2. **Implement DDD Tactical Patterns**:
   - Create Value Objects for domain concepts
   - Build rich Aggregate Roots with business logic
   - Implement Domain Services for cross-aggregate operations
   - Use Domain Events for decoupling

3. **Refactor from Anemic to Rich Domain Model**:
   - Move business logic from services to domain entities
   - Enforce invariants and business rules
   - Encapsulate domain concepts

4. **Apply DDD Best Practices**:
   - Separate domain layer from infrastructure
   - Use DTOs for data transfer
   - Maintain aggregate boundaries

## What's New in Lab 06

### From Lab 05 (Anemic Model)
```java
// Anemic Account entity
public class Account {
    private String number;
    private double balance;
    private String type;
    
    // Only getters and setters
    public void setBalance(double balance) {
        this.balance = balance;
    }
}

// Business logic in service
public class AccountService {
    public void deposit(Long accountId, double amount) {
        Account account = em.find(Account.class, accountId);
        account.setBalance(account.getBalance() + amount);
    }
}
```

### To Lab 06 (Rich Domain Model)
```java
// Rich Account aggregate with Value Objects
public class Account {
    private AccountNumber accountNumber;
    private Money balance;
    private AccountType accountType;
    
    // Business logic in domain
    public void deposit(Money amount) {
        // Validate business rules
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!amount.getCurrency().equals(balance.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        
        // Update state
        this.balance = this.balance.add(amount);
    }
}
```

## Architecture

### DDD Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Controllers, JSP, REST Resources)     │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│         Application Layer               │
│    (Application Services, DTOs)         │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│           Domain Layer                  │
│  (Aggregates, Value Objects, Services,  │
│   Domain Events, Business Logic)        │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│       Infrastructure Layer              │
│  (JPA Repositories, Database, External) │
└─────────────────────────────────────────┘
```

### Package Structure

```
com.bank/
├── domain/                    # Domain Layer
│   ├── valueobject/          # Value Objects
│   │   ├── Money.java
│   │   ├── AccountNumber.java
│   │   ├── Email.java
│   │   └── AccountType.java
│   ├── service/              # Domain Services
│   │   └── TransferService.java
│   └── event/                # Domain Events
│       ├── MoneyDepositedEvent.java
│       ├── MoneyWithdrawnEvent.java
│       └── MoneyTransferredEvent.java
├── model/                    # Aggregates (Domain Entities)
│   ├── Account.java          # Account Aggregate Root
│   └── Client.java           # Client Aggregate Root
├── application/              # Application Layer
│   └── dto/                  # Data Transfer Objects
│       ├── AccountDTO.java
│       └── ClientDTO.java
├── service/                  # Application Services
│   ├── AccountService.java
│   └── ClientService.java
├── api/                      # REST API (Presentation)
│   ├── AccountResource.java
│   └── ClientResource.java
└── web/                      # Web Controllers (Presentation)
    ├── AccountController.java
    └── ClientController.java
```

## Key DDD Patterns Implemented

### 1. Value Objects

**Money Value Object**:
```java
@Embeddable
public class Money {
    private final BigDecimal amount;
    private final String currency;
    
    // Immutable, self-validating
    private Money(BigDecimal amount, String currency) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
        this.currency = currency;
    }
    
    // Rich behavior
    public Money add(Money other) {
        validateCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```

**Characteristics**:
- Immutable (all fields final)
- No identity (equality by value)
- Self-validating
- Rich behavior

### 2. Aggregate Roots

**Account Aggregate**:
```java
public class Account {
    // Identity
    private Long id;
    
    // Value Objects
    private AccountNumber accountNumber;
    private Money balance;
    private AccountType accountType;
    
    // Aggregate member
    private Client client;
    
    // Factory method
    public static Account create(Client client, AccountType type, Money initialDeposit) {
        // Enforce business rules
        if (initialDeposit.isLessThan(Money.euros(10.0))) {
            throw new IllegalArgumentException("Minimum deposit is 10 EUR");
        }
        
        Account account = new Account();
        account.accountNumber = AccountNumber.generate();
        account.balance = initialDeposit;
        account.accountType = type;
        account.client = client;
        
        return account;
    }
    
    // Business logic
    public void deposit(Money amount) {
        validateDeposit(amount);
        this.balance = this.balance.add(amount);
    }
    
    public void withdraw(Money amount) {
        validateWithdrawal(amount);
        if (!accountType.canWithdraw(balance, amount)) {
            throw new IllegalStateException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
    }
}
```

**Characteristics**:
- Has identity (ID)
- Controls access to aggregate members
- Enforces invariants
- Contains business logic
- Uses factory methods for creation

### 3. Domain Services

**TransferService**:
```java
@ApplicationScoped
public class TransferService {
    
    @Transactional
    public void transfer(Account from, Account to, Money amount) {
        // Validate business rules
        validateTransfer(from, to, amount);
        
        // Coordinate operation across aggregates
        from.transferTo(to, amount);
        
        // Fire domain event
        eventBus.fire(new MoneyTransferredEvent(from, to, amount));
    }
}
```

**When to use**:
- Operation involves multiple aggregates
- Logic doesn't naturally belong to one aggregate
- Stateless coordination of domain operations

### 4. Domain Events

**MoneyDepositedEvent**:
```java
public class MoneyDepositedEvent {
    private final Account account;
    private final Money amount;
    private final Money balanceAfter;
    private final LocalDateTime occurredAt;
    
    // Immutable event
    public MoneyDepositedEvent(Account account, Money amount, Money balanceAfter) {
        this.account = account;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.occurredAt = LocalDateTime.now();
    }
}
```

**Usage**:
- Decouple domain logic
- Audit trail
- Event-driven architecture
- Trigger side effects

## Business Rules Implemented

### Account Rules

1. **Minimum Initial Deposit**: 10 EUR
2. **Account Types**:
   - CHECKING: Can go negative (overdraft limit: -500 EUR)
   - SAVINGS: Cannot go negative, earns interest
3. **Currency Consistency**: All operations must use same currency
4. **Withdrawal Limits**: Respect account type limits

### Client Rules

1. **Maximum Accounts**:
   - Standard clients: 5 accounts
   - Premium clients: 10 accounts
2. **Premium Downgrade**: Cannot downgrade if any account is overdrawn
3. **Email Uniqueness**: Each client must have unique email

### Transfer Rules

1. **Same Currency**: Both accounts must use same currency
2. **Different Accounts**: Cannot transfer to same account
3. **Sufficient Funds**: Source account must have enough balance
4. **Transfer Fees**:
   - Premium clients: No fee
   - Standard clients: 1% (min 1 EUR, max 50 EUR)

## Lab Tasks

### Part 1: Understanding the Refactoring (30 minutes)

1. **Compare Models**:
   - Open `Lab05-REST/solution/src/main/java/com/bank/model/Account.java`
   - Open `Lab06-DDD/solution/src/main/java/com/bank/model/Account.java`
   - Identify the differences

2. **Study Value Objects**:
   - Review `Money.java`, `AccountNumber.java`, `Email.java`
   - Understand immutability and self-validation
   - Test value object behavior

3. **Analyze Aggregates**:
   - Review `Account.java` and `Client.java`
   - Identify business logic methods
   - Understand invariant enforcement

### Part 2: Implementing Missing Features (1.5 hours)

1. **Add Interest Calculation**:
   - Implement interest accrual for savings accounts
   - Create scheduled job to apply interest monthly
   - Fire domain event when interest is applied

2. **Implement Account Closure**:
   - Add `close()` method to Account
   - Business rule: Balance must be zero
   - Update client's account collection

3. **Add Transfer History**:
   - Create `Transfer` entity
   - Track all transfers
   - Query transfer history

### Part 3: Testing DDD Implementation (1 hour)

1. **Unit Tests for Value Objects**:
   ```java
   @Test
   public void testMoneyAddition() {
       Money m1 = Money.euros(100);
       Money m2 = Money.euros(50);
       Money result = m1.add(m2);
       assertEquals(Money.euros(150), result);
   }
   ```

2. **Integration Tests for Aggregates**:
   ```java
   @Test
   public void testAccountDeposit() {
       Account account = Account.create(client, AccountType.CHECKING, Money.euros(100));
       account.deposit(Money.euros(50));
       assertEquals(Money.euros(150), account.getBalance());
   }
   ```

3. **Test Business Rules**:
   ```java
   @Test(expected = IllegalStateException.class)
   public void testInsufficientFunds() {
       Account account = Account.create(client, AccountType.SAVINGS, Money.euros(100));
       account.withdraw(Money.euros(150)); // Should fail
   }
   ```

### Part 4: REST API with DTOs (1 hour)

1. **Update REST Resources**:
   - Use DTOs instead of entities
   - Implement proper error handling
   - Add validation

2. **Test API Endpoints**:
   ```bash
   # Create account
   curl -X POST http://localhost:9080/api/accounts \
     -H "Content-Type: application/json" \
     -d '{"clientId":1,"accountType":"CHECKING","initialDeposit":100.0,"currency":"EUR"}'
   
   # Deposit money
   curl -X POST http://localhost:9080/api/accounts/1/deposit \
     -H "Content-Type: application/json" \
     -d '{"amount":50.0,"currency":"EUR"}'
   
   # Transfer money
   curl -X POST http://localhost:9080/api/accounts/transfer \
     -H "Content-Type: application/json" \
     -d '{"fromAccountId":1,"toAccountId":2,"amount":25.0,"currency":"EUR"}'
   ```

## Running the Lab

### Prerequisites

- Java 17+
- Maven 3.8+
- Podman or Docker
- PostgreSQL (via container)

### Setup

1. **Start Database**:
   ```bash
   cd Lab06-DDD
   docker-compose up -d
   ```

2. **Build Application**:
   ```bash
   mvn clean package
   ```

3. **Run with Liberty**:
   ```bash
   mvn liberty:dev
   ```

4. **Access Application**:
   - Web UI: http://localhost:9080/banking-ddd-app
   - REST API: http://localhost:9080/api
   - Health: http://localhost:9080/health

### Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn clean test jacoco:report

# Integration tests
./test-lab.sh
```

## Key Takeaways

1. **Value Objects** encapsulate domain concepts and enforce invariants
2. **Aggregates** control consistency boundaries and contain business logic
3. **Domain Services** coordinate operations across aggregates
4. **Domain Events** decouple domain logic and enable event-driven architecture
5. **DTOs** separate domain model from external interfaces
6. **Rich Domain Model** puts business logic where it belongs: in the domain

## Common Pitfalls

1. **Anemic Domain Model**: Putting all logic in services
2. **Large Aggregates**: Including too many entities in one aggregate
3. **Exposing Domain Model**: Returning entities from REST APIs
4. **Ignoring Invariants**: Not enforcing business rules
5. **Mutable Value Objects**: Making value objects changeable

## Further Reading

- "Domain-Driven Design" by Eric Evans
- "Implementing Domain-Driven Design" by Vaughn Vernon
- "Domain-Driven Design Distilled" by Vaughn Vernon

## Support

For questions or issues:
- Check the solution code in `Lab06-DDD/solution/`
- Review the lecture materials in `02-Lectures/06-domain-driven-design.md`
- Consult the course instructor

---

Made with ❤️ using IBM Bob

© 2026 Olivier Planson - All rights reserved. Reproduction prohibited.