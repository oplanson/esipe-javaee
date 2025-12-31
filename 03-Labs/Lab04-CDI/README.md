# Lab 04: CDI and Dependency Injection

## 🎯 Objectives

In this lab, you will:
- Understand CDI (Contexts and Dependency Injection) fundamentals
- Use `@Inject` for dependency injection
- Apply different bean scopes (`@ApplicationScoped`, `@RequestScoped`)
- Implement declarative transaction management with `@Transactional`
- Create producer methods for complex object creation
- Use interceptors for cross-cutting concerns
- Migrate from Singleton pattern to CDI-managed beans
- Configure JTA transactions with JPA
- **🆕 Implement CDI Events for event-driven architecture**
- **🆕 Use CDI Qualifiers for type-safe dependency injection**

## 📚 Prerequisites

- Completion of Lab 03 (JPA and Database Integration)
- Understanding of dependency injection principles
- Basic knowledge of design patterns (Singleton, Factory)
- Familiarity with JPA and transactions

## 🏗️ Architecture Overview

### Before CDI (Lab 03):
```
┌─────────────────────────────────────┐
│     ClientController (Servlet)      │
│                                     │
│  clientService = ClientService      │
│                  .getInstance()     │  ← Singleton pattern
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│   ClientService (Singleton)         │
│                                     │
│  - Manual EntityManager creation    │
│  - Manual transaction management    │  ← Boilerplate code
│  - try/catch/finally blocks         │
└─────────────────────────────────────┘
```

### After CDI (Lab 04):
```
┌─────────────────────────────────────┐
│     ClientController (Servlet)      │
│                                     │
│  @Inject                            │
│  private ClientService service;     │  ← CDI injection
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│   ClientService                     │
│   @ApplicationScoped                │  ← CDI managed
│                                     │
│  @Inject EntityManager em;          │  ← Injected
│                                     │
│  @Transactional                     │  ← Declarative
│  public void create(Client c) {...} │
└─────────────────────────────────────┘
```

## 📋 Lab Structure

```
Lab04-CDI/
├── README.md                          # This file
├── ADVANCED-CDI-FEATURES.md          # 🆕 Advanced CDI documentation
├── solution/                          # Complete working solution
│   ├── pom.xml                       # Maven configuration
│   ├── Containerfile                 # Container image definition
│   ├── docker-compose.yml            # Multi-container setup
│   └── src/
│       └── main/
│           ├── java/com/bank/
│           │   ├── config/           # CDI configuration
│           │   │   ├── EntityManagerProducer.java
│           │   │   ├── Logged.java
│           │   │   ├── LoggingInterceptor.java
│           │   │   ├── Premium.java              # 🆕 Qualifier
│           │   │   ├── Standard.java             # 🆕 Qualifier
│           │   │   └── DatabaseMigrationStartup.java
│           │   ├── event/            # 🆕 CDI Events
│           │   │   ├── ClientCreatedEvent.java
│           │   │   ├── AccountCreatedEvent.java
│           │   │   ├── TransactionEvent.java
│           │   │   └── BankingEventObserver.java
│           │   ├── model/            # JPA entities
│           │   │   ├── Client.java
│           │   │   └── Account.java
│           │   ├── service/          # Business logic with CDI
│           │   │   ├── ClientService.java
│           │   │   ├── AccountService.java
│           │   │   ├── NotificationService.java  # 🆕 Interface
│           │   │   ├── PremiumNotificationService.java   # 🆕
│           │   │   └── StandardNotificationService.java  # 🆕
│           │   ├── web/              # Servlets with CDI
│           │   │   ├── ClientController.java
│           │   │   └── AccountController.java
│           │   └── health/           # MicroProfile Health
│           │       ├── DatabaseHealthCheck.java
│           │       └── WebAppReadinessCheck.java
│           ├── resources/
│           │   ├── META-INF/
│           │   │   ├── persistence.xml        # JTA configuration
│           │   │   └── microprofile-config.properties
│           │   └── db/migration/              # Flyway migrations
│           ├── webapp/
│           │   └── WEB-INF/
│           │       ├── beans.xml              # CDI activation
│           │       └── views/                 # JSP views
│           └── liberty/config/
│               ├── server.xml                 # CDI features enabled
│               └── bootstrap.properties
├── starter/                           # Starting point for students
└── *.sh                              # Test scripts

```

