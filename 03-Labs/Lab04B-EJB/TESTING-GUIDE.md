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
1. Check prerequisites
2. Start PostgreSQL if needed
3. Build the application
4. Start Liberty server
5. Run automated tests
6. Display results

### Method 3: Container Testing with Podman

```bash
./podman-test.sh
```

This script will:
1. Clean up existing containers
2. Build the application
3. Create Podman network
4. Start PostgreSQL container
5. Build Liberty container image
6. Start Liberty container
7. Run comprehensive tests
8. Display results and management commands

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

## Verification Checklist

- [ ] Application builds successfully
- [ ] PostgreSQL connection established
- [ ] Liberty server starts without errors
- [ ] Health checks return UP status
- [ ] Metrics endpoint accessible
- [ ] Home page loads correctly
- [ ] Banking operations work with authentication
- [ ] Account creation successful
- [ ] Deposit/withdrawal operations work
- [ ] Transfer between accounts works
- [ ] Role-based access control enforced
- [ ] Transactions commit/rollback correctly
- [ ] Timer service generates reports
- [ ] MDB processes messages
- [ ] Singleton bean initializes on startup

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