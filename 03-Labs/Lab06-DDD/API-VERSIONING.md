# API Versioning Strategy - Lab 06

© Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

## Overview

This document explains the API versioning strategy implemented in Lab 06 to demonstrate professional practices for evolving APIs without breaking existing clients.

## API Versions

### Version 1 (DEPRECATED) - `/api/accounts`

**Status:** ⚠️ Deprecated (Sunset: 2026-06-01)

Legacy API from Lab 05 with simple balance representation.

**Response Format:**
```json
{
  "id": 1,
  "number": "ACC001",
  "balance": 1000.00,
  "type": "CHECKING"
}
```

**Deprecation Headers:**
```http
X-API-Version: 1.0
X-API-Deprecated: true
X-API-Deprecation-Info: This API version is deprecated. Use /api/v2/accounts instead.
X-API-Sunset-Date: 2026-06-01
X-API-Migration-Guide: https://docs.bank.com/api/v1-to-v2-migration
```

### Version 2 (CURRENT) - `/api/v2/accounts`

**Status:** ✅ Current

New API with Money Value Object (amount + currency).

**Response Format:**
```json
{
  "id": 1,
  "accountNumber": "FR1234567890123456789012345",
  "balance": {
    "amount": 1000.00,
    "currency": "EUR"
  },
  "accountType": "CHECKING",
  "clientId": 1
}
```

**Response Headers:**
```http
X-API-Version: 2.0
```

## Migration Guide

### Breaking Changes in V2

1. **Balance Format**
   - V1: `"balance": 1000.00` (simple number)
   - V2: `"balance": {"amount": 1000.00, "currency": "EUR"}` (Money object)

2. **Field Names**
   - V1: `"number"` → V2: `"accountNumber"`
   - V1: `"type"` → V2: `"accountType"`

3. **Request Format for Operations**
   - V1: Query parameters (`?amount=100`)
   - V2: JSON body with Money object

### Step-by-Step Migration

#### Step 1: Update GET Requests

**V1 (Old):**
```bash
curl http://localhost:9080/api/accounts/1
```

**V2 (New):**
```bash
curl http://localhost:9080/api/v2/accounts/1
```

**Response Comparison:**

V1:
```json
{
  "id": 1,
  "number": "ACC001",
  "balance": 1000.00,
  "type": "CHECKING"
}
```

V2:
```json
{
  "id": 1,
  "accountNumber": "FR1234567890123456789012345",
  "balance": {
    "amount": 1000.00,
    "currency": "EUR"
  },
  "accountType": "CHECKING",
  "clientId": 1
}
```

#### Step 2: Update POST Requests (Deposit)

**V1 (Old):**
```bash
curl -X POST "http://localhost:9080/api/accounts/1/deposit?amount=500"
```

**V2 (New):**
```bash
curl -X POST http://localhost:9080/api/v2/accounts/1/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 500.00,
    "currency": "EUR"
  }'
```

#### Step 3: Update Transfer Requests

**V1 (Old):**
```bash
curl -X POST "http://localhost:9080/api/accounts/1/transfer?toId=2&amount=100"
```

**V2 (New):**
```bash
curl -X POST http://localhost:9080/api/v2/accounts/1/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "toAccountId": 2,
    "amount": 100.00,
    "currency": "EUR"
  }'
```

## Code Examples

### JavaScript/TypeScript Client

**V1 Client:**
```javascript
// Old client code
async function getAccount(id) {
  const response = await fetch(`/api/accounts/${id}`);
  const account = await response.json();
  console.log(`Balance: ${account.balance}`);
  return account;
}
```

**V2 Client:**
```javascript
// New client code
async function getAccount(id) {
  const response = await fetch(`/api/v2/accounts/${id}`);
  const account = await response.json();
  console.log(`Balance: ${account.balance.amount} ${account.balance.currency}`);
  return account;
}
```

### Java Client

**V1 Client:**
```java
// Old client code
public class AccountClient {
    public Account getAccount(Long id) {
        Response response = client.target("/api/accounts/" + id).request().get();
        Account account = response.readEntity(Account.class);
        System.out.println("Balance: " + account.getBalance());
        return account;
    }
}
```

**V2 Client:**
```java
// New client code
public class AccountClientV2 {
    public AccountDTO getAccount(Long id) {
        Response response = client.target("/api/v2/accounts/" + id).request().get();
        AccountDTO account = response.readEntity(AccountDTO.class);
        Money balance = account.getBalance();
        System.out.println("Balance: " + balance.getAmount() + " " + balance.getCurrency());
        return account;
    }
}
```

## Testing Both Versions

