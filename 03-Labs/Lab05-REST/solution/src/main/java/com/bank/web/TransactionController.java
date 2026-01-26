package com.bank.web;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Transaction;
import com.bank.model.Account;
import com.bank.service.TransactionService;
import com.bank.service.AccountService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Web Controller for Transaction operations.
 * Handles JSP-based web interface requests for viewing transaction history.
 * 
 * Lab 05 - JAX-RS: Transaction Management (Web Interface)
 */
@WebServlet(urlPatterns = {"/transactions", "/transactions/*"})
public class TransactionController extends HttpServlet {
    
    @Inject
    private Logger logger;
    
    @Inject
    private TransactionService transactionService;
    
    @Inject
    private AccountService accountService;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();
        
        try {
            // Handle different paths
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all transactions or filter by account
                String accountIdParam = request.getParameter("accountId");
                
                if (accountIdParam != null) {
                    listTransactionsByAccount(request, response, Long.parseLong(accountIdParam));
                } else {
                    listAllTransactions(request, response);
                }
            } else if (pathInfo.equals("/view")) {
                // View a specific transaction
                viewTransaction(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid number format: " + e.getMessage());
            request.setAttribute("error", "Invalid parameter format");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        } catch (Exception e) {
            logger.severe("Error in TransactionController: " + e.getMessage());
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    /**
     * List all transactions.
     */
    private void listAllTransactions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        logger.info("Listing all transactions");
        
        List<Transaction> transactions = transactionService.findAll();
        request.setAttribute("transactions", transactions);
        request.setAttribute("title", "All Transactions");
        
        request.getRequestDispatcher("/WEB-INF/views/transaction-list.jsp").forward(request, response);
    }
    
    /**
     * List transactions for a specific account.
     */
    private void listTransactionsByAccount(HttpServletRequest request, HttpServletResponse response, Long accountId)
            throws ServletException, IOException {
        
        logger.info("Listing transactions for account: " + accountId);
        
        // Get the account
        Account account = accountService.findById(accountId);
        if (account == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Account not found");
            return;
        }
        
        // Get transactions
        List<Transaction> transactions = transactionService.findByAccount(accountId);
        
        request.setAttribute("transactions", transactions);
        request.setAttribute("account", account);
        request.setAttribute("title", "Transactions for Account " + account.getNumber());
        
        request.getRequestDispatcher("/WEB-INF/views/transaction-list.jsp").forward(request, response);
    }
    
    /**
     * View a specific transaction.
     */
    private void viewTransaction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Transaction ID is required");
            return;
        }
        
        Long id = Long.parseLong(idParam);
        logger.info("Viewing transaction: " + id);
        
        Transaction transaction = transactionService.findById(id);
        if (transaction == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Transaction not found");
            return;
        }
        
        // Get the account
        Account account = transaction.getAccount();
        
        request.setAttribute("transaction", transaction);
        request.setAttribute("account", account);
        
        request.getRequestDispatcher("/WEB-INF/views/transaction-details.jsp").forward(request, response);
    }
}

// Made with Bob