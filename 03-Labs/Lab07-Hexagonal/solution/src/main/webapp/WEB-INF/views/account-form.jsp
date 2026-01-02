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
    <title><%= request.getAttribute("account") == null ? "New Account" : "Edit Account" %> - Banking Application</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>🏦 Banking Application</h1>
            <nav>
                <a href="<%= request.getContextPath() %>/">Home</a>
                <a href="<%= request.getContextPath() %>/clients">Clients</a>
                <a href="<%= request.getContextPath() %>/accounts" class="active">Accounts</a>
            </nav>
        </header>

        <main>
            <%
                AccountDTO account = (AccountDTO) request.getAttribute("account");
                ClientDTO client = (ClientDTO) request.getAttribute("client");
                List<ClientDTO> clients = (List<ClientDTO>) request.getAttribute("clients");
                String error = request.getParameter("error");
                boolean isEdit = account != null && account.getId() != null;
            %>

            <% if (client != null) { %>
                <div class="breadcrumb">
                    <a href="<%= request.getContextPath() %>/clients">Clients</a> >
                    <a href="<%= request.getContextPath() %>/clients/<%= client.getId() %>"><%= client.getName() %></a> >
                    <span><%= isEdit ? "Edit Account" : "New Account" %></span>
                </div>
            <% } %>

            <h2><%= isEdit ? "✏️ Edit Account" : "➕ New Account" %></h2>

            <% if (error != null) { %>
                <div class="message error">
                    <% if ("client_required".equals(error)) { %>
                        ✗ Please select a client
                    <% } else if ("number_required".equals(error)) { %>
                        ✗ Account number is required
                    <% } else if ("type_required".equals(error)) { %>
                        ✗ Account type is required
                    <% } else { %>
                        ✗ Error: <%= error %>
                    <% } %>
                </div>
            <% } %>

            <form method="post" action="<%= request.getContextPath() %><%= isEdit ? "/accounts/" + account.getId() : "/accounts" %>" class="form">

                <div class="form-group">
                    <label for="clientId">Client: <span class="required">*</span></label>
                    <% if (isEdit && account.getClient() != null) { %>
                        <input type="text" value="<%= account.getClient().getName() %>" disabled>
                        <input type="hidden" name="clientId" value="<%= account.getClient().getId() %>">
                        <small>Client cannot be changed after account creation</small>
                    <% } else if (client != null) { %>
                        <input type="text" value="<%= client.getName() %>" disabled>
                        <input type="hidden" name="clientId" value="<%= client.getId() %>">
                    <% } else { %>
                        <select id="clientId" name="clientId" required>
                            <option value="">-- Select a client --</option>
                            <% if (clients != null) {
                                for (ClientDTO c : clients) { %>
                                    <option value="<%= c.getId() %>"><%= c.getName() %> (<%= c.getEmail() %>)</option>
                            <%  }
                            } %>
                        </select>
                    <% } %>
                </div>

                <div class="form-group">
                    <label for="accountType">Account Type: <span class="required">*</span></label>
                    <select id="accountType" name="accountType" required>
                        <option value="">-- Select type --</option>
                        <option value="CHECKING" <%= account != null && "CHECKING".equals(account.getAccountType()) ? "selected" : "" %>>
                            Checking Account
                        </option>
                        <option value="SAVINGS" <%= account != null && "SAVINGS".equals(account.getAccountType()) ? "selected" : "" %>>
                            Savings Account
                        </option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="initialBalance">Initial Balance:</label>
                    <input type="number"
                           id="initialBalance"
                           name="initialBalance"
                           value="<%= account != null ? account.getBalance() : 0.0 %>"
                           step="0.01"
                           min="0">
                    <small>Starting balance for the account (default: €0.00)</small>
                </div>

                <div class="form-group">
                    <label for="currency">Currency:</label>
                    <input type="text"
                           id="currency"
                           name="currency"
                           value="EUR"
                           readonly>
                    <small>Currency is fixed to EUR</small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="button">
                        <%= isEdit ? "💾 Update Account" : "➕ Create Account" %>
                    </button>
                    <% if (isEdit && account.getClient() != null) { %>
                        <a href="<%= request.getContextPath() %>/clients/<%= account.getClient().getId() %>" 
                           class="button secondary">Cancel</a>
                    <% } else if (client != null) { %>
                        <a href="<%= request.getContextPath() %>/clients/<%= client.getId() %>" 
                           class="button secondary">Cancel</a>
                    <% } else { %>
                        <a href="<%= request.getContextPath() %>/accounts" class="button secondary">Cancel</a>
                    <% } %>
                </div>
            </form>

            <% if (isEdit) { %>
                <div class="info-box">
                    <h3>ℹ️ Account Information</h3>
                    <p><strong>Account ID:</strong> <%= account.getId() %></p>
                    <p><strong>Current Balance:</strong> $<%= String.format("%.2f", account.getBalance()) %></p>
                    <p><strong>Client:</strong> <%= account.getClient() != null ? account.getClient().getName() : "N/A" %></p>
                </div>
            <% } %>
        </main>

        <footer>
            <p>© 2026 Banking Application - Lab 07: Hexagonal Architecture</p>
        </footer>
    </div>
</body>
</html>