### Test V1 (Deprecated)

```bash
# Get account - V1
curl -v http://localhost:9080/api/accounts/1

# Check deprecation headers
# X-API-Deprecated: true
# X-API-Sunset-Date: 2026-06-01
```

### Test V2 (Current)

```bash
# Get account - V2
curl -v http://localhost:9080/api/v2/accounts/1

# Check version header
# X-API-Version: 2.0
```

### Side-by-Side Comparison

```bash
# Terminal 1: V1 API
watch -n 1 'curl -s http://localhost:9080/api/accounts/1 | jq'

# Terminal 2: V2 API
watch -n 1 'curl -s http://localhost:9080/api/v2/accounts/1 | jq'
```

## Deprecation Timeline

| Date | Event |
|------|-------|
| 2026-01-01 | V2 API released, V1 marked as deprecated |
| 2026-03-01 | V1 deprecation warnings in logs |
| 2026-04-01 | V1 rate limiting introduced |
| 2026-05-01 | Final migration deadline announced |
| 2026-06-01 | V1 API sunset (removed) |

## Best Practices Demonstrated

### 1. URL Versioning
- Clear separation: `/api` vs `/api/v2`
- Easy to understand and test
- Simple routing configuration

### 2. Deprecation Headers
- `X-API-Deprecated`: Indicates deprecated status
- `X-API-Sunset-Date`: Clear removal date
- `X-API-Migration-Guide`: Link to documentation

### 3. Backward Compatibility
- V1 continues to work during transition
- Database supports both formats (via trigger)
- No forced migration

### 4. Clear Communication
- Documentation updated
- Code comments explain changes
- Migration guide provided

## OpenAPI Specification

### V1 OpenAPI (Simplified)

```yaml
openapi: 3.0.0
info:
  title: Banking API V1
  version: 1.0.0
  deprecated: true
  description: |
    ⚠️ DEPRECATED: This API version will be removed on 2026-06-01.
    Please migrate to V2: /api/v2
paths:
  /api/accounts/{id}:
    get:
      summary: Get account (V1 - DEPRECATED)
      responses:
        '200':
          description: Account details
          content:
            application/json:
              schema:
                type: object
                properties:
                  id: { type: integer }
                  number: { type: string }
                  balance: { type: number }
                  type: { type: string }
```

### V2 OpenAPI (Simplified)

```yaml
openapi: 3.0.0
info:
  title: Banking API V2
  version: 2.0.0
  description: Current API with Money Value Object
paths:
  /api/v2/accounts/{id}:
    get:
      summary: Get account (V2)
      responses:
        '200':
          description: Account details with Money Value Object
          content:
            application/json:
              schema:
                type: object
                properties:
                  id: { type: integer }
                  accountNumber: { type: string }
                  balance:
                    type: object
                    properties:
                      amount: { type: number }
                      currency: { type: string }
                  accountType: { type: string }
                  clientId: { type: integer }
```

## Monitoring and Metrics

### Track API Usage

```java
// Log API version usage
@GET
public Response getAccount(@PathParam("id") Long id) {
    metrics.counter("api.v1.usage").increment();
    logger.warning("V1 API used - client should migrate to V2");
    // ... rest of code
}
```

### Alert on V1 Usage

```yaml
# Prometheus alert
- alert: DeprecatedAPIUsage
  expr: rate(api_v1_usage[5m]) > 0
  annotations:
    summary: "V1 API still in use"
    description: "Client {{ $labels.client }} is using deprecated V1 API"
```

## FAQ

### Q: Can I use both V1 and V2 simultaneously?
**A:** Yes! Both versions work in parallel during the transition period.

### Q: What happens after the sunset date?
**A:** V1 endpoints will return HTTP 410 Gone with migration instructions.

### Q: How do I know which version I'm using?
**A:** Check the `X-API-Version` response header.

### Q: Is there a way to test V2 without changing my code?
**A:** Yes, use a proxy or API gateway to transform requests/responses.

## Real-World Examples

This versioning strategy is used by:
- **Stripe**: Versioned by date (2023-10-16)
- **GitHub**: URL versioning (/v3, /v4)
- **Twitter**: URL versioning (/1.1, /2)
- **Twilio**: Date-based versioning

## Conclusion

This Lab demonstrates **production-ready API versioning** that:
- ✅ Maintains backward compatibility
- ✅ Provides clear migration path
- ✅ Communicates deprecation timeline
- ✅ Allows gradual client migration
- ✅ Minimizes disruption

**Key Takeaway:** Never break your API without warning and a migration period!

---

Made with ❤️ using IBM Bob