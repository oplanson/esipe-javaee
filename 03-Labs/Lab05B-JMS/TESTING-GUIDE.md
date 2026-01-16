<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 05B: JMS Enterprise Messaging - Testing Guide

## Overview

This guide provides comprehensive testing procedures for Lab 05B, covering local development testing, container-based testing, and manual verification of JMS messaging functionality.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Testing](#local-testing)
3. [Container Testing](#container-testing)
4. [Manual Testing](#manual-testing)
5. [Test Scenarios](#test-scenarios)
6. [Troubleshooting](#troubleshooting)
7. [Performance Testing](#performance-testing)

---

## Prerequisites

### Required Software

- **Java 17+**: OpenJDK or Oracle JDK
- **Maven 3.8+**: Build tool
- **Podman or Docker**: Container runtime
- **PostgreSQL 16**: Database (for local testing)
- **curl**: HTTP client for automated tests

### Verify Installation

```bash
java -version
mvn -version
podman --version
psql --version
curl --version
```

---

## Local Testing

### 1. Quick Build Verification

Run the automated test script:

```bash
cd Lab05B-JMS
./test-lab.sh
```

**Expected Output:**
```
╔════════════════════════════════════════════════════════════╗
║   Lab 05B - JMS Banking Application - Local Testing       ║
╚════════════════════════════════════════════════════════════╝

Checking prerequisites...
✓ Maven found: Apache Maven 3.9.x
✓ Java found: openjdk version "17.x.x"

Cleaning previous builds...
✓ Clean complete

Compiling application...
✓ PASS - Compilation

Running unit tests...
✓ PASS - Unit Tests

Packaging application...
✓ PASS - Packaging (WAR creation)

Verifying build artifacts...
✓ PASS - WAR file exists (Size: 15M)

Verifying JMS components in WAR...
✓ PASS - TransactionEvent class
✓ PASS - TransactionEventProducer class
✓ PASS - EmailNotificationMDB class
✓ PASS - AuditLoggingMDB class
✓ PASS - DeadLetterQueueMDB class
✓ PASS - TransactionEventMDB class
✓ PASS - AuditLog entity
✓ PASS - FailedMessage entity
✓ PASS - EmailService class
✓ PASS - MessagingTestServlet class

Verifying configuration files...
✓ PASS - JMS feature in server.xml
✓ PASS - JMS ConnectionFactory configured
✓ PASS - Transaction Queue configured
✓ PASS - Audit Topic configured
✓ PASS - persistence.xml exists

╔════════════════════════════════════════════════════════════╗
║                     TEST SUMMARY                           ║
╚════════════════════════════════════════════════════════════╝
Total Tests: 18
Passed: 18
Failed: 0

╔════════════════════════════════════════════════════════════╗
║              ✓ ALL TESTS PASSED!                           ║
╚════════════════════════════════════════════════════════════╝
```

### 2. Manual Local Testing

#### Step 1: Start PostgreSQL

```bash
# Using Podman
podman run -d \
  --name banking-jms-db \
  -e POSTGRES_DB=bankingdb \
  -e POSTGRES_USER=bankuser \
  -e POSTGRES_PASSWORD=bankpass \
  -p 5432:5432 \
  postgres:16-alpine

# Verify database is running
podman exec banking-jms-db pg_isready -U bankuser -d bankingdb
```

#### Step 2: Start Liberty in Dev Mode

```bash
cd solution
mvn liberty:dev
```

**Expected Output:**
```
[INFO] CWWKF0011I: The defaultServer server is ready to run a smarter planet.
[INFO] CWWKZ0001I: Application banking-jms-app started in 12.345 seconds.
```

#### Step 3: Access Application

Open browser to: http://localhost:9080

---

## Container Testing

### 1. Automated Container Testing

Run the comprehensive Podman test script:

```bash
cd Lab05B-JMS
./podman-test.sh
```

**Expected Output:**
```
╔════════════════════════════════════════════════════════════╗
║   Lab 05B - JMS Banking - Podman Container Testing        ║
╚════════════════════════════════════════════════════════════╝

Checking prerequisites...
✓ Podman found: podman version 4.x.x
✓ curl found

Creating container network...
✓ Network created: banking-jms-network

Starting PostgreSQL database...
✓ PASS - PostgreSQL database started

Building application image...
✓ PASS - Application image build

Starting application container...
Waiting for service at http://localhost:9080/health...
✓ Service is ready
✓ PASS - Application container started

╔════════════════════════════════════════════════════════════╗
║                  AUTOMATED TESTS                           ║
╚════════════════════════════════════════════════════════════╝

Test 1: Health Check
✓ PASS - Health check endpoint

Test 2: Home Page
✓ PASS - Home page accessible

Test 3: Test Messaging Page
✓ PASS - Test messaging page accessible

Test 4: Send Deposit Event
✓ PASS - Deposit event sent

Test 5: Send Withdrawal Event
✓ PASS - Withdrawal event sent

Test 6: Send Transfer Event
✓ PASS - Transfer event sent

Test 7: Send Email Notification
✓ PASS - Email notification sent

Test 8: Publish Audit Event
✓ PASS - Audit event published

Test 9: Send All Events
✓ PASS - All events sent

Test 10: Batch Processing (10 messages)
✓ PASS - Batch messages sent

Test 11: Verify Audit Logs in Database
✓ PASS - Audit logs persisted (Count: 15)

Test 12: Verify MDB Processing in Logs
✓ PASS - EmailNotificationMDB processed messages
✓ PASS - AuditLoggingMDB processed messages
✓ PASS - TransactionEventMDB processed messages

╔════════════════════════════════════════════════════════════╗
║                     TEST SUMMARY                           ║
╚════════════════════════════════════════════════════════════╝
Total Tests: 17
Passed: 17
Failed: 0

╔════════════════════════════════════════════════════════════╗
║              ✓ ALL TESTS PASSED!                           ║
╚════════════════════════════════════════════════════════════╝
```

### 2. Docker Compose Testing

```bash
cd solution
docker-compose up -d

# Wait for services to start
sleep 30

# Check services
docker-compose ps

# View logs
docker-compose logs -f banking-app

# Stop services
docker-compose down
```

---

## Manual Testing

### Test Messaging Interface

1. **Access Test Page**
   ```
   http://localhost:9080/test-messaging
   ```

2. **Available Tests:**
   - Test Deposit Event
   - Test Withdrawal Event
   - Test Transfer Event
   - Test Email Notification
   - Test Audit Event
   - Test All Events
   - Test Batch (10 messages)

### Verify MDB Processing

#### Check Liberty Logs

```bash
# Local testing
tail -f target/liberty/wlp/usr/servers/lab05bServer/logs/messages.log

# Container testing
podman logs -f banking-jms-app
```

**Expected Log Entries:**

```
[INFO] TransactionEventProducer: Sending event to queue
[INFO] TransactionEventMDB: Received message
[INFO] TransactionEventMDB: Processing DEPOSIT transaction: 1001
[INFO] EmailNotificationMDB: Processing transaction event: 1001
[INFO] EmailService: Sending email for transaction: 1001
[INFO] AuditLoggingMDB: Logging transaction: 1001
[INFO] Audit log persisted successfully: ID=1, Transaction=1001
```

#### Check Database

```bash
# Connect to database
podman exec -it banking-jms-db psql -U bankuser -d bankingdb

# Query audit logs
SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT 10;

# Query failed messages (should be empty)
SELECT * FROM failed_messages;

# Exit
\q
```

---

## Test Scenarios

### Scenario 1: Single Transaction Event

**Objective:** Verify basic message sending and MDB processing

**Steps:**
1. Navigate to http://localhost:9080/test-messaging/deposit
2. Verify success message displayed
3. Check Liberty logs for MDB processing
4. Verify audit log in database

**Expected Result:**
- HTTP 200 response
- TransactionEventMDB processes message
- AuditLoggingMDB creates database entry
- No errors in logs

### Scenario 2: Email Notification

**Objective:** Verify email queue and message selector

**Steps:**
1. Navigate to http://localhost:9080/test-messaging/email
2. Verify success message
3. Check logs for EmailNotificationMDB processing
4. Verify email simulation logs

**Expected Result:**
- EmailNotificationMDB receives message
- Message selector filters correctly
- Email service simulates sending

### Scenario 3: Audit Topic Subscription

**Objective:** Verify durable topic subscription

**Steps:**
1. Navigate to http://localhost:9080/test-messaging/audit
2. Verify success message
3. Check database for audit log entry
4. Restart application
5. Send another audit event
6. Verify both events are logged

**Expected Result:**
- Durable subscription persists across restarts
- All audit events are captured
- No message loss

### Scenario 4: Batch Processing

**Objective:** Verify concurrent message processing

**Steps:**
1. Navigate to http://localhost:9080/test-messaging/batch
2. Wait for all messages to process (5-10 seconds)
3. Check logs for 10 transaction events
4. Verify database has 10 audit entries

**Expected Result:**
- All 10 messages processed
- MDBs handle concurrent messages
- No message loss or duplication

### Scenario 5: Dead Letter Queue

**Objective:** Verify DLQ handling (requires manual error injection)

**Steps:**
1. Modify MDB to throw exception
2. Send message
3. Verify redelivery attempts (5 times)
4. Check DLQ for failed message
5. Verify FailedMessage entity in database

**Expected Result:**
- Message redelivered 5 times
- Message moved to DLQ
- DeadLetterQueueMDB logs failure
- FailedMessage persisted

---

## Troubleshooting

### Issue 1: Messages Not Received by MDB

**Symptoms:**
- Messages sent successfully
- No MDB processing logs
- No errors

**Solutions:**
1. Check JMS feature enabled in server.xml:
   ```xml
   <feature>messaging-3.1</feature>
   <feature>mdb-4.0</feature>
   ```

2. Verify queue/topic JNDI names match:
   ```java
   @Resource(lookup = "jms/transactionQueue")
   ```

3. Check MDB activation config:
   ```java
   @ActivationConfigProperty(
       propertyName = "destination",
       propertyValue = "jms/transactionQueue"
   )
   ```

4. Restart Liberty server

### Issue 2: Database Connection Failed

**Symptoms:**
- Application starts but database operations fail
- JPA errors in logs

**Solutions:**
1. Verify PostgreSQL is running:
   ```bash
   podman ps | grep postgres
   ```

2. Check database credentials in bootstrap.properties

3. Test database connection:
   ```bash
   podman exec banking-jms-db psql -U bankuser -d bankingdb -c "SELECT 1;"
   ```

4. Check server.xml dataSource configuration

### Issue 3: Audit Logs Not Persisted

**Symptoms:**
- AuditLoggingMDB processes messages
- No database entries

**Solutions:**
1. Check persistence.xml configuration
2. Verify EntityManager injection
3. Check transaction management
4. Review database schema creation

### Issue 4: Container Build Fails

**Symptoms:**
- Podman build errors
- Missing dependencies

**Solutions:**
1. Clean Maven cache:
   ```bash
   mvn clean
   rm -rf ~/.m2/repository/com/bank
   ```

2. Rebuild with verbose output:
   ```bash
   podman build --no-cache -t banking-jms-app:latest .
   ```

3. Check Containerfile syntax

4. Verify base image availability

---

## Performance Testing

### Load Testing with curl

```bash
# Send 100 messages rapidly
for i in {1..100}; do
  curl -s http://localhost:9080/test-messaging/deposit > /dev/null &
done
wait

# Check processing time in logs
```

### Monitor JMS Queue Depth

```bash
# View Liberty metrics
curl http://localhost:9080/metrics | grep jms

# Expected metrics:
# - Queue depth
# - Message count
# - Processing time
```

### Database Performance

```sql
-- Check audit log count
SELECT COUNT(*) FROM audit_logs;

-- Check average processing time
SELECT 
  AVG(EXTRACT(EPOCH FROM (created_at - timestamp))) as avg_delay_seconds
FROM audit_logs;

-- Check failed messages
SELECT COUNT(*) FROM failed_messages;
```

---

## Test Checklist

### Build & Deployment
- [ ] Application compiles without errors
- [ ] WAR file created successfully
- [ ] All JMS classes present in WAR
- [ ] Configuration files valid
- [ ] Container image builds successfully
- [ ] Application starts in container

### JMS Functionality
- [ ] Messages sent to queue successfully
- [ ] TransactionEventMDB processes messages
- [ ] EmailNotificationMDB processes messages
- [ ] AuditLoggingMDB processes messages
- [ ] DeadLetterQueueMDB handles failures
- [ ] Message selectors work correctly
- [ ] Durable subscriptions persist

### Database Integration
- [ ] Audit logs persisted correctly
- [ ] Failed messages logged
- [ ] Database schema created
- [ ] Transactions committed properly

### Error Handling
- [ ] Message redelivery works
- [ ] DLQ captures failed messages
- [ ] Error logs are comprehensive
- [ ] Application recovers from errors

### Performance
- [ ] Batch processing handles 10+ messages
- [ ] No message loss under load
- [ ] MDB concurrency works correctly
- [ ] Database performance acceptable

---

## Additional Resources

- [Jakarta Messaging 3.1 Specification](https://jakarta.ee/specifications/messaging/3.1/)
- [Open Liberty Messaging Documentation](https://openliberty.io/docs/latest/reference/feature/messaging-3.1.html)
- [MDB Configuration Guide](https://openliberty.io/docs/latest/reference/feature/mdb-4.0.html)
- [Lab 05B README](README.md)

---

**Last Updated:** January 16, 2026  
**Version:** 1.0  
**Author:** Olivier Planson

<!-- Made with Bob -->