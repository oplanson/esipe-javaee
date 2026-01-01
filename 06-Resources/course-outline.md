# Complete Course Outline
## Jakarta EE and Microservices with DDD and Hexagonal Architecture

**Total Duration:** 48 hours (24h lectures + 24h labs)  
**Format:** 8 sessions × 6 hours (3h lecture + 3h lab)  
**Language:** English  
**Level:** Intermediate to Advanced

---

## 📅 Session 1: Jakarta EE Foundations (6 hours) ✅ COMPLETE

### Lecture 1.1: Introduction to Jakarta EE (2 hours) ✅
**Topics:**
- Jakarta EE ecosystem and evolution
- Core specifications overview
- Application server architecture
- Development environment setup
- First servlet application

**Learning Outcomes:**
- Understand Jakarta EE platform
- Set up development environment
- Create and deploy basic servlet

**Materials:**
- Slides: `02-Lectures/01-intro-jakartaee-microprofile.md` ✅
- Code examples: Basic servlet demos ✅

---

### Lab 1: First Servlet Application (2 hours) ✅
**Objectives:**
- Create Maven-based Jakarta EE project
- Implement servlets with annotations
- Handle HTTP GET/POST requests
- Deploy to WildFly

**Deliverables:**
- Working client management servlet
- HTML forms and styling
- Deployed WAR file

**Materials:**
- Lab guide: `03-Labs/Lab01-FirstServlet/README.md`
- Starter code: `03-Labs/Lab01-FirstServlet/starter/`
- Solution: `03-Labs/Lab01-FirstServlet/solution/`

---

### Lecture 1.2: Servlets and JSP Deep Dive (1 hour) ✅
**Topics:**
- Servlet lifecycle in detail
- Request/response handling
- Session management
- Introduction to JSP

**Learning Outcomes:**
- Master servlet lifecycle
- Implement session tracking
- Understand JSP basics

---

### Lab 1 Continuation (1 hour) ✅
**Activities:**
- Add session management
- Implement client search
- Enhance error handling

---

## 📅 Session 2: Web Technologies (6 hours) ✅ COMPLETE

### Lecture 2.1: JSP and JSTL (2 hours) ✅
**Topics:**
- JSP syntax and directives
- Expression Language (EL)
- JSTL core tags
- Custom tag libraries
- MVC pattern with JSP

**Learning Outcomes:**
- Create dynamic JSP pages
- Use JSTL for logic
- Implement MVC pattern

**Materials:**
- Slides: `02-Lectures/02-servlets-jsp-microprofile.md` ✅
- Code examples: JSP demos ✅

---

### Lab 2: JSP Client Management (2 hours) ✅
**Objectives:**
- Convert servlets to JSP
- Implement JSTL tags
- Create reusable components
- Apply MVC pattern

**Deliverables:**
- JSP-based client views
- JSTL tag usage
- Proper MVC structure

**Materials:**
- Lab guide: `03-Labs/Lab02-ServletsJSP/README.md` ✅

---

### Lecture 2.2: Advanced Web Concepts (1 hour) ✅
**Topics:**
- Filters and listeners
- Security basics
- File upload/download
- AJAX integration

---

### Lab 2 Continuation (1 hour) ✅
**Activities:**
- Add authentication filter
- Implement file upload
- Create AJAX search

---

## 📅 Session 3: Java Persistence API (6 hours) ✅ COMPLETE

### Lecture 3.1: JPA Fundamentals (2 hours) ✅
**Topics:**
- ORM concepts
- Entity mapping and annotations
- EntityManager and persistence context
- JPQL basics
- Transaction management

**Learning Outcomes:**
- Map entities to database
- Perform CRUD operations
- Write JPQL queries
- Manage transactions

**Materials:**
- Slides: `02-Lectures/03-jpa-database-integration.md` ✅
- Database schema: Banking app ERD ✅
- PostgreSQL setup with Docker Compose ✅

---

### Lab 3: JPA and Database Integration (2 hours) ✅
**Objectives:**
- Create JPA entities (Client, Account) with annotations
- Configure persistence.xml and datasource
- Implement service layer with manual EntityManager management
- Write JPQL queries and use Criteria API
- Set up Flyway database migrations
- Apply manual transaction management with EntityTransaction

