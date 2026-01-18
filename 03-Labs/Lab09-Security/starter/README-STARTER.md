<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 9 - Jakarta EE Security: Starter Code

**This is the starter code for Lab 9. Follow the instructions in the main README.md to complete the exercises.**

---

## 📁 Project Structure

This starter code provides the complete project structure with all necessary configuration files. The Java classes contain the full implementation to serve as a reference.

### What's Included

✅ **Complete Maven Project**
- `pom.xml` - All dependencies configured (Jakarta EE 10, MicroProfile 6.1, JJWT 0.12.5)
- Liberty Maven Plugin configured
- PostgreSQL driver included

✅ **Configuration Files**
- `src/main/liberty/config/server.xml` - Liberty server configuration
- `src/main/liberty/config/bootstrap.properties` - Environment variables
- `src/main/resources/META-INF/persistence.xml` - JPA configuration
- `src/main/resources/META-INF/microprofile-config.properties` - Security configuration
- `src/main/webapp/WEB-INF/web.xml` - Security roles declaration

✅ **Container Support**
- `Containerfile` - Multi-stage build for Podman/Docker
- `docker-compose.yml` - PostgreSQL + Liberty orchestration
- `.gitignore` - Git ignore rules

✅ **Complete Implementation**
- All model classes (User, Role, SecurityAuditLog, Account)
- All security services (PasswordService, JwtService, DatabaseIdentityStore, etc.)
- All REST resources (AuthResource, AccountResource)
- All filters (SecurityHeadersFilter, CorsFilter)
- All DTOs (LoginRequest, RegisterRequest, AuthResponse, ErrorResponse)

---

## 🎯 How to Use This Starter Code

### Option 1: Study the Complete Implementation

The starter code contains the full working implementation. You can:

1. **Read and understand** the code structure
2. **Run the application** to see it in action
3. **Test the security features** using the provided scripts
4. **Modify and experiment** with the code

### Option 2: Practice by Reimplementing

If you want to practice implementing the security features yourself:

1. **Delete specific classes** you want to reimplement
2. **Follow the README.md exercises** to recreate them
3. **Compare your implementation** with the provided code
4. **Test your changes** using the test scripts

### Option 3: Incremental Learning

1. **Start with Exercise 1** (User entity and Role enum)
2. **Study the provided implementation** for that exercise
3. **Modify or extend** the code with your own ideas
4. **Move to the next exercise** when ready

---

## 🚀 Quick Start

### 1. Build and Run Locally

```bash
# Build the project
mvn clean package

# Run with Liberty dev mode
mvn liberty:dev

# Access the application
open http://localhost:9080
```

### 2. Run with Containers

```bash
# Start PostgreSQL and application
podman-compose up

# Or use the test script
cd ..
./podman-test.sh
```

### 3. Test the Implementation

```bash
# Run local build tests
cd ..
./test-lab.sh

# Run container tests with security validation
./podman-test.sh
```

---

## 📚 Learning Path

### Recommended Study Order

1. **Model Layer** (`com.bank.model`)
   - Study `User.java` - User entity with roles and account lockout
   - Study `Role.java` - Role enum (ADMIN, MANAGER, TELLER, CUSTOMER)
   - Study `SecurityAuditLog.java` - Security event tracking
   - Study `Account.java` - Simple account entity

2. **Security Layer** (`com.bank.security`)
   - Study `PasswordService.java` - PBKDF2 password hashing
   - Study `JwtService.java` - JWT token generation and validation
   - Study `DatabaseIdentityStore.java` - Custom authentication
   - Study `JwtAuthenticationMechanism.java` - HTTP authentication
   - Study `SecurityAuditService.java` - Audit logging

3. **Service Layer** (`com.bank.service`)
   - Study `UserService.java` - User management operations
   - Study `AccountService.java` - Account operations

4. **API Layer** (`com.bank.api`)
   - Study `AuthResource.java` - Authentication endpoints
   - Study `AccountResource.java` - Secured account endpoints
   - Study `RestApplication.java` - JAX-RS configuration

