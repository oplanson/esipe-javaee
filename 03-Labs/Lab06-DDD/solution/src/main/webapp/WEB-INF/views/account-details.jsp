<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bank.model.Account" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Details - Banking Application</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        .transaction-form {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin: 20px 0;
        }
        .transaction-form h3 {
            margin-top: 0;
        }
        .balance-display {
            font-size: 2em;
            font-weight: bold;
            text-align: center;
            padding: 20px;
            margin: 20px 0;
            border-radius: 8px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        .account-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin: 20px 0;
        }
        .info-card {
            background: white;
            padding: 15px;
            border-radius: 8px;
            border: 1px solid #dee2e6;
        }
        .info-card h4 {
            margin: 0 0 10px 0;
            color: #6c757d;
            font-size: 0.9em;
            text-transform: uppercase;
        }
        .info-card p {
            margin: 0;
            font-size: 1.2em;
            font-weight: bold;
        }
    </style>
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
                Account account = (Account) request.getAttribute("account");
                Boolean deletionEnabled = (Boolean) request.getAttribute("deletionEnabled");
                Boolean transferEnabled = (Boolean) request.getAttribute("transferEnabled");
                String message = request.getParameter("message");
                String error = request.getParameter("error");
            %>

            <% if (account != null && account.getClient() != null) { %>
                <div class="breadcrumb">
                    <a href="<%= request.getContextPath() %>/clients">Clients</a> >
                    <a href="<%= request.getContextPath() %>/client?action=view&id=<%= account.getClient().getId() %>">
                        <%= account.getClient().getName() %>
                    </a> >
                    <span>Account <%= account.getNumber() %></span>
                </div>
            <% } %>

            <h2>💳 Account Details</h2>

            <% if (message != null) { %>
                <div class="message success">
                    <% if ("updated".equals(message)) { %>
                        ✓ Account updated successfully
                    <% } else if ("deposit_success".equals(message)) { %>
                        ✓ Deposit completed successfully
                    <% } else if ("withdraw_success".equals(message)) { %>
                        ✓ Withdrawal completed successfully
                    <% } else if ("transfer_success".equals(message)) { %>
                        ✓ Transfer completed successfully
                    <% } %>
                </div>
            <% } %>

            <% if (error != null) { %>
                <div class="message error">
                    <% if ("insufficient_funds".equals(error)) { %>
                        ✗ Insufficient funds for this transaction
                    <% } else if ("deposit_failed".equals(error)) { %>
                        ✗ Deposit failed
                    <% } else if ("transfer_failed".equals(error)) { %>
                        ✗ Transfer failed
                    <% } else { %>
                        ✗ Error: <%= error %>
                    <% } %>
                </div>
            <% } %>

            <% if (account != null) { %>
                <div class="balance-display">
                    <div>Current Balance</div>
                    <div>$<%= String.format("%.2f", account.getBalanceAsDouble()) %></div>
                </div>

                <div class="account-info">
                    <div class="info-card">
                        <h4>Account Number</h4>
                        <p><%= account.getNumber() %></p>
                    </div>
                    <div class="info-card">
                        <h4>Account Type</h4>
                        <p>
                            <span class="badge <%= "CHECKING".equals(account.getType()) ? "badge-primary" : "badge-success" %>">
                                <%= account.getType() %>
                            </span>
                        </p>
                    </div>
                    <div class="info-card">
                        <h4>Account ID</h4>
                        <p>#<%= account.getId() %></p>
                    </div>
                    <% if (account.getClient() != null) { %>
                        <div class="info-card">
                            <h4>Account Holder</h4>
                            <p>
                                <a href="<%= request.getContextPath() %>/client?action=view&id=<%= account.getClient().getId() %>">
                                    <%= account.getClient().getName() %>
                                </a>
                            </p>
                        </div>
                    <% } %>
                </div>

                <!-- Deposit Form -->
                <div class="transaction-form">
                    <h3>💰 Deposit Money</h3>
                    <form method="post" action="<%= request.getContextPath() %>/account" style="display: flex; gap: 10px; align-items: flex-end;">
                        <input type="hidden" name="action" value="deposit">
                        <input type="hidden" name="id" value="<%= account.getId() %>">
                        <div class="form-group" style="flex: 1; margin: 0;">
                            <label for="depositAmount">Amount:</label>
                            <input type="number" id="depositAmount" name="amount" step="0.01" min="0.01" required>
                        </div>
                        <button type="submit" class="button">Deposit</button>
                    </form>
                </div>

                <!-- Withdraw Form -->
                <div class="transaction-form">
                    <h3>💸 Withdraw Money</h3>
                    <form method="post" action="<%= request.getContextPath() %>/account" style="display: flex; gap: 10px; align-items: flex-end;">
                        <input type="hidden" name="action" value="withdraw">
                        <input type="hidden" name="id" value="<%= account.getId() %>">
                        <div class="form-group" style="flex: 1; margin: 0;">
                            <label for="withdrawAmount">Amount:</label>
                            <input type="number" id="withdrawAmount" name="amount" step="0.01" min="0.01" max="<%= account.getBalanceAsDouble() %>" required>
                        </div>
                        <button type="submit" class="button secondary">Withdraw</button>
                    </form>
                </div>

                <!-- Transfer Form -->
                <% if (transferEnabled != null && transferEnabled) { %>
                    <div class="transaction-form">
                        <h3>🔄 Transfer Money</h3>
                        <form method="post" action="<%= request.getContextPath() %>/account" style="display: flex; gap: 10px; align-items: flex-end; flex-wrap: wrap;">
                            <input type="hidden" name="action" value="transfer">
                            <input type="hidden" name="fromId" value="<%= account.getId() %>">
                            <div class="form-group" style="flex: 1; min-width: 200px; margin: 0;">
                                <label for="toId">To Account ID:</label>
                                <input type="number" id="toId" name="toId" required>
                            </div>
                            <div class="form-group" style="flex: 1; min-width: 150px; margin: 0;">
                                <label for="transferAmount">Amount:</label>
                                <input type="number" id="transferAmount" name="amount" step="0.01" min="0.01" max="<%= account.getBalanceAsDouble() %>" required>
                            </div>
                            <button type="submit" class="button">Transfer</button>
                        </form>
                    </div>
                <% } %>

                <div class="actions">
                    <a href="<%= request.getContextPath() %>/account?action=edit&id=<%= account.getId() %>" class="button secondary">
                        ✏️ Edit Account
                    </a>
                    <% if (account.getClient() != null) { %>
                        <a href="<%= request.getContextPath() %>/client?action=view&id=<%= account.getClient().getId() %>" class="button secondary">
                            ← Back to Client
                        </a>
                    <% } %>
                    <a href="<%= request.getContextPath() %>/accounts" class="button secondary">
                        📋 All Accounts
                    </a>
                    <% if (deletionEnabled != null && deletionEnabled) { %>
                        <form method="post" action="<%= request.getContextPath() %>/account" 
                              style="display: inline;"
                              onsubmit="return confirm('Are you sure you want to delete this account? This action cannot be undone.');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%= account.getId() %>">
                            <% if (account.getClient() != null) { %>
                                <input type="hidden" name="clientId" value="<%= account.getClient().getId() %>">
                            <% } %>
                            <button type="submit" class="button danger">🗑️ Delete Account</button>
                        </form>
                    <% } %>
                </div>
            <% } else { %>
                <div class="message error">
                    ✗ Account not found
                </div>
                <a href="<%= request.getContextPath() %>/accounts" class="button">← Back to Accounts</a>
            <% } %>
        </main>

        <footer>
            <p>© 2026 Banking Application - Lab 06: Domain-Driven Design</p>
        </footer>
    </div>
</body>
</html>