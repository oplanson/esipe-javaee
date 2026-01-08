<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Jakarta EE Course Improvement Plan
## Adding Missing Concepts and Enhancing Content

**Date:** January 2026  
**Version:** 2.0 (Enhanced with Gap Analysis)
**Status:** Validated and Ready for Implementation

---

## 📋 Executive Summary

This document outlines a comprehensive, **validated** plan to enhance the Jakarta EE and Microservices course by incorporating missing key concepts that are essential for enterprise Java development.

**Validation Status:** ✅ Cross-referenced with existing lectures (see [`COURSE-GAP-ANALYSIS.md`](esipe-javaee/COURSE-GAP-ANALYSIS.md:1))

### Core Improvements (Must Have)
1. **Enterprise Java Beans (EJB)** - Business component model (Coverage: 1/10 → 8/10)
2. **JavaServer Faces (JSF)** - Component-based web framework (Coverage: 1/10 → 8/10)
3. **Web Technologies** - HTTPSession, Filters, Listeners (Coverage: 2-4/10 → 8-9/10)
4. **Enterprise Integration** - JMS, JNDI, JTA/JTS with UserTransaction (Coverage: 0-6/10 → 8/10)
5. **Security** - Jakarta EE Security and Microservices Security (Coverage: 3-4/10 → 9/10)

### Optional Enhancements (Nice to Have)
6. **Bean Validation** - Cross-layer validation guide (Coverage: 7/10 → 9/10)
7. **WebSocket** - Real-time communication (Coverage: 0/10 → 7/10)
8. **Batch Processing** - Enterprise batch operations (Coverage: 0/10 → 7/10)
9. **Concurrency Utilities** - Async processing patterns (Coverage: 0/10 → 7/10)
10. **Mail** - Email integration (Coverage: 0/10 → 6/10)

**Overall Course Coverage:**
- **Before:** 52% (11/21 Jakarta EE topics adequately covered)
- **After Core Plan:** 71% (15/21 topics adequately covered)
- **With Optional Topics:** 86% (18/21 topics adequately covered)

---

## 🎯 Current State Analysis

> **Note:** Detailed gap analysis available in [`COURSE-GAP-ANALYSIS.md`](esipe-javaee/COURSE-GAP-ANALYSIS.md:1)

### ✅ What's Already Covered (Validated)

| Topic | Current Coverage | Location | Status |
|-------|-----------------|----------|--------|
| **Servlets** | 8/10 | Lecture 2 | ✅ Good |
| **JSP** | 8/10 | Lecture 2 | ✅ Good |
| **CDI** | 9/10 | Lecture 4 | ✅ Excellent |
| **JPA** | 9/10 | Lecture 3 | ✅ Excellent |
| **JAX-RS** | 9/10 | Lecture 5 | ✅ Excellent |
| **JSON-B** | 8/10 | Lecture 5 | ✅ Good |
| **Bean Validation** | 7/10 | Lecture 5 (REST context) | ✅ Good |
| **HttpSession** | 3/10 | Lecture 2, line 492 | ⚠️ Basic only |
| **Filters** | 2-5/10 | Lectures 2, 8 (scattered) | ⚠️ Fragmented |
| **Listeners** | 4/10 | Lecture 3, line 1535 | ⚠️ One example |
| **JNDI** | 3/10 | Lecture 3, lines 1540-1630 | ⚠️ Basic only |
| **JTA** | 5-6/10 | Lectures 3-4 (declarative) | ⚠️ Partial |

### ❌ Critical Gaps Identified (Validated)

| # | Topic | Current | Gap Severity | Impact |
|---|-------|---------|--------------|--------|
| 1 | **EJB** | 1/10 | 🔴 Critical | Core enterprise feature missing |
| 2 | **JSF** | 1/10 | 🔴 Critical | Web framework incomplete |
| 3 | **JMS** | 0/10 | 🔴 Critical | No messaging coverage |
| 4 | **Security** | 3-4/10 | 🔴 Critical | Insufficient for production |
| 5 | **HTTPSession** | 3/10 | 🟡 Moderate | Missing advanced features |
| 6 | **Filters** | 2-5/10 | 🟡 Moderate | Scattered, needs consolidation |
| 7 | **Listeners** | 4/10 | 🟡 Moderate | Limited coverage |
| 8 | **JNDI** | 3/10 | 🟡 Moderate | Basic usage only |
| 9 | **UserTransaction** | 0/10 | 🟡 Moderate | Programmatic transactions missing |

### 🆕 Additional Opportunities Discovered

| Topic | Current | Priority | Recommendation |
|-------|---------|----------|----------------|
| **WebSocket** | 0/10 | 🟡 Medium | Real-time features |
| **Batch Processing** | 0/10 | 🟡 Medium | Enterprise operations |
| **Concurrency** | 0/10 | 🟡 Medium | Async patterns |
| **Mail** | 0/10 | 🟢 Low | Practical examples |
| **Bean Validation** | 7/10 | 🟢 Low | Cross-layer guide |

**Key Findings:**
- ✅ No duplication with existing content
- ✅ All instructor-identified gaps confirmed
- ✅ Integration points clearly defined
- ✅ Additional opportunities identified

