<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${appName} - Clients</title>
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
        <div class="card">
            <div class="card-header">
                <h2>Client Management</h2>
            </div>

            <!-- Success Messages -->
            <c:if test="${not empty param.message}">
                <c:choose>
                    <c:when test="${param.message == 'created'}">
                        <div class="alert alert-success">
                            ✓ Client created successfully!
                        </div>
                    </c:when>
                    <c:when test="${param.message == 'updated'}">
                        <div class="alert alert-success">
                            ✓ Client updated successfully!
                        </div>
                    </c:when>
                    <c:when test="${param.message == 'deleted'}">
                        <div class="alert alert-success">
                            ✓ Client deleted successfully!
                        </div>
                    </c:when>
                </c:choose>
            </c:if>

            <!-- Search Form -->
            <form method="get" action="${pageContext.request.contextPath}/clients" class="mb-3">
                <div class="form-group">
                    <input type="text" name="search" placeholder="Search by name..." 
                           value="<c:out value='${param.search}' />" class="form-control">
                    <button type="submit" class="btn btn-secondary">Search</button>
                    <c:if test="${not empty param.search}">
                        <a href="${pageContext.request.contextPath}/clients" class="btn btn-secondary">Clear</a>
                    </c:if>
                </div>
            </form>

            <!-- Client Table -->
            <c:choose>
                <c:when test="${empty clients}">
                    <div class="empty-state">
                        <p>No clients found.</p>
                        <c:if test="${not empty param.search}">
                            <p>Try a different search term or <a href="${pageContext.request.contextPath}/clients">view all clients</a>.</p>
                        </c:if>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Status</th>
                                    <th>Accounts</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="client" items="${clients}">
                                    <tr>
                                        <td>${client.id}</td>
                                        <td><c:out value="${client.name}" /></td>
                                        <td><c:out value="${client.email}" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${client.premium}">
                                                    <span class="badge badge-success" title="Premium client with enhanced services">
                                                        ⭐ PREMIUM
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-secondary" title="Standard client">
                                                        STANDARD
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <span class="badge badge-info">
                                                ${client.accounts.size()}
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/client?action=view&id=${client.id}" 
                                               class="btn btn-primary btn-sm">View</a>
                                            <a href="${pageContext.request.contextPath}/client?action=edit&id=${client.id}" 
                                               class="btn btn-secondary btn-sm">Edit</a>
                                            <c:if test="${deletionEnabled}">
                                                <form method="post" action="${pageContext.request.contextPath}/client" 
                                                      style="display:inline;" 
                                                      onsubmit="return confirm('Are you sure you want to delete this client?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${client.id}">
                                                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                                </form>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>

            <!-- Actions -->
            <div class="mt-3">
                <a href="${pageContext.request.contextPath}/client?action=new" class="btn btn-primary">
                    ➕ Add New Client
                </a>
            </div>
        </div>

        <!-- Statistics -->
        <div class="card mt-3">
            <p><strong>Total Clients:</strong> ${clients.size()}</p>
            <c:if test="${not empty param.search}">
                <p><strong>Search Results:</strong> Showing ${clients.size()} client(s) matching "<c:out value='${param.search}' />"</p>
            </c:if>
        </div>
    </div>

    <footer>
        <div class="container">
            <p>&copy; 2026 Banking Application - Lab 05</p>
            <p>Made with ❤️ using Jakarta EE & MicroProfile</p>
        </div>
    </footer>
</body>
</html>