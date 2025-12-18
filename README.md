# Jakarta EE and Microservices Course
## Complete Course Package with DDD and Hexagonal Architecture

**Course Duration:** 48 hours (24h lectures + 24h labs)  
**Language:** English  
**Target Audience:** Computer Science Students  
**Final Project:** Banking Application with Microservices Architecture

---

## 📚 Course Overview

This comprehensive course covers Jakarta EE fundamentals through advanced microservices architecture, emphasizing Domain-Driven Design (DDD) and hexagonal architecture principles. Students will build a complete banking application progressively throughout the course.

### Learning Objectives
- Master Jakarta EE core technologies (Servlets, JPA, CDI, JAX-RS)
- Understand and implement microservices architecture
- Apply Domain-Driven Design principles
- Implement hexagonal architecture patterns
- Build production-ready enterprise applications

---

## 📁 Repository Structure

```
esipe-javaee/
├── 01-Inputs/              # Course requirements and specifications
├── 02-Lectures/            # Lecture slides (Markdown → PowerPoint)
├── 03-Labs/                # Lab exercises with solutions
├── 04-BankingApp/          # Banking application source code
│   ├── src/                # Application source code (Git branches)
│   └── docs/               # Application documentation
├── 05-Deployment/          # Deployment guides and configurations
├── 06-Resources/           # Additional resources and references
└── README.md               # This file
```

---

## 🎓 Course Outline

### **Part 1: Jakarta EE Fundamentals (12h lectures + 12h labs)**

#### Week 1: Web Foundations
1. **Introduction to Jakarta EE** (2h lecture + 2h lab)
   - Jakarta EE ecosystem and architecture
   - Development environment setup
   - First servlet application

2. **Servlets and JSP** (3h lecture + 3h lab)
   - HTTP protocol and servlet lifecycle
   - Request/response handling
   - JSP basics and JSTL
   - Banking app: Client listing page

3. **Java Persistence API (JPA)** (4h lecture + 4h lab)
   - ORM concepts and entity mapping
   - JPQL and Criteria API
   - Transactions and entity lifecycle
   - Banking app: Client and Account entities

4. **Contexts and Dependency Injection (CDI)** (3h lecture + 3h lab)
   - Dependency injection principles
   - CDI scopes and qualifiers
   - Interceptors and decorators
   - Banking app: Service layer implementation

### **Part 2: Advanced Jakarta EE & Microservices (12h lectures + 12h labs)**

#### Week 2: RESTful Services and Architecture
5. **JAX-RS and RESTful APIs** (3h lecture + 3h lab)
   - REST principles and HTTP methods
   - JAX-RS annotations and providers
   - JSON processing with JSON-B
   - Banking app: REST API for clients/accounts

6. **Domain-Driven Design (DDD)** (3h lecture + 3h lab)
   - DDD strategic patterns
   - Bounded contexts and aggregates
   - Value objects and entities
   - Banking app: DDD refactoring

7. **Hexagonal Architecture** (2h lecture + 2h lab)
   - Ports and adapters pattern
   - Dependency inversion
   - Clean architecture principles
   - Banking app: Hexagonal restructuring

8. **Microservices Architecture** (4h lecture + 4h lab)
   - Microservices patterns and principles
   - Service decomposition strategies
   - Inter-service communication
   - Banking app: Microservices split (Client/Account services)

---

## 🛠️ Prerequisites

### Software Requirements
- **JDK:** OpenJDK 17 or later
- **Application Server:** WildFly 27+ or Payara 6+
- **Database:** PostgreSQL 14+ or MySQL 8+
- **Build Tool:** Maven 3.8+
- **IDE:** IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Git:** Version control
- **Docker:** For containerization (optional but recommended)

### Knowledge Prerequisites
- Java SE fundamentals
- Basic SQL and database concepts
- HTTP protocol basics
- Object-oriented programming principles

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd esipe-javaee
```

### 2. Set Up Development Environment
Follow the detailed setup guide in `05-Deployment/00-environment-setup.md`

### 3. Convert Markdown Slides to PowerPoint

#### Option A: Using Marp CLI (Recommended)
```bash
# Install Marp CLI
npm install -g @marp-team/marp-cli

# Convert a single lecture
marp 02-Lectures/01-intro-jakartaee.md -o slides/01-intro-jakartaee.pptx

