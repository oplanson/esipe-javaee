/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.rest.adapter.v2;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.command.DepositCommand;
import com.bank.application.command.OpenAccountCommand;
import com.bank.application.command.TransferCommand;
import com.bank.application.command.WithdrawCommand;
import com.bank.application.dto.AccountDTO;
import com.bank.application.port.in.AccountManagementUseCase;
import com.bank.application.port.in.MoneyOperationsUseCase;
import com.bank.domain.valueobject.AccountType;
import com.bank.domain.valueobject.Money;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST adapter for Account operations - Version 2 (Rich format with Money VO).
 *
 * API V2: Rich format with Money value object
 * - Balance as object: {"balance": {"amount": 1000.00, "currency": "EUR"}}
 * - Exposes domain richness through the API
 * - Better representation of monetary values
 *
 * Hexagonal Architecture: Primary Adapter (Driving Adapter)
 * - Receives HTTP requests
 * - Converts REST requests to use case commands
 * - Delegates to use cases (primary ports)
 * - Returns DTOs as JSON responses
 * - Isolated from domain and application logic
 *
 * Multiple adapters can coexist for the same ports, demonstrating
 * the flexibility of hexagonal architecture.
 */
@Path("/v2/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountRestAdapterV2 {
    
    @Inject
    private AccountManagementUseCase accountManagement;
    
    @Inject
    private MoneyOperationsUseCase moneyOperations;
    
    /**
     * Open a new account.
     * POST /api/v2/accounts
     *
     * Request body example:
     * {
     *   "clientId": 1,
     *   "initialBalance": {"amount": 1000.00, "currency": "EUR"},
     *   "accountType": "CHECKING"
     * }
     */
    @POST
    public Response openAccount(OpenAccountRequestV2 request) {
        try {
            MoneyDTO balance = request.initialBalance != null ? request.initialBalance : new MoneyDTO(BigDecimal.ZERO, "EUR");
            OpenAccountCommand command = new OpenAccountCommand(
                request.clientId,
                null, // AccountNumber will be generated
                Money.of(balance.amount, balance.currency),
                AccountType.valueOf(request.accountType),
                balance.currency
            );
            
            AccountDTO account = accountManagement.openAccount(command);
            return Response.status(Response.Status.CREATED).entity(account).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal server error")).build();
        }
    }
    
    /**
     * Get account by ID.
     * GET /api/v2/accounts/{id}
     *
     * Response example:
     * {
     *   "id": 1,
     *   "accountNumber": "ACC001",
     *   "balance": {"amount": 1000.00, "currency": "EUR"},
     *   "accountType": "CHECKING",
     *   "clientId": 1,
     *   "active": true
     * }
     */
    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") Long id) {
        try {
            AccountDTO account = accountManagement.getAccount(id);
            return Response.ok(account).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * Get all accounts for a client.
     * GET /api/v2/accounts?clientId={clientId}
     */
    @GET
    public Response getClientAccounts(@QueryParam("clientId") Long clientId) {
        if (clientId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("clientId parameter is required")).build();
        }
        
        List<AccountDTO> accounts = accountManagement.getClientAccounts(clientId);
        return Response.ok(accounts).build();
    }
    
    /**
     * Close an account.
     * DELETE /api/v2/accounts/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response closeAccount(@PathParam("id") Long id) {
        try {
            accountManagement.closeAccount(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage())).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * Deposit money.
     * POST /api/v2/accounts/{id}/deposit
     *
     * Request body example:
     * {"amount": 100.00, "currency": "EUR"}
     */
    @POST
    @Path("/{id}/deposit")
    public Response deposit(@PathParam("id") Long id, MoneyDTO money) {
        try {
            DepositCommand command = new DepositCommand(
                id,
                Money.of(money.amount, money.currency != null ? money.currency : "EUR")
            );
            
            moneyOperations.deposit(command);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * Withdraw money.
     * POST /api/v2/accounts/{id}/withdraw
     *
     * Request body example:
     * {"amount": 50.00, "currency": "EUR"}
     */
    @POST
    @Path("/{id}/withdraw")
    public Response withdraw(@PathParam("id") Long id, MoneyDTO money) {
        try {
            WithdrawCommand command = new WithdrawCommand(
                id,
                Money.of(money.amount, money.currency != null ? money.currency : "EUR")
            );
            
            moneyOperations.withdraw(command);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * Transfer money between accounts.
     * POST /api/v2/accounts/transfer
     *
     * Request body example:
     * {
     *   "fromAccountId": 1,
     *   "toAccountId": 2,
     *   "money": {"amount": 200.00, "currency": "EUR"}
     * }
     */
    @POST
    @Path("/transfer")
    public Response transfer(TransferRequestV2 request) {
        try {
            TransferCommand command = new TransferCommand(
                request.fromAccountId,
                request.toAccountId,
                Money.of(request.money.amount, request.money.currency != null ? request.money.currency : "EUR")
            );
            
            moneyOperations.transfer(command);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    // Request/Response DTOs for REST API V2
    
    /**
     * Money DTO - represents Money value object in JSON.
     * Example: {"amount": 1000.00, "currency": "EUR"}
     */
    public static class MoneyDTO {
        public BigDecimal amount;
        public String currency;
        
        public MoneyDTO() {}
        
        public MoneyDTO(BigDecimal amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }
    }
    
    /**
     * Open account request with Money VO.
     */
    public static class OpenAccountRequestV2 {
        public Long clientId;
        public MoneyDTO initialBalance;
        public String accountType;
    }
    
    /**
     * Transfer request with Money VO.
     */
    public static class TransferRequestV2 {
        public Long fromAccountId;
        public Long toAccountId;
        public MoneyDTO money;
    }
    
    public static class ErrorResponse {
        public String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}

// Made with Bob
