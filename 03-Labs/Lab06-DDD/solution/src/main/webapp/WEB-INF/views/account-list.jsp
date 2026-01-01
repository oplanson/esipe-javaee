<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bank.model.Account" %>
<%@ page import="com.bank.model.Client" %>
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
                Client client = (Client) request.getAttribute("client");
                List<Account> accounts = (List<Account>) request.getAttribute("accounts");
                Boolean deletionEnabled = (Boolean) request.getAttribute("deletionEnabled");
                String message = request.getParameter("message");
                String error = request.getParameter("error");
            %>

            <% if (client != null) { %>
                <div class="breadcrumb">
                    <a href="<%= request.getContextPath() %>/clients">Clients</a> >
                    <a href="<%= request.getContextPath() %>/client?action=view&id=<%= client.getId() %>"><%= client.getName() %></a> >
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
                    <a href="<%= request.getContextPath() %>/account?action=new&clientId=<%= client.getId() %>" class="button">
                        ➕ New Account
                    </a>
                    <a href="<%= request.getContextPath() %>/client?action=view&id=<%= client.getId() %>" class="button secondary">
                        ← Back to Client
                    </a>
                <% } else { %>
                    <a href="<%= request.getContextPath() %>/account?action=new" class="button">
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
                        <% for (Account account : accounts) { %>
                            <tr>
                                <td><%= account.getId() %></td>
                                <td><%= account.getNumber() %></td>
                                <td>
                                    <span class="badge <%= "CHECKING".equals(account.getType()) ? "badge-primary" : "badge-success" %>">
                                        <%= account.getType() %>
                                    </span>
                                </td>
                                <td class="<%= account.getBalanceAsDouble() < 0 ? "text-danger" : "text-success" %>">
                                    $<%= String.format("%.2f", account.getBalanceAsDouble()) %>
                                </td>
                                <% if (client == null && account.getClient() != null) { %>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/client?action=view&id=<%= account.getClient().getId() %>">
                                            <%= account.getClient().getName() %>
                                        </a>
                                    </td>
                                <% } %>
                                <td class="actions-cell">
                                    <a href="<%= request.getContextPath() %>/account?action=view&id=<%= account.getId() %>" 
                                       class="button small" title="View Details">👁️</a>
                                    <a href="<%= request.getContextPath() %>/account?action=edit&id=<%= account.getId() %>" 
                                       class="button small secondary" title="Edit">✏️</a>
                                    <% if (deletionEnabled != null && deletionEnabled) { %>
                                        <form method="post" action="<%= request.getContextPath() %>/account" 
                                              style="display: inline;"
                                              onsubmit="return confirm('Are you sure you want to delete this account?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="<%= account.getId() %>">
                                            <% if (client != null) { %>
                                                <input type="hidden" name="clientId" value="<%= client.getId() %>">
                                            <% } %>
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
                        <a href="<%= request.getContextPath() %>/account?action=new&clientId=<%= client.getId() %>" class="button">
                            Create First Account
                        </a>
                    <% } %>
                </div>
            <% } %>
        </main>

        <footer>
            <p>© 2026 Banking Application - Lab 06: Domain-Driven Design</p>
        </footer>
    </div>
</body>
</html>