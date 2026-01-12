<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Lab 02B: JSF Client Management

## 🎯 Objectives

In this lab, you will:
- Create JSF pages using Facelets templating
- Implement managed beans with appropriate scopes
- Use JSF validators and converters
- Implement AJAX functionality for dynamic updates
- Create reusable composite components
- Apply JSF navigation patterns
- Build a complete CRUD interface with JSF

**Duration:** 3 hours  
**Difficulty:** Intermediate  
**Prerequisites:** Lab 02 completed, Lecture 2B reviewed

---

## 📋 What You'll Build

A JSF-based client management interface with:
- **Template Layout:** Reusable page template with header/footer
- **Client List:** DataTable with search and pagination
- **Client Form:** Create/edit form with validation
- **Client Details:** Master-detail view with accounts
- **AJAX Search:** Real-time client search
- **Composite Component:** Reusable address input component
- **Custom Validator:** Email validation
- **Navigation:** Implicit and explicit navigation

---

## 🏗️ Architecture

```
Lab02B-JSF/
├── starter/                    # Your starting point
│   ├── pom.xml                # Dependencies configured
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/bank/
│   │   │   │       ├── model/
│   │   │   │       │   ├── Client.java          # Provided
│   │   │   │       │   ├── Account.java         # Provided
│   │   │   │       │   └── Address.java         # TODO: Create
│   │   │   │       ├── service/
│   │   │   │       │   └── ClientService.java   # Provided
│   │   │   │       ├── web/
│   │   │   │       │   └── ClientBean.java      # TODO: Implement
│   │   │   │       └── validator/
│   │   │   │           └── EmailValidator.java  # TODO: Implement
│   │   │   ├── resources/
│   │   │   │   └── META-INF/
│   │   │   │       └── microprofile-config.properties
│   │   │   └── webapp/
│   │   │       ├── WEB-INF/
│   │   │       │   ├── web.xml
│   │   │       │   ├── faces-config.xml         # TODO: Configure
│   │   │       │   ├── templates/
│   │   │       │   │   └── main.xhtml           # TODO: Create
│   │   │       │   └── views/
│   │   │       │       ├── client-list.xhtml    # TODO: Create
│   │   │       │       ├── client-form.xhtml    # TODO: Create
│   │   │       │       └── client-details.xhtml # TODO: Create
│   │   │       ├── resources/
│   │   │       │   └── components/
│   │   │       │       └── addressInput.xhtml   # TODO: Create
│   │   │       ├── css/
│   │   │       │   └── style.css                # Provided
│   │   │       └── index.xhtml                  # TODO: Create
│   │   └── test/
│   └── Containerfile
├── solution/                   # Complete working solution
└── README.md                   # This file
```

---

## 📝 Lab Instructions

### Part 1: Setup and Configuration (20 minutes)

#### Step 1.1: Review Dependencies

The `pom.xml` already includes:
- Jakarta EE 10 Web Profile
- JSF (Faces) API
- CDI
- Bean Validation

#### Step 1.2: Configure Faces Servlet

Open `src/main/webapp/WEB-INF/web.xml` and verify the Faces Servlet configuration:

```xml
<servlet>
    <servlet-name>Faces Servlet</servlet-name>
    <servlet-class>jakarta.faces.webapp.FacesServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>Faces Servlet</servlet-name>
    <url-pattern>*.xhtml</url-pattern>
</servlet-mapping>

<welcome-file-list>
    <welcome-file>index.xhtml</welcome-file>
</welcome-file-list>

<context-param>
    <param-name>jakarta.faces.PROJECT_STAGE</param-name>
    <param-value>Development</param-value>
</context-param>
```

#### Step 1.3: Create faces-config.xml

Create `src/main/webapp/WEB-INF/faces-config.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<faces-config xmlns="https://jakarta.ee/xml/ns/jakartaee"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee 
                                  https://jakarta.ee/xml/ns/jakartaee/web-facesconfig_4_0.xsd"
              version="4.0">
    
    <application>
        <locale-config>
            <default-locale>en</default-locale>
        </locale-config>
    </application>
    
</faces-config>
```

