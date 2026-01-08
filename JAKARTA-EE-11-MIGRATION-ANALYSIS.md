<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Jakarta EE 11 Migration Analysis
## Upgrading Course from Jakarta EE 10 to Jakarta EE 11

**Date:** January 8, 2026  
**Current Version:** Jakarta EE 10.0.0  
**Target Version:** Jakarta EE 11.0.0  
**Status:** Analysis and Planning

---

## 📋 Executive Summary

Jakarta EE 11 was released in **2024** and brings significant updates to the platform. This document analyzes the changes required to migrate the course from Jakarta EE 10 to Jakarta EE 11.

**Key Findings:**
- ✅ **Backward Compatible:** Most Jakarta EE 10 code will work with EE 11
- 🔄 **API Updates:** Several specifications have new versions
- 🆕 **New Features:** Enhanced capabilities in multiple areas
- ⚠️ **Breaking Changes:** Minimal, mostly deprecations
- 📦 **Dependencies:** Version updates required

**Migration Effort:** Low to Moderate (estimated 2-3 weeks)

---

## 🎯 Jakarta EE 11 - What's New

### Major Version Updates

| Specification | EE 10 Version | EE 11 Version | Status |
|---------------|---------------|---------------|--------|
| **Jakarta Servlet** | 6.0 | 6.1 | 🔄 Updated |
| **Jakarta Server Pages (JSP)** | 3.1 | 4.0 | 🔄 Updated |
| **Jakarta Expression Language (EL)** | 5.0 | 6.0 | 🔄 Updated |
| **Jakarta Server Faces (JSF)** | 4.0 | 4.1 | 🔄 Updated |
| **Jakarta RESTful Web Services (JAX-RS)** | 3.1 | 4.0 | 🔄 Major Update |
| **Jakarta Contexts and Dependency Injection (CDI)** | 4.0 | 4.1 | 🔄 Updated |
| **Jakarta Persistence (JPA)** | 3.1 | 3.2 | 🔄 Updated |
| **Jakarta Bean Validation** | 3.0 | 3.1 | 🔄 Updated |
| **Jakarta JSON Binding (JSON-B)** | 3.0 | 3.0 | ✅ Unchanged |
| **Jakarta JSON Processing (JSON-P)** | 2.1 | 2.1 | ✅ Unchanged |
| **Jakarta Enterprise Beans (EJB)** | 4.0 | 4.0 | ✅ Unchanged |
| **Jakarta Messaging (JMS)** | 3.1 | 3.1 | ✅ Unchanged |
| **Jakarta Transactions (JTA)** | 2.0 | 2.0 | ✅ Unchanged |
| **Jakarta Security** | 3.0 | 4.0 | 🔄 Major Update |
| **Jakarta Concurrency** | 3.0 | 3.1 | 🔄 Updated |
| **Jakarta Batch** | 2.1 | 2.1 | ✅ Unchanged |
| **Jakarta WebSocket** | 2.1 | 2.2 | 🔄 Updated |

### Platform Requirements

| Component | EE 10 | EE 11 | Change |
|-----------|-------|-------|--------|
| **Java SE** | 11, 17, 21 | 17, 21 | ⚠️ Java 11 dropped |
| **Jakarta EE API** | 10.0.0 | 11.0.0 | 🔄 Update required |
| **MicroProfile** | 6.0 | 7.0 | 🔄 Update recommended |

---

## 🔍 Detailed Changes by Specification

### 1. Jakarta Servlet 6.1 (from 6.0)

#### New Features
- **Enhanced Cookie API:** Better cookie management with SameSite attribute support
- **Improved Error Handling:** More granular error page configuration
- **Performance Improvements:** Better async processing

#### Code Impact
```java
// NEW in Servlet 6.1: SameSite cookie attribute
Cookie cookie = new Cookie("sessionId", "abc123");
cookie.setAttribute("SameSite", "Strict"); // NEW
response.addCookie(cookie);

// Enhanced error page configuration in web.xml
<error-page>
    <error-code>404</error-code>
    <location>/error-404.html</location>
    <exception-type>java.lang.Exception</exception-type> <!-- NEW: More specific -->
</error-page>
```

#### Course Impact
- ✅ **Minimal:** Existing code works
- 🔄 **Enhancement:** Update security section to cover SameSite cookies
- 📝 **Lab Update:** Add SameSite cookie examples in security lab

---

### 2. Jakarta Server Pages (JSP) 4.0 (from 3.1)

