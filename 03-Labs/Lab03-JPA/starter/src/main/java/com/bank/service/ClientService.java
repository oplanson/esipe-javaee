package com.bank.service;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Client;
import com.bank.model.Account;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing clients.
 * Uses in-memory storage (will be replaced with database in Lab 3).
 * 
 * TODO: Implement all CRUD operations:
 * 1. findAll() - retrieve all clients
 * 2. findById(Long id) - find client by ID
 * 3. create(Client client) - add new client
 * 4. update(Client client) - update existing client
 * 5. delete(Long id) - remove client
 * 6. findByName(String name) - search clients by name
 */
public class ClientService {
    
    // In-memory storage
    private Map<Long, Client> clients = new HashMap<>();
    private Long nextId = 1L;
    
    /**
     * Retrieve all clients.
     * 
     * TODO: Implement this method
     * Hint: Use clients.values() and convert to List
     * 
     * @return List of all clients
     */
    public List<Client> findAll() {
        // TODO: Return all clients as a list
        return null;
    }
    
    /**
     * Find a client by ID.
     * 
     * TODO: Implement this method
     * Hint: Use clients.get(id)
     * 
     * @param id The client ID
     * @return The client if found, null otherwise
     */
    public Client findById(Long id) {
        // TODO: Return client with given ID
        return null;
    }
    
    /**
     * Create a new client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Generate new ID using nextId++
     * 2. Set the ID on the client
     * 3. Add client to the map
     * 4. Return the created client
     * 
     * @param client The client to create
     * @return The created client with ID set
     */
    public Client create(Client client) {
        // TODO: Generate ID and save client
        return null;
    }
    
    /**
     * Update an existing client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Check if client exists
     * 2. Update the client in the map
     * 3. Return the updated client
     * 
     * @param client The client to update
     * @return The updated client, or null if not found
     */
    public Client update(Client client) {
        // TODO: Update client if exists
        return null;
    }
    
    /**
     * Delete a client by ID.
     * 
     * TODO: Implement this method
     * Hint: Use clients.remove(id)
     * 
     * @param id The client ID to delete
     * @return true if deleted, false if not found
     */
    public boolean delete(Long id) {
        // TODO: Remove client from map
        return false;
    }
    
    /**
     * Search clients by name (partial match, case-insensitive).
     * 
     * TODO: Implement this method
     * Hint: Use stream() and filter() with contains()
     * 
     * @param name The name to search for
     * @return List of matching clients
     */
    public List<Client> findByName(String name) {
        // TODO: Filter clients by name
        return null;
    }
    
    /**
     * Add an account to a client.
     * 
     * TODO: Implement this method (optional)
     * 
     * @param clientId The client ID
     * @param account The account to add
     * @return true if added, false if client not found
     */
    public boolean addAccount(Long clientId, Account account) {
        // TODO: Add account to client's account list
        return false;
    }
    
    /**
     * Get the total number of clients.
     * 
     * @return The number of clients
     */
    public int count() {
        return clients.size();
    }
}

// Made with Bob