---

### Part 2: Model Layer (15 minutes)

#### Step 2.1: Create Address Entity

Create `src/main/java/com/bank/model/Address.java`:

```java
package com.bank.model;

import java.io.Serializable;

public class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String street;
    private String city;
    private String postalCode;
    private String country;
    
    // TODO: Add constructors
    // TODO: Add getters and setters
    // TODO: Add toString() method
}
```

**Tasks:**
1. Add default and parameterized constructors
2. Add getters and setters for all fields
3. Add a `toString()` method

#### Step 2.2: Update Client Entity

The `Client.java` is provided but needs an address field. Add:

```java
private Address address;

// Add getter and setter
public Address getAddress() { return address; }
public void setAddress(Address address) { this.address = address; }
```

---

### Part 3: Managed Bean (30 minutes)

#### Step 3.1: Create ClientBean

Create `src/main/java/com/bank/web/ClientBean.java`:

```java
package com.bank.web;

import com.bank.model.Client;
import com.bank.model.Address;
import com.bank.service.ClientService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class ClientBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ClientService clientService;
    
    private Client client = new Client();
    private List<Client> clients;
    private List<Client> filteredClients;
    private String searchTerm = "";
    private Long selectedClientId;
    
    @PostConstruct
    public void init() {
        loadClients();
        client.setAddress(new Address());
    }
    
    public void loadClients() {
        // TODO: Load all clients from service
    }
    
    public void search() {
        // TODO: Filter clients based on searchTerm
        // Filter by name or email containing searchTerm (case-insensitive)
    }
    
    public String save() {
        // TODO: Save client using service
        // Add success message
        // Reload clients
        // Reset form
        // Navigate to client-list with redirect
    }
    
    public String edit() {
        // TODO: Load client by selectedClientId
        // Navigate to client-form with redirect
    }
    
    public String delete() {
        // TODO: Delete client by selectedClientId
        // Add success message
        // Reload clients
        // Stay on same page (return null)
    }
    
    public String cancel() {
        // TODO: Reset client
        // Navigate to client-list with redirect
    }
    
    public String viewDetails() {
        // TODO: Load client by selectedClientId
        // Navigate to client-details with redirect
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(severity, summary, detail));
    }
    
    // TODO: Add getters and setters for all fields
}
```

**Tasks:**
1. Implement all TODO methods
2. Add proper error handling with try-catch
3. Add FacesMessage for user feedback
4. Implement getters and setters

---

### Part 4: Custom Validator (15 minutes)

#### Step 4.1: Create Email Validator

Create `src/main/java/com/bank/validator/EmailValidator.java`:

```java
package com.bank.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import java.util.regex.Pattern;

@FacesValidator("emailValidator")
public class EmailValidator implements Validator<String> {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    @Override
    public void validate(FacesContext context, UIComponent component, String value) 
            throws ValidatorException {
        // TODO: Implement validation logic
        // 1. Return if value is null or empty (use required="true" for that)
        // 2. Check if value matches EMAIL_PATTERN
        // 3. If not, throw ValidatorException with appropriate FacesMessage
    }
}
```

**Tasks:**
1. Implement the validation logic
2. Create appropriate error messages
3. Test with valid and invalid emails

---

### Part 5: Facelets Template (20 minutes)

#### Step 5.1: Create Main Template

Create `src/main/webapp/WEB-INF/templates/main.xhtml`:

```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="jakarta.faces.html"
      xmlns:ui="jakarta.faces.facelets">
<h:head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title><ui:insert name="title">Banking Application</ui:insert></title>
    <h:outputStylesheet library="css" name="style.css"/>
</h:head>
<h:body>
    <div id="wrapper">
        <!-- Header -->
        <div id="header">
            <ui:insert name="header">
                <h1>🏦 ESIPE Bank - Client Management</h1>
                <nav>
                    <h:link outcome="/index" value="Home"/>
                    <h:link outcome="/views/client-list" value="Clients"/>
                </nav>
            </ui:insert>
        </div>
        
        <!-- Content -->
        <div id="content">
            <ui:insert name="content">
                <!-- Page content goes here -->
            </ui:insert>
        </div>
        
        <!-- Footer -->
        <div id="footer">
            <ui:insert name="footer">
                <p>&copy; 2026 ESIPE Bank. All rights reserved.</p>
            </ui:insert>
        </div>
    </div>
</h:body>
</html>
```