**Deliverables:**
- JPA entity classes with relationships
- Service layer with Singleton pattern
- Manual transaction management (no CDI)
- Database-backed application with PostgreSQL
- Flyway migration scripts
- ServletContextListener for database initialization

**Materials:**
- Lab guide: `03-Labs/Lab03-JPA/README.md` ✅
- Flyway migrations: V1 (clients), V2 (accounts), V3 (account status) ✅
- Testing scripts: run-lab.sh, podman-test.sh, docker-test.sh ✅

---

### Lecture 3.2: Advanced JPA (1 hour) ✅
**Topics:**
- Entity relationships
- Cascade operations
- Lazy vs eager loading
- Criteria API
- Named queries

---

### Lab 3 Continuation (1 hour) ✅
**Activities:**
- Implement bidirectional relationships
- Add Criteria API dynamic search
- Test transaction rollback scenarios
- Optimize queries to avoid N+1 problem
- Add aggregate queries (total balance, account counts)

---

## 📅 Session 4: Dependency Injection with CDI (6 hours) ✅ COMPLETE

### Lecture 4.1: CDI Fundamentals (2 hours) ✅
**Topics:**
- Dependency injection principles and benefits
- CDI beans and scopes (@ApplicationScoped, @RequestScoped, @SessionScoped)
- Injection points with @Inject
- Qualifiers and alternatives
- Producer methods and disposers
- Declarative transaction management with @Transactional

**Learning Outcomes:**
- Understand DI benefits and principles
- Use CDI annotations effectively
- Implement loose coupling
- Apply declarative transactions
- Create producer methods

**Materials:**
- Slides: `02-Lectures/04-cdi-dependency-injection.md` ✅
- Code examples: CDI demos with interceptors ✅

---

### Lab 4: CDI and Dependency Injection (2 hours) ✅
**Objectives:**
- Convert Lab 3 code from Singleton pattern to CDI
- Use @Inject for dependency injection
- Apply @ApplicationScoped for service beans
- Implement @Transactional for declarative transactions
- Change persistence.xml from RESOURCE_LOCAL to JTA
- Create EntityManager producer
- Add logging interceptor

**Deliverables:**
- CDI-managed service layer
- Declarative transaction management
- Producer methods for EntityManager and Logger
- Logging interceptor implementation
- JTA-configured persistence unit
- Cleaner, more testable code

**Materials:**
- Lab guide: `03-Labs/Lab04-CDI/README.md` ✅
- Solution: `03-Labs/Lab04-CDI/solution/` ✅
- Testing scripts: run-lab.sh, podman-test.sh, docker-test.sh ✅

---

### Lecture 4.2: Advanced CDI (1 hour) ✅
**Topics:**
- Interceptors and decorators
- Interceptor bindings (@InterceptorBinding)
- CDI events and observers
- Transaction attributes (REQUIRED, REQUIRES_NEW, SUPPORTS)
- CDI best practices
- Testing CDI beans

---

### Lab 4 Continuation (1 hour) ✅
**Activities:**
- Test declarative transactions
- Verify interceptor logging
- Compare code with Lab 3 (before/after CDI)
- Experiment with different bean scopes
- Test transaction rollback scenarios

---

## 📅 Session 5: RESTful Web Services (6 hours) ✅ COMPLETE

### Lecture 5.1: JAX-RS Fundamentals (2 hours) ✅
**Topics:**
- REST principles and Richardson Maturity Model
- JAX-RS annotations (@Path, @GET, @POST, @PUT, @DELETE)
- HTTP methods mapping and status codes
- Path parameters and query strings
- JSON-B for JSON processing
- Handling circular references with @JsonbTransient
- Exception handling and custom mappers
- Bean Validation integration

**Learning Outcomes:**
- Design RESTful APIs following REST principles
- Implement REST endpoints with JAX-RS
- Handle JSON data with JSON-B
- Manage errors with exception mappers
- Apply Bean Validation to REST resources