#### New Features
- **Expression Language 6.0:** Enhanced EL with new operators
- **Better Integration:** Improved CDI integration
- **Performance:** Faster page compilation

#### Code Impact
```jsp
<!-- NEW in EL 6.0: Null-safe navigation operator -->
${client?.address?.city} <!-- Returns null if any part is null -->

<!-- NEW: Stream operations in EL -->
${clients.stream().filter(c -> c.premium).toList()}

<!-- NEW: Enhanced lambda expressions -->
${clients.stream().map(c -> c.name.toUpperCase()).toList()}
```

#### Course Impact
- ✅ **Minimal:** Existing JSP code works
- 🔄 **Enhancement:** Update JSP lecture with EL 6.0 features
- 📝 **Lab Update:** Add modern EL examples

---

### 3. Jakarta RESTful Web Services (JAX-RS) 4.0 (from 3.1)

#### Major Changes
- **Multipart Support:** Native multipart/form-data handling (no more external libraries!)
- **Server-Sent Events (SSE):** Enhanced SSE support
- **JSON-P Integration:** Better JSON processing
- **Reactive Streams:** Improved reactive programming support

#### Code Impact
```java
// NEW in JAX-RS 4.0: Native multipart support
@POST
@Path("/upload")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public Response uploadFile(
    @FormParam("file") EntityPart filePart,
    @FormParam("description") String description) {
    
    InputStream inputStream = filePart.getContent();
    String fileName = filePart.getFileName().orElse("unknown");
    // Process file...
    return Response.ok().build();
}

// NEW: Enhanced SSE
@GET
@Path("/events")
@Produces(MediaType.SERVER_SENT_EVENTS)
public void streamEvents(@Context SseEventSink eventSink,
                         @Context Sse sse) {
    // Send events with retry and reconnect
    OutboundSseEvent event = sse.newEventBuilder()
        .name("message")
        .data("Hello")
        .reconnectDelay(3000)
        .build();
    eventSink.send(event);
}

// NEW: Reactive streams support
@GET
@Path("/reactive")
@Produces(MediaType.APPLICATION_JSON)
public Multi<Client> streamClients() {
    return Multi.createFrom().items(clientService.findAll().stream());
}
```

#### Course Impact
- 🔄 **Moderate:** Significant new features to cover
- 📝 **Lecture Update:** Add multipart and SSE sections to JAX-RS lecture
- 📝 **Lab Update:** Add file upload example, SSE notifications
- 🆕 **New Content:** Reactive streams introduction

---

### 4. Jakarta Security 4.0 (from 3.0)

#### Major Changes
- **OpenID Connect Support:** Native OIDC authentication
- **Enhanced Identity Stores:** Better integration with external identity providers
- **Improved Authorization:** Fine-grained authorization controls
- **Security Annotations:** New annotations for declarative security

#### Code Impact
```java
// NEW in Security 4.0: OpenID Connect configuration
@OpenIdAuthenticationMechanismDefinition(
    providerURI = "${oidc.provider.uri}",
    clientId = "${oidc.client.id}",
    clientSecret = "${oidc.client.secret}",
    redirectURI = "${oidc.redirect.uri}"
)
public class SecurityConfig {
}

// NEW: Enhanced identity store with external providers
@ApplicationScoped
public class ExternalIdentityStore implements IdentityStore {
    @Override
    public CredentialValidationResult validate(Credential credential) {
        // Integrate with external identity provider (LDAP, OAuth2, etc.)
    }
}

// NEW: Fine-grained authorization
@RolesAllowed("ADMIN")
@PermissionsAllowed("account:delete") // NEW: Permission-based
public Response deleteAccount(@PathParam("id") Long id) {
    // ...
}
```

#### Course Impact
- 🔄 **Significant:** Major security enhancements
- 📝 **Lecture Update:** Add OIDC section to security lecture
- 📝 **Lab Update:** Add OIDC authentication example
- 🆕 **New Content:** External identity provider integration

---

### 5. Jakarta Persistence (JPA) 3.2 (from 3.1)

#### New Features
- **Enhanced Query API:** Better type safety
- **Improved Criteria API:** More intuitive query building
- **UUID Support:** Native UUID primary key support
- **JSON Support:** Better JSON column handling

