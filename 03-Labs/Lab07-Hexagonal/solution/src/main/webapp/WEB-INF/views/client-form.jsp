<%-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Banking Application - Client Form</title>
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
                <c:if test="${empty client}">
                    <h2>New Client</h2>
                </c:if>
                <c:if test="${not empty client}">
                    <h2>Edit Client</h2>
                </c:if>
            </div>

            <!-- Error Messages -->
            <c:if test="${not empty param.error}">
                <c:choose>
                    <c:when test="${param.error == 'name_required'}">
                        <div class="alert alert-danger">
                            ✗ Name is required!
                        </div>
                    </c:when>
                    <c:when test="${param.error == 'email_required'}">
                        <div class="alert alert-danger">
                            ✗ Email is required!
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-danger">
                            ✗ An error occurred. Please try again.
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:if>

            <!-- Client Form - CREATE (when client is empty) -->
            <c:if test="${empty client}">
                <form method="post" action="${pageContext.request.contextPath}/clients">
                    <div class="form-group">
                        <label for="name">Name: <span style="color: red;">*</span></label>
                        <input type="text" id="name" name="name" required maxlength="100" placeholder="Enter client name">
                        <small>Full name of the client</small>
                    </div>

                    <div class="form-group">
                        <label for="email">Email: <span style="color: red;">*</span></label>
                        <input type="email" id="email" name="email" required maxlength="100" placeholder="client@example.com">
                        <small>Valid email address</small>
                    </div>

                    <div class="form-group">
                        <label for="premium">
                            <input type="checkbox" id="premium" name="premium">
                            <strong>Premium Client</strong>
                        </label>
                        <small>Premium clients receive enhanced notification services</small>
                    </div>

                    <div class="btn-group">
                        <button type="submit" class="btn btn-primary">➕ Create Client</button>
                        <a href="${pageContext.request.contextPath}/clients" class="btn btn-secondary">✗ Cancel</a>
                    </div>
                </form>
            </c:if>

            <!-- Client Form - UPDATE (when client exists) -->
            <c:if test="${not empty client}">
                <form method="post" action="${pageContext.request.contextPath}/clients/${client.id}">
                    <div class="form-group">
                        <label for="name">Name: <span style="color: red;">*</span></label>
                        <input type="text" id="name" name="name" value="<c:out value='${client.name}' />" required maxlength="100" placeholder="Enter client name">
                        <small>Full name of the client</small>
                    </div>

                    <div class="form-group">
                        <label for="email">Email: <span style="color: red;">*</span></label>
                        <input type="email" id="email" name="email" value="<c:out value='${client.email}' />" required maxlength="100" placeholder="client@example.com">
                        <small>Valid email address</small>
                    </div>

                    <div class="form-group">
                        <label for="premium">
                            <input type="checkbox" id="premium" name="premium" ${client.premium ? 'checked' : ''}>
                            <strong>Premium Client</strong>
                        </label>
                        <small>Premium clients receive enhanced notification services</small>
                    </div>

                    <div class="btn-group">
                        <button type="submit" class="btn btn-primary">✓ Update Client</button>
                        <a href="${pageContext.request.contextPath}/clients" class="btn btn-secondary">✗ Cancel</a>
                    </div>
                </form>
            </c:if>

            <!-- Help Text -->
            <div class="mt-3">
                <p><small><span style="color: red;">*</span> Required fields</small></p>
            </div>
        </div>
    </div>

    <footer>
        <div class="container">
            <p>&copy; 2026 Banking Application - Lab 07</p>
        </div>
    </footer>
</body>
</html>