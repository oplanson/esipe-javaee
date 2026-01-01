package com.bank.api;

import com.bank.exception.NotFoundException;
import com.bank.model.Account;
import com.bank.service.AccountService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST resource for Account operations.
 * 
 * Base URL: /api/accounts
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
    
    @Inject
    private AccountService accountService;
    
    @Inject
    private Logger logger;
    
    /**
     * Get all accounts.
     * 
     * @return List of all accounts
     * 
     * Example:
     * GET /api/accounts
     * 
     * Response: 200 OK
     * [
     *   {"id": 1, "number": "ACC001", "balance": 1000.00, "type": "CHECKING"},
     *   {"id": 2, "number": "ACC002", "balance": 5000.00, "type": "SAVINGS"}
     * ]
     */
    @GET
    public List<Account> getAllAccounts() {
        logger.info("REST: Getting all accounts");
        return accountService.findAll();
    }
    
    /**
     * Get account by ID.
     * 
     * @param id Account ID
     * @return Account object
     * @throws NotFoundException if account not found
     * 
     * Example:
     * GET /api/accounts/1
     * 
     * Response: 200 OK
     * {"id": 1, "number": "ACC001", "balance": 1000.00, "type": "CHECKING"}
     */
    @GET
    @Path("/{id}")
    public Account getAccount(@PathParam("id") Long id) {
        logger.info("REST: Getting account with ID: " + id);
        
        Account account = accountService.findById(id);
        
        if (account == null) {
            throw new NotFoundException("Account with ID " + id + " not found");
        }
        
        return account;
    }
    
    /**
     * Create new account.
     * 
     * @param account Account data (validated)
     * @return Created account with HTTP 201 status
     * 
     * Example:
     * POST /api/accounts
     * Content-Type: application/json
     * 
     * {"number": "ACC001", "balance": 1000.00, "type": "CHECKING", "clientId": 1}
     * 
     * Response: 201 Created
     * {"id": 1, "number": "ACC001", "balance": 1000.00, "type": "CHECKING"}
     */
    @POST
    public Response createAccount(@Valid Account account) {
        logger.info("REST: Creating account: " + account.getNumber());
        
        // Extract client ID from the account's client relationship
        if (account.getClient() == null || account.getClient().getId() == null) {
            throw new IllegalArgumentException("Account must have a valid client with ID");
        }
        
        Long clientId = account.getClient().getId();
        Account created = accountService.create(account, clientId);
        
        return Response
            .status(Response.Status.CREATED)
            .entity(created)
            .build();
    }
    
    /**
     * Update existing account.
     * 
     * @param id Account ID
     * @param account Updated account data (validated)
     * @return Updated account
     * @throws NotFoundException if account not found
     * 
     * Example:
     * PUT /api/accounts/1
     * Content-Type: application/json
     * 
     * {"number": "ACC001", "balance": 1500.00, "type": "CHECKING"}
     * 
     * Response: 200 OK
     * {"id": 1, "number": "ACC001", "balance": 1500.00, "type": "CHECKING"}
     */
    @PUT
    @Path("/{id}")
    public Account updateAccount(@PathParam("id") Long id, @Valid Account account) {
        logger.info("REST: Updating account with ID: " + id);
        
        Account existing = accountService.findById(id);
        
        if (existing == null) {
            throw new NotFoundException("Account with ID " + id + " not found");
        }
        
        account.setId(id);
        return accountService.update(account);
    }
    
    /**
     * Delete account.
     * 
     * @param id Account ID
     * @return No content (HTTP 204)
     * @throws NotFoundException if account not found
     * 
     * Example:
     * DELETE /api/accounts/1
     * 
     * Response: 204 No Content
     */
    @DELETE
    @Path("/{id}")
    public Response deleteAccount(@PathParam("id") Long id) {
        logger.info("REST: Deleting account with ID: " + id);
        
        boolean deleted = accountService.delete(id);
        
        if (!deleted) {
            throw new NotFoundException("Account with ID " + id + " not found");
        }
        
        return Response.noContent().build();
    }
    
    /**
     * Get accounts for a specific client.
     * 
     * @param clientId Client ID
     * @return List of client's accounts
     * 
     * Example:
     * GET /api/accounts/client/1
     * 
     * Response: 200 OK
     * [
     *   {"id": 1, "number": "ACC001", "balance": 1000.00, "type": "CHECKING"},
     *   {"id": 2, "number": "ACC002", "balance": 5000.00, "type": "SAVINGS"}
     * ]
     */
    @GET
    @Path("/client/{clientId}")
    public List<Account> getClientAccounts(@PathParam("clientId") Long clientId) {
        logger.info("REST: Getting accounts for client ID: " + clientId);
        return accountService.findByClient(clientId);
    }
    
    /**
     * Get accounts by type.
     * 
     * @param type Account type (CHECKING or SAVINGS)
     * @return List of accounts of specified type
     * 
     * Example:
     * GET /api/accounts/type/CHECKING
     * 
     * Response: 200 OK
     * [{"id": 1, "number": "ACC001", "balance": 1000.00, "type": "CHECKING"}]
     */
    @GET
    @Path("/type/{type}")
    public List<Account> getAccountsByType(@PathParam("type") String type) {
        logger.info("REST: Getting accounts by type: " + type);
        return accountService.findByType(type);
    }
    
    /**
     * Deposit money into account.
     * 
     * @param id Account ID
     * @param amount Amount to deposit
     * @return Updated account
     * @throws NotFoundException if account not found
     * 
     * Example:
     * POST /api/accounts/1/deposit?amount=500.00
     * 
     * Response: 200 OK
     * {"id": 1, "number": "ACC001", "balance": 1500.00, "type": "CHECKING"}
     */
    @POST
    @Path("/{id}/deposit")
    public Account deposit(@PathParam("id") Long id, @QueryParam("amount") double amount) {
        logger.info("REST: Depositing " + amount + " to account ID: " + id);
        
        Account account = accountService.findById(id);
        
        if (account == null) {
            throw new NotFoundException("Account with ID " + id + " not found");
        }
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        account.deposit(amount);
        return accountService.update(account);
    }
    
    /**
     * Withdraw money from account.
     * 
     * @param id Account ID
     * @param amount Amount to withdraw
     * @return Updated account
     * @throws NotFoundException if account not found
     * @throws IllegalArgumentException if insufficient funds
     * 
     * Example:
     * POST /api/accounts/1/withdraw?amount=200.00
     * 
     * Response: 200 OK
     * {"id": 1, "number": "ACC001", "balance": 800.00, "type": "CHECKING"}
     */
    @POST
    @Path("/{id}/withdraw")
    public Account withdraw(@PathParam("id") Long id, @QueryParam("amount") double amount) {
        logger.info("REST: Withdrawing " + amount + " from account ID: " + id);
        
        Account account = accountService.findById(id);
        
        if (account == null) {
            throw new NotFoundException("Account with ID " + id + " not found");
        }
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        boolean success = account.withdraw(amount);
        
        if (!success) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        
        return accountService.update(account);
    }
    
    /**
     * Transfer money between accounts.
     * 
     * @param fromId Source account ID
     * @param toId Destination account ID
     * @param amount Amount to transfer
     * @return Transfer result with updated source account
     * @throws NotFoundException if either account not found
     * @throws IllegalArgumentException if insufficient funds or invalid amount
     * 
     * Example:
     * POST /api/accounts/1/transfer?toId=2&amount=100.00
     * 
     * Response: 200 OK
     * {
     *   "success": true,
     *   "message": "Transfer completed successfully",
     *   "fromAccount": {"id": 1, "balance": 900.00},
     *   "toAccount": {"id": 2, "balance": 1100.00}
     * }
     */
    @POST
    @Path("/{fromId}/transfer")
    public Response transfer(
            @PathParam("fromId") Long fromId,
            @QueryParam("toId") Long toId,
            @QueryParam("amount") double amount) {
        
        logger.info("REST: Transferring " + amount + " from account " + fromId + " to account " + toId);
        
        // Validate parameters
        if (toId == null) {
            throw new IllegalArgumentException("Destination account ID is required");
        }
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        // Verify both accounts exist
        Account fromAccount = accountService.findById(fromId);
        if (fromAccount == null) {
            throw new NotFoundException("Source account with ID " + fromId + " not found");
        }
        
        Account toAccount = accountService.findById(toId);
        if (toAccount == null) {
            throw new NotFoundException("Destination account with ID " + toId + " not found");
        }
        
        // Perform transfer
        boolean success = accountService.transfer(fromId, toId, amount);
        
        if (!success) {
            throw new IllegalArgumentException("Transfer failed - insufficient funds or invalid operation");
        }
        
        // Fetch updated accounts
        fromAccount = accountService.findById(fromId);
        toAccount = accountService.findById(toId);
        
        // Build response
        return Response.ok()
                .entity(new TransferResponse(true, "Transfer completed successfully", fromAccount, toAccount))
                .build();
    }
    
    /**
     * Inner class for transfer response.
     */
    public static class TransferResponse {
        private boolean success;
        private String message;
        private Account fromAccount;
        private Account toAccount;
        
        public TransferResponse(boolean success, String message, Account fromAccount, Account toAccount) {
            this.success = success;
            this.message = message;
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Account getFromAccount() {
            return fromAccount;
        }
        
        public void setFromAccount(Account fromAccount) {
            this.fromAccount = fromAccount;
        }
        
        public Account getToAccount() {
            return toAccount;
        }
        
        public void setToAccount(Account toAccount) {
            this.toAccount = toAccount;
        }
    }
    
    /**
     * Get count of all accounts.
     * 
     * @return Count object with total number
     * 
     * Example:
     * GET /api/accounts/count
     * 
     * Response: 200 OK
     * {"count": 15}
     */
    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountCount() {
        logger.info("REST: Getting account count");
        
        long count = accountService.count();
        
        return Response.ok()
            .entity("{\"count\": " + count + "}")
            .build();
    }
}

// Made with Bob