#### Code Impact
```java
// NEW in JPA 3.2: Native UUID support
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // NEW
    private UUID id;
}

// NEW: Enhanced Criteria API with type-safe metamodel
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Client> query = cb.createQuery(Client.class);
Root<Client> root = query.from(Client.class);

// NEW: More intuitive query building
query.select(root)
     .where(cb.and(
         cb.equal(root.get(Client_.premium), true),
         cb.greaterThan(root.get(Client_.balance), 1000)
     ))
     .orderBy(cb.desc(root.get(Client_.balance)));

// NEW: JSON column support
@Entity
public class Account {
    @Column(columnDefinition = "jsonb") // PostgreSQL JSON
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> metadata;
}
```

#### Course Impact
- 🔄 **Moderate:** Useful new features
- 📝 **Lecture Update:** Add UUID and JSON support to JPA lecture
- 📝 **Lab Update:** Use UUID for new entities, add JSON column example

---

### 6. Jakarta CDI 4.1 (from 4.0)

#### New Features
- **Build-time CDI:** Better performance with build-time bean discovery
- **Enhanced Interceptors:** More flexible interceptor binding
- **Improved Events:** Better event handling with priorities

#### Code Impact
```java
// NEW in CDI 4.1: Priority-based event observers
@ApplicationScoped
public class EventHandler {
    
    public void handleHighPriority(@Observes @Priority(1) TransactionEvent event) {
        // Executed first
    }
    
    public void handleLowPriority(@Observes @Priority(100) TransactionEvent event) {
        // Executed later
    }
}

// NEW: Enhanced interceptor binding
@InterceptorBinding
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface Audited {
    AuditLevel value() default AuditLevel.BASIC;
    
    enum AuditLevel {
        BASIC, DETAILED, FULL
    }
}
```

#### Course Impact
- ✅ **Minimal:** Mostly internal improvements
- 🔄 **Enhancement:** Update CDI lecture with priority-based events
- 📝 **Lab Update:** Add event priority examples

---

### 7. Jakarta Bean Validation 3.1 (from 3.0)

#### New Features
- **Enhanced Constraints:** New built-in constraints
- **Better Integration:** Improved CDI integration
- **Custom Validators:** Easier custom validator creation

#### Code Impact
```java
// NEW in Bean Validation 3.1: Additional constraints
public class Client {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;
    
    @Email(regexp = ".*@company\\.com") // NEW: Pattern support
    private String email;
    
    @Positive // NEW: More semantic than @Min(1)
    private BigDecimal balance;
    
    @UUID // NEW: UUID validation
    private String clientId;
}
```

#### Course Impact
- ✅ **Minimal:** Backward compatible
- 🔄 **Enhancement:** Update validation examples with new constraints
- 📝 **Lab Update:** Use new semantic constraints

---

### 8. Jakarta WebSocket 2.2 (from 2.1)

#### New Features
- **Better Error Handling:** Enhanced error management
- **Improved Performance:** Better message handling
- **CDI Integration:** Full CDI support in WebSocket endpoints

#### Code Impact
```java
// NEW in WebSocket 2.2: Full CDI support
@ServerEndpoint("/chat")
@ApplicationScoped // NEW: CDI scope
public class ChatEndpoint {
    
    @Inject // NEW: CDI injection works
    private ChatService chatService;
    
    @OnMessage
    public void onMessage(String message, Session session) {
        chatService.broadcast(message);
    }
}
```

#### Course Impact
- 🔄 **Moderate:** If WebSocket is added to course
- 📝 **New Content:** WebSocket with CDI integration
- 📝 **Lab Update:** Real-time notifications with WebSocket

---

### 9. Jakarta Concurrency 3.1 (from 3.0)

#### New Features
- **Virtual Threads Support:** Java 21 virtual threads integration
- **Enhanced Executors:** Better async execution
- **Improved Context Propagation:** Better context handling

#### Code Impact
```java
// NEW in Concurrency 3.1: Virtual threads support
@Resource
private ManagedExecutorService executor;

public CompletableFuture<Result> processAsync() {
    return executor.supplyAsync(() -> {
        // Runs on virtual thread if Java 21+
        return heavyComputation();
    });
}

// NEW: Enhanced context propagation
@Asynchronous
@Transactional
public void processWithContext() {
    // Transaction context automatically propagated
}
```

#### Course Impact
- 🔄 **Moderate:** If Concurrency is added to course
- 📝 **New Content:** Virtual threads introduction
- 📝 **Lab Update:** Async processing with virtual threads

---

## 📦 Migration Checklist

