<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 02B - Testing Guide
## Comprehensive Testing Instructions for JSF Client Management

---

## 🎯 Overview

This guide provides detailed instructions for testing your Lab 02B JSF implementation using different deployment methods.

---

## 📋 Prerequisites

Before testing, ensure you have:

- ✅ **JDK 17+** installed
- ✅ **Maven 3.8+** installed
- ✅ **Podman** (recommended) OR **Docker** OR **Open Liberty** installed
- ✅ Completed Lab 02 successfully
- ✅ Reviewed Lecture 2B on JSF
- ✅ Basic understanding of JSF and Facelets

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
cd 03-Labs/Lab02B-JSF

# Run automated test script
./podman-test.sh
```

**What happens:**
1. ✅ Builds your application with Maven
2. ✅ Creates a container image with Open Liberty
3. ✅ Starts the container on port 9080
4. ✅ Waits for Liberty to start
5. ✅ Runs automated endpoint tests
6. ✅ Opens browser to http://localhost:9080/lab02b-jsf/

**Expected output:**
```
==========================================
Lab 02B: Podman + Open Liberty Testing
JSF Client Management
==========================================

✓ Podman found
✓ Build successful
✓ Podman image built
✓ Container started
✓ Open Liberty started successfully

Testing endpoints...
----------------------------
Testing home page... ✓ PASS
Testing client list page... ✓ PASS
Testing client form page... ✓ PASS
Testing health endpoint... ✓ PASS
Testing liveness endpoint... ✓ PASS
Testing readiness endpoint... ✓ PASS

Test Results: 6 passed, 0 failed
```

---

## 🧪 Testing Methods

### Method 2: Build Verification Only

Test that your code compiles and packages correctly:

```bash
cd 03-Labs/Lab02B-JSF
./test-lab.sh
```

**This script:**
- ✅ Verifies Java and Maven installation
- ✅ Builds the application
- ✅ Checks WAR file creation
- ✅ Verifies all required files are present
- ✅ Provides deployment options

**Expected output:**
```
==========================================
Lab 02B: JSF Client Management Test
==========================================

Checking prerequisites...
✓ Java 17 found
✓ Maven found

Building application...
✓ Build successful
✓ WAR file created: target/lab02b-jsf.war

Verifying key files...
✓ ClientBean.class found
✓ ClientService.class found
✓ Client.class found
✓ Address.class found
✓ EmailValidator.class found
✓ client-list.xhtml found
✓ client-form.xhtml found
✓ client-details.xhtml found
✓ main.xhtml template found
✓ index.xhtml found
✓ addressInput.xhtml composite component found
✓ web.xml found
✓ faces-config.xml found
✓ beans.xml found
✓ style.css found
```

### Method 3: Local Open Liberty (Maven)

If you have Open Liberty installed locally:

```bash
cd 03-Labs/Lab02B-JSF/starter  # or solution
mvn liberty:dev
```

**Access the application:**
- Home: http://localhost:9080/lab02b-jsf/
- Client List: http://localhost:9080/lab02b-jsf/views/client-list.xhtml

**To stop:**
Press `Ctrl+C` in the terminal

---

## 🔍 Manual Testing Checklist

Once the application is running, test these features:

### 1. Home Page
- [ ] Navigate to http://localhost:9080/lab02b-jsf/
- [ ] Verify page loads with ESIPE Bank branding
- [ ] Click "View Clients" link
- [ ] Verify navigation to client list

### 2. Client List Page
- [ ] Verify client list displays
- [ ] Test search functionality (type in search box)
- [ ] Verify AJAX updates table without page refresh
- [ ] Click "New Client" button
- [ ] Verify navigation to client form

### 3. Create Client
- [ ] Fill in client name (e.g., "John Doe")
- [ ] Fill in email (e.g., "john@example.com")
- [ ] Fill in address fields
- [ ] Click "Save"
- [ ] Verify success message appears
- [ ] Verify redirect to client list
- [ ] Verify new client appears in list

### 4. Form Validation
- [ ] Try to submit empty form
- [ ] Verify "required" validation messages
- [ ] Enter invalid email (e.g., "notanemail")
- [ ] Verify custom email validator error
- [ ] Enter name with less than 2 characters
- [ ] Verify length validation error

### 5. Edit Client
- [ ] Click "Edit" on a client
- [ ] Verify form pre-populated with client data
- [ ] Modify some fields
- [ ] Click "Save"
- [ ] Verify changes saved
- [ ] Verify redirect to client list

### 6. View Client Details
- [ ] Click "View" on a client
- [ ] Verify all client information displayed
- [ ] Verify address information shown
- [ ] Click "Edit" link
- [ ] Verify navigation to edit form
- [ ] Click "Back to List"
- [ ] Verify navigation to client list

### 7. Delete Client
- [ ] Click "Delete" on a client
- [ ] Verify client removed from list
- [ ] Verify success message

### 8. AJAX Search
- [ ] Type in search box (e.g., "John")
- [ ] Verify table updates without page refresh
- [ ] Verify only matching clients shown
- [ ] Clear search box
- [ ] Verify all clients shown again

### 9. Template and Navigation
- [ ] Verify header appears on all pages
- [ ] Verify footer appears on all pages
- [ ] Click "Home" link in navigation
- [ ] Verify navigation works
- [ ] Click "Clients" link in navigation
- [ ] Verify navigation works

### 10. Composite Component
- [ ] On client form, verify address section
- [ ] Verify all address fields present
- [ ] Verify country dropdown works
- [ ] Fill in address
- [ ] Save and verify address saved

---

## 🐛 Troubleshooting

### Issue: Build fails with compilation errors

**Solution:**
```bash
# Check Java version
java -version  # Must be 17+

