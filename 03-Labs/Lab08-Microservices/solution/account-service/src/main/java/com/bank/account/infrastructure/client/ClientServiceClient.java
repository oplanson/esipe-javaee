// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.infrastructure.client;

import com.bank.account.application.dto.ClientDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * MicroProfile REST Client for Client Service
 * Provides fault-tolerant communication with the Client Service
 */
@Path("/clients")
@RegisterRestClient(configKey = "com.bank.account.infrastructure.client.ClientServiceClient")
@Produces(MediaType.APPLICATION_JSON)
public interface ClientServiceClient {
    
    /**
     * Get client by ID from Client Service
     * Includes fault tolerance: Circuit Breaker, Retry, Timeout, and Fallback
     * 
     * @param id Client ID
     * @return Client information
     */
    @GET
    @Path("/{id}")
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(value = 2000)
    @CircuitBreaker(
        requestVolumeThreshold = 4,
        failureRatio = 0.5,
        delay = 5000,
        successThreshold = 2
    )
    @Fallback(fallbackMethod = "getClientByIdFallback")
    ClientDTO getClientById(@PathParam("id") Long id);
    
    /**
     * Fallback method when Client Service is unavailable
     * Returns a default client with minimal information
     * 
     * @param id Client ID
     * @return Default client DTO
     */
    default ClientDTO getClientByIdFallback(Long id) {
        ClientDTO fallbackClient = new ClientDTO();
        fallbackClient.setId(id);
        fallbackClient.setFirstName("Unknown");
        fallbackClient.setLastName("Client");
        fallbackClient.setEmail("unavailable@bank.com");
        return fallbackClient;
    }
}

// Made with Bob