---

## 🎓 Proposed Course Structure Enhancement

> **Validation:** Both options validated against existing content - no conflicts identified

### Option A: Extended Course (Recommended) - 60-66 hours

**Rationale:** Provides comprehensive coverage without compromising existing content quality

**New Structure:**
```
Session 1:  Jakarta EE Foundations (6h) ✅ EXISTING
Session 2:  Servlets and JSP (6h) 🔄 ENHANCED (Filters, Listeners, HTTPSession)
Session 2B: JavaServer Faces (JSF) (6h) 🆕 NEW
Session 3:  JPA and Database (6h) 🔄 ENHANCED (JNDI)
Session 4:  CDI (6h) 🔄 ENHANCED (UserTransaction)
Session 4B: Enterprise Java Beans (EJB) (6h) 🆕 NEW
Session 5:  REST APIs (6h) ✅ EXISTING
Session 5B: Enterprise Messaging (JMS) (3h) 🆕 NEW
Session 6:  Domain-Driven Design (6h) ✅ EXISTING
Session 7:  Hexagonal Architecture (6h) ✅ EXISTING
Session 8:  Microservices (6h) 🔄 ENHANCED (Security basics)
Session 9:  Jakarta EE Security (6h) 🆕 NEW

Total: 63-69 hours
Coverage: 71% → 86% (with optional topics)
```

**Benefits:**
- ✅ Comprehensive coverage of all critical topics
- ✅ No compromise on existing content quality
- ✅ Clear progression and learning path
- ✅ Time for hands-on practice
- ✅ Industry-ready skill set

**Challenges:**
- ⚠️ Requires additional course time
- ⚠️ May need schedule adjustment
- ⚠️ More content to maintain

---

### Option B: Integrated Approach (Maintained) - 48 hours

**Rationale:** Maintains current duration while adding essential topics

**Redistributed Structure:**
```
Session 1: Jakarta EE Foundations (6h)
  - Current content (4h)
  - EJB overview (1h)
  - JSF overview (1h)

Session 2: Web Technologies (6h)
  - Servlets and JSP (2h)
  - HTTPSession, Filters, Listeners (1.5h)
  - JSF Basics (1.5h)
  - Lab: Combined exercises (1h)

Session 3: Persistence and Messaging (6h)
  - JPA (2h)
  - JNDI (30 min)
  - JMS Basics (1h)
  - Lab: Database + Messaging (2.5h)

Session 4: Dependency Injection and EJB (6h)
  - CDI (2h)
  - EJB (1.5h)
  - UserTransaction (30 min)
  - Lab: CDI + EJB (2h)

Session 5: REST APIs (6h) ✅ EXISTING

Session 6: DDD (6h) ✅ EXISTING

Session 7: Hexagonal (6h) ✅ EXISTING

Session 8: Microservices and Security (6h)
  - Microservices patterns (2.5h)
  - Security fundamentals (1.5h)
  - Lab: Secure microservices (2h)

Total: 48 hours (maintained)
Coverage: 71% (core topics only)
```

**Benefits:**
- ✅ Maintains current course duration
- ✅ Covers all critical topics
- ✅ No schedule changes needed
- ✅ Easier to implement

**Challenges:**
- ⚠️ Less depth in some topics
- ⚠️ Faster pace required
- ⚠️ Less hands-on time
- ⚠️ Optional topics excluded

---

### 📊 Comparison Matrix

| Aspect | Option A (Extended) | Option B (Integrated) |
|--------|--------------------|-----------------------|
| **Duration** | 63-69 hours | 48 hours |
| **Coverage** | 86% (with optional) | 71% (core only) |
| **Depth** | Comprehensive | Adequate |
| **Hands-on Time** | Extensive | Moderate |
| **Implementation** | New sessions | Redistribute |
| **Flexibility** | High | Medium |
| **Student Workload** | Higher | Moderate |
| **Industry Readiness** | Excellent | Good |

### 🎯 Recommendation

**Option A (Extended Course)** is strongly recommended because:

1. **Complete Coverage:** All Jakarta EE core specifications adequately covered
2. **Quality Learning:** Sufficient time for understanding and practice
3. **Industry Alignment:** Matches enterprise development requirements
4. **Future-Proof:** Room for emerging technologies
5. **Validation:** Gap analysis confirms no content conflicts

**However**, if time constraints require Option B:
- Prioritize: Security > EJB > JSF > JMS
- Make WebSocket, Batch, Concurrency optional/self-study
- Provide supplementary materials for deeper learning

---

## 📚 Detailed Improvement Plan

### 1. EJB (Enterprise Java Beans)

#### New Lecture: "Lecture 4B: Enterprise Java Beans (EJB)"
**Duration:** 3 hours  
**Position:** After CDI lecture (between current Lecture 4 and 5)

**Topics to Cover:**
- EJB architecture and container services
- Session Beans (Stateless, Stateful, Singleton)
- Message-Driven Beans (MDB)
- EJB lifecycle and callbacks
- Transaction management with EJB
- Security with EJB (@RolesAllowed, @PermitAll, @DenyAll)
- EJB vs CDI: When to use what
- Timer Service and scheduling
- Asynchronous methods with @Asynchronous

