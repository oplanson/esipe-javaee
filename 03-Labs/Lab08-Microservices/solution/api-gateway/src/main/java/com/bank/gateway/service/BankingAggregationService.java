// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.service;

import com.bank.gateway.client.AccountServiceClient;
import com.bank.gateway.client.ClientServiceClient;
import com.bank.gateway.dto.AccountDTO;
import com.bank.gateway.dto.ClientDTO;
import com.bank.gateway.dto.ClientWithAccountsDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service that aggregates data from multiple microservices
 * This is the core of the BFF (Backend For Frontend) pattern
 */
@ApplicationScoped
public class BankingAggregationService {
    
    private static final Logger LOGGER = Logger.getLogger(BankingAggregationService.class.getName());
    
    @Inject
    @RestClient
    private ClientServiceClient clientServiceClient;
    
    @Inject
    @RestClient
    private AccountServiceClient accountServiceClient;
    
    /**
     * Default constructor for CDI
     */
    public BankingAggregationService() {
    }
    
    /**
     * Constructor for programmatic instantiation (used by servlets)
     */
    public BankingAggregationService(ClientServiceClient clientServiceClient,
                                     AccountServiceClient accountServiceClient) {
        this.clientServiceClient = clientServiceClient;
        this.accountServiceClient = accountServiceClient;
    }
    
    /**
     * Get client with all their accounts (aggregated data)
     * This demonstrates the BFF pattern - combining data from multiple services
     */
    @Retry(maxRetries = 2, delay = 1000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Bulkhead(value = 10)
    @Fallback(fallbackMethod = "getClientWithAccountsFallback")
    public ClientWithAccountsDTO getClientWithAccounts(Long clientId) {
        LOGGER.info("Aggregating data for client: " + clientId);
        
        try {
            // Call Client Service
            ClientDTO client = clientServiceClient.getClientById(clientId);
            
            // Call Account Service
            List<AccountDTO> accounts = accountServiceClient.getAccountsByClientId(clientId);
            
            // Aggregate the data
            ClientWithAccountsDTO result = new ClientWithAccountsDTO(client, accounts);
            
            LOGGER.info("Successfully aggregated data for client " + clientId + 
                       " with " + accounts.size() + " accounts");
            
            return result;
            
        } catch (Exception e) {
            LOGGER.severe("Error aggregating data for client " + clientId + ": " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get all clients with their accounts
     */
    @Retry(maxRetries = 2, delay = 1000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Bulkhead(value = 10)
    @Fallback(fallbackMethod = "getAllClientsWithAccountsFallback")
    public List<ClientWithAccountsDTO> getAllClientsWithAccounts() {
        LOGGER.info("Aggregating data for all clients");
        
        try {
            // Call Client Service
            List<ClientDTO> clients = clientServiceClient.getAllClients();
            
            // For each client, get their accounts
            List<ClientWithAccountsDTO> results = new ArrayList<>();
            for (ClientDTO client : clients) {
                try {
                    List<AccountDTO> accounts = accountServiceClient.getAccountsByClientId(client.getId());
                    results.add(new ClientWithAccountsDTO(client, accounts));
                } catch (Exception e) {
                    LOGGER.warning("Failed to get accounts for client " + client.getId() + ": " + e.getMessage());
                    // Add client with empty accounts list
                    results.add(new ClientWithAccountsDTO(client, new ArrayList<>()));
                }
            }
            
            LOGGER.info("Successfully aggregated data for " + results.size() + " clients");
            return results;
            
        } catch (Exception e) {
            LOGGER.severe("Error aggregating data for all clients: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Check if both services are available
     */
    public boolean areServicesAvailable() {
        try {
            clientServiceClient.getAllClients();
            accountServiceClient.getAllAccounts();
            return true;
        } catch (Exception e) {
            LOGGER.warning("One or more services are unavailable: " + e.getMessage());
            return false;
        }
    }
    
    // Fallback methods
    
    public ClientWithAccountsDTO getClientWithAccountsFallback(Long clientId) {
        LOGGER.warning("Using fallback for client " + clientId);
        
        // Try to get at least the client data
        try {
            ClientDTO client = clientServiceClient.getClientById(clientId);
            return new ClientWithAccountsDTO(client, new ArrayList<>());
        } catch (Exception e) {
            // Return minimal fallback data
            ClientDTO fallbackClient = new ClientDTO();
            fallbackClient.setId(clientId);
            fallbackClient.setFirstName("Service");
            fallbackClient.setLastName("Unavailable");
            fallbackClient.setEmail("unavailable@service.com");
            return new ClientWithAccountsDTO(fallbackClient, new ArrayList<>());
        }
    }
    
    public List<ClientWithAccountsDTO> getAllClientsWithAccountsFallback() {
        LOGGER.warning("Using fallback for all clients");
        return new ArrayList<>();
    }
}

// Made with Bob
