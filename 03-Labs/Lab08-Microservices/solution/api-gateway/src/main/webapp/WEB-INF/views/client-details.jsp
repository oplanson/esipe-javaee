<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Client Details - Banking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>👤 Client Details</h1>
            <p class="subtitle">View client information and accounts (Aggregated Data)</p>
        </header>
        
        <main>
            <div class="actions" style="justify-content: space-between; margin-bottom: 30px;">
                <a href="${pageContext.request.contextPath}/web/clients" class="button secondary">← Back to List</a>
                <div>
                    <a href="${pageContext.request.contextPath}/web/clients/edit?id=${clientWithAccounts.client.id}"
                       class="button">Edit Client</a>
                    <a href="${pageContext.request.contextPath}/web/accounts/new?clientId=${clientWithAccounts.client.id}"
                       class="button success">+ Add Account</a>
                    <a href="${pageContext.request.contextPath}/web/clients/delete?id=${clientWithAccounts.client.id}"
                       class="button danger"
                       onclick="return confirm('Are you sure you want to delete this client?')">Delete</a>
                </div>
            </div>
            
            <!-- Client Information -->
            <div class="info-section">
                <h2>Client Information</h2>
                <div class="service-box">
                    <p><strong>ID:</strong> ${clientWithAccounts.client.id}</p>
                    <p><strong>Name:</strong> ${clientWithAccounts.client.fullName}</p>
                    <p><strong>Email:</strong> ${clientWithAccounts.client.email}</p>
                    <p><strong>Phone:</strong> ${clientWithAccounts.client.phone}</p>
                    <c:if test="${not empty clientWithAccounts.client.createdAt}">
                        <p><strong>Created:</strong> 
                            <fmt:formatDate value="${clientWithAccounts.client.createdAt}" 
                                          pattern="yyyy-MM-dd HH:mm:ss"/>
                        </p>
                    </c:if>
                </div>
            </div>
            
            <!-- Accounts Summary -->
            <div class="info-section" style="margin-top: 30px;">
                <h2>Accounts Summary</h2>
                <div class="architecture-grid">
                    <div class="service-box">
                        <h4>📊 Total Accounts</h4>
                        <p style="font-size: 2em; color: #667eea; font-weight: bold;">
                            ${clientWithAccounts.accountCount}
                        </p>
                    </div>
                    <div class="service-box">
                        <h4>💰 Total Balance</h4>
                        <p style="font-size: 2em; color: #28a745; font-weight: bold;">
                            <fmt:formatNumber value="${clientWithAccounts.totalBalance}" 
                                            type="currency" 
                                            currencySymbol="€" 
                                            maxFractionDigits="2"/>
                        </p>
                    </div>
                </div>
            </div>
            
            <!-- Accounts List -->
            <div class="info-section" style="margin-top: 30px;">
                <h2>Accounts</h2>
                <c:choose>
                    <c:when test="${empty clientWithAccounts.accounts}">
                        <div class="service-box">
                            <p>No accounts found for this client.</p>
                            <a href="${pageContext.request.contextPath}/web/accounts/new?clientId=${clientWithAccounts.client.id}"
                               class="button success" style="margin-top: 15px;">+ Create First Account</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table>
                            <thead>
                                <tr>
                                    <th>Account Number</th>
                                    <th>Type</th>
                                    <th>Balance</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="account" items="${clientWithAccounts.accounts}">
                                    <tr>
                                        <td><strong>${account.accountNumber}</strong></td>
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
                                            <a href="${pageContext.request.contextPath}/web/accounts/view?id=${account.id}"
                                               class="button" style="padding: 8px 15px; font-size: 0.9em;">View</a>
                                            <c:if test="${account.status.name() == 'ACTIVE'}">
                                                <a href="${pageContext.request.contextPath}/web/accounts/deposit?id=${account.id}"
                                                   class="button success" style="padding: 8px 15px; font-size: 0.9em;">Deposit</a>
                                                <a href="${pageContext.request.contextPath}/web/accounts/withdraw?id=${account.id}"
                                                   class="button secondary" style="padding: 8px 15px; font-size: 0.9em;">Withdraw</a>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div class="info-section" style="margin-top: 30px; background: #f8f9fa; padding: 20px; border-radius: 10px;">
                <h3>🔄 Data Aggregation</h3>
                <p>This page demonstrates the <strong>BFF (Backend For Frontend)</strong> pattern:</p>
                <ul style="margin-top: 10px;">
                    <li>Client information retrieved from <strong>Client Service</strong> (port 9081)</li>
                    <li>Account information retrieved from <strong>Account Service</strong> (port 9082)</li>
                    <li>Data aggregated by the <strong>API Gateway</strong> using MicroProfile Rest Client</li>
                    <li>Fault tolerance ensures graceful degradation if services are unavailable</li>
                </ul>
            </div>
        </main>
        
        <footer>
            <p>&copy; 2025-2026 Olivier Planson - Banking Microservices Lab 08</p>
        </footer>
    </div>
</body>
</html>