/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.web.adapter;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.command.DepositCommand;
import com.bank.application.command.OpenAccountCommand;
import com.bank.application.command.TransferCommand;
import com.bank.application.command.WithdrawCommand;
import com.bank.application.dto.AccountDTO;
import com.bank.application.dto.ClientDTO;
import com.bank.application.port.in.AccountManagementUseCase;
import com.bank.application.port.in.ClientManagementUseCase;
import com.bank.application.port.in.MoneyOperationsUseCase;
import com.bank.domain.valueobject.AccountType;
import com.bank.domain.valueobject.Money;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Web adapter for Account operations (JSP/Servlet).
 *
 * Hexagonal Architecture: Primary Adapter (Driving Adapter)
 * - Receives HTTP requests from web browser
 * - Converts form data to use case commands
 * - Delegates to use cases (primary ports)
 * - Forwards to JSP views with DTOs
 * - Isolated from domain and application logic
 *
 * CDI-managed servlet (declared in web.xml, NOT with @WebServlet).
 * This allows proper CDI injection to work.
 */
public class AccountWebAdapter extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(AccountWebAdapter.class.getName());
    
    @Inject
    private AccountManagementUseCase accountManagement;
    
    @Inject
    private MoneyOperationsUseCase moneyOperations;
    
    @Inject
    private ClientManagementUseCase clientManagement;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List accounts (requires clientId parameter)
                String clientIdParam = request.getParameter("clientId");
                if (clientIdParam != null) {
                    Long clientId = Long.parseLong(clientIdParam);
                    listAccounts(request, response, clientId);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "clientId parameter required");
                }
            } else if (pathInfo.equals("/new")) {
                // Show create form
                showCreateForm(request, response);
            } else if (pathInfo.matches("/\\d+")) {
                // Show account details
                Long id = Long.parseLong(pathInfo.substring(1));
                showAccount(request, response, id);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Account GET request failed: " + e.getMessage(), e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Create new account
                createAccount(request, response);
            } else if (pathInfo.matches("/\\d+/deposit")) {
                // Deposit money
                Long id = Long.parseLong(pathInfo.substring(1, pathInfo.indexOf("/deposit")));
                deposit(request, response, id);
            } else if (pathInfo.matches("/\\d+/withdraw")) {
                // Withdraw money
                Long id = Long.parseLong(pathInfo.substring(1, pathInfo.indexOf("/withdraw")));
                withdraw(request, response, id);
            } else if (pathInfo.equals("/transfer")) {
                // Transfer money
                transfer(request, response);
            } else if (pathInfo.matches("/\\d+/close")) {
                // Close account
                Long id = Long.parseLong(pathInfo.substring(1, pathInfo.indexOf("/close")));
                closeAccount(request, response, id);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Account POST request failed: " + e.getMessage(), e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    private void listAccounts(HttpServletRequest request, HttpServletResponse response, Long clientId)
            throws ServletException, IOException {
        List<AccountDTO> accounts = accountManagement.getClientAccounts(clientId);
        ClientDTO client = clientManagement.getClient(clientId);
        
        request.setAttribute("accounts", accounts);
        request.setAttribute("client", client);
        request.getRequestDispatcher("/WEB-INF/views/account-list.jsp").forward(request, response);
    }
    
    private void showAccount(HttpServletRequest request, HttpServletResponse response, Long id)
            throws ServletException, IOException {
        AccountDTO account = accountManagement.getAccount(id);
        request.setAttribute("account", account);
        request.getRequestDispatcher("/WEB-INF/views/account-details.jsp").forward(request, response);
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String clientIdParam = request.getParameter("clientId");
        if (clientIdParam != null) {
            Long clientId = Long.parseLong(clientIdParam);
            ClientDTO client = clientManagement.getClient(clientId);
            request.setAttribute("client", client);
        }
        
        request.setAttribute("accountTypes", AccountType.values());
        request.getRequestDispatcher("/WEB-INF/views/account-form.jsp").forward(request, response);
    }
    
    private void createAccount(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Long clientId = Long.parseLong(request.getParameter("clientId"));
        BigDecimal initialBalance = new BigDecimal(request.getParameter("initialBalance"));
        String currency = request.getParameter("currency");
        if (currency == null || currency.isEmpty()) {
            currency = "EUR";
        }
        AccountType accountType = AccountType.valueOf(request.getParameter("accountType"));
        
        OpenAccountCommand command = new OpenAccountCommand(
            clientId,
            null, // AccountNumber will be generated
            Money.of(initialBalance, currency),
            accountType,
            currency
        );
        AccountDTO account = accountManagement.openAccount(command);
        
        response.sendRedirect(request.getContextPath() + "/accounts/" + account.getId());
    }
    
    private void deposit(HttpServletRequest request, HttpServletResponse response, Long accountId)
            throws IOException {
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
        String currency = request.getParameter("currency");
        if (currency == null || currency.isEmpty()) {
            currency = "EUR";
        }
        
        DepositCommand command = new DepositCommand(accountId, Money.of(amount, currency));
        moneyOperations.deposit(command);
        
        response.sendRedirect(request.getContextPath() + "/accounts/" + accountId);
    }
    
    private void withdraw(HttpServletRequest request, HttpServletResponse response, Long accountId)
            throws IOException {
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
        String currency = request.getParameter("currency");
        if (currency == null || currency.isEmpty()) {
            currency = "EUR";
        }
        
        WithdrawCommand command = new WithdrawCommand(accountId, Money.of(amount, currency));
        moneyOperations.withdraw(command);
        
        response.sendRedirect(request.getContextPath() + "/accounts/" + accountId);
    }
    
    private void transfer(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Long fromAccountId = Long.parseLong(request.getParameter("fromAccountId"));
        Long toAccountId = Long.parseLong(request.getParameter("toAccountId"));
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
        String currency = request.getParameter("currency");
        if (currency == null || currency.isEmpty()) {
            currency = "EUR";
        }
        
        TransferCommand command = new TransferCommand(fromAccountId, toAccountId, Money.of(amount, currency));
        moneyOperations.transfer(command);
        
        response.sendRedirect(request.getContextPath() + "/accounts/" + fromAccountId);
    }
    
    private void closeAccount(HttpServletRequest request, HttpServletResponse response, Long accountId)
            throws IOException {
        AccountDTO account = accountManagement.getAccount(accountId);
        accountManagement.closeAccount(accountId);
        
        response.sendRedirect(request.getContextPath() + "/clients/" + account.getClientId());
    }
}

// Made with Bob