**Code Examples:**
```java
// Stateless Session Bean
@Stateless
public class AccountServiceBean implements AccountService {
    @PersistenceContext
    private EntityManager em;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // Business logic
    }
}

// Message-Driven Bean
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", 
                             propertyValue = "jakarta.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", 
                             propertyValue = "java:/jms/queue/TransactionQueue")
})
public class TransactionProcessorMDB implements MessageListener {
    public void onMessage(Message message) {
        // Process message
    }
}
```

#### New Lab: "Lab 4B: EJB Banking Services"
**Duration:** 3 hours

**Objectives:**
- Convert CDI services to EJB Session Beans
- Implement Message-Driven Bean for async transaction processing
- Use EJB Timer Service for scheduled tasks
- Apply EJB security annotations
- Compare EJB vs CDI approaches

**Deliverables:**
- Stateless Session Beans for account operations
- Stateful Session Bean for shopping cart/transaction batch
- Singleton Session Bean for application configuration
- Message-Driven Bean for transaction notifications
- Scheduled task for daily balance reports

---

### 2. JSF (JavaServer Faces)

#### New Lecture: "Lecture 2B: JavaServer Faces (JSF)"
**Duration:** 3 hours  
**Position:** After Servlets/JSP lecture (between current Lecture 2 and 3)

**Topics to Cover:**
- JSF architecture and lifecycle
- Managed Beans and backing beans
- Facelets templating
- JSF components and composite components
- Navigation and page flow
- Validators and converters
- AJAX with JSF (f:ajax)
- PrimeFaces introduction (optional)
- JSF vs JSP: Modern web development

**Code Examples:**
```java
// Managed Bean
@Named
@ViewScoped
public class ClientBean implements Serializable {
    @Inject
    private ClientService clientService;
    
    private Client client = new Client();
    private List<Client> clients;
    
    @PostConstruct
    public void init() {
        loadClients();
    }
    
    public String save() {
        clientService.save(client);
        return "client-list?faces-redirect=true";
    }
}
```

```xhtml
<!-- Facelets Template -->
<h:form>
    <h:panelGrid columns="2">
        <h:outputLabel value="Name:" for="name"/>
        <h:inputText id="name" value="#{clientBean.client.name}" 
                     required="true"/>
        
        <h:outputLabel value="Email:" for="email"/>
        <h:inputText id="email" value="#{clientBean.client.email}">
            <f:validator validatorId="emailValidator"/>
        </h:inputText>
    </h:panelGrid>
    
    <h:commandButton value="Save" action="#{clientBean.save}">
        <f:ajax execute="@form" render="@form"/>
    </h:commandButton>
</h:form>
```

#### New Lab: "Lab 2B: JSF Client Management"
**Duration:** 3 hours

**Objectives:**
- Create JSF pages with Facelets
- Implement managed beans
- Use JSF validators and converters
- Implement AJAX functionality
- Create reusable composite components
- Apply JSF navigation

**Deliverables:**
- JSF-based client management interface
- Custom validators for email and phone
- AJAX-enabled search functionality
- Reusable address component
- Master-detail view with navigation

---

### 3. Web Technologies Deep Dive

#### Enhanced Lecture 2: Add Section on HTTPSession, Filters, Listeners

**New Section: "Advanced Web Technologies" (1 hour)**

**Topics to Cover:**

**A. HTTPSession Management**
- Session lifecycle and scope
- Session attributes and data storage
- Session timeout configuration
- Session clustering and replication
- Session security (session fixation, hijacking)
- Cookie-based vs URL rewriting

**B. Servlet Filters**
- Filter chain and ordering
- Authentication filters
- Logging filters
- Compression filters
- CORS filters
- Character encoding filters
- Filter patterns and best practices

**C. Servlet Listeners**
- ServletContextListener (application lifecycle)
- HttpSessionListener (session lifecycle)
- ServletRequestListener (request lifecycle)
- Attribute listeners
- Use cases and patterns

**Code Examples:**
```java
// Authentication Filter
@WebFilter(urlPatterns = "/secure/*")
public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        chain.doFilter(request, response);
    }
}

// Session Listener
@WebListener
public class SessionCounterListener implements HttpSessionListener {
    private static int activeSessions = 0;
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        activeSessions++;
        log.info("Session created. Active sessions: " + activeSessions);
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activeSessions--;
        log.info("Session destroyed. Active sessions: " + activeSessions);
    }
}

// Request Listener for logging
@WebListener
public class RequestLoggingListener implements ServletRequestListener {
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        log.info("Request started: " + request.getRequestURI());
    }
}
```

#### Enhanced Lab 2: Add Filter and Listener Exercises

**New Exercises:**
1. Implement authentication filter
2. Create logging filter with request/response timing
3. Add session listener for active user tracking
4. Implement CORS filter for REST API
5. Create compression filter for responses

---

### 4. JMS (Java Message Service)

#### New Lecture: "Lecture 5B: Enterprise Messaging with JMS"
**Duration:** 2 hours  
**Position:** After REST lecture or as part of EJB lecture

