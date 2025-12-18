# © Copyright Olivier Planson - 2025
# Lab 1: First Servlet Application
## Introduction to Jakarta EE Web Development

**Duration:** 2 hours
**Difficulty:** Beginner
**Prerequisites:** JDK 17+, Maven, and one of:
- **Podman** (recommended - no server installation needed)
- **Docker** (alternative to Podman)
- **Open Liberty** 23+ (for local development)

---

## 🎯 Learning Objectives

By completing this lab, you will:
- ✅ Create a Maven-based Jakarta EE web application
- ✅ Implement servlets using annotations
- ✅ Handle HTTP GET and POST requests
- ✅ Deploy applications using Podman/Docker with Open Liberty
- ✅ Use MicroProfile features (Health, Metrics, OpenAPI)
- ✅ Test web applications in a browser

---

## 📋 Lab Overview

You will build a simple **Client Management** web application that:
1. Displays a welcome page
2. Shows a list of clients (hardcoded for now)
3. Allows adding new clients via a form
4. Uses servlets to handle requests

This is the foundation for our banking application!

---

## 🏗️ Project Structure

```
Lab01-FirstServlet/
├── starter/                    # Your starting point
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/bank/
│           │       └── web/
│           │           └── .gitkeep
│           ├── liberty/
│           │   └── config/
│           │       └── server.xml
│           └── webapp/
│               ├── WEB-INF/
│               │   └── web.xml
│               ├── index.html
│               └── css/
│                   └── style.css
├── solution/                   # Complete solution
│   ├── pom.xml
│   ├── Containerfile          # For Podman/Docker deployment
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/bank/
│           │       ├── model/
│           │       │   └── Client.java
│           │       └── web/
│           │           ├── WelcomeServlet.java
│           │           └── ClientListServlet.java
│           ├── resources/
│           │   └── META-INF/
│           │       └── microprofile-config.properties
│           ├── liberty/
│           │   └── config/
│           │       └── server.xml
│           └── webapp/
│               ├── WEB-INF/
│               │   └── web.xml
│               ├── index.html
│               ├── add-client.html
│               └── css/
│                   └── style.css
├── podman-test.sh             # Test with Podman + Open Liberty
├── docker-test.sh             # Test with Docker + Open Liberty
├── run-lab.sh                 # Run with local Open Liberty
├── test-lab.sh                # Build and test only
├── TESTING-GUIDE.md           # Detailed testing instructions
└── README.md                  # This file
```

---

## 📝 Part 1: Project Setup (15 minutes)

### Step 1: Navigate to Starter Project

```bash
cd 03-Labs/Lab01-FirstServlet/starter
```

### Step 2: Examine pom.xml

The Maven configuration is already set up with:
- Jakarta EE 10 API dependency
- Maven WAR plugin
- Liberty Maven plugin (for local development)

### Step 3: Build the Project

```bash
mvn clean package
```

**Expected output:** `BUILD SUCCESS` and `target.war` created

### Step 4: Deploy and Run (Recommended: Podman)

**Option A: Podman + Open Liberty (Recommended - No Installation Required)**

```bash
# Navigate to lab directory
cd 03-Labs/Lab01-FirstServlet

# Run with Podman + Open Liberty
./podman-test.sh
```

This will:
- Build the application with Maven
- Create a container image with Open Liberty
- Deploy and test automatically
- Open browser to http://localhost:9080/

**Advantages:**
- ✅ No local server installation needed
- ✅ Includes MicroProfile features (Health, Metrics, OpenAPI)
- ✅ Isolated environment
- ✅ Easy cleanup

**Access URLs:**
- Home: http://localhost:9080/
- Welcome: http://localhost:9080/welcome
- Clients: http://localhost:9080/clients
- Health: http://localhost:9080/health
- Metrics: http://localhost:9080/metrics

**Option B: Local Open Liberty (For Development)**

If you have Open Liberty installed locally:

```bash
# Set Liberty home
export LIBERTY_HOME=/path/to/liberty

# Run Liberty Maven plugin
mvn liberty:dev
```

Access at: http://localhost:9080/

**Option C: Docker (Alternative to Podman)**

```bash
./docker-test.sh
```

---

## 💻 Part 2: Create Welcome Servlet (20 minutes)

### Task 2.1: Implement WelcomeServlet

Create `src/main/java/com/bank/web/WelcomeServlet.java`:

```java
package com.bank.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/welcome")
public class WelcomeServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
            throws ServletException, IOException {
        
        // TODO: Set response content type to "text/html"
        
        // TODO: Get PrintWriter from response
        
        // TODO: Write HTML output:
        // - HTML structure with head and body
        // - H1 with "Welcome to Banking Application"
        // - Paragraph with current date/time
        // - Link to /clients servlet
        
        // TODO: Close the writer
    }
}
```