### 1. Dependencies Update

#### Maven pom.xml Changes
```xml
<!-- BEFORE (Jakarta EE 10) -->
<properties>
    <jakartaee.version>10.0.0</jakartaee.version>
    <microprofile.version>6.0</microprofile.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>

<!-- AFTER (Jakarta EE 11) -->
<properties>
    <jakartaee.version>11.0.0</jakartaee.version>
    <microprofile.version>7.0</microprofile.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <!-- Or use Java 21 for virtual threads -->
    <!-- <maven.compiler.source>21</maven.compiler.source> -->
    <!-- <maven.compiler.target>21</maven.compiler.target> -->
</properties>

<dependencies>
    <dependency>
        <groupId>jakarta.platform</groupId>
        <artifactId>jakarta.jakartaee-api</artifactId>
        <version>11.0.0</version> <!-- UPDATED -->
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### 2. Server Configuration Update

#### Open Liberty server.xml
```xml
<!-- BEFORE (Jakarta EE 10) -->
<featureManager>
    <feature>jakartaee-10.0</feature>
    <feature>microProfile-6.0</feature>
</featureManager>

<!-- AFTER (Jakarta EE 11) -->
<featureManager>
    <feature>jakartaee-11.0</feature>
    <feature>microProfile-7.0</feature>