**Topics to Cover:**
- Messaging concepts and patterns
- JMS architecture (providers, destinations, messages)
- Point-to-Point (Queues) vs Publish-Subscribe (Topics)
- Message types (TextMessage, ObjectMessage, etc.)
- Message producers and consumers
- Message-Driven Beans (MDB)
- Asynchronous processing patterns
- Transaction management with JMS
- Error handling and dead letter queues

**Code Examples:**
```java
// JMS Producer
@Stateless
public class NotificationService {
    @Resource(lookup = "java:/jms/queue/NotificationQueue")
    private Queue notificationQueue;
    
    @Inject
    private JMSContext context;
    
    public void sendNotification(String message) {
        context.createProducer()
               .send(notificationQueue, message);
    }
}

// Message-Driven Bean Consumer
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", 
                             propertyValue = "jakarta.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", 
                             propertyValue = "java:/jms/queue/NotificationQueue")
})
public class NotificationMDB implements MessageListener {
    @Inject
    private EmailService emailService;
    
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String text = ((TextMessage) message).getText();
                emailService.send(text);
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
```

#### New Lab: "Lab 5B: Asynchronous Transaction Processing"
**Duration:** 2 hours

**Objectives:**
- Configure JMS queue in application server
- Implement JMS producer for transaction events
- Create Message-Driven Bean for processing
- Handle transaction notifications asynchronously
- Implement error handling and retry logic

**Deliverables:**
- JMS queue configuration
- Transaction event producer
- Email notification MDB
- Audit logging MDB
- Dead letter queue handling

---

### 5. JNDI (Java Naming and Directory Interface)

#### Enhanced Lecture 3: Add JNDI Section

**New Section: "JNDI and Resource Management" (30 minutes)**

**Topics to Cover:**
- JNDI concepts and naming contexts
- Looking up resources (DataSources, JMS, EJB)
- JNDI naming conventions
- Resource injection vs JNDI lookup
- Environment entries and configuration
- Best practices

**Code Examples:**
```java
// JNDI Lookup
public class ResourceLocator {
    public static DataSource getDataSource() throws NamingException {
        InitialContext ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:jboss/datasources/BankDS");
    }
    
    public static Queue getQueue(String queueName) throws NamingException {
        InitialContext ctx = new InitialContext();
        return (Queue) ctx.lookup("java:/jms/queue/" + queueName);
    }
    
    public static <T> T getEJB(Class<T> beanClass) throws NamingException {
        InitialContext ctx = new InitialContext();
        String jndiName = "java:global/banking-app/" + 
                         beanClass.getSimpleName();
        return (T) ctx.lookup(jndiName);
    }
}

// Environment Entries
@Stateless
public class ConfigurationService {
    @Resource(lookup = "java:comp/env/maxTransferAmount")
    private BigDecimal maxTransferAmount;
    
    @Resource(lookup = "java:comp/env/supportEmail")
    private String supportEmail;
}
```

#### Enhanced Lab 3: Add JNDI Exercises

**New Exercises:**
1. Look up DataSource using JNDI
2. Configure environment entries in web.xml
3. Look up EJB references
4. Use JNDI for configuration management

---

### 6. JTA/JTS with UserTransaction

#### Enhanced Lecture 4: Add Transaction Management Section

**New Section: "Advanced Transaction Management" (45 minutes)**

**Topics to Cover:**
- JTA (Java Transaction API) overview
- Container-Managed Transactions (CMT)
- Bean-Managed Transactions (BMT)
- UserTransaction for programmatic control
- Transaction attributes and propagation
- Distributed transactions with JTS
- Two-phase commit protocol
- Transaction isolation levels
- Handling transaction failures

**Code Examples:**
```java
// Programmatic Transaction with UserTransaction
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class TransferService {
    @PersistenceContext
    private EntityManager em;
    
    @Resource
    private UserTransaction userTransaction;
    
    public void complexTransfer(Long fromId, Long toId, BigDecimal amount) 
            throws Exception {
        try {
            userTransaction.begin();
            
            // First operation
            Account from = em.find(Account.class, fromId);
            from.withdraw(amount);
            
            // Simulate external call
            externalPaymentGateway.process(amount);
            
            // Second operation
            Account to = em.find(Account.class, toId);
            to.deposit(amount);
            
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (SystemException se) {
                throw new RuntimeException("Rollback failed", se);
            }
            throw e;
        }
    }
}

// Declarative Transaction Attributes
@Stateless
public class AccountService {
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deposit(Long accountId, BigDecimal amount) {
        // Joins existing transaction or creates new one
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void auditLog(String action) {
        // Always creates new transaction
    }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Account findById(Long id) {
        // Runs in transaction if one exists, otherwise non-transactional
    }
}
```

#### Enhanced Lab 4: Add Transaction Management Exercises

**New Exercises:**
1. Implement UserTransaction for complex operations
2. Test transaction rollback scenarios
3. Compare CMT vs BMT approaches
4. Handle distributed transactions
5. Configure transaction timeout

---

### 7. Security (New Comprehensive Coverage)

