/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.rest.adapter.v1;

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
 * REST adapter for Account operations - Version 1 (Simple format).
 *
 * API V1: Simple format with BigDecimal for amounts
 * - Balance as simple number: {"balance": 1000.00}
 * - Backward compatible format
 *
 * @deprecated Use V2 API instead ({@link com.bank.infrastructure.rest.adapter.v2.AccountRestAdapterV2})
 *             V1 will be removed in a future release. Migrate to /api/v2/accounts
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
@Deprecated
@Path("/v1/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountRestAdapter {
    
    @Inject
    private AccountManagementUseCase accountManagement;
    
    @Inject
    private MoneyOperationsUseCase moneyOperations;
    
    /**
     * Open a new account.
     * POST /api/v1/accounts
     *
     * @deprecated Use POST /api/v2/accounts instead
     */
    @Deprecated
    @POST
    public Response openAccount(OpenAccountRequest request) {
        try {
            String currency = request.currency != null ? request.currency : "EUR";
            OpenAccountCommand command = new OpenAccountCommand(
                request.clientId,
                null, // AccountNumber will be generated
                Money.of(BigDecimal.valueOf(request.initialBalance), currency),
                AccountType.valueOf(request.accountType),
                currency
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
     * GET /api/v1/accounts/{id}
     *
     * @deprecated Use GET /api/v2/accounts/{id} instead
     */
    @Deprecated
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
     * GET /api/v1/accounts?clientId={clientId}
     *
     * @deprecated Use GET /api/v2/accounts?clientId={clientId} instead
     */
    @Deprecated
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
     * DELETE /api/v1/accounts/{id}
     *
     * @deprecated Use DELETE /api/v2/accounts/{id} instead
     */
    @Deprecated
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
     * POST /api/v1/accounts/{id}/deposit
     *
     * @deprecated Use POST /api/v2/accounts/{id}/deposit instead
     */
    @Deprecated
    @POST
    @Path("/{id}/deposit")
    public Response deposit(@PathParam("id") Long id, MoneyOperationRequest request) {
        try {
            DepositCommand command = new DepositCommand(
                id,
                Money.of(request.amount, request.currency != null ? request.currency : "EUR")
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
     * POST /api/v1/accounts/{id}/withdraw
     *
     * @deprecated Use POST /api/v2/accounts/{id}/withdraw instead
     */
    @Deprecated
    @POST
    @Path("/{id}/withdraw")
    public Response withdraw(@PathParam("id") Long id, MoneyOperationRequest request) {
        try {
            WithdrawCommand command = new WithdrawCommand(
                id,
                Money.of(request.amount, request.currency != null ? request.currency : "EUR")
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
     * POST /api/v1/accounts/transfer
     *
     * @deprecated Use POST /api/v2/accounts/transfer instead
     */
    @Deprecated
    @POST
    @Path("/transfer")
    public Response transfer(TransferRequest request) {
        try {
            TransferCommand command = new TransferCommand(
                request.fromAccountId,
                request.toAccountId,
                Money.of(request.amount, request.currency != null ? request.currency : "EUR")
            );
            
            moneyOperations.transfer(command);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    // Request/Response DTOs for REST API
    
    public static class OpenAccountRequest {
        public Long clientId;
        public double initialBalance;
        public String currency;
        public String accountType;
    }
    
    public static class MoneyOperationRequest {
        public BigDecimal amount;
        public String currency;
    }
    
    public static class TransferRequest {
        public Long fromAccountId;
        public Long toAccountId;
        public BigDecimal amount;
        public String currency;
    }
    
    public static class ErrorResponse {
        public String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}

// Made with Bob
