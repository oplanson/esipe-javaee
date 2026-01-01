<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Banking Application - Client Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <div class="container">
            <h1>Banking Application</h1>
            <nav>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/clients">Clients</a></li>
                </ul>
            </nav>
        </div>
    </header>

    <div class="container">
        <!-- Success Message -->
        <c:if test="${param.message == 'updated'}">
            <div class="alert alert-success">
                ✓ Client updated successfully!
            </div>
        </c:if>

        <!-- Client Information -->
        <div class="card">
            <div class="card-header">
                <h2>Client Details</h2>
            </div>

            <div class="client-details">
                <div class="detail-item">
                    <label>ID</label>
                    <div class="value">${client.id}</div>
                </div>

                <div class="detail-item">
                    <label>Name</label>
                    <div class="value"><c:out value="${client.name}" /></div>
                </div>

                <div class="detail-item">
                    <label>Email</label>
                    <div class="value"><c:out value="${client.email}" /></div>
                </div>

                <div class="detail-item">
                    <label>Number of Accounts</label>
                    <div class="value">
                        <span class="badge badge-info">${client.accounts.size()}</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Accounts Section -->
        <div class="card mt-3">
            <div class="card-header">
                <h2>Accounts</h2>
            </div>

            <c:choose>
                <c:when test="${empty client.accounts}">
                    <div class="empty-state">
                        <p>📭 No accounts found for this client.</p>
                        <p><small>Accounts will be displayed here once they are created.</small></p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Account Number</th>
                                    <th>Type</th>
                                    <th>Balance</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="account" items="${client.accounts}">
                                    <tr>
                                        <td>${account.id}</td>
                                        <td><c:out value="${account.number}" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${account.type == 'CHECKING'}">
                                                    <span class="badge badge-info">Checking</span>
                                                </c:when>
                                                <c:when test="${account.type == 'SAVINGS'}">
                                                    <span class="badge badge-success">Savings</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${account.type}" />
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${account.balance}" 
                                                            type="currency" 
                                                            currencySymbol="€" 
                                                            maxFractionDigits="2" 
                                                            minFractionDigits="2" />
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot>
                                <tr>
                                    <td colspan="3" class="text-right"><strong>Total Balance:</strong></td>
                                    <td>
                                        <strong>
                                            <c:set var="totalBalance" value="0" />
                                            <c:forEach var="account" items="${client.accounts}">
                                                <c:set var="totalBalance" value="${totalBalance + account.balance}" />
                                            </c:forEach>
                                            <fmt:formatNumber value="${totalBalance}" 
                                                            type="currency" 
                                                            currencySymbol="€" 
                                                            maxFractionDigits="2" 
                                                            minFractionDigits="2" />
                                        </strong>
                                    </td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Actions -->
        <div class="card mt-3">
            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/client?action=edit&id=${client.id}" 
                   class="btn btn-primary">
                    ✏️ Edit Client
                </a>

                <c:if test="${deletionEnabled}">
                    <form method="post" 
                          action="${pageContext.request.contextPath}/client" 
                          style="display:inline;" 
                          onsubmit="return confirm('Are you sure you want to delete this client? This action cannot be undone.');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${client.id}">
                        <button type="submit" class="btn btn-danger">
                            🗑️ Delete Client
                        </button>
                    </form>
                </c:if>

                <a href="${pageContext.request.contextPath}/clients" class="btn btn-secondary">
                    ← Back to List
                </a>
            </div>
        </div>
    </div>

    <footer>
        <div class="container">
            <p>&copy; 2025 Banking Application - Lab 02</p>
            <p>Made with ❤️ using Jakarta EE & MicroProfile</p>
        </div>
    </footer>
</body>
</html>