<%-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Lab 04B - EJB Banking</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
        h1 { color: #2c3e50; }
        h2 { color: #34495e; margin-top: 30px; }
        h3 { color: #7f8c8d; margin-top: 20px; }
        .success { color: green; padding: 10px; background: #d4edda; border: 1px solid #c3e6cb; margin: 10px 0; border-radius: 4px; }
        .error { color: #721c24; padding: 10px; background: #f8d7da; border: 1px solid #f5c6cb; margin: 10px 0; border-radius: 4px; }
        .info { color: #004085; padding: 10px; background: #cce5ff; border: 1px solid #b8daff; margin: 10px 0; border-radius: 4px; }
        table { border-collapse: collapse; width: 100%; margin: 20px 0; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #3498db; color: white; font-weight: bold; }
        tr:nth-child(even) { background-color: #f9f9f9; }
        tr:hover { background-color: #e8f4f8; }
        .status-active { color: #27ae60; font-weight: bold; }
        .status-inactive { color: #e74c3c; font-weight: bold; }
        .status-suspended { color: #f39c12; font-weight: bold; }
        .button { display: inline-block; padding: 10px 20px; margin: 5px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; transition: background 0.3s; }
        .button:hover { background: #2980b9; }
        .actions { margin: 20px 0; }
        .stats { background: white; padding: 20px; margin: 20px 0; border-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .stats p { margin: 10px 0; font-size: 16px; }
    </style>
</head>
<body>
    <h1>🏦 Lab 04B - Enterprise Java Beans Banking</h1>
    
    <!-- Display configuration info -->
    <div class="info">
        <strong>Application:</strong> <c:out value="${appName}"/><br>
        <strong>Version:</strong> <c:out value="${appVersion}"/><br>
        <strong>Report Statistics:</strong> <c:out value="${reportStats}"/>
    </div>
    
    <!-- Display success message if present -->
    <c:if test="${not empty successMessage}">
        <div class="success">✓ <c:out value="${successMessage}"/></div>
    </c:if>
    
    <!-- Display error message if present -->
    <c:if test="${not empty errorMessage}">
        <div class="error">✗ Error: <c:out value="${errorMessage}"/></div>
    </c:if>
    
    <!-- Display accounts table -->
    <c:if test="${showAccounts}">
        <h2>📊 All Accounts</h2>
        
        <c:choose>
            <c:when test="${empty accounts}">
                <p>No accounts found. Create one to get started!</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Account Number</th>
                        <th>Client</th>
                        <th>Email</th>
                        <th>Premium</th>
                        <th>Type</th>
                        <th>Balance</th>
                        <th>Status</th>
                        <th>Created</th>
                    </tr>
                    
                    <c:forEach var="account" items="${accounts}">
                        <tr>
                            <td><c:out value="${account.id}"/></td>
                            <td><c:out value="${account.accountNumber}"/></td>
                            <td><c:out value="${account.client != null ? account.client.name : 'N/A'}"/></td>
                            <td><c:out value="${account.client != null ? account.client.email : 'N/A'}"/></td>
                            <td><c:out value="${account.client != null && account.client.premium ? '⭐ Yes' : 'No'}"/></td>
                            <td><c:out value="${account.type}"/></td>
                            <td>$<fmt:formatNumber value="${account.balance}" pattern="#,##0.00"/></td>
                            <td><span class="status-${account.status.toString().toLowerCase()}"><c:out value="${account.status}"/></span></td>
                            <td><c:out value="${account.createdAt != null ? account.createdAt.toLocalDate() : 'N/A'}"/></td>
                        </tr>
                    </c:forEach>
                </table>
                
                <!-- Display summary statistics -->
                <div class="stats">
                    <h3>📈 Summary</h3>
                    <p>Total Accounts: <c:out value="${accounts.size()}"/></p>
                    <p>Total Balance: $<fmt:formatNumber value="${totalBalance}" pattern="#,##0.00"/></p>
                    <p>Active Accounts: <c:out value="${activeAccountCount}"/></p>
                </div>
            </c:otherwise>
        </c:choose>
    </c:if>
    
    <!-- Display clients table -->
    <c:if test="${showClients}">
        <h2>👥 All Clients</h2>
        
        <c:choose>
            <c:when test="${empty clients}">
                <p>No clients found.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Premium</th>
                        <th>Accounts</th>
                        <th>Created</th>
                    </tr>
                    
                    <c:forEach var="client" items="${clients}">
                        <tr>
                            <td><c:out value="${client.id}"/></td>
                            <td><c:out value="${client.name}"/></td>
                            <td><c:out value="${client.email}"/></td>
                            <td><c:out value="${client.phone != null ? client.phone : 'N/A'}"/></td>
                            <td><c:out value="${client.premium ? '⭐ Yes' : 'No'}"/></td>
                            <td><c:out value="${client.accounts.size()}"/></td>
                            <td><c:out value="${client.createdAt != null ? client.createdAt.toLocalDate() : 'N/A'}"/></td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </c:if>
    
    <!-- Action buttons -->
    <h2>🎯 Actions</h2>
    <div class="actions">
        <a href="banking?action=create" class="button">➕ Create Account</a>
        <a href="banking?action=clients" class="button">👥 View Clients</a>
        <a href="banking" class="button">🔄 Refresh</a>
        <a href="/" class="button">🏠 Back to Home</a>
    </div>
</body>
</html>