#### New Lecture: "Lecture 9: Jakarta EE Security"
**Duration:** 3 hours  
**Position:** After Hexagonal Architecture, before or alongside Microservices

**Topics to Cover:**

**Part A: Jakarta EE Security Fundamentals**
- Authentication vs Authorization
- JAAS (Java Authentication and Authorization Service)
- Security realms and identity stores
- Declarative security (@RolesAllowed, @PermitAll, @DenyAll)
- Programmatic security (SecurityContext)
- Form-based authentication
- HTTP Basic/Digest authentication
- Client certificate authentication

**Part B: Modern Security Patterns**
- JWT (JSON Web Tokens)
- OAuth2 and OpenID Connect
- API key authentication
- Session management security
- CSRF protection
- XSS prevention
- SQL injection prevention
- Secure password storage (bcrypt, PBKDF2)

**Part C: Microservices Security**
- Service-to-service authentication
- API Gateway security
- mTLS (mutual TLS)
- Token propagation
- Security in distributed systems

**Code Examples:**
```java
// Jakarta Security - Identity Store
@ApplicationScoped
public class DatabaseIdentityStore implements IdentityStore {
    @Inject
    private UserRepository userRepository;
    
    @Override
    public CredentialValidationResult validate(
            UsernamePasswordCredential credential) {
        User user = userRepository.findByUsername(credential.getCaller());
        
        if (user != null && verifyPassword(credential.getPassword(), 
                                          user.getPasswordHash())) {
            return new CredentialValidationResult(
                user.getUsername(), 
                new HashSet<>(user.getRoles())
            );
        }
        return CredentialValidationResult.INVALID_RESULT;
    }
}

// Declarative Security
@Path("/accounts")
@Stateless
public class AccountResource {
    @GET
    @RolesAllowed({"USER", "ADMIN"})
    public List<Account> getAccounts() {
        // Accessible by USER and ADMIN roles
    }
    
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteAccount(@PathParam("id") Long id) {
        // Only ADMIN can delete
    }
    
    @GET
    @Path("/public")
    @PermitAll
    public Response getPublicInfo() {
        // Accessible by everyone
    }
}

// Programmatic Security
@Inject
private SecurityContext securityContext;

public void performOperation() {
    if (securityContext.isCallerInRole("ADMIN")) {
        // Admin-specific logic
    }
    
    String username = securityContext.getCallerPrincipal().getName();
}

// JWT Authentication
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthenticationFilter implements ContainerRequestFilter {
    @Override
    public void doFilter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
                    
                String username = claims.getSubject();
                List<String> roles = claims.get("roles", List.class);
                
                SecurityContext securityContext = 
                    new JWTSecurityContext(username, roles);
                requestContext.setSecurityContext(securityContext);
            } catch (JwtException e) {
                requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build()
                );
            }
        }
    }
}

// Password Hashing
public class PasswordUtil {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
    public static boolean verifyPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
```

#### New Lab: "Lab 9: Secure Banking Application"
**Duration:** 3 hours

**Objectives:**
- Implement user authentication with Jakarta Security
- Create custom IdentityStore with database
- Apply role-based authorization
- Implement JWT authentication for REST API
- Add security filters and interceptors
- Secure passwords with bcrypt
- Implement CSRF protection
- Add audit logging for security events

**Deliverables:**
- User entity with roles
- Database-backed IdentityStore
- Login/logout functionality
- JWT token generation and validation
- Secured REST endpoints with @RolesAllowed
- Security filter for API authentication
- Password hashing utility
- Security audit log
- HTTPS configuration

---

### 8. Microservices Security Enhancement

#### Enhanced Lecture 8: Add Security Section

**New Section: "Microservices Security Patterns" (1 hour)**

**Topics to Cover:**
- API Gateway authentication
- Service-to-service authentication
- Token propagation between services
- mTLS for service communication
- Secret management
- Security in service mesh
- OAuth2 for microservices
- Rate limiting and DDoS protection

**Code Examples:**
```java
// API Gateway Security
@Path("/api")
public class APIGateway {
    @Inject
    private JWTValidator jwtValidator;
    
    @GET
    @Path("/clients/{id}")
    public Response getClient(@PathParam("id") Long id,
                             @HeaderParam("Authorization") String token) {
        // Validate JWT
        if (!jwtValidator.validate(token)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        // Forward to client service with token
        return clientService.getClient(id, token);
    }
}

// Service-to-Service Authentication
@RegisterRestClient(configKey = "account-service")
public interface AccountServiceClient {
    @GET
    @Path("/accounts/{id}")
    Response getAccount(@PathParam("id") Long id,
                       @HeaderParam("Authorization") String token);
}

// Token Propagation Filter
@Provider
public class TokenPropagationFilter implements ClientRequestFilter {
    @Inject
    private SecurityContext securityContext;
    
    @Override
    public void filter(ClientRequestContext requestContext) {
        String token = securityContext.getToken();
        if (token != null) {
            requestContext.getHeaders()
                .add("Authorization", "Bearer " + token);
        }
    }
}
```

#### Enhanced Lab 8: Add Security Exercises

