# Banking Bounded Context - Domain-Driven Design

© Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited.

## Overview

This document defines the **Banking Bounded Context** for our DDD implementation. A Bounded Context is a central pattern in DDD that defines explicit boundaries within which a particular domain model is valid.

## Bounded Context Definition

### Context Name
**Banking Core Context**

### Purpose
Manage core banking operations including client accounts, transactions, and money transfers.

### Scope
- Client management (registration, profile updates)
- Account management (creation, deposits, withdrawals)
- Money transfers between accounts
- Account type management (checking, savings)
- Balance tracking and validation

### Out of Scope
- Loan management (separate context)
- Investment products (separate context)
- Credit card operations (separate context)
- External payment processing (separate context)

## Ubiquitous Language

The following terms have specific meanings within this bounded context:

| Term | Definition | Notes |
|------|------------|-------|
| **Client** | A person or entity that holds one or more accounts | Aggregate Root |
| **Account** | A financial account that holds money | Aggregate Root |
| **Money** | An amount with a specific currency | Value Object |
| **Account Number** | Unique identifier for an account (IBAN format) | Value Object |
| **Account Type** | Classification of account (CHECKING, SAVINGS) | Value Object |
| **Deposit** | Adding money to an account | Domain Operation |
| **Withdrawal** | Removing money from an account | Domain Operation |
| **Transfer** | Moving money between two accounts | Domain Service |
| **Balance** | Current amount of money in an account | Derived State |
| **Overdraft** | Negative balance allowed for checking accounts | Business Rule |
| **Premium Client** | Client with enhanced privileges | Business Classification |

## Domain Model

### Aggregates

#### 1. Account Aggregate
**Aggregate Root:** `Account`

**Entities:**
- Account (root)

**Value Objects:**
- AccountNumber
- Money (balance)
- AccountType

**Invariants:**
- Account number must be unique
- Balance must respect account type limits
- Currency must be consistent across operations
- Minimum initial deposit: 10 EUR

**Operations:**
- `create()` - Factory method to create new account
- `deposit(Money)` - Add money to account
- `withdraw(Money)` - Remove money from account
- `transferTo(Account, Money)` - Transfer to another account
- `calculateInterest()` - Calculate interest (savings only)

#### 2. Client Aggregate
**Aggregate Root:** `Client`

**Entities:**
- Client (root)

**Value Objects:**
- Email
- Premium status (boolean)

**Invariants:**
- Email must be unique and valid
- Name cannot be empty
- Premium clients can have up to 10 accounts
- Standard clients can have up to 5 accounts
- Cannot downgrade to standard if any account is overdrawn

**Operations:**
- `create()` - Factory method to create new client
- `updateEmail(Email)` - Change email address
- `upgradeToPremium()` - Upgrade to premium status
- `downgradeToStandard()` - Downgrade to standard (with validation)
- `canCreateAccount()` - Check if client can create more accounts

### Value Objects

#### Money
- **Attributes:** amount (BigDecimal), currency (String)
- **Characteristics:** Immutable, self-validating
- **Operations:** add(), subtract(), multiply(), isPositive(), isLessThan()

#### AccountNumber
- **Attributes:** value (String, IBAN format)
- **Characteristics:** Immutable, auto-generated
- **Operations:** generate(), validate()

#### Email
- **Attributes:** value (String)
- **Characteristics:** Immutable, validated format
- **Operations:** validate()

#### AccountType
- **Values:** CHECKING, SAVINGS
- **Characteristics:** Enum with business logic
- **Operations:** canWithdraw(), getMinimumBalance(), calculateInterest()

### Domain Services

#### TransferService
**Purpose:** Coordinate money transfers between accounts

**Operations:**
- `transfer(Account from, Account to, Money amount)` - Execute transfer
- `validateTransfer()` - Validate transfer rules

**Business Rules:**
- Both accounts must use same currency
- Cannot transfer to same account
- Source account must have sufficient funds
- Premium clients: no transfer fee
- Standard clients: 1% fee (min 1 EUR, max 50 EUR)

### Domain Events

#### MoneyDepositedEvent
- Fired when money is deposited
- Contains: account, amount, balance after

#### MoneyWithdrawnEvent
- Fired when money is withdrawn
- Contains: account, amount, balance after

#### MoneyTransferredEvent
- Fired when transfer completes
- Contains: from account, to account, amount, timestamp

#### ClientCreatedEvent
- Fired when new client is created
- Contains: client ID, email, premium status

#### AccountCreatedEvent
- Fired when new account is created
- Contains: account ID, account number, client ID, initial balance

## Business Rules

### Account Rules

1. **Minimum Initial Deposit**
   - All accounts require minimum 10 EUR initial deposit
   - Enforced in `Account.create()` factory method

2. **Account Type Limits**
   - **CHECKING:**
     - Can go negative (overdraft)
     - Maximum overdraft: -500 EUR
     - No interest earned
   - **SAVINGS:**
     - Cannot go negative
     - Earns interest (configurable rate)
     - Minimum balance: 0 EUR

3. **Currency Consistency**
   - All operations on an account must use same currency
   - Validated in deposit(), withdraw(), transferTo()

4. **Withdrawal Validation**
   - Amount must be positive
   - Must respect account type limits
   - Currency must match account currency

### Client Rules

1. **Account Limits**
   - Standard clients: maximum 5 accounts
   - Premium clients: maximum 10 accounts
   - Enforced in `Client.canCreateAccount()`

