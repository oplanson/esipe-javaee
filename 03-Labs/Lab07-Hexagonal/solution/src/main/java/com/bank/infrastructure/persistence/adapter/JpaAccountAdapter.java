/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.persistence.adapter;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.port.out.AccountRepository;
import com.bank.domain.model.Account;
import com.bank.domain.valueobject.AccountNumber;
import com.bank.infrastructure.persistence.entity.AccountEntity;
import com.bank.infrastructure.persistence.mapper.AccountMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA implementation of AccountRepository port.
 * 
 * Hexagonal Architecture: Secondary Adapter (Driven Adapter)
 * - Implements secondary port (AccountRepository)
 * - Handles all JPA/database concerns
 * - Uses mapper to convert between domain and persistence
 * - Isolated from domain and application layers
 */
@ApplicationScoped
public class JpaAccountAdapter implements AccountRepository {
    
    @Inject
    private EntityManager entityManager;
    
    @Inject
    private AccountMapper mapper;
    
    @Override
    public Optional<Account> findById(Long id) {
        AccountEntity entity = entityManager.find(AccountEntity.class, id);
        return Optional.ofNullable(mapper.toDomain(entity));
    }
    
    @Override
    public Optional<Account> findByNumber(AccountNumber accountNumber) {
        try {
            TypedQuery<AccountEntity> query = entityManager.createQuery(
                "SELECT a FROM AccountEntity a WHERE a.accountNumber = :number",
                AccountEntity.class
            );
            query.setParameter("number", accountNumber.getValue());
            AccountEntity entity = query.getSingleResult();
            return Optional.of(mapper.toDomain(entity));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Account> findByClientId(Long clientId) {
        TypedQuery<AccountEntity> query = entityManager.createQuery(
            "SELECT a FROM AccountEntity a WHERE a.clientId = :clientId",
            AccountEntity.class
        );
        query.setParameter("clientId", clientId);
        
        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void save(Account account) {
        if (account.getId() == null) {
            // New account - persist
            AccountEntity entity = mapper.toEntity(account);
            entityManager.persist(entity);
            // Force flush to generate ID immediately
            entityManager.flush();
            // Update domain object with generated ID
            account.setId(entity.getId());
        } else {
            // Existing account - merge
            AccountEntity existingEntity = entityManager.find(AccountEntity.class, account.getId());
            if (existingEntity != null) {
                mapper.updateEntity(account, existingEntity);
                entityManager.merge(existingEntity);
            } else {
                // Entity was removed, persist as new
                AccountEntity entity = mapper.toEntity(account);
                entityManager.persist(entity);
                entityManager.flush();
                account.setId(entity.getId());
            }
        }
    }
    
    @Override
    public void delete(Account account) {
        if (account.getId() != null) {
            AccountEntity entity = entityManager.find(AccountEntity.class, account.getId());
            if (entity != null) {
                entityManager.remove(entity);
            }
        }
    }
    
    @Override
    public List<Account> findAll() {
        TypedQuery<AccountEntity> query = entityManager.createQuery(
            "SELECT a FROM AccountEntity a ORDER BY a.accountNumber",
            AccountEntity.class
        );
        
        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByNumber(AccountNumber accountNumber) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(a) FROM AccountEntity a WHERE a.accountNumber = :number",
            Long.class
        );
        query.setParameter("number", accountNumber.getValue());
        return query.getSingleResult() > 0;
    }
}

// Made with Bob
