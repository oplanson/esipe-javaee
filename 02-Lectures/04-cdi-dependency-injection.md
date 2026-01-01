<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

---
marp: true
theme: default
paginate: true
backgroundColor: #fff
header: 'Jakarta EE & MicroProfile Course'
footer: 'Lecture 4: CDI and Dependency Injection | © 2025'
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

# 💉 Lecture 4: CDI and Dependency Injection

**Jakarta Contexts and Dependency Injection (CDI)**

---

## 📋 Lecture Objectives

By the end of this lecture, you will be able to:

| | |
| --- | --- |
| ✅ | Understand dependency injection principles and benefits |
| ✅ | Use CDI annotations to manage bean lifecycle |
| ✅ | Apply different bean scopes appropriately |
| ✅ | Implement qualifiers for type-safe injection |
| ✅ | Use producer methods for complex object creation |
| ✅ | Apply declarative transaction management with @Transactional |

---

## 📚 Topics Covered

**Part 1:** Introduction to Dependency Injection and CDI
**Part 2:** CDI Bean Scopes and Lifecycle
**Part 3:** Injection Points and Qualifiers
**Part 4:** Producer Methods and Disposers
**Part 5:** Interceptors and Decorators
**Part 6:** Declarative Transaction Management
**Part 7:** CDI Events and Observers

---

# Part 1: Introduction to Dependency Injection

---

## What is Dependency Injection?

**Dependency Injection (DI)** is a design pattern where objects receive their dependencies from external sources rather than creating them internally.

### Traditional Approach (Without DI):

```java
public class ClientController {
    private ClientService clientService;
    
    public ClientController() {
        // Tight coupling - controller creates its own dependencies
        this.clientService = new ClientService();
    }
}
```

