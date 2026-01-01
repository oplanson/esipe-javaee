package com.bank.web;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Account;
import com.bank.model.Client;
import com.bank.service.AccountService;
import com.bank.service.ClientService;
import com.bank.config.Logged;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller servlet for managing accounts.
 * Handles all account-related HTTP requests.
 * 
 * Lab 4 - CDI Features:
 * - @Inject for dependency injection
 * - @ConfigProperty for configuration
 * - @Logged custom interceptor
 * - Clean separation of concerns
 */
@WebServlet(
    name = "AccountController",
    urlPatterns = {"/accounts", "/account"},
    loadOnStartup = 2
)
public class AccountController extends HttpServlet {
    
    /**
     * AccountService injected by CDI.
     */
    @Inject
    private AccountService accountService;
    
    /**
     * ClientService injected by CDI.
     */
    @Inject
    private ClientService clientService;
    
    /**
     * Logger injected by CDI.
     */
    @Inject
    private Logger logger;
    
    /**
     * Configuration properties injected by MicroProfile Config.
     */
    @Inject
    @ConfigProperty(name = "feature.account.deletion.enabled", defaultValue = "true")
    private Boolean deletionEnabled;
    
    @Inject
    @ConfigProperty(name = "feature.account.transfer.enabled", defaultValue = "true")
    private Boolean transferEnabled;
    
    @Inject
    @ConfigProperty(name = "app.name", defaultValue = "Banking Application")
    private String appName;
    
    /**
     * Initialize the servlet.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        
        logger.info("AccountController initialized with CDI");
        logger.info("App name: " + appName);
        logger.info("Deletion enabled: " + deletionEnabled);
        logger.info("Transfer enabled: " + transferEnabled);
    }
    
    /**
     * Handle GET requests.
     */
    @Override
    @Logged
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        String path = req.getServletPath();
        
