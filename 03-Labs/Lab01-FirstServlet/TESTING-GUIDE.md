# © Copyright Olivier Planson - 2025
# Lab 1 Testing Guide
## How to Build, Test, and Run the Banking Application

**Lab:** First Servlet Application
**Duration:** 15-30 minutes for testing
**Prerequisites:** Java 17+, Maven 3.8+

---

## 📋 Overview

This guide provides multiple ways to test and run Lab 1, from simple build verification to full deployment testing with Podman/Docker or local Open Liberty.

---

## 🎯 Quick Start (Choose One Method)

### Method 1: Podman Testing (Recommended - Uses Open Liberty)

```bash
cd esipe-javaee/03-Labs/Lab01-FirstServlet
./podman-test.sh
```

**What it does:**
- ✅ Builds the application with Maven
- ✅ Creates Podman image with Open Liberty
- ✅ Starts container on ports 9080/9443
- ✅ Deploys application automatically
- ✅ Tests all endpoints (including MicroProfile)
- ✅ Opens browser automatically

**Requirements:** Podman only (no local server needed)

**Access URLs:**
- Home: http://localhost:9080/
- Welcome: http://localhost:9080/welcome
- Clients: http://localhost:9080/clients
- Health: http://localhost:9080/health
- Metrics: http://localhost:9080/metrics

---

### Method 2: Docker Testing (Alternative to Podman)

```bash
cd esipe-javaee/03-Labs/Lab01-FirstServlet
./docker-test.sh
```

**What it does:**
- ✅ Builds the application
- ✅ Creates Docker image with Open Liberty
- ✅ Starts container
- ✅ Deploys application
- ✅ Tests all endpoints
- ✅ Opens browser automatically

**Requirements:** Docker only

---

### Method 3: Local Open Liberty Testing (Full Control)

```bash
cd esipe-javaee/03-Labs/Lab01-FirstServlet
./run-lab.sh
```

**What it does:**
- ✅ Checks Open Liberty installation
- ✅ Starts Open Liberty if needed
- ✅ Builds application
- ✅ Deploys to Open Liberty
- ✅ Tests endpoints
- ✅ Opens browser

**Requirements:** Open Liberty installed and LIBERTY_HOME set

---

### Method 4: Build and Test Only (No Deployment)

```bash
cd esipe-javaee/03-Labs/Lab01-FirstServlet
./test-lab.sh
```

**What it does:**
- ✅ Verifies Java and Maven
- ✅ Builds application
- ✅ Creates WAR file
- ✅ Lists WAR contents
- ✅ Optionally deploys if Open Liberty is running

**Requirements:** Java 17+, Maven 3.8+

---

## 🔧 Manual Testing Steps

### Step 1: Build the Application

```bash
cd esipe-javaee/03-Labs/Lab01-FirstServlet/solution

# Clean and build
mvn clean package

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: X.XXX s
```

**Verify WAR file created:**
```bash
ls -lh target.war
# Should show: banking-app.war (approximately 10-15 KB)
```

---

### Step 2: Inspect WAR Contents

```bash
# List all files in WAR
jar -tf target.war

# Expected structure:
# META-INF/
# META-INF/MANIFEST.MF
# WEB-INF/
# WEB-INF/classes/
# WEB-INF/classes/com/bank/model/Client.class
# WEB-INF/classes/com/bank/web/WelcomeServlet.class
# WEB-INF/classes/com/bank/web/ClientListServlet.class
# WEB-INF/classes/META-INF/microprofile-config.properties
# WEB-INF/web.xml
# index.html
# add-client.html
# css/style.css
```

**Note:** The `microprofile-config.properties` file contains MicroProfile configuration for Open Liberty deployment.

---

### Step 3: Deploy to Open Liberty

#### Option A: Using Maven Plugin

```bash
# Make sure Open Liberty is running
mvn liberty:deploy

# Or to redeploy after changes
mvn liberty:redeploy

# Or to undeploy
mvn liberty:undeploy
```

#### Option B: Manual Deployment