# Clean and rebuild
cd starter
mvn clean compile
```

### Issue: WAR file not created

**Solution:**
```bash
# Check for errors in pom.xml
mvn validate

# Try verbose build
mvn clean package -X
```

### Issue: Podman container won't start

**Solution:**
```bash
# Check if port 9080 is in use
lsof -i :9080  # macOS/Linux
netstat -ano | findstr :9080  # Windows

# Stop conflicting containers
podman ps
podman stop <container-name>

# Check container logs
podman logs lab02b-jsf-container
```

### Issue: Faces Servlet not found (404 error)

**Solution:**
1. Verify `web.xml` has Faces Servlet configured
2. Check servlet mapping is `*.xhtml`
3. Ensure WAR contains `WEB-INF/web.xml`
4. Rebuild application

### Issue: Managed bean not found

**Solution:**
1. Verify `@Named` annotation on bean
2. Check `beans.xml` exists in `WEB-INF/`
3. Verify CDI is enabled in `server.xml`
4. Check bean scope annotation (`@ViewScoped`, etc.)

### Issue: JSF pages show as plain text

**Solution:**
1. Verify URL ends with `.xhtml` not `.html`
2. Check Faces Servlet mapping in `web.xml`
3. Ensure `faces-config.xml` is present
4. Verify JSF libraries in WAR

### Issue: AJAX not working

**Solution:**
1. Check `<f:ajax>` tag syntax
2. Verify `render` attribute targets correct component ID
3. Check browser console for JavaScript errors
4. Ensure `jakarta.faces.js` is loaded

### Issue: Validation not triggered

**Solution:**
1. Verify `required="true"` on input components
2. Check validator is properly registered
3. Ensure form has `<h:form>` tag
4. Verify `<h:messages>` component present

### Issue: Navigation not working

**Solution:**
1. Check action method return value
2. Verify `faces-redirect=true` parameter
3. Check navigation rules in `faces-config.xml`
4. Ensure target page exists

### Issue: Composite component not found

**Solution:**
1. Verify component in `resources/components/` directory
2. Check namespace declaration: `xmlns:comp="jakarta.faces.composite/components"`
3. Ensure `<composite:interface>` and `<composite:implementation>` tags
4. Verify component file name matches usage

---

## 📊 Performance Testing

### Load Testing with curl

Test multiple concurrent requests:

```bash
# Test home page
for i in {1..10}; do
  curl -s -o /dev/null -w "%{http_code}\n" \
    http://localhost:9080/lab02b-jsf/
