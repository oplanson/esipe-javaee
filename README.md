<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

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
   - API versioning and breaking changes
   - Database migration strategies (Option 4)
   - Banking app: DDD refactoring with V1/V2 APIs

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
- **Application Server:** Open Liberty 24.0+ (automatically downloaded by Maven)
- **Container Runtime:** Podman or Docker (for containerized deployment)
- **Database:** PostgreSQL 14+ (for Labs 3-5)
- **Build Tool:** Maven 3.8+
- **IDE:** IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Git:** Version control

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

#### Automated Conversion (Recommended)
```bash
# Use the provided conversion script
cd 06-Resources/tools
./convert-slides.sh

# Slides will be generated in: 06-Resources/slides/
```

#### Manual Conversion with Marp CLI
```bash
# Install Marp CLI
npm install -g @marp-team/marp-cli

# Convert a single lecture
cd 02-Lectures
marp 01-intro-jakartaee-microprofile.md -o ../06-Resources/slides/01-intro-jakartaee-microprofile.pptx --theme esipe-theme.css

# Convert all lectures
marp *.md -o ../06-Resources/slides/ --theme esipe-theme.css
```

For detailed conversion instructions, see: `06-Resources/MERMAID-CONVERSION-GUIDE.md`

### 4. Run Lab Exercises
Each lab includes:
- **Instructions:** Step-by-step guide
- **Starter Code:** Initial project structure with TODO comments
- **Solution:** Complete working solution
- **Deployment Scripts:** Automated deployment with Podman/Docker

```bash
cd 03-Labs/Lab01-FirstServlet
# Option 1: Quick test with Podman (recommended)
./podman-test.sh

# Option 2: Development mode with Liberty
./run-lab.sh

# Option 3: Build verification only
./test-lab.sh

# Option 4: Docker alternative
./docker-test.sh
```

### 5. Verify All Labs
Test all labs at once with the verification script:

```bash
cd 06-Resources/tools
./verify-all-labs.sh

# This will test Labs 1-5 sequentially
# Compatible with macOS (Bash 3.2) and Linux
```

For individual lab testing, see each lab's README.md

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
├── TESTING-GUIDE.md       # Testing documentation (Labs 1-2)
├── SOLUTION-STATUS.md     # Implementation status (Labs 3-5)
├── starter/               # Initial code for students
│   ├── pom.xml
│   ├── Containerfile      # Container build file
│   └── src/
├── solution/              # Complete solution
│   ├── pom.xml
│   ├── Containerfile      # Container build file
│   ├── docker-compose.yml # Database setup (Labs 3-5)
│   └── src/
├── podman-test.sh         # Podman deployment script (recommended)
├── docker-test.sh         # Docker deployment script
├── run-lab.sh             # Development mode script
└── test-lab.sh            # Build verification script
```

### Lab Workflow
1. Read `README.md` for objectives and instructions
2. Start with code in `starter/` directory (or solution for reference)
3. Implement required functionality
4. Test with deployment scripts:
   - `./podman-test.sh` - Quick containerized test (recommended)
   - `./run-lab.sh` - Development mode with hot reload
   - `./test-lab.sh` - Build verification only
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
- [Open Liberty Documentation](https://openliberty.io/docs/)
- [MicroProfile Documentation](https://microprofile.io/)

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

**Last Updated:** January 2026
**Version:** 1.0
**Course Code:** JAKARTAEE-MS-2026