<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Course Improvement Implementation Status

**Date:** January 12, 2026  
**Based on:** COURSE-IMPROVEMENT-PLAN.md

---

## 📊 Overall Progress

**Total Tasks:** 21
**Completed:** 6 (29%)
**In Progress:** 1 (5%)
**Pending:** 14 (67%)

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

## 🔄 In Progress

### 3. 🔄 Enhance Lecture 2: Servlets & JSP - IN PROGRESS
- **File:** `esipe-javaee/02-Lectures/02-servlets-jsp-microprofile.md`
- **Enhancement:** Add section on HTTPSession, Filters, Listeners (1 hour content)

---

## 📋 Pending Tasks

### New Lectures (3 remaining)

#### 3. ⏳ Lecture 4B: EJB (Enterprise Java Beans)
- **File:** `esipe-javaee/02-Lectures/04b-ejb-enterprise-java-beans.md`
- **Estimated Lines:** ~1,200
- **Content to Cover:**
  - EJB architecture and container services
  - Session Beans (Stateless, Stateful, Singleton)
  - Message-Driven Beans (MDB)
  - EJB lifecycle and callbacks
  - Transaction management with EJB
  - Security with EJB
  - EJB vs CDI comparison
  - Timer Service and scheduling
  - Asynchronous methods

#### 4. ⏳ Lecture 5B: JMS (Enterprise Messaging)
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

#### 5. ⏳ Lecture 9: Jakarta EE Security
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

### New Labs (3 remaining)

#### 4. ⏳ Lab 4B: EJB Banking Services
- **Directory:** `esipe-javaee/03-Labs/Lab04B-EJB/`
- **Components:**
  - README.md with objectives and instructions
  - Starter code with TODOs
  - Solution with complete implementation
  - **Testing scripts (REQUIRED):**
    - `test-lab.sh` - Build verification
    - `podman-test.sh` - Deployment and testing
    - `TESTING-GUIDE.md` - Comprehensive testing guide
- **Deliverables:**
  - Stateless Session Beans for account operations
  - Stateful Session Bean for transaction batch
  - Singleton Session Bean for configuration
  - Message-Driven Bean for notifications
  - Scheduled task for reports

#### 5. ⏳ Lab 5B: Asynchronous Transaction Processing
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

#### 6. ⏳ Lab 9: Secure Banking Application
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

### Lecture Enhancements (4 remaining)

#### 7. ⏳ Enhance Lecture 2: Servlets & JSP
- **File:** `esipe-javaee/02-Lectures/02-servlets-jsp-microprofile.md`
- **Enhancement:** Add section on HTTPSession, Filters, Listeners (1 hour content)
- **New Content:**
  - HTTPSession Management (lifecycle, security, clustering)
  - Servlet Filters (chain, authentication, logging, CORS)
  - Servlet Listeners (context, session, request)
  - Code examples for each
  - Best practices

#### 8. ⏳ Enhance Lecture 3: JPA & Database
- **File:** `esipe-javaee/02-Lectures/03-jpa-database-integration.md`
- **Enhancement:** Add JNDI section (30 minutes content)
- **New Content:**
  - JNDI concepts and naming contexts
  - Looking up resources (DataSources, JMS, EJB)
  - JNDI naming conventions
  - Resource injection vs JNDI lookup
  - Environment entries and configuration
  - Best practices

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

### Lab Enhancements (4 remaining)

#### 11. ⏳ Enhance Lab 2: Servlets & JSP
- **Directory:** `esipe-javaee/03-Labs/Lab02-ServletsJSP/`
- **Enhancement:** Add Filter and Listener exercises
- **New Exercises:**
  1. Implement authentication filter
  2. Create logging filter with request/response timing
  3. Add session listener for active user tracking
  4. Implement CORS filter for REST API
  5. Create compression filter for responses

#### 12. ⏳ Enhance Lab 3: JPA
- **Directory:** `esipe-javaee/03-Labs/Lab03-JPA/`
- **Enhancement:** Add JNDI exercises
- **New Exercises:**
  1. Look up DataSource using JNDI
  2. Configure environment entries in web.xml
  3. Look up EJB references
  4. Use JNDI for configuration management

#### 13. ⏳ Enhance Lab 4: CDI
- **Directory:** `esipe-javaee/03-Labs/Lab04-CDI/`
- **Enhancement:** Add Transaction Management exercises
- **New Exercises:**
  1. Implement UserTransaction for complex operations
  2. Test transaction rollback scenarios
  3. Compare CMT vs BMT approaches
  4. Handle distributed transactions
  5. Configure transaction timeout

#### 14. ⏳ Enhance Lab 8: Microservices
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
- **New Lectures:** 3 × 4 hours = 12 hours
- **New Labs:** 3 × 6 hours = 18 hours
- **Lecture Enhancements:** 4 × 2 hours = 8 hours
- **Lab Enhancements:** 4 × 3 hours = 12 hours

**Total Estimated:** 50 hours

### Token Usage Estimate
- **New Lectures:** ~3,700 lines × 3 = ~11,100 lines
- **New Labs:** ~2,000 lines × 3 = ~6,000 lines
- **Enhancements:** ~1,500 lines × 8 = ~12,000 lines

**Total Estimated:** ~29,100 lines of code/documentation

---

## 🎯 Recommended Implementation Order

### Phase 1: Complete JSF Content (Current)
1. ✅ Lecture 2B: JSF - DONE
2. 🔄 Lab 2B: JSF - IN PROGRESS

### Phase 2: Web Technologies Enhancement
3. Enhance Lecture 2: Add HTTPSession, Filters, Listeners
4. Enhance Lab 2: Add Filter and Listener exercises

### Phase 3: EJB Content
5. Create Lecture 4B: EJB
6. Create Lab 4B: EJB Banking Services

### Phase 4: Database & Transactions
7. Enhance Lecture 3: Add JNDI section
8. Enhance Lab 3: Add JNDI exercises
9. Enhance Lecture 4: Add Transaction Management
10. Enhance Lab 4: Add Transaction Management exercises

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
2. Review Phase 1 completion with instructor
3. Proceed to Phase 2: Web Technologies Enhancement
4. Continue systematic implementation through Phase 6
5. **Ensure all new labs include complete testing tools**
