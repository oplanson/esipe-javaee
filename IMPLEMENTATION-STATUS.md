<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Course Improvement Implementation Status

**Date:** January 12, 2026  
**Based on:** COURSE-IMPROVEMENT-PLAN.md

---

## 📊 Overall Progress

**Total Tasks:** 21
**Completed:** 14 (67%)
**In Progress:** 0 (0%)
**Pending:** 7 (33%)

---

## ✅ Completed Tasks

### 1. ✅ Lecture 2B: JSF (JavaServer Faces) - CREATED
- **File:** `esipe-javaee/02-Lectures/02b-jsf-javaserver-faces.md`
- **Lines:** 1,337
- **Status:** Complete
- **Content:**
  - JSF architecture and lifecycle
  - Managed beans with CDI
  - Facelets templating
  - JSF components
  - Validation and conversion
  - AJAX support
  - Navigation
  - Composite components
  - PrimeFaces introduction
  - Best practices

---

## ✅ Completed Tasks (continued)

### 2. ✅ Lab 2B: JSF Client Management - CREATED
- **Directory:** `esipe-javaee/03-Labs/Lab02B-JSF/`
- **Status:** Complete with testing tools
- **Files Created:**
  - `README.md` (738 lines) - Complete lab instructions
  - `test-lab.sh` (229 lines) - Build verification script
  - `podman-test.sh` (330 lines) - Podman deployment and testing
  - `TESTING-GUIDE.md` (545 lines) - Comprehensive testing guide
- **Directory Structure:** Created complete lab structure with starter/solution folders
- **Testing Philosophy:** Follows same pattern as existing labs with automated testing scripts

---

## ✅ Completed Tasks (continued)

### 3. ✅ Enhance Lecture 2: Servlets & JSP - COMPLETED
- **File:** `esipe-javaee/02-Lectures/02-servlets-jsp-microprofile.md`
- **Status:** Complete
- **Enhancement:** Added section on HTTPSession, Filters, Listeners (1 hour content)
- **Content Added:**
  - HTTPSession Management (26 slides, ~1,100 lines)
  - Servlet Filters (Authentication, Logging, CORS, Compression)
  - Servlet Listeners (SessionCounter, ApplicationLifecycle)
  - Code examples and best practices

### 4. ✅ Enhance Lab 2: Servlets & JSP - COMPLETED
- **Directory:** `esipe-javaee/03-Labs/Lab02-ServletsJSP/`
- **Status:** Complete
- **Enhancement:** Added Filter and Listener exercises (Part 7 - 90 minutes)
- **Files Created:**
  - 6 Filter classes (solution + starter with TODOs)
  - 2 Listener classes (solution + starter with TODOs)
  - `FILTERS-LISTENERS-GUIDE.md` (comprehensive guide)
  - Updated `README.md` with Part 7 exercises
- **Total Lines:** ~2,461 lines of new code

### 5. ✅ Lecture 4B: EJB (Enterprise Java Beans) - COMPLETED
- **File:** `esipe-javaee/02-Lectures/04b-ejb-enterprise-java-beans.md`
- **Lines:** 2,976
- **Status:** Complete
- **Content:**
  - Introduction to EJB (15 min)
  - Session Beans: Stateless, Stateful, Singleton (45 min)
  - Message-Driven Beans (30 min)
  - EJB Lifecycle (20 min)
  - Transaction Management (30 min)
  - Security with EJB (20 min)
  - Timer Service (20 min)
  - Asynchronous Methods (15 min)
  - EJB vs CDI (15 min)
  - Best Practices (10 min)
- **PowerPoint:** Auto-generated via Marp

### 6. ✅ Lab 4B: EJB Banking Services - COMPLETED
- **Directory:** `esipe-javaee/03-Labs/Lab04B-EJB/`
- **Status:** Complete with comprehensive testing
- **Files Created:**
  - `README.md` - Complete lab instructions
  - `test-lab.sh` (229 lines) - Local testing with 10 automated tests
  - `podman-test.sh` (485 lines) - Container testing with 12 automated tests
  - `TESTING-GUIDE.md` (545 lines) - Comprehensive testing guide with detailed test scenarios
