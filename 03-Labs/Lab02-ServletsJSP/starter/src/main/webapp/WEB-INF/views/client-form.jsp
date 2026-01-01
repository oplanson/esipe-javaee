<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${appName} - ${empty client ? 'New' : 'Edit'} Client</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>${empty client ? 'New' : 'Edit'} Client</h1>
        </header>

        <!-- TODO: Add error message display -->
        <!-- Hint: Check if param.error exists and display appropriate message -->
        

        <!-- Client Form -->
        <form method="post" action="${pageContext.request.contextPath}/client" class="form">
            
            <!-- TODO: Add hidden field for action -->
            <!-- If client is empty: action="create" -->
            <!-- If client exists: action="update" -->
            <!-- Hint: Use c:choose or conditional expression -->
            

            <!-- TODO: Add hidden field for id (only if editing) -->
            <!-- Hint: Use c:if to check if client is not empty -->
            

            <!-- Name Field -->
            <div class="form-group">
                <label for="name">Name: <span class="required">*</span></label>
                <!-- TODO: Add input field for name -->
                <!-- Set value to client.name if editing -->
                <!-- Hint: <input type="text" id="name" name="name" value="<c:out value='${client.name}' />" required /> -->
                
            </div>

            <!-- Email Field -->
            <div class="form-group">
                <label for="email">Email: <span class="required">*</span></label>
                <!-- TODO: Add input field for email -->
                <!-- Set value to client.email if editing -->
                <!-- Add type="email" for HTML5 validation -->
                
            </div>

            <!-- Form Actions -->
            <div class="form-actions">
                <!-- TODO: Add submit button -->
                <!-- Text: "Create Client" if new, "Update Client" if editing -->
                

                <!-- TODO: Add cancel button/link -->
                <!-- Link back to: ${pageContext.request.contextPath}/clients -->
                
            </div>
        </form>

        <!-- Help Text -->
        <div class="help-text">
            <p><span class="required">*</span> Required fields</p>
        </div>
    </div>
</body>
</html>