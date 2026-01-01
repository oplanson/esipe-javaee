<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 02: Servlets, JSP & MicroProfile

## 🎯 Objectives

In this lab, you will:
- Implement the complete servlet lifecycle
- Create dynamic web pages using JSP and JSTL
- Apply the MVC pattern to build a web application
- Use MicroProfile Config for web tier configuration
- Add health checks for monitoring web components
- Build a client management interface for the banking application

**Duration:** 3 hours  
**Difficulty:** Intermediate  
**Prerequisites:** Lab 01 completed

---

## 📋 What You'll Build

A complete web interface for managing bank clients with:
- **Client List Page:** Display all clients with pagination
- **Client Details Page:** View client information and accounts
- **Client Form:** Create and edit clients
- **Account List:** Display accounts for a client
- **Health Monitoring:** Check application and database health
- **Configuration:** Externalized settings with MP Config

---

## 🏗️ Architecture

```
Lab02-ServletsJSP/
├── starter/                    # Your starting point
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/bank/
│   │   │   │       ├── model/
│   │   │   │       │   ├── Client.java          # TODO: Complete
│   │   │   │       │   └── Account.java         # TODO: Complete
│   │   │   │       ├── service/
│   │   │   │       │   └── ClientService.java   # TODO: Implement
│   │   │   │       ├── web/
│   │   │   │       │   └── ClientController.java # TODO: Implement
│   │   │   │       └── health/
│   │   │   │           └── DatabaseHealthCheck.java # TODO: Implement
│   │   │   ├── resources/
│   │   │   │   └── META-INF/
│   │   │   │       └── microprofile-config.properties
│   │   │   ├── liberty/
│   │   │   │   └── config/
│   │   │   │       └── server.xml
│   │   │   └── webapp/
│   │   │       ├── WEB-INF/
│   │   │       │   ├── web.xml
│   │   │       │   └── views/
│   │   │       │       ├── client-list.jsp      # TODO: Complete
│   │   │       │       ├── client-form.jsp      # TODO: Complete
│   │   │       │       └── client-details.jsp   # TODO: Complete
│   │   │       ├── css/
│   │   │       │   └── style.css
│   │   │       └── index.html
│   │   └── test/
│   └── Containerfile
├── solution/                   # Complete working solution
└── README.md                   # This file
```

---

## 📝 Lab Instructions

### Part 1: Model Layer (30 minutes)

#### Step 1.1: Complete the Client Entity

Open `src/main/java/com/bank/model/Client.java` and complete the class:

```java
package com.bank.model;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private Long id;
    private String name;
    private String email;
    private List<Account> accounts;
    
    // TODO: Add constructors
    // TODO: Add getters and setters
    // TODO: Add toString() method
}
```

**Requirements:**
- Default constructor
- Constructor with name and email
- All getters and setters
- Initialize accounts list in constructors

#### Step 1.2: Complete the Account Entity

Open `src/main/java/com/bank/model/Account.java`:

```java
package com.bank.model;

public class Account {
    private Long id;
    private String number;
    private double balance;
    private String type; // CHECKING, SAVINGS
    private Long clientId;
    
    // TODO: Add constructors
    // TODO: Add getters and setters
    // TODO: Add toString() method
}
```

**Requirements:**
- Default constructor
- Constructor with all fields
- All getters and setters

---

### Part 2: Service Layer (45 minutes)

#### Step 2.1: Implement ClientService

Open `src/main/java/com/bank/service/ClientService.java`:

```java
package com.bank.service;

import com.bank.model.Client;
import com.bank.model.Account;
import java.util.*;

public class ClientService {
    
    // In-memory storage (will be replaced with database in Lab 3)
    private Map<Long, Client> clients = new HashMap<>();
    private Long nextId = 1L;
    
    // TODO: Implement findAll() - return all clients
    
    // TODO: Implement findById(Long id) - return client by ID
    
    // TODO: Implement create(Client client) - add new client
    
    // TODO: Implement update(Client client) - update existing client
    
    // TODO: Implement delete(Long id) - remove client
    
    // TODO: Implement findByName(String name) - search by name
}
```

**Hints:**
- Use `clients.values()` to get all clients
- Generate IDs using `nextId++`
- Return `null` if client not found
- For search, use `contains()` for partial matching

**Test your service:**
```java
ClientService service = new ClientService();

// Create clients
Client client1 = new Client("John Doe", "john@example.com");
service.create(client1);

// Find all
List<Client> all = service.findAll();
System.out.println("Total clients: " + all.size());

// Find by ID
Client found = service.findById(1L);
System.out.println("Found: " + found.getName());
```

