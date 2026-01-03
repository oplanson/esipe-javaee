<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Account - Banking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>➕ New Account</h1>
            <p class="subtitle">Create a new bank account</p>
        </header>
        
        <main>
            <div class="actions" style="justify-content: flex-start; margin-bottom: 30px;">
                <a href="${pageContext.request.contextPath}/accounts${not empty selectedClientId ? '?clientId='.concat(selectedClientId) : ''}" 
                   class="button secondary">← Back</a>
            </div>
            
            <form method="post" action="${pageContext.request.contextPath}/accounts/new">
                <div class="form-group">
                    <label for="accountNumber">Account Number *</label>
                    <input type="text" 
                           id="accountNumber" 
                           name="accountNumber" 
                           required 
                           maxlength="20"
                           pattern="[A-Z0-9]+"
                           placeholder="ACC123456789"
                           title="Account number must contain only uppercase letters and numbers">
                    <small>Format: Uppercase letters and numbers only (e.g., ACC123456789)</small>
                </div>
                
                <div class="form-group">
                    <label for="clientId">Client *</label>
                    <select id="clientId" name="clientId" required>
                        <option value="">-- Select a client --</option>
                        <c:forEach var="client" items="${clients}">
                            <option value="${client.id}" 
                                    ${client.id == selectedClientId ? 'selected' : ''}>
                                ${client.fullName} (${client.email})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="accountType">Account Type *</label>
                    <select id="accountType" name="accountType" required>
                        <option value="">-- Select account type --</option>
                        <option value="CHECKING">Checking Account</option>
                        <option value="SAVINGS">Savings Account</option>
                        <option value="BUSINESS">Business Account</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="balance">Initial Balance (optional)</label>
                    <input type="number" 
                           id="balance" 
                           name="balance" 
                           step="0.01" 
                           min="0"
                           placeholder="0.00">
                    <small>Leave empty for zero balance. Must be positive if provided.</small>
                </div>
                
                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/accounts${not empty selectedClientId ? '?clientId='.concat(selectedClientId) : ''}" 
                       class="button secondary">Cancel</a>
                    <button type="submit" class="button">Create Account</button>
                </div>
            </form>
            
            <div class="info-section" style="margin-top: 30px; background: #f8f9fa; padding: 20px; border-radius: 10px;">
                <h3>ℹ️ Account Types</h3>
                <ul style="margin-top: 10px;">
                    <li><strong>Checking Account:</strong> For everyday transactions and payments</li>
                    <li><strong>Savings Account:</strong> For saving money with potential interest</li>
                    <li><strong>Business Account:</strong> For business operations and transactions</li>
                </ul>
            </div>
            
            <div class="info-section" style="margin-top: 20px; background: #f8f9fa; padding: 20px; border-radius: 10px;">
                <h3>🔄 Microservices Flow</h3>
                <p>When you create an account:</p>
                <ol style="margin-top: 10px;">
                    <li>API Gateway receives the request</li>
                    <li>Account Service validates the client exists by calling Client Service</li>
                    <li>If client exists, account is created in Account Service database</li>
                    <li>Fault tolerance ensures graceful handling if services are unavailable</li>
                </ol>
            </div>
        </main>
        
        <footer>
            <p>&copy; 2025-2026 Olivier Planson - Banking Microservices Lab 08</p>
        </footer>
    </div>
</body>
</html>