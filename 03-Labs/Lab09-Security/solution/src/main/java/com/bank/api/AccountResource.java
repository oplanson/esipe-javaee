// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.api;

import com.bank.dto.ErrorResponse;
import com.bank.model.Account;
import com.bank.security.SecurityAuditService;
import com.bank.service.AccountService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST resource for account operations with role-based access control
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
    
    private static final Logger LOGGER = Logger.getLogger(AccountResource.class.getName());
    
    @Inject
    private AccountService accountService;
    
    @Inject
    private SecurityAuditService auditService;
    
    @Context
    private HttpServletRequest request;
    
    /**
     * Get all accounts (ADMIN and MANAGER only)
     * GET /api/accounts
     */
    @GET
    @RolesAllowed({"ADMIN", "MANAGER"})
    public Response getAllAccounts(@Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            LOGGER.info("User " + username + " retrieving all accounts");
            
            List<Account> accounts = accountService.getAllAccounts();
            return Response.ok(accounts).build();
            
        } catch (Exception e) {
            LOGGER.severe("Error getting all accounts: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Get accounts for current user (CUSTOMER can only see their own)
     * GET /api/accounts/my
     */
    @GET
    @Path("/my")
    @RolesAllowed({"CUSTOMER", "TELLER", "MANAGER", "ADMIN"})
    public Response getMyAccounts(@Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            LOGGER.info("User " + username + " retrieving their accounts");
            
            List<Account> accounts = accountService.getAccountsByOwner(username);
            return Response.ok(accounts).build();
            
        } catch (Exception e) {
            LOGGER.severe("Error getting user accounts: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Get account by ID
     * GET /api/accounts/{id}
     */
    @GET
    @Path("/{id}")
    @RolesAllowed({"CUSTOMER", "TELLER", "MANAGER", "ADMIN"})
    public Response getAccountById(@PathParam("id") Long id, 
                                   @Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            Account account = accountService.getAccountById(id);
            
            if (account == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Not found", "Account not found", 404))
                        .build();
            }
            
            // CUSTOMER can only see their own accounts
            if (securityContext.isUserInRole("CUSTOMER") && 
                !account.getOwnerUsername().equals(username)) {
                
                String ipAddress = getClientIpAddress();
                String userAgent = request.getHeader("User-Agent");
                auditService.logAccessDenied(username, "/api/accounts/" + id, 
                        ipAddress, userAgent, "Not account owner");
                
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ErrorResponse("Forbidden", 
                                "You can only access your own accounts", 403))
                        .build();
            }
            
            return Response.ok(account).build();
            
        } catch (Exception e) {
            LOGGER.severe("Error getting account: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Create account (TELLER, MANAGER, ADMIN)
     * POST /api/accounts
     */
    @POST
    @RolesAllowed({"TELLER", "MANAGER", "ADMIN"})
    public Response createAccount(Account accountRequest, 
                                  @Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            LOGGER.info("User " + username + " creating account for: " + 
                       accountRequest.getOwnerUsername());
            
            Account account = accountService.createAccount(
                    accountRequest.getOwnerUsername(),
                    accountRequest.getBalance() != null ? accountRequest.getBalance() : BigDecimal.ZERO,
                    accountRequest.getAccountType()
            );
            
            return Response.status(Response.Status.CREATED)
                    .entity(account)
                    .build();
                    
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad request", e.getMessage(), 400))
                    .build();
                    
        } catch (Exception e) {
            LOGGER.severe("Error creating account: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Deposit money (TELLER, MANAGER, ADMIN)
     * POST /api/accounts/{id}/deposit
     */
    @POST
    @Path("/{id}/deposit")
    @RolesAllowed({"TELLER", "MANAGER", "ADMIN"})
    public Response deposit(@PathParam("id") Long id, 
                           @QueryParam("amount") BigDecimal amount,
                           @Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            LOGGER.info("User " + username + " depositing " + amount + " to account " + id);
            
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Bad request", 
                                "Amount must be positive", 400))
                        .build();
            }
            
            Account account = accountService.deposit(id, amount);
            return Response.ok(account).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad request", e.getMessage(), 400))
                    .build();
                    
        } catch (Exception e) {
            LOGGER.severe("Error depositing: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Withdraw money (TELLER, MANAGER, ADMIN)
     * POST /api/accounts/{id}/withdraw
     */
    @POST
    @Path("/{id}/withdraw")
    @RolesAllowed({"TELLER", "MANAGER", "ADMIN"})
    public Response withdraw(@PathParam("id") Long id, 
                            @QueryParam("amount") BigDecimal amount,
                            @Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            LOGGER.info("User " + username + " withdrawing " + amount + " from account " + id);
            
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Bad request", 
                                "Amount must be positive", 400))
                        .build();
            }
            
            Account account = accountService.withdraw(id, amount);
            return Response.ok(account).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad request", e.getMessage(), 400))
                    .build();
                    
        } catch (Exception e) {
            LOGGER.severe("Error withdrawing: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Transfer money (MANAGER, ADMIN only)
     * POST /api/accounts/transfer
     */
    @POST
    @Path("/transfer")
    @RolesAllowed({"MANAGER", "ADMIN"})
    public Response transfer(@QueryParam("from") Long fromId,
                            @QueryParam("to") Long toId,
                            @QueryParam("amount") BigDecimal amount,
                            @Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            LOGGER.info("User " + username + " transferring " + amount + 
                       " from account " + fromId + " to " + toId);
            
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Bad request", 
                                "Amount must be positive", 400))
                        .build();
            }
            
            accountService.transfer(fromId, toId, amount);
            
            return Response.ok()
                    .entity(new ErrorResponse("Success", "Transfer completed", 200))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad request", e.getMessage(), 400))
                    .build();
                    
        } catch (Exception e) {
            LOGGER.severe("Error transferring: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Delete account (ADMIN only)
     * DELETE /api/accounts/{id}
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteAccount(@PathParam("id") Long id,
                                  @Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            LOGGER.info("User " + username + " deleting account " + id);
            
            accountService.deleteAccount(id);
            
            return Response.noContent().build();
            
        } catch (Exception e) {
            LOGGER.severe("Error deleting account: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error", e.getMessage(), 500))
                    .build();
        }
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}

// Made with Bob
