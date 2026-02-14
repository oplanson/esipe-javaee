package com.bank.application.dto;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.AccountType;
import com.bank.model.Account;

import java.math.BigDecimal;

/**
 * AccountDTO for transferring account data between layers.
 *
 * DDD Pattern: Data Transfer Object (DTO)
 * - Separates domain model from presentation/API layer
 * - Provides a stable interface for external consumers
 * - Prevents exposing domain model internals
 * - Simplifies serialization (JSON, XML)
 *
 * Refactored to Java Record (JDK 17+):
 * - Immutable by design
 * - Concise syntax (no boilerplate)
 * - Automatic equals/hashCode/toString
 * - Perfect for DTOs (data carriers)
 */
public record AccountDTO(
    Long id,
    String accountNumber,
    BigDecimal balance,
    String currency,
    String accountType,
    Long clientId,
    String clientName
) {
    
    /**
     * Create DTO from Account entity.
     *
     * @param account The account entity
     * @return AccountDTO
     */
    public static AccountDTO fromEntity(Account account) {
        if (account == null) {
            return null;
        }
        
        return new AccountDTO(
            account.getId(),
            account.getAccountNumber() != null ? account.getAccountNumber().getValue() : null,
            account.getBalance() != null ? account.getBalance().getAmount() : BigDecimal.ZERO,
            account.getBalance() != null ? account.getBalance().getCurrency() : "EUR",
            account.getAccountType() != null ? account.getAccountType().name() : null,
            account.getClientId(),
            account.getClient() != null ? account.getClient().getName() : null
        );
    }
    
    /**
     * Get balance as double for backward compatibility.
     *
     * @return Balance as double
     */
    public double getBalanceAsDouble() {
        return balance != null ? balance.doubleValue() : 0.0;
    }
    
    /**
     * Get account type display name.
     *
     * @return Display name
     */
    public String getAccountTypeDisplayName() {
        if (accountType == null) {
            return "";
        }
        try {
            return AccountType.valueOf(accountType).getDisplayName();
        } catch (IllegalArgumentException e) {
            return accountType;
        }
    }
}

// Made with Bob