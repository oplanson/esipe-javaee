package com.bank.web;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.ejb.AccountServiceBean;
import com.bank.ejb.ClientServiceBean;
import com.bank.ejb.ConfigServiceBean;
import com.bank.ejb.ReportGeneratorBean;
import com.bank.ejb.TransactionBatchBean;
import com.bank.model.Account;
import com.bank.model.AccountStatus;
import com.bank.model.AccountType;
import com.bank.model.Client;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servlet for testing EJB functionality.
 */
@WebServlet("/banking")
public class BankingServlet extends HttpServlet {

    @EJB
    private AccountServiceBean accountService;
    
    @EJB
    private ClientServiceBean clientService;
    
    @EJB
    private ConfigServiceBean configService;
    
    @EJB
    private ReportGeneratorBean reportGenerator;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        String action = request.getParameter("action");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Lab 04B - EJB Banking</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }");
            out.println("h1 { color: #2c3e50; }");
            out.println("h2 { color: #34495e; margin-top: 30px; }");
            out.println("h3 { color: #7f8c8d; margin-top: 20px; }");
            out.println(".success { color: green; padding: 10px; background: #d4edda; border: 1px solid #c3e6cb; margin: 10px 0; border-radius: 4px; }");
            out.println(".error { color: #721c24; padding: 10px; background: #f8d7da; border: 1px solid #f5c6cb; margin: 10px 0; border-radius: 4px; }");
            out.println(".info { color: #004085; padding: 10px; background: #cce5ff; border: 1px solid #b8daff; margin: 10px 0; border-radius: 4px; }");
            out.println("table { border-collapse: collapse; width: 100%; margin: 20px 0; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
            out.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
            out.println("th { background-color: #3498db; color: white; font-weight: bold; }");
            out.println("tr:nth-child(even) { background-color: #f9f9f9; }");
            out.println("tr:hover { background-color: #e8f4f8; }");
            out.println(".status-active { color: #27ae60; font-weight: bold; }");
            out.println(".status-inactive { color: #e74c3c; font-weight: bold; }");
            out.println(".status-suspended { color: #f39c12; font-weight: bold; }");
            out.println(".button { display: inline-block; padding: 10px 20px; margin: 5px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; transition: background 0.3s; }");
            out.println(".button:hover { background: #2980b9; }");
            out.println(".actions { margin: 20px 0; }");
            out.println(".stats { background: white; padding: 20px; margin: 20px 0; border-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
            out.println(".stats p { margin: 10px 0; font-size: 16px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            out.println("<h1>🏦 Lab 04B - Enterprise Java Beans Banking</h1>");
            
            // Display configuration
            out.println("<div class='info'>");
            out.println("<strong>Application:</strong> " + configService.getConfig("app.name") + "<br>");
            out.println("<strong>Version:</strong> " + configService.getConfig("app.version") + "<br>");
            out.println("<strong>Report Statistics:</strong> " + reportGenerator.getStatistics());
            out.println("</div>");
            
            // Handle actions
            if ("create".equals(action)) {
                handleCreateAccount(out);
            } else if ("deposit".equals(action)) {
                handleDeposit(request, out);
            } else if ("withdraw".equals(action)) {
                handleWithdraw(request, out);
            } else if ("transfer".equals(action)) {
                handleTransfer(request, out);
            } else if ("clients".equals(action)) {
                displayClients(out);
                displayActions(out);
                out.println("</body>");
                out.println("</html>");
                return;
            }
            
            // Display all accounts
            displayAccounts(out);
            
            // Display action buttons
            displayActions(out);
            
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    private void handleCreateAccount(PrintWriter out) {
        try {
            // First, ensure we have a default client or create one
            Client client = getOrCreateDefaultClient();
            
            // Create account with the client
            String accountNumber = "ACC-" + System.currentTimeMillis();
            Account account = accountService.createAccount(accountNumber, AccountType.CHECKING, client.getId());
            out.println("<div class='success'>✓ Account created: " + account.getAccountNumber() + " for client: " + client.getName() + "</div>");
        } catch (Exception e) {
            out.println("<div class='error'>✗ Error: " + e.getMessage() + "</div>");
        }
    }
    
    /**
     * Get or create a default client for testing purposes.
     * In a real application, clients would be created separately.
     */
    private Client getOrCreateDefaultClient() {
        List<Client> clients = clientService.getAllClients();
        if (clients.isEmpty()) {
            // Create a default test client
            return clientService.createClient("John Doe", "john.doe@example.com");
        }
        return clients.get(0);
    }
    
    private void handleDeposit(HttpServletRequest request, PrintWriter out) {
        try {
            Long accountId = Long.parseLong(request.getParameter("accountId"));
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            accountService.deposit(accountId, amount);
            out.println("<div class='success'>✓ Deposit successful: $" + amount + "</div>");
        } catch (Exception e) {
            out.println("<div class='error'>✗ Error: " + e.getMessage() + "</div>");
        }
    }
    
    private void handleWithdraw(HttpServletRequest request, PrintWriter out) {
        try {
            Long accountId = Long.parseLong(request.getParameter("accountId"));
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            accountService.withdraw(accountId, amount);
            out.println("<div class='success'>✓ Withdrawal successful: $" + amount + "</div>");
        } catch (Exception e) {
            out.println("<div class='error'>✗ Error: " + e.getMessage() + "</div>");
        }
    }
    
    private void handleTransfer(HttpServletRequest request, PrintWriter out) {
        try {
            Long fromId = Long.parseLong(request.getParameter("fromId"));
            Long toId = Long.parseLong(request.getParameter("toId"));
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            accountService.transfer(fromId, toId, amount);
            out.println("<div class='success'>✓ Transfer successful: $" + amount + "</div>");
        } catch (Exception e) {
            out.println("<div class='error'>✗ Error: " + e.getMessage() + "</div>");
        }
    }
    
    private void displayAccounts(PrintWriter out) {
        out.println("<h2>📊 All Accounts</h2>");
        
        List<Account> accounts = accountService.findAll();
        
        if (accounts.isEmpty()) {
            out.println("<p>No accounts found. Create one to get started!</p>");
        } else {
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>ID</th>");
            out.println("<th>Account Number</th>");
            out.println("<th>Client</th>");
            out.println("<th>Email</th>");
            out.println("<th>Premium</th>");
            out.println("<th>Type</th>");
            out.println("<th>Balance</th>");
            out.println("<th>Status</th>");
            out.println("<th>Created</th>");
            out.println("</tr>");
            
            for (Account account : accounts) {
                Client client = account.getClient();
                out.println("<tr>");
                out.println("<td>" + account.getId() + "</td>");
                out.println("<td>" + account.getAccountNumber() + "</td>");
                out.println("<td>" + (client != null ? client.getName() : "N/A") + "</td>");
                out.println("<td>" + (client != null ? client.getEmail() : "N/A") + "</td>");
                out.println("<td>" + (client != null && client.isPremium() ? "⭐ Yes" : "No") + "</td>");
                out.println("<td>" + account.getType() + "</td>");
                out.println("<td>$" + String.format("%.2f", account.getBalance()) + "</td>");
                out.println("<td><span class='status-" + account.getStatus().toString().toLowerCase() + "'>" + account.getStatus() + "</span></td>");
                out.println("<td>" + (account.getCreatedAt() != null ? account.getCreatedAt().toLocalDate() : "N/A") + "</td>");
                out.println("</tr>");
            }
            
            out.println("</table>");
            
            // Display summary statistics
            out.println("<div class='stats'>");
            out.println("<h3>📈 Summary</h3>");
            out.println("<p>Total Accounts: " + accounts.size() + "</p>");
            BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            out.println("<p>Total Balance: $" + String.format("%.2f", totalBalance) + "</p>");
            long activeAccounts = accounts.stream()
                .filter(a -> a.getStatus() == AccountStatus.ACTIVE)
                .count();
            out.println("<p>Active Accounts: " + activeAccounts + "</p>");
        }
    }
    
    private void displayClients(PrintWriter out) {
        out.println("<h2>👥 All Clients</h2>");
        
        List<Client> clients = clientService.getAllClients();
        
        if (clients.isEmpty()) {
            out.println("<p>No clients found.</p>");
        } else {
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>ID</th>");
            out.println("<th>Name</th>");
            out.println("<th>Email</th>");
            out.println("<th>Phone</th>");
            out.println("<th>Premium</th>");
            out.println("<th>Accounts</th>");
            out.println("<th>Created</th>");
            out.println("</tr>");
            
            for (Client client : clients) {
                out.println("<tr>");
                out.println("<td>" + client.getId() + "</td>");
                out.println("<td>" + client.getName() + "</td>");
                out.println("<td>" + client.getEmail() + "</td>");
                out.println("<td>" + (client.getPhone() != null ? client.getPhone() : "N/A") + "</td>");
                out.println("<td>" + (client.isPremium() ? "⭐ Yes" : "No") + "</td>");
                out.println("<td>" + client.getAccounts().size() + "</td>");
                out.println("<td>" + (client.getCreatedAt() != null ? client.getCreatedAt().toLocalDate() : "N/A") + "</td>");
                out.println("</tr>");
            }
            
            out.println("</table>");
        }
    }
    
    private void displayActions(PrintWriter out) {
        out.println("<h2>🎯 Actions</h2>");
        out.println("<div class='actions'>");
        out.println("<a href='banking?action=create' class='button'>➕ Create Account</a>");
        out.println("<a href='banking?action=clients' class='button'>👥 View Clients</a>");
        out.println("<a href='banking' class='button'>🔄 Refresh</a>");
        out.println("<a href='/' class='button'>🏠 Back to Home</a>");
        out.println("</div>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

// Made with Bob