### Task 2.2: Test WelcomeServlet

1. Build and deploy:
   ```bash
   # Using Podman
   ./podman-test.sh
   
   # Or using Liberty dev mode
   mvn liberty:dev
   ```

2. Open browser: `http://localhost:9080/welcome`

3. **Expected output:**
   - Page title: "Welcome to Banking Application"
   - Current date and time displayed
   - Link to view clients

---

## 👥 Part 3: Client List Servlet (30 minutes)

### Task 3.1: Create Client Class

Create `src/main/java/com/bank/model/Client.java`:

```java
package com.bank.model;

public class Client {
    private Long id;
    private String name;
    private String email;
    private String phone;
    
    // TODO: Add constructors
    // - Default constructor
    // - Constructor with all fields
    
    // TODO: Add getters and setters for all fields
    
    // TODO: Override toString() method
}
```

### Task 3.2: Implement ClientListServlet

Create `src/main/java/com/bank/web/ClientListServlet.java`:

```java
package com.bank.web;

import com.bank.model.Client;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/clients")
public class ClientListServlet extends HttpServlet {
    
    // Hardcoded client list (we'll use database later)
    private List<Client> clients = new ArrayList<>();
    
    @Override
    public void init() throws ServletException {
        // TODO: Initialize with sample clients
        // Add at least 3 clients with different data
    }
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // TODO: Write HTML output with:
        // - Page title "Client List"
        // - Link to CSS file: /css/style.css
        // - H1 header
        // - HTML table with columns: ID, Name, Email, Phone
        // - Loop through clients and display in table rows
        // - Link back to welcome page
        
        out.close();
    }
}
```

### Task 3.3: Style the Page

Edit `src/main/webapp/css/style.css`:

```css
/* TODO: Add CSS styles for:
 * - Body: font-family, margin, padding, background color
 * - H1: color, text-align
 * - Table: border, width, border-collapse
 * - Table headers: background color, color, padding
 * - Table cells: padding, border
 * - Links: color, text-decoration
 */
```

### Task 3.4: Test Client List

1. Redeploy:
   ```bash
   mvn clean package liberty:redeploy
   ```

2. Navigate to: `http://localhost:9080/clients`

3. **Verify:**
   - Table displays all clients
   - Styling is applied
   - Data is properly formatted

---

## ➕ Part 4: Add Client Form (30 minutes)

### Task 4.1: Create Add Client Form

Create `src/main/webapp/add-client.html`:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Add New Client</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <h1>Add New Client</h1>
    
    <!-- TODO: Create form with:
         - Action: /clients
         - Method: POST
         - Fields: name (text), email (email), phone (tel)
         - Submit button
         - Cancel link back to /clients
    -->
    
</body>
</html>
```

### Task 4.2: Handle POST Request

Update `ClientListServlet.java` to add `doPost` method:

```java
@Override
protected void doPost(HttpServletRequest request, 
                     HttpServletResponse response) 
        throws ServletException, IOException {
    
    // TODO: Get form parameters
    // - name
    // - email
    // - phone
    
    // TODO: Create new Client object
    // - Generate ID (use clients.size() + 1)
    // - Set name, email, phone from parameters
    
    // TODO: Add client to list
    
    // TODO: Redirect to /clients page
    // Use: response.sendRedirect("clients");
}
```

### Task 4.3: Add Link to Form

Update `ClientListServlet.doGet()` to include a link to add-client.html:

```java
out.println("<p><a href='add-client.html'>Add New Client</a></p>");
```

### Task 4.4: Test Add Client Feature

1. Redeploy application
2. Go to client list
3. Click "Add New Client"
4. Fill form and submit
5. **Verify:** New client appears in the list

---

## 🧪 Part 5: Testing and Validation (15 minutes)

### Manual Testing Checklist

- [ ] Welcome page loads correctly
- [ ] Client list displays all clients
- [ ] Table is properly styled
- [ ] Add client form is accessible
- [ ] Form validation works (try empty fields)
- [ ] New clients are added successfully
- [ ] Navigation links work
- [ ] CSS styling is applied consistently

### Test Different Scenarios

1. **Empty form submission:** What happens?
2. **Invalid email format:** Does it accept it?
3. **Special characters in name:** How is it handled?
4. **Multiple rapid submissions:** Any issues?

### Improvements to Consider

Think about:
- Input validation (client-side and server-side)
- Error messages for invalid data
- Confirmation messages after adding client
- Delete client functionality
- Edit client functionality

---

## 🎓 Part 6: Understanding the Code (10 minutes)

### Key Concepts Review

#### 1. Servlet Lifecycle
```
init() → service() → doGet()/doPost() → destroy()
```

#### 2. Annotations vs web.xml
```java
@WebServlet("/clients")  // Modern approach
```
vs
```xml
<servlet>
    <servlet-name>ClientListServlet</servlet-name>
    <servlet-class>com.bank.web.ClientListServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>ClientListServlet</servlet-name>
    <url-pattern>/clients</url-pattern>
