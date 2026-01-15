# Lab 04B - EJB Banking Application Testing Guide

© Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

## Overview

This guide provides comprehensive testing instructions for Lab 04B - Enterprise Java Beans (EJB) Banking Application.

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 16 (or Docker/Podman for containerized testing)
- Podman or Docker (for container testing)

## Testing Methods

### Method 1: Local Testing with Maven

#### 1. Start PostgreSQL

```bash
# Using Docker
docker run -d \
  --name lab04b-postgres \
  -e POSTGRES_DB=bankingdb \
  -e POSTGRES_USER=bankuser \
  -e POSTGRES_PASSWORD=bankpass \
  -p 5432:5432 \
  postgres:16-alpine

# Or using Podman
podman run -d \
  --name lab04b-postgres \
  -e POSTGRES_DB=bankingdb \
  -e POSTGRES_USER=bankuser \
  -e POSTGRES_PASSWORD=bankpass \
  -p 5432:5432 \
  postgres:16-alpine
```

#### 2. Build and Run

```bash
cd solution
mvn clean package
mvn liberty:run
```

#### 3. Access the Application

- **Home Page**: http://localhost:9080/
- **Banking Operations**: http://localhost:9080/banking
- **Health Check**: http://localhost:9080/health
- **Metrics**: http://localhost:9080/metrics

#### 4. Test with Credentials

```bash
# Test with admin user
curl -u admin:admin123 "http://localhost:9080/banking?action=create"

# Test with teller user
curl -u teller:teller123 "http://localhost:9080/banking"

# Test with customer user (read-only)
curl -u customer:customer123 "http://localhost:9080/banking"
```

### Method 2: Automated Local Testing

```bash
./test-lab.sh
```

This script will:
1. Check prerequisites (Maven, PostgreSQL)
2. Start PostgreSQL if needed
3. Build the application with Maven
4. Start Liberty server
5. Run comprehensive automated tests:
   - **Test 1**: Create first account (Stateless EJB)
   - **Test 2**: Create second account
   - **Test 3**: Verify accounts list
   - **Test 4**: Deposit operation (CMT)
   - **Test 5**: Withdrawal operation
   - **Test 6**: Transfer operation (Transaction Management)
   - **Test 7**: Verify account balances
   - **Test 8**: Singleton EJB (ConfigServiceBean)
   - **Test 9**: Timer Service (ReportGeneratorBean)
   - **Test 10**: EJB Security (Role-based access)
6. Display test summary with pass/fail counts
7. Keep server running for manual testing

### Method 3: Container Testing with Podman (Recommended)

```bash
./podman-test.sh
```

This script provides the most comprehensive testing and will:
1. **Cleanup Phase**: Remove existing containers and networks
2. **Build Phase**: Build application with Maven
3. **Network Setup**: Create isolated Podman network
4. **Database Setup**: Start PostgreSQL container
5. **Container Build**: Build Liberty container image
6. **Container Start**: Start Liberty container with environment variables
7. **Comprehensive Testing**:
   - **Test 1**: Create first account (Stateless EJB - AccountServiceBean)
   - **Test 2**: Create second account
   - **Test 3**: Verify accounts list
   - **Test 4**: Deposit operation (Container-Managed Transaction)
   - **Test 5**: Withdrawal operation
   - **Test 6**: Transfer operation (Transaction Management)
   - **Test 7**: Verify account balances after operations
   - **Test 8**: Singleton EJB (ConfigServiceBean)
   - **Test 9**: Timer Service (ReportGeneratorBean)
   - **Test 10**: EJB Security (Role-based access)
   - **Test 11**: Database persistence verification
   - **Test 12**: JMS configuration check (for NotificationMDB)
8. **Results**: Display test summary with pass/fail counts
9. **Management Info**: Provide container management commands

**Expected Output:**
```
========================================
Test Summary
========================================
Tests Passed: 12
Tests Failed: 0
Total Tests: 12

✓ All tests passed!
```

### Method 4: Docker Compose

```bash
cd solution
docker-compose up -d
```

Access the application at http://localhost:9080/

To stop:
```bash
docker-compose down
```

## Testing EJB Components

### 1. Stateless Session Bean (AccountServiceBean)

Test account operations:

```bash
# Create account
curl -u admin:admin123 "http://localhost:9080/banking?action=create"

# Deposit money
curl -u teller:teller123 -X POST \
  "http://localhost:9080/banking?action=deposit&accountId=1&amount=1000"

# Withdraw money
curl -u teller:teller123 -X POST \
  "http://localhost:9080/banking?action=withdraw&accountId=1&amount=500"

# Transfer money
curl -u admin:admin123 -X POST \
  "http://localhost:9080/banking?action=transfer&fromId=1&toId=2&amount=200"
```

### 2. Stateful Session Bean (TransactionBatchBean)

