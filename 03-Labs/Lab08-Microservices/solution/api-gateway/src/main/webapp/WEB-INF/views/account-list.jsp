<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account List - Banking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>💰 Account List</h1>
            <p class="subtitle">
                <c:choose>
                    <c:when test="${not empty client}">
                        Accounts for ${client.fullName}
                    </c:when>
                    <c:otherwise>
                        All bank accounts
                    </c:otherwise>
                </c:choose>
            </p>
        </header>
        
        <main>
            <div class="actions" style="justify-content: space-between; margin-bottom: 30px;">
                <a href="${pageContext.request.contextPath}/${not empty client ? 'clients?action=view&id='.concat(client.id) : ''}" 
                   class="button secondary">← Back</a>
                <c:if test="${not empty client}">
                    <a href="${pageContext.request.contextPath}/accounts/new?clientId=${client.id}" 
                       class="button">+ Add New Account</a>
                </c:if>
            </div>
            
            <c:if test="${not empty client}">
                <div class="info-section" style="margin-bottom: 30px;">
                    <h3>Client Information</h3>
                    <p><strong>Name:</strong> ${client.fullName}</p>
                    <p><strong>Email:</strong> ${client.email}</p>
                </div>
            </c:if>
            
            <c:choose>
                <c:when test="${empty accounts}">
                    <div class="info-section">
                        <p>No accounts found.</p>
                        <c:if test="${not empty client}">
                            <a href="${pageContext.request.contextPath}/accounts/new?clientId=${client.id}" 
                               class="button" style="margin-top: 15px;">+ Create First Account</a>
                        </c:if>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>Account Number</th>
                                <th>Client ID</th>
                                <th>Type</th>
                                <th>Balance</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="account" items="${accounts}">
                                <tr>
                                    <td><strong>${account.accountNumber}</strong></td>
                                    <td>${account.clientId}</td>
                                    <td>
                                        <span class="account-type">${account.accountType.displayName}</span>
                                    </td>
                                    <td>
                                        <strong>
                                            <fmt:formatNumber value="${account.balance}" 
                                                            type="currency" 
                                                            currencySymbol="€" 
                                                            maxFractionDigits="2"/>
                                        </strong>
                                    </td>
                                    <td>
                                        <span class="status-badge status-${account.status.name().toLowerCase()}">
                                            ${account.status.displayName}
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/accounts/view?id=${account.id}" 
                                           class="button" style="padding: 8px 15px; font-size: 0.9em;">View</a>
                                        <c:if test="${account.status.name() == 'ACTIVE'}">
                                            <a href="${pageContext.request.contextPath}/accounts/deposit?id=${account.id}" 
                                               class="button success" style="padding: 8px 15px; font-size: 0.9em;">Deposit</a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    
                    <div class="info-section" style="margin-top: 30px;">
                        <h3>📊 Summary</h3>
                        <p><strong>Total Accounts:</strong> ${accounts.size()}</p>
                        <c:set var="totalBalance" value="0"/>
                        <c:forEach var="account" items="${accounts}">
                            <c:set var="totalBalance" value="${totalBalance + account.balance}"/>
                        </c:forEach>
                        <p><strong>Total Balance:</strong> 
                            <fmt:formatNumber value="${totalBalance}" 
                                            type="currency" 
                                            currencySymbol="€" 
                                            maxFractionDigits="2"/>
                        </p>
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