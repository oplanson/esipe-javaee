// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.api;

import com.bank.gateway.client.AccountServiceClient;
import com.bank.gateway.dto.AccountDTO;
import com.bank.gateway.dto.TransactionDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.logging.Logger;

/**
 * Proxy Resource for Account Service
 * Forwards requests to the Account microservice
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountProxyResource {
    
    private static final Logger LOGGER = Logger.getLogger(AccountProxyResource.class.getName());
    
    @Inject
    @RestClient
    private AccountServiceClient accountServiceClient;
    
    @GET
    public Response getAllAccounts() {
        LOGGER.info("Proxying GET /accounts to Account Service");
        try {
            List<AccountDTO> accounts = accountServiceClient.getAllAccounts();
            return Response.ok(accounts).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Account Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Account Service unavailable\"}")
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getAccountById(@PathParam("id") Long id) {
        LOGGER.info("Proxying GET /accounts/" + id + " to Account Service");
        try {
            AccountDTO account = accountServiceClient.getAccountById(id);
            return Response.ok(account).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Account Service: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Account not found or service unavailable\"}")
                    .build();
        }
    }
    
    @GET
    @Path("/client/{clientId}")
    public Response getAccountsByClientId(@PathParam("clientId") Long clientId) {
        LOGGER.info("Proxying GET /accounts/client/" + clientId + " to Account Service");
        try {
            List<AccountDTO> accounts = accountServiceClient.getAccountsByClientId(clientId);
            return Response.ok(accounts).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Account Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Account Service unavailable\"}")
                    .build();
        }
    }
    
    @POST
    public Response createAccount(AccountDTO account) {
        LOGGER.info("Proxying POST /accounts to Account Service");
        try {
            AccountDTO created = accountServiceClient.createAccount(account);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Account Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Account Service unavailable\"}")
                    .build();
        }
    }
    
    @POST
    @Path("/{id}/deposit")
    public Response deposit(@PathParam("id") Long id, TransactionDTO transaction) {
        LOGGER.info("Proxying POST /accounts/" + id + "/deposit to Account Service");
        try {
            AccountDTO account = accountServiceClient.deposit(id, transaction);
            return Response.ok(account).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Account Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Account Service unavailable\"}")
                    .build();
        }
    }
    
    @POST
    @Path("/{id}/withdraw")
    public Response withdraw(@PathParam("id") Long id, TransactionDTO transaction) {
        LOGGER.info("Proxying POST /accounts/" + id + "/withdraw to Account Service");
        try {
            AccountDTO account = accountServiceClient.withdraw(id, transaction);
            return Response.ok(account).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Account Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Account Service unavailable\"}")
                    .build();
        }
    }
    
    @POST
    @Path("/{fromId}/transfer/{toId}")
    public Response transfer(@PathParam("fromId") Long fromId, @PathParam("toId") Long toId, TransactionDTO transaction) {
        LOGGER.info("Proxying POST /accounts/" + fromId + "/transfer/" + toId + " to Account Service");
        try {
            AccountDTO account = accountServiceClient.transfer(fromId, toId, transaction);
            return Response.ok(account).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Account Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Account Service unavailable\"}")
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}/suspend")
    public Response suspendAccount(@PathParam("id") Long id) {
        LOGGER.info("Proxying PUT /accounts/" + id + "/suspend to Account Service");
        try {
            AccountDTO account = accountServiceClient.suspendAccount(id);
            return Response.ok(account).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Account Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Account Service unavailable\"}")
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}/activate")
    public Response activateAccount(@PathParam("id") Long id) {
        LOGGER.info("Proxying PUT /accounts/" + id + "/activate to Account Service");
        try {
            AccountDTO account = accountServiceClient.activateAccount(id);
            return Response.ok(account).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Account Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Account Service unavailable\"}")
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}/close")
    public Response closeAccount(@PathParam("id") Long id) {
        LOGGER.info("Proxying PUT /accounts/" + id + "/close to Account Service");
        try {
            AccountDTO account = accountServiceClient.closeAccount(id);
            return Response.ok(account).build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Account Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Account Service unavailable\"}")
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteAccount(@PathParam("id") Long id) {
        LOGGER.info("Proxying DELETE /accounts/" + id + " to Account Service");
        try {
            accountServiceClient.deleteAccount(id);
            return Response.noContent().build();
        } catch (Exception e) {
            LOGGER.severe("Error proxying to Account Service: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Account Service unavailable\"}")
                    .build();
        }
    }
}

// Made with Bob