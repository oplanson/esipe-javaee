<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${appName} - Client Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Client Details</h1>
        </header>

        <!-- Client Information -->
        <div class="client-info card">
            <h2>Personal Information</h2>
            
            <!-- TODO: Display client ID -->
            <p><strong>ID:</strong> ${client.id}</p>
            
            <!-- TODO: Display client name -->
            <!-- Hint: Use c:out for safety -->
            

            <!-- TODO: Display client email -->
            

        </div>

        <!-- Accounts Section -->
        <div class="accounts-section card">
            <h2>Accounts</h2>
            
            <!-- TODO: Check if client has accounts -->
            <!-- Use c:choose with c:when and c:otherwise -->
            
            <!-- If no accounts: -->
            <!-- <p class="no-data">No accounts found for this client.</p> -->
            
            <!-- If accounts exist: -->
            <!-- Create a table with columns: Account Number, Type, Balance -->
            <!-- Loop through client.accounts using c:forEach -->
            <!-- Format balance using fmt:formatNumber with type="currency" -->
            
            <!-- Example structure:
            <table class="table">
                <thead>
                    <tr>
                        <th>Account Number</th>
                        <th>Type</th>
                        <th>Balance</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="account" items="${client.accounts}">
                        <tr>
                            <td>TODO: account number</td>
                            <td>TODO: account type</td>
                            <td>TODO: formatted balance</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            -->
            
        </div>

        <!-- Actions -->
        <div class="actions">
            <!-- TODO: Add Edit button -->
            <!-- Link to: ${pageContext.request.contextPath}/client?action=edit&id=${client.id} -->
            

            <!-- TODO: Add Delete button (only if deletionEnabled) -->
            <!-- Use c:if to check deletionEnabled -->
            <!-- Use form with POST method and action="delete" -->
            <!-- Add confirmation with onclick="return confirm('Are you sure?')" -->
            

            <!-- TODO: Add Back to List button -->
            <!-- Link to: ${pageContext.request.contextPath}/clients -->
            
        </div>
    </div>
</body>
</html>