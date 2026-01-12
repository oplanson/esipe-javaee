<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 04B: Enterprise Java Beans (EJB)

## 🎯 Objectives

In this lab, you will:
- Understand EJB architecture and container services
- Implement **Stateless Session Beans** for account operations
- Create a **Stateful Session Bean** for transaction batches
- Build a **Singleton Session Bean** for application configuration
- Develop a **Message-Driven Bean (MDB)** for asynchronous notifications
- Use the **Timer Service** for scheduled reports
- Apply **Container-Managed Transactions (CMT)**
- Implement **EJB Security** with role-based access control
- Use **Asynchronous methods** for long-running operations
- Compare EJB vs CDI approaches

## 📚 Prerequisites

- Completion of Lab 04 (CDI and Dependency Injection)
- Understanding of transaction management
- Basic knowledge of messaging concepts
- Familiarity with concurrent programming

## 🏗️ Architecture Overview

### EJB Component Types:

```
┌─────────────────────────────────────────────────────────┐
│                    Banking Application                   │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ┌──────────────────┐  ┌──────────────────┐            │
│  │  Stateless EJB   │  │  Stateful EJB    │            │
│  │                  │  │                  │            │
│  │ AccountService   │  │ TransactionBatch │            │
│  │ - deposit()      │  │ - addTransaction()│           │
│  │ - withdraw()     │  │ - commit()       │            │
│  │ - transfer()     │  │ - rollback()     │            │
│  └──────────────────┘  └──────────────────┘            │
│                                                           │
│  ┌──────────────────┐  ┌──────────────────┐            │
│  │  Singleton EJB   │  │  Message-Driven  │            │
│  │                  │  │      Bean        │            │
│  │ ConfigService    │  │                  │            │
│  │ - getConfig()    │  │ NotificationMDB  │            │
│  │ - updateConfig() │  │ - onMessage()    │            │
│  └──────────────────┘  └──────────────────┘            │
│                                                           │
│  ┌──────────────────────────────────────┐               │
│  │         Timer Service                 │               │
│  │                                       │               │
│  │  ReportGeneratorEJB                  │               │
│  │  @Schedule(hour="0", minute="0")     │               │
│  │  - generateDailyReport()             │               │
│  └──────────────────────────────────────┘               │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

### Transaction Flow:

```
Client Request
     ↓
┌─────────────────────┐
│   Servlet/REST      │
│   (Web Layer)       │
└─────────────────────┘
     ↓ @EJB injection
┌─────────────────────┐
│  Stateless EJB      │
│  @TransactionAttribute(REQUIRED)
│                     │
│  Container starts   │ ← Automatic transaction
│  transaction        │    management
└─────────────────────┘
     ↓
