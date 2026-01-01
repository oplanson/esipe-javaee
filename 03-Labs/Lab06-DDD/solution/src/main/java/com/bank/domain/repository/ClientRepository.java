package com.bank.domain.repository;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.Email;
import com.bank.model.Client;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Client aggregate.
 * 
 * DDD Pattern: Repository
 * - Provides collection-like interface for aggregates
 * - Abstracts persistence mechanism
 * - Part of domain layer (interface)
 * - Implementation in infrastructure layer
 * 
 * This interface defines the contract for persisting and retrieving
 * Client aggregates. It uses domain language and domain types.
 */
public interface ClientRepository {
    
    /**
     * Save or update a client.
     * 
     * @param client The client to save
     * @return The saved client
     */
    Client save(Client client);
    
    /**
     * Find a client by its ID.
     * 
     * @param id The client ID
     * @return Optional containing the client if found
     */
    Optional<Client> findById(Long id);
    
    /**
     * Find a client by email address.
     * 
     * @param email The email address (Value Object)
     * @return Optional containing the client if found
     */
    Optional<Client> findByEmail(Email email);
    
    /**
     * Find all premium clients.
     * 
     * @return List of premium clients
     */
    List<Client> findPremiumClients();
    
    /**
     * Find all standard (non-premium) clients.
     * 
     * @return List of standard clients
     */
    List<Client> findStandardClients();
    
    /**
     * Find all clients.
     * 
     * @return List of all clients
     */
    List<Client> findAll();
    
    /**
     * Find clients with pagination.
     * 
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of clients for the specified page
     */
    List<Client> findAll(int page, int size);
    
    /**
     * Count total number of clients.
     * 
     * @return Total count of clients
     */
    long count();
    
    /**
     * Count premium clients.
     * 
     * @return Number of premium clients
     */
    long countPremiumClients();
    
    /**
     * Delete a client.
     * 
     * @param client The client to delete
     */
    void delete(Client client);
    
    /**
     * Delete a client by ID.
     * 
     * @param id The client ID
     */
    void deleteById(Long id);
    
    /**
     * Check if a client exists by ID.
     * 
     * @param id The client ID
     * @return true if client exists
     */
    boolean existsById(Long id);
    
    /**
     * Check if an email is already in use.
     * 
     * @param email The email to check
     * @return true if email exists
     */
    boolean existsByEmail(Email email);
    
    /**
     * Search clients by name (case-insensitive, partial match).
     * 
     * @param name The name to search for
     * @return List of matching clients
     */
    List<Client> searchByName(String name);
}

// Made with Bob