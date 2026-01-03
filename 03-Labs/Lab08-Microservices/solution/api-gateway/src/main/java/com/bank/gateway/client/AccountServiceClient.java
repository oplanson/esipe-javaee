// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.client;

import com.bank.gateway.dto.AccountDTO;
import com.bank.gateway.dto.TransactionDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * MicroProfile REST Client for Account Service
 * Includes comprehensive fault tolerance patterns
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "com.bank.gateway.client.AccountServiceClient")
public interface AccountServiceClient {
    
    Logger LOGGER = Logger.getLogger(AccountServiceClient.class.getName());
    
    @GET
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(value = 3000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "getAllAccountsFallback")
    List<AccountDTO> getAllAccounts();
    
    @GET
    @Path("/{id}")
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(value = 3000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "getAccountByIdFallback")
    AccountDTO getAccountById(@PathParam("id") Long id);
    
    @GET
    @Path("/client/{clientId}")
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(value = 3000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "getAccountsByClientIdFallback")
    List<AccountDTO> getAccountsByClientId(@PathParam("clientId") Long clientId);
    
    @POST
    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(value = 5000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    AccountDTO createAccount(AccountDTO account);
    
    @POST
    @Path("/{id}/deposit")
    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(value = 5000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    AccountDTO deposit(@PathParam("id") Long id, TransactionDTO transaction);
    
    @POST
    @Path("/{id}/withdraw")
    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(value = 5000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    AccountDTO withdraw(@PathParam("id") Long id, TransactionDTO transaction);
    
    @POST
    @Path("/{fromId}/transfer/{toId}")
    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(value = 5000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    AccountDTO transfer(@PathParam("fromId") Long fromId, @PathParam("toId") Long toId, TransactionDTO transaction);
    
    @PUT
    @Path("/{id}/suspend")
    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(value = 3000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    AccountDTO suspendAccount(@PathParam("id") Long id);
    
    @PUT
    @Path("/{id}/activate")
    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(value = 3000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    AccountDTO activateAccount(@PathParam("id") Long id);
    
    @PUT
    @Path("/{id}/close")
    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(value = 3000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    AccountDTO closeAccount(@PathParam("id") Long id);
    
    @DELETE
    @Path("/{id}")
    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(value = 3000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    void deleteAccount(@PathParam("id") Long id);
    
    // Fallback methods
    default List<AccountDTO> getAllAccountsFallback() {
        LOGGER.warning("Account Service unavailable - returning empty list");
        return new ArrayList<>();
    }
    
    default AccountDTO getAccountByIdFallback(Long id) {
        LOGGER.warning("Account Service unavailable for account " + id + " - returning null");
        return null;
    }
    
    default List<AccountDTO> getAccountsByClientIdFallback(Long clientId) {
        LOGGER.warning("Account Service unavailable for client " + clientId + " - returning empty list");
        return new ArrayList<>();
    }
}

// Made with Bob