# Convert all lectures
marp 02-Lectures/*.md -o slides/
```

#### Option B: Using Slidev
```bash
# Install Slidev
npm install -g @slidev/cli

# Start presentation server
cd 02-Lectures
slidev 01-intro-jakartaee.md

# Export to PowerPoint
slidev export 01-intro-jakartaee.md --format pptx
```

#### Option C: Using Pandoc
```bash
# Install Pandoc
# macOS: brew install pandoc
# Linux: apt-get install pandoc
# Windows: Download from pandoc.org

# Convert to PowerPoint
pandoc 02-Lectures/01-intro-jakartaee.md -o slides/01-intro-jakartaee.pptx
```

### 4. Run Lab Exercises
Each lab includes:
- **Instructions:** Step-by-step guide
- **Starter Code:** Initial project structure
- **Solution:** Complete working solution
- **Tests:** Unit tests to validate implementation

```bash
cd 03-Labs/Lab01-FirstServlet
mvn clean install
mvn wildfly:deploy
```

### 5. Build Banking Application
The banking application evolves through Git branches:

```bash
cd 04-BankingApp/src
git checkout 01-basic-servlets    # Week 1
git checkout 02-jpa-entities       # Week 1
git checkout 03-cdi-services       # Week 1
git checkout 04-rest-api           # Week 2
git checkout 05-ddd-refactor       # Week 2
git checkout 06-hexagonal-arch     # Week 2
git checkout 07-microservices      # Week 2
```

---

## 📖 Lecture Slides Format

All lecture slides are written in Markdown with YAML frontmatter for easy conversion to PowerPoint:

```markdown
---
marp: true
theme: default
paginate: true
backgroundColor: #fff
---

# Lecture Title
## Subtitle

---

## Learning Objectives
- Objective 1
- Objective 2

---

## Key Concept
Content with code examples, diagrams, and visuals
```

### Slide Features
- **Mermaid Diagrams:** Architecture and flow diagrams
- **Code Snippets:** Syntax-highlighted Java code
- **Visual Aids:** ASCII art, tables, and lists
- **Progressive Disclosure:** One concept per slide
- **Consistent Styling:** Professional theme throughout

---

## 🧪 Lab Exercises Structure

Each lab follows this structure:

```
03-Labs/LabXX-TopicName/
├── README.md              # Lab instructions
├── starter/               # Initial code for students
│   ├── pom.xml
│   └── src/
├── solution/              # Complete solution
│   ├── pom.xml
│   └── src/
└── tests/                 # Validation tests
```

### Lab Workflow
1. Read `README.md` for objectives and instructions
2. Start with code in `starter/` directory
3. Implement required functionality
4. Run tests to validate solution
5. Compare with `solution/` if needed

---

## 🏦 Banking Application Specifications

### Domain Model
- **Client:** id, name, email, phone, address
- **Account:** id, accountNumber, balance, type (CHECKING/SAVINGS), clientId
- **Transaction:** id, amount, type (DEPOSIT/WITHDRAWAL/TRANSFER), timestamp

### Core Features
1. **Client Management:** CRUD operations
2. **Account Management:** Create, view, close accounts
3. **Balance Operations:** Deposit, withdrawal, balance inquiry
4. **Transfers:** Between accounts with validation
5. **Transaction History:** View account transactions

### Technical Architecture
- **Presentation Layer:** REST API (JAX-RS)
- **Application Layer:** Service classes (CDI)
- **Domain Layer:** Entities and business logic (DDD)
- **Infrastructure Layer:** JPA repositories
- **Database:** PostgreSQL with proper schema

---

## 📚 Additional Resources

### Official Documentation
- [Jakarta EE Specification](https://jakarta.ee/specifications/)
- [Jakarta EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/)
- [WildFly Documentation](https://docs.wildfly.org/)

### Recommended Reading
- "Domain-Driven Design" by Eric Evans
- "Implementing Domain-Driven Design" by Vaughn Vernon
- "Building Microservices" by Sam Newman
- "Clean Architecture" by Robert C. Martin

### Online Resources
- Jakarta EE GitHub: https://github.com/eclipse-ee4j
- Microservices Patterns: https://microservices.io/patterns/
- DDD Community: https://www.domainlanguage.com/

---

## 🤝 Contributing

This course material is designed for educational purposes. Suggestions and improvements are welcome:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

---

## 📝 License

This course material is provided for educational use. Please respect intellectual property rights when using or sharing these materials.

---

## 👨‍🏫 Instructor Notes

### Teaching Tips
- **Hands-on First:** Start each session with a practical demo
- **Incremental Complexity:** Build on previous concepts
- **Real-world Context:** Relate concepts to banking domain
- **Code Reviews:** Review student solutions in class
- **Pair Programming:** Encourage collaboration during labs

### Time Management
- **Lectures:** 45-minute blocks with 5-minute breaks
- **Labs:** 2-hour blocks with instructor support
- **Code Reviews:** 30 minutes at end of each lab session
- **Q&A:** Reserve 15 minutes for questions

### Assessment Suggestions
- **Weekly Quizzes:** Test theoretical understanding
- **Lab Completion:** Practical skill validation
- **Final Project:** Complete banking application
- **Code Quality:** Clean code and best practices
- **Presentation:** Explain architectural decisions

---

## 📞 Support

For questions or issues:
- Review lecture slides and lab instructions
- Check the `06-Resources/FAQ.md` file
- Consult official Jakarta EE documentation
- Contact course instructor

---

**Last Updated:** December 2025  
**Version:** 1.0  
**Course Code:** JAKARTAEE-MS-2025