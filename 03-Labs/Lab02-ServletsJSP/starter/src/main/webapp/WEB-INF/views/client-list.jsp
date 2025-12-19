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
    <div class="container">
        <header>
            <h1>Client Management</h1>
            <p class="subtitle">${appName}</p>
        </header>

        <!-- TODO: Add success message display -->
        <!-- Hint: Use c:if to check if param.message exists -->
        <!-- Display different messages based on param.message value (created, updated, deleted) -->
        

        <!-- TODO: Add search form (optional) -->
        <!-- <form method="get" action="${pageContext.request.contextPath}/clients" class="search-form">
            <input type="text" name="search" placeholder="Search by name..." />
            <button type="submit">Search</button>
        </form> -->

        <!-- Client Table -->
        <table class="table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Accounts</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <!-- TODO: Loop through clients using c:forEach -->
                <!-- Hint: <c:forEach var="client" items="${clients}"> -->
                
                <!-- TODO: For each client, create a table row with: -->
                <!-- - client.id -->
                <!-- - client.name (use c:out for safety) -->
                <!-- - client.email -->
                <!-- - client.accounts.size() -->
                <!-- - Action links: View, Edit, Delete -->
                
                <!-- Example structure:
                <tr>
                    <td>${client.id}</td>
                    <td><c:out value="${client.name}" /></td>
                    <td>TODO: email</td>
                    <td>TODO: account count</td>
                    <td>
                        <a href="TODO: view link">View</a>
                        <a href="TODO: edit link">Edit</a>
                        TODO: Delete link (only if deletionEnabled is true)
                    </td>
                </tr>
                -->
                
                <!-- TODO: Close c:forEach -->
                
                <!-- TODO: Add empty state if no clients -->
                <!-- Hint: Use c:if with empty condition -->
                
            </tbody>
        </table>

        <!-- Actions -->
        <div class="actions">
            <!-- TODO: Add "New Client" button -->
            <!-- Link to: ${pageContext.request.contextPath}/client?action=new -->
            
        </div>

        <footer>
            <p>
                Total Clients: ${clients.size()}
                <!-- TODO: Add more info if needed -->
            </p>
        </footer>
    </div>
</body>
</html>