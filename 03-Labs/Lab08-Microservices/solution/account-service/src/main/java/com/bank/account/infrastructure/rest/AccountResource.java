// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.infrastructure.rest;

import com.bank.account.application.dto.AccountDTO;
import com.bank.account.application.dto.TransactionDTO;
import com.bank.account.application.service.AccountService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.logging.Logger;

/**
 * REST API for Account operations
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Account Management", description = "Operations for managing bank accounts")
public class AccountResource {
    
    private static final Logger LOGGER = Logger.getLogger(AccountResource.class.getName());
    
    @Inject
    private AccountService accountService;
    
    @POST
    @Operation(summary = "Create a new account", description = "Creates a new bank account for a client")
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Account created successfully",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))
        ),
        @APIResponse(responseCode = "400", description = "Invalid input data"),
        @APIResponse(responseCode = "404", description = "Client not found"),
        @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response createAccount(@Valid AccountDTO accountDTO) {
        try {
            LOGGER.info("Creating account for client: " + accountDTO.getClientId());
            AccountDTO created = accountService.createAccount(accountDTO);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid input: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        } catch (Exception e) {
            LOGGER.severe("Error creating account: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Failed to create account"))
                .build();
        }
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Get account by ID", description = "Retrieves account details by ID")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Account found",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))
        ),
        @APIResponse(responseCode = "404", description = "Account not found")
    })
    public Response getAccountById(
        @Parameter(description = "Account ID", required = true)
        @PathParam("id") Long id
    ) {
        LOGGER.info("Getting account: " + id);
        return accountService.getAccountById(id)
            .map(account -> Response.ok(account).build())
            .orElse(Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Account not found"))
                .build());
    }
    
    @GET
    @Path("/number/{accountNumber}")
    @Operation(summary = "Get account by account number", description = "Retrieves account details by account number")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Account found",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))
        ),
        @APIResponse(responseCode = "404", description = "Account not found")
    })
    public Response getAccountByNumber(
        @Parameter(description = "Account number", required = true)
        @PathParam("accountNumber") String accountNumber
    ) {
        LOGGER.info("Getting account by number: " + accountNumber);
        return accountService.getAccountByAccountNumber(accountNumber)
            .map(account -> Response.ok(account).build())
            .orElse(Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Account not found"))
                .build());
    }
    
    @GET
    @Path("/client/{clientId}")
    @Operation(summary = "Get accounts by client ID", description = "Retrieves all accounts for a specific client")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Accounts retrieved successfully",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))
        )
    })
    public Response getAccountsByClientId(
        @Parameter(description = "Client ID", required = true)
        @PathParam("clientId") Long clientId
    ) {
        LOGGER.info("Getting accounts for client: " + clientId);
        List<AccountDTO> accounts = accountService.getAccountsByClientId(clientId);
        return Response.ok(accounts).build();
    }
    
    @GET
    @Operation(summary = "Get all accounts", description = "Retrieves all bank accounts")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Accounts retrieved successfully",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))
        )
    })
    public Response getAllAccounts() {
        LOGGER.info("Getting all accounts");
        List<AccountDTO> accounts = accountService.getAllAccounts();
        return Response.ok(accounts).build();
    }
    
    @POST
    @Path("/{id}/deposit")
    @Operation(summary = "Deposit money", description = "Deposits money into an account")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Deposit successful",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))
        ),
        @APIResponse(responseCode = "400", description = "Invalid amount or account status"),
        @APIResponse(responseCode = "404", description = "Account not found")
    })
    public Response deposit(
        @Parameter(description = "Account ID", required = true)
        @PathParam("id") Long id,
        @Valid TransactionDTO transaction
    ) {
        try {
            LOGGER.info("Depositing " + transaction.getAmount() + " to account: " + id);
            AccountDTO updated = accountService.deposit(id, transaction);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            LOGGER.warning("Deposit failed: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/{id}/withdraw")
    @Operation(summary = "Withdraw money", description = "Withdraws money from an account")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Withdrawal successful",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))
        ),
        @APIResponse(responseCode = "400", description = "Invalid amount, insufficient funds, or account status"),
        @APIResponse(responseCode = "404", description = "Account not found")
    })
    public Response withdraw(
        @Parameter(description = "Account ID", required = true)
        @PathParam("id") Long id,
        @Valid TransactionDTO transaction
    ) {
        try {
            LOGGER.info("Withdrawing " + transaction.getAmount() + " from account: " + id);
            AccountDTO updated = accountService.withdraw(id, transaction);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            LOGGER.warning("Withdrawal failed: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/{fromId}/transfer/{toId}")
    @Operation(summary = "Transfer money", description = "Transfers money between two accounts")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Transfer successful",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))
        ),
        @APIResponse(responseCode = "400", description = "Invalid amount, insufficient funds, or account status"),
        @APIResponse(responseCode = "404", description = "Account not found")
    })
    public Response transfer(
        @Parameter(description = "Source account ID", required = true)
        @PathParam("fromId") Long fromId,
        @Parameter(description = "Target account ID", required = true)
        @PathParam("toId") Long toId,
        @Valid TransactionDTO transaction
    ) {
        try {
            LOGGER.info("Transferring " + transaction.getAmount() + " from account " + fromId + " to " + toId);
            TransactionDTO transferTransaction = new TransactionDTO(transaction.getAmount(), toId);
            AccountDTO updated = accountService.transfer(fromId, transferTransaction);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            LOGGER.warning("Transfer failed: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        }
    }
    
    @PUT
    @Path("/{id}/suspend")
    @Operation(summary = "Suspend account", description = "Suspends an account")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Account suspended successfully",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))
        ),
        @APIResponse(responseCode = "400", description = "Invalid account status"),
        @APIResponse(responseCode = "404", description = "Account not found")
    })
    public Response suspendAccount(
        @Parameter(description = "Account ID", required = true)
        @PathParam("id") Long id
    ) {
        try {
            LOGGER.info("Suspending account: " + id);
            AccountDTO updated = accountService.suspendAccount(id);
            return Response.ok(updated).build();
        } catch (IllegalStateException e) {
            LOGGER.warning("Suspend failed: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        }
    }
    
    @PUT
    @Path("/{id}/activate")
    @Operation(summary = "Activate account", description = "Activates a suspended account")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Account activated successfully",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))
        ),
        @APIResponse(responseCode = "400", description = "Invalid account status"),
        @APIResponse(responseCode = "404", description = "Account not found")
    })
    public Response activateAccount(
        @Parameter(description = "Account ID", required = true)
        @PathParam("id") Long id
    ) {
        try {
            LOGGER.info("Activating account: " + id);
            AccountDTO updated = accountService.activateAccount(id);
            return Response.ok(updated).build();
        } catch (IllegalStateException e) {
            LOGGER.warning("Activation failed: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        }
    }
    
    @PUT
    @Path("/{id}/close")
    @Operation(summary = "Close account", description = "Closes an account (balance must be zero)")
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Account closed successfully",
            content = @Content(schema = @Schema(implementation = AccountDTO.class))
        ),
        @APIResponse(responseCode = "400", description = "Account has non-zero balance or invalid status"),
        @APIResponse(responseCode = "404", description = "Account not found")
    })
    public Response closeAccount(
        @Parameter(description = "Account ID", required = true)
        @PathParam("id") Long id
    ) {
        try {
            LOGGER.info("Closing account: " + id);
            AccountDTO updated = accountService.closeAccount(id);
            return Response.ok(updated).build();
        } catch (IllegalStateException e) {
            LOGGER.warning("Close failed: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete account", description = "Deletes an account (must be closed first)")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Account deleted successfully"),
        @APIResponse(responseCode = "400", description = "Account is not closed"),
        @APIResponse(responseCode = "404", description = "Account not found")
    })
    public Response deleteAccount(
        @Parameter(description = "Account ID", required = true)
        @PathParam("id") Long id
    ) {
        try {
            LOGGER.info("Deleting account: " + id);
            accountService.deleteAccount(id);
            return Response.noContent().build();
        } catch (IllegalStateException e) {
            LOGGER.warning("Delete failed: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        }
    }
    
    /**
     * Simple error response class
     */
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse() {}
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}

// Made with Bob
