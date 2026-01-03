// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.client.domain.port;

import com.bank.client.domain.model.Client;
import java.util.List;
import java.util.Optional;

/**
 * Client Repository Port (Interface)
 * Defines the contract for client persistence operations
 * This is a domain interface that will be implemented by infrastructure layer
 */
public interface ClientRepository {
    
    /**
     * Save a new client or update an existing one
     * @param client The client to save
     * @return The saved client with generated ID
     */
    Client save(Client client);
    
    /**
     * Find a client by ID
     * @param id The client ID
     * @return Optional containing the client if found
     */
    Optional<Client> findById(Long id);
    
    /**
     * Find a client by email
     * @param email The client email
     * @return Optional containing the client if found
     */
    Optional<Client> findByEmail(String email);
    
    /**
     * Find all clients
     * @return List of all clients
     */
    List<Client> findAll();
    
    /**
     * Find all premium clients
     * @return List of premium clients
     */
    List<Client> findAllPremium();
    
    /**
     * Delete a client by ID
     * @param id The client ID
     */
    void deleteById(Long id);
    
    /**
     * Check if a client exists by ID
     * @param id The client ID
     * @return true if client exists, false otherwise
     */
    boolean existsById(Long id);
    
    /**
     * Check if a client exists by email
     * @param email The client email
     * @return true if client exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Count total number of clients
     * @return Total number of clients
     */
    long count();
    
    /**
     * Count number of premium clients
     * @return Number of premium clients
     */
    long countPremium();
}

// Made with Bob