5. **Filter Layer** (`com.bank.filter`)
   - Study `SecurityHeadersFilter.java` - Security headers
   - Study `CorsFilter.java` - CORS configuration

6. **DTO Layer** (`com.bank.dto`)
   - Study request/response DTOs

---

## 🔐 Key Security Concepts to Understand

### 1. Password Hashing (PasswordService)
- **Algorithm**: PBKDF2WithHmacSHA512
- **Iterations**: 310,000 (OWASP recommended)
- **Salt**: 64 bytes, randomly generated
- **Key Length**: 512 bits

### 2. JWT Authentication (JwtService)
- **Library**: JJWT 0.12.5
- **Algorithm**: HS256 (HMAC with SHA-256)
- **Expiration**: 1 hour (configurable)
- **Claims**: username, roles, issuer

### 3. Account Lockout (DatabaseIdentityStore)
- **Max Attempts**: 5 failed logins
- **Lockout**: Automatic account lock
- **Reset**: Manual unlock required

### 4. Role-Based Access Control
- **Roles**: ADMIN, MANAGER, TELLER, CUSTOMER
- **Enforcement**: @RolesAllowed annotations
- **Hierarchy**: ADMIN > MANAGER > TELLER > CUSTOMER

### 5. Security Audit Logging
- **Events**: Login, logout, registration, access denied
- **Data**: Username, action, result, IP, user agent, timestamp
- **Storage**: PostgreSQL database

### 6. Security Headers
- **CSP**: Content Security Policy
- **HSTS**: HTTP Strict Transport Security
- **X-Frame-Options**: Clickjacking protection
- **X-Content-Type-Options**: MIME sniffing protection

---

## 🧪 Testing Your Understanding

### Manual Testing Checklist

- [ ] Register a new user
- [ ] Login with correct credentials
- [ ] Login with wrong password (5 times to trigger lockout)
- [ ] Access protected endpoints with JWT token
- [ ] Try to access admin endpoints as customer (should fail)
- [ ] Check security audit logs in database
- [ ] Verify security headers in responses
- [ ] Test token expiration
- [ ] Test invalid token handling

### Automated Testing

```bash
# Run all tests
cd ..
./test-lab.sh      # 10 build verification tests
./podman-test.sh   # 13 security integration tests
```

---

## 📖 Additional Resources

### Documentation
- Main README: `../README.md` - Complete lab instructions
- Testing Guide: `../TESTING-GUIDE.md` - Comprehensive testing documentation
- Lecture: `../../02-Lectures/09-jakarta-ee-security.md` - Theory and concepts

### External Resources
- [Jakarta EE Security Specification](https://jakarta.ee/specifications/security/)
- [MicroProfile JWT RBAC](https://microprofile.io/specifications/microprofile-jwt-auth/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [JJWT Documentation](https://github.com/jwtk/jjwt)

---

## 💡 Tips for Success

1. **Understand Before Coding**: Read the provided implementation carefully
2. **Test Frequently**: Use the test scripts after each change
3. **Security First**: Never store passwords in plain text
4. **Follow Standards**: Use Jakarta EE and MicroProfile APIs
5. **Log Everything**: Security events should always be logged
6. **Validate Input**: Never trust user input
7. **Use Strong Secrets**: JWT secret should be strong and unique
8. **Enable HTTPS**: Always use HTTPS in production

---

## 🆘 Getting Help

### Common Issues

1. **Build Fails**: Check Java version (17+) and Maven version (3.8+)
2. **Database Connection**: Ensure PostgreSQL is running
3. **Port Conflicts**: Check ports 9080 and 5432 are available
4. **JWT Errors**: Verify JWT secret in microprofile-config.properties

### Where to Look

- **Build Errors**: Check `pom.xml` dependencies
- **Runtime Errors**: Check `server.xml` configuration
- **Database Errors**: Check `persistence.xml` and database connection
- **Security Errors**: Check `web.xml` and security annotations

---

**Good luck with your learning! 🚀**

**© 2026 Olivier Planson - All rights reserved. Reproduction prohibited.**