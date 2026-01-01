# Advanced CDI Features in Lab 05

This document describes the advanced CDI features implemented in Lab 05 (inherited from Lab 04), including **CDI Events** and **CDI Qualifiers**.

## 📋 Table of Contents

1. [CDI Events](#cdi-events)
2. [CDI Qualifiers](#cdi-qualifiers)
3. [How to Use](#how-to-use)
4. [Testing](#testing)

---

## 🎯 CDI Events

CDI Events provide a loosely-coupled, event-driven architecture where producers fire events and observers react to them.

### Event Classes

#### 1. ClientCreatedEvent
**Location**: `com.bank.event.ClientCreatedEvent`

Fired when a new client is created.

```java
public class ClientCreatedEvent {
    private final Client client;
    private final String createdBy;
    private final long timestamp;
    // ...
}
```

**Fired by**: `ClientService.create()`

#### 2. AccountCreatedEvent
**Location**: `com.bank.event.AccountCreatedEvent`

Fired when a new account is created.

```java
public class AccountCreatedEvent {
    private final Account account;
    private final Long clientId;
    private final String createdBy;
    private final long timestamp;
    // ...
}
```

**Fired by**: `AccountService.create()`

#### 3. TransactionEvent
**Location**: `com.bank.event.TransactionEvent`

Fired when a financial transaction occurs (deposit, withdrawal, transfer).

```java
public class TransactionEvent {
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER
    }
    
    private final Account account;
    private final TransactionType type;
    private final double amount;
    private final Long targetAccountId; // For transfers
    // ...
}
```

**Fired by**: 
- `AccountService.deposit()`
- `AccountService.withdraw()`
- `AccountService.transfer()`

### Event Observer

#### BankingEventObserver
**Location**: `com.bank.event.BankingEventObserver`

Observes all banking events and performs actions:

```java
@ApplicationScoped
public class BankingEventObserver {
    
    @Inject
    @Standard
    private NotificationService notificationService;
    
    // Observes client creation
    public void onClientCreated(@Observes ClientCreatedEvent event) {
        logger.info("📢 EVENT OBSERVED: Client created");
        notificationService.sendWelcomeNotification(event.getClient());
    }
    
    // Observes account creation
    public void onAccountCreated(@Observes AccountCreatedEvent event) {
        logger.info("📢 EVENT OBSERVED: Account created");
        notificationService.sendAccountCreatedNotification(event.getAccount());
    }
    
    // Observes transactions
    public void onTransaction(@Observes TransactionEvent event) {
        logger.info("📢 EVENT OBSERVED: Transaction");
        notificationService.sendTransactionNotification(...);
    }
    
    // Observes large transactions (conditional)
    public void onLargeTransaction(@Observes TransactionEvent event) {
        if (event.getAmount() > 10000) {
            logger.warning("⚠️ LARGE TRANSACTION DETECTED!");
        }
    }
}
```

### How Events Work

1. **Producer** fires an event:
   ```java
   @Inject
   private Event<ClientCreatedEvent> clientCreatedEvent;
   
   clientCreatedEvent.fire(new ClientCreatedEvent(client));
   ```

2. **Observer** reacts to the event:
   ```java
   public void onClientCreated(@Observes ClientCreatedEvent event) {
       // React to event
   }
   ```

3. **Benefits**:
   - Loose coupling between components
   - Easy to add new observers without modifying producers
   - Synchronous by default (can be made asynchronous)
   - Type-safe event handling

---

## 🏷️ CDI Qualifiers

CDI Qualifiers provide type-safe dependency injection, allowing multiple implementations of the same interface.

### Qualifier Annotations

#### 1. @Premium
**Location**: `com.bank.config.Premium`

Marks premium service implementations.

```java
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface Premium {
}
```

#### 2. @Standard
**Location**: `com.bank.config.Standard`

Marks standard service implementations.

```java
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface Standard {
}
```

### Service Interface

#### NotificationService
**Location**: `com.bank.service.NotificationService`

Interface for notification services with multiple implementations.

```java
public interface NotificationService {
    void sendWelcomeNotification(Client client);
    void sendAccountCreatedNotification(Account account);
    void sendTransactionNotification(Account account, String type, double amount);
    String getServiceLevel();
}
```

### Implementations

#### 1. PremiumNotificationService
**Location**: `com.bank.service.PremiumNotificationService`

Premium implementation with enhanced features.

```java
@ApplicationScoped
@Premium  // ← Qualifier
public class PremiumNotificationService implements NotificationService {
    
    @Override
    public void sendWelcomeNotification(Client client) {
        logger.info("🌟 PREMIUM: Sending personalized welcome");
        // Multi-channel: Email + SMS + Push + In-app
    }
    
    @Override
    public String getServiceLevel() {
        return "Premium";
    }
}
```

**Features**:
- Multi-channel notifications (Email, SMS, Push, In-app)
- Real-time transaction alerts
- Personalized messages
- Premium benefits information

#### 2. StandardNotificationService
**Location**: `com.bank.service.StandardNotificationService`

Standard implementation with basic features.

```java
@ApplicationScoped
@Standard  // ← Qualifier
public class StandardNotificationService implements NotificationService {
    
    @Override
    public void sendWelcomeNotification(Client client) {
        logger.info("📧 STANDARD: Sending welcome email");
        // Email only
    }
    
    @Override
    public String getServiceLevel() {
        return "Standard";
    }
}
```

**Features**:
- Email notifications only
- Notifications for large transactions (> $1000)
- Basic account information

### Using Qualifiers

#### Inject Premium Service:
```java
@Inject
@Premium
private NotificationService notificationService;
```

#### Inject Standard Service:
```java
@Inject
@Standard
private NotificationService notificationService;
```

#### Current Configuration:
The `BankingEventObserver` uses `@Standard` by default. To switch to Premium:

```java
// Change from:
@Inject
@Standard
private NotificationService notificationService;

// To:
@Inject
@Premium
private NotificationService notificationService;
```

---

## 🚀 How to Use

### 1. Create a Client

When you create a client via the web interface:

1. **ClientService** fires `ClientCreatedEvent`
2. **BankingEventObserver** observes the event
3. **NotificationService** (Standard or Premium) sends welcome notification
4. Logs show the complete event flow

### 2. Create an Account

When you create an account:

1. **AccountService** fires `AccountCreatedEvent`
2. **BankingEventObserver** observes the event
3. **NotificationService** sends account creation notification
4. Logs show the event and notification details

### 3. Perform a Transaction

When you deposit, withdraw, or transfer:

1. **AccountService** fires `TransactionEvent`
2. **BankingEventObserver** observes the event
3. **NotificationService** sends transaction notification (based on service level)
4. If amount > $10,000, additional warning is logged
5. Logs show complete transaction flow

---

## 🧪 Testing

### View Event Logs

```bash
# Start the application
cd solution
./podman-test.sh

# View logs with event information
podman logs banking-cdi-lab04 | grep "EVENT OBSERVED"
podman logs banking-cdi-lab04 | grep "PREMIUM\|STANDARD"
```

### Test Scenarios

#### Scenario 1: Create Client
1. Navigate to http://localhost:9080/banking-cdi-app/
2. Click "Add New Client"
3. Fill in client details
4. Submit
5. Check logs for:
   - `📢 EVENT OBSERVED: Client created`
   - `📧 STANDARD: Sending welcome email` (or `🌟 PREMIUM`)

#### Scenario 2: Create Account
1. View a client's details
2. Click "Add Account"
3. Fill in account details
4. Submit
5. Check logs for:
   - `📢 EVENT OBSERVED: Account created`
   - Notification service logs

#### Scenario 3: Deposit Money
1. View an account's details
2. Enter deposit amount
3. Submit
4. Check logs for:
   - `📢 EVENT OBSERVED: Transaction`
   - Transaction type: DEPOSIT
   - Notification logs

#### Scenario 4: Large Transaction
1. Deposit or withdraw > $10,000
2. Check logs for:
   - `⚠️ LARGE TRANSACTION DETECTED!`
   - Additional compliance logging

### Switch to Premium Service

To test Premium notifications:

1. Edit `BankingEventObserver.java`
2. Change `@Standard` to `@Premium`
3. Rebuild: `mvn clean package`
4. Restart container
5. Perform operations and observe enhanced notifications

---

## 📊 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Web Layer (Controllers)                   │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────┐
│                  Service Layer (CDI Beans)                   │
│                                                               │
│  ClientService          AccountService                       │
│  @ApplicationScoped     @ApplicationScoped                   │
│                                                               │
│  create() ──────→ Fire Event ──────→ ClientCreatedEvent     │
│  update()                            AccountCreatedEvent     │
│  delete()                            TransactionEvent        │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────┐
│                  Event Observer Layer                        │
│                                                               │
│  BankingEventObserver                                        │
│  @ApplicationScoped                                          │
│                                                               │
│  onClientCreated(@Observes ClientCreatedEvent)              │
│  onAccountCreated(@Observes AccountCreatedEvent)            │
│  onTransaction(@Observes TransactionEvent)                  │
│  onLargeTransaction(@Observes TransactionEvent)             │
│                                                               │
│  Uses: @Inject @Standard NotificationService                │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────┐
│              Notification Service (Qualifiers)               │
│                                                               │
│  ┌──────────────────────┐    ┌──────────────────────┐      │
│  │ @Standard            │    │ @Premium             │      │
│  │ StandardNotification │    │ PremiumNotification  │      │
│  │ Service              │    │ Service              │      │
│  │                      │    │                      │      │
│  │ - Email only         │    │ - Multi-channel      │      │
│  │ - Large tx only      │    │ - All transactions   │      │
│  │ - Basic info         │    │ - Enhanced features  │      │
│  └──────────────────────┘    └──────────────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎓 Learning Outcomes

After exploring these advanced CDI features, you will understand:

1. **CDI Events**:
   - How to fire events from producers
   - How to observe events with `@Observes`
   - Synchronous vs asynchronous event handling
   - Conditional event observation
   - Benefits of event-driven architecture

2. **CDI Qualifiers**:
   - How to create custom qualifiers
   - Type-safe dependency injection
   - Multiple implementations of same interface
   - Runtime selection of implementations
   - Benefits of qualifier-based injection

3. **Best Practices**:
   - Loose coupling through events
   - Separation of concerns
   - Extensibility without modification
   - Type-safe configuration
   - Clean architecture patterns

---

## 📚 Additional Resources

- [Jakarta CDI Specification - Events](https://jakarta.ee/specifications/cdi/4.0/jakarta-cdi-spec-4.0.html#events)
- [Jakarta CDI Specification - Qualifiers](https://jakarta.ee/specifications/cdi/4.0/jakarta-cdi-spec-4.0.html#qualifiers)
- [Open Liberty CDI Guide](https://openliberty.io/docs/latest/cdi-beans.html)

---

**Made with Bob - Lab 05: JAX-RS & RESTful Services** 🚀