```bash
# Copy WAR to deployments folder
cp target.war $LIBERTY_HOME/standalone/deployments/

# Open Liberty will auto-deploy
# Check for: banking-app.war.deployed marker file
```

#### Option C: Admin Console

1. Open: http://localhost:9990
2. Login with admin credentials
3. Navigate to: Deployments
4. Click: Add
5. Upload: target.war
6. Enable deployment

---

### Step 4: Verify Deployment

```bash
# Check Open Liberty logs
tail -f $LIBERTY_HOME/standalone/log/server.log

# Look for:
# Deployed "banking-app.war"
```

**Check deployment marker:**
```bash
ls $LIBERTY_HOME/standalone/deployments.war.*

# Should see:
# banking-app.war.deployed
```

---

### Step 5: Test Endpoints

#### Using curl

```bash
# Test home page
curl http://localhost:9080/

# Test welcome servlet
curl http://localhost:9080/welcome

# Test clients servlet
curl http://localhost:9080/clients

# Test add client form
curl http://localhost:9080/add-client.html
```

#### Using Browser

Open these URLs:
- **Home:** http://localhost:9080/
- **Welcome:** http://localhost:9080/welcome
- **Clients:** http://localhost:9080/clients
- **Add Client:** http://localhost:9080/add-client.html

---

### Step 6: Test Functionality

#### Test 1: View Client List
1. Navigate to: http://localhost:9080/clients
2. **Expected:** Table with 5 sample clients
3. **Verify:** All client data displays correctly

#### Test 2: Add New Client
1. Click "Add New Client" button
2. Fill form:
   - Name: "Test User"
   - Email: "test@example.com"
   - Phone: "+1-555-9999"
3. Click "Save Client"
4. **Expected:** Redirect to client list with success message
5. **Verify:** New client appears in table

#### Test 3: Navigation
1. Test all navigation links
2. **Verify:** All pages load correctly
3. **Verify:** CSS styling is applied

#### Test 4: Form Validation
1. Try submitting empty form
2. **Expected:** Browser validation error
3. Try invalid email format
4. **Expected:** Validation error

---

## 🧪 Automated Testing

### Run All Tests

```bash
cd solution

# Run unit tests (if any)
mvn test

# Run integration tests
mvn verify

# Run with coverage
mvn clean test jacoco:report
```

---

## 📊 Expected Results

### Successful Build Output

```
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------------< com.bank:banking-app >-----------------
[INFO] Building Banking Application - Lab 01 1.0-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO] 
[INFO] --- maven-clean-plugin:3.1.0:clean (default-clean) @ banking-app ---
[INFO] --- maven-resources-plugin:3.0.2:resources (default-resources) @ banking-app ---
[INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ banking-app ---
[INFO] --- maven-war-plugin:3.4.0:war (default-war) @ banking-app ---
[INFO] Packaging webapp
[INFO] Building war: /path/to/target.war
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Successful Deployment Output

```
[INFO] --- liberty-maven-plugin:4.2.0.Final:deploy (default-cli) @ banking-app ---
[INFO] Deploying /path/to/target.war
[INFO] Successful deployment of banking-app.war
```

### Open Liberty Log Output

```
INFO  [org.jboss.as.server.deployment] (MSC service thread 1-2) WFLYSRV0027: Starting deployment of "banking-app.war" (runtime-name: "banking-app.war")
INFO  [org.liberty.extension.undertow] (ServerService Thread Pool -- 82) WFLYUT0021: Registered web context: '' for server 'default-server'
INFO  [org.jboss.as.server] (DeploymentScanner-threads - 1) WFLYSRV0010: Deployed "banking-app.war" (runtime-name : "banking-app.war")
```

---

## 🐛 Troubleshooting

### Issue 1: Build Fails

**Error:** `[ERROR] Failed to execute goal`

**Solutions:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U

# Check Java version
java -version  # Must be 17+
```

---

### Issue 2: Deployment Fails

**Error:** `Failed to deploy`

