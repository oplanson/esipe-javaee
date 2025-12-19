package com.bank.service;

import com.bank.model.Client;
import com.bank.model.Account;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing clients.
 * Uses in-memory storage (will be replaced with database in Lab 3).
 * Complete implementation with all CRUD operations.
 */
public class ClientService {
    
    // In-memory storage
    private Map<Long, Client> clients = new HashMap<>();
    private Long nextId = 1L;
    
    /**
     * Retrieve all clients.
     * 
     * @return List of all clients
     */
    public List<Client> findAll() {
        return new ArrayList<>(clients.values());
    }
    
    /**
     * Find a client by ID.
     * 
     * @param id The client ID
     * @return The client if found, null otherwise
     */
    public Client findById(Long id) {
        return clients.get(id);
    }
    
    /**
     * Create a new client.
     * 
     * @param client The client to create
     * @return The created client with ID set
     */
    public Client create(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        
        // Generate new ID
        Long id = nextId++;
        client.setId(id);
        
        // Save client
        clients.put(id, client);
        
        return client;
    }
    
    /**
     * Update an existing client.
     * 
     * @param client The client to update
     * @return The updated client, or null if not found
     */
    public Client update(Client client) {
        if (client == null || client.getId() == null) {
            throw new IllegalArgumentException("Client and client ID cannot be null");
        }
        
        // Check if client exists
        if (!clients.containsKey(client.getId())) {
            return null;
        }
        
        // Update client
        clients.put(client.getId(), client);
        
        return client;
    }
    
    /**
     * Delete a client by ID.
     * 
     * @param id The client ID to delete
     * @return true if deleted, false if not found
     */
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        return clients.remove(id) != null;
    }
    
    /**
     * Search clients by name (partial match, case-insensitive).
     * 
     * @param name The name to search for
     * @return List of matching clients
     */
    public List<Client> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = name.toLowerCase();
        
        return clients.values().stream()
                .filter(client -> client.getName() != null && 
                                 client.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    /**
     * Search clients by email (partial match, case-insensitive).
     * 
     * @param email The email to search for
     * @return List of matching clients
     */
    public List<Client> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchTerm = email.toLowerCase();
        
        return clients.values().stream()
                .filter(client -> client.getEmail() != null && 
                                 client.getEmail().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    /**
     * Add an account to a client.
     * 
     * @param clientId The client ID
     * @param account The account to add
     * @return true if added, false if client not found
     */
    public boolean addAccount(Long clientId, Account account) {
        Client client = clients.get(clientId);
        
        if (client == null) {
            return false;
        }
        
        client.addAccount(account);
        return true;
    }
    
    /**
     * Remove an account from a client.
     * 
     * @param clientId The client ID
     * @param account The account to remove
     * @return true if removed, false if client not found
     */
    public boolean removeAccount(Long clientId, Account account) {
        Client client = clients.get(clientId);
        
        if (client == null) {
            return false;
        }
        
        client.removeAccount(account);
        return true;
    }
    
    /**
     * Get the total number of clients.
     * 
     * @return The number of clients
     */
    public int count() {
        return clients.size();
    }
    
    /**
     * Check if a client exists.
     * 
     * @param id The client ID
     * @return true if exists, false otherwise
     */
    public boolean exists(Long id) {
        return clients.containsKey(id);
    }
    
    /**
     * Clear all clients (useful for testing).
     */
    public void clear() {
        clients.clear();
        nextId = 1L;
    }
    
    /**
     * Initialize with sample data.
     */
    public void initializeSampleData() {
        // Create sample clients
        Client client1 = new Client("Jean Dupont", "jean.dupont@example.com");
        create(client1);
        
        // Add accounts to client1
        Account account1 = new Account("FR7612345678901234567890123", 1500.00, "CHECKING", client1.getId());
        account1.setId(1L);
        client1.addAccount(account1);
        
        Account account2 = new Account("FR7698765432109876543210987", 5000.00, "SAVINGS", client1.getId());
        account2.setId(2L);
        client1.addAccount(account2);
        
        Client client2 = new Client("Marie Martin", "marie.martin@example.com");
        create(client2);
        
        // Add account to client2
        Account account3 = new Account("FR7611111111111111111111111", 2500.00, "CHECKING", client2.getId());
        account3.setId(3L);
        client2.addAccount(account3);
        
        Client client3 = new Client("Pierre Durand", "pierre.durand@example.com");
        create(client3);
        
        // Add accounts to client3
        Account account4 = new Account("FR7622222222222222222222222", 3000.00, "CHECKING", client3.getId());
        account4.setId(4L);
        client3.addAccount(account4);
        
        Account account5 = new Account("FR7633333333333333333333333", 10000.00, "SAVINGS", client3.getId());
        account5.setId(5L);
        client3.addAccount(account5);
        
        Client client4 = new Client("Sophie Bernard", "sophie.bernard@example.com");
        create(client4);
        
        Client client5 = new Client("Luc Petit", "luc.petit@example.com");
        create(client5);
        
        // Add account to client5
        Account account6 = new Account("FR7644444444444444444444444", 750.00, "CHECKING", client5.getId());
        account6.setId(6L);
        client5.addAccount(account6);
    }
}

// Made with Bob