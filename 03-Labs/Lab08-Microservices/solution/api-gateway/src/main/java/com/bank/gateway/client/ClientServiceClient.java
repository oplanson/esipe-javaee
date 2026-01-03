// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.client;

import com.bank.gateway.dto.ClientDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * MicroProfile REST Client for Client Service
 * Includes comprehensive fault tolerance patterns
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "com.bank.gateway.client.ClientServiceClient")
public interface ClientServiceClient {
    
    Logger LOGGER = Logger.getLogger(ClientServiceClient.class.getName());
    
    @GET
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(value = 3000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "getAllClientsFallback")
    List<ClientDTO> getAllClients();
    
    @GET
    @Path("/{id}")
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(value = 3000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "getClientByIdFallback")
    ClientDTO getClientById(@PathParam("id") Long id);
    
    @POST
    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(value = 5000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    ClientDTO createClient(ClientDTO client);
    
    @PUT
    @Path("/{id}")
    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(value = 5000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    ClientDTO updateClient(@PathParam("id") Long id, ClientDTO client);
    
    @DELETE
    @Path("/{id}")
    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(value = 3000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    void deleteClient(@PathParam("id") Long id);
    
    // Fallback methods
    default List<ClientDTO> getAllClientsFallback() {
        LOGGER.warning("Client Service unavailable - returning empty list");
        return new ArrayList<>();
    }
    
    default ClientDTO getClientByIdFallback(Long id) {
        LOGGER.warning("Client Service unavailable for client " + id + " - returning default client");
        ClientDTO fallback = new ClientDTO();
        fallback.setId(id);
        fallback.setFirstName("Service");
        fallback.setLastName("Unavailable");
        fallback.setEmail("unavailable@service.com");
        fallback.setPhone("N/A");
        return fallback;
    }
}

// Made with Bob
