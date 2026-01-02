/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.rest.adapter.v1;

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
 * REST adapter for Client operations - Version 1.
 *
 * API V1: Standard client operations
 *
 * @deprecated Use V2 API instead ({@link com.bank.infrastructure.rest.adapter.v2.ClientRestAdapterV2})
 *             V1 will be removed in a future release. Migrate to /api/v2/clients
 *             V2 includes premium client features not available in V1.
 *
 * Hexagonal Architecture: Primary Adapter (Driving Adapter)
 * - Receives HTTP requests
 * - Converts REST requests to use case commands
 * - Delegates to use cases (primary ports)
 * - Returns DTOs as JSON responses
 * - Isolated from domain and application logic
 */
@Deprecated
@Path("/v1/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientRestAdapter {
    
    @Inject
    private ClientManagementUseCase clientManagement;
    
    /**
     * Create a new client.
     * POST /api/v1/clients
     *
     * @deprecated Use POST /api/v2/clients instead
     */
    @Deprecated
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
     * GET /api/v1/clients/{id}
     *
     * @deprecated Use GET /api/v2/clients/{id} instead
     */
    @Deprecated
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
     * GET /api/v1/clients
     *
     * @deprecated Use GET /api/v2/clients instead
     */
    @Deprecated
    @GET
    public Response getAllClients() {
        List<ClientDTO> clients = clientManagement.getAllClients();
        return Response.ok(clients).build();
    }
    
    /**
     * Update a client.
     * PUT /api/v1/clients/{id}
     *
     * @deprecated Use PUT /api/v2/clients/{id} instead
     */
    @Deprecated
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
     * DELETE /api/v1/clients/{id}
     *
     * @deprecated Use DELETE /api/v2/clients/{id} instead
     */
    @Deprecated
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
