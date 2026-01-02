/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.rest.adapter.v2;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.command.CreateClientCommand;
import com.bank.application.command.UpdateClientCommand;
import com.bank.application.dto.ClientDTO;
import com.bank.application.port.in.ClientManagementUseCase;
import com.bank.domain.valueobject.Email;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * REST adapter for Client operations - Version 2.
 *
 * API V2: Enhanced client operations with additional features
 * - Premium client management
 * - Email value object validation
 *
 * Hexagonal Architecture: Primary Adapter (Driving Adapter)
 * - Receives HTTP requests
 * - Converts REST requests to use case commands
 * - Delegates to use cases (primary ports)
 * - Returns DTOs as JSON responses
 * - Isolated from domain and application logic
 */
@Path("/v2/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientRestAdapterV2 {
    
    @Inject
    private ClientManagementUseCase clientManagement;
    
    /**
     * Create a new client.
     * POST /api/v2/clients
     */
    @POST
    public Response createClient(CreateClientRequest request) {
        try {
            CreateClientCommand command = new CreateClientCommand(
                request.name,
                Email.of(request.email),
                request.premium != null ? request.premium : false
            );
            
            ClientDTO client = clientManagement.createClient(command);
            return Response.status(Response.Status.CREATED).entity(client).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal server error")).build();
        }
    }
    
    /**
     * Get client by ID.
     * GET /api/v2/clients/{id}
     */
    @GET
    @Path("/{id}")
    public Response getClient(@PathParam("id") Long id) {
        try {
            ClientDTO client = clientManagement.getClient(id);
            return Response.ok(client).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * Get all clients.
     * GET /api/v2/clients
     */
    @GET
    public Response getAllClients() {
        List<ClientDTO> clients = clientManagement.getAllClients();
        return Response.ok(clients).build();
    }
    
    /**
     * Get premium clients only.
     * GET /api/v2/clients/premium
     */
    @GET
    @Path("/premium")
    public Response getPremiumClients() {
        List<ClientDTO> clients = clientManagement.getPremiumClients();
        return Response.ok(clients).build();
    }
    
    /**
     * Upgrade a client to premium.
     * POST /api/v2/clients/{id}/upgrade
     */
    @POST
    @Path("/{id}/upgrade")
    public Response upgradeToPremium(@PathParam("id") Long id) {
        try {
            clientManagement.upgradeToPremium(id);
            ClientDTO client = clientManagement.getClient(id);
            return Response.ok(client).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * Downgrade a client from premium.
     * POST /api/v2/clients/{id}/downgrade
     */
    @POST
    @Path("/{id}/downgrade")
    public Response downgradeFromPremium(@PathParam("id") Long id) {
        try {
            clientManagement.downgradeFromPremium(id);
            ClientDTO client = clientManagement.getClient(id);
            return Response.ok(client).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    /**
     * Update a client.
     * PUT /api/v2/clients/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateClient(@PathParam("id") Long id, UpdateClientRequest request) {
        try {
            UpdateClientCommand command = new UpdateClientCommand(
                id,
                request.name,
                Email.of(request.email),
                request.premium
            );
            
            ClientDTO client = clientManagement.updateClient(id, command);
            return Response.ok(client).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal server error")).build();
        }
    }
    
    /**
     * Delete a client.
     * DELETE /api/v2/clients/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id) {
        try {
            clientManagement.deleteClient(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(e.getMessage())).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    
    // Request/Response DTOs for REST API
    
    public static class CreateClientRequest {
        public String name;
        public String email;
        public Boolean premium;
    }
    
    public static class UpdateClientRequest {
        public String name;
        public String email;
        public Boolean premium;
    }
    
    public static class ErrorResponse {
        public String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}

// Made with Bob