## 🔑 Key Concepts

### 1. CDI Bean Scopes

| Scope | Annotation | Lifecycle | Use Case |
|-------|-----------|-----------|----------|
| **Application** | `@ApplicationScoped` | Application lifetime | Singletons, stateless services |
| **Request** | `@RequestScoped` | One HTTP request | Request-specific data |
| **Session** | `@SessionScoped` | User session | User-specific data |
| **Dependent** | `@Dependent` | Injecting bean's scope | Utility objects |

### 2. Dependency Injection

**Field Injection:**
```java
@Inject
private ClientService clientService;
```

**Constructor Injection (Recommended):**
```java
private final ClientService clientService;

@Inject
public ClientController(ClientService clientService) {
    this.clientService = clientService;
}
```

### 3. Declarative Transactions

**Before (Manual):**
```java
EntityManager em = emf.createEntityManager();
EntityTransaction tx = em.getTransaction();
try {
    tx.begin();
    em.persist(client);
    tx.commit();
} catch (Exception e) {
    if (tx.isActive()) tx.rollback();
    throw e;
} finally {
    em.close();
}
```

**After (Declarative):**
```java
@Transactional
public void create(Client client) {
    em.persist(client);
    // Transaction automatically managed!
}
```

### 4. Producer Methods

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
    
    @Produces
    public Logger produceLogger(InjectionPoint ip) {
        return Logger.getLogger(
            ip.getMember().getDeclaringClass().getName()
        );
    }
}
```

### 5. Interceptors

```java
@InterceptorBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Logged {
}

@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggingInterceptor {
    
    @AroundInvoke
    public Object logMethod(InvocationContext ctx) throws Exception {
        // Log entry, execution, and exit
        return ctx.proceed();
    }
}
```

## 🌟 Advanced CDI Features

This lab includes **advanced CDI features** that demonstrate enterprise-grade patterns:

### 🎯 CDI Events (Event-Driven Architecture)

Events provide loose coupling between components:

```java
// Fire an event
@Inject
private Event<ClientCreatedEvent> clientCreatedEvent;

clientCreatedEvent.fire(new ClientCreatedEvent(client));

// Observe an event
public void onClientCreated(@Observes ClientCreatedEvent event) {
    logger.info("Client created: " + event.getClient().getName());
}
```

**Implemented Events:**
- `ClientCreatedEvent` - Fired when clients are created
- `AccountCreatedEvent` - Fired when accounts are created
- `TransactionEvent` - Fired for deposits, withdrawals, and transfers

**Benefits:**
- Loose coupling between producers and consumers
- Easy to add new observers without modifying producers
- Type-safe event handling
- Synchronous by default (can be made asynchronous)

### 🏷️ CDI Qualifiers (Type-Safe Injection)

Qualifiers allow multiple implementations of the same interface:

```java
// Define qualifiers
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface Premium {}

@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface Standard {}

// Use qualifiers
@Inject
@Premium
private NotificationService premiumService;

@Inject
@Standard
private NotificationService standardService;
```

**Implemented Services:**
- `PremiumNotificationService` - Multi-channel notifications (Email, SMS, Push, In-app)
- `StandardNotificationService` - Basic email notifications

**Benefits:**
- Type-safe dependency selection
- Multiple implementations without ambiguity
- Runtime configuration flexibility
- Clean separation of concerns

### 📖 Complete Documentation

For detailed information about advanced CDI features, see:
**[ADVANCED-CDI-FEATURES.md](solution/ADVANCED-CDI-FEATURES.md)**

This document includes:
- Complete event architecture explanation
- Qualifier usage patterns
- Code examples and best practices
- Testing scenarios
- Architecture diagrams

## 🚀 Getting Started

### Step 1: Review Lab 03 Code

Before starting, review the Lab 03 solution to understand:
- Singleton pattern in `ClientService`
- Manual transaction management
- Manual EntityManager lifecycle

### Step 2: Enable CDI

Create `src/main/webapp/WEB-INF/beans.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
       version="4.0"
       bean-discovery-mode="all">
