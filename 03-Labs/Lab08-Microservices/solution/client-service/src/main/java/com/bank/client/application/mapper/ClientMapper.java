// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.client.application.mapper;

import com.bank.client.application.dto.ClientDTO;
import com.bank.client.domain.model.Client;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper to convert between Client domain model and ClientDTO
 */
@ApplicationScoped
public class ClientMapper {
    
    /**
     * Convert Client domain model to ClientDTO
     * @param client The domain model
     * @return The DTO
     */
    public ClientDTO toDTO(Client client) {
        if (client == null) {
            return null;
        }
        
        return new ClientDTO(
            client.getId(),
            client.getFirstName(),
            client.getLastName(),
            client.getEmail(),
            client.getPhone(),
            client.getAddress(),
            client.isPremium(),
            client.getCreatedAt(),
            client.getUpdatedAt()
        );
    }
    
    /**
     * Convert ClientDTO to Client domain model
     * @param dto The DTO
     * @return The domain model
     */
    public Client toDomain(ClientDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return new Client(
            dto.getId(),
            dto.getFirstName(),
            dto.getLastName(),
            dto.getEmail(),
            dto.getPhone(),
            dto.getAddress(),
            dto.isPremium(),
            dto.getCreatedAt(),
            dto.getUpdatedAt()
        );
    }
    
    /**
     * Update existing Client domain model from ClientDTO
     * @param client The existing domain model
     * @param dto The DTO with updated data
     */
    public void updateFromDTO(Client client, ClientDTO dto) {
        if (client == null || dto == null) {
            return;
        }
        
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setAddress(dto.getAddress());
        client.setPremium(dto.isPremium());
    }
}

// Made with Bob