        try {
            if ("/accounts".equals(path)) {
                listAccounts(req, resp);
            } else if ("/account".equals(path)) {
                if (action == null || "list".equals(action)) {
                    resp.sendRedirect(req.getContextPath() + "/accounts");
                } else if ("view".equals(action)) {
                    viewAccount(req, resp);
                } else if ("new".equals(action)) {
                    showForm(req, resp, null);
                } else if ("edit".equals(action)) {
                    editAccount(req, resp);
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.severe("Error in doGet: " + e.getMessage());
            req.setAttribute("error", "An error occurred: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }
    
    /**
     * Handle POST requests.
     */
    @Override
    @Logged
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                createAccount(req, resp);
            } else if ("update".equals(action)) {
                updateAccount(req, resp);
            } else if ("delete".equals(action)) {
                deleteAccount(req, resp);
            } else if ("deposit".equals(action)) {
                deposit(req, resp);
            } else if ("withdraw".equals(action)) {
                withdraw(req, resp);
            } else if ("transfer".equals(action)) {
                transfer(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
            }
        } catch (Exception e) {
            logger.severe("Error in doPost: " + e.getMessage());
            req.setAttribute("error", "An error occurred: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }
    
    /**
     * Display list of all accounts.
     */
    private void listAccounts(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String clientIdParam = req.getParameter("clientId");
        List<Account> accounts;
        Client client = null;
        
        if (clientIdParam != null && !clientIdParam.trim().isEmpty()) {
            try {
                Long clientId = Long.parseLong(clientIdParam);
                client = clientService.findById(clientId);
                accounts = accountService.findByClient(clientId);
                logger.info("Listing accounts for client: " + clientId + " - Found: " + accounts.size());
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid client ID");
                return;
            }
        } else {
            accounts = accountService.findAll();
            logger.info("Listing all accounts - Total: " + accounts.size());
        }
        
        req.setAttribute("accounts", accounts);
        req.setAttribute("client", client);
        req.setAttribute("appName", appName);
        req.setAttribute("deletionEnabled", deletionEnabled);
        req.setAttribute("transferEnabled", transferEnabled);
        
        req.getRequestDispatcher("/WEB-INF/views/account-list.jsp").forward(req, resp);
    }
    
    /**
     * Display details of a specific account.
     */
    private void viewAccount(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String idParam = req.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account ID is required");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            Account account = accountService.findById(id);
            
            if (account == null) {
                logger.warning("Account not found: " + id);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Account not found");
                return;
            }
            
            logger.info("Viewing account: " + id);
            req.setAttribute("account", account);
            req.setAttribute("deletionEnabled", deletionEnabled);
            req.setAttribute("transferEnabled", transferEnabled);
            req.getRequestDispatcher("/WEB-INF/views/account-details.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid account ID");
        }
    }
    
    /**
     * Show form for editing an account.
     */
    private void editAccount(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String idParam = req.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account ID is required");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            Account account = accountService.findById(id);
            
            if (account == null) {
                logger.warning("Account not found for edit: " + id);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Account not found");
                return;
            }
            
            showForm(req, resp, account);
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid account ID");
        }
    }
    
    /**
     * Show account form (for create or edit).
     */
    private void showForm(HttpServletRequest req, HttpServletResponse resp, Account account)
            throws ServletException, IOException {
        
        // Get client ID if provided
        String clientIdParam = req.getParameter("clientId");
        Client client = null;
        
        if (clientIdParam != null && !clientIdParam.trim().isEmpty()) {
            try {
                Long clientId = Long.parseLong(clientIdParam);
                client = clientService.findById(clientId);
            } catch (NumberFormatException e) {
                // Ignore invalid client ID
            }
        } else if (account != null && account.getClient() != null) {
            client = account.getClient();
        }
        
        // Get all clients for dropdown
        List<Client> clients = clientService.findAll();
        
        req.setAttribute("account", account);
        req.setAttribute("client", client);
        req.setAttribute("clients", clients);
        req.getRequestDispatcher("/WEB-INF/views/account-form.jsp").forward(req, resp);
    }
    
    /**
     * Create a new account.
     */
    private void createAccount(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        String clientIdParam = req.getParameter("clientId");
        String number = req.getParameter("number");
        String balanceParam = req.getParameter("balance");
        String type = req.getParameter("type");
        
        // Validate input
        if (clientIdParam == null || clientIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/account?action=new&error=client_required");
            return;
        }
        
        if (number == null || number.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/account?action=new&clientId=" + clientIdParam + "&error=number_required");
            return;
        }
        
        if (type == null || type.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/account?action=new&clientId=" + clientIdParam + "&error=type_required");
            return;
        }
        
        try {
            Long clientId = Long.parseLong(clientIdParam);
            double balance = balanceParam != null && !balanceParam.trim().isEmpty() 
                ? Double.parseDouble(balanceParam) : 0.0;
            
            Account account = new Account(number.trim(), balance, type.trim());
            accountService.create(account, clientId);
            
            logger.info("Created new account: " + account.getId() + " for client: " + clientId);
            
            // Redirect to client details
            resp.sendRedirect(req.getContextPath() + "/client?action=view&id=" + clientId + "&message=account_created");
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid client ID or balance");
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/account?action=new&clientId=" + clientIdParam + "&error=" + e.getMessage());
        }
    }
    
    /**
     * Update an existing account.
     */
    private void updateAccount(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        String idParam = req.getParameter("id");
        String number = req.getParameter("number");
        String balanceParam = req.getParameter("balance");
        String type = req.getParameter("type");
        
        // Validate input
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account ID is required");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            Account account = accountService.findById(id);
            
            if (account == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Account not found");
                return;
            }
            
            // Update fields
            if (number != null && !number.trim().isEmpty()) {
                account.setNumber(number.trim());
            }
            if (balanceParam != null && !balanceParam.trim().isEmpty()) {
                account.setBalance(Double.parseDouble(balanceParam));
            }
            if (type != null && !type.trim().isEmpty()) {
                account.setType(type.trim());
            }
            
            accountService.update(account);
            
            logger.info("Updated account: " + id);
            
            resp.sendRedirect(req.getContextPath() + "/account?action=view&id=" + id + "&message=updated");
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid account ID or balance");
        }
    }
    
    /**
     * Delete an account.
     */
    private void deleteAccount(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        if (!deletionEnabled) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Account deletion is disabled");
            return;
        }
        
        String idParam = req.getParameter("id");
        String clientIdParam = req.getParameter("clientId");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account ID is required");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            
            boolean deleted = accountService.delete(id);
            
            if (deleted) {
                logger.info("Deleted account: " + id);
                
                // Redirect to client details if clientId provided
                if (clientIdParam != null && !clientIdParam.trim().isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/client?action=view&id=" + clientIdParam + "&message=account_deleted");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/accounts?message=deleted");
                }
            } else {
                logger.warning("Account not found for deletion: " + id);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Account not found");
            }
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid account ID");
        }
    }
    
    /**
     * Deposit money into an account.
     */
    private void deposit(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        String idParam = req.getParameter("id");
        String amountParam = req.getParameter("amount");
        
        if (idParam == null || amountParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account ID and amount are required");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            double amount = Double.parseDouble(amountParam);
            
            boolean success = accountService.deposit(id, amount);
            
            if (success) {
                logger.info("Deposited " + amount + " to account: " + id);
                resp.sendRedirect(req.getContextPath() + "/account?action=view&id=" + id + "&message=deposit_success");
            } else {
                resp.sendRedirect(req.getContextPath() + "/account?action=view&id=" + id + "&error=deposit_failed");
            }
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid account ID or amount");
        }
    }
    
    /**
     * Withdraw money from an account.
     */
    private void withdraw(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        String idParam = req.getParameter("id");
        String amountParam = req.getParameter("amount");
        
        if (idParam == null || amountParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account ID and amount are required");
            return;
        }
        
        try {
            Long id = Long.parseLong(idParam);
            double amount = Double.parseDouble(amountParam);
            
            boolean success = accountService.withdraw(id, amount);
            
            if (success) {
                logger.info("Withdrew " + amount + " from account: " + id);
                resp.sendRedirect(req.getContextPath() + "/account?action=view&id=" + id + "&message=withdraw_success");
            } else {
                resp.sendRedirect(req.getContextPath() + "/account?action=view&id=" + id + "&error=insufficient_funds");
            }
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid account ID or amount");
        }
    }
    
    /**
     * Transfer money between accounts.
     */
    private void transfer(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        if (!transferEnabled) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Account transfer is disabled");
            return;
        }
        
        String fromIdParam = req.getParameter("fromId");
        String toIdParam = req.getParameter("toId");
        String amountParam = req.getParameter("amount");
        
        if (fromIdParam == null || toIdParam == null || amountParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "From account, to account, and amount are required");
            return;
        }
        
        try {
            Long fromId = Long.parseLong(fromIdParam);
            Long toId = Long.parseLong(toIdParam);
            double amount = Double.parseDouble(amountParam);
            
            boolean success = accountService.transfer(fromId, toId, amount);
            
            if (success) {
                logger.info("Transferred " + amount + " from account " + fromId + " to " + toId);
                resp.sendRedirect(req.getContextPath() + "/account?action=view&id=" + fromId + "&message=transfer_success");
            } else {
                resp.sendRedirect(req.getContextPath() + "/account?action=view&id=" + fromId + "&error=transfer_failed");
            }
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid account ID or amount");
        }
    }
    
    @Override
    public void destroy() {
        logger.info("AccountController destroyed");
        super.destroy();
    }
}

// Made with Bob