---

### Part 3: Controller Layer (60 minutes)

#### Step 3.1: Implement ClientController Servlet

Open `src/main/java/com/bank/web/ClientController.java`:

```java
@WebServlet(
    name = "ClientController",
    urlPatterns = {"/clients", "/client"},
    loadOnStartup = 1
)
public class ClientController extends HttpServlet {
    
    private ClientService clientService;
    
    @Inject
    @ConfigProperty(name = "web.pagination.default.size", defaultValue = "10")
    private int defaultPageSize;
    
    @Override
    public void init() throws ServletException {
        // TODO: Initialize clientService
        // TODO: Add some sample data
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        // TODO: Implement routing logic
        // - If action is null or "list": show client list
        // - If action is "view": show client details
        // - If action is "new": show empty form
        // - If action is "edit": show form with client data
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        // TODO: Implement form handling
        // - If action is "create": create new client
        // - If action is "update": update existing client
        // - If action is "delete": delete client
        // - Use PRG pattern (Post-Redirect-Get)
    }
    
    // TODO: Add helper methods
    // - listClients(req, resp)
    // - viewClient(req, resp)
    // - showForm(req, resp)
    // - createClient(req, resp)
    // - updateClient(req, resp)
    // - deleteClient(req, resp)
}
```

**Implementation Guide:**

1. **List Clients:**
```java
private void listClients(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    List<Client> clients = clientService.findAll();
    req.setAttribute("clients", clients);
    req.getRequestDispatcher("/WEB-INF/views/client-list.jsp").forward(req, resp);
}
```

2. **View Client:**
```java
private void viewClient(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    Long id = Long.parseLong(req.getParameter("id"));
    Client client = clientService.findById(id);
    
    if (client == null) {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Client not found");
        return;
    }
    
    req.setAttribute("client", client);
    req.getRequestDispatcher("/WEB-INF/views/client-details.jsp").forward(req, resp);
}
```

3. **Create Client (POST):**
```java
private void createClient(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
    String name = req.getParameter("name");
    String email = req.getParameter("email");
    
    // Validate input
    if (name == null || name.trim().isEmpty()) {
        resp.sendRedirect(req.getContextPath() + "/client?action=new&error=name_required");
        return;
    }
    
    Client client = new Client(name, email);
    clientService.create(client);
    
    // PRG pattern
    resp.sendRedirect(req.getContextPath() + "/clients?message=created");
}
```

---

### Part 4: View Layer with JSP (60 minutes)

#### Step 4.1: Client List Page

Open `src/main/webapp/WEB-INF/views/client-list.jsp`:

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Banking App - Clients</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Client Management</h1>
        
        <!-- TODO: Add success message display -->
        <c:if test="${not empty param.message}">
            <div class="alert alert-success">
                <!-- Display appropriate message based on param.message -->
            </div>
        </c:if>
        
        <!-- TODO: Add search form -->
        
        <!-- TODO: Add client table -->
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Accounts</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <!-- TODO: Loop through clients using c:forEach -->
                <!-- TODO: Display client data -->
                <!-- TODO: Add action links (View, Edit, Delete) -->
            </tbody>
        </table>
        
        <!-- TODO: Add "New Client" button -->
        <a href="${pageContext.request.contextPath}/client?action=new" class="btn btn-primary">
            Add New Client
        </a>
    </div>
</body>
</html>
```

**Hints:**
- Use `<c:forEach var="client" items="${clients}">` to loop
- Use `<c:out value="${client.name}" />` to safely output data
- Use `${client.accounts.size()}` to get account count
- Build URLs with `${pageContext.request.contextPath}/client?action=view&id=${client.id}`

#### Step 4.2: Client Form Page

Open `src/main/webapp/WEB-INF/views/client-form.jsp`:

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Banking App - ${empty client ? 'New' : 'Edit'} Client</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>${empty client ? 'New' : 'Edit'} Client</h1>
        
        <!-- TODO: Add error message display -->
        
        <!-- TODO: Create form -->
        <form method="post" action="${pageContext.request.contextPath}/client">
            <!-- TODO: Add hidden field for action (create or update) -->
            <!-- TODO: Add hidden field for id (if editing) -->
            
            <!-- TODO: Add name input field -->
            <div class="form-group">
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" 
                       value="<c:out value='${client.name}' />" required>
            </div>
            
            <!-- TODO: Add email input field -->
            
            <!-- TODO: Add submit and cancel buttons -->
        </form>
    </div>
</body>
</html>
```

