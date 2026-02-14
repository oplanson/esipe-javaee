package com.bank.application.dto;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Client;

/**
 * ClientDTO for transferring client data between layers.
 *
 * DDD Pattern: Data Transfer Object (DTO)
 * - Separates domain model from presentation/API layer
 * - Provides a stable interface for external consumers
 * - Prevents exposing domain model internals
 * - Simplifies serialization (JSON, XML)
 *
 * Refactored to Java Record (JDK 17+):
 * - Immutable by design
 * - Concise syntax (no boilerplate)
 * - Automatic equals/hashCode/toString
 * - Perfect for DTOs (data carriers)
 */
public record ClientDTO(
    Long id,
    String name,
    String email,
    boolean premium,
    int accountCount
) {
    
    /**
     * Create DTO from Client entity.
     *
     * @param client The client entity
     * @return ClientDTO
     */
    public static ClientDTO fromEntity(Client client) {
        if (client == null) {
            return null;
        }
        
        return new ClientDTO(
            client.getId(),
            client.getName(),
            client.getEmail() != null ? client.getEmail().getValue() : null,
            client.isPremium(),
            client.getAccountCount()
        );
    }
    
    /**
     * Get masked email for display.
     *
     * @return Masked email
     */
    public String getMaskedEmail() {
        if (email == null || email.isEmpty()) {
            return "***@***.***";
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return "***@***.***";
        }
        
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***" + domain;
        }
        
        return localPart.substring(0, 2) + "***" + domain;
    }
}

// Made with Bob