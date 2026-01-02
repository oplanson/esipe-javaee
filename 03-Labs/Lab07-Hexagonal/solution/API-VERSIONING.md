<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# API Versioning in Hexagonal Architecture

## Overview

This application demonstrates **API versioning** as a key benefit of hexagonal architecture. Multiple REST adapters (v1 and v2) coexist for the same application ports, showing how the architecture supports API evolution without changing domain or application logic.

## Architecture Benefit

**Hexagonal Architecture Principle**: Multiple adapters can implement the same ports

```
┌─────────────────────────────────────────────────────────┐
│                    Application Core                      │
│  ┌──────────────────────────────────────────────────┐  │
│  │         Primary Ports (Use Cases)                 │  │
│  │  - AccountManagementUseCase                       │  │
│  │  - ClientManagementUseCase                        │  │
│  │  - MoneyOperationsUseCase                         │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
           ▲                           ▲
           │                           │
    ┌──────┴──────┐           ┌───────┴────────┐
    │   REST v1   │           │    REST v2     │
    │   Adapter   │           │    Adapter     │
    │ /api/v1/*   │           │  /api/v2/*     │
    └─────────────┘           └────────────────┘
```

Both adapters use the **same application ports**, but expose different API contracts.

## API Versions

### Version 1 (v1) - Simple Format

**Base Path**: `/api/v1/`

**Philosophy**: Backward-compatible, simple JSON format
- Amounts as simple numbers
- Minimal structure
- Easy to consume

**Example Endpoints**:

```bash
# Create account with simple balance
POST /api/v1/accounts
{
  "clientId": 1,
  "initialBalance": 1000.00,
  "currency": "EUR",
  "accountType": "CHECKING"
}

# Deposit with simple amount
POST /api/v1/accounts/1/deposit
{
  "amount": 100.00,
  "currency": "EUR"
}

# Get account - simple balance format
GET /api/v1/accounts/1
Response:
{
  "id": 1,
  "accountNumber": "ACC001",
  "balance": 1000.00,
  "currency": "EUR",
  "accountType": "CHECKING",
  "clientId": 1,
  "active": true
}
```

### Version 2 (v2) - Rich Format with Value Objects

**Base Path**: `/api/v2/`

**Philosophy**: Domain-rich, exposes value objects
- Money as object with amount and currency
- Better representation of domain concepts
- More explicit and type-safe

**Example Endpoints**:

```bash
# Create account with Money value object
POST /api/v2/accounts
{
  "clientId": 1,
  "initialBalance": {
    "amount": 1000.00,
    "currency": "EUR"
  },
  "accountType": "CHECKING"
}

# Deposit with Money value object
POST /api/v2/accounts/1/deposit
{
  "amount": 100.00,
  "currency": "EUR"
}

# Get account - Money as object
GET /api/v2/accounts/1
Response:
{
  "id": 1,
  "accountNumber": "ACC001",
  "balance": {
    "amount": 1000.00,
    "currency": "EUR"
  },
  "accountType": "CHECKING",
  "clientId": 1,
  "active": true
}

# V2 exclusive: Premium client operations
GET /api/v2/clients/premium
POST /api/v2/clients/1/upgrade
POST /api/v2/clients/1/downgrade
```

## Implementation Details

### Directory Structure

```
infrastructure/rest/
├── RestApplication.java          # Base path: /api
└── adapter/
    ├── v1/
    │   ├── AccountRestAdapter.java    # @Path("/v1/accounts")
    │   └── ClientRestAdapter.java     # @Path("/v1/clients")
    └── v2/
        ├── AccountRestAdapterV2.java  # @Path("/v2/accounts")
        └── ClientRestAdapterV2.java   # @Path("/v2/clients")
```

### Key Differences

| Aspect | V1 | V2 |
|--------|----|----|
| **Balance Format** | `"balance": 1000.00` | `"balance": {"amount": 1000.00, "currency": "EUR"}` |
| **Money Operations** | Simple BigDecimal | Money DTO object |
| **Premium Features** | ❌ Not available | ✅ Available |
| **Backward Compatibility** | ✅ Stable | ⚠️ May evolve |
| **Domain Richness** | Low | High |

### Code Example: V1 Adapter