---

### Part 6: JSF Pages (40 minutes)

#### Step 6.1: Create Index Page

Create `src/main/webapp/index.xhtml`:

```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="jakarta.faces.facelets"
      xmlns:h="jakarta.faces.html">

<ui:composition template="/WEB-INF/templates/main.xhtml">
    
    <ui:define name="title">Home - Banking Application</ui:define>
    
    <ui:define name="content">
        <div class="welcome">
            <h2>Welcome to ESIPE Bank</h2>
            <p>Client Management System with JSF</p>
            
            <div class="actions">
                <h:link outcome="/views/client-list" 
                       value="View Clients" 
                       styleClass="button"/>
            </div>
        </div>
    </ui:define>
    
</ui:composition>
</html>
```

#### Step 6.2: Create Client List Page

Create `src/main/webapp/WEB-INF/views/client-list.xhtml`:

```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="jakarta.faces.facelets"
      xmlns:h="jakarta.faces.html"
      xmlns:f="jakarta.faces.core">

<ui:composition template="/WEB-INF/templates/main.xhtml">
    
    <ui:define name="title">Client List</ui:define>
    
    <ui:define name="content">
        <h2>Client List</h2>
        
        <h:form id="clientListForm">
            <!-- Messages -->
            <h:messages globalOnly="true" styleClass="messages"/>
            
            <!-- Search Box with AJAX -->
            <div class="search-box">
                <h:outputLabel value="Search:" for="search"/>
                <h:inputText id="search" value="#{clientBean.searchTerm}">
                    <!-- TODO: Add f:ajax tag -->
                    <!-- event="keyup", listener="#{clientBean.search}", render="clientTable" -->
                </h:inputText>
            </div>
            
            <!-- Action Buttons -->
            <div class="actions">
                <h:commandButton value="New Client" 
                                action="client-form?faces-redirect=true"
                                styleClass="button"/>
            </div>
            
            <!-- Client Table -->
            <h:dataTable id="clientTable" 
                        value="#{clientBean.filteredClients != null ? clientBean.filteredClients : clientBean.clients}" 
                        var="client"
                        styleClass="data-table"
                        headerClass="table-header"
                        rowClasses="row-odd,row-even">
                
                <!-- TODO: Add columns for ID, Name, Email, Actions -->
                <!-- ID Column -->
                <h:column>
                    <f:facet name="header">ID</f:facet>
                    #{client.id}
                </h:column>
                
                <!-- Name Column -->
                <!-- TODO: Add name column -->
                
                <!-- Email Column -->
                <!-- TODO: Add email column -->
                
                <!-- Actions Column -->
                <h:column>
                    <f:facet name="header">Actions</f:facet>
                    <!-- TODO: Add View, Edit, Delete buttons -->
                    <!-- Use commandButton with action methods -->
                    <!-- Set selectedClientId using f:setPropertyActionListener -->
                </h:column>
            </h:dataTable>
        </h:form>
    </ui:define>
    
</ui:composition>
</html>
```

**Tasks:**
1. Add AJAX to search input
2. Complete all table columns
3. Add action buttons (View, Edit, Delete)
4. Use `f:setPropertyActionListener` to set selectedClientId

#### Step 6.3: Create Client Form Page

Create `src/main/webapp/WEB-INF/views/client-form.xhtml`:

