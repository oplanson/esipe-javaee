# JPA Bidirectional Relationships - Best Practices

## 🎯 Overview

This document explains best practices for managing bidirectional relationships in JPA, particularly when deleting entities.

---

## 📋 The Problem

When working with bidirectional relationships in JPA, improper handling during deletion can cause constraint violations.

### ❌ Common Mistake

```java
// INCORRECT - Causes constraint violation
@Transactional
public boolean delete(Long accountId) {
    Account account = em.find(Account.class, accountId);
    
    // This sets client_id to NULL in the database
    Client client = account.getClient();
    client.removeAccount(account);  // ← Triggers UPDATE
    
    // Then tries to DELETE, but UPDATE already violated NOT NULL constraint
    em.remove(account);
    
    return true;
}
```

**What happens:**
1. `client.removeAccount(account)` modifies the relationship
2. JPA marks the account as dirty
3. JPA flushes the UPDATE: `UPDATE accounts SET client_id = NULL WHERE id = ?`
4. Database rejects UPDATE due to NOT NULL constraint on `client_id`
5. Transaction rolls back before DELETE can execute

**Error:**
```
ERROR: null value in column "client_id" of relation "accounts" violates not-null constraint
```

---

## ✅ Correct Approaches

### Approach 1: Delete First, Clean Up After (Recommended)

```java
@Transactional
public boolean delete(Long accountId) {
    Account account = em.find(Account.class, accountId);
    if (account == null) return false;
    
    // Get client reference for in-memory cleanup
    Client client = account.getClient();
    
    // DELETE from database first
    em.remove(account);
    
    // Clean up in-memory collection (optional, for consistency)
    if (client != null) {
        client.getAccounts().remove(account);
    }
    
    return true;
}
```

**Why it works:**
- `em.remove()` issues DELETE statement directly
- No UPDATE is triggered
- In-memory cleanup happens after database operation
- Collection manipulation doesn't trigger database changes

### Approach 2: Let Cascade Handle It

```java
@Transactional
public boolean deleteClient(Long clientId) {
    Client client = em.find(Client.class, clientId);
    if (client == null) return false;
    
    // Simply remove the client
    // Cascade will automatically delete all accounts
    em.remove(client);
    
    return true;
}
```

**Configuration required:**
```java
@Entity
public class Client {
    @OneToMany(
        mappedBy = "client",
        cascade = CascadeType.ALL,      // ← Propagates DELETE
        orphanRemoval = true,            // ← Removes orphans
        fetch = FetchType.LAZY
    )
    private List<Account> accounts;
}
```

**Why it works:**
- JPA handles the cascade automatically
- Accounts are deleted before the client
- No manual relationship management needed

---

## 🔍 Understanding JPA Lifecycle

### Entity States

```
┌─────────────┐
│   NEW       │  Created with 'new', not yet persisted
└──────┬──────┘
       │ em.persist()
       ↓
┌─────────────┐
│  MANAGED    │  Tracked by EntityManager, changes auto-synced
└──────┬──────┘
       │ em.remove()
       ↓
┌─────────────┐
│  REMOVED    │  Marked for deletion, will be deleted on flush/commit
└──────┬──────┘
       │ flush/commit
       ↓
┌─────────────┐
│  DETACHED   │  No longer tracked, changes not synced
└─────────────┘
```

### Flush Behavior

JPA automatically flushes changes to the database:
1. Before executing queries (to ensure query sees latest data)
2. Before transaction commit
3. When explicitly calling `em.flush()`

**Key Point:** Modifying a managed entity's relationships triggers a flush, which can cause constraint violations if not handled properly.

---

## 📝 Best Practices Summary

### ✅ DO

1. **Delete entities before modifying relationships**
   ```java
   em.remove(account);  // Delete first
   client.getAccounts().remove(account);  // Clean up after
   ```

2. **Use cascade for parent-child relationships**
   ```java
   @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
   ```

3. **Keep in-memory state consistent**
   ```java
   // After database operation, update collections
   client.getAccounts().remove(account);
   ```

4. **Use helper methods for bidirectional relationships**
   ```java
   public void addAccount(Account account) {
       accounts.add(account);
       account.setClient(this);
   }
   
   public void removeAccount(Account account) {
       accounts.remove(account);
       account.setClient(null);
   }
   ```

### ❌ DON'T

1. **Don't modify relationships before deletion**
   ```java
   // BAD: Triggers UPDATE before DELETE
   client.removeAccount(account);
   em.remove(account);
   ```

2. **Don't forget cascade configuration**
   ```java
   // BAD: No cascade, orphans remain
   @OneToMany(mappedBy = "client")
   ```

3. **Don't mix manual and cascade deletion**
   ```java
   // BAD: Redundant and error-prone
   for (Account account : client.getAccounts()) {
       em.remove(account);  // Unnecessary with cascade
   }
   em.remove(client);
   ```

---

## 🧪 Testing Deletion

### Test Scenarios

#### 1. Delete Account (Child Entity)
```java
@Test
public void testDeleteAccount() {
    // Given: Client with account
    Client client = createClient();
    Account account = createAccount(client);
    
    // When: Delete account
    accountService.delete(account.getId());
    
    // Then: Account deleted, client remains
    assertNull(accountService.findById(account.getId()));
    assertNotNull(clientService.findById(client.getId()));
}
```

#### 2. Delete Client (Parent Entity with Cascade)
```java
@Test
public void testDeleteClient() {
    // Given: Client with multiple accounts
    Client client = createClient();
    Account account1 = createAccount(client);
    Account account2 = createAccount(client);
    
    // When: Delete client
    clientService.delete(client.getId());
    
    // Then: Client and all accounts deleted
    assertNull(clientService.findById(client.getId()));
    assertNull(accountService.findById(account1.getId()));
    assertNull(accountService.findById(account2.getId()));
}
```

#### 3. Orphan Removal
```java
@Test
public void testOrphanRemoval() {
    // Given: Client with account
    Client client = createClient();
    Account account = createAccount(client);
    
    // When: Remove account from collection
    client.getAccounts().remove(account);
    clientService.update(client);
    
    // Then: Account automatically deleted (orphanRemoval = true)
    assertNull(accountService.findById(account.getId()));
}
```

---

## 🔧 Debugging Tips

### Enable SQL Logging

In `persistence.xml`:
```xml
<property name="eclipselink.logging.level.sql" value="FINE"/>
<property name="eclipselink.logging.parameters" value="true"/>
```

### Watch for These Patterns

**Good (DELETE only):**
```sql
DELETE FROM accounts WHERE id = ?
```

**Bad (UPDATE then DELETE):**
```sql
UPDATE accounts SET client_id = NULL WHERE id = ?  -- ❌ Constraint violation
DELETE FROM accounts WHERE id = ?                   -- Never executed
```

### Common Error Messages

1. **NOT NULL constraint violation**
   ```
   ERROR: null value in column "client_id" violates not-null constraint
   ```
   **Cause:** Relationship modified before deletion
   **Fix:** Delete entity first, then clean up collections

2. **Foreign key constraint violation**
   ```
   ERROR: update or delete on table "clients" violates foreign key constraint
   ```
   **Cause:** Missing cascade configuration
   **Fix:** Add `cascade = CascadeType.ALL` to `@OneToMany`

---

## 📚 References

- [Jakarta Persistence Specification](https://jakarta.ee/specifications/persistence/)
- [EclipseLink Documentation](https://www.eclipse.org/eclipselink/documentation/)
- [JPA Best Practices](https://thorben-janssen.com/jpa-best-practices/)

---

**Lab 04 - CDI and Dependency Injection**  
*Made with Bob* 🚀