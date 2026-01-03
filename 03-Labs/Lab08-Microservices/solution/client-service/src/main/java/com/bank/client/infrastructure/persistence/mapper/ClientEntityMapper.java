// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.client.infrastructure.persistence.mapper;

import com.bank.client.domain.model.Client;
import com.bank.client.infrastructure.persistence.entity.ClientEntity;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper to convert between ClientEntity (JPA) and Client (domain model)
 */
@ApplicationScoped
public class ClientEntityMapper {
    
    /**
     * Convert ClientEntity to Client domain model
     * @param entity The JPA entity
     * @return The domain model
     */
    public Client toDomain(ClientEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new Client(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getAddress(),
            entity.isPremium(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    /**
     * Convert Client domain model to ClientEntity
     * @param client The domain model
     * @return The JPA entity
     */
    public ClientEntity toEntity(Client client) {
        if (client == null) {
            return null;
        }
        
        ClientEntity entity = new ClientEntity();
        entity.setId(client.getId());
        entity.setFirstName(client.getFirstName());
        entity.setLastName(client.getLastName());
        entity.setEmail(client.getEmail());
        entity.setPhone(client.getPhone());
        entity.setAddress(client.getAddress());
        entity.setPremium(client.isPremium());
        entity.setCreatedAt(client.getCreatedAt());
        entity.setUpdatedAt(client.getUpdatedAt());
        
        return entity;
    }
    
    /**
     * Update existing ClientEntity from Client domain model
     * @param entity The existing JPA entity
     * @param client The domain model with updated data
     */
    public void updateEntity(ClientEntity entity, Client client) {
        if (entity == null || client == null) {
            return;
        }
        
        entity.setFirstName(client.getFirstName());
        entity.setLastName(client.getLastName());
        entity.setEmail(client.getEmail());
        entity.setPhone(client.getPhone());
        entity.setAddress(client.getAddress());
        entity.setPremium(client.isPremium());
    }
}

// Made with Bob
