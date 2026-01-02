/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.port.out;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.model.Account;
import com.bank.domain.valueobject.AccountNumber;

import java.util.List;
import java.util.Optional;

/**
 * Secondary port (driven) - Repository interface for Account aggregate.
 * Defined by the application layer, implemented by infrastructure layer.
 * Uses domain objects only - no infrastructure concerns.
 */
public interface AccountRepository {
    
    /**
     * Find an account by its ID
     */
    Optional<Account> findById(Long id);
    
    /**
     * Find an account by its account number
     */
    Optional<Account> findByNumber(AccountNumber accountNumber);
    
    /**
     * Find all accounts belonging to a client
     */
    List<Account> findByClientId(Long clientId);
    
    /**
     * Find all accounts
     */
    List<Account> findAll();
    
    /**
     * Save an account (create or update)
     */
    void save(Account account);
    
    /**
     * Delete an account
     */
    void delete(Account account);
    
    /**
     * Check if an account exists by account number
     */
    boolean existsByNumber(AccountNumber accountNumber);
}

// Made with Bob
