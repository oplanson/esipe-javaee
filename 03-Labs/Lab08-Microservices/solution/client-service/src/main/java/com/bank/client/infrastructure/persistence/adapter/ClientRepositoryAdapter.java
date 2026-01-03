// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.client.infrastructure.persistence.adapter;

import com.bank.client.domain.model.Client;
import com.bank.client.domain.port.ClientRepository;
import com.bank.client.infrastructure.persistence.entity.ClientEntity;
import com.bank.client.infrastructure.persistence.mapper.ClientEntityMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA implementation of ClientRepository
 * Adapter that implements the domain port using JPA
 */
@ApplicationScoped
public class ClientRepositoryAdapter implements ClientRepository {
    
    @PersistenceContext(unitName = "clientPU")
    private EntityManager entityManager;
    
    @Inject
    private ClientEntityMapper entityMapper;
    
    @Override
    public Client save(Client client) {
        ClientEntity entity;
        
        if (client.getId() == null) {
            // New client - persist
            entity = entityMapper.toEntity(client);
            entityManager.persist(entity);
        } else {
            // Existing client - merge
            entity = entityManager.find(ClientEntity.class, client.getId());
            if (entity != null) {
                entityMapper.updateEntity(entity, client);
                entity = entityManager.merge(entity);
            } else {
                entity = entityMapper.toEntity(client);
                entityManager.persist(entity);
            }
        }
        
        entityManager.flush();
        return entityMapper.toDomain(entity);
    }
    
    @Override
    public Optional<Client> findById(Long id) {
        ClientEntity entity = entityManager.find(ClientEntity.class, id);
        return Optional.ofNullable(entity)
                .map(entityMapper::toDomain);
    }
    
    @Override
    public Optional<Client> findByEmail(String email) {
        try {
            TypedQuery<ClientEntity> query = entityManager.createQuery(
                "SELECT c FROM ClientEntity c WHERE c.email = :email", 
                ClientEntity.class
            );
            query.setParameter("email", email);
            ClientEntity entity = query.getSingleResult();
            return Optional.of(entityMapper.toDomain(entity));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Client> findAll() {
        TypedQuery<ClientEntity> query = entityManager.createQuery(
            "SELECT c FROM ClientEntity c ORDER BY c.lastName, c.firstName", 
            ClientEntity.class
        );
        return query.getResultList().stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Client> findAllPremium() {
        TypedQuery<ClientEntity> query = entityManager.createQuery(
            "SELECT c FROM ClientEntity c WHERE c.premium = true ORDER BY c.lastName, c.firstName", 
            ClientEntity.class
        );
        return query.getResultList().stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(Long id) {
        ClientEntity entity = entityManager.find(ClientEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM ClientEntity c WHERE c.id = :id", 
            Long.class
        );
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM ClientEntity c WHERE c.email = :email", 
            Long.class
        );
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM ClientEntity c", 
            Long.class
        );
        return query.getSingleResult();
    }
    
    @Override
    public long countPremium() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM ClientEntity c WHERE c.premium = true", 
            Long.class
        );
        return query.getSingleResult();
    }
}

// Made with Bob