The batch processing is demonstrated in the solution code. Students will implement:
- Adding multiple transactions to a batch
- Committing the batch
- Rolling back if needed

### 3. Singleton Session Bean (ConfigServiceBean)

Test configuration management:

```bash
# View configuration (check server logs)
# The singleton initializes on startup with default config
```

### 4. Message-Driven Bean (NotificationMDB)

The MDB processes JMS messages asynchronously. Check server logs for:
```
[INFO] Processing notification: Account created
[INFO] Processing notification: Deposit completed
```

### 5. Timer Service (ReportGeneratorBean)

The timer service runs scheduled tasks. Check server logs for:
```
[INFO] === Generating Daily Report ===
[INFO] === Generating Hourly Summary ===
[INFO] === Generating Weekly Report ===
```

## Health Checks

### Database Health

```bash
curl http://localhost:9080/health/live
```

Expected response:
```json
{
  "status": "UP",
  "checks": [
    {
      "name": "database-connection",
      "status": "UP",
      "data": {
        "database": "PostgreSQL",
        "status": "connected"
      }
    }
  ]
}
```

### EJB Container Health

```bash
curl http://localhost:9080/health/ready
```

Expected response:
```json
{
  "status": "UP",
  "checks": [
    {
      "name": "ejb-container",
      "status": "UP",
      "data": {
        "accountService": "available",
        "configService": "available",
        "status": "ready"
      }
    }
  ]
}
```

## Metrics

View application metrics:

```bash
curl http://localhost:9080/metrics
```

Key metrics to monitor:
- `application_processing_time_seconds` - Request processing time
- `vendor_threadpool_activeThreads` - Active threads
- `vendor_connectionpool_inUseConnections` - Database connections

## Security Testing

### Test Role-Based Access Control

```bash
# Admin can create accounts
curl -u admin:admin123 "http://localhost:9080/banking?action=create"
# Expected: Success

# Teller can perform operations
curl -u teller:teller123 "http://localhost:9080/banking?action=deposit&accountId=1&amount=100"
# Expected: Success

# Customer has read-only access
curl -u customer:customer123 "http://localhost:9080/banking?action=create"
# Expected: 403 Forbidden
```

## Transaction Testing

### Test Container-Managed Transactions

1. **Successful Transaction**:
   ```bash
   curl -u admin:admin123 -X POST \
     "http://localhost:9080/banking?action=transfer&fromId=1&toId=2&amount=100"
   ```
   Expected: Both accounts updated atomically

2. **Failed Transaction** (insufficient funds):
   ```bash
   curl -u admin:admin123 -X POST \
     "http://localhost:9080/banking?action=transfer&fromId=1&toId=2&amount=999999"
   ```
   Expected: Transaction rolled back, no changes

## Performance Testing

### Load Testing with Apache Bench

```bash
# Test account creation
ab -n 100 -c 10 -A admin:admin123 \
  "http://localhost:9080/banking?action=create"

# Test concurrent deposits
ab -n 1000 -c 50 -A teller:teller123 \
  "http://localhost:9080/banking?action=deposit&accountId=1&amount=10"
```

## Troubleshooting

### Common Issues

#### 1. Port Already in Use

```bash
# Find process using port 9080
lsof -i :9080

# Kill the process
kill -9 <PID>
```

#### 2. PostgreSQL Connection Failed

```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Check logs
docker logs lab04b-postgres

# Restart PostgreSQL
docker restart lab04b-postgres
```

#### 3. Liberty Server Won't Start

```bash
# Check Liberty logs
tail -f solution/target/liberty/wlp/usr/servers/lab04bServer/logs/messages.log

# Clean and rebuild
mvn clean package
```

#### 4. EJB Injection Failures

Check for:
- Missing `@EJB` annotations
- Incorrect JNDI names
- Missing `beans.xml` (for CDI)

#### 5. Transaction Failures

Check for:
- Correct `@TransactionAttribute` annotations
- Database connection pool settings
- Transaction timeout configuration

## Automated Test Details

### Test Scenarios Covered

#### Test 1: Account Creation (Stateless EJB)
- **Component**: [`AccountServiceBean`](solution/src/main/java/com/bank/ejb/AccountServiceBean.java:1)
- **Operation**: Create new checking account
- **Verification**: Account number generated and returned
- **Expected**: Account created with unique ACC-{timestamp} number

#### Test 2: Multiple Account Creation
- **Purpose**: Verify stateless bean can handle multiple requests
- **Verification**: Second account created with different ID
- **Expected**: Each account has unique identifier

#### Test 3: Account List Verification
- **Purpose**: Verify findAll() operation
- **Verification**: Count of accounts in response
- **Expected**: At least 2 accounts present

