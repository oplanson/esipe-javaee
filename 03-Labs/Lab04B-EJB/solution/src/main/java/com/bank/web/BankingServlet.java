package com.bank.web;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.ejb.AccountServiceBean;
import com.bank.ejb.ClientServiceBean;
import com.bank.ejb.ConfigServiceBean;
import com.bank.ejb.ReportGeneratorBean;
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
import java.math.BigDecimal;
import java.util.List;

/**
 * Servlet for testing EJB functionality.
 * Refactored to use JSP views for secure HTML output and XSS prevention.
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
        
        String action = request.getParameter("action");
        
        // Set configuration attributes
        request.setAttribute("appName", configService.getConfig("app.name"));
        request.setAttribute("appVersion", configService.getConfig("app.version"));
        request.setAttribute("reportStats", reportGenerator.getStatistics());
        
        // Handle actions
        if ("create".equals(action)) {
            handleCreateAccount(request);
        } else if ("deposit".equals(action)) {
            handleDeposit(request);
        } else if ("withdraw".equals(action)) {
            handleWithdraw(request);
        } else if ("transfer".equals(action)) {
            handleTransfer(request);
        } else if ("clients".equals(action)) {
            prepareClientsView(request);
            request.getRequestDispatcher("/WEB-INF/views/banking.jsp").forward(request, response);
            return;
        }
        
        // Prepare accounts view
        prepareAccountsView(request);
        
        // Forward to JSP
        request.getRequestDispatcher("/WEB-INF/views/banking.jsp").forward(request, response);
    }
    
    private void handleCreateAccount(HttpServletRequest request) {
        try {
            // First, ensure we have a default client or create one
            Client client = getOrCreateDefaultClient();
            
            // Create account with the client
            String accountNumber = "ACC-" + System.currentTimeMillis();
            Account account = accountService.createAccount(accountNumber, AccountType.CHECKING, client.getId());
            request.setAttribute("successMessage", "Account created: " + account.getAccountNumber() + " for client: " + client.getName());
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
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
    
    private void handleDeposit(HttpServletRequest request) {
        try {
            Long accountId = Long.parseLong(request.getParameter("accountId"));
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            accountService.deposit(accountId, amount);
            request.setAttribute("successMessage", "Deposit successful: $" + amount);
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
    }
    
    private void handleWithdraw(HttpServletRequest request) {
        try {
            Long accountId = Long.parseLong(request.getParameter("accountId"));
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            accountService.withdraw(accountId, amount);
            request.setAttribute("successMessage", "Withdrawal successful: $" + amount);
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
    }
    
    private void handleTransfer(HttpServletRequest request) {
        try {
            Long fromId = Long.parseLong(request.getParameter("fromId"));
            Long toId = Long.parseLong(request.getParameter("toId"));
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            accountService.transfer(fromId, toId, amount);
            request.setAttribute("successMessage", "Transfer successful: $" + amount);
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
    }
    
    private void prepareAccountsView(HttpServletRequest request) {
        List<Account> accounts = accountService.findAll();
        request.setAttribute("accounts", accounts);
        request.setAttribute("showAccounts", true);
        
        if (!accounts.isEmpty()) {
            // Calculate summary statistics
            BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            long activeAccounts = accounts.stream()
                .filter(a -> a.getStatus() == AccountStatus.ACTIVE)
                .count();
            
            request.setAttribute("totalBalance", totalBalance);
            request.setAttribute("activeAccountCount", activeAccounts);
        }
    }
    
    private void prepareClientsView(HttpServletRequest request) {
        List<Client> clients = clientService.getAllClients();
        request.setAttribute("clients", clients);
        request.setAttribute("showClients", true);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

// Made with Bob