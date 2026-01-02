/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.port.out;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.model.Client;
import com.bank.domain.valueobject.Email;

import java.util.List;
import java.util.Optional;

/**
 * Secondary port (driven) - Repository interface for Client aggregate.
 * Defined by the application layer, implemented by infrastructure layer.
 * Uses domain objects only - no infrastructure concerns.
 */
public interface ClientRepository {
    
    /**
     * Find a client by its ID
     */
    Optional<Client> findById(Long id);
    
    /**
     * Find a client by email
     */
    Optional<Client> findByEmail(Email email);
    
    /**
     * Find all clients
     */
    List<Client> findAll();
    
    /**
     * Find premium clients only
     */
    List<Client> findPremiumClients();
    
    /**
     * Save a client (create or update)
     */
    void save(Client client);
    
    /**
     * Delete a client
     */
    void delete(Client client);
    
    /**
     * Check if a client exists by email
     */
    boolean existsByEmail(Email email);
}

// Made with Bob
