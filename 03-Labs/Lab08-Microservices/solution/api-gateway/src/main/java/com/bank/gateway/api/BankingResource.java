// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.api;

import com.bank.gateway.dto.ClientWithAccountsDTO;
import com.bank.gateway.service.BankingAggregationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.logging.Logger;

/**
 * REST API Resource for API Gateway / BFF
 * Exposes aggregated banking data from multiple microservices
 */
@Path("/banking")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BankingResource {
    
    private static final Logger LOGGER = Logger.getLogger(BankingResource.class.getName());
    
    @Inject
    private BankingAggregationService aggregationService;
    
    /**
     * Get all clients with their accounts (aggregated data)
     * This is the BFF pattern in action - one call returns data from multiple services
     */
    @GET
    @Path("/clients-with-accounts")
    public Response getAllClientsWithAccounts() {
        LOGGER.info("API: Getting all clients with accounts");
        
        try {
            List<ClientWithAccountsDTO> result = aggregationService.getAllClientsWithAccounts();
            return Response.ok(result).build();
        } catch (Exception e) {
            LOGGER.severe("Error getting all clients with accounts: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Backend services unavailable")
                    .build();
        }
    }
    
    /**
     * Get a specific client with their accounts
     */
    @GET
    @Path("/clients-with-accounts/{clientId}")
    public Response getClientWithAccounts(@PathParam("clientId") Long clientId) {
        LOGGER.info("API: Getting client " + clientId + " with accounts");
        
        try {
            ClientWithAccountsDTO result = aggregationService.getClientWithAccounts(clientId);
            return Response.ok(result).build();
        } catch (Exception e) {
            LOGGER.severe("Error getting client " + clientId + " with accounts: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Backend services unavailable")
                    .build();
        }
    }
    
    /**
     * Health check endpoint for services
     */
    @GET
    @Path("/health")
    public Response checkServicesHealth() {
        LOGGER.info("API: Checking backend services health");
        
        boolean available = aggregationService.areServicesAvailable();
        
        if (available) {
            return Response.ok()
                    .entity("{\"status\": \"UP\", \"message\": \"All backend services are available\"}")
                    .build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"status\": \"DOWN\", \"message\": \"One or more backend services are unavailable\"}")
                    .build();
        }
    }
}

// Made with Bob