// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.application.mapper;

import com.bank.account.application.dto.AccountDTO;
import com.bank.account.domain.model.Account;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper to convert between Account domain model and AccountDTO
 */
@ApplicationScoped
public class AccountMapper {
    
    /**
     * Convert Account domain model to AccountDTO
     * @param account The domain model
     * @return The DTO
     */
    public AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }
        
        return new AccountDTO(
            account.getId(),
            account.getAccountNumber(),
            account.getClientId(),
            account.getAccountType(),
            account.getBalance(),
            account.getStatus(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
    
    /**
     * Convert AccountDTO to Account domain model
     * @param dto The DTO
     * @return The domain model
     */
    public Account toDomain(AccountDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return new Account(
            dto.getId(),
            dto.getAccountNumber(),
            dto.getClientId(),
            dto.getAccountType(),
            dto.getBalance(),
            dto.getStatus(),
            dto.getCreatedAt(),
            dto.getUpdatedAt()
        );
    }
    
    /**
     * Update existing Account domain model from AccountDTO
     * @param account The existing domain model
     * @param dto The DTO with updated data
     */
    public void updateFromDTO(Account account, AccountDTO dto) {
        if (account == null || dto == null) {
            return;
        }
        
        account.setAccountNumber(dto.getAccountNumber());
        account.setClientId(dto.getClientId());
        account.setAccountType(dto.getAccountType());
        account.setBalance(dto.getBalance());
        account.setStatus(dto.getStatus());
    }
}

// Made with Bob