#### Step 4.3: Client Details Page

Open `src/main/webapp/WEB-INF/views/client-details.jsp`:

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Banking App - Client Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Client Details</h1>
        
        <!-- TODO: Display client information -->
        <div class="client-info">
            <p><strong>ID:</strong> ${client.id}</p>
            <!-- TODO: Add name and email -->
        </div>
        
        <!-- TODO: Display accounts table -->
        <h2>Accounts</h2>
        <c:choose>
            <c:when test="${empty client.accounts}">
                <p>No accounts found.</p>
            </c:when>
            <c:otherwise>
                <!-- TODO: Create accounts table -->
            </c:otherwise>
        </c:choose>
        
        <!-- TODO: Add action buttons (Edit, Delete, Back) -->
    </div>
</body>
</html>
```

---

### Part 5: MicroProfile Config (20 minutes)

#### Step 5.1: Configure Application Settings

Open `src/main/resources/META-INF/microprofile-config.properties`:

```properties
# Application Information
app.name=Banking Web Application
app.version=1.0.0
app.environment=development

# Web Configuration
web.pagination.default.size=10
web.pagination.max.size=100
web.session.timeout=1800

# Feature Flags
feature.client.registration.enabled=true
feature.client.deletion.enabled=true
feature.advanced.search.enabled=false

# TODO: Add your own configuration properties
```

#### Step 5.2: Use Config in Servlet

Update your `ClientController` to use configuration:

```java
@Inject
@ConfigProperty(name = "web.pagination.default.size", defaultValue = "10")
private int defaultPageSize;

@Inject
@ConfigProperty(name = "feature.client.deletion.enabled", defaultValue = "true")
private boolean deletionEnabled;

@Inject
@ConfigProperty(name = "app.name")
private String appName;

// Use in your methods
private void listClients(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    
    // Use configured page size
    int pageSize = defaultPageSize;
    
    List<Client> clients = clientService.findAll();
    req.setAttribute("clients", clients);
    req.setAttribute("appName", appName);
    req.setAttribute("deletionEnabled", deletionEnabled);
    
    req.getRequestDispatcher("/WEB-INF/views/client-list.jsp").forward(req, resp);
}
```

---

### Part 6: Health Checks (25 minutes)

#### Step 6.1: Implement Database Health Check

Open `src/main/java/com/bank/health/DatabaseHealthCheck.java`:

```java
package com.bank.health;

import org.eclipse.microprofile.health.*;
import jakarta.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {
    
    @Override
    public HealthCheckResponse call() {
        // TODO: Implement health check
        // For now, simulate database check
        boolean databaseAvailable = checkDatabase();
        
        if (databaseAvailable) {
            return HealthCheckResponse
                .named("database-connection")
                .up()
                .withData("status", "connected")
                .build();
        } else {
            return HealthCheckResponse
                .named("database-connection")
                .down()
                .withData("status", "disconnected")
                .build();
        }
    }
    
    private boolean checkDatabase() {
        // TODO: Implement actual database check
        // For now, return true
        return true;
    }
}
```

#### Step 6.2: Implement Web Application Readiness Check

Create `src/main/java/com/bank/health/WebAppReadinessCheck.java`:

```java
package com.bank.health;

import org.eclipse.microprofile.health.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Readiness
@ApplicationScoped
public class WebAppReadinessCheck implements HealthCheck {
    
    @Inject
    @ConfigProperty(name = "app.name")
    private String appName;
    
    @Override
    public HealthCheckResponse call() {
        // TODO: Check if application is ready
        boolean configLoaded = appName != null && !appName.isEmpty();
        
        if (configLoaded) {
            return HealthCheckResponse
                .named("web-application-readiness")
                .up()
                .withData("configuration", "loaded")
                .withData("app_name", appName)
                .build();
        } else {
            return HealthCheckResponse
                .named("web-application-readiness")
                .down()
                .withData("configuration", "not loaded")
                .build();
        }
    }
}
```

---

## 🧪 Testing Your Application

### Step 1: Build and Run

```bash
# Navigate to starter directory
cd starter

# Build the application
mvn clean package

