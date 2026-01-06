// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.api;

import com.bank.gateway.client.ClientServiceClient;
import com.bank.gateway.dto.ClientDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.logging.Logger;

/**
 * Proxy Resource for Client Service
 * Forwards requests to the Client microservice
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientProxyResource {
    
    private static final Logger LOGGER = Logger.getLogger(ClientProxyResource.class.getName());
    
    @Inject
    @RestClient
    private ClientServiceClient clientServiceClient;
    
    @GET
    public Response getAllClients() {
        LOGGER.info("Proxying GET /clients to Client Service");
        try {
            System.out.println("ClientProxyResource : getAllClients : Proxying GET /clients to Client Service");
            List<ClientDTO> clients = clientServiceClient.getAllClients();
            return Response.ok(clients).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Client Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Client Service unavailable\"}")
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getClientById(@PathParam("id") Long id) {
        LOGGER.info("Proxying GET /clients/" + id + " to Client Service");
        try {
            ClientDTO client = clientServiceClient.getClientById(id);
            return Response.ok(client).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Client Service: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Client not found or service unavailable\"}")
                    .build();
        }
    }
    
    @POST
    public Response createClient(ClientDTO client) {
        LOGGER.info("Proxying POST /clients to Client Service");
        try {
            ClientDTO created = clientServiceClient.createClient(client);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Client Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Client Service unavailable\"}")
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateClient(@PathParam("id") Long id, ClientDTO client) {
        LOGGER.info("Proxying PUT /clients/" + id + " to Client Service");
        try {
            ClientDTO updated = clientServiceClient.updateClient(id, client);
            return Response.ok(updated).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Client Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Client Service unavailable\"}")
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id) {
        LOGGER.info("Proxying DELETE /clients/" + id + " to Client Service");
        try {
            clientServiceClient.deleteClient(id);
            return Response.noContent().build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Client Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Client Service unavailable\"}")
                    .build();
        }
    }
}

// Made with Bob