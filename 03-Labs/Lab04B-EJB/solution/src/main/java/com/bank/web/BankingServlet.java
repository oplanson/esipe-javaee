package com.bank.web;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.ejb.AccountServiceBean;
import com.bank.ejb.ConfigServiceBean;
import com.bank.ejb.ReportGeneratorBean;
import com.bank.ejb.TransactionBatchBean;
import com.bank.model.Account;
import com.bank.model.AccountType;
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
            out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println("h1 { color: #2c3e50; }");
            out.println("h2 { color: #34495e; margin-top: 30px; }");
            out.println(".success { color: green; padding: 10px; background: #d4edda; border: 1px solid #c3e6cb; margin: 10px 0; }");
            out.println(".info { color: #004085; padding: 10px; background: #cce5ff; border: 1px solid #b8daff; margin: 10px 0; }");
            out.println("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
            out.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
            out.println("th { background-color: #3498db; color: white; }");
            out.println("tr:nth-child(even) { background-color: #f2f2f2; }");
            out.println(".button { display: inline-block; padding: 10px 20px; margin: 5px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; }");
            out.println(".button:hover { background: #2980b9; }");
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
            Account account = new Account("ACC-" + System.currentTimeMillis(), AccountType.CHECKING);
            account = accountService.createAccount(account);
            out.println("<div class='success'>✓ Account created: " + account.getAccountNumber() + "</div>");
        } catch (Exception e) {
            out.println("<div class='error'>✗ Error: " + e.getMessage() + "</div>");
        }
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
            out.println("<tr><th>ID</th><th>Account Number</th><th>Type</th><th>Balance</th><th>Status</th></tr>");
            
            for (Account account : accounts) {
                out.println("<tr>");
                out.println("<td>" + account.getId() + "</td>");
                out.println("<td>" + account.getAccountNumber() + "</td>");
                out.println("<td>" + account.getType() + "</td>");
                out.println("<td>$" + account.getBalance() + "</td>");
                out.println("<td>" + account.getStatus() + "</td>");
                out.println("</tr>");
            }
            
            out.println("</table>");
        }
    }
    
    private void displayActions(PrintWriter out) {
        out.println("<h2>🎯 Actions</h2>");
        out.println("<a href='banking?action=create' class='button'>Create Account</a>");
        out.println("<a href='/' class='button'>Back to Home</a>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

// Made with Bob