```java
@Path("/v1/accounts")
public class AccountRestAdapter {
    
    @POST
    public Response openAccount(OpenAccountRequest request) {
        // Convert simple format to domain
        OpenAccountCommand command = new OpenAccountCommand(
            request.clientId,
            null,
            Money.of(BigDecimal.valueOf(request.initialBalance), currency),
            AccountType.valueOf(request.accountType),
            currency
        );
        // Use same application port
        AccountDTO account = accountManagement.openAccount(command);
        return Response.status(Response.Status.CREATED).entity(account).build();
    }
    
    public static class OpenAccountRequest {
        public Long clientId;
        public double initialBalance;  // Simple format
        public String currency;
        public String accountType;
    }
}
```

### Code Example: V2 Adapter

```java
@Path("/v2/accounts")
public class AccountRestAdapterV2 {
    
    @POST
    public Response openAccount(OpenAccountRequestV2 request) {
        // Convert rich format to domain
        MoneyDTO balance = request.initialBalance;
        OpenAccountCommand command = new OpenAccountCommand(
            request.clientId,
            null,
            Money.of(balance.amount, balance.currency),
            AccountType.valueOf(request.accountType),
            balance.currency
        );
        // Use same application port
        AccountDTO account = accountManagement.openAccount(command);
        return Response.status(Response.Status.CREATED).entity(account).build();
    }
    
    public static class MoneyDTO {
        public BigDecimal amount;
        public String currency;
    }
    
    public static class OpenAccountRequestV2 {
        public Long clientId;
        public MoneyDTO initialBalance;  // Rich format
        public String accountType;
    }
}
```

## Benefits of This Approach

### 1. **API Evolution Without Breaking Changes**
- V1 clients continue working unchanged
- V2 introduces new features and better structure
- Both versions maintained simultaneously

### 2. **Domain Logic Unchanged**
- Application layer (use cases) remains identical
- Domain entities and value objects untouched
- Business rules consistent across versions

### 3. **Adapter Flexibility**
- Each adapter translates its format to domain commands
- Adapters are independent and isolated
- Easy to add V3, V4, etc.

### 4. **Clear Migration Path**
```
V1 (Legacy) → V2 (Current) → V3 (Future)
     ↓              ↓              ↓
     └──────────────┴──────────────┘
              Same Domain Core
```

### 5. **Testing Independence**
- Test V1 and V2 separately
- Domain tests remain version-agnostic
- Integration tests per version

## Migration Strategy

### For API Consumers

1. **Start with V1** (simple, stable)
2. **Evaluate V2** (richer, more features)
3. **Migrate gradually** (endpoint by endpoint)
4. **Deprecate V1** (after migration period)

### Deprecation Timeline Example

```
Year 1: V1 (stable), V2 (beta)
Year 2: V1 (stable), V2 (stable)
Year 3: V1 (deprecated), V2 (stable), V3 (beta)
Year 4: V1 (removed), V2 (stable), V3 (stable)
```

## Testing Both Versions

### V1 Tests
```bash
# Create client
curl -X POST http://localhost:9080/api/v1/clients \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'

# Create account
curl -X POST http://localhost:9080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{"clientId": 1, "initialBalance": 1000.00, "currency": "EUR", "accountType": "CHECKING"}'
```

### V2 Tests
```bash
# Create client
curl -X POST http://localhost:9080/api/v2/clients \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Doe", "email": "jane@example.com", "premium": true}'

# Create account with Money VO
curl -X POST http://localhost:9080/api/v2/accounts \
  -H "Content-Type: application/json" \
  -d '{"clientId": 1, "initialBalance": {"amount": 1000.00, "currency": "EUR"}, "accountType": "CHECKING"}'

# Upgrade to premium (V2 only)
curl -X POST http://localhost:9080/api/v2/clients/1/upgrade
```

## Conclusion

This implementation demonstrates how **hexagonal architecture enables API versioning** by:

1. **Keeping domain pure** - No API concerns in business logic
2. **Supporting multiple adapters** - V1 and V2 coexist peacefully
3. **Enabling evolution** - Add new versions without breaking existing ones
4. **Maintaining consistency** - Same business rules across all versions

The architecture's flexibility allows the API to evolve while the core domain remains stable and focused on business logic.

---

**Made with Bob** - Demonstrating hexagonal architecture benefits through practical API versioning