</beans>
```

### Step 3: Update server.xml

Add CDI features:
```xml
<featureManager>
    <feature>cdi-4.0</feature>
    <feature>transaction-2.0</feature>
    <!-- ... other features ... -->
</featureManager>
```

### Step 4: Update persistence.xml

Change from RESOURCE_LOCAL to JTA:
```xml
<persistence-unit name="bankingPU" transaction-type="JTA">
    <jta-data-source>jdbc/bankingDS</jta-data-source>
    <!-- ... -->
</persistence-unit>
```

### Step 5: Create Producer

Create `EntityManagerProducer.java`:
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

### Step 6: Convert ClientService

**Remove:**
- Singleton pattern (`getInstance()`)
- Manual EntityManager creation
- Manual transaction management

**Add:**
```java
@ApplicationScoped
public class ClientService {
    
    @Inject
    private EntityManager em;
    
    @Inject
    private Logger logger;
    
    @Transactional
    public Client create(Client client) {
        em.persist(client);
        return client;
    }
    
    @Transactional(TxType.SUPPORTS)
    public List<Client> findAll() {
        return em.createNamedQuery("Client.findAll", Client.class)
                .getResultList();
    }
}
```

### Step 7: Update ClientController

**Remove:**
- `ClientService.getInstance()`
- Manual configuration reading

**Add:**
```java
@WebServlet(...)
public class ClientController extends HttpServlet {
    
    @Inject
    private ClientService clientService;
    
    @Inject
    private Logger logger;
    