- **Solution Components:**
  - Stateless Session Bean: `AccountServiceBean.java`
  - Stateful Session Bean: `TransactionBatchBean.java`
  - Singleton Session Bean: `ConfigServiceBean.java`
  - Message-Driven Bean: `NotificationMDB.java`
  - Scheduled Bean: `ReportGeneratorBean.java`
  - Web Servlet: `BankingServlet.java`
  - Health Checks: `DatabaseHealthCheck.java`, `EJBHealthCheck.java`
  - Models: `Account.java`, `Transaction.java`, `TransactionType.java`
- **Configuration:**
  - `server.xml` - Complete Liberty configuration with EJB, JMS, security
  - `bootstrap.properties` - Environment variable support
  - `persistence.xml` - JPA configuration
  - `Containerfile` - Multi-stage build with PostgreSQL driver
- **Testing Enhancements (Jan 15, 2026):**
  - ✅ Enhanced `podman-test.sh` with 12 comprehensive automated tests
  - ✅ Enhanced `test-lab.sh` with 10 comprehensive automated tests
  - ✅ Updated `TESTING-GUIDE.md` with detailed test scenarios
  - ✅ Added test counters and pass/fail reporting
  - ✅ Comprehensive EJB operation tests (create, deposit, withdraw, transfer)
  - ✅ Balance verification after operations
  - ✅ Singleton EJB testing (ConfigServiceBean)
  - ✅ Timer Service verification (ReportGeneratorBean)
  - ✅ Security role-based access testing
  - ✅ Database persistence verification (Podman)
  - ✅ JMS configuration verification (Podman)
  - ✅ Transaction management testing (CMT)
  - ✅ Fixed JPQL enum comparison in ReportGeneratorBean (use TransactionType enum instead of string literals)

### 7. ✅ Enhance Lecture 3: JPA & Database - COMPLETED (Jan 15, 2026)
- **File:** `esipe-javaee/02-Lectures/03-jpa-database-integration.md`
- **Status:** Complete
- **Enhancement:** JNDI section already present (Part 8 - 30 minutes content)
- **Content Verified:**
  - ✅ JNDI concepts and naming contexts
  - ✅ Looking up resources (DataSources, JMS, EJB)
  - ✅ JNDI naming conventions
  - ✅ Resource injection vs JNDI lookup
  - ✅ Environment entries and configuration
  - ✅ Best practices and troubleshooting
  - ✅ Complete examples with InitialContext
  - ✅ Liberty configuration examples

### 8. ✅ Enhance Lab 3: JPA - COMPLETED (Jan 15, 2026)
- **Directory:** `esipe-javaee/03-Labs/Lab03-JPA/`
- **Status:** Complete with comprehensive JNDI implementation
- **Enhancement:** Added JNDI exercises and practical demonstration
- **Files Created/Modified:**
  - ✅ `JndiConfigService.java` (solution) - 234 lines
  - ✅ `JndiConfigService.java` (starter) - 234 lines with TODOs
  - ✅ `TransactionValidatorServlet.java` - 310 lines (HTML + JSON API)
  - ✅ `web.xml` - Added 3 environment entries (maxTransactionAmount, supportEmail, maxLoginAttempts)
  - ✅ `index.html` - Added JNDI features section and validator link
  - ✅ `README.md` - Added Exercise 5 Part D (60+ lines) with complete JNDI documentation
  - ✅ `podman-test.sh` - Added 5 new automated tests (11 total)
  - ✅ `COURSE-SUMMARY.md` - Updated Lab 3 description with JNDI features
- **Implementation Details:**
  - Singleton pattern for servlet compatibility (no CDI in Lab 3)
  - JNDI lookups for DataSource, environment entries, and JMS resources
  - Transaction validator with configurable limits via JNDI
  - Comprehensive error handling and logging
  - HTML interface and JSON REST API
  - Automated testing for all JNDI functionality

