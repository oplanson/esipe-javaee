---
marp: true
theme: default
paginate: true
backgroundColor: #fff
header: 'Jakarta EE & MicroProfile Course'
footer: 'Lecture 3: JPA and Database Integration | © 2025'
style: |
  section {
    font-size: 22px;
    padding: 40px 60px;
  }
  img {
    max-width: 85%;
    max-height: 380px;
    display: block;
    margin: 10px auto;
  }
  pre {
    font-size: 0.65em;
    margin: 10px 0;
    padding: 10px;
  }
  code {
    font-size: 0.85em;
  }
  ul, ol {
    font-size: 0.85em;
    line-height: 1.8;
    margin: 8px 0;
  }
  li {
    margin: 6px 0;
    line-height: 1.8;
  }
  li::marker {
    flex-shrink: 0;
  }
  h1 {
    font-size: 1.8em;
    margin-bottom: 20px;
    line-height: 1.3;
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: nowrap;
    white-space: nowrap;
  }
  h2 {
    font-size: 1.3em;
    margin: 15px 0 10px 0;
    line-height: 1.3;
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: nowrap;
    white-space: nowrap;
  }
  h3 {
    font-size: 1.1em;
    margin: 10px 0 8px 0;
    line-height: 1.3;
    display: flex;
    align-items: center;
    gap: 6px;
    flex-wrap: nowrap;
    white-space: nowrap;
  }
  table {
    font-size: 0.8em;
  }
  td {
    vertical-align: middle;
  }
  th {
    white-space: nowrap;
  }
  p {
    margin: 8px 0;
    line-height: 1.6;
  }
  strong {
    white-space: nowrap;
  }
  blockquote {
    font-size: 0.9em;
    margin: 10px 0;
    padding: 10px 15px;
  }
  .columns {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    align-items: start;
  }
  .columns-3 {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: 15px;
    align-items: start;
  }
---

# 🗄️ Lecture 3: JPA and Database Integration

**Jakarta Persistence API (JPA)**

---

## 📋 Lecture Objectives

By the end of this lecture, you will be able to:

- Understand Object-Relational Mapping (ORM) concepts
- Create and configure JPA entities with annotations
- Define entity relationships (One-to-One, One-to-Many, Many-to-Many)
- Write queries using JPQL and Criteria API
- Manage transactions effectively
- Handle database migrations and schema evolution

---

## 📚 Topics Covered

1. **Introduction to JPA and ORM**
2. **JPA Entities and Annotations**
3. **Entity Relationships**
4. **JPQL (Java Persistence Query Language)**
5. **Criteria API**
6. **Transaction Management**
7. **Database Migrations**

---

# Part 1: Introduction to JPA and ORM

---

## What is ORM?

**Object-Relational Mapping (ORM)** bridges the gap between object-oriented programming and relational databases.

<div class="columns">
<div>

**Without ORM:**
```java
// Manual JDBC code
String sql = "SELECT * FROM client WHERE id = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setLong(1, clientId);
ResultSet rs = ps.executeQuery();
if (rs.next()) {
    Client client = new Client();
    client.setId(rs.getLong("id"));
    client.setName(rs.getString("name"));
    client.setEmail(rs.getString("email"));
}
```

</div>
<div>

**With ORM (JPA):**
```java
// Simple JPA code
Client client = entityManager.find(
    Client.class, 
    clientId
);
```

</div>
</div>

---

## Why Use JPA?

**Benefits:**
- ✅ **Productivity:** Less boilerplate code
- ✅ **Maintainability:** Object-oriented approach
- ✅ **Database Independence:** Switch databases easily
- ✅ **Caching:** Built-in first and second-level caching
- ✅ **Lazy Loading:** Load data only when needed
- ✅ **Transaction Management:** Automatic transaction handling

**Challenges:**
- ⚠️ Learning curve for complex mappings
- ⚠️ Performance tuning required for large datasets
- ⚠️ N+1 query problem if not careful

---

