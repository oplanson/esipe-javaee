# Lab 02 - Testing Guide
## Comprehensive Testing Instructions for Servlets, JSP & MicroProfile

---

## 🎯 Overview

This guide provides detailed instructions for testing your Lab 02 implementation using different deployment methods.

---

## 📋 Prerequisites

Before testing, ensure you have:

- ✅ **JDK 17+** installed
- ✅ **Maven 3.8+** installed
- ✅ **Podman** (recommended) OR **Docker** OR **Open Liberty** installed
- ✅ Completed Lab 01 successfully
- ✅ Basic understanding of servlets and JSP

### Verify Prerequisites

```bash
# Check Java version
java -version
# Should show version 17 or higher

# Check Maven version
mvn -version
# Should show version 3.8 or higher

# Check Podman (optional but recommended)
podman --version

# Check Docker (alternative)
docker --version
```

---

## 🚀 Quick Start (Recommended)

### Method 1: Podman + Open Liberty (Easiest)

**No local server installation required!**

```bash
# Navigate to lab directory
cd 03-Labs/Lab02-ServletsJSP

# Run automated test script
./podman-test.sh
```

**What happens:**
1. ✅ Builds your application with Maven
2. ✅ Creates a container image with Open Liberty
3. ✅ Starts the container on port 9080
4. ✅ Waits for Liberty to start
5. ✅ Runs automated endpoint tests
6. ✅ Opens browser to http://localhost:9080/

**Expected output:**
```
==========================================
Lab 02: Podman + Open Liberty Testing
Servlets, JSP & MicroProfile
==========================================

✓ Podman found
✓ Build successful
✓ Podman image built
✓ Container started
✓ Open Liberty started successfully

Testing endpoints...
----------------------------
Testing home page... ✓ PASS
Testing clients page... ✓ PASS
Testing new client form... ✓ PASS
Testing health endpoint... ✓ PASS
Testing liveness endpoint... ✓ PASS
Testing readiness endpoint... ✓ PASS
Testing metrics endpoint... ✓ PASS

Test Results: 7 passed, 0 failed
```

---

## 🧪 Testing Methods

### Method 2: Docker + Open Liberty

If you prefer Docker over Podman:

```bash
./docker-test.sh
```

Same features as Podman, but uses Docker.

---

### Method 3: Liberty Dev Mode (For Development)

Best for active development with hot reload:

```bash
./run-lab.sh
```

**Features:**
- 🔄 Automatic reload on code changes
- 🚀 Hot deployment
- 📝 Live updates without restart

**To stop:** Press `Ctrl+C`

---

### Method 4: Build and Verify Only

Just want to verify your code compiles?

```bash
./test-lab.sh
```

This builds the application and verifies the WAR file contents without starting a server.

---

## 🌐 Manual Testing

### Step 1: Access the Application

Open your browser to: **http://localhost:9080/**

You should see the Banking Application home page.

---

### Step 2: Test Client List

1. Navigate to: **http://localhost:9080/clients**
2. **Verify:**
   - ✅ Page displays "Client Management" header
   - ✅ Table shows list of clients
   - ✅ Each client has ID, Name, Email, and Accounts count
   - ✅ "Add New Client" button is visible
   - ✅ CSS styling is applied

---

### Step 3: Test Create Client

1. Click "Add New Client" button
2. **Verify form displays:**
   - ✅ Name input field
   - ✅ Email input field
   - ✅ Submit button
   - ✅ Cancel link

3. **Fill in the form:**
   - Name: "Test Client"
   - Email: "test@example.com"

4. Click "Submit"

5. **Verify:**
   - ✅ Redirected to client list
   - ✅ New client appears in the table
   - ✅ Success message displayed (if implemented)

---

### Step 4: Test View Client

1. Click "View" link for any client
2. **Verify:**
   - ✅ Client details page displays
   - ✅ Shows client ID, name, and email
   - ✅ Shows list of accounts (if any)
   - ✅ "Edit" and "Back" buttons visible

---

### Step 5: Test Edit Client

1. From client details, click "Edit"
2. **Verify:**
   - ✅ Form pre-filled with client data
   - ✅ Can modify name and email
3. Change the name
4. Click "Submit"
5. **Verify:**
   - ✅ Redirected to client list
   - ✅ Changes are reflected

---

### Step 6: Test Delete Client

1. From client list, click "Delete" for a client
2. **Verify:**
   - ✅ Client is removed from list
   - ✅ Confirmation message displayed (if implemented)

---

## 🏥 Health Check Testing

### Test All Health Endpoints

```bash
# Overall health
curl http://localhost:9080/health

# Liveness probe
curl http://localhost:9080/health/live

# Readiness probe
curl http://localhost:9080/health/ready
```

### Expected Health Response

```json
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

## 📊 Metrics Testing

### Access Metrics

```bash
# All metrics
curl http://localhost:9080/metrics

# Base metrics only
curl http://localhost:9080/metrics/base

