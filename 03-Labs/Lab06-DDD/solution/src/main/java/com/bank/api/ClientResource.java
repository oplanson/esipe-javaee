package com.bank.api;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.exception.NotFoundException;
import com.bank.model.Client;
import com.bank.service.ClientService;
import com.bank.domain.valueobject.Email;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST resource for Client operations - API Version 1 (DEPRECATED).
 * Provides CRUD operations via HTTP methods.
 *
 * Base URL: /api/clients
 *
 * ⚠️ DEPRECATION NOTICE:
 * This API version is deprecated and will be removed on 2026-06-01.
 * Please migrate to /api/v2/clients for future compatibility.
 *
 * @author Banking Application Team
 * @version 1.0 (DEPRECATED)
 * @since Lab 06
 * @deprecated Use {@link com.bank.api.v2.ClientResourceV2} instead
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Deprecated(since = "1.0", forRemoval = true)
public class ClientResource {
    
    @Inject
    private ClientService clientService;
    
    @Inject
    private Logger logger;
    
    /**
     * Get all clients.
     * 
     * @return List of all clients
     * 
     * Example:
     * GET /api/clients
     * 
     * Response: 200 OK
     * [
     *   {"id": 1, "name": "John Doe", "email": "john@example.com", "premium": false},
     *   {"id": 2, "name": "Jane Smith", "email": "jane@example.com", "premium": true}
     * ]
     * @deprecated Use {@link com.bank.api.v2.ClientResourceV2#getAllClients()} instead
     */
    @GET
    @Deprecated(since = "1.0", forRemoval = true)
    public Response getAllClients() {
        logger.info("REST V1 (DEPRECATED): Getting all clients");
        List<Client> clients = clientService.findAll();
        
        return Response.ok(clients)
            .header("X-API-Version", "1.0")
            .header("X-API-Deprecated", "true")
            .header("X-API-Deprecation-Info", "This API version is deprecated. Use /api/v2/clients instead.")
            .header("X-API-Sunset-Date", "2026-06-01")
            .build();
    }
    
    /**
     * Get client by ID.
     * 
     * @param id Client ID
     * @return Client object
     * @throws NotFoundException if client not found
     * 
     * Example:
     * GET /api/clients/1
     * 
     * Response: 200 OK
     * {"id": 1, "name": "John Doe", "email": "john@example.com", "premium": false}
     * @deprecated Use {@link com.bank.api.v2.ClientResourceV2#getClient(Long)} instead
     */
    @GET
    @Path("/{id}")
    @Deprecated(since = "1.0", forRemoval = true)
    public Response getClient(@PathParam("id") Long id) {
        logger.info("REST V1 (DEPRECATED): Getting client with ID: " + id);
        
        Client client = clientService.findById(id);
        
        if (client == null) {
            throw new NotFoundException("Client with ID " + id + " not found");
        }
        
        return Response.ok(client)
            .header("X-API-Version", "1.0")
            .header("X-API-Deprecated", "true")
            .header("X-API-Sunset-Date", "2026-06-01")
            .build();
    }
    
    /**
     * Create new client.
     * 
     * @param client Client data (validated)
     * @return Created client with HTTP 201 status
     * 
     * Example:
     * POST /api/clients
     * Content-Type: application/json
     * 
     * {"name": "John Doe", "email": "john@example.com", "premium": false}
     * 
     * Response: 201 Created
     * {"id": 1, "name": "John Doe", "email": "john@example.com", "premium": false}
     * @deprecated Use {@link com.bank.api.v2.ClientResourceV2#createClient(com.bank.application.dto.ClientDTO)} instead
     */
    @POST
    @Deprecated(since = "1.0", forRemoval = true)
    public Response createClient(@Valid Client client) {
        logger.info("REST V1 (DEPRECATED): Creating client: " + client.getName());
        
        // Note: Client should be created using factory methods
        // For REST API, we accept the client object and persist it
        // In a real application, you might want to use DTOs instead
        Client created = clientService.create(client);
        
        return Response
            .status(Response.Status.CREATED)
            .entity(created)
            .header("X-API-Version", "1.0")
            .header("X-API-Deprecated", "true")
            .header("X-API-Sunset-Date", "2026-06-01")
            .build();
    }
    
