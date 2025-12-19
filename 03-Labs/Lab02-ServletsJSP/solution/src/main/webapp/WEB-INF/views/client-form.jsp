<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Banking Application - ${empty client ? 'New' : 'Edit'} Client</title>
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
                <h2>${empty client ? 'New' : 'Edit'} Client</h2>
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

            <!-- Client Form -->
            <form method="post" action="${pageContext.request.contextPath}/client">
                
                <!-- Hidden Fields -->
                <input type="hidden" name="action" value="${empty client ? 'create' : 'update'}">
                <c:if test="${not empty client}">
                    <input type="hidden" name="id" value="${client.id}">
                </c:if>

                <!-- Name Field -->
                <div class="form-group">
                    <label for="name">Name: <span style="color: red;">*</span></label>
                    <input type="text" 
                           id="name" 
                           name="name" 
                           value="<c:out value='${client.name}' />" 
                           required 
                           maxlength="100"
                           placeholder="Enter client name">
                    <small>Full name of the client</small>
                </div>

                <!-- Email Field -->
                <div class="form-group">
                    <label for="email">Email: <span style="color: red;">*</span></label>
                    <input type="email" 
                           id="email" 
                           name="email" 
                           value="<c:out value='${client.email}' />" 
                           required 
                           maxlength="100"
                           placeholder="client@example.com">
                    <small>Valid email address</small>
                </div>

                <!-- Form Actions -->
                <div class="btn-group">
                    <button type="submit" class="btn btn-primary">
                        ${empty client ? '➕ Create Client' : '✓ Update Client'}
                    </button>
                    <a href="${pageContext.request.contextPath}/clients" class="btn btn-secondary">
                        ✗ Cancel
                    </a>
                </div>
            </form>

            <!-- Help Text -->
            <div class="mt-3">
                <p><small><span style="color: red;">*</span> Required fields</small></p>
            </div>
        </div>
    </div>

    <footer>
        <div class="container">
            <p>&copy; 2025 Banking Application - Lab 02</p>
        </div>
    </footer>
</body>
</html>