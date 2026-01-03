// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.infrastructure.persistence.adapter;

import com.bank.account.domain.model.Account;
import com.bank.account.domain.model.AccountStatus;
import com.bank.account.domain.model.AccountType;
import com.bank.account.domain.port.AccountRepository;
import com.bank.account.infrastructure.persistence.entity.AccountEntity;
import com.bank.account.infrastructure.persistence.mapper.AccountEntityMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA implementation of AccountRepository
 */
@ApplicationScoped
@Transactional
public class AccountRepositoryAdapter implements AccountRepository {
    
    @PersistenceContext(unitName = "accountPU")
    private EntityManager entityManager;
    
    @jakarta.inject.Inject
    private AccountEntityMapper mapper;
    
    @Override
    public Account save(Account account) {
        if (account.getId() == null) {
            // Create new account
            AccountEntity entity = mapper.toEntity(account);
            entityManager.persist(entity);
            entityManager.flush();
            return mapper.toDomain(entity);
        } else {
            // Update existing account
            AccountEntity entity = entityManager.find(AccountEntity.class, account.getId());
            if (entity == null) {
                throw new IllegalArgumentException("Account not found with id: " + account.getId());
            }
            mapper.updateEntity(entity, account);
            entity = entityManager.merge(entity);
            entityManager.flush();
            return mapper.toDomain(entity);
        }
    }
    
    @Override
    public Optional<Account> findById(Long id) {
        AccountEntity entity = entityManager.find(AccountEntity.class, id);
        return Optional.ofNullable(mapper.toDomain(entity));
    }
    
    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        try {
            TypedQuery<AccountEntity> query = entityManager.createQuery(
                "SELECT a FROM AccountEntity a WHERE a.accountNumber = :accountNumber",
                AccountEntity.class
            );
            query.setParameter("accountNumber", accountNumber);
            AccountEntity entity = query.getSingleResult();
            return Optional.of(mapper.toDomain(entity));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Account> findByClientId(Long clientId) {
        TypedQuery<AccountEntity> query = entityManager.createQuery(
            "SELECT a FROM AccountEntity a WHERE a.clientId = :clientId ORDER BY a.createdAt DESC",
            AccountEntity.class
        );
        query.setParameter("clientId", clientId);
        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Account> findByStatus(AccountStatus status) {
        TypedQuery<AccountEntity> query = entityManager.createQuery(
            "SELECT a FROM AccountEntity a WHERE a.status = :status ORDER BY a.createdAt DESC",
            AccountEntity.class
        );
        query.setParameter("status", status);
        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Account> findAll() {
        TypedQuery<AccountEntity> query = entityManager.createQuery(
            "SELECT a FROM AccountEntity a ORDER BY a.createdAt DESC",
            AccountEntity.class
        );
        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Account> findByAccountType(AccountType accountType) {
        TypedQuery<AccountEntity> query = entityManager.createQuery(
            "SELECT a FROM AccountEntity a WHERE a.accountType = :accountType ORDER BY a.createdAt DESC",
            AccountEntity.class
        );
        query.setParameter("accountType", accountType);
        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(Long id) {
        AccountEntity entity = entityManager.find(AccountEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(a) FROM AccountEntity a WHERE a.id = :id",
            Long.class
        );
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(a) FROM AccountEntity a WHERE a.accountNumber = :accountNumber",
            Long.class
        );
        query.setParameter("accountNumber", accountNumber);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(a) FROM AccountEntity a",
            Long.class
        );
        return query.getSingleResult();
    }
    
    @Override
    public long countByClientId(Long clientId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(a) FROM AccountEntity a WHERE a.clientId = :clientId",
            Long.class
        );
        query.setParameter("clientId", clientId);
        return query.getSingleResult();
    }
}

// Made with Bob