┌─────────────────────┐
│   JPA/Database      │
│                     │
│  Commit/Rollback    │ ← Container handles
│  automatically      │    commit/rollback
└─────────────────────┘
```

## 📋 Lab Structure

```
Lab04B-EJB/
├── README.md                          # This file
├── solution/                          # Complete working solution
│   ├── pom.xml                       # Maven with EJB dependencies
│   ├── Containerfile                 # Container image definition
│   └── src/
│       └── main/
│           ├── java/com/bank/
│           │   ├── ejb/              # Session Beans
│           │   │   ├── AccountServiceBean.java      # Stateless
│           │   │   ├── TransactionBatchBean.java    # Stateful
│           │   │   ├── ConfigServiceBean.java       # Singleton
│           │   │   └── ReportGeneratorBean.java     # Timer Service
│           │   ├── mdb/              # Message-Driven Beans
│           │   │   └── NotificationMDB.java
│           │   ├── model/            # JPA entities
│           │   │   ├── Account.java
│           │   │   ├── Transaction.java
│           │   │   └── Report.java
│           │   ├── config/           # Configuration
│           │   │   ├── EntityManagerProducer.java
│           │   │   └── DatabaseMigrationStartup.java
│           │   └── health/           # MicroProfile Health
│           │       ├── DatabaseHealthCheck.java
│           │       └── WebAppReadinessCheck.java
│           ├── resources/
│           │   └── META-INF/
│           │       ├── persistence.xml
│           │       └── microprofile-config.properties
│           ├── liberty/config/
│           │   ├── server.xml        # Liberty configuration with EJB
│           │   └── bootstrap.properties
│           └── webapp/
│               ├── index.html
│               └── WEB-INF/
│                   └── web.xml
├── starter/                           # Starter code with TODOs
│   └── (same structure with TODOs)
├── test-lab.sh                        # Build verification script
├── podman-test.sh                     # Deployment and testing script
└── TESTING-GUIDE.md                   # Comprehensive testing guide
```

## 🚀 Getting Started

### Part 1: Stateless Session Bean (30 minutes)

**Objective:** Create a stateless EJB for account operations.

**Key Concepts:**
- Stateless beans don't maintain conversational state
- Pooled by container for scalability
- Thread-safe and highly concurrent
- Ideal for stateless business operations

**Tasks:**

1. **Create AccountServiceBean.java** (Stateless EJB)
   ```java
   @Stateless
   @TransactionAttribute(TransactionAttributeType.REQUIRED)
   public class AccountServiceBean {
       
       @PersistenceContext
       private EntityManager em;
       
       public void deposit(Long accountId, BigDecimal amount) {
           // TODO: Implement deposit logic
           // 1. Find account
           // 2. Validate amount > 0
           // 3. Update balance
           // 4. Create transaction record
       }
       
       public void withdraw(Long accountId, BigDecimal amount) {
           // TODO: Implement withdrawal logic
           // 1. Find account
           // 2. Validate amount > 0
           // 3. Check sufficient balance
           // 4. Update balance
           // 5. Create transaction record
       }
       
       public void transfer(Long fromId, Long toId, BigDecimal amount) {
           // TODO: Implement transfer logic
           // 1. Withdraw from source account
           // 2. Deposit to target account
           // Note: Both operations in same transaction
       }
   }
   ```

2. **Test Transaction Rollback**
   - Throw exception in transfer to test automatic rollback
   - Verify both accounts remain unchanged

**Expected Behavior:**
- All operations are transactional
- Exceptions trigger automatic rollback
- Multiple clients can use bean concurrently

---

### Part 2: Stateful Session Bean (30 minutes)

**Objective:** Create a stateful EJB for transaction batches.

**Key Concepts:**
- Maintains conversational state per client
- Dedicated instance per client session
- Useful for multi-step workflows
- Must be explicitly removed after use

**Tasks:**

1. **Create TransactionBatchBean.java** (Stateful EJB)
   ```java
   @Stateful
   public class TransactionBatchBean {
       
       private List<Transaction> pendingTransactions = new ArrayList<>();
       
       @PersistenceContext
       private EntityManager em;
       
       public void addTransaction(Transaction transaction) {
           // TODO: Add transaction to batch
           pendingTransactions.add(transaction);
       }
       
       @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
       public void commit() {
           // TODO: Persist all pending transactions
           // 1. Validate all transactions
           // 2. Persist to database
           // 3. Clear pending list
       }
       
       public void rollback() {
           // TODO: Clear pending transactions
           pendingTransactions.clear();
       }
       
       @Remove
       public void close() {
           // TODO: Cleanup resources
           pendingTransactions.clear();
       }
   }
   ```

2. **Implement Lifecycle Callbacks**
   ```java
   @PostConstruct
   public void init() {
       System.out.println("TransactionBatch created for client");
   }
   
   @PreDestroy
   public void cleanup() {
       System.out.println("TransactionBatch destroyed");
   }
   ```

**Expected Behavior:**
- Each client gets dedicated bean instance
- State maintained across multiple method calls
- @Remove method releases bean back to pool

---

### Part 3: Singleton Session Bean (20 minutes)

**Objective:** Create a singleton EJB for application configuration.

**Key Concepts:**
- Single instance per application
- Shared across all clients
- Initialized at startup
- Thread-safe with concurrency management

**Tasks:**

1. **Create ConfigServiceBean.java** (Singleton EJB)
   ```java
   @Singleton
   @Startup
   @ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
   public class ConfigServiceBean {
       
       private Map<String, String> config = new ConcurrentHashMap<>();
       
       @PostConstruct
       public void init() {
           // TODO: Load configuration
           config.put("max.transfer.amount", "10000");
           config.put("daily.withdrawal.limit", "5000");
       }
       
       @Lock(LockType.READ)
       public String getConfig(String key) {
           // TODO: Return configuration value
           return config.get(key);
       }
       
       @Lock(LockType.WRITE)
       public void updateConfig(String key, String value) {
           // TODO: Update configuration
           config.put(key, value);
       }
   }
   ```

2. **Test Concurrency**
   - Multiple threads reading configuration (READ lock)
   - Single thread updating configuration (WRITE lock)

**Expected Behavior:**
- Initialized once at startup
- Thread-safe access to configuration
- Shared across all application components

---

### Part 4: Message-Driven Bean (30 minutes)

**Objective:** Create an MDB for asynchronous notifications.

**Key Concepts:**
- Listens to JMS queues/topics
- Processes messages asynchronously
- Container-managed concurrency
- Automatic message acknowledgment

**Tasks:**

1. **Create NotificationMDB.java** (Message-Driven Bean)
   ```java
   @MessageDriven(
       activationConfig = {
           @ActivationConfigProperty(
               propertyName = "destinationType",
               propertyValue = "jakarta.jms.Queue"
           ),
           @ActivationConfigProperty(
               propertyName = "destination",
               propertyValue = "jms/notificationQueue"
           )
       }
   )
   public class NotificationMDB implements MessageListener {
       
       @Override
       public void onMessage(Message message) {
           try {
               // TODO: Process notification message
               if (message instanceof TextMessage) {
                   TextMessage textMessage = (TextMessage) message;
                   String content = textMessage.getText();
                   
                   // Parse and send notification
                   System.out.println("Sending notification: " + content);
               }
           } catch (JMSException e) {
               // TODO: Handle error
           }
       }
   }
   ```

2. **Send Messages to Queue**
   ```java
   @Inject
   @JMSConnectionFactory("jms/connectionFactory")
   private JMSContext context;
   
   @Resource(lookup = "jms/notificationQueue")
   private Queue notificationQueue;
   
   public void sendNotification(String message) {
       context.createProducer().send(notificationQueue, message);
   }
   ```

**Expected Behavior:**
- Messages processed asynchronously
- Multiple MDB instances for concurrent processing
- Automatic retry on failure

---

### Part 5: Timer Service (25 minutes)

**Objective:** Create scheduled tasks for report generation.

**Key Concepts:**
- Automatic scheduling with @Schedule
- Programmatic timers with TimerService
- Persistent or non-persistent timers
- Cron-like expressions

**Tasks:**

1. **Create ReportGeneratorBean.java** (Timer Service)
   ```java
   @Singleton
   public class ReportGeneratorBean {
       
       @PersistenceContext
       private EntityManager em;
       
       // Daily report at midnight
       @Schedule(hour = "0", minute = "0", persistent = false)
       public void generateDailyReport() {
           // TODO: Generate daily transaction report
           System.out.println("Generating daily report...");
           
           // Query transactions from last 24 hours
           // Generate report
           // Store in database
       }
       
       // Hourly account summary
       @Schedule(hour = "*", minute = "0", persistent = false)
       public void generateHourlySummary() {
           // TODO: Generate hourly summary
           System.out.println("Generating hourly summary...");
       }
       
       @Timeout
       public void handleTimeout(Timer timer) {
           // TODO: Handle programmatic timer
           System.out.println("Timer expired: " + timer.getInfo());
       }
   }
   ```

2. **Create Programmatic Timer**
   ```java
   @Resource
   private TimerService timerService;
   
   public void createCustomTimer(long duration) {
       timerService.createTimer(duration, "Custom report");
   }
   ```

**Expected Behavior:**
- Reports generated automatically at scheduled times
- No manual intervention required
- Timers survive server restarts (if persistent)

---

### Part 6: EJB Security (20 minutes)

**Objective:** Implement role-based access control.

**Key Concepts:**
- Declarative security with annotations
- Role-based method access
- Security context for programmatic checks
- Integration with application security

**Tasks:**

1. **Add Security Annotations**
   ```java
   @Stateless
   @DeclareRoles({"admin", "teller", "customer"})
   public class AccountServiceBean {
       
       @RolesAllowed({"admin", "teller"})
       public void deposit(Long accountId, BigDecimal amount) {
           // Only admin and teller can deposit
       }
       
       @RolesAllowed({"admin", "teller"})
       public void withdraw(Long accountId, BigDecimal amount) {
           // Only admin and teller can withdraw
       }
       
       @RolesAllowed({"admin"})
       public void closeAccount(Long accountId) {
           // Only admin can close accounts
       }
       
       @PermitAll
       public Account getAccount(Long accountId) {
           // Anyone can view account
       }
   }
   ```

2. **Programmatic Security**
   ```java
   @Resource
   private SessionContext ctx;
   
   public void transfer(Long fromId, Long toId, BigDecimal amount) {
       if (ctx.isCallerInRole("admin")) {
           // Admin can transfer any amount
       } else {
           // Regular users have limits
           BigDecimal limit = new BigDecimal("1000");
           if (amount.compareTo(limit) > 0) {
               throw new SecurityException("Transfer limit exceeded");
           }
       }
   }
   ```

**Expected Behavior:**
- Unauthorized access throws EJBAccessException
- Role checks enforced by container
- Security context available for custom logic

---

### Part 7: Asynchronous Methods (15 minutes)

**Objective:** Implement long-running operations asynchronously.

**Key Concepts:**
- @Asynchronous annotation
- Non-blocking execution
- Future<T> for result retrieval
- Fire-and-forget pattern

**Tasks:**

1. **Create Asynchronous Methods**
   ```java
   @Stateless
   public class AccountServiceBean {
       
       @Asynchronous
       public Future<Report> generateAccountReport(Long accountId) {
           // TODO: Generate report asynchronously
           try {
               Thread.sleep(5000); // Simulate long operation
               Report report = createReport(accountId);
               return new AsyncResult<>(report);
           } catch (Exception e) {
               return new AsyncResult<>(null);
           }
       }
       
       @Asynchronous
       public void sendWelcomeEmail(String email) {
           // TODO: Send email asynchronously (fire-and-forget)
           System.out.println("Sending welcome email to: " + email);
       }
   }
   ```

2. **Call Asynchronous Methods**
   ```java
   Future<Report> futureReport = accountService.generateAccountReport(123L);
   
   // Do other work...
   
   // Get result when ready
   if (futureReport.isDone()) {
       Report report = futureReport.get();
   }
   ```

**Expected Behavior:**
- Method returns immediately
- Execution continues in background
- Results available via Future

---

## 🧪 Testing

### Build and Test Locally

```bash
# Build the application
./test-lab.sh