    /**
     * Update existing client.
     * 
     * @param id Client ID
     * @param client Updated client data (validated)
     * @return Updated client
     * @throws NotFoundException if client not found
     * 
     * Example:
     * PUT /api/clients/1
     * Content-Type: application/json
     * 
     * {"name": "John Updated", "email": "john.updated@example.com", "premium": true}
     * 
     * Response: 200 OK
     * {"id": 1, "name": "John Updated", "email": "john.updated@example.com", "premium": true}
     * @deprecated Use {@link com.bank.api.v2.ClientResourceV2#updateClient(Long, com.bank.application.dto.ClientDTO)} instead
     */
    @PUT
    @Path("/{id}")
    @Deprecated(since = "1.0", forRemoval = true)
    public Response updateClient(@PathParam("id") Long id, @Valid Client client) {
        logger.info("REST V1 (DEPRECATED): Updating client with ID: " + id);
        
        Client existing = clientService.findById(id);
        
        if (existing == null) {
            throw new NotFoundException("Client with ID " + id + " not found");
        }
        
        // Update using domain methods instead of setters
        try {
            Email emailVO = Email.of(client.getEmail().getValue());
            existing.updateName(client.getName());
            existing.updateEmail(emailVO);
            
            // Handle premium status change
            if (client.isPremium() && !existing.isPremium()) {
                existing.upgradeToPremium();
            } else if (!client.isPremium() && existing.isPremium()) {
                existing.downgradeFromPremium();
            }
            
            Client updated = clientService.update(existing);
            
            return Response.ok(updated)
                .header("X-API-Version", "1.0")
                .header("X-API-Deprecated", "true")
                .header("X-API-Sunset-Date", "2026-06-01")
                .build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("Error updating client: " + e.getMessage());
        }
    }
    
    /**
     * Delete client.
     * 
     * @param id Client ID
     * @return No content (HTTP 204)
     * @throws NotFoundException if client not found
     * 
     * Example:
     * DELETE /api/clients/1
     * 
     * Response: 204 No Content
     * @deprecated Use {@link com.bank.api.v2.ClientResourceV2#deleteClient(Long)} instead
     */
    @DELETE
    @Path("/{id}")
    @Deprecated(since = "1.0", forRemoval = true)
    public Response deleteClient(@PathParam("id") Long id) {
        logger.info("REST V1 (DEPRECATED): Deleting client with ID: " + id);
        
        boolean deleted = clientService.delete(id);
        
        if (!deleted) {
            throw new NotFoundException("Client with ID " + id + " not found");
        }
        
        return Response.noContent()
            .header("X-API-Version", "1.0")
            .header("X-API-Deprecated", "true")
            .header("X-API-Sunset-Date", "2026-06-01")
            .build();
    }
    
    /**
     * Search clients by name.
     * 
     * @param name Name to search for (optional)
     * @return List of matching clients
     * 
     * Example:
     * GET /api/clients/search?name=John
     * 
     * Response: 200 OK
     * [{"id": 1, "name": "John Doe", "email": "john@example.com", "premium": false}]
     * @deprecated Use {@link com.bank.api.v2.ClientResourceV2#searchClients(String)} instead
     */
    @GET
    @Path("/search")
    @Deprecated(since = "1.0", forRemoval = true)
    public Response searchClients(@QueryParam("name") String name) {
        logger.info("REST V1 (DEPRECATED): Searching clients by name: " + name);
        
        List<Client> clients;
        if (name == null || name.trim().isEmpty()) {
            clients = clientService.findAll();
        } else {
            clients = clientService.findByName(name);
        }
        
        return Response.ok(clients)
            .header("X-API-Version", "1.0")
            .header("X-API-Deprecated", "true")
            .header("X-API-Sunset-Date", "2026-06-01")
            .build();
    }
    
    /**
     * Get count of all clients.
     * 
     * @return Count object with total number
     * 
     * Example:
     * GET /api/clients/count
     * 
     * Response: 200 OK
     * {"count": 42}
     * @deprecated Use {@link com.bank.api.v2.ClientResourceV2#getClientCount()} instead
     */
    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated(since = "1.0", forRemoval = true)
    public Response getClientCount() {
        logger.info("REST: Getting client count");
        
        long count = clientService.count();
        
        return Response.ok()
            .entity("{\"count\": " + count + "}")
            .header("X-API-Version", "1.0")
            .header("X-API-Deprecated", "true")
            .header("X-API-Sunset-Date", "2026-06-01")
            .build();
    }
}

// Made with Bob
