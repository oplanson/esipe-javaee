<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transaction Details - Banking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>🏦 Banking Application</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/">Home</a>
                <a href="${pageContext.request.contextPath}/clients">Clients</a>
                <a href="${pageContext.request.contextPath}/accounts">Accounts</a>
                <a href="${pageContext.request.contextPath}/transactions" class="active">Transactions</a>
            </nav>
        </header>
        
        <main>
            <h2>Transaction Details</h2>
            
            <div class="details-card">
                <h3>Transaction Information</h3>
                
                <div class="detail-row">
                    <span class="label">Transaction ID:</span>
                    <span class="value">${transaction.id}</span>
                </div>
                
                <div class="detail-row">
                    <span class="label">Date & Time:</span>
                    <span class="value">
                        ${transaction.formattedTransactionDate}
                    </span>
                </div>
                
                <div class="detail-row">
                    <span class="label">Type:</span>
                    <span class="value">
                        <span class="badge badge-${fn:toLowerCase(transaction.type)}">
                            ${transaction.type}
                        </span>
                    </span>
                </div>
                
                <div class="detail-row">
                    <span class="label">Amount:</span>
                    <span class="value amount ${transaction.type == 'DEPOSIT' || transaction.type == 'TRANSFER_IN' ? 'positive' : 'negative'}">
                        <c:choose>
                            <c:when test="${transaction.type == 'DEPOSIT' || transaction.type == 'TRANSFER_IN'}">
                                +<fmt:formatNumber value="${transaction.amount}" type="currency" currencySymbol="€"/>
                            </c:when>
                            <c:otherwise>
                                -<fmt:formatNumber value="${transaction.amount}" type="currency" currencySymbol="€"/>
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>
                
                <div class="detail-row">
                    <span class="label">Balance Before:</span>
                    <span class="value"><fmt:formatNumber value="${transaction.balanceBefore}" type="currency" currencySymbol="€"/></span>
                </div>
                
                <div class="detail-row">
                    <span class="label">Balance After:</span>
                    <span class="value"><fmt:formatNumber value="${transaction.balanceAfter}" type="currency" currencySymbol="€"/></span>
                </div>
                
                <c:if test="${not empty transaction.description}">
                    <div class="detail-row">
                        <span class="label">Description:</span>
                        <span class="value">${transaction.description}</span>
                    </div>
                </c:if>
                
                <c:if test="${not empty transaction.targetAccountId}">
                    <div class="detail-row">
                        <span class="label">Target Account ID:</span>
                        <span class="value">${transaction.targetAccountId}</span>
                    </div>
                </c:if>
            </div>
            
            <div class="details-card">
                <h3>Account Information</h3>
                
                <div class="detail-row">
                    <span class="label">Account Number:</span>
                    <span class="value">${account.number}</span>
                </div>
                
                <div class="detail-row">
                    <span class="label">Account Type:</span>
                    <span class="value">${account.type}</span>
                </div>
                
                <div class="detail-row">
                    <span class="label">Current Balance:</span>
                    <span class="value"><fmt:formatNumber value="${account.balance}" type="currency" currencySymbol="€"/></span>
                </div>
                
                <div class="detail-row">
                    <span class="label">Client ID:</span>
                    <span class="value">${account.clientId}</span>
                </div>
            </div>
            
            <div class="actions">
                <a href="${pageContext.request.contextPath}/transactions?accountId=${account.id}" class="button">View Account Transactions</a>
                <a href="${pageContext.request.contextPath}/accounts/view?id=${account.id}" class="button">View Account Details</a>
                <a href="${pageContext.request.contextPath}/transactions" class="button secondary">All Transactions</a>
                <a href="${pageContext.request.contextPath}/" class="button secondary">Home</a>
            </div>
        </main>
        
        <footer>
            <p>&copy; 2026 Olivier Planson - Banking Application Lab 05</p>
        </footer>
    </div>
</body>
</html>
<!-- Made with Bob -->