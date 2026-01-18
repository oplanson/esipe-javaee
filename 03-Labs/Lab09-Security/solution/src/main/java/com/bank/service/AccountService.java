// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.service;

import com.bank.model.Account;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service for account management operations
 */
@ApplicationScoped
public class AccountService {
    
    private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());
    
    @PersistenceContext(unitName = "bankPU")
    private EntityManager em;
    
    /**
     * Create a new account
     * 
     * @param ownerUsername Owner username
     * @param initialBalance Initial balance
     * @param accountType Account type
     * @return Created account
     */
    @Transactional
    public Account createAccount(String ownerUsername, BigDecimal initialBalance, String accountType) {
        if (ownerUsername == null || ownerUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner username is required");
        }
        
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance must be non-negative");
        }
        
        Account account = new Account();
        account.setOwnerUsername(ownerUsername);
        account.setBalance(initialBalance);
        account.setAccountType(accountType != null ? accountType : "CHECKING");
        
        em.persist(account);
        em.flush();
        
        LOGGER.info("Account created: " + account.getAccountNumber() + " for user: " + ownerUsername);
        return account;
    }
    
    /**
     * Get account by ID
     */
    public Account getAccountById(Long id) {
        return em.find(Account.class, id);
    }
    
    /**
     * Get account by account number
     */
    public Account getAccountByNumber(String accountNumber) {
        try {
            return em.createNamedQuery("Account.findByAccountNumber", Account.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Get all accounts for a user
     */
    public List<Account> getAccountsByOwner(String username) {
        return em.createNamedQuery("Account.findByOwner", Account.class)
                .setParameter("username", username)
                .getResultList();
    }
    
    /**
     * Get all accounts
     */
    public List<Account> getAllAccounts() {
        return em.createNamedQuery("Account.findAll", Account.class)
                .getResultList();
    }
    
    /**
     * Update account balance
     */
    @Transactional
    public Account updateBalance(Long accountId, BigDecimal newBalance) {
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        
        if (newBalance == null || newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance must be non-negative");
        }
        
        account.setBalance(newBalance);
        em.merge(account);
        
        LOGGER.info("Balance updated for account: " + account.getAccountNumber() + 
                   " to: " + newBalance);
        return account;
    }
    
    /**
     * Deposit money
     */
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        em.merge(account);
        
        LOGGER.info("Deposited " + amount + " to account: " + account.getAccountNumber());
        return account;
    }
    
    /**
     * Withdraw money
     */
    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        
        BigDecimal newBalance = account.getBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        
        account.setBalance(newBalance);
        em.merge(account);
        
        LOGGER.info("Withdrew " + amount + " from account: " + account.getAccountNumber());
        return account;
    }
    
    /**
     * Transfer money between accounts
     */
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        Account fromAccount = em.find(Account.class, fromAccountId);
        if (fromAccount == null) {
            throw new IllegalArgumentException("Source account not found");
        }
        
        Account toAccount = em.find(Account.class, toAccountId);
        if (toAccount == null) {
            throw new IllegalArgumentException("Destination account not found");
        }
        
        // Check sufficient funds
        BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
        if (newFromBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        
        // Perform transfer
        fromAccount.setBalance(newFromBalance);
        toAccount.setBalance(toAccount.getBalance().add(amount));
        
        em.merge(fromAccount);
        em.merge(toAccount);
        
        LOGGER.info("Transferred " + amount + " from " + fromAccount.getAccountNumber() + 
                   " to " + toAccount.getAccountNumber());
    }
    
    /**
     * Delete account
     */
    @Transactional
    public void deleteAccount(Long accountId) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            em.remove(account);
            LOGGER.info("Account deleted: " + account.getAccountNumber());
        }
    }
    
    /**
     * Check if user owns account
     */
    public boolean isAccountOwner(Long accountId, String username) {
        Account account = em.find(Account.class, accountId);
        return account != null && account.getOwnerUsername().equals(username);
    }
}

// Made with Bob
