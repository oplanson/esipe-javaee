package com.bank.domain.repository;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.AccountNumber;
import com.bank.domain.valueobject.AccountType;
import com.bank.model.Account;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Account aggregate.
 * 
 * DDD Pattern: Repository
 * - Provides collection-like interface for aggregates
 * - Abstracts persistence mechanism
 * - Part of domain layer (interface)
 * - Implementation in infrastructure layer
 * 
 * This interface defines the contract for persisting and retrieving
 * Account aggregates. It uses domain language and domain types.
 */
public interface AccountRepository {
    
    /**
     * Save or update an account.
     * 
     * @param account The account to save
     * @return The saved account
     */
    Account save(Account account);
    
    /**
     * Find an account by its ID.
     * 
     * @param id The account ID
     * @return Optional containing the account if found
     */
    Optional<Account> findById(Long id);
    
    /**
     * Find an account by its account number.
     * 
     * @param accountNumber The account number (Value Object)
     * @return Optional containing the account if found
     */
    Optional<Account> findByAccountNumber(AccountNumber accountNumber);
    
    /**
     * Find all accounts for a specific client.
     * 
     * @param clientId The client ID
     * @return List of accounts owned by the client
     */
    List<Account> findByClientId(Long clientId);
    
    /**
     * Find all accounts of a specific type.
     * 
     * @param accountType The account type (Value Object)
     * @return List of accounts of the specified type
     */
    List<Account> findByType(AccountType accountType);
    
    /**
     * Find all accounts.
     * 
     * @return List of all accounts
     */
    List<Account> findAll();
    
    /**
     * Count total number of accounts.
     * 
     * @return Total count of accounts
     */
    long count();
    
    /**
     * Count accounts for a specific client.
     * 
     * @param clientId The client ID
     * @return Number of accounts owned by the client
     */
    long countByClientId(Long clientId);
    
    /**
     * Delete an account.
     * 
     * @param account The account to delete
     */
    void delete(Account account);
    
    /**
     * Delete an account by ID.
     * 
     * @param id The account ID
     */
    void deleteById(Long id);
    
    /**
     * Check if an account exists by ID.
     * 
     * @param id The account ID
     * @return true if account exists
     */
    boolean existsById(Long id);
    
    /**
     * Check if an account number is already in use.
     * 
     * @param accountNumber The account number to check
     * @return true if account number exists
     */
    boolean existsByAccountNumber(AccountNumber accountNumber);
}

// Made with Bob