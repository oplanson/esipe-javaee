<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Banking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>⚠️ Error</h1>
        </header>
        
        <main>
            <div class="error-message">
                <h2>An error occurred</h2>
                
                <% if (request.getAttribute("error") != null) { %>
                    <p class="error"><%= request.getAttribute("error") %></p>
                <% } else if (exception != null) { %>
                    <p class="error"><%= exception.getMessage() %></p>
                <% } else { %>
                    <p>An unexpected error occurred. Please try again.</p>
                <% } %>
                
                <div class="actions">
                    <a href="${pageContext.request.contextPath}/clients" class="button">Back to Client List</a>
                    <a href="${pageContext.request.contextPath}/" class="button secondary">Home</a>
                </div>
            </div>
        </main>
        
        <footer>
            <p>&copy; 2026 Banking Application - Lab 06</p>
        </footer>
    </div>
</body>
</html>