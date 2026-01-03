<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty client ? 'New' : 'Edit'} Client - Banking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>${empty client ? '➕ New Client' : '✏️ Edit Client'}</h1>
            <p class="subtitle">${empty client ? 'Add a new client' : 'Update client information'}</p>
        </header>
        
        <main>
            <div class="actions" style="justify-content: flex-start; margin-bottom: 30px;">
                <a href="${pageContext.request.contextPath}/clients" class="button secondary">← Back to List</a>
            </div>
            
            <form method="post" action="${pageContext.request.contextPath}/${empty client ? 'clients/new' : 'clients/edit'}">
                <c:if test="${not empty client}">
                    <input type="hidden" name="id" value="${client.id}">
                </c:if>
                
                <div class="form-group">
                    <label for="firstName">First Name *</label>
                    <input type="text" 
                           id="firstName" 
                           name="firstName" 
                           value="${client.firstName}" 
                           required 
                           maxlength="50"
                           placeholder="Enter first name">
                </div>
                
                <div class="form-group">
                    <label for="lastName">Last Name *</label>
                    <input type="text" 
                           id="lastName" 
                           name="lastName" 
                           value="${client.lastName}" 
                           required 
                           maxlength="50"
                           placeholder="Enter last name">
                </div>
                
                <div class="form-group">
                    <label for="email">Email *</label>
                    <input type="email" 
                           id="email" 
                           name="email" 
                           value="${client.email}" 
                           required 
                           maxlength="100"
                           placeholder="client@example.com">
                </div>
                
                <div class="form-group">
                    <label for="phone">Phone *</label>
                    <input type="tel" 
                           id="phone" 
                           name="phone" 
                           value="${client.phone}" 
                           required 
                           maxlength="20"
                           placeholder="+33 1 23 45 67 89">
                </div>
                
                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/clients" class="button secondary">Cancel</a>
                    <button type="submit" class="button">${empty client ? 'Create Client' : 'Update Client'}</button>
                </div>
            </form>
            
            <div class="info-section" style="margin-top: 30px; background: #f8f9fa; padding: 20px; border-radius: 10px;">
                <h3>ℹ️ Information</h3>
                <p>This form sends data to the <strong>Client Service</strong> via the API Gateway using MicroProfile Rest Client.</p>
                <ul style="margin-top: 10px;">
                    <li>All fields are required</li>
                    <li>Email must be unique</li>
                    <li>Phone number should include country code</li>
                </ul>
            </div>
        </main>
        
        <footer>
            <p>&copy; 2025-2026 Olivier Planson - Banking Microservices Lab 08</p>
        </footer>
    </div>
</body>
</html>