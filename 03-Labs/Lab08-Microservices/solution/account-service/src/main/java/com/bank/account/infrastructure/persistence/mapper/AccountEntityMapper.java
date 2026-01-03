// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.infrastructure.persistence.mapper;

import com.bank.account.domain.model.Account;
import com.bank.account.infrastructure.persistence.entity.AccountEntity;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper to convert between AccountEntity (JPA) and Account (domain model)
 */
@ApplicationScoped
public class AccountEntityMapper {
    
    /**
     * Convert AccountEntity to Account domain model
     * @param entity The JPA entity
     * @return The domain model
     */
    public Account toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new Account(
            entity.getId(),
            entity.getAccountNumber(),
            entity.getClientId(),
            entity.getAccountType(),
            entity.getBalance(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    /**
     * Convert Account domain model to AccountEntity
     * @param account The domain model
     * @return The JPA entity
     */
    public AccountEntity toEntity(Account account) {
        if (account == null) {
            return null;
        }
        
        AccountEntity entity = new AccountEntity();
        entity.setId(account.getId());
        entity.setAccountNumber(account.getAccountNumber());
        entity.setClientId(account.getClientId());
        entity.setAccountType(account.getAccountType());
        entity.setBalance(account.getBalance());
        entity.setStatus(account.getStatus());
        entity.setCreatedAt(account.getCreatedAt());
        entity.setUpdatedAt(account.getUpdatedAt());
        
        return entity;
    }
    
    /**
     * Update existing AccountEntity from Account domain model
     * @param entity The existing JPA entity
     * @param account The domain model with updated data
     */
    public void updateEntity(AccountEntity entity, Account account) {
        if (entity == null || account == null) {
            return;
        }
        
        entity.setAccountNumber(account.getAccountNumber());
        entity.setClientId(account.getClientId());
        entity.setAccountType(account.getAccountType());
        entity.setBalance(account.getBalance());
        entity.setStatus(account.getStatus());
    }
}

// Made with Bob
