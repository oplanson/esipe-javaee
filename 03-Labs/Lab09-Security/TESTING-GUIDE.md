<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 9 - Jakarta EE Security: Testing Guide

**Comprehensive Testing Documentation for Secure Banking Application**

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Testing Scripts](#testing-scripts)
3. [Local Testing (test-lab.sh)](#local-testing)
4. [Container Testing (podman-test.sh)](#container-testing)
5. [Manual Testing](#manual-testing)
6. [Security Testing Scenarios](#security-testing-scenarios)
7. [Troubleshooting](#troubleshooting)
8. [Performance Testing](#performance-testing)

---

## 1. Overview

This guide provides comprehensive testing procedures for Lab 9 - Jakarta EE Security implementation. The lab includes:

- **JWT Authentication** - Token-based authentication
- **Role-Based Access Control** - 4 roles (ADMIN, MANAGER, TELLER, CUSTOMER)
- **Password Security** - PBKDF2 hashing with 310,000 iterations
- **Account Lockout** - Automatic lock after 5 failed attempts
- **Security Audit Logging** - All security events tracked
- **Security Headers** - CSP, HSTS, X-Frame-Options, etc.

### Testing Levels

1. **Build Verification** - Compile and package (test-lab.sh)
2. **Container Deployment** - Full stack with PostgreSQL (podman-test.sh)
3. **Manual Testing** - Interactive API testing
4. **Security Testing** - Penetration testing scenarios

---

## 2. Testing Scripts

### Available Scripts

| Script | Purpose | Tests | Duration |
|--------|---------|-------|----------|
| `test-lab.sh` | Local build verification | 10 | ~30s |
| `podman-test.sh` | Container deployment + security tests | 13 | ~2-3min |

### Prerequisites

```bash
# Required tools
- Java 17+
- Maven 3.8+
- Podman or Docker
- curl
- jq (for JSON parsing)

# Verify installations
java -version
mvn -version
podman --version
curl --version
jq --version
```

---

## 3. Local Testing (test-lab.sh)

### Purpose

Verifies that the solution code compiles, packages correctly, and includes all required components.

### Running Local Tests

```bash
cd esipe-javaee/03-Labs/Lab09-Security
chmod +x test-lab.sh
./test-lab.sh
```

### Test Cases (10 tests)

#### Test 1: Clean Build
```bash
✓ Clean completed
```
Verifies Maven clean removes target directory.

#### Test 2: Compilation
```bash
✓ Compilation successful
```
Verifies all Java classes compile without errors.

#### Test 3: Test Execution
```bash
✓ Tests passed
```
Runs unit tests (if present).

#### Test 4: Packaging
```bash
✓ Packaging successful
```
Creates WAR file (bank-security.war).

#### Test 5: WAR File Existence
```bash
✓ WAR file exists: target/bank-security.war (3.4 MB)
```
Verifies WAR file is created with expected size.

#### Test 6-10: Component Verification
```bash
✓ Model classes found (4 classes)
✓ Security services found (5 classes)
✓ REST resources found (3 classes)
✓ Configuration files found (6 files)
✓ All required components present
```

### Expected Output

```
===========================================
Lab 9 - Jakarta EE Security: Build Test
===========================================

Running build verification tests...

Test 1/10: Clean build
✓ Clean completed

Test 2/10: Compilation
✓ Compilation successful

Test 3/10: Test execution
✓ Tests passed

Test 4/10: Packaging
✓ Packaging successful

Test 5/10: WAR file verification
✓ WAR file exists: target/bank-security.war (3.4 MB)

Test 6/10: Model classes
✓ Found 4 model classes

Test 7/10: Security services
✓ Found 5 security services

Test 8/10: REST resources
✓ Found 3 REST resources

Test 9/10: Configuration files
✓ Found 6 configuration files

Test 10/10: Component verification
✓ All required components present

===========================================
✅ All 10 tests passed!
===========================================
```

### Troubleshooting Local Tests

#### Compilation Errors

```bash
# Check Java version
java -version  # Should be 17+

# Clean and rebuild
mvn clean compile

# Check for missing dependencies
mvn dependency:tree
```

#### Missing Classes

```bash
# Verify source structure
find src/main/java -name "*.java"

# Check package names
grep -r "package com.bank" src/main/java
```

---

## 4. Container Testing (podman-test.sh)

### Purpose

Deploys the complete application stack (PostgreSQL + Liberty) and runs comprehensive security tests.

### Running Container Tests

```bash
cd esipe-javaee/03-Labs/Lab09-Security
chmod +x podman-test.sh
./podman-test.sh
```

### Test Cases (13 tests)

#### Phase 1: Infrastructure Setup (Tests 1-4)

**Test 1: Cleanup**
```bash
✓ Cleanup completed
```
Removes existing containers, networks, and images.

**Test 2: Port Availability**
```bash
✓ Ports 9080 and 5432 are available
```
Checks that required ports are not in use.

**Test 3: Network Creation**
```bash
✓ Network 'bank-security-network' created
```
Creates isolated Podman network.

**Test 4: PostgreSQL Deployment**
```bash
✓ PostgreSQL container started and healthy
```
Starts database with health checks.

#### Phase 2: Application Deployment (Tests 5-7)

**Test 5: Image Build**
```bash
✓ Application image built successfully
```
Builds Liberty application image.

**Test 6: Application Deployment**
```bash
✓ Application container started
```
Starts application with environment variables.

**Test 7: Health Checks**
```bash
✓ Application is healthy (liveness: UP, readiness: UP)
```
Verifies MicroProfile Health endpoints.

#### Phase 3: Security Testing (Tests 8-13)

**Test 8: User Registration**
```bash
✓ User registration successful (JWT token received)
```
Tests POST /api/auth/register endpoint.

**Test 9: User Login**
```bash
✓ User login successful (JWT token received)
```
Tests POST /api/auth/login endpoint.

**Test 10: Current User Info**
```bash
✓ Current user info retrieved (username: testuser)
```
Tests GET /api/auth/me with JWT token.

**Test 11: My Accounts**
```bash
✓ My accounts retrieved (role-based access)
```
Tests GET /api/accounts/my with CUSTOMER role.

**Test 12: Access Denied**
```bash
✓ Access denied for unauthorized role (403 Forbidden)
```
Tests GET /api/accounts with CUSTOMER role (requires ADMIN/MANAGER).

**Test 13: Database Persistence**
```bash
✓ Database persistence verified (users and audit logs)
```
Verifies data is stored in PostgreSQL.

### Expected Output

```
===========================================
Lab 9 - Jakarta EE Security: Container Test
===========================================

Phase 1: Infrastructure Setup
------------------------------
Test 1/13: Cleanup existing resources
✓ Cleanup completed

Test 2/13: Check port availability
✓ Ports 9080 and 5432 are available

Test 3/13: Create network
✓ Network 'bank-security-network' created

Test 4/13: Start PostgreSQL
✓ PostgreSQL container started and healthy

Phase 2: Application Deployment
--------------------------------
Test 5/13: Build application image
✓ Application image built successfully

Test 6/13: Start application
✓ Application container started

Test 7/13: Health checks
✓ Application is healthy (liveness: UP, readiness: UP)

Phase 3: Security Testing
--------------------------
Test 8/13: User registration
✓ User registration successful (JWT token received)

Test 9/13: User login
✓ User login successful (JWT token received)

Test 10/13: Current user info
✓ Current user info retrieved (username: testuser)

Test 11/13: My accounts
✓ My accounts retrieved (role-based access)

Test 12/13: Access denied
✓ Access denied for unauthorized role (403 Forbidden)

Test 13/13: Database persistence
✓ Database persistence verified (users and audit logs)

===========================================
✅ All 13 tests passed!
===========================================

Application is running at: http://localhost:9080
API Documentation: http://localhost:9080/

Next steps:
1. Test the application manually
2. Check application logs: podman logs bank-security-app
3. Access PostgreSQL: podman exec -it bank-security-db psql -U bankuser -d bankdb
4. Stop containers: podman stop bank-security-app bank-security-db
5. Remove containers: podman rm bank-security-app bank-security-db
```

### Troubleshooting Container Tests

#### Port Conflicts

```bash
# Check what's using the ports
lsof -i :9080
lsof -i :5432

# Kill processes if needed
kill -9 <PID>

# Or use different ports in docker-compose.yml
```

#### Container Startup Issues

```bash
# Check container logs
podman logs bank-security-app
podman logs bank-security-db

# Check container status
podman ps -a

# Restart containers
podman restart bank-security-app
```

#### Database Connection Issues

```bash
# Test PostgreSQL connection
podman exec -it bank-security-db psql -U bankuser -d bankdb

# Check database logs
podman logs bank-security-db

# Verify network
podman network inspect bank-security-network
```

---

## 5. Manual Testing

### Setup

```bash
# Start the application
cd solution
mvn liberty:dev

# Or use containers
cd ..
podman-compose up
```

### Base URL

```
http://localhost:9080
```

### API Endpoints

#### 1. Register New User

```bash
curl -X POST http://localhost:9080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john.doe@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Expected Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john.doe",
  "email": "john.doe@example.com",
  "roles": ["CUSTOMER"],
  "expiresIn": 3600
}
```

#### 2. Login

```bash
curl -X POST http://localhost:9080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "SecurePass123!"
  }'
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john.doe",
  "email": "john.doe@example.com",
  "roles": ["CUSTOMER"],
  "expiresIn": 3600
}
```

#### 3. Get Current User Info

```bash
# Save token from login response
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X GET http://localhost:9080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):**
```json
{
  "username": "john.doe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["CUSTOMER"],
  "accountLocked": false,
  "createdAt": "2026-01-18T10:30:00Z"
}
```

#### 4. Create Account (Authenticated)

```bash
curl -X POST http://localhost:9080/api/accounts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "ACC001",
    "accountType": "CHECKING",
    "balance": 1000.00
  }'
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "accountNumber": "ACC001",
  "accountType": "CHECKING",
  "balance": 1000.00,
  "status": "ACTIVE",
  "createdAt": "2026-01-18T10:30:00Z"
}
```

#### 5. Get My Accounts

```bash
curl -X GET http://localhost:9080/api/accounts/my \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "accountNumber": "ACC001",
    "accountType": "CHECKING",
    "balance": 1000.00,
    "status": "ACTIVE"
  }
]
```

#### 6. Get All Accounts (Admin/Manager Only)

```bash
curl -X GET http://localhost:9080/api/accounts \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (403 Forbidden for CUSTOMER):**
```json
{
  "error": "Forbidden",
  "message": "Access denied",
  "timestamp": "2026-01-18T10:30:00Z"
}
```

#### 7. Deposit Money

```bash
curl -X POST http://localhost:9080/api/accounts/1/deposit \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 500.00
  }'
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "accountNumber": "ACC001",
  "balance": 1500.00,
  "message": "Deposit successful"
}
```

#### 8. Withdraw Money

```bash
curl -X POST http://localhost:9080/api/accounts/1/withdraw \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 200.00
  }'
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "accountNumber": "ACC001",
  "balance": 1300.00,
  "message": "Withdrawal successful"
}
```

#### 9. Transfer Money

```bash
curl -X POST http://localhost:9080/api/accounts/1/transfer \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "toAccountId": 2,
    "amount": 300.00
  }'
```

**Expected Response (200 OK):**
```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 300.00,
  "message": "Transfer successful"
}
```

#### 10. Logout

```bash
curl -X POST http://localhost:9080/api/auth/logout \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):**
```json
{
  "message": "Logout successful"
}
```

---

## 6. Security Testing Scenarios

### Scenario 1: Failed Login Attempts

**Objective:** Test account lockout after 5 failed attempts.

```bash
# Attempt 1-5 with wrong password
for i in {1..5}; do
  curl -X POST http://localhost:9080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{
      "username": "john.doe",
      "password": "WrongPassword"
    }'
  echo "Attempt $i"
done

# Attempt 6 - should return account locked
curl -X POST http://localhost:9080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "SecurePass123!"
  }'
```

**Expected Response (6th attempt):**
```json
{
  "error": "Account Locked",
  "message": "Account has been locked due to too many failed login attempts",
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### Scenario 2: JWT Token Expiration

**Objective:** Test expired token handling.

```bash
# Wait for token to expire (default: 1 hour)
# Or use an expired token
EXPIRED_TOKEN="eyJhbGciOiJIUzI1NiJ9.expired.token"

curl -X GET http://localhost:9080/api/auth/me \
  -H "Authorization: Bearer $EXPIRED_TOKEN"
```

**Expected Response (401 Unauthorized):**
```json
{
  "error": "Unauthorized",
  "message": "Token has expired",
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### Scenario 3: Invalid JWT Token

**Objective:** Test invalid token handling.

```bash
curl -X GET http://localhost:9080/api/auth/me \
  -H "Authorization: Bearer invalid.token.here"
```

**Expected Response (401 Unauthorized):**
```json
{
  "error": "Unauthorized",
  "message": "Invalid token",
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### Scenario 4: Missing Authorization Header

**Objective:** Test missing authentication.

```bash
curl -X GET http://localhost:9080/api/accounts/my
```

**Expected Response (401 Unauthorized):**
```json
{
  "error": "Unauthorized",
  "message": "No authorization token provided",
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### Scenario 5: Role-Based Access Control

**Objective:** Test RBAC enforcement.

```bash
# Login as CUSTOMER
TOKEN_CUSTOMER="..."

# Try to access admin endpoint
curl -X GET http://localhost:9080/api/accounts \
  -H "Authorization: Bearer $TOKEN_CUSTOMER"
```

**Expected Response (403 Forbidden):**
```json
{
  "error": "Forbidden",
  "message": "Access denied. Required roles: [ADMIN, MANAGER]",
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### Scenario 6: SQL Injection Prevention

**Objective:** Test SQL injection protection.

```bash
curl -X POST http://localhost:9080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin'\'' OR '\''1'\''='\''1",
    "password": "anything"
  }'
```

**Expected Response (401 Unauthorized):**
```json
{
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### Scenario 7: XSS Prevention

**Objective:** Test XSS attack prevention.

```bash
curl -X POST http://localhost:9080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "<script>alert(\"XSS\")</script>",
    "email": "test@example.com",
    "password": "SecurePass123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "error": "Validation Error",
  "message": "Invalid username format",
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### Scenario 8: Security Headers Verification

**Objective:** Verify security headers are present.

```bash
curl -I http://localhost:9080/api/auth/me
```

**Expected Headers:**
```
HTTP/1.1 401 Unauthorized
Content-Security-Policy: default-src 'self'
X-Frame-Options: DENY
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
```

### Scenario 9: Password Strength Validation

**Objective:** Test weak password rejection.

```bash
curl -X POST http://localhost:9080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "weakuser",
    "email": "weak@example.com",
    "password": "123",
    "firstName": "Weak",
    "lastName": "User"
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "error": "Validation Error",
  "message": "Password must be at least 8 characters long",
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### Scenario 10: Audit Log Verification

**Objective:** Verify security events are logged.

```bash
# Access PostgreSQL
podman exec -it bank-security-db psql -U bankuser -d bankdb

# Query audit logs
SELECT * FROM security_audit_log ORDER BY timestamp DESC LIMIT 10;
```

**Expected Output:**
```
 id | username  |      action       | result  |   ip_address   | user_agent | timestamp
----+-----------+-------------------+---------+----------------+------------+-----------
  1 | john.doe  | LOGIN_SUCCESS     | SUCCESS | 172.17.0.1     | curl/7.68  | 2026-01-18
  2 | john.doe  | LOGIN_FAILED      | FAILURE | 172.17.0.1     | curl/7.68  | 2026-01-18
  3 | john.doe  | REGISTRATION      | SUCCESS | 172.17.0.1     | curl/7.68  | 2026-01-18
```

---

## 7. Troubleshooting

### Common Issues

#### Issue 1: "Port already in use"

**Symptoms:**
```
Error: Port 9080 is already in use
```

**Solution:**
```bash
# Find process using port
lsof -i :9080

# Kill process
kill -9 <PID>

# Or use different port
export HTTP_PORT=9081
```

#### Issue 2: "Database connection failed"

**Symptoms:**
```
Error: Could not connect to database
```

**Solution:**
```bash
# Check PostgreSQL is running
podman ps | grep postgres

# Check database logs
podman logs bank-security-db

# Restart database
podman restart bank-security-db

# Verify connection
podman exec -it bank-security-db psql -U bankuser -d bankdb
```

#### Issue 3: "JWT token invalid"

**Symptoms:**
```
401 Unauthorized: Invalid token
```

**Solution:**
```bash
# Check token format
echo $TOKEN | cut -d'.' -f1 | base64 -d

# Verify JWT secret in microprofile-config.properties
grep jwt.secret src/main/resources/META-INF/microprofile-config.properties

# Get new token
curl -X POST http://localhost:9080/api/auth/login ...
```

#### Issue 4: "Account locked"

**Symptoms:**
```
Account has been locked due to too many failed login attempts
```

**Solution:**
```bash
# Unlock account in database
podman exec -it bank-security-db psql -U bankuser -d bankdb

UPDATE users SET account_locked = false, failed_login_attempts = 0 
WHERE username = 'john.doe';
```

#### Issue 5: "403 Forbidden"

**Symptoms:**
```
Access denied. Required roles: [ADMIN, MANAGER]
```

**Solution:**
```bash
# Check user roles
curl -X GET http://localhost:9080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"

# Add role in database
podman exec -it bank-security-db psql -U bankuser -d bankdb

INSERT INTO user_roles (user_id, role) VALUES (1, 'ADMIN');
```

---

## 8. Performance Testing

### Load Testing with Apache Bench

```bash
# Install Apache Bench
sudo apt-get install apache2-utils  # Ubuntu/Debian
brew install httpd  # macOS

# Test login endpoint
ab -n 1000 -c 10 -p login.json -T application/json \
  http://localhost:9080/api/auth/login

# login.json content:
# {"username":"john.doe","password":"SecurePass123!"}
```

### Expected Performance

| Endpoint | Requests/sec | Avg Response Time |
|----------|--------------|-------------------|
| /api/auth/login | 100-200 | 50-100ms |
| /api/auth/register | 50-100 | 100-200ms |
| /api/accounts/my | 200-300 | 20-50ms |
| /api/accounts | 150-250 | 30-60ms |

### Stress Testing

```bash
# Concurrent users test
for i in {1..100}; do
  curl -X POST http://localhost:9080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"user'$i'","password":"pass"}' &
done
wait
```

---

## 9. Security Checklist

### Before Deployment

- [ ] All passwords are hashed with PBKDF2
- [ ] JWT secret is strong and unique
- [ ] Account lockout is enabled (5 attempts)
- [ ] Security headers are configured
- [ ] CORS is properly configured
- [ ] SQL injection protection is verified
- [ ] XSS protection is verified
- [ ] CSRF protection is implemented
- [ ] HTTPS is enabled (production)
- [ ] Security audit logging is enabled
- [ ] Database credentials are secure
- [ ] Environment variables are used for secrets
- [ ] Token expiration is configured (1 hour)
- [ ] Role-based access control is enforced
- [ ] Input validation is comprehensive

### Monitoring

- [ ] Monitor failed login attempts
- [ ] Monitor locked accounts
- [ ] Monitor security audit logs
- [ ] Monitor JWT token usage
- [ ] Monitor API response times
- [ ] Monitor database connections
- [ ] Monitor error rates
- [ ] Set up alerts for security events

---

## 10. Additional Resources

### Documentation

- [Jakarta EE Security Specification](https://jakarta.ee/specifications/security/)
- [MicroProfile JWT RBAC](https://microprofile.io/specifications/microprofile-jwt-auth/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)

### Tools

- [Postman](https://www.postman.com/) - API testing
- [OWASP ZAP](https://www.zaproxy.org/) - Security testing
- [Burp Suite](https://portswigger.net/burp) - Web security testing
- [jwt.io](https://jwt.io/) - JWT debugging

---

**© 2026 Olivier Planson - All rights reserved. Reproduction prohibited.**