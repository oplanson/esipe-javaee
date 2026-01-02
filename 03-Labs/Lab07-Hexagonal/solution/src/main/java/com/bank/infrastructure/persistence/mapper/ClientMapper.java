/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.persistence.mapper;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.model.Client;
import com.bank.domain.valueobject.Email;
import com.bank.infrastructure.persistence.entity.ClientEntity;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper between Client domain object and ClientEntity JPA entity.
 * 
 * Hexagonal Architecture: Infrastructure Layer
 * - Part of the secondary adapter (JPA adapter)
 * - Bidirectional conversion between domain and persistence
 * - Handles value object conversion
 * - No business logic, only mapping
 */
@ApplicationScoped
public class ClientMapper {
    
    /**
     * Convert JPA entity to domain object.
     * 
     * @param entity The JPA entity
     * @return Domain Client
     */
    public Client toDomain(ClientEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Client client = new Client(
            entity.getId(),
            entity.getName(),
            Email.of(entity.getEmail()),
            entity.isPremium()
        );
        
        return client;
    }
    
    /**
     * Convert domain object to JPA entity.
     * 
     * @param client The domain Client
     * @return JPA ClientEntity
     */
    public ClientEntity toEntity(Client client) {
        if (client == null) {
            return null;
        }
        
        ClientEntity entity = new ClientEntity();
        entity.setId(client.getId());
        entity.setName(client.getName());
        entity.setEmail(client.getEmail().getValue());
        entity.setPremium(client.isPremium());
        
        return entity;
    }
    
    /**
     * Update existing entity from domain object.
     * Used for updates to preserve JPA managed state.
     * 
     * @param client The domain Client
     * @param entity The existing JPA entity
     */
    public void updateEntity(Client client, ClientEntity entity) {
        if (client == null || entity == null) {
            return;
        }
        
        entity.setName(client.getName());
        entity.setEmail(client.getEmail().getValue());
        entity.setPremium(client.isPremium());
    }
}

// Made with Bob
