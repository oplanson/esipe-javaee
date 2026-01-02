/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.persistence.adapter;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.port.out.ClientRepository;
import com.bank.domain.model.Client;
import com.bank.domain.valueobject.Email;
import com.bank.infrastructure.persistence.entity.ClientEntity;
import com.bank.infrastructure.persistence.mapper.ClientMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA implementation of ClientRepository port.
 * 
 * Hexagonal Architecture: Secondary Adapter (Driven Adapter)
 * - Implements secondary port (ClientRepository)
 * - Handles all JPA/database concerns
 * - Uses mapper to convert between domain and persistence
 * - Isolated from domain and application layers
 */
@ApplicationScoped
public class JpaClientAdapter implements ClientRepository {
    
    @Inject
    private EntityManager entityManager;
    
    @Inject
    private ClientMapper mapper;
    
    @Override
    public Optional<Client> findById(Long id) {
        ClientEntity entity = entityManager.find(ClientEntity.class, id);
        return Optional.ofNullable(mapper.toDomain(entity));
    }
    
    @Override
    public List<Client> findAll() {
        TypedQuery<ClientEntity> query = entityManager.createQuery(
            "SELECT c FROM ClientEntity c ORDER BY c.name",
            ClientEntity.class
        );
        
        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void save(Client client) {
        if (client.getId() == null) {
            // New client - persist
            ClientEntity entity = mapper.toEntity(client);
            entityManager.persist(entity);
            // Force flush to generate ID immediately
            entityManager.flush();
            // Update domain object with generated ID
            client.setId(entity.getId());
        } else {
            // Existing client - merge
            ClientEntity existingEntity = entityManager.find(ClientEntity.class, client.getId());
            if (existingEntity != null) {
                mapper.updateEntity(client, existingEntity);
                entityManager.merge(existingEntity);
            } else {
                // Entity was removed, persist as new
                ClientEntity entity = mapper.toEntity(client);
                entityManager.persist(entity);
                entityManager.flush();
                client.setId(entity.getId());
            }
        }
    }
    
    @Override
    public void delete(Client client) {
        if (client.getId() != null) {
            ClientEntity entity = entityManager.find(ClientEntity.class, client.getId());
            if (entity != null) {
                entityManager.remove(entity);
            }
        }
    }
    
    @Override
    public Optional<Client> findByEmail(Email email) {
        try {
            TypedQuery<ClientEntity> query = entityManager.createQuery(
                "SELECT c FROM ClientEntity c WHERE c.email = :email",
                ClientEntity.class
            );
            query.setParameter("email", email.getValue());
            ClientEntity entity = query.getSingleResult();
            return Optional.of(mapper.toDomain(entity));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Client> findPremiumClients() {
        TypedQuery<ClientEntity> query = entityManager.createQuery(
            "SELECT c FROM ClientEntity c WHERE c.premium = true ORDER BY c.name",
            ClientEntity.class
        );
        
        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM ClientEntity c WHERE c.email = :email",
            Long.class
        );
        query.setParameter("email", email.getValue());
        return query.getSingleResult() > 0;
    }
}

// Made with Bob