done

# Test client list
for i in {1..10}; do
  curl -s -o /dev/null -w "%{http_code}\n" \
    http://localhost:9080/lab02b-jsf/views/client-list.xhtml
done
```

**Expected:** All requests return `200`

### Memory Usage

Check container memory usage:

```bash
podman stats lab02b-jsf-container
```

**Expected:** Memory usage < 512MB for basic operations

---

## 🔐 Security Testing

### Test Input Validation

1. **SQL Injection Attempt:**
   - Enter `'; DROP TABLE clients; --` in name field
   - Verify application handles safely

2. **XSS Attempt:**
   - Enter `<script>alert('XSS')</script>` in name field
   - Verify output is escaped

3. **Email Validation:**
   - Try various invalid emails
   - Verify custom validator catches them

---

## 📈 Monitoring

### Health Checks

```bash
# Overall health
curl http://localhost:9080/health

# Liveness
curl http://localhost:9080/health/live

# Readiness
curl http://localhost:9080/health/ready
```

### Metrics

```bash
# All metrics
curl http://localhost:9080/metrics

# Application metrics only
curl http://localhost:9080/metrics/application

# Base metrics
curl http://localhost:9080/metrics/base
```

---

## 🧹 Cleanup

### Stop and Remove Container

```bash
# Stop container
podman stop lab02b-jsf-container

# Remove container
podman rm lab02b-jsf-container

# Remove image
podman rmi lab02b-jsf:latest

# Or use cleanup script
./cleanup.sh  # if provided
```

### Clean Maven Build

```bash
cd starter  # or solution
mvn clean
```

---

## 📚 Additional Resources

### JSF Documentation
- [Jakarta Faces Specification](https://jakarta.ee/specifications/faces/)
- [Facelets Tag Library](https://jakarta.ee/specifications/faces/4.0/vdldoc/)
- [JSF Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/)

### Debugging Tools
- Browser Developer Tools (F12)
- JSF Debug Mode (set `PROJECT_STAGE` to `Development`)
- Open Liberty Logs: `podman logs -f lab02b-jsf-container`

### Common JSF Patterns
- [JSF Best Practices](https://www.baeldung.com/jsf)
- [PrimeFaces Showcase](https://www.primefaces.org/showcase/)

---

## ✅ Success Criteria

Your Lab 02B implementation is successful if:

- [x] Application builds without errors
- [x] WAR file contains all required files
- [x] All pages load without 404 errors
- [x] Client CRUD operations work correctly
- [x] Form validation functions properly
- [x] AJAX search updates table dynamically
- [x] Navigation between pages works
- [x] Template layout appears on all pages
- [x] Composite component renders correctly
- [x] Custom validator catches invalid emails
- [x] Health endpoints return UP status
- [x] No errors in browser console
- [x] No errors in server logs

---

## 🎓 Learning Verification

After completing testing, you should be able to:

1. ✅ Explain JSF lifecycle phases
2. ✅ Create managed beans with appropriate scopes
3. ✅ Build Facelets pages with templates
4. ✅ Implement form validation (built-in and custom)
5. ✅ Use AJAX for partial page updates
6. ✅ Create composite components
7. ✅ Handle navigation in JSF
8. ✅ Debug JSF applications
9. ✅ Deploy JSF applications to Open Liberty
10. ✅ Test JSF applications effectively

---

## 📞 Getting Help

If you encounter issues:

1. **Check logs:**
   ```bash
   podman logs -f lab02b-jsf-container
   ```

2. **Verify configuration:**
   - Check `web.xml`
   - Check `faces-config.xml`
   - Check `beans.xml`

3. **Browser console:**
   - Open Developer Tools (F12)
   - Check Console tab for JavaScript errors
   - Check Network tab for failed requests

4. **Ask for help:**
   - Provide error messages
   - Share relevant code snippets
   - Describe steps to reproduce issue

---

**Good luck with your testing! 🚀**
