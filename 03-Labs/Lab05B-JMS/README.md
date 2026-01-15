<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 05B: JMS Enterprise Messaging

## Overview

This lab introduces **Jakarta Messaging (JMS) 3.1** for asynchronous communication in enterprise applications. You'll implement a complete messaging solution for the banking application, including message producers, Message-Driven Beans (MDBs), transaction management, and error handling.

## Learning Objectives

By completing this lab, you will:

1. **Configure JMS resources** (ConnectionFactory, Queues, Topics)
2. **Implement message producers** to send asynchronous events
3. **Create Message-Driven Beans (MDBs)** for asynchronous processing
4. **Handle transactions** with JMS messages
5. **Implement error handling** and Dead Letter Queues (DLQ)
6. **Test messaging scenarios** including failure cases

## Prerequisites

- Completed Lab 04 (CDI)
- Understanding of asynchronous communication
- Basic knowledge of transactions
- Podman or Docker installed

## Architecture

```
┌─────────────────┐
│   Web Layer     │
│   (Servlet)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐      ┌──────────────────┐
│  Service Layer  │─────►│  JMS Producer    │
│  (CDI Beans)    │      │  (Send Messages) │
└─────────────────┘      └────────┬─────────┘
                                  │
                                  ▼
                         ┌────────────────┐
                         │  JMS Broker    │
                         │  (Queues/Topics)│
                         └────────┬───────┘
                                  │
                    ┌─────────────┼─────────────┐
                    ▼             ▼             ▼
            ┌───────────┐  ┌──────────┐  ┌──────────┐
            │ Email MDB │  │ Audit MDB│  │  DLQ MDB │
            └───────────┘  └──────────┘  └──────────┘
```

## Lab Structure

```
Lab05B-JMS/
├── README.md                    # This file
├── TESTING-GUIDE.md            # Testing instructions
├── podman-test.sh              # Container testing script
├── test-lab.sh                 # Local testing script
├── solution/                   # Complete solution
│   ├── pom.xml
│   ├── Containerfile
│   └── src/main/
│       ├── java/com/bank/
│       │   ├── event/          # Event classes
│       │   ├── mdb/            # Message-Driven Beans
│       │   ├── model/          # Domain entities
│       │   ├── producer/       # Message producers
│       │   ├── service/        # Business services
│       │   └── web/            # Servlets
│       ├── liberty/config/
│       │   └── server.xml      # JMS configuration
│       ├── resources/
│       │   └── META-INF/
│       │       └── persistence.xml
│       └── webapp/
│           ├── index.html
│           └── WEB-INF/
│               └── web.xml
└── starter/                    # Starter code with TODOs
    └── (same structure)
```

## Part A: JMS Configuration (20 minutes)

### Objectives
- Configure JMS ConnectionFactory
- Define Queues and Topics
- Configure Dead Letter Queue

### Tasks

1. **Configure server.xml** with JMS resources:

```xml
<server>
    <featureManager>
        <feature>messaging-3.1</feature>
        <feature>mdb-4.0</feature>
        <feature>cdi-4.0</feature>
        <feature>persistence-3.1</feature>
        <feature>servlet-6.0</feature>
    </featureManager>
    
    <!-- JMS Connection Factory -->
    <jmsConnectionFactory jndiName="jms/connectionFactory">
        <properties.wasJms/>
    </jmsConnectionFactory>
    
    <!-- Transaction Event Queue -->
    <jmsQueue id="transactionQueue" jndiName="jms/transactionQueue">
        <properties.wasJms 
            deliveryMode="Persistent"
            maxRedeliveryCount="5"
            redeliveryDelay="5000"/>
    </jmsQueue>
    
    <!-- Email Notification Queue -->
    <jmsQueue id="emailQueue" jndiName="jms/emailQueue">
        <properties.wasJms deliveryMode="Persistent"/>
    </jmsQueue>
    
    <!-- Audit Topic -->
    <jmsTopic id="auditTopic" jndiName="jms/auditTopic">
        <properties.wasJms/>
    </jmsTopic>
    
    <!-- Dead Letter Queue -->
    <jmsQueue id="deadLetterQueue" jndiName="jms/deadLetterQueue">
        <properties.wasJms deliveryMode="Persistent"/>
    </jmsQueue>
</server>
```

2. **Verify configuration** by checking Liberty logs

### Expected Output
```
[AUDIT   ] CWWKF0011I: The defaultServer server is ready to run a smarter planet.
[AUDIT   ] CWWKG0028A: Processing included configuration resource: /opt/ol/wlp/usr/servers/defaultServer/configDropins/defaults/messaging.xml
```

## Part B: Transaction Event Producer (25 minutes)