## JPA Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Application Layer                   │
│              (Business Logic & Services)             │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│              JPA API (jakarta.persistence)           │
│  EntityManager │ EntityManagerFactory │ Query        │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│           JPA Provider (Hibernate, EclipseLink)      │
│              (Implementation of JPA Spec)            │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                  JDBC Driver                         │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│              Database (PostgreSQL, MySQL, etc.)      │
└─────────────────────────────────────────────────────┘
```

---

## Key JPA Concepts

| Concept | Description |
|---------|-------------|
| **Entity** | Java class mapped to database table |
| **EntityManager** | Interface to interact with persistence context |
| **Persistence Context** | Set of managed entity instances |
| **Persistence Unit** | Configuration defining database connection |
| **Transaction** | Unit of work that must be atomic |
| **JPQL** | Object-oriented query language |

---

# Part 2: JPA Entities and Annotations

---

## Creating a Basic Entity

```java
package com.bank.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;
    
    // Constructors, getters, setters
}
```

---

## Essential JPA Annotations

<div class="columns">
<div>

**Class-Level:**
- `@Entity` - Marks class as JPA entity
- `@Table` - Specifies table details
- `@NamedQuery` - Defines named queries

**Field-Level:**
- `@Id` - Primary key
- `@GeneratedValue` - Auto-generation strategy
- `@Column` - Column mapping
- `@Temporal` - Date/time mapping
- `@Enumerated` - Enum mapping

</div>
<div>

**Relationship:**
- `@OneToOne` - One-to-one relationship
- `@OneToMany` - One-to-many relationship
- `@ManyToOne` - Many-to-one relationship
- `@ManyToMany` - Many-to-many relationship
- `@JoinColumn` - Foreign key column
- `@JoinTable` - Join table for M:N

</div>
</div>

---

## ID Generation Strategies

```java
// IDENTITY - Database auto-increment
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

// SEQUENCE - Database sequence
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
@SequenceGenerator(name = "client_seq", sequenceName = "client_sequence", 
                   allocationSize = 1)
private Long id;

// TABLE - Separate table for ID generation
@Id
@GeneratedValue(strategy = GenerationType.TABLE, generator = "client_gen")
@TableGenerator(name = "client_gen", table = "id_generator", 
                pkColumnName = "gen_name", valueColumnName = "gen_value")
private Long id;

// UUID - Universally Unique Identifier
@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
```

---

## Complete Entity Example

```java
@Entity
@Table(name = "clients", 
       indexes = {@Index(name = "idx_email", columnList = "email")})
@NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c")
@NamedQuery(name = "Client.findByEmail", 
            query = "SELECT c FROM Client c WHERE c.email = :email")
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
```

---

## Entity Lifecycle Callbacks

```java
@Entity
public class Client {
    
    @PrePersist
    protected void onCreate() {
        // Called before entity is persisted
        this.createdAt = new Date();
    }
    
    @PostPersist
    protected void afterCreate() {
        // Called after entity is persisted
        System.out.println("Client created: " + this.id);
    }
    
    @PreUpdate
    protected void onUpdate() {
        // Called before entity is updated
        this.updatedAt = new Date();
    }
    
    @PostUpdate
    protected void afterUpdate() {
        // Called after entity is updated
    }
    
    @PreRemove
    protected void onDelete() {
        // Called before entity is removed
    }
    
    @PostLoad
    protected void afterLoad() {
        // Called after entity is loaded from database
    }
}
```

---

# Part 3: Entity Relationships

---

## One-to-Many Relationship

**Client has many Accounts**

```java
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, 
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();
    
    // Helper methods
    public void addAccount(Account account) {
        accounts.add(account);
        account.setClient(this);
    }
    
    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setClient(null);
    }
}
```

---

## Many-to-One Relationship

**Account belongs to one Client**

```java
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String number;
    
    @Column(nullable = false)
    private Double balance;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    // Getters and setters
}
```

---

## Cascade Types

```java
public enum CascadeType {
    ALL,        // All operations cascade
    PERSIST,    // Persist operations cascade
    MERGE,      // Merge operations cascade
    REMOVE,     // Remove operations cascade
    REFRESH,    // Refresh operations cascade
    DETACH      // Detach operations cascade
}
```

**Example:**
```java
@OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
private List<Account> accounts;

