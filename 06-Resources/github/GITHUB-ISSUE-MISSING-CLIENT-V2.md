# Enhancement: Complete API V2 with ClientResourceV2

## 📋 Issue Type
**Enhancement Request** - Missing API V2 Resource

## 🎯 Priority
**Medium** - Feature gap in API versioning implementation

## 📝 Description

The Lab06-DDD solution implements API versioning with a V2 endpoint, but only `AccountResourceV2` exists. The `ClientResourceV2` is missing, causing test failures and incomplete API versioning demonstration.

### Current State
- ✅ `/api/v2/accounts` - **EXISTS** (AccountResourceV2)
- ❌ `/api/v2/clients` - **MISSING** (ClientResourceV2)
- ✅ `/api/clients` - EXISTS (ClientResource V1 - deprecated)
- ✅ Test exists in `podman-test.sh` (lines 554-557) but fails

### Evidence
1. **RestApplicationV2.java** (line 56) has commented code:
   ```java
   // Future: Add more V2 resources here as they are created
   // classes.add(ClientResourceV2.class);
   ```

2. **podman-test.sh** (lines 554-557) tests V2 endpoint:
   ```bash
   if curl -f -s "http://localhost:${APP_PORT}/api/v2/clients" > /dev/null 2>&1; then
       run_test "API v2 available" \
           "curl -f -s http://localhost:${APP_PORT}/api/v2/clients > /dev/null"
   fi
   ```

3. **ClientResource.java** is marked as deprecated:
   ```java
   @Deprecated(since = "1.0", forRemoval = true)
   ```

## 🔧 Proposed Solution

### 1. Create ClientResourceV2.java
**Location:** `src/main/java/com/bank/api/v2/ClientResourceV2.java`

**Key Features:**
- Use `ClientDTO` with Value Objects (Email)
- Implement same endpoints as V1 with improved format
- Better error handling
- Clear domain/API separation

**Endpoints to implement:**
```
GET    /api/v2/clients          - List all clients
GET    /api/v2/clients/{id}     - Get client by ID
POST   /api/v2/clients          - Create new client
PUT    /api/v2/clients/{id}     - Update client
DELETE /api/v2/clients/{id}     - Delete client
```

### 2. Create ClientDTO.java
**Location:** `src/main/java/com/bank/application/dto/ClientDTO.java`

**Structure:**
```java
public class ClientDTO {
    private Long id;
    private String name;
    private EmailDTO email;  // Value Object wrapper
    private boolean premium;
    // ... getters/setters
}

public class EmailDTO {
    private String value;
    // ... validation, getters/setters
}
```

### 3. Update RestApplicationV2.java
**Change line 56:**
```java
// FROM (commented):
// classes.add(ClientResourceV2.class);

// TO (active):
classes.add(ClientResourceV2.class);
```

### 4. No Test Changes Needed
The test in `podman-test.sh` is already correct and will pass once the resource is implemented.

## 📊 API Format Comparison

### V1 Format (Current - Deprecated)
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "premium": false
}
```

### V2 Format (Proposed)
```json
{
  "id": 1,
  "name": "John Doe",
  "email": {
    "value": "john@example.com"
  },
  "premium": false
}
```

**Benefits of V2:**
- Email as Value Object (domain-driven design)
- Better validation encapsulation
- Consistent with AccountResourceV2 pattern
- Easier to extend (e.g., add email verification status)

## 🎓 Educational Value

This enhancement demonstrates:
1. **API Versioning Strategy** - URL-based versioning
2. **Domain-Driven Design** - Value Objects in DTOs
3. **Backward Compatibility** - V1 remains available (deprecated)
4. **Migration Path** - Clear upgrade path for API consumers
5. **Consistency** - All V2 resources follow same pattern

## 📁 Files to Create/Modify

### New Files (3)
1. `src/main/java/com/bank/api/v2/ClientResourceV2.java` (~200 lines)
2. `src/main/java/com/bank/application/dto/ClientDTO.java` (~80 lines)
3. `src/main/java/com/bank/application/dto/EmailDTO.java` (~40 lines)

### Modified Files (1)
1. `src/main/java/com/bank/api/v2/RestApplicationV2.java` (1 line change)

### Reference Files
- Use `AccountResourceV2.java` as template
- Use `AccountDTO.java` as DTO pattern reference
- Follow same structure and conventions

## ✅ Acceptance Criteria

- [ ] ClientResourceV2 class created with all CRUD endpoints
- [ ] ClientDTO and EmailDTO classes created
- [ ] RestApplicationV2 registers ClientResourceV2
- [ ] All V2 endpoints return proper JSON with Value Objects
- [ ] Test in podman-test.sh passes (lines 554-557)
- [ ] V1 ClientResource remains functional (backward compatibility)
- [ ] Documentation updated in README.md
- [ ] Code follows same patterns as AccountResourceV2

## 🔗 Related Files

- `esipe-javaee/03-Labs/Lab06-DDD/solution/src/main/java/com/bank/api/v2/AccountResourceV2.java`
- `esipe-javaee/03-Labs/Lab06-DDD/solution/src/main/java/com/bank/api/v2/RestApplicationV2.java`
- `esipe-javaee/03-Labs/Lab06-DDD/solution/src/main/java/com/bank/api/ClientResource.java`
- `esipe-javaee/03-Labs/Lab06-DDD/solution/src/main/java/com/bank/application/dto/AccountDTO.java`
- `esipe-javaee/03-Labs/Lab06-DDD/podman-test.sh`

## 🏷️ Labels
- `enhancement`
- `api-versioning`
- `domain-driven-design`
- `lab06-ddd`
- `good-first-issue` (for students)

## 👥 Assignee
TBD - Could be a good learning exercise for students

## 📅 Milestone
Lab06-DDD Completion

---

**Created:** 2026-02-01  
**Lab:** Lab06-DDD  
**Component:** REST API V2  
**Severity:** Medium (feature gap, not a bug)

<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->