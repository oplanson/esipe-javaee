/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.port.in;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.command.CreateClientCommand;
import com.bank.application.command.UpdateClientCommand;
import com.bank.application.dto.ClientDTO;
import com.bank.domain.valueobject.Email;

import java.util.List;

/**
 * Primary port (driving) - Client management use case interface.
 * Defines what the application can do regarding client management.
 */
public interface ClientManagementUseCase {
    
    /**
     * Create a new client
     */
    ClientDTO createClient(CreateClientCommand command);
    
    /**
     * Update client information
     */
    ClientDTO updateClient(Long clientId, UpdateClientCommand command);
    
    /**
     * Delete a client
     */
    void deleteClient(Long clientId);
    
    /**
     * Get client details by ID
     */
    ClientDTO getClient(Long clientId);
    
    /**
     * Get client by email
     */
    ClientDTO getClientByEmail(Email email);
    
    /**
     * Get all clients
     */
    List<ClientDTO> getAllClients();
    
    /**
     * Get premium clients only
     */
    List<ClientDTO> getPremiumClients();
    
    /**
     * Upgrade client to premium
     */
    void upgradeToPremium(Long clientId);
    
    /**
     * Downgrade client from premium
     */
    void downgradeFromPremium(Long clientId);
}

// Made with Bob