# Run with Liberty dev mode
mvn liberty:dev
```

### Step 2: Test Endpoints

Open your browser and test:

1. **Home Page:** http://localhost:9080/
2. **Client List:** http://localhost:9080/clients
3. **New Client:** http://localhost:9080/client?action=new
4. **Health Check:** http://localhost:9080/health
5. **Liveness:** http://localhost:9080/health/live
6. **Readiness:** http://localhost:9080/health/ready

### Step 3: Test CRUD Operations

1. **Create a Client:**
   - Go to http://localhost:9080/client?action=new
   - Fill in name and email
   - Submit form
   - Verify redirect to client list

2. **View Client:**
   - Click "View" on a client
   - Verify details are displayed

3. **Edit Client:**
   - Click "Edit" on a client
   - Modify name or email
   - Submit form
   - Verify changes

4. **Delete Client:**
   - Click "Delete" on a client
   - Verify client is removed from list

### Step 4: Test Health Checks

```bash
# Check all health endpoints
curl http://localhost:9080/health

# Expected response:
{
  "status": "UP",
  "checks": [
    {
      "name": "database-connection",
      "status": "UP",
      "data": {
        "status": "connected"
      }
    },
    {
      "name": "web-application-readiness",
      "status": "UP",
      "data": {
        "configuration": "loaded",
        "app_name": "Banking Web Application"
      }
    }
  ]
}
```

---

## 🐳 Running with Podman

### Build Container Image

```bash
# Build the application first
mvn clean package

# Build container image
podman build -t banking-web-app:lab02 .

# Run container
podman run -d -p 9080:9080 -p 9443:9443 \
  --name banking-web-lab02 \
  banking-web-app:lab02

# View logs
podman logs -f banking-web-lab02

# Stop container
podman stop banking-web-lab02

# Remove container
podman rm banking-web-lab02
```
## 🚀 Quick Start Scripts

This lab includes automated scripts to make deployment and testing easier!

### Option 1: Podman + Open Liberty (Recommended - No Installation Required)

**Best for:** Students who want the easiest setup without installing Open Liberty locally.

```bash
# Navigate to lab directory
cd 03-Labs/Lab02-ServletsJSP

