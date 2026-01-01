package com.bank.api;

import com.bank.exception.NotFoundException;
import com.bank.model.Client;
import com.bank.service.ClientService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST resource for Client operations.
 * Provides CRUD operations via HTTP methods.
 * 
 * Base URL: /api/clients
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 05
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
     */
    @GET
    public List<Client> getAllClients() {
        logger.info("REST: Getting all clients");
        return clientService.findAll();
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
     */
    @GET
    @Path("/{id}")
    public Client getClient(@PathParam("id") Long id) {
        logger.info("REST: Getting client with ID: " + id);
        
        Client client = clientService.findById(id);
        
        if (client == null) {
            throw new NotFoundException("Client with ID " + id + " not found");
        }
        
        return client;
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
     */
    @POST
    public Response createClient(@Valid Client client) {
        logger.info("REST: Creating client: " + client.getName());
        
        Client created = clientService.create(client);
        
        return Response
            .status(Response.Status.CREATED)
            .entity(created)
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
     */
    @PUT
    @Path("/{id}")
    public Client updateClient(@PathParam("id") Long id, @Valid Client client) {
        logger.info("REST: Updating client with ID: " + id);
        
        Client existing = clientService.findById(id);
        
        if (existing == null) {
            throw new NotFoundException("Client with ID " + id + " not found");
        }
        
        client.setId(id);
        return clientService.update(client);
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
     */
    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id) {
        logger.info("REST: Deleting client with ID: " + id);
        
        boolean deleted = clientService.delete(id);
        
        if (!deleted) {
            throw new NotFoundException("Client with ID " + id + " not found");
        }
        
        return Response.noContent().build();
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
     */
    @GET
    @Path("/search")
    public List<Client> searchClients(@QueryParam("name") String name) {
        logger.info("REST: Searching clients by name: " + name);
        
        if (name == null || name.trim().isEmpty()) {
            return clientService.findAll();
        }
        
        return clientService.findByName(name);
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
     */
    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientCount() {
        logger.info("REST: Getting client count");
        
        long count = clientService.count();
        
        return Response.ok()
            .entity("{\"count\": " + count + "}")
            .build();
    }
}

// Made with Bob
