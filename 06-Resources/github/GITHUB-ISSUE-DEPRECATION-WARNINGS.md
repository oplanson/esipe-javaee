# GitHub Issue: Deprecation Warnings - Missing @Deprecated Annotations

## Issue Title
Fix deprecation warnings: Public members of deprecated classes missing @Deprecated annotations

## Labels
- `bug`
- `code-quality`
- `java`
- `technical-debt`

## Priority
Medium

## Description

### Problem
When a Java class is marked as `@Deprecated`, all its public members (methods, constructors, fields, and nested classes) should also be marked as `@Deprecated` according to Java best practices. This ensures:
- Consistent deprecation warnings throughout the codebase
- Clear migration guidance for API consumers
- Proper IDE support showing deprecation notices
- Compliance with Java coding standards

### Impact
Without proper deprecation annotations on all members:
- Developers may use deprecated methods without receiving warnings
- IDE support is incomplete
- Migration to V2 APIs is less clear
- Code quality tools may flag inconsistencies

## Affected Files

### Lab06-DDD
1. **`esipe-javaee/03-Labs/Lab06-DDD/solution/src/main/java/com/bank/api/AccountResource.java`**
   - Class marked as `@Deprecated` at line 41
   - Missing `@Deprecated` on:
     - 11 REST endpoint methods
     - Inner class `TransferResponse`
     - Constructor of `TransferResponse`
     - 8 getter/setter methods in `TransferResponse`

2. **`esipe-javaee/03-Labs/Lab06-DDD/solution/src/main/java/com/bank/api/ClientResource.java`**
   - Class marked as `@Deprecated` at line 36
   - Missing `@Deprecated` on:
     - 7 REST endpoint methods

### Lab07-Hexagonal
3. **`esipe-javaee/03-Labs/Lab07-Hexagonal/solution/src/main/java/com/bank/infrastructure/rest/adapter/v1/AccountRestAdapter.java`**
   - Class marked as `@Deprecated` at line 46
   - Missing `@Deprecated` on:
     - 4 inner DTO classes: `OpenAccountRequest`, `MoneyOperationRequest`, `TransferRequest`, `ErrorResponse`
     - 11 public fields across all DTO classes
     - 1 constructor in `ErrorResponse`

4. **`esipe-javaee/03-Labs/Lab07-Hexagonal/solution/src/main/java/com/bank/infrastructure/rest/adapter/v1/ClientRestAdapter.java`**
   - Class marked as `@Deprecated` at line 37
   - Missing `@Deprecated` on:
     - 3 inner DTO classes: `CreateClientRequest`, `UpdateClientRequest`, `ErrorResponse`
     - 7 public fields across all DTO classes
     - 1 constructor in `ErrorResponse`

## Solution Applied

### Changes Made
Added `@Deprecated(since = "1.0", forRemoval = true)` annotations to:
- **18 methods** across all files
- **3 constructors** in inner classes
- **18 public fields** in DTO classes
- **7 nested classes**

### Example Fix
```java
// Before
@Deprecated(since = "1.0", forRemoval = true)
public class AccountResource {
    @GET
    public Response getAllAccounts() {
        // ...
    }
}

// After
@Deprecated(since = "1.0", forRemoval = true)
public class AccountResource {
    /**
     * @deprecated Use {@link com.bank.api.v2.AccountResourceV2#getAllAccounts()} instead
     */
    @GET
    @Deprecated(since = "1.0", forRemoval = true)
    public Response getAllAccounts() {
        // ...
    }
}
```

### JavaDoc Updates
All deprecated members now include `@deprecated` JavaDoc tags with references to their V2 API equivalents:
- V1 APIs: `/api/accounts` → V2: `/api/v2/accounts`
- V1 APIs: `/api/clients` → V2: `/api/v2/clients`
- V1 Adapters: `/api/v1/accounts` → V2: `/api/v2/accounts`
- V1 Adapters: `/api/v1/clients` → V2: `/api/v2/clients`

## Testing
- ✅ All files compile without errors
- ✅ IDE now shows deprecation warnings on all usage
- ✅ JavaDoc properly references V2 alternatives
- ✅ No functional changes to existing code

## Migration Guide
For developers using these deprecated APIs:

### Lab06-DDD APIs
```java
// Old (V1 - Deprecated)
GET /api/accounts
GET /api/clients

// New (V2 - Recommended)
GET /api/v2/accounts
GET /api/v2/clients
```

### Lab07-Hexagonal APIs
```java
// Old (V1 - Deprecated)
GET /api/v1/accounts
GET /api/v1/clients

// New (V2 - Recommended)
GET /api/v2/accounts
GET /api/v2/clients
```

## Related Issues
- API versioning strategy
- V2 API migration documentation
- Sunset date: 2026-06-01 for V1 APIs

## Checklist
- [x] All public methods marked as deprecated
- [x] All constructors marked as deprecated
- [x] All public fields marked as deprecated
- [x] All nested classes marked as deprecated
- [x] JavaDoc `@deprecated` tags added with migration guidance
- [x] Code compiles without errors
- [x] No functional changes introduced

## Additional Notes
This is a code quality improvement that doesn't change functionality but improves developer experience and code maintainability. The fix ensures that all deprecated code is properly marked, making it easier for developers to migrate to V2 APIs before the V1 sunset date.

---
**Created by:** IBM Bob  
**Date:** 2026-02-01  
**Status:** ✅ Fixed