# Deploy with Podman and test
./podman-test.sh
```

### Manual Testing

1. **Test Stateless EJB:**
   ```bash
   curl -X POST http://localhost:9080/lab04b/api/accounts/1/deposit \
     -H "Content-Type: application/json" \
     -d '{"amount": 100.00}'
   ```

2. **Test Transaction Batch:**
   ```bash
   # Create batch
   curl -X POST http://localhost:9080/lab04b/api/batch/create
   
   # Add transactions
   curl -X POST http://localhost:9080/lab04b/api/batch/add \
     -H "Content-Type: application/json" \
     -d '{"accountId": 1, "amount": 50.00}'
   
   # Commit batch
   curl -X POST http://localhost:9080/lab04b/api/batch/commit
   ```

3. **Test Configuration:**
   ```bash
   curl http://localhost:9080/lab04b/api/config/max.transfer.amount
   ```

4. **Send Notification:**
   ```bash
   curl -X POST http://localhost:9080/lab04b/api/notifications/send \
     -H "Content-Type: application/json" \
     -d '{"message": "Account created"}'
   ```

### Verify Scheduled Tasks

Check server logs for scheduled report generation:
```bash
podman logs lab04b-ejb | grep "Generating"
```

---

## 📊 Key Differences: EJB vs CDI

| Feature | EJB | CDI |
|---------|-----|-----|
| **Transaction Management** | Built-in CMT | Requires @Transactional |
| **Pooling** | Automatic bean pooling | No pooling |
| **Remote Access** | Supports remote clients | Local only |
| **Messaging** | MDB support | No MDB equivalent |
| **Timer Service** | Built-in scheduling | Requires external scheduler |
| **Security** | Declarative with @RolesAllowed | Requires custom implementation |
| **Asynchronous** | @Asynchronous built-in | Requires ManagedExecutorService |
| **State Management** | Stateful beans | Session-scoped beans |

**When to Use EJB:**
- Need distributed transactions
- Require message-driven processing
- Need scheduled tasks
- Want declarative security
- Building enterprise-scale applications

**When to Use CDI:**
- Building modern microservices
- Need lightweight dependency injection
- Want type-safe qualifiers
- Prefer event-driven architecture
- Building cloud-native applications

---

## 🎓 Learning Outcomes

After completing this lab, you will be able to:

✅ Implement different types of EJB session beans  
✅ Use Container-Managed Transactions effectively  
✅ Create Message-Driven Beans for asynchronous processing  
✅ Schedule tasks with the Timer Service  
✅ Apply declarative security to EJB methods  
✅ Use asynchronous methods for long-running operations  
✅ Understand when to use EJB vs CDI  
✅ Build enterprise-grade banking applications  

---

## 📚 Additional Resources

- [Jakarta EJB 4.0 Specification](https://jakarta.ee/specifications/enterprise-beans/4.0/)
- [Open Liberty EJB Guide](https://openliberty.io/docs/latest/reference/feature/ejb-3.2.html)
- [EJB Best Practices](https://www.oracle.com/technical-resources/articles/java/ejb-best-practices.html)
- [Lecture 04B: Enterprise Java Beans](../../02-Lectures/04b-ejb-enterprise-java-beans.md)

---

## 🆘 Troubleshooting

### Common Issues

1. **EJBException: Transaction rolled back**
   - Check for uncaught exceptions in business methods
   - Verify transaction attributes are correct
   - Review database constraints

2. **Message not received by MDB**
   - Verify JMS queue configuration in server.xml
   - Check activation config properties
   - Ensure message format is correct

3. **Timer not firing**
   - Check schedule expression syntax
   - Verify server time zone settings
   - Review server logs for errors

4. **Security exception**
   - Verify user roles are configured
   - Check @RolesAllowed annotations
   - Ensure security realm is properly set up

---

**Duration:** 3 hours  
**Difficulty:** Intermediate to Advanced  
**Prerequisites:** Lab 04 (CDI)

---

<!-- Made with IBM Bob -->