```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="jakarta.faces.facelets"
      xmlns:h="jakarta.faces.html"
      xmlns:f="jakarta.faces.core"
      xmlns:comp="jakarta.faces.composite/components">

<ui:composition template="/WEB-INF/templates/main.xhtml">
    
    <ui:define name="title">
        #{clientBean.client.id == null ? 'New Client' : 'Edit Client'}
    </ui:define>
    
    <ui:define name="content">
        <h2>#{clientBean.client.id == null ? 'New Client' : 'Edit Client'}</h2>
        
        <h:form id="clientForm">
            <h:messages globalOnly="true" styleClass="messages"/>
            
            <h:panelGrid columns="2" styleClass="form-grid">
                <!-- Name -->
                <h:outputLabel value="Name:" for="name"/>
                <h:inputText id="name" 
                            value="#{clientBean.client.name}" 
                            required="true"
                            requiredMessage="Name is required">
                    <f:validateLength minimum="2" maximum="100"/>
                </h:inputText>
                
                <!-- Email -->
                <h:outputLabel value="Email:" for="email"/>
                <h:inputText id="email" 
                            value="#{clientBean.client.email}" 
                            required="true"
                            requiredMessage="Email is required">
                    <!-- TODO: Add custom email validator -->
                    <f:validator validatorId="emailValidator"/>
                </h:inputText>
            </h:panelGrid>
            
            <!-- Address Section -->
            <h3>Address</h3>
            <!-- TODO: Use composite component for address -->
            <!-- <comp:addressInput address="#{clientBean.client.address}" required="true"/> -->
            
            <!-- For now, use simple inputs -->
            <h:panelGrid columns="2" styleClass="form-grid">
                <h:outputLabel value="Street:" for="street"/>
                <h:inputText id="street" value="#{clientBean.client.address.street}"/>
                
                <!-- TODO: Add city, postalCode, country fields -->
            </h:panelGrid>
            
            <!-- Buttons -->
            <div class="form-actions">
                <h:commandButton value="Save" 
                                action="#{clientBean.save}"
                                styleClass="button button-primary"/>
                <h:commandButton value="Cancel" 
                                action="#{clientBean.cancel}"
                                immediate="true"
                                styleClass="button"/>
            </div>
        </h:form>
    </ui:define>
    
</ui:composition>
</html>
```

**Tasks:**
1. Add remaining address fields
2. Add email validator
3. Test form validation

#### Step 6.4: Create Client Details Page

Create `src/main/webapp/WEB-INF/views/client-details.xhtml`:

```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="jakarta.faces.facelets"
      xmlns:h="jakarta.faces.html"
      xmlns:f="jakarta.faces.core">

<ui:composition template="/WEB-INF/templates/main.xhtml">
    
    <ui:define name="title">Client Details</ui:define>
    
    <ui:define name="content">
        <h2>Client Details</h2>
        
        <div class="details-panel">
            <h3>Personal Information</h3>
            <dl>
                <dt>ID:</dt>
                <dd>#{clientBean.client.id}</dd>
                
                <dt>Name:</dt>
                <dd>#{clientBean.client.name}</dd>
                
                <dt>Email:</dt>
                <dd>#{clientBean.client.email}</dd>
            </dl>
            
            <!-- TODO: Add address display -->
            <h3>Address</h3>
            <dl>
                <dt>Street:</dt>
                <dd>#{clientBean.client.address.street}</dd>
                
                <!-- TODO: Add city, postal code, country -->
            </dl>
            
            <!-- TODO: Add accounts section if available -->
        </div>
        
        <div class="actions">
            <h:link outcome="client-form" value="Edit" styleClass="button">
                <f:param name="id" value="#{clientBean.client.id}"/>
            </h:link>
            <h:link outcome="client-list" value="Back to List" styleClass="button"/>
        </div>
    </ui:define>
    
</ui:composition>
</html>
```

**Tasks:**
1. Complete address display
2. Add accounts section (if time permits)

---

### Part 7: Composite Component (25 minutes)

#### Step 7.1: Create Address Input Component

Create `src/main/webapp/resources/components/addressInput.xhtml`:

```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="jakarta.faces.html"
      xmlns:f="jakarta.faces.core"
      xmlns:composite="jakarta.faces.composite">

<composite:interface>
    <composite:attribute name="address" 
                        required="true" 
                        type="com.bank.model.Address"/>
    <composite:attribute name="required" 
                        default="false"
                        type="java.lang.Boolean"/>
</composite:interface>

<composite:implementation>
    <h:panelGrid columns="2" styleClass="address-input">
        <h:outputLabel value="Street:"/>
        <h:inputText value="#{cc.attrs.address.street}" 
                    required="#{cc.attrs.required}"/>
        
        <!-- TODO: Add city field -->
        
        <!-- TODO: Add postal code field -->
        
        <!-- TODO: Add country dropdown -->
        <!-- Use selectOneMenu with hardcoded countries for now -->
    </h:panelGrid>
</composite:implementation>

</html>
```