**New Exercises:**
1. Implement JWT authentication in API Gateway
2. Add token propagation between services
3. Configure mTLS for service communication
4. Implement rate limiting
5. Add security headers (CORS, CSP, etc.)

---

## 📊 Implementation Roadmap

### Phase 1: Planning and Design (Week 1-2)
- [x] Analyze current course structure
- [x] Identify gaps and missing topics
- [ ] Create detailed improvement plan
- [ ] Review plan with stakeholders
- [ ] Prioritize topics based on importance

### Phase 2: Content Creation (Week 3-8)

**Week 3-4: EJB Content**
- [ ] Create Lecture 4B: Enterprise Java Beans
- [ ] Develop Lab 4B: EJB Banking Services
- [ ] Create code examples and demos
- [ ] Write lab instructions and solutions

**Week 5: JSF Content**
- [ ] Create Lecture 2B: JavaServer Faces
- [ ] Develop Lab 2B: JSF Client Management
- [ ] Create Facelets templates and examples
- [ ] Write lab instructions and solutions

**Week 6: Web Technologies Enhancement**
- [ ] Enhance Lecture 2 with HTTPSession, Filters, Listeners
- [ ] Update Lab 2 with new exercises
- [ ] Create comprehensive examples
- [ ] Update lab solutions

**Week 7: JMS and JNDI**
- [ ] Create Lecture 5B: Enterprise Messaging with JMS
- [ ] Enhance Lecture 3 with JNDI section
- [ ] Develop Lab 5B: Asynchronous Processing
- [ ] Update Lab 3 with JNDI exercises
- [ ] Create JMS configuration guides

**Week 8: Transaction Management**
- [ ] Enhance Lecture 4 with JTA/JTS section
- [ ] Update Lab 4 with UserTransaction exercises
- [ ] Create transaction management examples
- [ ] Document best practices

### Phase 3: Security Content (Week 9-10)

**Week 9: Jakarta EE Security**
- [ ] Create Lecture 9: Jakarta EE Security
- [ ] Develop Lab 9: Secure Banking Application
- [ ] Create authentication/authorization examples
- [ ] Write security best practices guide

**Week 10: Microservices Security**
- [ ] Enhance Lecture 8 with security section
- [ ] Update Lab 8 with security exercises
- [ ] Create JWT and OAuth2 examples
- [ ] Document microservices security patterns

### Phase 4: Integration and Testing (Week 11-12)

**Week 11: Integration**
- [ ] Update course outline and README
- [ ] Update COURSE-SUMMARY.md
- [ ] Create navigation between topics
- [ ] Ensure consistency across materials
- [ ] Update all references and links

**Week 12: Testing and Refinement**
- [ ] Test all code examples
- [ ] Verify all labs work correctly
- [ ] Review for technical accuracy
- [ ] Proofread all content
- [ ] Create instructor notes
- [ ] Prepare presentation materials

### Phase 5: Deployment (Week 13)
- [ ] Convert new lectures to PowerPoint
- [ ] Package all materials
- [ ] Create student handouts
- [ ] Prepare assessment materials
- [ ] Final review and approval

---

## 🎯 Integration Strategy

### Option A: Extended Course (Recommended)

**New Course Structure (60-66 hours):**

```
Session 1: Jakarta EE Foundations (6h) - EXISTING
Session 2: Servlets and JSP (6h) - ENHANCED with Filters/Listeners
Session 2B: JavaServer Faces (JSF) (6h) - NEW
Session 3: JPA and Database (6h) - ENHANCED with JNDI
Session 4: CDI (6h) - ENHANCED with JTA/UserTransaction
Session 4B: Enterprise Java Beans (EJB) (6h) - NEW
Session 5: REST APIs (6h) - EXISTING
Session 5B: Enterprise Messaging (JMS) (3h) - NEW
Session 6: Domain-Driven Design (6h) - EXISTING
Session 7: Hexagonal Architecture (6h) - EXISTING
Session 8: Microservices (6h) - ENHANCED with Security
Session 9: Jakarta EE Security (6h) - NEW

Total: 63-69 hours
```

### Option B: Integrated Approach (48 hours maintained)

**Redistribute content within existing sessions:**

```
Session 1: Jakarta EE Foundations (6h)
  - Add EJB overview (30 min)
  - Add JSF overview (30 min)

Session 2: Web Technologies (6h)
  - Servlets and JSP (2h)
  - HTTPSession, Filters, Listeners (1.5h)
  - JSF Basics (1.5h)
  - Lab: Combined exercises (1h)

Session 3: Persistence and Messaging (6h)
  - JPA (2h)
  - JNDI (30 min)
  - JMS Basics (1h)
  - Lab: Database + Messaging (2.5h)

Session 4: Dependency Injection and EJB (6h)
  - CDI (2h)
  - EJB (1.5h)
  - JTA/UserTransaction (30 min)
  - Lab: CDI + EJB (2h)

Session 5: REST APIs (6h) - EXISTING

Session 6: DDD (6h) - EXISTING

Session 7: Hexagonal (6h) - EXISTING

Session 8: Microservices and Security (6h)
  - Microservices patterns (2.5h)
  - Security fundamentals (1.5h)
  - Lab: Secure microservices (2h)

Total: 48 hours (maintained)
```