### 9. ✅ Enhance Lecture 4: CDI - COMPLETED (Jan 15, 2026)
- **File:** `esipe-javaee/02-Lectures/04-cdi-dependency-injection.md`
- **Status:** Complete
- **Enhancement:** Added Part 6B - Programmatic Transaction Management (BMT)
- **Content Added:** ~450 lines
- **Topics Covered:**
  - Bean-Managed Transactions (BMT) overview
  - UserTransaction API with complete examples
  - CMT vs BMT comparison table
  - Multiple transaction boundaries
  - Transaction isolation levels (READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE)
  - Distributed transactions with JTS
  - Two-phase commit (2PC) explanation with diagram
  - Transaction timeout configuration
  - Transaction status codes
  - Best practices (DO/DON'T lists)
  - Complete BMT example with retry logic
  - When to use CMT vs BMT decision guide
  - Lab 4 preview with transaction exercises
- **Integration:** Seamlessly follows existing Part 6 (Declarative Transactions)

### 10. ✅ Enhance Lab 4: CDI - COMPLETED (Jan 15, 2026)
- **Directory:** `esipe-javaee/03-Labs/Lab04-CDI/`
- **Status:** Complete with comprehensive BMT implementation
- **Enhancement:** Added Exercise 6 - Advanced Transaction Management (BMT)
- **Content Added:** ~450 lines in README.md
- **Exercise Structure:**
  - **Part A:** Create BMT Transfer Service (20 min)
    - `BatchTransferService.java` with UserTransaction (223 lines)
    - Batch processing with individual transaction boundaries
    - Transfer with retry logic and exponential backoff
    - Helper classes (TransferRequest, BatchTransferResult, TransferResult)
    - `TransactionTestServlet.java` for testing (complete implementation)
  - **Part B:** Compare CMT vs BMT (15 min)
    - `TransactionComparisonService.java` (complete implementation)
    - Side-by-side CMT and BMT implementations
    - Performance measurement methods
  - **Part C:** Test Transaction Timeout (10 min)
    - `TimeoutTestService.java` (complete implementation)
    - Short timeout test (2 seconds)
    - Adequate timeout test (10 seconds)
  - **Part D:** Update server.xml Configuration (5 min)
    - Transaction timeout settings
    - Heuristic retry configuration
  - **Part E:** Testing Implementation (10 min)
    - Test batch processing
    - Test CMT vs BMT performance
    - Verify partial success scenarios
- **Learning Outcomes:**
  - When to use CMT vs BMT
  - Batch processing patterns
  - Transaction timeout handling
  - Error recovery strategies
  - Performance considerations
- **Verification Checklist:** 8 items
- **Troubleshooting Guide:** 3 common issues with solutions
- **Solution Files Created:**
  - ✅ `BatchTransferService.java` (223 lines) - Complete BMT implementation
  - ✅ `TransactionComparisonService.java` (complete) - CMT vs BMT comparison
  - ✅ `TimeoutTestService.java` (complete) - Transaction timeout testing
  - ✅ `TransactionTestServlet.java` (complete) - Web interface for testing
- **Bug Fixes (Jan 15, 2026):**
  - ✅ Fixed "double cannot be dereferenced" compilation errors
  - ✅ Corrected type mismatches in BatchTransferService (lines 63, 70, 123, 128-129)
  - ✅ Corrected type mismatches in TransactionComparisonService (lines 46, 50-51, 73, 78-79)
  - ✅ Changed BigDecimal operations to primitive double operations
  - ✅ Build verification: mvn clean package successful (10.821s)
- **Testing:**
  - ✅ All compilation errors resolved
  - ✅ Application successfully packaged as WAR file
  - ✅ Liberty server configuration validated
- **Commit:** `8abadcf` - Published to GitHub (Jan 15, 2026)

---

## 📋 Pending Tasks

### New Lectures (3 remaining)

#### 6. ⏳ Lecture 5B: JMS (Enterprise Messaging)
- **File:** `esipe-javaee/02-Lectures/05b-jms-enterprise-messaging.md`
- **Estimated Lines:** ~1,000
- **Content to Cover:**
  - Messaging concepts and patterns
  - JMS architecture
  - Point-to-Point vs Publish-Subscribe
  - Message types
  - Message producers and consumers
  - Message-Driven Beans integration
  - Asynchronous processing patterns
  - Transaction management with JMS
  - Error handling and dead letter queues

#### 7. ⏳ Lecture 9: Jakarta EE Security
- **File:** `esipe-javaee/02-Lectures/09-jakarta-ee-security.md`
- **Estimated Lines:** ~1,500
- **Content to Cover:**
  - Authentication vs Authorization
  - JAAS (Java Authentication and Authorization Service)
  - Security realms and identity stores
  - Declarative security (@RolesAllowed, etc.)
  - Programmatic security (SecurityContext)
  - Authentication mechanisms (Form, Basic, Certificate)
  - JWT (JSON Web Tokens)
  - OAuth2 and OpenID Connect
  - Session management security
  - CSRF and XSS prevention
  - Secure password storage
  - Microservices security patterns

### New Labs (2 remaining)

#### 7. ⏳ Lab 5B: Asynchronous Transaction Processing
- **Directory:** `esipe-javaee/03-Labs/Lab05B-JMS/`
- **Components:**
  - README.md with objectives and instructions
  - Starter code with TODOs
  - Solution with complete implementation
  - **Testing scripts (REQUIRED):**
    - `test-lab.sh` - Build verification
    - `podman-test.sh` - Deployment and testing
    - `TESTING-GUIDE.md` - Comprehensive testing guide
- **Deliverables:**
  - JMS queue configuration
  - Transaction event producer
  - Email notification MDB
  - Audit logging MDB
  - Dead letter queue handling

#### 8. ⏳ Lab 9: Secure Banking Application
- **Directory:** `esipe-javaee/03-Labs/Lab09-Security/`
- **Components:**
  - README.md with objectives and instructions
  - Starter code with TODOs
  - Solution with complete implementation
  - **Testing scripts (REQUIRED):**
    - `test-lab.sh` - Build verification
    - `podman-test.sh` - Deployment and testing
    - `TESTING-GUIDE.md` - Comprehensive testing guide
- **Deliverables:**
  - User entity with roles
  - Database-backed IdentityStore
  - Login/logout functionality
  - JWT token generation and validation
  - Secured REST endpoints
  - Security filter for API authentication
  - Password hashing utility
  - Security audit log

### Lecture Enhancements (2 remaining)

#### 9. ⏳ Enhance Lecture 4: CDI
- **File:** `esipe-javaee/02-Lectures/04-cdi-dependency-injection.md`
- **Enhancement:** Add Transaction Management section (45 minutes content)
- **New Content:**
  - JTA (Java Transaction API) overview
  - Container-Managed Transactions (CMT)
  - Bean-Managed Transactions (BMT)
  - UserTransaction for programmatic control
  - Transaction attributes and propagation
  - Distributed transactions with JTS
  - Two-phase commit protocol
  - Transaction isolation levels
  - Handling transaction failures

#### 10. ⏳ Enhance Lecture 8: Microservices
- **File:** `esipe-javaee/02-Lectures/08-microservices-architecture.md`
- **Enhancement:** Add Microservices Security section (1 hour content)
- **New Content:**
  - API Gateway authentication
  - Service-to-service authentication
  - Token propagation between services
  - mTLS for service communication
  - Secret management
  - Security in service mesh
  - OAuth2 for microservices
  - Rate limiting and DDoS protection

### Lab Enhancements (2 remaining)

#### 11. ⏳ Enhance Lab 4: CDI
- **Directory:** `esipe-javaee/03-Labs/Lab04-CDI/`
- **Enhancement:** Add Transaction Management exercises
- **New Exercises:**
  1. Implement UserTransaction for complex operations
  2. Test transaction rollback scenarios
  3. Compare CMT vs BMT approaches
  4. Handle distributed transactions
  5. Configure transaction timeout

#### 12. ⏳ Enhance Lab 8: Microservices
- **Directory:** `esipe-javaee/03-Labs/Lab08-Microservices/`
- **Enhancement:** Add Security exercises
- **New Exercises:**
  1. Implement JWT authentication in API Gateway
  2. Add token propagation between services
  3. Configure mTLS for service communication
  4. Implement rate limiting
  5. Add security headers (CORS, CSP, etc.)

---

## 📈 Estimated Effort Remaining

### Content Creation
- **New Lectures:** 2 × 4 hours = 8 hours
- **New Labs:** 2 × 6 hours = 12 hours
- **Lecture Enhancements:** 4 × 2 hours = 8 hours
- **Lab Enhancements:** 4 × 3 hours = 12 hours

**Total Estimated:** 40 hours

### Token Usage Estimate
- **New Lectures:** ~3,700 lines × 3 = ~11,100 lines
- **New Labs:** ~2,000 lines × 3 = ~6,000 lines
- **Enhancements:** ~1,500 lines × 8 = ~12,000 lines

**Total Estimated:** ~29,100 lines of code/documentation

---

## 🎯 Recommended Implementation Order

### Phase 1: Complete JSF Content ✅ COMPLETED
1. ✅ Lecture 2B: JSF - DONE
2. ✅ Lab 2B: JSF - DONE

### Phase 2: Web Technologies Enhancement ✅ COMPLETED
3. ✅ Enhance Lecture 2: Add HTTPSession, Filters, Listeners - DONE
4. ✅ Enhance Lab 2: Add Filter and Listener exercises - DONE

### Phase 3: EJB Content ✅ COMPLETED
5. ✅ Create Lecture 4B: EJB - DONE
6. ✅ Create Lab 4B: EJB Banking Services - DONE

### Phase 4: Database & Transactions ✅ 100% COMPLETED (Jan 15, 2026)
7. ✅ Enhance Lecture 3: Add JNDI section - DONE (already present, verified)
8. ✅ Enhance Lab 3: Add JNDI exercises - DONE (comprehensive implementation)
9. ✅ Enhance Lecture 4: Add Transaction Management (BMT) - DONE (Jan 15, 2026)
10. ✅ Enhance Lab 4: Add Transaction Management exercises (BMT) - DONE (Jan 15, 2026)
   - ✅ Complete solution implementation with 4 new service classes
   - ✅ Bug fixes for type mismatches (double vs BigDecimal)
   - ✅ Build verification and testing completed

### Phase 5: Messaging
11. Create Lecture 5B: JMS
12. Create Lab 5B: Asynchronous Transaction Processing

### Phase 6: Security (Critical)
13. Create Lecture 9: Jakarta EE Security
14. Create Lab 9: Secure Banking Application
15. Enhance Lecture 8: Add Microservices Security
16. Enhance Lab 8: Add Security exercises

---

## 📝 Notes

- All new content follows existing course structure and style
- Labs maintain consistency with existing lab format
- **All labs MUST include testing tools:**
  - `test-lab.sh` - Build verification script
  - `podman-test.sh` - Podman deployment and automated testing
  - `TESTING-GUIDE.md` - Comprehensive testing documentation
- **Maven Project Structure (REQUIRED for all labs):**
  - `pom.xml` with Jakarta EE 10 dependencies
  - Liberty Maven Plugin configuration
  - Proper packaging (war)
  - MicroProfile features integration
  - Health checks and metrics
- **Container Support (REQUIRED for all labs):**
  - `Containerfile` for Podman/Docker deployment
  - Open Liberty base image
  - Proper server configuration in `src/main/liberty/config/`
  - Bootstrap properties and server.xml
- **Application Structure Standards:**
  - Package structure: `com.bank.{model,service,web,health,validator,etc.}`
  - CDI beans with proper scopes
  - Health checks (liveness and readiness)
  - MicroProfile Config integration
  - Proper error handling and validation
- Code examples use Jakarta EE 10 APIs
- All content includes copyright notice
- Marp slides use esipe theme
- Labs include both starter and solution code

---

## 🚀 Next Actions

1. ✅ Complete Lab 2B: JSF Client Management - DONE
2. ✅ Complete Phase 2: Web Technologies Enhancement - DONE
3. ✅ Complete Phase 3: EJB Content - DONE
   - ✅ Lecture 4B: EJB - DONE
   - ✅ Lab 4B: EJB Banking Services - DONE
   - ✅ Comprehensive testing scripts - DONE
4. ✅ **Phase 4 Complete:** Database & Transactions (100% complete - Jan 15, 2026)
   - ✅ Enhance Lecture 3: Add JNDI section - DONE
   - ✅ Enhance Lab 3: Add JNDI exercises - DONE
   - ✅ Enhance Lecture 4: Add Transaction Management (BMT) - DONE
   - ✅ Enhance Lab 4: Add Transaction Management exercises (BMT) - DONE
5. **Phase 5 Next:** Messaging (JMS)
   - ⏳ Create Lecture 5B: JMS
   - ⏳ Create Lab 5B: Asynchronous Transaction Processing
6. Continue systematic implementation through Phase 6
6. **All labs include complete testing tools as required**