# Application metrics
curl http://localhost:9080/metrics/application
```

### Expected Metrics

You should see metrics like:
- JVM memory usage
- Thread count
- HTTP request counts
- Response times

---

## 🔍 Validation Checklist

Use this checklist to verify your implementation:

### Model Layer
- [ ] Client class has all required fields
- [ ] Account class is complete
- [ ] Constructors work correctly
- [ ] Getters and setters function properly

### Service Layer
- [ ] ClientService implements findAll()
- [ ] ClientService implements findById()
- [ ] ClientService implements create()
- [ ] ClientService implements update()
- [ ] ClientService implements delete()
- [ ] Search functionality works (if implemented)

### Controller Layer
- [ ] ClientController handles GET requests
- [ ] ClientController handles POST requests
- [ ] Routing logic works correctly
- [ ] PRG pattern is implemented
- [ ] Error handling is present

### View Layer (JSP)
- [ ] client-list.jsp displays all clients
- [ ] client-form.jsp works for create and edit
- [ ] client-details.jsp shows client info
- [ ] JSTL tags are used correctly
- [ ] CSS styling is applied

### MicroProfile
- [ ] Config properties are loaded
- [ ] @ConfigProperty injection works
- [ ] Health checks return UP status
- [ ] Liveness check is implemented
- [ ] Readiness check is implemented
- [ ] Metrics endpoint is accessible

---

## 🐛 Troubleshooting

### Issue 1: Port Already in Use

**Error:** `Address already in use: bind`

**Solution:**
```bash
# Find process using port 9080
lsof -i :9080  # Mac/Linux
netstat -ano | findstr :9080  # Windows

# Stop the container
podman stop banking-web-lab02
podman rm banking-web-lab02

# Or kill the process
kill -9 <PID>
```

---

### Issue 2: Container Won't Start

**Error:** Container stops immediately

**Solution:**
```bash
# Check container logs
podman logs banking-web-lab02

# Common causes:
# - Port conflict
# - Configuration error in server.xml
# - Missing dependencies
```

---

### Issue 3: 404 Not Found

**Error:** Page not found

**Solution:**
- ✅ Check servlet URL pattern matches
- ✅ Verify JSP files are in `/WEB-INF/views/`
- ✅ Check forward path in servlet
- ✅ Ensure application is deployed

---

### Issue 4: JSTL Tags Not Working

**Error:** Tags display as plain text

**Solution:**
- ✅ Add taglib directive: `<%@ taglib prefix="c" uri="jakarta.tags.core" %>`
- ✅ Check JSTL dependency in pom.xml
- ✅ Verify URI is correct (jakarta.tags.core, not javax)

---

### Issue 5: Config Properties Not Injected

**Error:** @ConfigProperty returns null

**Solution:**
- ✅ Verify property name matches exactly
- ✅ Check microprofile-config.properties location
- ✅ Ensure CDI is enabled (beans.xml exists)
- ✅ Use @ApplicationScoped on the bean

---

### Issue 6: Health Check Not Showing

**Error:** /health returns 404

**Solution:**
- ✅ Verify @Liveness or @Readiness annotation
- ✅ Check class is @ApplicationScoped
- ✅ Ensure mpHealth feature in server.xml
- ✅ Rebuild and redeploy

---

## 📝 Test Scenarios

### Scenario 1: Happy Path

1. Start application
2. View client list
3. Add new client
4. View client details
5. Edit client
6. Delete client

**Expected:** All operations succeed without errors

---

### Scenario 2: Validation Testing

1. Try to create client with empty name
2. Try to create client with invalid email
3. Try to view non-existent client

**Expected:** Appropriate error messages displayed

---

### Scenario 3: Concurrent Operations

1. Open two browser windows
2. Add clients in both windows
3. Verify both clients appear in list

**Expected:** No data loss or conflicts

---

## 🎓 Learning Verification

After testing, you should be able to answer:

1. **How does the servlet lifecycle work?**
   - init() → service() → doGet()/doPost() → destroy()

2. **What is the PRG pattern and why use it?**
   - Post-Redirect-Get prevents duplicate form submissions

3. **How do JSP and servlets work together?**
   - Servlet processes logic, forwards to JSP for presentation

4. **What's the difference between liveness and readiness?**
   - Liveness: Is the app running?
   - Readiness: Is the app ready to serve requests?

5. **How does MicroProfile Config work?**
   - Externalizes configuration from code
   - Allows environment-specific settings

---

## 📊 Performance Testing

### Load Testing (Optional)

```bash
# Install Apache Bench (if not installed)
# Mac: brew install httpd
# Linux: sudo apt-get install apache2-utils

# Test client list endpoint
ab -n 1000 -c 10 http://localhost:9080/clients

# Test health endpoint
ab -n 1000 -c 10 http://localhost:9080/health
```

**Analyze:**
- Requests per second
- Average response time
- Failed requests (should be 0)

---

## 🔒 Security Testing (Optional)

### Test Input Validation

```bash
# Try SQL injection
curl -X POST http://localhost:9080/client \
  -d "action=create&name='; DROP TABLE clients;--&email=test@test.com"

# Try XSS
curl -X POST http://localhost:9080/client \
  -d "action=create&name=<script>alert('XSS')</script>&email=test@test.com"
```

**Expected:** Input should be sanitized/escaped

---

## 📚 Additional Resources

- **Jakarta Servlet Spec:** https://jakarta.ee/specifications/servlet/
- **JSP Specification:** https://jakarta.ee/specifications/pages/
- **JSTL Documentation:** https://jakarta.ee/specifications/tags/
- **MicroProfile Health:** https://microprofile.io/specifications/microprofile-health/
- **Open Liberty Guides:** https://openliberty.io/guides/

---

## ✅ Success Criteria

Your lab is successful when:

1. ✅ All automated tests pass
2. ✅ Application starts without errors
3. ✅ All CRUD operations work
4. ✅ JSP pages render correctly
5. ✅ Health checks return UP
6. ✅ Configuration is loaded
7. ✅ No console errors
8. ✅ CSS styling is applied
9. ✅ Navigation works smoothly
10. ✅ You understand the concepts!

---

**Good luck with your testing! 🚀**

Remember: Testing is not just about finding bugs, it's about understanding how your application works!

---

© Copyright Olivier Planson - 2025