#### Test 4: Deposit Operation (CMT)
- **Component**: [`AccountServiceBean.deposit()`](solution/src/main/java/com/bank/ejb/AccountServiceBean.java:1)
- **Transaction**: Container-Managed Transaction (REQUIRED)
- **Operation**: Deposit $1000.00 to first account
- **Verification**: Success message and balance update
- **Expected**: Transaction commits automatically

#### Test 5: Withdrawal Operation
- **Component**: [`AccountServiceBean.withdraw()`](solution/src/main/java/com/bank/ejb/AccountServiceBean.java:1)
- **Operation**: Withdraw $250.00 from first account
- **Verification**: Success message and balance update
- **Expected**: Balance reduced by withdrawal amount

#### Test 6: Transfer Operation (Transaction Management)
- **Component**: [`AccountServiceBean.transfer()`](solution/src/main/java/com/bank/ejb/AccountServiceBean.java:1)
- **Transaction**: Tests atomic transaction across two accounts
- **Operation**: Transfer $100.00 from first to second account
- **Verification**: Both accounts updated atomically
- **Expected**: Debit and credit happen in single transaction

#### Test 7: Balance Verification
- **Purpose**: Verify transaction integrity
- **Calculations**:
  - First account: $1000 - $250 - $100 = $650
  - Second account: $0 + $100 = $100
- **Expected**: Balances match calculated values

#### Test 8: Singleton EJB
- **Component**: [`ConfigServiceBean`](solution/src/main/java/com/bank/ejb/ConfigServiceBean.java:1)
- **Purpose**: Verify singleton initialization and configuration
- **Verification**: Application name retrieved from config
- **Expected**: "EJB Banking Application" displayed

#### Test 9: Timer Service
- **Component**: [`ReportGeneratorBean`](solution/src/main/java/com/bank/ejb/ReportGeneratorBean.java:1)
- **Purpose**: Verify scheduled task execution
- **Verification**: Report statistics displayed
- **Expected**: Timer service active and generating reports

#### Test 10: EJB Security
- **Component**: Role-based access control
- **Purpose**: Verify `@RolesAllowed` enforcement
- **Operation**: Admin creates account
- **Expected**: Admin role has necessary permissions

#### Test 11: Database Persistence (Podman only)
- **Purpose**: Verify JPA persistence
- **Operation**: Query PostgreSQL directly
- **Verification**: Account count in database
- **Expected**: Database contains created accounts

#### Test 12: JMS Configuration (Podman only)
- **Component**: [`NotificationMDB`](solution/src/main/java/com/bank/ejb/NotificationMDB.java:1)
- **Purpose**: Verify JMS queue configuration
- **Verification**: Health check includes JMS status
- **Expected**: JMS infrastructure ready for MDB

## Verification Checklist

### Automated Tests (via test-lab.sh or podman-test.sh)
- [ ] Test 1: Account creation successful
- [ ] Test 2: Multiple accounts created
- [ ] Test 3: Account list verified
- [ ] Test 4: Deposit operation works
- [ ] Test 5: Withdrawal operation works
- [ ] Test 6: Transfer operation works
- [ ] Test 7: Balances calculated correctly
- [ ] Test 8: Singleton ConfigService working
- [ ] Test 9: Timer Service active
- [ ] Test 10: Security roles enforced
- [ ] Test 11: Database persistence verified (Podman)
- [ ] Test 12: JMS configuration healthy (Podman)

### Manual Verification
- [ ] Application builds successfully
- [ ] PostgreSQL connection established
- [ ] Liberty server starts without errors
- [ ] Health checks return UP status
- [ ] Metrics endpoint accessible
- [ ] Home page loads correctly
- [ ] Banking operations work with authentication
- [ ] Role-based access control enforced
- [ ] Transactions commit/rollback correctly
- [ ] Timer service generates reports (check logs)
- [ ] MDB processes messages (check logs)
- [ ] Singleton bean initializes on startup (check logs)

## Cleanup

### Stop Local Server

```bash
# Press Ctrl+C in the terminal running mvn liberty:run
```

### Stop Containers

```bash
# Docker
docker stop lab04b-postgres lab04b-liberty
docker rm lab04b-postgres lab04b-liberty

# Podman
podman stop lab04b-postgres lab04b-liberty
podman rm lab04b-postgres lab04b-liberty
podman network rm lab04b-network

# Docker Compose
docker-compose down -v
```

## Additional Resources

- [Open Liberty Documentation](https://openliberty.io/docs/)
- [Jakarta EE 10 Specification](https://jakarta.ee/specifications/platform/10/)
- [EJB 4.0 Specification](https://jakarta.ee/specifications/enterprise-beans/4.0/)
- [MicroProfile 6.0](https://microprofile.io/)

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review Liberty server logs
3. Verify all prerequisites are met
4. Consult the README.md for lab instructions

---

Made with Bob