**Solutions:**
```bash
# Check Open Liberty is running
curl http://localhost:9080

# Check deployment folder permissions
ls -la $LIBERTY_HOME/standalone/deployments/

# Check Open Liberty logs
tail -f $LIBERTY_HOME/standalone/log/server.log

# Try manual deployment
cp target.war $LIBERTY_HOME/standalone/deployments/
```

---

### Issue 3: 404 Not Found

**Error:** Page not found

**Solutions:**
```bash
# Verify deployment
ls $LIBERTY_HOME/standalone/deployments.war.deployed

# Check context path
# Should be: 

# Verify servlet mappings
jar -tf target.war | grep -E "\.class|web.xml"

# Check Open Liberty logs for errors
grep ERROR $LIBERTY_HOME/standalone/log/server.log
```

---

### Issue 4: Port Already in Use

**Error:** `Address already in use`

**Solutions:**
```bash
# Find process using port 9080
lsof -i :9080  # macOS/Linux
netstat -ano | findstr :9080  # Windows

# Kill process
kill -9 <PID>

# Or change Open Liberty port in standalone.xml
```

---

### Issue 5: CSS Not Loading

**Error:** Styles not applied

**Solutions:**
```bash
# Verify CSS file in WAR
jar -tf target.war | grep css

# Check browser console for 404 errors
# Verify path: /css/style.css

# Clear browser cache
# Ctrl+Shift+R (hard refresh)
```

---

## 📈 Performance Testing

### Load Testing with Apache Bench

```bash
# Install Apache Bench
# macOS: brew install httpd
# Linux: sudo apt-get install apache2-utils

# Test home page
ab -n 1000 -c 10 http://localhost:9080/

# Test clients servlet
ab -n 1000 -c 10 http://localhost:9080/clients

# Expected: 
# Requests per second: > 100
# Time per request: < 100ms
```

---

## 🔍 Code Quality Checks

### Static Analysis

```bash
# Run SpotBugs
mvn spotbugs:check

# Run Checkstyle
mvn checkstyle:check

# Run PMD
mvn pmd:check
```

---

## 📝 Test Checklist

Before marking Lab 1 as complete:

- [ ] Application builds without errors
- [ ] WAR file created successfully
- [ ] Deployment succeeds
- [ ] Home page loads
- [ ] Welcome servlet works
- [ ] Client list displays
- [ ] Add client form works
- [ ] New clients are saved
- [ ] CSS styling applied
- [ ] Navigation links work
- [ ] Form validation works
- [ ] No console errors
- [ ] No server errors in logs

---

## 🎓 Learning Verification

### Questions to Answer

1. What is the servlet lifecycle?
2. How does HTTP GET differ from POST?
3. What is the purpose of web.xml?
4. How are servlets mapped to URLs?
5. What is a WAR file?
6. How does Maven build the project?
7. What is the role of Open Liberty?

### Code Understanding

Review and understand:
- `Client.java` - POJO structure
- `WelcomeServlet.java` - GET request handling
- `ClientListServlet.java` - GET and POST handling
- `pom.xml` - Maven configuration
- `web.xml` - Web application configuration

---

## 🚀 Next Steps

After successfully testing Lab 1:

1. **Experiment:** Modify code and redeploy
2. **Enhance:** Add new features
3. **Document:** Add comments to code
4. **Prepare:** Move to Lab 2 (JSP and JSTL)

---

## 📞 Getting Help

If you encounter issues:

1. Check this troubleshooting guide
2. Review Open Liberty logs
3. Consult course documentation
4. Ask instructor or TA
5. Check Jakarta EE documentation

---

## 📚 Additional Resources

### Documentation
- [Jakarta Servlet Specification](https://jakarta.ee/specifications/servlet/)
- [Open Liberty Documentation](https://docs.liberty.org/)
- [Maven WAR Plugin](https://maven.apache.org/plugins/maven-war-plugin/)

### Tools
- [Postman](https://www.postman.com/) - API testing
- [SoapUI](https://www.soapui.org/) - Web service testing
- [JMeter](https://jmeter.apache.org/) - Load testing

---

**Testing completed successfully? Move on to Lab 2! 🎉**