// When you persist a client, all accounts are also persisted
Client client = new Client("John Doe", "john@example.com");
client.addAccount(new Account("ACC001", 1000.0, AccountType.CHECKING));
entityManager.persist(client); // Account is also persisted
```

---

## Fetch Types

<div class="columns">
<div>

**LAZY (Default for collections):**
```java
@OneToMany(fetch = FetchType.LAZY)
private List<Account> accounts;

// Accounts loaded only when accessed
Client client = em.find(Client.class, 1L);
// No query for accounts yet

List<Account> accounts = client.getAccounts();
// NOW accounts are loaded
```

</div>
<div>

**EAGER (Default for single entities):**
```java
@ManyToOne(fetch = FetchType.EAGER)
private Client client;

// Client loaded immediately
Account account = em.find(Account.class, 1L);
// Client is already loaded
String name = account.getClient().getName();
// No additional query
```

</div>
</div>

**Best Practice:** Use LAZY by default, EAGER only when necessary

---

## Many-to-Many Relationship

**Example: Client and Advisor relationship**

```java
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToMany
    @JoinTable(
        name = "client_advisor",
        joinColumns = @JoinColumn(name = "client_id"),
        inverseJoinColumns = @JoinColumn(name = "advisor_id")
    )
    private Set<Advisor> advisors = new HashSet<>();
}

@Entity
public class Advisor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToMany(mappedBy = "advisors")
    private Set<Client> clients = new HashSet<>();
}
```

---

## Bidirectional Relationship Best Practices

```java
@Entity
public class Client {
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, 
               orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();
    
    // Synchronization methods
    public void addAccount(Account account) {
        accounts.add(account);
        account.setClient(this);  // Maintain both sides
    }
    
    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setClient(null);  // Maintain both sides
    }
}

@Entity
public class Account {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;
    
    // Don't provide setClient() publicly
    void setClient(Client client) {
        this.client = client;
    }
}
```

---

# Part 4: JPQL (Java Persistence Query Language)

---

## What is JPQL?

**JPQL** is an object-oriented query language similar to SQL but operates on entities rather than tables.

<div class="columns">
<div>

**SQL (Table-based):**
```sql
SELECT c.id, c.name, c.email
FROM clients c
WHERE c.email LIKE '%@example.com'
ORDER BY c.name
```

</div>
<div>

**JPQL (Entity-based):**
```java
SELECT c FROM Client c
WHERE c.email LIKE '%@example.com'
ORDER BY c.name
```

</div>
</div>

**Key Differences:**
- JPQL uses entity names, not table names
- JPQL uses entity properties, not column names
- JPQL is database-independent

---

## Basic JPQL Queries

```java
// Find all clients
String jpql = "SELECT c FROM Client c";
List<Client> clients = entityManager
    .createQuery(jpql, Client.class)
    .getResultList();

// Find client by ID
String jpql = "SELECT c FROM Client c WHERE c.id = :id";
Client client = entityManager
    .createQuery(jpql, Client.class)
    .setParameter("id", 1L)
    .getSingleResult();

// Find clients by email pattern
String jpql = "SELECT c FROM Client c WHERE c.email LIKE :pattern";
List<Client> clients = entityManager
    .createQuery(jpql, Client.class)
    .setParameter("pattern", "%@example.com")
    .getResultList();

// Count clients
String jpql = "SELECT COUNT(c) FROM Client c";
Long count = entityManager
    .createQuery(jpql, Long.class)
    .getSingleResult();
```

---

## JPQL with Joins

```java
// Fetch clients with their accounts (JOIN FETCH to avoid N+1)
String jpql = "SELECT c FROM Client c LEFT JOIN FETCH c.accounts WHERE c.id = :id";
Client client = entityManager
    .createQuery(jpql, Client.class)
    .setParameter("id", 1L)
    .getSingleResult();

// Find clients with accounts having balance > 1000
String jpql = """
    SELECT DISTINCT c FROM Client c
    JOIN c.accounts a
    WHERE a.balance > :minBalance
    """;
List<Client> clients = entityManager
    .createQuery(jpql, Client.class)
    .setParameter("minBalance", 1000.0)
    .getResultList();

