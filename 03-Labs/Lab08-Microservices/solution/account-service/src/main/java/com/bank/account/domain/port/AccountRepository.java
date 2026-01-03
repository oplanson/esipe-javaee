// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.domain.port;

import com.bank.account.domain.model.Account;
import com.bank.account.domain.model.AccountStatus;
import com.bank.account.domain.model.AccountType;
import java.util.List;
import java.util.Optional;

/**
 * Account Repository Port (Interface)
 * Defines the contract for account persistence operations
 * This is a domain interface that will be implemented by infrastructure layer
 */
public interface AccountRepository {
    
    /**
     * Save a new account or update an existing one
     * @param account The account to save
     * @return The saved account with generated ID
     */
    Account save(Account account);
    
    /**
     * Find an account by ID
     * @param id The account ID
     * @return Optional containing the account if found
     */
    Optional<Account> findById(Long id);
    
    /**
     * Find an account by account number
     * @param accountNumber The account number
     * @return Optional containing the account if found
     */
    Optional<Account> findByAccountNumber(String accountNumber);
    
    /**
     * Find all accounts
     * @return List of all accounts
     */
    List<Account> findAll();
    
    /**
     * Find all accounts for a specific client
     * @param clientId The client ID
     * @return List of accounts for the client
     */
    List<Account> findByClientId(Long clientId);
    
    /**
     * Find accounts by type
     * @param accountType The account type
     * @return List of accounts of the specified type
     */
    List<Account> findByAccountType(AccountType accountType);
    
    /**
     * Find accounts by status
     * @param status The account status
     * @return List of accounts with the specified status
     */
    List<Account> findByStatus(AccountStatus status);
    
    /**
     * Delete an account by ID
     * @param id The account ID
     */
    void deleteById(Long id);
    
    /**
     * Check if an account exists by ID
     * @param id The account ID
     * @return true if account exists, false otherwise
     */
    boolean existsById(Long id);
    
    /**
     * Check if an account exists by account number
     * @param accountNumber The account number
     * @return true if account exists, false otherwise
     */
    boolean existsByAccountNumber(String accountNumber);
    
    /**
     * Count total number of accounts
     * @return Total number of accounts
     */
    long count();
    
    /**
     * Count accounts for a specific client
     * @param clientId The client ID
     * @return Number of accounts for the client
     */
    long countByClientId(Long clientId);
}

// Made with Bob
