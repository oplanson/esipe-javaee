<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${operation == 'deposit'}">Deposit Money</c:when>
            <c:when test="${operation == 'withdraw'}">Withdraw Money</c:when>
            <c:when test="${operation == 'transfer'}">Transfer Money</c:when>
        </c:choose>
        - Banking Application
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>
                <c:choose>
                    <c:when test="${operation == 'deposit'}">💰 Deposit Money</c:when>
                    <c:when test="${operation == 'withdraw'}">💸 Withdraw Money</c:when>
                    <c:when test="${operation == 'transfer'}">🔄 Transfer Money</c:when>
                </c:choose>
            </h1>
            <p class="subtitle">
                <c:choose>
                    <c:when test="${operation == 'deposit'}">Add money to account</c:when>
                    <c:when test="${operation == 'withdraw'}">Withdraw money from account</c:when>
                    <c:when test="${operation == 'transfer'}">Transfer money between accounts</c:when>
                </c:choose>
            </p>
        </header>
        
        <main>
            <div class="actions" style="justify-content: flex-start; margin-bottom: 30px;">
                <a href="${pageContext.request.contextPath}/accounts/view?id=${operation == 'transfer' ? fromAccount.id : account.id}" 
                   class="button secondary">← Back to Account</a>
            </div>
            
            <!-- Account Information -->
            <div class="info-section" style="margin-bottom: 30px;">
                <h3>
                    <c:choose>
                        <c:when test="${operation == 'transfer'}">Source Account</c:when>
                        <c:otherwise>Account Information</c:otherwise>
                    </c:choose>
                </h3>
                <div class="service-box">
                    <p><strong>Account Number:</strong> ${operation == 'transfer' ? fromAccount.accountNumber : account.accountNumber}</p>
                    <p><strong>Current Balance:</strong> 
                        <strong style="color: #28a745; font-size: 1.5em;">
                            <fmt:formatNumber value="${operation == 'transfer' ? fromAccount.balance : account.balance}" 
                                            type="currency" 
                                            currencySymbol="€" 
                                            maxFractionDigits="2"/>
                        </strong>
                    </p>
                </div>
            </div>
            
            <!-- Transaction Form -->
            <form method="post" action="${pageContext.request.contextPath}/accounts/${operation}">
                <c:choose>
                    <c:when test="${operation == 'transfer'}">
                        <input type="hidden" name="fromId" value="${fromAccount.id}">
                        
                        <div class="form-group">
                            <label for="toId">Target Account *</label>
                            <select id="toId" name="toId" required>
                                <option value="">-- Select target account --</option>
                                <c:forEach var="targetAccount" items="${allAccounts}">
                                    <c:if test="${targetAccount.id != fromAccount.id && targetAccount.status.name() == 'ACTIVE'}">
                                        <option value="${targetAccount.id}">
                                            ${targetAccount.accountNumber} - 
                                            <fmt:formatNumber value="${targetAccount.balance}" 
                                                            type="currency" 
                                                            currencySymbol="€" 
                                                            maxFractionDigits="2"/>
                                        </option>
                                    </c:if>
                                </c:forEach>
                            </select>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="id" value="${account.id}">
                    </c:otherwise>
                </c:choose>
                
                <div class="form-group">
                    <label for="amount">Amount (€) *</label>
                    <input type="number" 
                           id="amount" 
                           name="amount" 
                           step="0.01" 
                           min="0.01"
                           <c:if test="${operation == 'withdraw' || operation == 'transfer'}">
                               max="${operation == 'transfer' ? fromAccount.balance : account.balance}"
                           </c:if>
                           required 
                           placeholder="0.00">
                    <c:if test="${operation == 'withdraw' || operation == 'transfer'}">
                        <small>Maximum: 
                            <fmt:formatNumber value="${operation == 'transfer' ? fromAccount.balance : account.balance}" 
                                            type="currency" 
                                            currencySymbol="€" 
                                            maxFractionDigits="2"/>
                        </small>
                    </c:if>
                </div>
                
                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/accounts/view?id=${operation == 'transfer' ? fromAccount.id : account.id}" 
                       class="button secondary">Cancel</a>
                    <button type="submit" class="button 
                        <c:choose>
                            <c:when test="${operation == 'deposit'}">success</c:when>
                            <c:when test="${operation == 'withdraw'}">secondary</c:when>
                            <c:otherwise></c:otherwise>
                        </c:choose>">
                        <c:choose>
                            <c:when test="${operation == 'deposit'}">💰 Deposit</c:when>
                            <c:when test="${operation == 'withdraw'}">💸 Withdraw</c:when>
                            <c:when test="${operation == 'transfer'}">🔄 Transfer</c:when>
                        </c:choose>
                    </button>
                </div>
            </form>
            
            <!-- Operation Information -->
            <div class="info-section" style="margin-top: 30px; background: #f8f9fa; padding: 20px; border-radius: 10px;">
                <h3>ℹ️ Operation Information</h3>
                <c:choose>
                    <c:when test="${operation == 'deposit'}">
                        <p><strong>Deposit:</strong> Add money to your account.</p>
                        <ul style="margin-top: 10px;">
                            <li>Amount must be positive</li>
                            <li>No maximum limit</li>
                            <li>Balance will be updated immediately</li>
                        </ul>
                    </c:when>
                    <c:when test="${operation == 'withdraw'}">
                        <p><strong>Withdraw:</strong> Remove money from your account.</p>
                        <ul style="margin-top: 10px;">
                            <li>Amount must be positive</li>
                            <li>Cannot exceed current balance</li>
                            <li>Account must have sufficient funds</li>
                        </ul>
                    </c:when>
                    <c:when test="${operation == 'transfer'}">
                        <p><strong>Transfer:</strong> Move money between accounts.</p>
                        <ul style="margin-top: 10px;">
                            <li>Amount must be positive</li>
                            <li>Cannot exceed source account balance</li>
                            <li>Both accounts must be active</li>
                            <li>Transaction is atomic (all or nothing)</li>
                        </ul>
                    </c:when>
                </c:choose>
            </div>
            
            <div class="info-section" style="margin-top: 20px; background: #f8f9fa; padding: 20px; border-radius: 10px;">
                <h3>🔄 Microservices Transaction</h3>
                <p>This operation is processed by the Account Service with:</p>
                <ul style="margin-top: 10px;">
                    <li>Domain-driven business rules validation</li>
                    <li>Transactional consistency (JPA transactions)</li>
                    <li>Fault tolerance (retry, timeout, circuit breaker)</li>
                    <li>Immediate balance updates</li>
                </ul>
            </div>
        </main>
        
        <footer>
            <p>&copy; 2025-2026 Olivier Planson - Banking Microservices Lab 08</p>
        </footer>
    </div>
</body>
</html>