// Find accounts with client information
String jpql = "SELECT a FROM Account a JOIN FETCH a.client WHERE a.type = :type";
List<Account> accounts = entityManager
    .createQuery(jpql, Account.class)
    .setParameter("type", AccountType.CHECKING)
    .getResultList();
```

---

## JPQL Aggregate Functions

```java
// Total balance across all accounts
String jpql = "SELECT SUM(a.balance) FROM Account a";
Double totalBalance = entityManager
    .createQuery(jpql, Double.class)
    .getSingleResult();

// Average balance by account type
String jpql = """
    SELECT a.type, AVG(a.balance), COUNT(a)
    FROM Account a
    GROUP BY a.type
    """;
List<Object[]> results = entityManager
    .createQuery(jpql, Object[].class)
    .getResultList();

for (Object[] row : results) {
    AccountType type = (AccountType) row[0];
    Double avgBalance = (Double) row[1];
    Long count = (Long) row[2];
    System.out.println(type + ": " + avgBalance + " (" + count + " accounts)");
}

// Find clients with more than 2 accounts
String jpql = """
    SELECT c FROM Client c
    WHERE SIZE(c.accounts) > :minAccounts
    """;
```

---

## Named Queries

**Define at entity level:**
```java
@Entity
@NamedQuery(
    name = "Client.findAll",
    query = "SELECT c FROM Client c ORDER BY c.name"
)
@NamedQuery(
    name = "Client.findByEmail",
    query = "SELECT c FROM Client c WHERE c.email = :email"
)
@NamedQuery(
    name = "Client.findWithAccounts",
    query = "SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.accounts"
)
public class Client {
    // Entity fields
}
```

**Use in code:**
```java
// Using named query
List<Client> clients = entityManager
    .createNamedQuery("Client.findAll", Client.class)
    .getResultList();

Client client = entityManager
    .createNamedQuery("Client.findByEmail", Client.class)
    .setParameter("email", "john@example.com")
    .getSingleResult();
```

---

## JPQL Update and Delete

```java
// Update query
String jpql = "UPDATE Account a SET a.balance = a.balance * 1.05 WHERE a.type = :type";
int updatedCount = entityManager
    .createQuery(jpql)
    .setParameter("type", AccountType.SAVINGS)
    .executeUpdate();

// Delete query
String jpql = "DELETE FROM Account a WHERE a.balance = 0";
int deletedCount = entityManager
    .createQuery(jpql)
    .executeUpdate();

// Bulk operations require transaction
entityManager.getTransaction().begin();
try {
    int count = entityManager.createQuery(jpql).executeUpdate();
    entityManager.getTransaction().commit();
    System.out.println("Updated " + count + " records");
} catch (Exception e) {
    entityManager.getTransaction().rollback();
    throw e;
}
```

---

# Part 5: Criteria API

---

## What is Criteria API?

**Criteria API** provides a type-safe, programmatic way to build queries.

**Benefits:**
- ✅ Type-safe (compile-time checking)
- ✅ Dynamic query building
- ✅ IDE auto-completion support
- ✅ Refactoring-friendly

**When to use:**
- Dynamic search forms
- Complex conditional queries
- When type safety is critical

---

## Basic Criteria Query

```java
// Get CriteriaBuilder
CriteriaBuilder cb = entityManager.getCriteriaBuilder();

// Create query
CriteriaQuery<Client> cq = cb.createQuery(Client.class);

// Define root (FROM clause)
Root<Client> client = cq.from(Client.class);

// Build query (SELECT clause)
cq.select(client);

// Execute query
List<Client> clients = entityManager
    .createQuery(cq)
    .getResultList();
```

**Equivalent JPQL:** `SELECT c FROM Client c`

---

## Criteria Query with Conditions

```java
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<Client> cq = cb.createQuery(Client.class);
Root<Client> client = cq.from(Client.class);

// WHERE clause
Predicate emailCondition = cb.like(client.get("email"), "%@example.com");
cq.select(client).where(emailCondition);

// ORDER BY clause
cq.orderBy(cb.asc(client.get("name")));

List<Client> clients = entityManager
    .createQuery(cq)
    .getResultList();