# Run with Podman (builds, deploys, tests automatically)
./podman-test.sh
```

**What it does:**
- ✅ Builds the application with Maven
- ✅ Creates a container image with Open Liberty
- ✅ Starts the container
- ✅ Runs automated tests
- ✅ Opens browser automatically
- ✅ No local server installation needed!

**Access URLs:**
- Home: http://localhost:9080/
- Clients: http://localhost:9080/clients
- Health: http://localhost:9080/health
- Metrics: http://localhost:9080/metrics

**Stop the container:**
```bash
podman stop banking-web-lab02
podman rm banking-web-lab02
```

---

### Option 2: Docker + Open Liberty

**Best for:** Students who prefer Docker over Podman.

```bash
# Run with Docker
./docker-test.sh
```

Same features as Podman option, but uses Docker instead.

---

### Option 3: Local Open Liberty with Dev Mode

**Best for:** Active development with hot reload.

```bash
# Run Liberty in dev mode
./run-lab.sh
```

**Features:**
- 🔄 Automatic reload on code changes
- 🚀 Hot deployment
- 🧪 Integrated testing
- 📝 Live updates without restart

**Access:** http://localhost:9080/

**Stop:** Press `Ctrl+C` in the terminal

---

### Option 4: Build and Test Only

**Best for:** Verifying your code compiles without running the server.

```bash
# Just build and verify
./test-lab.sh
```

**What it does:**
- ✅ Checks prerequisites (Java, Maven)
- ✅ Builds the application
- ✅ Verifies WAR file contents
- ✅ Lists deployment options
- ❌ Does NOT start a server

---

## 📊 Comparison of Deployment Options

| Feature | Podman | Docker | Liberty Dev | Test Only |
|---------|--------|--------|-------------|-----------|
| No installation needed | ✅ | ✅ | ❌ | ✅ |
| Auto reload on changes | ❌ | ❌ | ✅ | N/A |
| Automated testing | ✅ | ✅ | ❌ | ✅ |
| Isolated environment | ✅ | ✅ | ❌ | N/A |
| Opens browser | ✅ | ✅ | ❌ | ❌ |
| Best for | First run | Docker users | Development | Verification |

**Recommendation:** Start with `./podman-test.sh` for the easiest experience!


---

## ✅ Validation Checklist

Before considering the lab complete, verify:

- [ ] Client model has all required fields and methods
- [ ] Account model is complete
- [ ] ClientService implements all CRUD operations
- [ ] ClientController handles GET and POST requests
- [ ] Client list page displays all clients
- [ ] Client form works for create and edit
- [ ] Client details page shows client info and accounts
- [ ] MicroProfile Config properties are loaded
- [ ] Configuration is used in servlet
- [ ] Database health check is implemented
- [ ] Web app readiness check is implemented
- [ ] All health endpoints return correct status
- [ ] Application runs without errors
- [ ] CRUD operations work correctly
- [ ] PRG pattern is implemented for forms
- [ ] Input validation is present
- [ ] Error messages are displayed
- [ ] CSS styling is applied

---

## 🎓 Learning Outcomes

After completing this lab, you should understand:

1. **Servlet Lifecycle:**
   - init(), service(), destroy() methods
   - When each method is called
   - Resource initialization and cleanup

2. **JSP and JSTL:**
   - JSP syntax and directives
   - JSTL core tags (c:forEach, c:if, c:choose)
   - Expression Language (EL)
   - Safe output with c:out

3. **MVC Pattern:**
   - Separation of concerns
   - Model: data and business logic
   - View: presentation (JSP)
   - Controller: request handling (Servlet)

4. **MicroProfile Config:**
   - Externalizing configuration
   - Using @ConfigProperty
   - Default values
   - Environment-specific settings

5. **Health Checks:**
   - Liveness vs Readiness
   - Implementing HealthCheck interface
   - Providing health data
   - Monitoring application status

---

## 🚀 Going Further (Optional Challenges)

If you finish early, try these enhancements:

1. **Pagination:**
   - Implement page navigation
   - Use MP Config for page size
   - Add "Previous" and "Next" buttons

2. **Search Functionality:**
   - Add search form
   - Implement search in ClientService
   - Display search results

3. **Validation:**
   - Add email format validation
   - Check for duplicate emails
   - Display field-specific errors

4. **Account Management:**
   - Add account creation form
   - Display accounts on client details page
   - Implement account CRUD operations

5. **Sorting:**
   - Add column headers for sorting
   - Implement sort by name, email, or ID
   - Remember sort preference

6. **Custom Health Checks:**
   - Add memory usage check
   - Add disk space check
   - Add response time check

---

## 📚 Resources

- **Jakarta Servlet Specification:** https://jakarta.ee/specifications/servlet/
- **Jakarta Server Pages:** https://jakarta.ee/specifications/pages/
- **JSTL Documentation:** https://jakarta.ee/specifications/tags/
- **MicroProfile Config:** https://microprofile.io/specifications/microprofile-config/
- **MicroProfile Health:** https://microprofile.io/specifications/microprofile-health/
- **Open Liberty Guides:** https://openliberty.io/guides/

---

## 💡 Tips and Hints

### Common Issues and Solutions

1. **404 Error on JSP:**
   - Ensure JSP files are in `/WEB-INF/views/`
   - Check forward path in servlet
   - Verify file names match exactly

2. **JSTL Tags Not Working:**
   - Add taglib directive at top of JSP
   - Check URI is correct: `jakarta.tags.core`
   - Ensure JSTL dependency in pom.xml

3. **Config Properties Not Injected:**
   - Verify property name matches exactly
   - Check microprofile-config.properties location
   - Ensure CDI is enabled (beans.xml)

4. **Health Check Not Showing:**
   - Verify @Liveness or @Readiness annotation
   - Check class is @ApplicationScoped
   - Ensure mpHealth feature in server.xml

5. **Form Submission Not Working:**
   - Check form method is POST
   - Verify action URL is correct
   - Ensure parameter names match servlet code

### Debugging Tips

```java
// Add logging in servlet
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    System.out.println("=== GET Request ===");
    System.out.println("Action: " + req.getParameter("action"));
    System.out.println("ID: " + req.getParameter("id"));
    // ... rest of code
}

// Log in JSP
<% System.out.println("Clients count: " + ((List)request.getAttribute("clients")).size()); %>
```

---

## 🎯 Success Criteria

Your lab is successful when:

1. ✅ Application starts without errors
2. ✅ Client list page displays correctly
3. ✅ You can create new clients
4. ✅ You can view client details
5. ✅ You can edit existing clients
6. ✅ You can delete clients
7. ✅ Health checks return UP status
8. ✅ Configuration properties are loaded
9. ✅ All JSP pages render correctly
10. ✅ No console errors or warnings

---

## 📞 Getting Help

If you're stuck:

1. **Check the solution** in the `solution/` directory
2. **Review the lecture slides** for concepts
3. **Read error messages carefully** - they often tell you what's wrong
4. **Use browser developer tools** to inspect requests/responses
5. **Check Liberty logs** in `target/liberty/wlp/usr/servers/defaultServer/logs/`
6. **Ask your instructor** during lab time

---

**Good luck! 🚀**

Remember: The goal is to learn, not just to finish. Take your time to understand each concept.