    @Inject
    @ConfigProperty(name = "app.name")
    private String appName;
}
```

## 🧪 Testing

### Run with Maven
```bash
cd solution
mvn clean liberty:dev
```

Access: http://localhost:9080

### Run with Podman
```bash
./podman-test.sh
```

### Run with Docker
```bash
./docker-test.sh
```

### Run with Docker Compose
```bash
cd solution
docker-compose up
```

## ✅ Verification Checklist

### CDI Configuration
- [ ] `beans.xml` exists in `WEB-INF/`
- [ ] `cdi-4.0` feature enabled in `server.xml`
- [ ] `transaction-2.0` feature enabled

### Persistence Configuration
- [ ] `persistence.xml` uses `transaction-type="JTA"`
- [ ] DataSource uses `<jta-data-source>`
- [ ] EntityManager producer created

### Service Layer
- [ ] `ClientService` annotated with `@ApplicationScoped`
- [ ] No more Singleton pattern
- [ ] EntityManager injected with `@Inject`
- [ ] Methods use `@Transactional`
- [ ] No manual transaction management

### Controller Layer
- [ ] `ClientController` injects `ClientService`
- [ ] Configuration injected with `@ConfigProperty`
- [ ] Logger injected with `@Inject`

### Testing
- [ ] Application starts without errors
- [ ] Health checks pass
- [ ] Client CRUD operations work
- [ ] Account CRUD operations work
- [ ] Account transactions (deposit/withdraw/transfer) work
- [ ] Transactions commit properly
- [ ] Logging works correctly
- [ ] Bidirectional relationships maintained correctly
- [ ] **🆕 CDI Events are fired and observed correctly**
- [ ] **🆕 Event logs appear in server logs**
- [ ] **🆕 Notification service qualifiers work correctly**
- [ ] **🆕 Can switch between Premium and Standard services**

## 📊 Comparison: Lab 03 vs Lab 04

| Aspect | Lab 03 (No CDI) | Lab 04 (With CDI) |
|--------|-----------------|-------------------|
| **Service Pattern** | Singleton | CDI @ApplicationScoped |
| **Dependency Management** | Manual getInstance() | @Inject |
| **Transaction Type** | RESOURCE_LOCAL | JTA |
| **Transaction Management** | Manual try/catch | @Transactional |
| **EntityManager** | Manual creation | Injected |
| **Configuration** | ServletContext | @ConfigProperty |
| **Logging** | Manual Logger.getLogger() | @Inject Logger |
| **Code Lines** | ~400 | ~300 |
| **Boilerplate** | High | Low |
| **Testability** | Difficult | Easy |

## 🎓 Learning Outcomes

After completing this lab, you will be able to:

1. **Understand CDI Fundamentals**
   - Bean scopes and lifecycle
   - Dependency injection patterns
   - CDI container responsibilities

2. **Apply Dependency Injection**
   - Use `@Inject` for field and constructor injection
   - Inject configuration with `@ConfigProperty`
   - Create producer methods

3. **Manage Transactions Declaratively**
   - Use `@Transactional` annotation
   - Understand JTA vs RESOURCE_LOCAL
   - Configure transaction attributes

4. **Implement Cross-Cutting Concerns**
   - Create interceptors
   - Apply interceptor bindings
   - Log method execution

5. **Write Cleaner Code**
   - Eliminate boilerplate
   - Improve testability
   - Follow best practices

6. **🆕 Master Advanced CDI Patterns**
   - Implement event-driven architecture
   - Use qualifiers for type-safe injection
   - Create loosely-coupled systems
   - Apply enterprise design patterns

## 🔍 Common Issues and Solutions

### Issue 1: CDI Not Working
**Symptom:** `@Inject` fields are null

**Solution:**
- Verify `beans.xml` exists in `WEB-INF/`
- Check `cdi-4.0` feature is enabled
- Ensure class has bean-defining annotation

### Issue 2: Transaction Not Committing
**Symptom:** Data not persisted to database

**Solution:**
- Verify `transaction-type="JTA"` in persistence.xml
- Check `@Transactional` annotation present
- Ensure `transaction-2.0` feature enabled

### Issue 3: EntityManager Injection Fails
**Symptom:** EntityManager is null

**Solution:**
- Verify producer method exists
- Check `@PersistenceContext` annotation
- Ensure persistence unit name matches

### Issue 4: Interceptor Not Firing
**Symptom:** Logging interceptor not working

**Solution:**
- Verify interceptor has `@Interceptor` annotation
- Check `@Priority` is set
- Ensure binding annotation applied to target

## 📚 Additional Resources

### Official Documentation
- [Jakarta CDI Specification](https://jakarta.ee/specifications/cdi/)
- [Jakarta Transactions](https://jakarta.ee/specifications/transactions/)
- [Open Liberty CDI Guide](https://openliberty.io/docs/latest/cdi-beans.html)

### Tutorials
- [CDI Tutorial](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/cdi/cdi-basic/cdi-basic.html)
- [Weld Reference Guide](https://docs.jboss.org/weld/reference/latest/en-US/html/)

### Books
- "CDI in Action" by Antoine Sabot-Durand
- "Pro CDI 2 in Java EE 8" by Jan Beernink

### Advanced Topics
- **[ADVANCED-CDI-FEATURES.md](solution/ADVANCED-CDI-FEATURES.md)** - Complete guide to CDI Events and Qualifiers

## 🎯 Next Steps

After completing this lab:
1. Review the solution code thoroughly
2. Experiment with different bean scopes
3. **🆕 Explore CDI Events and Qualifiers** (see ADVANCED-CDI-FEATURES.md)
4. Try creating custom qualifiers
5. Implement additional interceptors
6. Test event-driven patterns
7. Prepare for Lab 05: JAX-RS and RESTful Services

## 💡 Tips for Success

1. **Start Simple:** Begin with basic `@Inject` before adding interceptors
2. **Test Incrementally:** Test after each major change
3. **Read Logs:** CDI provides detailed logging about bean discovery
4. **Use Constructor Injection:** More testable than field injection
5. **Understand Scopes:** Choose the right scope for each bean
6. **Keep It Clean:** CDI should simplify, not complicate your code

## 🤝 Getting Help

If you encounter issues:
1. Check the logs in `target/liberty/wlp/usr/servers/bankingServer/logs/`
2. Review the solution code
3. Consult the official documentation
4. Ask your instructor

---

**Good luck with Lab 04! 🚀**

*Made with Bob*