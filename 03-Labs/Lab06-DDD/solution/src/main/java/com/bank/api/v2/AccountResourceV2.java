package com.bank.api.v2;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.dto.AccountDTO;
import com.bank.exception.NotFoundException;
import com.bank.model.Account;
import com.bank.service.AccountService;
import com.bank.domain.valueobject.Money;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * REST resource for Account operations - API Version 2.
 * 
 * Base URL: /api/v2/accounts
 * 
 * VERSION 2 CHANGES:
 * - Uses AccountDTO with Money Value Object (amount + currency)
 * - Improved response format
 * - Better error handling
 * - Cleaner separation between domain and API
 * 
 * MIGRATION FROM V1:
 * - V1: {"balance": 1000.00}
 * - V2: {"balance": {"amount": 1000.00, "currency": "EUR"}}
 * 
 * @author Banking Application Team
 * @version 2.0
 * @since Lab 06
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResourceV2 {
    
    @Inject
    private AccountService accountService;
    
    @Inject
    private Logger logger;
    
    /**
     * Get all accounts (V2 format with Money Value Object).
     * 
     * @return List of all accounts as DTOs
     * 
     * Example:
     * GET /api/v2/accounts
     * 
     * Response: 200 OK
     * [
     *   {
     *     "id": 1,
     *     "accountNumber": "FR1234567890123456789012345",
     *     "balance": {
     *       "amount": 1000.00,
     *       "currency": "EUR"
     *     },
     *     "accountType": "CHECKING",
     *     "clientId": 1
     *   }
     * ]
     */
    @GET
    public Response getAllAccounts() {
        logger.info("REST V2: Getting all accounts");
        
        List<Account> accounts = accountService.findAll();
        List<AccountDTO> dtos = accounts.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        
        return Response.ok(dtos)
            .header("X-API-Version", "2.0")
            .build();
    }
    
    /**
     * Get account by ID (V2 format).
     * 
     * @param id Account ID
     * @return Account DTO with Money Value Object
     * @throws NotFoundException if account not found
     * 
     * Example:
     * GET /api/v2/accounts/1
     * 
     * Response: 200 OK
     * {
     *   "id": 1,
     *   "accountNumber": "FR1234567890123456789012345",
     *   "balance": {
     *     "amount": 1000.00,
     *     "currency": "EUR"
     *   },
     *   "accountType": "CHECKING",
     *   "clientId": 1
     * }
     */
    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") Long id) {
        logger.info("REST V2: Getting account with ID: " + id);
        
        Account account = accountService.findById(id);
        
        if (account == null) {
            throw new NotFoundException("Account with ID " + id + " not found");
        }
        
        return Response.ok(toDTO(account))
            .header("X-API-Version", "2.0")
            .build();
    }
    
    /**
     * Deposit money into account (V2 format).
     * 
     * @param id Account ID
     * @param request Deposit request with amount and currency
     * @return Updated account DTO
     * 
     * Example:
     * POST /api/v2/accounts/1/deposit
     * Content-Type: application/json
     * 
     * {
     *   "amount": 500.00,
     *   "currency": "EUR"
     * }
     * 
     * Response: 200 OK
     * {
     *   "id": 1,
     *   "balance": {
     *     "amount": 1500.00,
     *     "currency": "EUR"
     *   }
     * }
     */
    @POST
    @Path("/{id}/deposit")
    public Response deposit(@PathParam("id") Long id, @Valid MoneyRequest request) {
        logger.info("REST V2: Depositing " + request.amount + " " + request.currency + " to account ID: " + id);
        
        Account account = accountService.findById(id);
        
        if (account == null) {
            throw new NotFoundException("Account with ID " + id + " not found");
        }
        
        if (request.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        Money depositAmount = Money.of(request.amount, request.currency);
        account.deposit(depositAmount);
        Account updated = accountService.update(account);
        
        return Response.ok(toDTO(updated))
            .header("X-API-Version", "2.0")
            .build();
    }
    
    /**
     * Withdraw money from account (V2 format).
     * 
     * @param id Account ID
     * @param request Withdrawal request with amount and currency
     * @return Updated account DTO
     * 
     * Example:
     * POST /api/v2/accounts/1/withdraw
     * Content-Type: application/json
     * 
     * {
     *   "amount": 200.00,
     *   "currency": "EUR"
     * }
     */
    @POST
    @Path("/{id}/withdraw")
    public Response withdraw(@PathParam("id") Long id, @Valid MoneyRequest request) {
        logger.info("REST V2: Withdrawing " + request.amount + " " + request.currency + " from account ID: " + id);
        
        Account account = accountService.findById(id);
        
        if (account == null) {
            throw new NotFoundException("Account with ID " + id + " not found");
        }
        
        if (request.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        try {
            Money withdrawAmount = Money.of(request.amount, request.currency);
            account.withdraw(withdrawAmount);
            Account updated = accountService.update(account);
            
            return Response.ok(toDTO(updated))
                .header("X-API-Version", "2.0")
                .build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Insufficient funds or invalid amount: " + e.getMessage());
        }
    }
    
    /**
     * Transfer money between accounts (V2 format).
     * 
     * @param fromId Source account ID
     * @param request Transfer request with destination and amount
     * @return Transfer result
     * 
     * Example:
     * POST /api/v2/accounts/1/transfer
     * Content-Type: application/json
     * 
     * {
     *   "toAccountId": 2,
     *   "amount": 100.00,
     *   "currency": "EUR"
     * }
     * 
     * Response: 200 OK
     * {
     *   "success": true,
     *   "message": "Transfer completed successfully",
     *   "fromAccount": {...},
     *   "toAccount": {...}
     * }
     */
    @POST
    @Path("/{fromId}/transfer")
    public Response transfer(@PathParam("fromId") Long fromId, @Valid TransferRequest request) {
        logger.info("REST V2: Transferring " + request.amount + " " + request.currency + 
                   " from account " + fromId + " to account " + request.toAccountId);
        
        // Validate parameters
        if (request.toAccountId == null) {
            throw new IllegalArgumentException("Destination account ID is required");
        }
        
        if (request.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        // Verify both accounts exist
        Account fromAccount = accountService.findById(fromId);
        if (fromAccount == null) {
            throw new NotFoundException("Source account with ID " + fromId + " not found");
        }
        
        Account toAccount = accountService.findById(request.toAccountId);
        if (toAccount == null) {
            throw new NotFoundException("Destination account with ID " + request.toAccountId + " not found");
        }
        
        // Perform transfer
        boolean success = accountService.transfer(fromId, request.toAccountId, request.amount.doubleValue());
        
        if (!success) {
            throw new IllegalArgumentException("Transfer failed - insufficient funds or invalid operation");
        }
        
        // Fetch updated accounts
        fromAccount = accountService.findById(fromId);
        toAccount = accountService.findById(request.toAccountId);
        
        // Build response
        TransferResponse response = new TransferResponse(
            true,
            "Transfer completed successfully",
            toDTO(fromAccount),
            toDTO(toAccount)
        );
        
        return Response.ok(response)
            .header("X-API-Version", "2.0")
            .build();
    }
    
    /**
     * Convert Account entity to DTO.
     */
    private AccountDTO toDTO(Account account) {
        // Use the static factory method from AccountDTO Record
        return AccountDTO.fromEntity(account);
    }
    
    /**
     * Request DTO for money operations (deposit/withdraw).
     */
    public static class MoneyRequest {
        public BigDecimal amount;
        public String currency = "EUR";
        
        // Getters and setters for JSON binding
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
    
    /**
     * Request DTO for transfer operations.
     */
    public static class TransferRequest {
        public Long toAccountId;
        public BigDecimal amount;
        public String currency = "EUR";
        
        // Getters and setters for JSON binding
        public Long getToAccountId() { return toAccountId; }
        public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
    
    /**
     * Response DTO for transfer operations.
     */
    public static class TransferResponse {
        private boolean success;
        private String message;
        private AccountDTO fromAccount;
        private AccountDTO toAccount;
        
        public TransferResponse(boolean success, String message, AccountDTO fromAccount, AccountDTO toAccount) {
            this.success = success;
            this.message = message;
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public AccountDTO getFromAccount() { return fromAccount; }
        public void setFromAccount(AccountDTO fromAccount) { this.fromAccount = fromAccount; }
        public AccountDTO getToAccount() { return toAccount; }
        public void setToAccount(AccountDTO toAccount) { this.toAccount = toAccount; }
    }
}

// Made with Bob