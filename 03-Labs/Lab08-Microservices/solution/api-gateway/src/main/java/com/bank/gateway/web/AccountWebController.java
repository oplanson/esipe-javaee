// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.web;

import com.bank.gateway.client.AccountServiceClient;
import com.bank.gateway.client.ClientServiceClient;
import com.bank.gateway.dto.AccountDTO;
import com.bank.gateway.dto.ClientDTO;
import com.bank.gateway.dto.TransactionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

/**
 * Web Controller for Account operations
 * Handles JSP-based web interface requests
 * Uses RestClientBuilder for programmatic REST client creation
 * Configured in web.xml
 */
public class AccountWebController extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(AccountWebController.class.getName());
    
    private AccountServiceClient accountServiceClient;
    private ClientServiceClient clientServiceClient;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Get service URLs from MicroProfile Config
        String accountServiceUrl = ConfigProvider.getConfig()
            .getValue("account.service.url", String.class);
        String clientServiceUrl = ConfigProvider.getConfig()
            .getValue("client.service.url", String.class);
        
        // Build REST clients programmatically
        accountServiceClient = RestClientBuilder.newBuilder()
            .baseUri(URI.create(accountServiceUrl))
            .build(AccountServiceClient.class);
            
        clientServiceClient = RestClientBuilder.newBuilder()
            .baseUri(URI.create(clientServiceUrl))
            .build(ClientServiceClient.class);
        
        LOGGER.info("AccountWebController initialized with account service URL: " + accountServiceUrl);
    }
    
    private AccountServiceClient getAccountServiceClient() {
        return accountServiceClient;
    }
    
    private ClientServiceClient getClientServiceClient() {
        return clientServiceClient;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        try {
            switch (path) {
                case "/accounts":
                    listAccounts(request, response);
                    break;
                    
                case "/accounts/new":
                    showNewForm(request, response);
                    break;
                    
                case "/accounts/view":
                    viewAccount(request, response);
                    break;
                    
                case "/accounts/deposit":
                    showDepositForm(request, response);
                    break;
                    
                case "/accounts/withdraw":
                    showWithdrawForm(request, response);
                    break;
                    
                case "/accounts/transfer":
                    showTransferForm(request, response);
                    break;
                    
                case "/accounts/suspend":
                    suspendAccount(request, response);
                    break;
                    
                case "/accounts/activate":
                    activateAccount(request, response);
                    break;
                    
                case "/accounts/close":
                    closeAccount(request, response);
                    break;
                    
                case "/accounts/delete":
                    deleteAccount(request, response);
                    break;
                    
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.severe("Error in AccountWebController: " + e.getMessage());
            request.setAttribute("error", "Service temporarily unavailable: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        try {
            switch (path) {
                case "/accounts/new":
                    createAccount(request, response);
                    break;
                    
                case "/accounts/deposit":
                    processDeposit(request, response);
                    break;
                    
                case "/accounts/withdraw":
                    processWithdraw(request, response);
                    break;
                    
                case "/accounts/transfer":
                    processTransfer(request, response);
                    break;
                    
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.severe("Error in AccountWebController POST: " + e.getMessage());
            request.setAttribute("error", "Failed to process request: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    private void listAccounts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String clientIdParam = request.getParameter("clientId");
        
        if (clientIdParam != null) {
            // List accounts for specific client
            Long clientId = Long.parseLong(clientIdParam);
            LOGGER.info("Listing accounts for client: " + clientId);
            
            ClientDTO client = getClientServiceClient().getClientById(clientId);
            List<AccountDTO> accounts = getAccountServiceClient().getAccountsByClientId(clientId);
            
            request.setAttribute("client", client);
            request.setAttribute("accounts", accounts);
        } else {
            // List all accounts
            LOGGER.info("Listing all accounts");
            List<AccountDTO> accounts = getAccountServiceClient().getAllAccounts();
            request.setAttribute("accounts", accounts);
        }
        
        request.getRequestDispatcher("/WEB-INF/views/account-list.jsp").forward(request, response);
    }
    
    private void viewAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Viewing account: " + id);
        
        AccountDTO account = getAccountServiceClient().getAccountById(id);
        ClientDTO client = getClientServiceClient().getClientById(account.getClientId());
        
        request.setAttribute("account", account);
        request.setAttribute("client", client);
        request.getRequestDispatcher("/WEB-INF/views/account-details.jsp").forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        LOGGER.info("Showing new account form");
        
        // Get all clients for the dropdown
        List<ClientDTO> clients = getClientServiceClient().getAllClients();
        request.setAttribute("clients", clients);
        
        // Pre-select client if provided
        String clientIdParam = request.getParameter("clientId");
        if (clientIdParam != null) {
            request.setAttribute("selectedClientId", Long.parseLong(clientIdParam));
        }
        
        request.getRequestDispatcher("/WEB-INF/views/account-form.jsp").forward(request, response);
    }
    
    private void createAccount(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        LOGGER.info("Creating new account");
        
        AccountDTO account = new AccountDTO();
        account.setAccountNumber(request.getParameter("accountNumber"));
        account.setClientId(Long.parseLong(request.getParameter("clientId")));
        account.setAccountType(AccountDTO.AccountType.valueOf(request.getParameter("accountType")));
        
        String balanceParam = request.getParameter("balance");
        if (balanceParam != null && !balanceParam.isEmpty()) {
            account.setBalance(new BigDecimal(balanceParam));
        }
        
        getAccountServiceClient().createAccount(account);
        
        response.sendRedirect(request.getContextPath() + "/accounts?clientId=" + account.getClientId());
    }
    
    private void showDepositForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Showing deposit form for account: " + id);
        
        AccountDTO account = getAccountServiceClient().getAccountById(id);
        request.setAttribute("account", account);
        request.setAttribute("operation", "deposit");
        request.getRequestDispatcher("/WEB-INF/views/transaction-form.jsp").forward(request, response);
    }
    
    private void processDeposit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
        
        LOGGER.info("Processing deposit of " + amount + " to account: " + id);
        
        TransactionDTO transaction = new TransactionDTO(amount);
        getAccountServiceClient().deposit(id, transaction);
        
        response.sendRedirect(request.getContextPath() + "/accounts/view?id=" + id);
    }
    
    private void showWithdrawForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Showing withdraw form for account: " + id);
        
        AccountDTO account = getAccountServiceClient().getAccountById(id);
        request.setAttribute("account", account);
        request.setAttribute("operation", "withdraw");
        request.getRequestDispatcher("/WEB-INF/views/transaction-form.jsp").forward(request, response);
    }
    
    private void processWithdraw(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
        
        LOGGER.info("Processing withdrawal of " + amount + " from account: " + id);
        
        TransactionDTO transaction = new TransactionDTO(amount);
        getAccountServiceClient().withdraw(id, transaction);
        
        response.sendRedirect(request.getContextPath() + "/accounts/view?id=" + id);
    }
    
    private void showTransferForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Long fromId = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Showing transfer form for account: " + fromId);
        
        AccountDTO fromAccount = getAccountServiceClient().getAccountById(fromId);
        List<AccountDTO> allAccounts = getAccountServiceClient().getAllAccounts();
        
        request.setAttribute("fromAccount", fromAccount);
        request.setAttribute("allAccounts", allAccounts);
        request.setAttribute("operation", "transfer");
        request.getRequestDispatcher("/WEB-INF/views/transaction-form.jsp").forward(request, response);
    }
    
    private void processTransfer(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Long fromId = Long.parseLong(request.getParameter("fromId"));
        Long toId = Long.parseLong(request.getParameter("toId"));
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
        
        LOGGER.info("Processing transfer of " + amount + " from account " + fromId + " to " + toId);
        
        TransactionDTO transaction = new TransactionDTO(amount);
        getAccountServiceClient().transfer(fromId, toId, transaction);
        
        response.sendRedirect(request.getContextPath() + "/accounts/view?id=" + fromId);
    }
    
    private void suspendAccount(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Suspending account: " + id);
        
        getAccountServiceClient().suspendAccount(id);
        
        response.sendRedirect(request.getContextPath() + "/accounts/view?id=" + id);
    }
    
    private void activateAccount(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Activating account: " + id);
        
        getAccountServiceClient().activateAccount(id);
        
        response.sendRedirect(request.getContextPath() + "/accounts/view?id=" + id);
    }
    
    private void closeAccount(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        LOGGER.info("Closing account: " + id);
        
        getAccountServiceClient().closeAccount(id);
        
        response.sendRedirect(request.getContextPath() + "/accounts/view?id=" + id);
    }
    
    private void deleteAccount(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        Long id = Long.parseLong(request.getParameter("id"));
        Long clientId = Long.parseLong(request.getParameter("clientId"));
        LOGGER.info("Deleting account: " + id);
        
        getAccountServiceClient().deleteAccount(id);
        
        response.sendRedirect(request.getContextPath() + "/accounts?clientId=" + clientId);
    }
}

// Made with Bob
