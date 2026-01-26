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
    <title>${title} - Banking Application</title>
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
            <h2>${title}</h2>
            
            <c:if test="${not empty account}">
                <div class="info-box">
                    <h3>Account Information</h3>
                    <p><strong>Account Number:</strong> ${account.number}</p>
                    <p><strong>Type:</strong> ${account.type}</p>
                    <p><strong>Current Balance:</strong> <fmt:formatNumber value="${account.balance}" type="currency" currencySymbol="€"/></p>
                    <p>
                        <a href="${pageContext.request.contextPath}/accounts/view?id=${account.id}" class="button">View Account Details</a>
                    </p>
                </div>
            </c:if>
            
            <c:if test="${not empty error}">
                <div class="error-message">
                    <p>${error}</p>
                </div>
            </c:if>
            
            <c:choose>
                <c:when test="${empty transactions}">
                    <div class="info-message">
                        <p>No transactions found.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Type</th>
                                    <th>Amount</th>
                                    <th>Balance Before</th>
                                    <th>Balance After</th>
                                    <th>Description</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="transaction" items="${transactions}">
                                    <tr>
                                        <td>
                                            ${transaction.formattedTransactionDate}
                                        </td>
                                        <td>
                                            <span class="badge badge-${fn:toLowerCase(transaction.type)}">
                                                ${transaction.type}
                                            </span>
                                        </td>
                                        <td class="amount ${transaction.type == 'DEPOSIT' || transaction.type == 'TRANSFER_IN' ? 'positive' : 'negative'}">
                                            <c:choose>
                                                <c:when test="${transaction.type == 'DEPOSIT' || transaction.type == 'TRANSFER_IN'}">
                                                    +<fmt:formatNumber value="${transaction.amount}" type="currency" currencySymbol="€"/>
                                                </c:when>
                                                <c:otherwise>
                                                    -<fmt:formatNumber value="${transaction.amount}" type="currency" currencySymbol="€"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><fmt:formatNumber value="${transaction.balanceBefore}" type="currency" currencySymbol="€"/></td>
                                        <td><fmt:formatNumber value="${transaction.balanceAfter}" type="currency" currencySymbol="€"/></td>
                                        <td>${transaction.description}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/transactions/view?id=${transaction.id}" 
                                               class="button button-small">View</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    
                    <div class="summary">
                        <p><strong>Total Transactions:</strong> ${transactions.size()}</p>
                    </div>
                </c:otherwise>
            </c:choose>
            
            <div class="actions">
                <c:if test="${not empty account}">
                    <a href="${pageContext.request.contextPath}/accounts/view?id=${account.id}" class="button">Back to Account</a>
                </c:if>
                <a href="${pageContext.request.contextPath}/transactions" class="button">All Transactions</a>
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