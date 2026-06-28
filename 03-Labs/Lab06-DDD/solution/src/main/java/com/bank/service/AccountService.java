package com.bank.service;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Account;
import com.bank.model.Client;
import com.bank.config.Logged;
import com.bank.event.AccountCreatedEvent;
import com.bank.event.TransactionEvent;
import com.bank.domain.valueobject.Money;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for managing accounts using CDI and JPA.
 * Uses CDI for dependency injection and declarative transaction management.
 * 
 * Key CDI features demonstrated:
 * - @ApplicationScoped: Singleton bean managed by CDI
 * - @Inject: Dependency injection
 * - @Transactional: Declarative transaction management
 * - @Logged: Custom interceptor for logging
 */
@ApplicationScoped
public class AccountService {
    
    @Inject
    private Logger logger;
    
    @Inject
    private EntityManager em;
    
    /**
     * CDI Events for firing account and transaction events.
     * Demonstrates CDI event-driven architecture.
     */
    @Inject
    private Event<AccountCreatedEvent> accountCreatedEvent;
    
    @Inject
    private Event<TransactionEvent> transactionEvent;
    
    /**
     * Retrieve all accounts.
     * Read-only operation, no transaction required.
     * 
     * @return List of all accounts
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    @Logged
    public List<Account> findAll() {
        logger.info("Finding all accounts");
        return em.createNamedQuery("Account.findAll", Account.class)
                .getResultList();
    }
    
    /**
     * Find an account by ID.
     * Read-only operation, no transaction required.
     * 
     * @param id The account ID
     * @return The account if found, null otherwise
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    @Logged
    public Account findById(Long id) {
        logger.info("Finding account by ID: " + id);
        return em.find(Account.class, id);
    }
    
    /**
     * Find all accounts for a specific client.
     * 
     * @param clientId The client ID
     * @return List of accounts for the client
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    @Logged
    public List<Account> findByClient(Long clientId) {
        logger.info("Finding accounts for client: " + clientId);
        
        TypedQuery<Account> query = em.createNamedQuery("Account.findByClient", Account.class);
        query.setParameter("clientId", clientId);
        
        return query.getResultList();
    }
    
    /**
     * Find accounts by type (CHECKING or SAVINGS).
     * 
     * @param type The account type
     * @return List of accounts of the specified type
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    @Logged
    public List<Account> findByType(String type) {
        logger.info("Finding accounts by type: " + type);
        
        TypedQuery<Account> query = em.createNamedQuery("Account.findByType", Account.class);
        query.setParameter("type", type);
        
        return query.getResultList();
    }
    
    /**
     * Create a new account for a client.
     * Transaction automatically managed by @Transactional.
     * 
     * @param account The account to create
     * @param clientId The client ID
     * @return The created account with ID set
     */
    @Transactional
    @Logged
    public Account create(Account account, Long clientId) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        
        logger.info("Creating account for client: " + clientId);
        
        // Find the client
        Client client = em.find(Client.class, clientId);
        if (client == null) {
            throw new IllegalArgumentException("Client not found: " + clientId);
        }
        
        // Add account to client using domain method
        client.addAccount(account);
        
        // Persist the account
        em.persist(account);
        em.flush(); // Force ID generation
        
        logger.info("Account created with ID: " + account.getId());
        
        // Fire CDI event for account creation
        accountCreatedEvent.fire(new AccountCreatedEvent(account));
        