**Problems:**
- Tight coupling between classes
- Hard to test (can't mock dependencies)
- Difficult to change implementations
- Manual lifecycle management

---

## Dependency Injection Approach

### With DI:

```java
@WebServlet("/clients")
public class ClientController extends HttpServlet {
    
    @Inject
    private ClientService clientService;
    
    // No constructor needed!
    // CDI container injects the dependency automatically
}
```

**Benefits:**
- Loose coupling
- Easy to test (inject mocks)
- Flexible (change implementations easily)
- Automatic lifecycle management
- Cleaner code

---

## What is CDI?

**Jakarta Contexts and Dependency Injection (CDI)** is the standard dependency injection framework for Jakarta EE.

### Key Features:

| Feature | Description |
|---------|-------------|
| **Type-safe DI** | Compile-time type checking |
| **Contextual lifecycle** | Automatic bean lifecycle management |
| **Interceptors** | Cross-cutting concerns (logging, security) |
| **Events** | Loosely coupled communication |
| **Decorators** | Dynamic behavior enhancement |
| **Producers** | Complex object creation |

---

## CDI Architecture

### How CDI Works:

```
┌─────────────────────────────────────┐
│     CDI Container (Weld)            │
│                                     │
│  ┌──────────────────────────────┐  │
│  │   Bean Discovery             │  │
│  │   - Scan classes             │  │
│  │   - Find @Inject points      │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │   Bean Creation              │  │
│  │   - Instantiate beans        │  │
│  │   - Inject dependencies      │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │   Lifecycle Management       │  │
│  │   - Manage scopes            │  │
│  │   - Handle destruction       │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```

---

## Enabling CDI

### beans.xml Configuration

Create `src/main/webapp/WEB-INF/beans.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
       https://jakarta.ee/xml/ns/jakartaee/beans_4_0.xsd"
       version="4.0"
       bean-discovery-mode="all">
</beans>
```

**Bean Discovery Modes:**
- `all` - All classes are potential beans
- `annotated` - Only classes with bean-defining annotations
- `none` - CDI disabled

---

# Part 2: CDI Bean Scopes

---

## What are Bean Scopes?

**Scopes** define the lifecycle and visibility of CDI beans.

### Available Scopes:

| Scope | Annotation | Lifecycle | Use Case |
|-------|-----------|-----------|----------|
| **Request** | `@RequestScoped` | One HTTP request | Request-specific data |
| **Session** | `@SessionScoped` | User session | User-specific data |
| **Application** | `@ApplicationScoped` | Application lifetime | Singletons, services |
| **Dependent** | `@Dependent` | Injecting bean's scope | Utility objects |
| **Conversation** | `@ConversationScoped` | Multi-request workflow | Wizards, multi-step forms |

---

## @ApplicationScoped - Singleton Beans

**Use for:** Services, repositories, stateless components

```java
@ApplicationScoped
public class ClientService {
    
    @Inject
    private EntityManager em;
    
    public List<Client> findAll() {
        return em.createQuery("SELECT c FROM Client c", Client.class)
                .getResultList();
    }
    
    @Transactional
    public void create(Client client) {
        em.persist(client);
    }
}
```

**Characteristics:**
- One instance per application
- Shared across all users
- Must be thread-safe
- Ideal for stateless services

---

## @RequestScoped - Per-Request Beans

**Use for:** Request-specific data, controllers

```java
@RequestScoped
@Named("clientBean")
public class ClientBean {
    
    @Inject
    private ClientService clientService;
    
    private List<Client> clients;
    private Client selectedClient;
    
    @PostConstruct
    public void init() {
        clients = clientService.findAll();
    }
    
    // Getters and setters
}
```

**Characteristics:**
- New instance per HTTP request
- Destroyed after response sent
- Not shared between requests
- Ideal for web controllers

---

## @SessionScoped - Per-User Beans

**Use for:** User-specific data, shopping carts, user preferences

```java
@SessionScoped
public class UserSession implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String username;
    private List<String> recentActions = new ArrayList<>();
    
    public void addAction(String action) {
        recentActions.add(action);
    }
    
    public void logout() {
        username = null;
        recentActions.clear();
    }
    
    // Getters and setters
}
```

**Characteristics:**
- One instance per user session
- Must implement `Serializable`
- Survives multiple requests
- Destroyed when session expires

---

## @Dependent - Default Scope

**Use for:** Utility objects, value holders

```java
@Dependent
public class EmailValidator {
    
    public boolean isValid(String email) {
        return email != null && 
               email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
```

**Characteristics:**
- No independent lifecycle
- Lifecycle tied to injecting bean
- New instance per injection point
- Default scope if none specified

---

## Bean Lifecycle Callbacks

```java
@ApplicationScoped
public class ClientService {
    
    @Inject
    private EntityManager em;
    
    @PostConstruct
    public void init() {
        System.out.println("ClientService initialized");
        // Initialization logic
    }
    
    @PreDestroy
    public void cleanup() {
        System.out.println("ClientService destroyed");
        // Cleanup logic
    }
}
```

**Lifecycle Methods:**
- `@PostConstruct` - Called after dependency injection
- `@PreDestroy` - Called before bean destruction

---

# Part 3: Injection Points and Qualifiers

---

## Basic Injection with @Inject

### Field Injection:

```java
@WebServlet("/clients")
public class ClientController extends HttpServlet {
    
    @Inject
    private ClientService clientService;
    
    @Inject
    private AccountService accountService;
}
```

### Constructor Injection (Recommended):

```java
@ApplicationScoped
public class ClientService {
    
    private final EntityManager em;
    
    @Inject
    public ClientService(EntityManager em) {
        this.em = em;
    }
}
```

---

## Why Constructor Injection?

**Advantages:**
- Immutable dependencies (final fields)
- Easier to test (no reflection needed)
- Clear dependencies
- Null-safe

**Example:**

```java
@ApplicationScoped
public class TransferService {
    
    private final ClientService clientService;
    private final AccountService accountService;
    
    @Inject
    public TransferService(ClientService clientService,
                          AccountService accountService) {
        this.clientService = clientService;
        this.accountService = accountService;
    }
    
    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // Use injected services
    }
}
```

---

## Qualifiers - Type-Safe Selection

**Problem:** Multiple implementations of same interface

```java
public interface PaymentProcessor {
    void process(Payment payment);
}

@ApplicationScoped
public class CreditCardProcessor implements PaymentProcessor {
    // Implementation
}

@ApplicationScoped
public class PayPalProcessor implements PaymentProcessor {
    // Implementation
}
```

**How to inject the right one?** → Use Qualifiers!

---

## Creating Custom Qualifiers

### Define Qualifier Annotation:

```java
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface CreditCard {
}

@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface PayPal {
}
```

### Apply to Implementations:

```java
@CreditCard
@ApplicationScoped
public class CreditCardProcessor implements PaymentProcessor {
    public void process(Payment payment) {
        // Credit card processing
    }
}

@PayPal
@ApplicationScoped
public class PayPalProcessor implements PaymentProcessor {
    public void process(Payment payment) {
        // PayPal processing
    }
}
```

---

## Using Qualifiers for Injection

```java
@ApplicationScoped
public class PaymentService {
    
    @Inject
    @CreditCard
    private PaymentProcessor creditCardProcessor;
    
    @Inject
    @PayPal
    private PaymentProcessor payPalProcessor;
    
    public void processPayment(Payment payment) {
        if (payment.getType() == PaymentType.CREDIT_CARD) {
            creditCardProcessor.process(payment);
        } else if (payment.getType() == PaymentType.PAYPAL) {
            payPalProcessor.process(payment);
        }
    }
}
```

**Benefits:**
- Type-safe selection
- Compile-time checking
- Clear intent
- No string-based lookup

---

## @Alternative - Switchable Implementations

**Use for:** Different implementations for different environments

```java
@Alternative
@Priority(100)
@ApplicationScoped
public class MockPaymentProcessor implements PaymentProcessor {
    
    @Override
    public void process(Payment payment) {
        System.out.println("Mock payment: " + payment.getAmount());
        // No actual processing
    }
}
```

**Activation in beans.xml:**

```xml
<beans>
    <alternatives>
        <class>com.bank.service.MockPaymentProcessor</class>
    </alternatives>
</beans>
```

---

# Part 4: Producer Methods

---

## What are Producer Methods?

**Producer methods** create and configure beans programmatically when simple injection isn't enough.

### Use Cases:
- Complex object creation
- Third-party classes (can't annotate)
- Runtime configuration
- Resource management

### Basic Producer:

```java
@ApplicationScoped
public class EntityManagerProducer {
    
    @PersistenceContext
    private EntityManager em;
    
    @Produces
    @RequestScoped
    public EntityManager getEntityManager() {
        return em;
    }
}
```

---

## Producer Methods with Configuration

```java
@ApplicationScoped
public class DataSourceProducer {
    
    @Inject
    @ConfigProperty(name = "db.url")
    private String dbUrl;
    
    @Inject
    @ConfigProperty(name = "db.user")
    private String dbUser;
    
    @Inject
    @ConfigProperty(name = "db.password")
    private String dbPassword;
    
    @Produces
    @ApplicationScoped
    public DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(10);
        
        return new HikariDataSource(config);
    }
}
```

---

## Producer Methods with Qualifiers

```java
@ApplicationScoped
public class LoggerProducer {
    
    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(
            injectionPoint.getMember()
                         .getDeclaringClass()
                         .getName()
        );
    }
}
```

**Usage:**

```java
@ApplicationScoped
public class ClientService {
    
    @Inject
    private Logger logger;  // Automatically gets logger for ClientService
    
    public void create(Client client) {
        logger.info("Creating client: " + client.getName());
        // ...
    }
}
```

---

## Disposer Methods

**Clean up resources** created by producers:

```java
@ApplicationScoped
public class DataSourceProducer {
    
    @Produces
    @ApplicationScoped
    public DataSource createDataSource() {
        // Create and configure DataSource
        return new HikariDataSource(config);
    }
    
    public void closeDataSource(@Disposes DataSource dataSource) {
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }
}
```

**Lifecycle:**
1. Producer method called when bean needed
2. Bean used throughout its scope
3. Disposer method called when scope ends

---

# Part 5: Interceptors and Decorators

---

## Interceptors - Cross-Cutting Concerns

**Interceptors** add behavior to methods without modifying the method itself.

### Common Use Cases:
- Logging
- Security checks
- Performance monitoring
- Transaction management
- Caching

### Creating an Interceptor:

```java
@InterceptorBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Logged {
}
```

---

## Implementing an Interceptor

```java
@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggingInterceptor {
    
    @Inject
    private Logger logger;
    
    @AroundInvoke
    public Object logMethod(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getSimpleName();
        
        logger.info("Entering: " + className + "." + methodName);
        
        try {
            Object result = context.proceed();
            logger.info("Exiting: " + className + "." + methodName);
            return result;
        } catch (Exception e) {
            logger.severe("Exception in: " + className + "." + methodName + 
                         " - " + e.getMessage());
            throw e;
        }
    }
}
```

---

## Using Interceptors

### On Class (All Methods):

```java
@Logged
@ApplicationScoped
public class ClientService {
    
    public List<Client> findAll() {
        // Method automatically logged
    }
    
    public void create(Client client) {
        // Method automatically logged
    }
}
```

### On Specific Methods:

```java
@ApplicationScoped
public class ClientService {
    
    @Logged
    public List<Client> findAll() {
        // Only this method logged
    }
    
    public void create(Client client) {
        // Not logged
    }
}
```

---

## Performance Monitoring Interceptor

```java
@InterceptorBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Monitored {
}

@Monitored
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class PerformanceInterceptor {
    
    @Inject
    private Logger logger;
    
    @AroundInvoke
    public Object monitor(InvocationContext context) throws Exception {
        long start = System.currentTimeMillis();
        
        try {
            return context.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            logger.info(context.getMethod().getName() + 
                       " took " + duration + "ms");
        }
    }
}
```

---

## Decorators - Dynamic Behavior

**Decorators** wrap beans to add behavior while maintaining the same interface.

```java
@Decorator
@Priority(Interceptor.Priority.APPLICATION)
public abstract class AuditedClientService implements ClientService {
    
    @Inject
    @Delegate
    @Any
    private ClientService delegate;
    
    @Inject
    private AuditService auditService;
    
    @Override
    public void create(Client client) {
        delegate.create(client);
        auditService.log("Created client: " + client.getName());
    }
    
    @Override
    public void delete(Long id) {
        delegate.delete(id);
        auditService.log("Deleted client: " + id);
    }
}
```

---

# Part 6: Declarative Transaction Management

---

## Why Declarative Transactions?

### Manual Transaction Management (Lab 3):

```java
public void create(Client client) {
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    
    try {
        tx.begin();
        em.persist(client);
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
```

**Problems:** Boilerplate code, error-prone, hard to maintain

---

## @Transactional Annotation

### Declarative Approach:

```java
@ApplicationScoped
public class ClientService {
    
    @Inject
    private EntityManager em;
    
    @Transactional
    public void create(Client client) {
        em.persist(client);
        // Transaction automatically managed!
    }
    
    @Transactional
    public void update(Client client) {
        em.merge(client);
    }
    
    @Transactional
    public void delete(Long id) {
        Client client = em.find(Client.class, id);
        if (client != null) {
            em.remove(client);
        }
    }
}
```

**Benefits:** Clean code, automatic rollback, consistent behavior

---

## Transaction Attributes

```java
@ApplicationScoped
public class ClientService {
    
    // Default: REQUIRED - Join existing or create new
    @Transactional
    public void create(Client client) {
        em.persist(client);
    }
    
    // REQUIRES_NEW - Always create new transaction
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void audit(String action) {
        AuditLog log = new AuditLog(action);
        em.persist(log);
    }
    
    // SUPPORTS - Use transaction if exists, otherwise non-transactional
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Client> findAll() {
        return em.createQuery("SELECT c FROM Client c", Client.class)
                .getResultList();
    }
}
```

---

## Transaction Types

| Type | Description | Use Case |
|------|-------------|----------|
| **REQUIRED** | Join existing or create new (default) | Most operations |
| **REQUIRES_NEW** | Always create new transaction | Independent operations, audit logs |
| **MANDATORY** | Must have existing transaction | Enforce transactional context |
| **SUPPORTS** | Use if exists, otherwise non-transactional | Read operations |
| **NOT_SUPPORTED** | Suspend existing transaction | Non-transactional operations |
| **NEVER** | Throw exception if transaction exists | Ensure non-transactional |

---

## Rollback Strategies

```java
@ApplicationScoped
public class TransferService {
    
    @Inject
    private AccountService accountService;
    
    // Rollback on any exception (default for RuntimeException)
    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Account from = accountService.findById(fromId);
        Account to = accountService.findById(toId);
        
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        
        accountService.update(from);
        accountService.update(to);
    }
    
    // Rollback on specific exceptions
    @Transactional(rollbackOn = {ValidationException.class})
    public void createWithValidation(Client client) {
        validate(client);
        em.persist(client);
    }
}
```

---

## JTA Configuration for CDI

### Update persistence.xml:

```xml
<persistence-unit name="bankingPU" transaction-type="JTA">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <jta-data-source>jdbc/bankingDS</jta-data-source>
    
    <class>com.bank.model.Client</class>
    <class>com.bank.model.Account</class>
    
    <properties>
        <property name="eclipselink.target-database" value="PostgreSQL"/>
        <property name="eclipselink.logging.level" value="FINE"/>
        <property name="eclipselink.ddl-generation" value="none"/>
    </properties>
</persistence-unit>
```

**Key Changes:**
- `transaction-type="JTA"` (was RESOURCE_LOCAL)
- `<jta-data-source>` (was non-jta-data-source)

---

## EntityManager Producer for CDI

```java
@ApplicationScoped
public class EntityManagerProducer {
    
    @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;
    
    @Produces
    @RequestScoped
    public EntityManager getEntityManager() {
        return em;
    }
}
```

**Now inject EntityManager anywhere:**

```java
@ApplicationScoped
public class ClientService {
    
    @Inject
    private EntityManager em;  // Injected by CDI!
    
    @Transactional
    public void create(Client client) {
        em.persist(client);
    }
}
```

---

# Part 7: CDI Events

---

## Event-Driven Architecture with CDI

**CDI Events** enable loosely coupled communication between beans.

### Benefits:
- Decoupled components
- Extensible architecture
- Asynchronous processing
- Observer pattern built-in

### Event Flow:

```
Producer Bean → Fire Event → CDI Container → Notify Observers
```

---

## Defining and Firing Events

### Event Class:

```java
public class ClientCreatedEvent {
    private final Client client;
    private final LocalDateTime timestamp;
    
    public ClientCreatedEvent(Client client) {
        this.client = client;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
}
```

### Firing Events:

```java
@ApplicationScoped
public class ClientService {
    
    @Inject
    private EntityManager em;
    
    @Inject
    private Event<ClientCreatedEvent> clientCreatedEvent;
    
    @Transactional
    public void create(Client client) {
        em.persist(client);
        clientCreatedEvent.fire(new ClientCreatedEvent(client));
    }
}
```

---

## Observing Events

### Synchronous Observer:

```java
@ApplicationScoped
public class EmailNotificationService {
    
    @Inject
    private Logger logger;
    
    public void onClientCreated(@Observes ClientCreatedEvent event) {
        logger.info("Sending welcome email to: " + 
                   event.getClient().getEmail());
        // Send email
    }
}
```

### Asynchronous Observer:

```java
@ApplicationScoped
public class AuditService {
    
    public void onClientCreated(@ObservesAsync ClientCreatedEvent event) {
        // Runs in separate thread
        AuditLog log = new AuditLog(
            "Client created: " + event.getClient().getName(),
            event.getTimestamp()
        );
        // Save audit log
    }
}
```

---

## Event Qualifiers

**Filter events** with qualifiers:

```java
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
public @interface Premium {
}

// Fire qualified event
@Inject
@Premium
private Event<ClientCreatedEvent> premiumClientEvent;

premiumClientEvent.fire(new ClientCreatedEvent(client));

// Observe qualified event
public void onPremiumClient(@Observes @Premium ClientCreatedEvent event) {
    // Only receives premium client events
}
```

---

## Conditional Observers

```java
@ApplicationScoped
public class NotificationService {
    
    // Only observe during transaction success
    public void onClientCreated(
            @Observes(during = TransactionPhase.AFTER_SUCCESS) 
            ClientCreatedEvent event) {
        sendNotification(event.getClient());
    }
    
    // Only observe during transaction failure
    public void onClientCreationFailed(
            @Observes(during = TransactionPhase.AFTER_FAILURE) 
            ClientCreatedEvent event) {
        logError(event.getClient());
    }
}
```

**Transaction Phases:**
- `IN_PROGRESS` - During transaction
- `AFTER_SUCCESS` - After commit
- `AFTER_FAILURE` - After rollback
- `AFTER_COMPLETION` - After transaction ends

---

## 📊 Complete CDI Example

### Service with All CDI Features:

```java
@ApplicationScoped
@Logged
@Monitored
public class ClientService {
    
    @Inject
    private EntityManager em;
    
    @Inject
    private Logger logger;
    
    @Inject
    private Event<ClientCreatedEvent> clientCreatedEvent;
    
    @Inject
    @ConfigProperty(name = "client.max.accounts", defaultValue = "5")
    private Integer maxAccounts;
    
    @Transactional
    public void create(Client client) {
        logger.info("Creating client: " + client.getName());
        em.persist(client);
        clientCreatedEvent.fire(new ClientCreatedEvent(client));
    }
    
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Client> findAll() {
        return em.createQuery("SELECT c FROM Client c", Client.class)
                .getResultList();
    }
}
```

---

## 🎯 Lab 4 Preview

**In the next lab, you will:**

1. Convert Lab 3 code to use CDI
2. Replace Singleton pattern with @ApplicationScoped beans
3. Use @Inject for dependency injection
4. Apply @Transactional for declarative transactions
5. Change persistence.xml from RESOURCE_LOCAL to JTA
6. Create producer methods for EntityManager
7. Implement custom qualifiers
8. Add interceptors for logging
9. Use CDI events for notifications
10. Test the complete CDI-based application

**Get ready to modernize your banking app with CDI!**

---

## 📚 Additional Resources

**Official Documentation:**
- Jakarta CDI Specification: https://jakarta.ee/specifications/cdi/
- Weld (CDI Implementation): https://weld.cdi-spec.org/
- Jakarta Transactions: https://jakarta.ee/specifications/transactions/

**Tutorials:**
- Jakarta EE Tutorial (CDI Chapter): https://jakarta.ee/learn/docs/jakartaee-tutorial/
- Baeldung CDI Guide: https://www.baeldung.com/java-ee-cdi

**Books:**
- "CDI in Action" by Antoine Sabot-Durand
- "Pro CDI 2 in Java EE 8" by Jan Beernink

---

## ❓ Questions?

**Common Questions:**

Q: When should I use @ApplicationScoped vs @RequestScoped?
A: Use @ApplicationScoped for stateless services, @RequestScoped for request-specific data.

Q: What's the difference between @Inject and @Autowired (Spring)?
A: @Inject is the Jakarta EE standard, @Autowired is Spring-specific. Functionality is similar.

Q: Can I use CDI with servlets?
A: Yes! Inject CDI beans into servlets using @Inject.

Q: How do I test CDI beans?
A: Use Arquillian for integration tests or mock @Inject fields for unit tests.

---

## 📝 Homework

**Before Next Lecture:**

| | |
|---|---|
| ✅ | Complete Lab 4: CDI Service Layer |
| ✅ | Convert Lab 3 code to use CDI |
| ✅ | Practice using different bean scopes |
| ✅ | Experiment with interceptors |

**Optional:**
- Read about CDI extensions
- Explore CDI events and observers
- Try creating custom producers

---

## 🙋 Questions & Discussion

**Discussion Topics:**
- How does CDI improve testability?
- When would you use decorators vs interceptors?
- What are the benefits of declarative transactions?

**Office Hours:**
- **When:** [Your schedule]
- **Where:** [Your location/online]
- **Contact:** [Your email]

---

## 📅 Next Lecture

### JAX-RS and RESTful Web Services
**Date:** [Next session date]  
**Duration:** 3 hours  
**Topics:**
- REST principles and architecture
- JAX-RS annotations and resources
- JSON-B for JSON processing
- Exception handling and validation
- MicroProfile Rest Client

**Preparation:** Complete Lab 4 and review REST concepts

---

# 🚀 Ready for Lab 4!

**Next Steps:**
1. Review this lecture
2. Complete Lab 4: CDI Service Layer
3. Practice dependency injection
4. Experiment with bean scopes
5. Try creating interceptors

**See you in the lab!**

---

**End of Lecture 4**

© 2025 - Jakarta EE & MicroProfile Course