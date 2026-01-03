<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Client List - Banking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>👥 Client List</h1>
            <p class="subtitle">View all clients with their accounts (Aggregated Data)</p>
        </header>
        
        <main>
            <div class="actions" style="justify-content: space-between; margin-bottom: 30px;">
                <a href="${pageContext.request.contextPath}/" class="button secondary">← Back to Home</a>
                <a href="${pageContext.request.contextPath}/clients/new" class="button">+ Add New Client</a>
            </div>
            
            <c:if test="${not empty error}">
                <div class="error-message">
                    <strong>Error:</strong> ${error}
                </div>
            </c:if>
            
            <c:choose>
                <c:when test="${empty clientsWithAccounts}">
                    <div class="info-section">
                        <p>No clients found. <a href="${pageContext.request.contextPath}/clients/new">Add your first client</a></p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Accounts</th>
                                <th>Total Balance</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="clientWithAccounts" items="${clientsWithAccounts}">
                                <tr>
                                    <td>${clientWithAccounts.client.id}</td>
                                    <td>
                                        <strong>${clientWithAccounts.client.fullName}</strong>
                                    </td>
                                    <td>${clientWithAccounts.client.email}</td>
                                    <td>${clientWithAccounts.client.phone}</td>
                                    <td>
                                        <span class="account-type">${clientWithAccounts.accountCount} account(s)</span>
                                    </td>
                                    <td>
                                        <strong>
                                            <fmt:formatNumber value="${clientWithAccounts.totalBalance}" 
                                                            type="currency" 
                                                            currencySymbol="€" 
                                                            maxFractionDigits="2"/>
                                        </strong>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/clients?action=view&id=${clientWithAccounts.client.id}" 
                                           class="button" style="padding: 8px 15px; font-size: 0.9em;">View</a>
                                        <a href="${pageContext.request.contextPath}/clients/edit?id=${clientWithAccounts.client.id}" 
                                           class="button secondary" style="padding: 8px 15px; font-size: 0.9em;">Edit</a>
                                        <a href="${pageContext.request.contextPath}/accounts?clientId=${clientWithAccounts.client.id}" 
                                           class="button success" style="padding: 8px 15px; font-size: 0.9em;">Accounts</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    
                    <div class="info-section" style="margin-top: 30px;">
                        <h3>📊 Summary</h3>
                        <p><strong>Total Clients:</strong> ${clientsWithAccounts.size()}</p>
                        <p><strong>Note:</strong> This view demonstrates the BFF pattern - data is aggregated from Client Service and Account Service</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
        
        <footer>
            <p>&copy; 2025-2026 Olivier Planson - Banking Microservices Lab 08</p>
        </footer>
    </div>
</body>
</html>