# Lab 06 - Domain-Driven Design - Solution Status

© Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited.

## Implementation Status

### ✅ Completed Features

#### 1. Value Objects
- [x] **Money** - Immutable monetary value with currency
  - BigDecimal for precision
  - Currency validation
  - Arithmetic operations (add, subtract, multiply)
  - Comparison methods
  - Self-validating

- [x] **AccountNumber** - IBAN-like account number
  - Format validation (FR + 25 digits)
  - Generation methods
  - Masked display
  - Immutable

- [x] **Email** - Email address value object
  - RFC 5322 compliant validation
  - Normalization (lowercase)
  - Masked display
  - Domain extraction

- [x] **AccountType** - Enum value object
  - CHECKING and SAVINGS types
  - Business rules per type
  - Interest calculation
  - Overdraft limits

#### 2. Aggregate Roots

- [x] **Account Aggregate**
  - Uses Value Objects (Money, AccountNumber, AccountType)
  - Factory methods for creation
  - Rich business logic (deposit, withdraw, transfer)
  - Invariant enforcement
  - Business rule validation
  - No public setters (encapsulation)

- [x] **Client Aggregate**
  - Uses Email Value Object
  - Controls Account collection
  - Premium/Standard status
  - Account limits enforcement
  - Business logic methods

#### 3. Domain Services

- [x] **TransferService**
  - Coordinates transfers between accounts
  - Fee calculation
  - Business rule enforcement
  - Stateless service

#### 4. Domain Events

- [x] **MoneyDepositedEvent**
- [x] **MoneyWithdrawnEvent**
- [x] **MoneyTransferredEvent**
- [x] Event observers (from Lab05)

#### 5. Application Layer

- [x] **DTOs**
  - AccountDTO
  - ClientDTO
  - Separation from domain model

- [x] **Application Services**
  - AccountService (needs update for DDD)
  - ClientService (needs update for DDD)

#### 6. Infrastructure

- [x] **Database Migration**
  - V5__refactor_for_ddd.sql
  - New columns for Value Objects
  - Backward compatibility
  - Constraints and indexes

- [x] **JPA Mappings**
  - @Embedded for Value Objects
  - @AttributeOverride for column mapping
  - Proper entity relationships

#### 7. Testing

- [x] **Test Scripts**
  - podman-test.sh (idempotent)
  - docker-compose.yml
  - Comprehensive test scenarios

- [x] **Documentation**
  - Complete README.md
  - Code examples
  - Architecture diagrams
  - Best practices

### ⚠️ Partially Implemented

#### 1. Application Services Update
- AccountService and ClientService still use old patterns
- Need to be updated to use:
  - Factory methods instead of constructors
  - Value Objects instead of primitives
  - Domain Events for all operations

#### 2. REST API Update
- AccountResource and ClientResource need updates
- Should use DTOs consistently
- Need proper error handling for domain exceptions

#### 3. Web Controllers
- AccountController and ClientController need updates
- JSP views need updates for Value Objects
- Form handling for new types

### ❌ Not Implemented (Future Enhancements)

#### 1. Advanced Features
- [ ] Interest accrual scheduled job
- [ ] Account closure workflow
- [ ] Transfer history tracking
- [ ] Transaction entity

#### 2. Additional Value Objects
- [ ] Address Value Object
- [ ] PhoneNumber Value Object
- [ ] ClientName Value Object (first/last name)

#### 3. Additional Domain Services
- [ ] InterestCalculationService
- [ ] AccountClosureService
- [ ] ReportingService

#### 4. Event Sourcing
- [ ] Event store
- [ ] Event replay
- [ ] CQRS pattern

#### 5. Specifications Pattern
- [ ] Account specifications
- [ ] Client specifications
- [ ] Query objects

## DDD Patterns Applied

### Strategic Patterns
- ✅ Bounded Context (Banking domain)
- ✅ Ubiquitous Language (Money, Account, Client, Transfer)
- ⚠️ Context Mapping (partially - needs documentation)

### Tactical Patterns
- ✅ Value Objects (Money, AccountNumber, Email, AccountType)
- ✅ Entities (Account, Client)
- ✅ Aggregates (Account Aggregate, Client Aggregate)
- ✅ Aggregate Roots (Account, Client)
- ✅ Domain Services (TransferService)
- ✅ Domain Events (MoneyDeposited, MoneyWithdrawn, MoneyTransferred)
- ✅ Repositories (JPA-based)
- ⚠️ Factories (partially - using factory methods)
- ❌ Specifications (not implemented)

## Business Rules Implemented

### Account Rules
1. ✅ Minimum initial deposit: 10 EUR
2. ✅ Account type limits:
   - CHECKING: Can overdraft up to -500 EUR
   - SAVINGS: Cannot go negative
3. ✅ Currency consistency in operations
4. ✅ Withdrawal validation

### Client Rules
1. ✅ Maximum accounts per client:
   - Standard: 5 accounts
   - Premium: 10 accounts
2. ✅ Premium downgrade restrictions
3. ✅ Email uniqueness
4. ✅ Name validation

### Transfer Rules
1. ✅ Same currency requirement
2. ✅ Different accounts requirement
3. ✅ Sufficient funds check
4. ✅ Transfer fees:
   - Premium: Free
   - Standard: 1% (min 1 EUR, max 50 EUR)