**Materials:**
- Slides: `02-Lectures/05-jaxrs-restful-services.md` ✅ (1770+ lines)
- Code examples: Complete REST resources ✅

---

### Lab 5: REST API Development (2 hours) ✅
**Objectives:**
- Create REST resources for Client and Account
- Implement complete CRUD endpoints
- Handle JSON requests/responses with JSON-B
- Implement custom exception mappers
- Add Bean Validation
- Configure MicroProfile Rest Client
- Implement transfer endpoint for money transfers
- Test with curl and automated scripts

**Deliverables:**
- Client REST API (CRUD + search)
- Account REST API (CRUD + deposit/withdraw/transfer)
- Exception handling with 4 custom mappers
- Bean Validation integration
- MicroProfile Rest Client configuration
- 11 automated tests (Podman/Docker support)
- Complete API documentation

**Materials:**
- Lab guide: `03-Labs/Lab05-REST/README.md` ✅ (1658 lines)
- Solution: `03-Labs/Lab05-REST/solution/` ✅
- Testing scripts: podman-test.sh, docker-test.sh ✅

---

### Lecture 5.2: Advanced REST (1 hour) ✅
**Topics:**
- MicroProfile Rest Client
- Type-safe REST client interfaces
- CDI integration with REST clients
- Exception handling in REST clients
- Content negotiation
- API documentation with MicroProfile OpenAPI

---

### Lab 5 Continuation (1 hour) ✅
**Activities:**
- Test all REST endpoints with curl
- Verify exception handling
- Test Bean Validation
- Use MicroProfile Rest Client
- Run automated test suite (11 tests)
- Deploy with Podman/Docker

---

## 📅 Session 6: Domain-Driven Design (6 hours)

### Lecture 6.1: DDD Strategic Patterns (2 hours)
**Topics:**
- DDD philosophy and benefits
- Ubiquitous language
- Bounded contexts
- Context mapping
- Domain events

**Learning Outcomes:**
- Understand DDD principles
- Identify bounded contexts
- Design domain model
- Use domain events

**Materials:**
- Slides: `02-Lectures/06-ddd-strategic.md`
- Banking domain analysis

---

### Lab 6: DDD Refactoring (Part 1) (2 hours)
**Objectives:**
- Identify aggregates
- Define value objects
- Implement domain events
- Refactor to DDD structure

**Deliverables:**
- Aggregate roots
- Value objects
- Domain events
- Refactored codebase

**Materials:**
- Lab guide: `03-Labs/Lab06-DDD/README.md`

---

### Lecture 6.2: DDD Tactical Patterns (1 hour)
**Topics:**
- Entities vs value objects
- Aggregates and aggregate roots
- Repositories
- Domain services
- Factories

---

### Lab 6 Continuation (1 hour)
**Activities:**
- Implement domain services
- Create factories
- Add business rules validation

---

## 📅 Session 7: Hexagonal Architecture (6 hours)

### Lecture 7.1: Hexagonal Architecture (2 hours)
**Topics:**
- Ports and adapters pattern
- Dependency inversion principle
- Clean architecture
- Separation of concerns
- Testing strategies

**Learning Outcomes:**
- Understand hexagonal architecture
- Implement ports and adapters
- Apply dependency inversion
- Write testable code

**Materials:**
- Slides: `02-Lectures/07-hexagonal-architecture.md`

---

### Lab 7: Hexagonal Refactoring (2 hours)
**Objectives:**
- Define domain ports
- Implement adapters
- Restructure application layers
- Write unit tests

**Deliverables:**
- Port interfaces
- Adapter implementations
- Layered architecture
- Unit test suite

**Materials:**
- Lab guide: `03-Labs/Lab07-Hexagonal/README.md`

---

### Lecture 7.2: Testing and Quality (1 hour)
**Topics:**
- Unit testing with JUnit 5
- Integration testing
- Mocking with Mockito
- Test-driven development
- Code quality tools

---

### Lab 7 Continuation (1 hour)
**Activities:**
- Write comprehensive tests
- Add integration tests
- Configure code coverage

---

## 📅 Session 8: Microservices Architecture (6 hours)