**Tasks:**
1. Complete all address fields
2. Add country dropdown with options (France, USA, UK, Germany, Spain)
3. Update client-form.xhtml to use this component

---

### Part 8: Testing (15 minutes)

#### Step 8.1: Build and Deploy

```bash
cd starter
mvn clean package liberty:run
```

#### Step 8.2: Test Functionality

1. **Navigate to** http://localhost:9080/lab02b-jsf/
2. **Test Client List:**
   - View all clients
   - Use search functionality (AJAX)
   - Verify real-time filtering

3. **Test Create Client:**
   - Click "New Client"
   - Fill form with valid data
   - Test validation (empty fields, invalid email)
   - Save and verify redirect

4. **Test Edit Client:**
   - Click "Edit" on a client
   - Modify data
   - Save and verify changes

5. **Test View Details:**
   - Click "View" on a client
   - Verify all information displayed
   - Test navigation back to list

6. **Test Delete Client:**
   - Click "Delete" on a client
   - Verify client removed from list

---

## 🎓 Learning Outcomes

After completing this lab, you will be able to:

✅ Create JSF pages using Facelets templating  
✅ Implement managed beans with appropriate scopes  
✅ Use JSF validators (built-in and custom)  
✅ Implement AJAX for dynamic page updates  
✅ Create reusable composite components  
✅ Apply JSF navigation patterns  
✅ Build complete CRUD interfaces with JSF  
✅ Handle form validation and error messages  
✅ Use Expression Language (EL) effectively  

---

## 📚 Additional Challenges

If you finish early, try these enhancements:

### Challenge 1: Add Pagination
- Implement pagination for client list
- Show 10 clients per page
- Add "Previous" and "Next" buttons

### Challenge 2: Add Sorting
- Make table columns sortable
- Click column header to sort
- Show sort direction indicator

### Challenge 3: Add Confirmation Dialog
- Add JavaScript confirmation before delete
- Use `onclick="return confirm('Are you sure?')"`

### Challenge 4: Add PrimeFaces
- Add PrimeFaces dependency
- Replace h:dataTable with p:dataTable
- Add p:dialog for edit form

### Challenge 5: Add Bean Validation
- Add @Email, @NotBlank annotations to Client
- Remove custom validator
- Use built-in Bean Validation

---

## 🐛 Troubleshooting

### Issue: Faces Servlet not found
**Solution:** Verify web.xml configuration and servlet mapping

### Issue: Managed bean not found
**Solution:** Check @Named annotation and CDI configuration (beans.xml)

### Issue: AJAX not working
**Solution:** Verify f:ajax attributes (event, listener, render)

### Issue: Validation not triggered
**Solution:** Check required="true" and validator configuration

### Issue: Navigation not working
**Solution:** Verify outcome strings and faces-redirect parameter

---

## 📖 References

- [Jakarta Faces Specification](https://jakarta.ee/specifications/faces/)
- [Jakarta EE Tutorial - JSF](https://eclipse-ee4j.github.io/jakartaee-tutorial/)
- [Facelets Tag Library](https://jakarta.ee/specifications/faces/4.0/vdldoc/)
- [PrimeFaces Showcase](https://www.primefaces.org/showcase/)

---

## ✅ Submission Checklist

Before submitting, ensure:

- [ ] All TODO items completed
- [ ] Application builds without errors
- [ ] All CRUD operations work correctly
- [ ] AJAX search functions properly
- [ ] Form validation works (required fields, email format)
- [ ] Custom validator implemented
- [ ] Composite component created and used
- [ ] Navigation works correctly
- [ ] No console errors in browser
- [ ] Code follows Java naming conventions
- [ ] Code is properly commented

---

**Good luck! 🚀**