```

**Equivalent JPQL:**
```sql
SELECT c FROM Client c 
WHERE c.email LIKE '%@example.com' 
ORDER BY c.name ASC
```

---

## Dynamic Query Building

```java
public List<Client> searchClients(String name, String email, Integer minAccounts) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Client> cq = cb.createQuery(Client.class);
    Root<Client> client = cq.from(Client.class);
    
    List<Predicate> predicates = new ArrayList<>();
    
    // Add conditions dynamically
    if (name != null && !name.isEmpty()) {
        predicates.add(cb.like(cb.lower(client.get("name")), 
                               "%" + name.toLowerCase() + "%"));
    }
    
    if (email != null && !email.isEmpty()) {
        predicates.add(cb.equal(client.get("email"), email));
    }
    
    if (minAccounts != null) {
        predicates.add(cb.greaterThanOrEqualTo(
            cb.size(client.get("accounts")), minAccounts));
    }
    
    // Combine predicates with AND
    cq.select(client).where(cb.and(predicates.toArray(new Predicate[0])));
    
    return entityManager.createQuery(cq).getResultList();
}
```

---

## Criteria Query with Joins

```java
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<Client> cq = cb.createQuery(Client.class);
Root<Client> client = cq.from(Client.class);

// Join with accounts
Join<Client, Account> accounts = client.join("accounts", JoinType.LEFT);

// Fetch to avoid N+1
client.fetch("accounts", JoinType.LEFT);

// WHERE clause on joined entity
Predicate balanceCondition = cb.greaterThan(accounts.get("balance"), 1000.0);

cq.select(client)
  .distinct(true)
  .where(balanceCondition);

List<Client> clients = entityManager.createQuery(cq).getResultList();
```

**Equivalent JPQL:**
```sql
SELECT DISTINCT c FROM Client c 
LEFT JOIN FETCH c.accounts a 
WHERE a.balance > 1000.0
```

---

## Criteria Query Aggregate Functions

```java
// Count clients
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<Long> cq = cb.createQuery(Long.class);
Root<Client> client = cq.from(Client.class);

cq.select(cb.count(client));

Long count = entityManager.createQuery(cq).getSingleResult();

// Sum of all account balances
CriteriaQuery<Double> cq2 = cb.createQuery(Double.class);
Root<Account> account = cq2.from(Account.class);

cq2.select(cb.sum(account.get("balance")));

Double totalBalance = entityManager.createQuery(cq2).getSingleResult();

// Group by with aggregate
CriteriaQuery<Object[]> cq3 = cb.createQuery(Object[].class);
Root<Account> acc = cq3.from(Account.class);

cq3.multiselect(
    acc.get("type"),
    cb.avg(acc.get("balance")),
    cb.count(acc)
).groupBy(acc.get("type"));

List<Object[]> results = entityManager.createQuery(cq3).getResultList();
```

---

# Part 6: Transaction Management

---

## What is a Transaction?

**Transaction** is a unit of work that must be:
- **Atomic:** All or nothing
- **Consistent:** Database remains in valid state
- **Isolated:** Concurrent transactions don't interfere
- **Durable:** Committed changes persist

```java
// Without transaction - WRONG!
Client client = new Client("John", "john@example.com");
entityManager.persist(client);  // May not be saved!