### Lecture 8.1: Microservices Fundamentals (2 hours)
**Topics:**
- Microservices principles
- Service decomposition strategies
- Inter-service communication
- API Gateway pattern
- Service discovery
- Configuration management

**Learning Outcomes:**
- Understand microservices architecture
- Decompose monolith to services
- Implement service communication
- Configure distributed system

**Materials:**
- Slides: `02-Lectures/08-microservices.md`

---

### Lab 8: Microservices Implementation (2 hours)
**Objectives:**
- Split application into services
- Implement REST communication
- Configure service discovery
- Deploy multiple services

**Deliverables:**
- Client microservice
- Account microservice
- API Gateway
- Docker compose setup

**Materials:**
- Lab guide: `03-Labs/Lab08-Microservices/README.md`

---

### Lecture 8.2: Advanced Microservices (1 hour)
**Topics:**
- Circuit breaker pattern
- Distributed tracing
- Centralized logging
- Monitoring and observability
- Deployment strategies

---

### Lab 8 Continuation & Final Project (1 hour)
**Activities:**
- Add circuit breaker
- Implement health checks
- Configure monitoring
- Final project presentation

---

## 📊 Assessment Strategy

### Continuous Assessment (40%)
- **Lab Completion:** 20%
  - All 8 labs completed
  - Code quality and best practices
  
- **Weekly Quizzes:** 10%
  - Short quizzes after each session
  - Theoretical understanding
  
- **Participation:** 10%
  - Class discussions
  - Code reviews
  - Peer collaboration

### Final Project (60%)
- **Banking Application:** 40%
  - Complete implementation
  - All features working
  - Proper architecture
  - Clean code
  
- **Documentation:** 10%
  - API documentation
  - Architecture diagrams
  - Deployment guide
  
- **Presentation:** 10%
  - Demo of application
  - Explain architectural decisions
  - Q&A session

---

## 📚 Required Reading

### Before Course Starts
- Jakarta EE Tutorial (Chapters 1-3)
- "Clean Code" by Robert C. Martin (Chapters 1-2)

### During Course
- Jakarta EE specifications (relevant sections)
- "Domain-Driven Design" by Eric Evans (selected chapters)
- "Building Microservices" by Sam Newman (Chapters 1-4)

### Recommended Books
- "Implementing Domain-Driven Design" by Vaughn Vernon
- "Clean Architecture" by Robert C. Martin
- "Microservices Patterns" by Chris Richardson

---

## 🎯 Learning Outcomes

By the end of this course, students will be able to:

### Technical Skills
1. ✅ Develop enterprise applications using Jakarta EE
2. ✅ Design and implement RESTful APIs
3. ✅ Apply Domain-Driven Design principles
4. ✅ Implement hexagonal architecture
5. ✅ Build microservices-based systems
6. ✅ Use modern development tools and practices

### Soft Skills
1. ✅ Analyze business requirements
2. ✅ Make architectural decisions
3. ✅ Collaborate in team environment
4. ✅ Document technical solutions
5. ✅ Present technical concepts

---

## 🔧 Tools and Technologies

### Core Technologies
- Jakarta EE 10
- Java 17
- Maven 3.8+
- WildFly 27+
- PostgreSQL 14+

### Development Tools
- IntelliJ IDEA / Eclipse / VS Code
- Git
- Postman
- Docker
- DBeaver

### Testing Tools
- JUnit 5
- Mockito
- Arquillian
- REST Assured

---

## 📞 Support and Resources

### During Course
- **Office Hours:** [Schedule]
- **Discussion Forum:** [Link]
- **Email:** [Instructor email]
- **Slack Channel:** [Link]

### Online Resources
- Course repository: [GitHub link]
- Video recordings: [Platform link]
- Additional materials: [Drive link]

---

## 🎓 Certificate Requirements

To receive course certificate:
- [ ] Attend at least 80% of sessions
- [ ] Complete all 8 labs
- [ ] Pass all quizzes (minimum 60%)
- [ ] Submit final project
- [ ] Present final project
- [ ] Achieve overall grade of 60% or higher

---

**Course prepared by:** [Instructor Name]  
**Institution:** [Institution Name]  
**Version:** 1.0  
**Last Updated:** January 2026