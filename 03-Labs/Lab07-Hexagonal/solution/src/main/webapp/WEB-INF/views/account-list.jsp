<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bank.application.dto.AccountDTO" %>
<%@ page import="com.bank.application.dto.ClientDTO" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accounts - <%= request.getAttribute("appName") %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>🏦 <%= request.getAttribute("appName") %></h1>
            <nav>
                <a href="<%= request.getContextPath() %>/">Home</a>
                <a href="<%= request.getContextPath() %>/clients">Clients</a>
                <a href="<%= request.getContextPath() %>/accounts" class="active">Accounts</a>
            </nav>
        </header>

        <main>
            <%
                ClientDTO client = (ClientDTO) request.getAttribute("client");
                List<AccountDTO> accounts = (List<AccountDTO>) request.getAttribute("accounts");
                Boolean deletionEnabled = (Boolean) request.getAttribute("deletionEnabled");
                String message = request.getParameter("message");
                String error = request.getParameter("error");
            %>

            <% if (client != null) { %>
                <div class="breadcrumb">
                    <a href="<%= request.getContextPath() %>/clients">Clients</a> >
                    <a href="<%= request.getContextPath() %>/clients/<%= client.getId() %>"><%= client.getName() %></a> >
                    <span>Accounts</span>
                </div>
                <h2>Accounts for <%= client.getName() %></h2>
            <% } else { %>
                <h2>All Accounts</h2>
            <% } %>

            <% if (message != null) { %>
                <div class="message success">
                    <% if ("deleted".equals(message)) { %>
                        ✓ Account deleted successfully
                    <% } else if ("account_created".equals(message)) { %>
                        ✓ Account created successfully
                    <% } %>
                </div>
            <% } %>

            <% if (error != null) { %>
                <div class="message error">
                    ✗ Error: <%= error %>
                </div>
            <% } %>

            <div class="actions">
                <% if (client != null) { %>
                    <a href="<%= request.getContextPath() %>/accounts/new?clientId=<%= client.getId() %>" class="button">
                        ➕ New Account
                    </a>
                    <a href="<%= request.getContextPath() %>/clients/<%= client.getId() %>" class="button secondary">
                        ← Back to Client
                    </a>
                <% } else { %>
                    <a href="<%= request.getContextPath() %>/accounts/new" class="button">
                        ➕ New Account
                    </a>
                <% } %>
            </div>

            <% if (accounts != null && !accounts.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Account Number</th>
                            <th>Type</th>
                            <th>Balance</th>
                            <% if (client == null) { %>
                                <th>Client</th>
                            <% } %>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (AccountDTO account : accounts) { %>
                            <tr>
                                <td><%= account.getId() %></td>
                                <td><%= account.getAccountNumber() %></td>
                                <td>
                                    <span class="badge <%= "CHECKING".equals(account.getAccountType()) ? "badge-primary" : "badge-success" %>">
                                        <%= account.getAccountType() %>
                                    </span>
                                </td>
                                <td class="<%= account.getBalanceAsDouble() < 0 ? "text-danger" : "text-success" %>">
                                    $<%= String.format("%.2f", account.getBalanceAsDouble()) %>
                                </td>
                                <% if (client == null && account.getClient() != null) { %>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/clients/<%= account.getClient().getId() %>">
                                            <%= account.getClient().getName() %>
                                        </a>
                                    </td>
                                <% } %>
                                <td class="actions-cell">
                                    <a href="<%= request.getContextPath() %>/accounts/<%= account.getId() %>" 
                                       class="button small" title="View Details">👁️</a>
                                    <a href="<%= request.getContextPath() %>/accounts/<%= account.getId() %>" 
                                       class="button small secondary" title="Edit">✏️</a>
                                    <% if (deletionEnabled != null && deletionEnabled) { %>
                                        <form method="post" action="<%= request.getContextPath() %>/accounts/<%= account.getId() %>/close"
                                              style="display: inline;"
                                              onsubmit="return confirm('Are you sure you want to close this account?');">
                                            <button type="submit" class="button small danger" title="Delete">🗑️</button>
                                        </form>
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>

                <div class="summary">
                    <p><strong>Total Accounts:</strong> <%= accounts.size() %></p>
                    <%
                        double totalBalance = accounts.stream()
                            .mapToDouble(Account::getBalanceAsDouble)
                            .sum();
                    %>
                    <p><strong>Total Balance:</strong> 
                        <span class="<%= totalBalance < 0 ? "text-danger" : "text-success" %>">
                            $<%= String.format("%.2f", totalBalance) %>
                        </span>
                    </p>
                </div>
            <% } else { %>
                <div class="empty-state">
                    <p>📭 No accounts found.</p>
                    <% if (client != null) { %>
                        <a href="<%= request.getContextPath() %>/accounts/new?clientId=<%= client.getId() %>" class="button">
                            Create First Account
                        </a>
                    <% } %>
                </div>
            <% } %>
        </main>

        <footer>
            <p>© 2026 Banking Application - Lab 07: Hexagonal Architecture</p>
        </footer>
    </div>
</body>
</html>