// With transaction - CORRECT!
entityManager.getTransaction().begin();
try {
    Client client = new Client("John", "john@example.com");
    entityManager.persist(client);
    entityManager.getTransaction().commit();
} catch (Exception e) {
    entityManager.getTransaction().rollback();
    throw e;
}
```

---

## Manual Transaction Management

**In this course, we use manual transaction management with EntityManager:**

```java
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class ClientService {
    
    private static ClientService instance;
    private EntityManagerFactory emf;
    
    private ClientService() {
        emf = Persistence.createEntityManagerFactory("bankingPU");
    }
    
    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }
    
    private EntityManager createEntityManager() {
        return emf.createEntityManager();
    }
    
    public Client createClient(String name, String email) {
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Client client = new Client(name, email);
            em.persist(client);
            tx.commit();  // Explicit commit
            return client;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();  // Explicit rollback on error
            }
            throw e;
        } finally {
            em.close();  // Always close EntityManager
        }
    }
    
    public void updateClient(Long id, String name) {
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Client client = em.find(Client.class, id);
            if (client != null) {
                client.setName(name);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
```

**Note:** CDI and `@Transactional` will be covered in Course 4.

---

## Transaction Patterns

**With manual transaction management, you control transaction boundaries explicitly:**

```java
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class BankingService {
    
    private static BankingService instance;
    private EntityManagerFactory emf;
    
    private BankingService() {
        emf = Persistence.createEntityManagerFactory("bankingPU");
    }
    
    public static synchronized BankingService getInstance() {
        if (instance == null) {
            instance = new BankingService();
        }
        return instance;
    }
    
    // Standard transaction pattern
    public void transfer(Long fromId, Long toId, Double amount) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Business logic here
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    // Read-only operation (no transaction needed)
    public Client findClient(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Client.class, id);
        } finally {
            em.close();
        }
    }
    
    // Non-transactional operation
    public void sendEmail(String to, String message) {
        // No database access, no transaction needed
        // Email sending logic here
    }
}
```

**Note:** Transaction propagation with `@Transactional` will be covered in Course 4 with CDI.

---

## Rollback Strategies

**With manual transaction management, you explicitly control rollback:**

```java
public void transferMoney(Long fromId, Long toId, Double amount)
        throws InsufficientFundsException {
    
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    
    try {
        tx.begin();
        
        Account from = em.find(Account.class, fromId);
        Account to = em.find(Account.class, toId);
        
        if (from.getBalance() < amount) {
            // Throw exception - will trigger rollback in catch block
            throw new InsufficientFundsException("Insufficient funds");
        }
        
        from.withdraw(amount);
        to.deposit(amount);
        
        tx.commit();  // Explicit commit if no exception
        
    } catch (InsufficientFundsException e) {
        // Rollback on business exception
        if (tx.isActive()) {
            tx.rollback();
        }
        throw e;  // Re-throw to caller
    } catch (Exception e) {
        // Rollback on any other exception
        if (tx.isActive()) {
            tx.rollback();
        }
        throw new RuntimeException("Transfer failed", e);
    } finally {
        em.close();
    }
}

// Selective rollback handling
public void processWithValidation(Client client) throws ValidationException {
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    
    try {
        tx.begin();
        
        // Validation logic
        if (!isValid(client)) {
            throw new ValidationException("Invalid client");
        }
        
        em.persist(client);
        tx.commit();  // Commit even if validation fails later
        
    } catch (ValidationException e) {
        // Don't rollback for validation errors
        if (tx.isActive()) {
            tx.commit();  // Still commit
        }
        throw e;
    } catch (Exception e) {
        // Rollback for other errors
        if (tx.isActive()) {
            tx.rollback();
        }
        throw e;
    } finally {
        em.close();
    }
}
```

**Note:** Declarative rollback control with `@Transactional` will be covered in Course 4.

---

## EntityManager Operations

**With manual transaction management:**

```java
public class ClientService {
    
    private static ClientService instance;
    private EntityManagerFactory emf;
    