---

## 📝 Lab Integration Strategy

### Existing Labs Enhancement

**Lab 1: First Servlet**
- Add: Session management exercise
- Add: Simple authentication filter

**Lab 2: Servlets and JSP**
- Add: Comprehensive filter chain
- Add: Session listener for tracking
- Add: JSF alternative implementation (optional)

**Lab 3: JPA**
- Add: JNDI DataSource lookup
- Add: JMS producer for audit events
- Add: Transaction management examples

**Lab 4: CDI**
- Add: UserTransaction examples
- Add: EJB comparison exercises
- Add: Message-Driven Bean for notifications

**Lab 5: REST**
- Add: JWT authentication
- Add: Role-based authorization
- Add: Security headers

**Lab 6-8: Existing**
- Add security considerations to each lab
- Ensure all labs follow security best practices

---

## 🎓 Learning Outcomes Enhancement

### Additional Learning Outcomes

After completing the enhanced course, students will be able to:

**Enterprise Components:**
1. Design and implement EJB Session Beans
2. Create Message-Driven Beans for asynchronous processing
3. Build JSF-based web applications
4. Choose appropriate component model (CDI vs EJB)

**Web Technologies:**
5. Implement comprehensive session management
6. Create servlet filters for cross-cutting concerns
7. Use servlet listeners for lifecycle management
8. Secure web applications effectively

**Enterprise Integration:**
9. Implement JMS messaging patterns
10. Use JNDI for resource lookup
11. Manage programmatic transactions with UserTransaction
12. Handle distributed transactions

**Security:**
13. Implement authentication and authorization
14. Secure REST APIs with JWT
15. Apply security best practices
16. Secure microservices architectures

---

## 📚 Additional Resources Needed

### Books and Documentation
- "Enterprise JavaBeans 3.1" by Andrew Lee Rubinger
- "JavaServer Faces: The Complete Reference" by Chris Schalk
- "Java Message Service" by Mark Richards
- "Java Security" by Scott Oaks
- Jakarta EE Security specification
- WildFly/OpenLiberty security documentation

### Tools and Software
- JMS provider (ActiveMQ Artemis or WildFly built-in)
- Security testing tools (OWASP ZAP, Burp Suite)
- JWT libraries (jose4j, jjwt)
- Password hashing libraries (jBCrypt)

### Example Applications
- Complete EJB banking application
- JSF client management system
- JMS-based notification system
- Secured microservices demo

---

## ✅ Success Criteria

The course improvement will be considered successful when:

1. **Content Completeness**
   - [ ] All missing topics covered comprehensively
   - [ ] Each topic has dedicated lecture content
   - [ ] Each topic has practical lab exercises
   - [ ] All code examples tested and working

2. **Quality Standards**
   - [ ] Content follows same format as existing lectures
   - [ ] Labs follow same structure as existing labs
   - [ ] All materials in English
   - [ ] Professional presentation quality
   - [ ] Technically accurate and up-to-date

3. **Integration**
   - [ ] New content integrates seamlessly
   - [ ] Clear progression between topics
   - [ ] Consistent terminology and style
   - [ ] Updated course outline and documentation

4. **Practical Application**
   - [ ] Students can implement all concepts
   - [ ] Labs build on each other progressively
   - [ ] Real-world banking application enhanced
   - [ ] Security best practices demonstrated

5. **Assessment**
   - [ ] New quiz questions created
   - [ ] Lab assessment criteria defined
   - [ ] Final project updated to include new concepts
   - [ ] Rubrics updated

---

## 🚀 Next Steps

### Immediate Actions (This Week)
1. Review and approve this improvement plan
2. Decide on Option A (extended) vs Option B (integrated)
3. Prioritize topics if time-constrained
4. Allocate resources for content creation
5. Set up development environment for new examples

### Short-term Actions (Next 2 Weeks)
1. Begin EJB lecture and lab creation
2. Start JSF content development
3. Enhance existing lectures with missing topics
4. Create code examples and test applications

### Medium-term Actions (Next 2 Months)
1. Complete all new content creation
2. Test all labs and examples
3. Review and refine materials
4. Prepare instructor guides
5. Create assessment materials

### Long-term Actions (Next 3 Months)
1. Pilot new content with test group
2. Gather feedback and iterate
3. Finalize all materials
4. Prepare for full course delivery
5. Create supplementary resources

---

## 📞 Questions for Discussion

1. **Course Duration:** Should we extend to 60+ hours (Option A) or maintain 48 hours (Option B)?

2. **Topic Priority:** If time-constrained, which topics are most critical?
   - EJB (enterprise components)
   - JSF (web framework)
   - JMS (messaging)
   - Security (authentication/authorization)

3. **Lab Approach:** Should we create separate labs for each new topic or integrate into existing labs?

4. **Security Depth:** How deep should we go into security topics?
   - Basic authentication/authorization only
   - Include JWT and OAuth2
   - Cover microservices security patterns
   - Include security testing and penetration testing

5. **Assessment:** How should new topics be assessed?
   - Additional quizzes
   - Enhanced final project
   - Separate security project
   - Certification exam preparation