        return account;
    }
    
    /**
     * Update an existing account.
     * Transaction automatically managed by @Transactional.
     * 
     * @param account The account to update
     * @return The updated account, or null if not found
     */
    @Transactional
    @Logged
    public Account update(Account account) {
        if (account == null || account.getId() == null) {
            throw new IllegalArgumentException("Account and account ID cannot be null");
        }
        
        logger.info("Updating account: " + account.getId());
        
        // Check if account exists
        Account existing = em.find(Account.class, account.getId());
        if (existing == null) {
            logger.warning("Account not found: " + account.getId());
            return null;
        }
        
        // Update account
        Account updated = em.merge(account);
        logger.info("Account updated: " + account.getId());
        
        return updated;
    }
    
    /**
     * Delete an account by ID.
     * Transaction automatically managed by @Transactional.
     * 
     * @param id The account ID to delete
     * @return true if deleted, false if not found
     */
    @Transactional
    @Logged
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        logger.info("Deleting account: " + id);
        
        Account account = em.find(Account.class, id);
        if (account == null) {
            logger.warning("Account not found: " + id);
            return false;
        }
        
        // Get client reference before deletion
        Client client = account.getClient();
        Long clientId = client != null ? client.getId() : null;
        
        // Remove from client's collection first to maintain in-memory consistency
        // Use package-private method that doesn't update the account's client reference
        // This avoids setting client_id to null before DELETE (which would cause constraint violation)
        if (client != null) {
            client.removeAccountFromCollection(account);
        }
        
        // Now remove the account - JPA will handle the database DELETE
        em.remove(account);
        
        logger.info("Account deleted: " + id + " (client: " + clientId + ")");
        
        return true;
    }
    
    /**
     * Deposit money into an account.
     * Transaction automatically managed by @Transactional.
     * 
     * @param accountId The account ID
     * @param amount The amount to deposit
     * @return true if successful, false if account not found
     */
    @Transactional
    @Logged
    public boolean deposit(Long accountId, double amount) {
        if (accountId == null || amount <= 0) {
            return false;
        }
        
        logger.info("Depositing " + amount + " to account: " + accountId);
        
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            logger.warning("Account not found: " + accountId);
            return false;
        }
        
        Money depositAmount = Money.of(java.math.BigDecimal.valueOf(amount), account.getBalance().getCurrency());
        account.deposit(depositAmount);
        em.merge(account);
        
        logger.info("Deposit successful. New balance: " + account.getBalance().getAmount());
        
        // Fire CDI event for deposit transaction
        transactionEvent.fire(new TransactionEvent(account, TransactionEvent.TransactionType.DEPOSIT, amount));
        
        return true;
    }
    
    /**
     * Withdraw money from an account.
     * Transaction automatically managed by @Transactional.
     * 
     * @param accountId The account ID
     * @param amount The amount to withdraw
     * @return true if successful, false if account not found or insufficient funds
     */
    @Transactional
    @Logged
    public boolean withdraw(Long accountId, double amount) {
        if (accountId == null || amount <= 0) {
            return false;
        }
        
        logger.info("Withdrawing " + amount + " from account: " + accountId);
        
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            logger.warning("Account not found: " + accountId);
            return false;
        }
        
        Money withdrawAmount = Money.of(java.math.BigDecimal.valueOf(amount), account.getBalance().getCurrency());
        account.withdraw(withdrawAmount);
        em.merge(account);
        logger.info("Withdrawal successful. New balance: " + account.getBalance().getAmount());
            
        // Fire CDI event for withdrawal transaction
        transactionEvent.fire(new TransactionEvent(account, TransactionEvent.TransactionType.WITHDRAWAL, amount));
        
        return true;
    }
    
    /**
     * Transfer money between accounts.
     * Transaction automatically managed by @Transactional.
     * 
     * @param fromAccountId The source account ID
     * @param toAccountId The destination account ID
     * @param amount The amount to transfer
     * @return true if successful, false otherwise
     */
    @Transactional
    @Logged
    public boolean transfer(Long fromAccountId, Long toAccountId, double amount) {
        if (fromAccountId == null || toAccountId == null || amount <= 0) {
            return false;
        }
        
        logger.info("Transferring " + amount + " from account " + fromAccountId + " to " + toAccountId);
        
        Account fromAccount = em.find(Account.class, fromAccountId);
        Account toAccount = em.find(Account.class, toAccountId);
        
        if (fromAccount == null || toAccount == null) {
            logger.warning("One or both accounts not found");
            return false;
        }
        
        // Perform transfer using Money Value Object
        Money transferAmount = Money.euros(amount);
        
        try {
            fromAccount.withdraw(transferAmount);
            toAccount.deposit(transferAmount);
        } catch (IllegalArgumentException e) {
            logger.warning("Transfer failed: " + e.getMessage());
            return false;
        }
        
        // Merge both accounts
        em.merge(fromAccount);
        em.merge(toAccount);
        
        logger.info("Transfer successful");
        
        // Fire CDI event for transfer transaction
        transactionEvent.fire(new TransactionEvent(fromAccount, TransactionEvent.TransactionType.TRANSFER, amount, toAccountId));
        
        return true;
    }
    
    /**
     * Get the total number of accounts.
     * 
     * @return The number of accounts
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public long count() {
        logger.info("Counting accounts");
        return em.createNamedQuery("Account.count", Long.class)
                .getSingleResult();
    }
    
    /**
     * Check if an account exists.
     * 
     * @param id The account ID
     * @return true if exists, false otherwise
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean exists(Long id) {
        return em.find(Account.class, id) != null;
    }
    
    /**
     * Get total balance for a client's accounts.
     * 
     * @param clientId The client ID
     * @return The total balance
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    @Logged
    public double getTotalBalance(Long clientId) {
        logger.info("Calculating total balance for client: " + clientId);
        
        List<Account> accounts = findByClient(clientId);
        return accounts.stream()
                .mapToDouble(account -> account.getBalance().getAmount().doubleValue())
                .sum();
    }
}

// Made with Bob
