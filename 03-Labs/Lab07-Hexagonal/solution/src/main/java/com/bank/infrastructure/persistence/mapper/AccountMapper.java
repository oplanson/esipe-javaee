/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.infrastructure.persistence.mapper;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.model.Account;
import com.bank.domain.valueobject.AccountNumber;
import com.bank.domain.valueobject.AccountType;
import com.bank.domain.valueobject.Money;
import com.bank.infrastructure.persistence.entity.AccountEntity;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper between Account domain object and AccountEntity JPA entity.
 * 
 * Hexagonal Architecture: Infrastructure Layer
 * - Part of the secondary adapter (JPA adapter)
 * - Bidirectional conversion between domain and persistence
 * - Handles value object conversion
 * - No business logic, only mapping
 */
@ApplicationScoped
public class AccountMapper {
    
    /**
     * Convert JPA entity to domain object.
     * 
     * @param entity The JPA entity
     * @return Domain Account
     */
    public Account toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }
        
        boolean active = !"CLOSED".equals(entity.getStatus());
        
        // Default currency is EUR since database doesn't store currency
        Account account = new Account(
            entity.getId(),
            AccountNumber.of(entity.getAccountNumber()),
            Money.of(entity.getBalance(), "EUR"),
            AccountType.valueOf(entity.getAccountType()),
            entity.getClientId(),
            active
        );
        
        return account;
    }
    
    /**
     * Convert domain object to JPA entity.
     * 
     * @param account The domain Account
     * @return JPA AccountEntity
     */
    public AccountEntity toEntity(Account account) {
        if (account == null) {
            return null;
        }
        
        AccountEntity entity = new AccountEntity();
        entity.setId(account.getId());
        entity.setAccountNumber(account.getAccountNumber().getValue());
        entity.setBalance(account.getBalance().getAmount());
        // Currency not stored in database - only balance amount
        entity.setAccountType(account.getAccountType().name());
        entity.setClientId(account.getClientId());
        entity.setStatus(account.isClosed() ? "CLOSED" : "ACTIVE");
        
        return entity;
    }
    
    /**
     * Update existing entity from domain object.
     * Used for updates to preserve JPA managed state.
     * 
     * @param account The domain Account
     * @param entity The existing JPA entity
     */
    public void updateEntity(Account account, AccountEntity entity) {
        if (account == null || entity == null) {
            return;
        }
        
        entity.setAccountNumber(account.getAccountNumber().getValue());
        entity.setBalance(account.getBalance().getAmount());
        // Currency not stored in database - only balance amount
        entity.setAccountType(account.getAccountType().name());
        entity.setClientId(account.getClientId());
        entity.setStatus(account.isClosed() ? "CLOSED" : "ACTIVE");
    }
}

// Made with Bob