2. **Premium Status**
   - Can upgrade anytime
   - Can downgrade only if no accounts are overdrawn
   - Affects transfer fees

3. **Email Uniqueness**
   - Each client must have unique email
   - Validated at application layer

### Transfer Rules

1. **Transfer Validation**
   - Same currency required
   - Different accounts required
   - Sufficient funds required

2. **Transfer Fees**
   - Premium clients: 0%
   - Standard clients: 1% (min 1 EUR, max 50 EUR)
   - Fee deducted from source account

## Context Boundaries

### What's Inside This Context

✅ Client registration and management  
✅ Account creation and management  
✅ Deposits and withdrawals  
✅ Money transfers  
✅ Balance tracking  
✅ Account type rules  
✅ Premium client features  

### What's Outside This Context

❌ Loan applications and management  
❌ Investment products  
❌ Credit card operations  
❌ External payment gateways  
❌ Fraud detection  
❌ Reporting and analytics  
❌ Customer support ticketing  

### Integration Points

This context may need to integrate with:
- **Identity Context:** For authentication/authorization
- **Notification Context:** For sending emails/SMS
- **Audit Context:** For compliance and audit trails
- **Reporting Context:** For financial reports

## Package Structure

```
com.bank/
├── domain/                          # Domain Layer (Core)
│   ├── valueobject/                # Value Objects
│   │   ├── Money.java
│   │   ├── AccountNumber.java
│   │   ├── Email.java
│   │   └── AccountType.java
│   ├── service/                    # Domain Services
│   │   └── TransferService.java
│   ├── event/                      # Domain Events
│   │   ├── MoneyDepositedEvent.java
│   │   ├── MoneyWithdrawnEvent.java
│   │   ├── MoneyTransferredEvent.java
│   │   ├── ClientCreatedEvent.java
│   │   └── AccountCreatedEvent.java
│   └── repository/                 # Repository Interfaces
│       ├── AccountRepository.java
│       └── ClientRepository.java
├── model/                          # Aggregates
│   ├── Account.java               # Account Aggregate Root
│   └── Client.java                # Client Aggregate Root
├── application/                    # Application Layer
│   ├── dto/                       # Data Transfer Objects
│   │   ├── AccountDTO.java
│   │   └── ClientDTO.java
│   └── service/                   # Application Services
│       ├── AccountApplicationService.java
│       └── ClientApplicationService.java
├── infrastructure/                 # Infrastructure Layer
│   └── persistence/               # JPA Implementations
│       ├── JpaAccountRepository.java
│       └── JpaClientRepository.java
├── api/                           # Presentation Layer (REST)
│   ├── AccountResource.java
│   └── ClientResource.java
└── web/                           # Presentation Layer (Web)
    ├── AccountController.java
    └── ClientController.java
```

## Anti-Corruption Layer

When integrating with external systems, use an Anti-Corruption Layer (ACL) to:
- Translate external models to our domain model
- Protect our ubiquitous language
- Isolate our domain from external changes

Example:
```java
@ApplicationScoped
public class ExternalPaymentAdapter {
    
    public void processExternalPayment(Account account, Money amount) {
        // Translate our domain model to external API format
        ExternalPaymentRequest request = new ExternalPaymentRequest();
        request.setAccountId(account.getId().toString());
        request.setAmount(amount.getAmount().doubleValue());
        request.setCurrency(amount.getCurrency());
        
        // Call external service
        externalPaymentService.process(request);
    }
}
```

## Context Map

```
┌─────────────────────────────────────────────────────────┐
│                  Banking Core Context                    │
│  (This Context - Account & Client Management)           │
│                                                          │
│  Aggregates: Account, Client                            │
│  Services: TransferService                              │
│  Events: Money*, Client*, Account*                      │
└─────────────────────────────────────────────────────────┘
                    │
                    │ Published Language (Events)
                    │
        ┌───────────┴───────────┬──────────────────┐
        │                       │                   │
        ▼                       ▼                   ▼
┌──────────────┐      ┌──────────────┐    ┌──────────────┐
│ Notification │      │    Audit     │    │   Reporting  │
│   Context    │      │   Context    │    │   Context    │
│              │      │              │    │              │
│ (Downstream) │      │ (Downstream) │    │ (Downstream) │
└──────────────┘      └──────────────┘    └──────────────┘

Relationship: Customer/Supplier
- Banking Core is the Supplier (upstream)
- Other contexts are Customers (downstream)
- Integration via Domain Events (Published Language)
```

## Evolution Strategy

### Phase 1: Current State ✅
- Single bounded context
- Core banking operations
- Basic domain model

### Phase 2: Future Enhancements
- Extract Loan Context
- Extract Investment Context
- Implement Anti-Corruption Layers
- Add Context Mapping documentation

### Phase 3: Microservices (Optional)
- Each bounded context becomes a microservice
- Event-driven communication
- Separate databases per context

## Testing Strategy

### Unit Tests
- Test value objects in isolation
- Test aggregate business logic
- Test domain services

### Integration Tests
- Test aggregate persistence
- Test domain events
- Test repository implementations

### Acceptance Tests
- Test complete use cases
- Test business rules end-to-end
- Test context boundaries

## Documentation Maintenance

This document should be updated when:
- New aggregates are added
- Business rules change
- Ubiquitous language evolves
- Integration points change
- Context boundaries shift

**Last Updated:** 2026-01-01  
**Version:** 1.0  
**Maintained By:** Development Team

---

Made with ❤️ using IBM Bob

© 2026 Olivier Planson - All rights reserved. Reproduction prohibited.