<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Details - Banking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>💳 Account Details</h1>
            <p class="subtitle">View account information and perform operations</p>
        </header>
        
        <main>
            <div class="actions" style="justify-content: space-between; margin-bottom: 30px;">
                <a href="${pageContext.request.contextPath}/accounts?clientId=${account.clientId}" 
                   class="button secondary">← Back to Accounts</a>
                <div>
                    <c:if test="${account.status.name() == 'ACTIVE'}">
                        <a href="${pageContext.request.contextPath}/accounts/deposit?id=${account.id}" 
                           class="button success">💰 Deposit</a>
                        <a href="${pageContext.request.contextPath}/accounts/withdraw?id=${account.id}" 
                           class="button secondary">💸 Withdraw</a>
                        <a href="${pageContext.request.contextPath}/accounts/transfer?id=${account.id}" 
                           class="button">🔄 Transfer</a>
                        <a href="${pageContext.request.contextPath}/accounts/suspend?id=${account.id}" 
                           class="button danger"
                           onclick="return confirm('Are you sure you want to suspend this account?')">⏸️ Suspend</a>
                    </c:if>
                    <c:if test="${account.status.name() == 'SUSPENDED'}">
                        <a href="${pageContext.request.contextPath}/accounts/activate?id=${account.id}" 
                           class="button success">▶️ Activate</a>
                    </c:if>
                    <c:if test="${account.status.name() == 'ACTIVE' || account.status.name() == 'SUSPENDED'}">
                        <a href="${pageContext.request.contextPath}/accounts/close?id=${account.id}" 
                           class="button danger"
                           onclick="return confirm('Are you sure you want to close this account? Balance must be zero.')">🔒 Close</a>
                    </c:if>
                    <c:if test="${account.status.name() == 'CLOSED'}">
                        <a href="${pageContext.request.contextPath}/accounts/delete?id=${account.id}&clientId=${account.clientId}" 
                           class="button danger"
                           onclick="return confirm('Are you sure you want to delete this account?')">🗑️ Delete</a>
                    </c:if>
                </div>
            </div>
            
            <!-- Account Information -->
            <div class="info-section">
                <h2>Account Information</h2>
                <div class="architecture-grid">
                    <div class="service-box">
                        <h4>📋 Basic Information</h4>
                        <p><strong>Account Number:</strong> ${account.accountNumber}</p>
                        <p><strong>Type:</strong> 
                            <span class="account-type">${account.accountType.displayName}</span>
                        </p>
                        <p><strong>Status:</strong> 
                            <span class="status-badge status-${account.status.name().toLowerCase()}">
                                ${account.status.displayName}
                            </span>
                        </p>
                    </div>
                    
                    <div class="service-box">
                        <h4>💰 Balance</h4>
                        <p style="font-size: 2.5em; color: #28a745; font-weight: bold; margin: 20px 0;">
                            <fmt:formatNumber value="${account.balance}" 
                                            type="currency" 
                                            currencySymbol="€" 
                                            maxFractionDigits="2"/>
                        </p>
                    </div>
                    
                    <div class="service-box">
                        <h4>👤 Client Information</h4>
                        <p><strong>Name:</strong> ${client.fullName}</p>
                        <p><strong>Email:</strong> ${client.email}</p>
                        <p><strong>Phone:</strong> ${client.phone}</p>
                        <a href="${pageContext.request.contextPath}/clients?action=view&id=${client.id}" 
                           class="button" style="margin-top: 10px;">View Client</a>
                    </div>
                </div>
            </div>
            
            <!-- Available Operations -->
            <div class="info-section" style="margin-top: 30px;">
                <h2>Available Operations</h2>
                <div class="features-grid">
                    <c:if test="${account.status.name() == 'ACTIVE'}">
                        <div class="feature">
                            <h4>💰 Deposit</h4>
                            <p>Add money to this account</p>
                            <a href="${pageContext.request.contextPath}/accounts/deposit?id=${account.id}" 
                               class="button success" style="margin-top: 10px;">Deposit</a>
                        </div>
                        
                        <div class="feature">
                            <h4>💸 Withdraw</h4>
                            <p>Withdraw money from this account</p>
                            <a href="${pageContext.request.contextPath}/accounts/withdraw?id=${account.id}" 
                               class="button secondary" style="margin-top: 10px;">Withdraw</a>
                        </div>
                        
                        <div class="feature">
                            <h4>🔄 Transfer</h4>
                            <p>Transfer money to another account</p>
                            <a href="${pageContext.request.contextPath}/accounts/transfer?id=${account.id}" 
                               class="button" style="margin-top: 10px;">Transfer</a>
                        </div>
                    </c:if>
                    
                    <c:if test="${account.status.name() == 'SUSPENDED'}">
                        <div class="feature">
                            <h4>⚠️ Account Suspended</h4>
                            <p>This account is currently suspended. No transactions are allowed.</p>
                            <a href="${pageContext.request.contextPath}/accounts/activate?id=${account.id}" 
                               class="button success" style="margin-top: 10px;">Activate Account</a>
                        </div>
                    </c:if>
                    
                    <c:if test="${account.status.name() == 'CLOSED'}">
                        <div class="feature">
                            <h4>🔒 Account Closed</h4>
                            <p>This account is closed. No operations are allowed.</p>
                        </div>
                    </c:if>
                </div>
            </div>
            
            <!-- Account Status Information -->
            <div class="info-section" style="margin-top: 30px; background: #f8f9fa; padding: 20px; border-radius: 10px;">
                <h3>ℹ️ Account Status Information</h3>
                <c:choose>
                    <c:when test="${account.status.name() == 'ACTIVE'}">
                        <p><strong>Active:</strong> This account is fully operational. All transactions are allowed.</p>
                    </c:when>
                    <c:when test="${account.status.name() == 'SUSPENDED'}">
                        <p><strong>Suspended:</strong> This account is temporarily suspended. No transactions are allowed until reactivated.</p>
                    </c:when>
                    <c:when test="${account.status.name() == 'CLOSED'}">
                        <p><strong>Closed:</strong> This account is permanently closed. The balance must be zero before closing.</p>
                    </c:when>
                </c:choose>
            </div>
            
            <div class="info-section" style="margin-top: 30px; background: #f8f9fa; padding: 20px; border-radius: 10px;">
                <h3>🔄 Microservices Communication</h3>
                <p>This page demonstrates inter-service communication:</p>
                <ul style="margin-top: 10px;">
                    <li>Account data retrieved from <strong>Account Service</strong> (port 9082)</li>
                    <li>Client data retrieved from <strong>Client Service</strong> (port 9081)</li>
                    <li>All operations performed via <strong>API Gateway</strong> with fault tolerance</li>
                </ul>
            </div>
        </main>
        
        <footer>
            <p>&copy; 2025-2026 Olivier Planson - Banking Microservices Lab 08</p>
        </footer>
    </div>
</body>
</html>