</featureManager>
```

### 3. Code Updates Required

| Area | Change Required | Effort | Priority |
|------|----------------|--------|----------|
| **Dependencies** | Update versions | Low | 🔴 High |
| **Server Config** | Update features | Low | 🔴 High |
| **Servlet** | Add SameSite cookies | Low | 🟡 Medium |
| **JSP/EL** | Use new EL 6.0 features | Low | 🟢 Low |
| **JAX-RS** | Add multipart/SSE examples | Medium | 🟡 Medium |
| **Security** | Add OIDC support | High | 🔴 High |
| **JPA** | Use UUID, JSON columns | Low | 🟡 Medium |
| **CDI** | Update event priorities | Low | 🟢 Low |
| **Validation** | Use new constraints | Low | 🟢 Low |
| **WebSocket** | Add if not present | Medium | 🟢 Low |
| **Concurrency** | Add if not present | Medium | 🟢 Low |

---

## 🎓 Course Content Updates Required

### Lectures to Update

#### 1. Lecture 1: Introduction (Minor)
- ✅ Update timeline: Add Jakarta EE 11 (2024)
- ✅ Update version numbers in examples
- ✅ Mention Java 21 support

#### 2. Lecture 2: Servlets and JSP (Moderate)
- 🔄 Add SameSite cookie security section
- 🔄 Update JSP with EL 6.0 features
- 🔄 Add null-safe navigation examples
- 🔄 Add stream operations in EL

#### 3. Lecture 3: JPA (Moderate)
- 🔄 Add UUID primary key section
- 🔄 Add JSON column support
- 🔄 Update Criteria API examples
- 🔄 Show enhanced type safety

#### 4. Lecture 4: CDI (Minor)
- 🔄 Add event priority examples
- 🔄 Update interceptor binding examples
- ✅ Mention build-time CDI improvements

#### 5. Lecture 5: JAX-RS (Significant)
- 🔄 Add multipart/form-data section (major)
- 🔄 Add Server-Sent Events section
- 🔄 Add file upload examples
- 🔄 Add reactive streams introduction
- 🔄 Update all examples to JAX-RS 4.0

#### 6. Lecture 9: Security (Significant - if added)
- 🔄 Add OpenID Connect section (major)
- 🔄 Add external identity provider integration
- 🔄 Add permission-based authorization
- 🔄 Update authentication examples

#### 7. Optional: New Lecture on WebSocket (if added)
- 🆕 WebSocket 2.2 with CDI integration
- 🆕 Real-time communication patterns
- 🆕 Server-Sent Events vs WebSocket

#### 8. Optional: New Lecture on Concurrency (if added)
- 🆕 Virtual threads introduction
- 🆕 Async processing patterns
- 🆕 Context propagation

### Labs to Update

#### All Labs
- ✅ Update `pom.xml` to Jakarta EE 11.0.0
- ✅ Update `server.xml` to jakartaee-11.0
- ✅ Test with Open Liberty 24.0.0.12+ (EE 11 support)

#### Lab 1: First Servlet
- 🔄 Add SameSite cookie example
- ✅ Update dependencies

#### Lab 2: Servlets and JSP
- 🔄 Update JSP with EL 6.0 features
- 🔄 Add null-safe navigation examples
- ✅ Update dependencies

#### Lab 3: JPA
- 🔄 Change ID from Long to UUID
- 🔄 Add JSON column example (metadata)
- ✅ Update dependencies

#### Lab 4: CDI
- 🔄 Add event priority examples
- ✅ Update dependencies

#### Lab 5: REST
- 🔄 Add file upload endpoint (multipart)
- 🔄 Add SSE endpoint for notifications
- 🔄 Update all REST examples
- ✅ Update dependencies

#### Lab 6-7: DDD and Hexagonal
- 🔄 Use UUID for entity IDs
- 🔄 Add JSON metadata support
- ✅ Update dependencies

#### Lab 8: Microservices
- 🔄 Add SSE for real-time updates
- 🔄 Use multipart for file uploads
- ✅ Update dependencies

#### Lab 9: Security (if added)
- 🔄 Add OIDC authentication example
- 🔄 Add external identity provider
- 🔄 Add permission-based authorization

---

## 📊 Migration Effort Estimation

### Phase 1: Core Updates (Week 1)
**Effort:** 20-30 hours

- [ ] Update all `pom.xml` files (2h)
- [ ] Update all `server.xml` files (1h)
- [ ] Test all labs compile (4h)
- [ ] Update Lecture 1 (2h)
- [ ] Update course documentation (3h)
- [ ] Create migration guide (3h)
- [ ] Test basic functionality (5h)

### Phase 2: Feature Enhancements (Week 2)
**Effort:** 30-40 hours

- [ ] Update Lecture 2 with EL 6.0 (4h)
- [ ] Update Lecture 3 with UUID/JSON (6h)
- [ ] Update Lecture 5 with multipart/SSE (10h)
- [ ] Update Lab 2 with new JSP features (4h)
- [ ] Update Lab 3 with UUID/JSON (6h)
- [ ] Update Lab 5 with multipart/SSE (10h)

### Phase 3: Security Updates (Week 3)
**Effort:** 20-30 hours

- [ ] Update Lecture 9 with OIDC (8h)
- [ ] Create OIDC examples (6h)
- [ ] Update Lab 9 with OIDC (8h)
- [ ] Test security features (8h)

### Phase 4: Optional Enhancements (Week 4+)
**Effort:** 20-30 hours (if WebSocket/Concurrency added)

- [ ] Add WebSocket lecture (8h)
- [ ] Add WebSocket lab (6h)
- [ ] Add Concurrency examples (6h)
- [ ] Test optional features (10h)

**Total Estimated Effort:** 90-130 hours (2-3 weeks full-time)

---

## ✅ Benefits of Upgrading to Jakarta EE 11

### 1. Modern Features
- ✅ Native multipart support (no more external libraries)
- ✅ OpenID Connect authentication (modern security)
- ✅ Enhanced EL with null-safe navigation
- ✅ UUID primary keys (better for distributed systems)
- ✅ Server-Sent Events (real-time updates)

### 2. Better Performance
- ✅ Virtual threads support (Java 21)
- ✅ Build-time CDI optimizations
- ✅ Improved async processing
- ✅ Better resource management

### 3. Industry Alignment
- ✅ Latest Jakarta EE version
- ✅ Modern security standards (OIDC)
- ✅ Cloud-native features
- ✅ Microservices-ready

### 4. Student Benefits
- ✅ Learn latest technologies
- ✅ Industry-relevant skills
- ✅ Modern development practices
- ✅ Better job market alignment

---

## ⚠️ Risks and Considerations

### 1. Application Server Support
- **Open Liberty:** 24.0.0.12+ supports Jakarta EE 11
- **WildFly:** 32+ supports Jakarta EE 11
- **Payara:** 6.2024.1+ supports Jakarta EE 11
- ⚠️ Verify server version before migration

### 2. Third-Party Libraries
- ⚠️ Some libraries may not support EE 11 yet
- ✅ Most major libraries already updated
- 📝 Check compatibility before upgrading

### 3. Breaking Changes
- ✅ Minimal breaking changes
- ⚠️ Java 11 no longer supported (use Java 17+)
- ⚠️ Some deprecated APIs removed

### 4. Learning Curve
- ✅ Most changes are additions, not replacements
- ✅ Existing knowledge still valid
- 🔄 New features require learning time

---

## 🎯 Recommendation

### Should We Upgrade?

**YES - Recommended** ✅

**Reasons:**
1. **Future-Proof:** Jakarta EE 11 is the current version
2. **Modern Features:** Significant improvements in JAX-RS and Security
3. **Low Risk:** Backward compatible, minimal breaking changes
4. **Industry Alignment:** Students learn latest technologies
5. **Moderate Effort:** 2-3 weeks for complete migration

### Migration Strategy

**Option A: Immediate Migration (Recommended)**
- Migrate all content to EE 11 before course delivery
- Students learn latest version from start
- No need for future migration
- **Timeline:** 2-3 weeks

**Option B: Gradual Migration**
- Start with EE 10, mention EE 11 differences
- Migrate during course updates
- Add EE 11 features incrementally
- **Timeline:** 3-6 months

**Option C: Parallel Versions**
- Maintain both EE 10 and EE 11 versions
- Let students choose
- More maintenance overhead
- **Timeline:** Ongoing

### Recommended Approach: Option A

**Rationale:**
- Clean migration, no technical debt
- Students learn latest version
- Simpler maintenance
- Better long-term value

---

## 📝 Migration Action Plan

### Immediate Actions (This Week)
1. [ ] Verify Open Liberty 24.0.0.12+ availability
2. [ ] Test Jakarta EE 11 compatibility
3. [ ] Review all third-party dependencies
4. [ ] Create migration branch in Git
5. [ ] Update one lab as proof-of-concept

### Short-term Actions (Next 2 Weeks)
1. [ ] Update all dependencies (Phase 1)
2. [ ] Update core lectures (Phases 1-2)
3. [ ] Update all labs (Phase 2)
4. [ ] Test all examples
5. [ ] Update documentation

### Medium-term Actions (Weeks 3-4)
1. [ ] Add new features (multipart, SSE, OIDC)
2. [ ] Update security content (Phase 3)
3. [ ] Create migration guide for students
4. [ ] Review and refine all content
5. [ ] Final testing

### Optional Actions (Month 2+)
1. [ ] Add WebSocket content (if desired)
2. [ ] Add Concurrency content (if desired)
3. [ ] Add virtual threads examples (Java 21)
4. [ ] Create advanced topics module

---

## 📞 Questions for Decision

1. **Timeline:** When should we start migration?
   - Before next course delivery?
   - During course updates?
   - Gradual over time?

2. **Java Version:** Should we use Java 17 or Java 21?
   - Java 17: Stable, widely adopted
   - Java 21: Latest LTS, virtual threads

3. **Optional Features:** Which to include?
   - WebSocket (real-time communication)?
   - Concurrency (async processing)?
   - Virtual threads (Java 21)?

4. **Migration Approach:** Which option?
   - Option A: Immediate migration (recommended)
   - Option B: Gradual migration
   - Option C: Parallel versions

5. **Priority:** What to focus on first?
   - Core updates (dependencies, basic features)
   - New features (multipart, SSE, OIDC)
   - Optional content (WebSocket, Concurrency)

---

## 📚 Additional Resources

### Jakarta EE 11 Documentation
- [Jakarta EE 11 Platform Specification](https://jakarta.ee/specifications/platform/11/)
- [Jakarta EE 11 API Documentation](https://jakarta.ee/specifications/platform/11/apidocs/)
- [Migration Guide](https://jakarta.ee/specifications/platform/11/jakarta-platform-spec-11.html#migration)

### Specification Updates
- [JAX-RS 4.0 Specification](https://jakarta.ee/specifications/restful-ws/4.0/)
- [Jakarta Security 4.0 Specification](https://jakarta.ee/specifications/security/4.0/)
- [Jakarta Servlet 6.1 Specification](https://jakarta.ee/specifications/servlet/6.1/)
- [Jakarta Persistence 3.2 Specification](https://jakarta.ee/specifications/persistence/3.2/)

### Server Support
- [Open Liberty Jakarta EE 11 Support](https://openliberty.io/docs/latest/jakarta-ee11.html)
- [WildFly 32 Release Notes](https://www.wildfly.org/news/2024/01/18/WildFly32-Released/)
- [Payara Platform 6 Documentation](https://docs.payara.fish/community/docs/6.2024.1/overview.html)

---

**Analysis Completed:** January 8, 2026  
**Prepared By:** IBM Bob  
**Status:** Ready for Review and Decision  
**Recommended Action:** Proceed with Option A (Immediate Migration)