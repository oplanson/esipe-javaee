package com.bank.client;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Client;
import com.bank.model.Account;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Type-safe REST client for Banking API.
 * Uses MicroProfile Rest Client for declarative REST calls.
 * 
 * Configuration in microprofile-config.properties:
 * banking-api/mp-rest/url=http://localhost:9080
 * 
 * @author Banking Application Team
 * @version 1.0
 * @since Lab 06
 */
@Path("/api")
@RegisterRestClient(configKey = "banking-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BankingRestClient {
    
    // ========================================
    // Client Operations
    // ========================================
    
    /**
     * Get all clients.
     * 
     * @return List of all clients
     */
    @GET
    @Path("/clients")
    List<Client> getAllClients();
    
    /**
     * Get client by ID.
     * 
     * @param id Client ID
     * @return Client object
     */
    @GET
    @Path("/clients/{id}")
    Client getClient(@PathParam("id") Long id);
    
    /**
     * Create new client.
     * 
     * @param client Client data
     * @return Response with created client
     */
    @POST
    @Path("/clients")
    Response createClient(Client client);
    
    /**
     * Update existing client.
     * 
     * @param id Client ID
     * @param client Updated client data
     * @return Response with updated client
     */
    @PUT
    @Path("/clients/{id}")
    Response updateClient(@PathParam("id") Long id, Client client);
    
    /**
     * Delete client.
     * 
     * @param id Client ID
     * @return Response
     */
    @DELETE
    @Path("/clients/{id}")
    Response deleteClient(@PathParam("id") Long id);
    
    /**
     * Search clients by name.
     * 
     * @param name Name to search for
     * @return List of matching clients
     */
    @GET
    @Path("/clients/search")
    List<Client> searchClients(@QueryParam("name") String name);
    
    // ========================================
    // Account Operations
    // ========================================
    
    /**
     * Get all accounts.
     * 
     * @return List of all accounts
     */
    @GET
    @Path("/accounts")
    List<Account> getAllAccounts();
    
    /**
     * Get account by ID.
     * 
     * @param id Account ID
     * @return Account object
     */
    @GET
    @Path("/accounts/{id}")
    Account getAccount(@PathParam("id") Long id);
    
    /**
     * Create new account.
     * 
     * @param account Account data
     * @return Response with created account
     */
    @POST
    @Path("/accounts")
    Response createAccount(Account account);
    
    /**
     * Update existing account.
     * 
     * @param id Account ID
     * @param account Updated account data
     * @return Response with updated account
     */
    @PUT
    @Path("/accounts/{id}")
    Response updateAccount(@PathParam("id") Long id, Account account);
    
    /**
     * Delete account.
     * 
     * @param id Account ID
     * @return Response
     */
    @DELETE
    @Path("/accounts/{id}")
    Response deleteAccount(@PathParam("id") Long id);
    
    /**
     * Get accounts for a specific client.
     * 
     * @param clientId Client ID
     * @return List of client's accounts
     */
    @GET
    @Path("/accounts/client/{clientId}")
    List<Account> getClientAccounts(@PathParam("clientId") Long clientId);
    
    /**
     * Get accounts by type.
     * 
     * @param type Account type (CHECKING or SAVINGS)
     * @return List of accounts of specified type
     */
    @GET
    @Path("/accounts/type/{type}")
    List<Account> getAccountsByType(@PathParam("type") String type);
    
    /**
     * Deposit money into account.
     * 
     * @param id Account ID
     * @param amount Amount to deposit
     * @return Response with updated account
     */
    @POST
    @Path("/accounts/{id}/deposit")
    Response deposit(@PathParam("id") Long id, @QueryParam("amount") double amount);
    
    /**
     * Withdraw money from account.
     * 
     * @param id Account ID
     * @param amount Amount to withdraw
     * @return Response with updated account
     */
    @POST
    @Path("/accounts/{id}/withdraw")
    Response withdraw(@PathParam("id") Long id, @QueryParam("amount") double amount);
}

// Made with Bob