## Testing Coverage

### Unit Tests
- ⚠️ Value Objects (need to be created)
- ⚠️ Aggregates (need to be created)
- ⚠️ Domain Services (need to be created)

### Integration Tests
- ✅ Database migrations
- ✅ REST API endpoints
- ✅ Business rule enforcement
- ⚠️ Event handling (needs more tests)

### End-to-End Tests
- ✅ podman-test.sh script
- ✅ Complete workflow testing
- ✅ DDD pattern verification

## Migration Strategy: Option 4 (Backward Compatible)

### Why Option 4?

This lab demonstrates **OPTION 4: Backward Compatible Migration**, a production-ready approach for evolving database schemas without breaking changes.

### Implementation Details

**Phase 1 (Current - Lab 06):**
```sql
accounts table:
├── balance (DEPRECATED - kept for compatibility)
├── balance_amount (NEW - Money Value Object amount)
└── balance_currency (NEW - Money Value Object currency)
```

**Synchronization Mechanism:**
- PostgreSQL trigger automatically syncs `balance` with `balance_amount`
- Zero code changes needed for synchronization
- Both old and new code can coexist

**Phase 2 (Future - Lab 07+):**
- Update all application code to use Money Value Object
- Test thoroughly in production
- Monitor for any issues

**Phase 3 (Future - V6 migration):**
```sql
-- After 3-6 months deprecation period:
DROP TRIGGER trigger_sync_account_balance ON accounts;
DROP FUNCTION sync_account_balance();
ALTER TABLE accounts DROP COLUMN balance;
```

### Pedagogical Value

This approach teaches students:

1. **Real-World Migration Patterns**
   - How companies like Stripe, GitHub handle API evolution
   - Zero-downtime deployment strategies
   - Risk mitigation in production systems

2. **Backward Compatibility**
   - Why breaking changes are costly
   - How to maintain compatibility during transitions
   - Deprecation timeline management

3. **Database Evolution**
   - Additive changes vs destructive changes
   - Using triggers for data synchronization
   - Planning multi-phase migrations

4. **Professional Practices**
   - Documentation of deprecation
   - Clear migration paths
   - Rollback strategies

### Comparison with Other Options

| Option | Downtime | Risk | Rollback | Complexity |
|--------|----------|------|----------|------------|
| 1. Breaking Change | ❌ Yes | 🔴 High | ❌ Hard | 🟢 Low |
| 2. Big Bang | ❌ Yes | 🔴 High | ❌ Hard | 🟡 Medium |
| 3. Dual Write | ✅ No | 🟡 Medium | 🟡 Medium | 🔴 High |
| **4. Backward Compatible** | ✅ **No** | 🟢 **Low** | ✅ **Easy** | 🟡 **Medium** |

**Option 4 is the winner** for production systems!

## Known Issues

1. **Backward Compatibility Mechanism**: Old `balance` column kept for compatibility
   - **Purpose**: Demonstrates Option 4 migration strategy
   - **Mechanism**: PostgreSQL trigger keeps it in sync with `balance_amount`
   - **Timeline**: Will be removed in V6 migration (after code migration complete)
   - **Learning**: Shows how to evolve schemas without breaking changes

2. **Service Layer**: Not fully refactored to DDD
   - Still uses some anemic patterns
   - Needs to delegate more to domain

3. **Error Handling**: Domain exceptions need better mapping
   - IllegalArgumentException → 400 Bad Request
   - IllegalStateException → 422 Unprocessable Entity

4. **Validation**: Mix of Bean Validation and domain validation
   - Should consolidate validation strategy
   - Consider removing Bean Validation from domain

## Next Steps

### Priority 1 (Critical)
1. Update AccountService to use factory methods
2. Update ClientService to use factory methods
3. Update REST resources to use DTOs
4. Add comprehensive unit tests

### Priority 2 (Important)
1. Update web controllers and JSPs
2. Improve error handling
3. Add more domain events
4. Document bounded context

### Priority 3 (Nice to Have)
1. Implement specifications pattern
2. Add event sourcing
3. Create additional value objects
4. Implement CQRS

## Lessons Learned

### What Worked Well
1. Value Objects greatly improved type safety
2. Factory methods enforce business rules at creation
3. Immutability prevents bugs
4. Domain events decouple logic
5. DTOs protect domain model

### Challenges
1. JPA with immutable Value Objects requires workarounds
2. Refactoring existing code is time-consuming
3. Team needs training on DDD concepts
4. Balance between purity and pragmatism

### Best Practices
1. Start with Value Objects - quick wins
2. Refactor incrementally - don't rewrite everything
3. Keep backward compatibility during transition
4. Document ubiquitous language
5. Test business rules thoroughly

## References

- Eric Evans - "Domain-Driven Design"
- Vaughn Vernon - "Implementing Domain-Driven Design"
- Martin Fowler - "Patterns of Enterprise Application Architecture"

---

**Status**: 🟡 In Progress (Core DDD patterns implemented, services need refactoring)

**Last Updated**: 2026-01-01

Made with ❤️ using IBM Bob

© 2026 Olivier Planson - All rights reserved. Reproduction prohibited.