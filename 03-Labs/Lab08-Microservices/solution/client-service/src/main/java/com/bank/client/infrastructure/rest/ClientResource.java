// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.client.infrastructure.rest;

import com.bank.client.application.dto.ClientDTO;
import com.bank.client.application.service.ClientService;
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
 * REST Resource for Client operations
 * Exposes client management endpoints
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Client Management", description = "Operations for managing banking clients")
public class ClientResource {
    
    private static final Logger LOGGER = Logger.getLogger(ClientResource.class.getName());
    
    @Inject
    private ClientService clientService;
    
    @POST
    @Operation(summary = "Create a new client", description = "Creates a new banking client")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Client created successfully",
                     content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @APIResponse(responseCode = "400", description = "Invalid client data"),
        @APIResponse(responseCode = "409", description = "Client with email already exists")
    })
    public Response createClient(@Valid ClientDTO clientDTO) {
        try {
            LOGGER.info("REST: Creating new client");
            ClientDTO created = clientService.createClient(clientDTO);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("REST: Failed to create client: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Get client by ID", description = "Retrieves a client by their ID")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Client found",
                     content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @APIResponse(responseCode = "404", description = "Client not found")
    })
    public Response getClientById(
            @Parameter(description = "Client ID", required = true)
            @PathParam("id") Long id) {
        LOGGER.info("REST: Fetching client with ID: " + id);
        return clientService.getClientById(id)
                .map(client -> Response.ok(client).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Client not found with ID: " + id))
                        .build());
    }
    
    @GET
    @Operation(summary = "Get all clients", description = "Retrieves all banking clients")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Clients retrieved successfully",
                     content = @Content(schema = @Schema(implementation = ClientDTO.class)))
    })
    public Response getAllClients(
            @Parameter(description = "Filter by premium status")
            @QueryParam("premium") Boolean premium) {
        LOGGER.info("REST: Fetching all clients (premium filter: " + premium + ")");
        
        List<ClientDTO> clients;
        if (premium != null && premium) {
            clients = clientService.getAllPremiumClients();
        } else {
            clients = clientService.getAllClients();
        }
        
        return Response.ok(clients).build();
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update client", description = "Updates an existing client")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Client updated successfully",
                     content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @APIResponse(responseCode = "400", description = "Invalid client data"),
        @APIResponse(responseCode = "404", description = "Client not found")
    })
    public Response updateClient(
            @Parameter(description = "Client ID", required = true)
            @PathParam("id") Long id,
            @Valid ClientDTO clientDTO) {
        try {
            LOGGER.info("REST: Updating client with ID: " + id);
            ClientDTO updated = clientService.updateClient(id, clientDTO);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("REST: Failed to update client: " + e.getMessage());
            if (e.getMessage().contains("not found")) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse(e.getMessage()))
                        .build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}/premium")
    @Operation(summary = "Upgrade to premium", description = "Upgrades a client to premium status")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Client upgraded successfully",
                     content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @APIResponse(responseCode = "404", description = "Client not found")
    })
    public Response upgradeToPremium(
            @Parameter(description = "Client ID", required = true)
            @PathParam("id") Long id) {
        try {
            LOGGER.info("REST: Upgrading client to premium: " + id);
            ClientDTO updated = clientService.upgradeToPremium(id);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("REST: Failed to upgrade client: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}/premium")
    @Operation(summary = "Downgrade from premium", description = "Downgrades a client from premium status")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Client downgraded successfully",
                     content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @APIResponse(responseCode = "404", description = "Client not found")
    })
    public Response downgradeFromPremium(
            @Parameter(description = "Client ID", required = true)
            @PathParam("id") Long id) {
        try {
            LOGGER.info("REST: Downgrading client from premium: " + id);
            ClientDTO updated = clientService.downgradeFromPremium(id);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("REST: Failed to downgrade client: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete client", description = "Deletes a client by ID")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Client deleted successfully"),
        @APIResponse(responseCode = "404", description = "Client not found")
    })
    public Response deleteClient(
            @Parameter(description = "Client ID", required = true)
            @PathParam("id") Long id) {
        try {
            LOGGER.info("REST: Deleting client with ID: " + id);
            clientService.deleteClient(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("REST: Failed to delete client: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/stats")
    @Operation(summary = "Get client statistics", description = "Retrieves statistics about clients")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public Response getStatistics() {
        LOGGER.info("REST: Fetching client statistics");
        
        long totalClients = clientService.getTotalClients();
        long premiumClients = clientService.getPremiumClientsCount();
        
        return Response.ok(new ClientStats(totalClients, premiumClients)).build();
    }
    
    // Inner classes for responses
    
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
    
    public static class ClientStats {
        private long totalClients;
        private long premiumClients;
        
        public ClientStats() {}
        
        public ClientStats(long totalClients, long premiumClients) {
            this.totalClients = totalClients;
            this.premiumClients = premiumClients;
        }
        
        public long getTotalClients() {
            return totalClients;
        }
        
        public void setTotalClients(long totalClients) {
            this.totalClients = totalClients;
        }
        
        public long getPremiumClients() {
            return premiumClients;
        }
        
        public void setPremiumClients(long premiumClients) {
            this.premiumClients = premiumClients;
        }
    }
}

// Made with Bob