### Objectives
- Create event classes for transactions
- Implement JMS producer with CDI
- Send messages on transaction events

### Tasks

1. **Create TransactionEvent class**:

```java
package com.bank.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long transactionId;
    private Long accountId;
    private String accountNumber;
    private BigDecimal amount;
    private String type; // DEPOSIT, WITHDRAWAL, TRANSFER
    private LocalDateTime timestamp;
    private String status; // SUCCESS, FAILED
    
    // Constructors, getters, setters
}
```

2. **Create TransactionEventProducer**:

```java
package com.bank.producer;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.*;

@ApplicationScoped
public class TransactionEventProducer {
    
    @Inject
    @JMSConnectionFactory("jms/connectionFactory")
    private JMSContext context;
    
    @Resource(lookup = "jms/transactionQueue")
    private Queue transactionQueue;
    
    @Resource(lookup = "jms/auditTopic")
    private Topic auditTopic;
    
    public void sendTransactionEvent(TransactionEvent event) {
        // TODO: Send event to queue
        // TODO: Set message properties
        // TODO: Handle exceptions
    }
    
    public void publishAuditEvent(TransactionEvent event) {
        // TODO: Publish to topic
    }
}
```

3. **Integrate with AccountService**:

```java
@Stateless
public class AccountService {
    
    @Inject
    private TransactionEventProducer eventProducer;
    
    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        // Perform deposit
        account.deposit(amount);
        em.merge(account);
        
        // Send async event
        TransactionEvent event = new TransactionEvent(/* ... */);
        eventProducer.sendTransactionEvent(event);
    }
}
```

### Expected Output
- Messages sent to queue successfully
- No exceptions in logs
- Messages visible in JMS broker

## Part C: Email Notification MDB (30 minutes)

### Objectives
- Create MDB to process email notifications
- Implement message filtering with selectors
- Handle different message types

### Tasks

1. **Create EmailNotificationMDB**:

```java
package com.bank.mdb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.*;
import java.util.logging.Logger;

@MessageDriven(
    activationConfig = {
        @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue"
        ),
        @ActivationConfigProperty(
            propertyName = "destination",
            propertyValue = "jms/emailQueue"
        ),
        @ActivationConfigProperty(
            propertyName = "messageSelector",
            propertyValue = "notificationType = 'EMAIL'"
        )
    }
)
public class EmailNotificationMDB implements MessageListener {
    
    @Inject
    private Logger logger;
    
    @Inject
    private EmailService emailService;
    
    @Override
    public void onMessage(Message message) {
        // TODO: Extract TransactionEvent from message
        // TODO: Send email notification
        // TODO: Handle errors
    }
}
```

2. **Create EmailService**:

```java
package com.bank.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.logging.Logger;

@ApplicationScoped
public class EmailService {
    
    @Inject
    private Logger logger;
    
    public void sendTransactionNotification(TransactionEvent event) {
        // TODO: Simulate email sending
        logger.info("Sending email for transaction: " + event.getTransactionId());
        
        // Simulate processing time
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### Expected Output
```
[INFO] EmailNotificationMDB: Processing transaction event: 12345
[INFO] EmailService: Sending email for transaction: 12345
[INFO] Email sent successfully
```

## Part D: Audit Logging MDB (25 minutes)

### Objectives
- Create MDB for audit topic
- Implement durable subscription
- Log all transaction events

### Tasks

1. **Create AuditLoggingMDB**:

```java
package com.bank.mdb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@MessageDriven(
    activationConfig = {
        @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "jakarta.jms.Topic"
        ),
        @ActivationConfigProperty(
            propertyName = "destination",
            propertyValue = "jms/auditTopic"
        ),
        @ActivationConfigProperty(
            propertyName = "subscriptionDurability",
            propertyValue = "Durable"
        ),
        @ActivationConfigProperty(
            propertyName = "clientId",
            propertyValue = "AuditClient"
        ),
        @ActivationConfigProperty(
            propertyName = "subscriptionName",
            propertyValue = "AuditSubscription"
        )
    }
)
public class AuditLoggingMDB implements MessageListener {
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public void onMessage(Message message) {
        // TODO: Extract event
        // TODO: Create AuditLog entity
        // TODO: Persist to database
    }
}
```

2. **Create AuditLog entity**:

```java
package com.bank.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private String transactionType;
    private LocalDateTime timestamp;
    private String status;
    
    // Getters, setters
}
```

### Expected Output
```
[INFO] AuditLoggingMDB: Logging transaction: 12345
[INFO] Audit log persisted with ID: 1
```

## Part E: Dead Letter Queue Handler (20 minutes)

### Objectives
- Create DLQ handler MDB
- Log failed messages
- Implement retry logic

### Tasks

1. **Create DeadLetterQueueMDB**:

```java
package com.bank.mdb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.logging.Logger;