</servlet-mapping>
```

#### 3. Request/Response Cycle
```
Browser → HTTP Request → Servlet → Process → HTTP Response → Browser
```

---

## 💡 Bonus Challenges (Optional)

### Challenge 1: Search Functionality
Add a search form to filter clients by name or email.

### Challenge 2: Client Details Page
Create a servlet that shows detailed information for a single client.
URL pattern: `/client?id=1`

### Challenge 3: Delete Client
Add a delete button for each client in the table.

### Challenge 4: Form Validation
Implement server-side validation:
- Name: required, min 2 characters
- Email: required, valid format
- Phone: required, valid format

### Challenge 5: Session Counter
Display how many clients the user has viewed in this session.

---

## 📚 What You Learned

### Technical Skills
- ✅ Maven project structure for Jakarta EE
- ✅ Servlet annotations and lifecycle
- ✅ HTTP GET and POST handling
- ✅ HTML form processing
- ✅ Response generation with PrintWriter
- ✅ Application deployment to Open Liberty

### Best Practices
- ✅ Separation of concerns (model, view, controller)
- ✅ RESTful URL patterns
- ✅ Proper HTTP method usage
- ✅ Resource organization

---

## 🔍 Common Issues and Solutions

### Issue 1: Port Already in Use
**Error:** `Address already in use: bind`
**Solution:** 
```bash
# Find process using port 9080
lsof -i :9080  # Mac/Linux
netstat -ano | findstr :9080  # Windows

# Kill the process or change Open Liberty port
```

### Issue 2: ClassNotFoundException
**Error:** `java.lang.ClassNotFoundException: com.bank.web.WelcomeServlet`
**Solution:** 
- Verify package names match
- Rebuild: `mvn clean package`
- Check WEB-INF/classes directory in WAR

### Issue 3: 404 Not Found
**Error:** Page not found
**Solution:**
- Check servlet URL pattern
- Verify context path: ``
- Check Open Liberty deployment status

### Issue 4: Changes Not Reflected
**Problem:** Code changes don't appear
**Solution:**
```bash
mvn clean package liberty:redeploy
```

---

## 📝 Submission Checklist

Before moving to the next lab, ensure:

- [ ] All servlets compile without errors
- [ ] Application deploys successfully
- [ ] Welcome page is accessible
- [ ] Client list displays correctly
- [ ] Add client form works
- [ ] CSS styling is applied
- [ ] Code is properly commented
- [ ] No console errors in browser

---

## 🚀 Next Steps

### Prepare for Lab 2: JSP and JSTL
- Review JSP syntax
- Understand MVC pattern
- Read about JSTL core tags

### Homework
1. Add edit client functionality
2. Implement client deletion
3. Add input validation
4. Improve CSS styling

### Questions to Consider
- How can we avoid hardcoding HTML in servlets?
- What happens to client data when server restarts?
- How can we make the code more maintainable?

---

## 📖 Additional Resources

### Documentation
- [Jakarta Servlet Specification](https://jakarta.ee/specifications/servlet/)
- [Open Liberty Getting Started](https://docs.liberty.org/27/Getting_Started_Guide.html)
- [Maven WAR Plugin](https://maven.apache.org/plugins/maven-war-plugin/)

### Tutorials
- [Jakarta EE Tutorial - Web Tier](https://eclipse-ee4j.github.io/jakartaee-tutorial/#the-web-tier)
- [Servlet Best Practices](https://www.oracle.com/java/technologies/servlet-best-practices.html)

---

## 🆘 Getting Help

### If You're Stuck:
1. Check the solution in `solution/` directory
2. Review lecture slides
3. Consult Jakarta EE documentation
4. Ask instructor or teaching assistant
5. Discuss with classmates

### Office Hours
- **When:** [Schedule]
- **Where:** [Location]
- **Contact:** [Email]

---

## ✅ Solution Preview

The complete solution is available in the `solution/` directory. 

**Don't peek until you've tried!** 

Key features in the solution:
- Full CRUD operations
- Input validation
- Error handling
- Professional styling
- Code comments and documentation

---

**Good luck with your first Jakarta EE application! 🚀**

Remember: The goal is to learn, not just to finish. Take your time to understand each concept.