6. **Tools and Infrastructure:** What additional tools/servers are needed?
   - JMS provider setup
   - Security testing tools
   - Additional databases
   - Cloud deployment options

---

## 📊 Estimated Effort

### Content Creation
- **EJB:** 40 hours (lecture + lab + examples)
- **JSF:** 40 hours (lecture + lab + examples)
- **Web Technologies:** 20 hours (enhancements)
- **JMS:** 30 hours (lecture + lab + examples)
- **JNDI:** 10 hours (enhancements)
- **JTA/UserTransaction:** 15 hours (enhancements)
- **Security:** 50 hours (lecture + lab + examples)
- **Integration:** 30 hours (updates and testing)

**Total Estimated Effort:** 235 hours (~6 weeks full-time)

### Review and Testing
- **Technical Review:** 40 hours
- **Lab Testing:** 30 hours
- **Documentation:** 20 hours
- **Refinement:** 20 hours

**Total Review Effort:** 110 hours (~3 weeks full-time)

**Grand Total:** 345 hours (~9 weeks full-time or 18 weeks half-time)

---

## 📝 Conclusion

This comprehensive improvement plan has been **validated** through detailed gap analysis and addresses all missing concepts in the Jakarta EE course.

### ✅ Validation Summary

**Gap Analysis Completed:** All 9 instructor-identified gaps confirmed and addressed
- See detailed analysis in [`COURSE-GAP-ANALYSIS.md`](esipe-javaee/COURSE-GAP-ANALYSIS.md:1)
- No content duplication identified
- Integration points clearly defined
- Additional opportunities discovered

**Coverage Improvement:**
- **Before:** 52% (11/21 Jakarta EE topics adequately covered)
- **After Core Plan:** 71% (15/21 topics adequately covered)
- **With Optional Topics:** 86% (18/21 topics adequately covered)

### 🎯 Core Improvements (Validated)

| Topic | Before | After | Status |
|-------|--------|-------|--------|
| **EJB** | 1/10 | 8/10 | ✅ New Lecture 4B + Lab 4B |
| **JSF** | 1/10 | 8/10 | ✅ New Lecture 2B + Lab 2B |
| **JMS** | 0/10 | 8/10 | ✅ New Lecture 5B + Lab 5B |
| **Security** | 3-4/10 | 9/10 | ✅ New Lecture 9 + Lab 9 |
| **HTTPSession** | 3/10 | 8/10 | ✅ Enhanced Lecture 2 |
| **Filters** | 2-5/10 | 9/10 | ✅ Consolidated in Lecture 2 |
| **Listeners** | 4/10 | 8/10 | ✅ Enhanced Lecture 2 |
| **JNDI** | 3/10 | 7/10 | ✅ Enhanced Lecture 3 |
| **UserTransaction** | 0/10 | 8/10 | ✅ Enhanced Lecture 4 |

### 🆕 Optional Enhancements (Discovered)

| Topic | Priority | Recommendation |
|-------|----------|----------------|
| **Bean Validation** | 🟢 Low | Cross-reference guide |
| **WebSocket** | 🟡 Medium | Real-time features |
| **Batch Processing** | 🟡 Medium | Enterprise operations |
| **Concurrency** | 🟡 Medium | Async patterns |
| **Mail** | 🟢 Low | Practical examples |

### 📊 Implementation Options

**Option A: Extended Course (60-66 hours)** - ⭐ Recommended
- Comprehensive coverage (86% with optional topics)
- No compromise on quality
- Industry-ready graduates
- Clear learning progression

**Option B: Integrated Approach (48 hours)** - Alternative
- Core topics covered (71%)
- Maintains current duration
- Faster pace required
- Optional topics excluded

### 🚀 Next Steps

1. **Immediate:** Review and approve this plan
2. **Decision:** Choose Option A or Option B
3. **Optional Topics:** Decide which to include
4. **Timeline:** Set implementation schedule
5. **Resources:** Allocate content creation resources

### ✅ Ready for Implementation

This plan is:
- ✅ Validated against existing content
- ✅ Free of duplication
- ✅ Properly prioritized
- ✅ Comprehensive and actionable
- ✅ Aligned with industry needs

**All critical gaps identified by the instructor are properly addressed with detailed implementation plans, code examples, and lab exercises.**

---

## 📞 Final Questions for Approval

1. **Duration:** Option A (60-66h) or Option B (48h)?
2. **Optional Topics:** Include WebSocket, Batch, Concurrency, Mail?
3. **Priority:** Confirm implementation order (Security → EJB → JSF → JMS)?
4. **Timeline:** When to start implementation?
5. **Resources:** Who will create content?

---

**Document Status:** ✅ Validated and Ready for Approval
**Gap Analysis:** [`COURSE-GAP-ANALYSIS.md`](esipe-javaee/COURSE-GAP-ANALYSIS.md:1)
**Next Review Date:** Awaiting instructor approval
**Approved By:** [Pending]
**Implementation Start:** [Pending approval]

---

**Prepared by:** IBM Bob
**Date:** January 8, 2026
**Version:** 2.0 (Enhanced with Gap Analysis)
**Status:** Ready for Implementation