@MessageDriven(
    activationConfig = {
        @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue"
        ),
        @ActivationConfigProperty(
            propertyName = "destination",
            propertyValue = "jms/deadLetterQueue"
        )
    }
)
public class DeadLetterQueueMDB implements MessageListener {
    
    @Inject
    private Logger logger;
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public void onMessage(Message message) {
        try {
            String messageId = message.getJMSMessageID();
            int deliveryCount = message.getIntProperty("JMSXDeliveryCount");
            
            logger.severe("Message failed after " + deliveryCount + 
                         " attempts: " + messageId);
            
            // TODO: Store failed message in database
            // TODO: Send alert notification
            
        } catch (JMSException e) {
            logger.severe("Error processing DLQ message: " + e.getMessage());
        }
    }
}
```

2. **Create FailedMessage entity**:

```java
package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "failed_messages")
public class FailedMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String messageId;
    private Integer deliveryCount;
    
    @Lob
    private String content;
    
    private LocalDateTime failureTime;
    private String errorMessage;
    
    // Getters, setters
}
```

### Expected Output
```
[SEVERE] DeadLetterQueueMDB: Message failed after 5 attempts: ID:414d51...
[INFO] Failed message stored in database
```

## Part F: Testing and Verification (20 minutes)

### Objectives
- Test message sending
- Verify MDB processing
- Test error scenarios

### Tasks

1. **Create test servlet**:

```java
package com.bank.web;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/test-messaging")
public class MessagingTestServlet extends HttpServlet {
    
    @Inject
    private TransactionEventProducer producer;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Test 1: Send normal transaction event
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(12345L);
        event.setAmount(new BigDecimal("100.00"));
        event.setType("DEPOSIT");
        
        producer.sendTransactionEvent(event);
        
        resp.getWriter().println("Test message sent!");
    }
}
```

2. **Run tests**:

```bash
# Start Liberty server
./test-lab.sh

# Or use container
./podman-test.sh

# Access test page
curl http://localhost:9080/banking-jms-app/test-messaging
```

3. **Verify in logs**:

```
[INFO] TransactionEventProducer: Sending event to queue
[INFO] EmailNotificationMDB: Processing transaction event: 12345
[INFO] AuditLoggingMDB: Logging transaction: 12345
```

### Expected Output
- Messages sent successfully
- MDBs process messages
- Audit logs created in database
- No errors in Liberty logs

## Testing

### Local Testing

```bash
# Build and run
./test-lab.sh

# Access application
open http://localhost:9080/banking-jms-app/
```

### Container Testing

```bash
# Build and run in container
./podman-test.sh

# View logs
podman logs -f banking-jms-app
```

### Manual Testing

1. **Send Transaction Event**:
   - Navigate to `/test-messaging`
   - Verify message sent in logs

2. **Check MDB Processing**:
   - Look for MDB log messages
   - Verify database entries

3. **Test Error Handling**:
   - Send invalid message
   - Verify DLQ processing

## Verification Checklist

- [ ] JMS resources configured in server.xml
- [ ] TransactionEventProducer sends messages
- [ ] EmailNotificationMDB processes messages
- [ ] AuditLoggingMDB logs to database
- [ ] DeadLetterQueueMDB handles failures
- [ ] All tests pass
- [ ] No errors in Liberty logs

## Common Issues

### Issue 1: Messages Not Received

**Symptom**: MDB not processing messages

**Solution**:
- Check queue/topic JNDI names
- Verify MDB activation config
- Check Liberty messaging feature enabled

### Issue 2: Transaction Rollback

**Symptom**: Messages redelivered multiple times

**Solution**:
- Check for exceptions in onMessage()
- Verify transaction configuration
- Implement proper error handling

### Issue 3: DLQ Not Working

**Symptom**: Failed messages not in DLQ

**Solution**:
- Check maxRedeliveryCount configuration
- Verify DLQ JNDI name
- Check Liberty messaging configuration

## Additional Resources

- [Jakarta Messaging 3.1 Specification](https://jakarta.ee/specifications/messaging/3.1/)
- [Open Liberty Messaging](https://openliberty.io/docs/latest/reference/feature/messaging-3.1.html)
- [Message-Driven Beans Guide](https://openliberty.io/docs/latest/reference/feature/mdb-4.0.html)
- [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/)

## Next Steps

After completing this lab:
1. Review Lecture 5B slides
2. Experiment with different message types
3. Implement message selectors
4. Try request-reply pattern
5. Explore MicroProfile Reactive Messaging

---

**Estimated Time**: 2 hours 20 minutes

**Difficulty**: Intermediate

**Prerequisites**: Lab 04 (CDI) completed