    private ClientService() {
        emf = Persistence.createEntityManagerFactory("bankingPU");
    }
    
    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }
    
    public Client create(Client client) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(client);  // Make transient entity persistent
            tx.commit();
            return client;       // Entity now has ID
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public Client findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Client.class, id);  // Find by primary key
        } finally {
            em.close();
        }
    }
    
    public Client update(Client client) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Client updated = em.merge(client);  // Merge detached entity
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Client client = em.find(Client.class, id);
            if (client != null) {
                em.remove(client);  // Remove managed entity
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void refresh(Client client) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.refresh(client);  // Reload from database
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
```

---

## Entity States

```
┌─────────────┐
│  Transient  │  New entity, not in database
└──────┬──────┘
       │ persist()
       ▼
┌─────────────┐
│  Managed    │  In persistence context, changes tracked
└──────┬──────┘
       │ commit() / flush()
       ▼
┌─────────────┐
│  Database   │  Saved to database
└─────────────┘
       │ detach() / clear() / close()
       ▼
┌─────────────┐
│  Detached   │  Was managed, now disconnected
└──────┬──────┘
       │ merge()
       ▼
┌─────────────┐
│  Managed    │  Back in persistence context
└──────┬──────┘
       │ remove()
       ▼
┌─────────────┐
│  Removed    │  Marked for deletion
└─────────────┘
```

---

# Part 7: Database Migrations

---

## Why Database Migrations?

**Challenges without migrations:**
- ❌ Manual SQL scripts error-prone
- ❌ Difficult to track schema changes
- ❌ Hard to rollback changes
- ❌ Inconsistent across environments

**Benefits with migrations:**
- ✅ Version-controlled schema changes
- ✅ Automated deployment
- ✅ Rollback capability
- ✅ Consistent across environments
- ✅ Team collaboration

---

## Migration Tools

| Tool | Description | Best For |
|------|-------------|----------|
| **Flyway** | Simple, SQL-based migrations | Teams preferring SQL |
| **Liquibase** | XML/YAML/JSON/SQL formats | Complex scenarios |
| **JPA Schema Generation** | Auto-generate from entities | Development only |

**We'll focus on Flyway** (most popular in Jakarta EE)

---

## Flyway Setup

**Add dependency to `pom.xml`:**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>9.22.0</version>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
    <version>9.22.0</version>
</dependency>
```

**Configure in `microprofile-config.properties`:**
```properties
# Database configuration
jakarta.persistence.jdbc.url=jdbc:postgresql://localhost:5432/bankdb
jakarta.persistence.jdbc.user=bankuser
jakarta.persistence.jdbc.password=bankpass

# Flyway configuration
flyway.url=${jakarta.persistence.jdbc.url}
flyway.user=${jakarta.persistence.jdbc.user}
flyway.password=${jakarta.persistence.jdbc.password}
flyway.locations=classpath:db/migration
```

---

## Migration File Structure

```
src/main/resources/
└── db/
    └── migration/
        ├── V1__create_clients_table.sql
        ├── V2__create_accounts_table.sql
        ├── V3__add_client_email_index.sql
        └── V4__add_account_type_column.sql
```

**Naming Convention:**
- `V` = Versioned migration
- `1` = Version number (sequential)
- `__` = Double underscore separator
- `create_clients_table` = Description (underscores)
- `.sql` = File extension

---

## Example Migration Files

**V1__create_clients_table.sql:**
```sql
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_clients_email ON clients(email);

COMMENT ON TABLE clients IS 'Bank clients table';
COMMENT ON COLUMN clients.email IS 'Unique email address';
```

**V2__create_accounts_table.sql:**
```sql
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    type VARCHAR(20) NOT NULL CHECK (type IN ('CHECKING', 'SAVINGS')),
    client_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_client FOREIGN KEY (client_id) 
        REFERENCES clients(id) ON DELETE CASCADE
);

CREATE INDEX idx_accounts_client_id ON accounts(client_id);
CREATE INDEX idx_accounts_number ON accounts(number);
```

---

## Running Migrations

**Programmatic approach with ServletContextListener:**
```java
import org.flywaydb.core.Flyway;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import javax.naming.InitialContext;
import javax.sql.DataSource;

@WebListener
public class DatabaseMigrationStartup implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Get DataSource from JNDI
            InitialContext ctx = new InitialContext();
            DataSource dataSource = (DataSource) ctx.lookup("jdbc/flywayDS");
            
            // Configure and run Flyway
            Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
            
            flyway.migrate();
            
            System.out.println("Database migration completed successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to run database migrations", e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}
```

**Note:** CDI-based initialization with `@PostConstruct` will be covered in Course 4.

---

## Migration Best Practices

**DO:**
- ✅ Never modify existing migrations
- ✅ Use sequential version numbers
- ✅ Test migrations on copy of production data
- ✅ Keep migrations small and focused
- ✅ Add comments to complex SQL
- ✅ Use transactions (default in Flyway)
- ✅ Version control migration files

**DON'T:**
- ❌ Don't delete old migrations
- ❌ Don't change migration checksums
- ❌ Don't use database-specific features without reason
- ❌ Don't mix DDL and DML in same migration
- ❌ Don't forget to test rollback scenarios

---

## Rollback Strategies

**Flyway doesn't support automatic rollback, but you can:**

1. **Create undo migrations:**
```
V5__add_column.sql
U5__remove_column.sql  (manual rollback)
```

2. **Use database backups:**
```bash
# Before migration
pg_dump bankdb > backup_before_v5.sql

# If needed, restore
psql bankdb < backup_before_v5.sql
```

3. **Write reversible migrations:**
```sql
-- V5__add_status_column.sql
ALTER TABLE clients ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE';

-- To rollback manually:
-- ALTER TABLE clients DROP COLUMN status;
```

---

## JPA Configuration

**persistence.xml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
             https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    
    <persistence-unit name="bankPU" transaction-type="JTA">
        <jta-data-source>java:jboss/datasources/BankDS</jta-data-source>
        
        <class>com.bank.model.Client</class>
        <class>com.bank.model.Account</class>
        
        <properties>
            <!-- Hibernate properties -->
            <property name="hibernate.dialect" 
                      value="org.hibernate.dialect.PostgreSQLDialect"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
            
            <!-- Schema generation: NONE (use Flyway instead) -->
            <property name="jakarta.persistence.schema-generation.database.action" 
                      value="none"/>
        </properties>
    </persistence-unit>
</persistence>
```

---

## 📊 Summary

**What we learned:**

1. **JPA Basics:** ORM concepts, entity mapping, annotations
2. **Relationships:** One-to-Many, Many-to-One, Many-to-Many
3. **Querying:** JPQL for object-oriented queries
4. **Criteria API:** Type-safe, dynamic query building
5. **Transactions:** Manual transaction management with EntityManager
6. **Migrations:** Flyway for version-controlled schema changes

**Key Takeaways:**
- Use JPA for database abstraction
- Prefer LAZY loading for collections
- Manage transactions explicitly with EntityTransaction
- Always close EntityManager in finally blocks
- Manage schema with migration tools
- Test queries and transactions thoroughly

**Note:** CDI and declarative transaction management (`@Transactional`) will be covered in Course 4.

---

## 🎯 Lab 3 Preview

**In the next lab, you will:**

1. Convert Client and Account to JPA entities
2. Configure persistence.xml and data source
3. Create service classes with manual EntityManager management
4. Write JPQL queries for CRUD operations
5. Implement Criteria API for dynamic search
6. Manage transactions manually with EntityTransaction
7. Set up Flyway migrations for database schema
8. Use ServletContextListener for application initialization
9. Test the complete persistence layer

**Get ready to make your banking app database-backed!**

---

## 📚 Additional Resources

**Official Documentation:**
- Jakarta Persistence Specification: https://jakarta.ee/specifications/persistence/
- Hibernate Documentation: https://hibernate.org/orm/documentation/
- Flyway Documentation: https://flywaydb.org/documentation/

**Tutorials:**
- Jakarta EE Tutorial (JPA Chapter): https://jakarta.ee/learn/docs/jakartaee-tutorial/
- Baeldung JPA Guide: https://www.baeldung.com/jpa-hibernate-guide

**Books:**
- "Pro JPA 2" by Mike Keith and Merrick Schincariol
- "Java Persistence with Hibernate" by Christian Bauer

---

## ❓ Questions?

**Common Questions:**

Q: When should I use JPQL vs Criteria API?
A: Use JPQL for static queries, Criteria API for dynamic queries.

Q: What's the difference between `persist()` and `merge()`?
A: `persist()` for new entities, `merge()` for detached entities.

Q: Should I use bidirectional relationships?
A: Only when you need to navigate from both sides.

Q: How do I avoid N+1 query problem?
A: Use JOIN FETCH in JPQL or fetch joins in Criteria API.

---

# 🚀 Ready for Lab 3!

**Next Steps:**
1. Review this lecture
2. Complete Lab 3: JPA Database Integration
3. Practice writing JPQL queries
4. Experiment with Criteria API
5. Set up Flyway migrations

**See you in the lab!**

---

**End of Lecture 3**

© 2025 - Jakarta EE & MicroProfile Course