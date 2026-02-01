package com.bank.ejb;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Account;
import com.bank.model.AccountStatus;
import com.bank.model.AccountType;
import com.bank.model.Client;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

/**
 * Stateless Session Bean for account operations.
 * 
 * Key Features:
 * - Stateless: No conversational state maintained
 * - Pooled by container for scalability
 * - Thread-safe and highly concurrent
 * - Container-Managed Transactions (CMT)
 * - Declarative security with role-based access
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@DeclareRoles({"admin", "teller", "customer"})
public class AccountServiceBean {
    
    private static final Logger LOGGER = Logger.getLogger(AccountServiceBean.class.getName());
    
    @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;
    
    /**
     * Create a new account.
     *
     * @param account The account to create
     * @return The persisted account with generated ID
     */
    @RolesAllowed({"admin", "teller"})
    public Account createAccount(Account account) {
        LOGGER.info("Creating new account: " + account.getAccountNumber());
        em.persist(account);
        em.flush();
        return account;
    }
    
    /**
     * Create a new account with account number and type.
     * Convenience method for creating accounts.
     *
     * @param accountNumber The account number
     * @param type The account type
     * @param clientId The client ID
     * @return The persisted account with generated ID
     */
    @RolesAllowed({"admin", "teller"})
    public Account createAccount(String accountNumber, AccountType type, Long clientId) {
        Client client = em.find(Client.class, clientId);
        if (client == null) {
            throw new EJBException("Client not found: " + clientId);
        }
    
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setType(type);
        account.setClient(client);  // Set the managed client
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        em.persist(account);
        em.flush();
        return account;
    }
    
    /**
     * Find account by ID.
     * 
     * @param accountId The account ID
     * @return The account or null if not found
     */
    @PermitAll
    public Account findAccount(Long accountId) {
        return em.find(Account.class, accountId);
    }
    
    /**
     * Find account by account number.
     * 
     * @param accountNumber The account number
     * @return The account or null if not found
     */
    @PermitAll
    public Account findAccountByNumber(String accountNumber) {
        List<Account> accounts = em.createNamedQuery("Account.findByNumber", Account.class)
                .setParameter("accountNumber", accountNumber)
                .getResultList();
        return accounts.isEmpty() ? null : accounts.get(0);
    }
    
    /**
     * Get all accounts.
     * 
     * @return List of all accounts
     */
    @PermitAll
    public List<Account> getAllAccounts() {
        return em.createNamedQuery("Account.findAll", Account.class)
                .getResultList();
    }
    /**
     * Alias for getAllAccounts() for compatibility.
     * 
     * @return List of all accounts
     */
    @PermitAll
    public List<Account> findAll() {
        return getAllAccounts();
    }
    
    
    /**
     * Deposit money into an account.
     * Transaction is automatically managed by container.
     * 
     * @param accountId The account ID
     * @param amount The amount to deposit
     * @throws IllegalArgumentException if amount is invalid
     * @throws EJBException if account not found or operation fails
     */
    @RolesAllowed({"admin", "teller"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deposit(Long accountId, BigDecimal amount) {
        LOGGER.info(String.format("Depositing %s to account %d", amount, accountId));
        
        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        // Find account
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            throw new EJBException("Account not found: " + accountId);
        }
        
        // Perform deposit
        account.deposit(amount);
        
        // Record transaction
        Transaction transaction = new Transaction(
            account,
            TransactionType.DEPOSIT,
            amount,
            account.getBalance(),
            "Deposit"
        );
        em.persist(transaction);
        
        LOGGER.info(String.format("Deposit successful. New balance: %s", account.getBalance()));
    }
    
    /**
     * Withdraw money from an account.
     * Transaction is automatically managed by container.
     * 
     * @param accountId The account ID
     * @param amount The amount to withdraw
     * @throws IllegalArgumentException if amount is invalid
     * @throws IllegalStateException if insufficient funds
     * @throws EJBException if account not found or operation fails
     */
    @RolesAllowed({"admin", "teller"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void withdraw(Long accountId, BigDecimal amount) {
        LOGGER.info(String.format("Withdrawing %s from account %d", amount, accountId));
        
        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        // Find account
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            throw new EJBException("Account not found: " + accountId);
        }
        
        // Perform withdrawal (throws exception if insufficient funds)
        account.withdraw(amount);
        
        // Record transaction
        Transaction transaction = new Transaction(
            account,
            TransactionType.WITHDRAWAL,
            amount,
            account.getBalance(),
            "Withdrawal"
        );
        em.persist(transaction);
        
        LOGGER.info(String.format("Withdrawal successful. New balance: %s", account.getBalance()));
    }
    
    /**
     * Transfer money between accounts.
     * Both operations are in the same transaction - if one fails, both rollback.
     * 
     * @param fromAccountId The source account ID
     * @param toAccountId The destination account ID
     * @param amount The amount to transfer
     * @throws IllegalArgumentException if amount is invalid or accounts are the same
     * @throws IllegalStateException if insufficient funds
     * @throws EJBException if accounts not found or operation fails
     */
    @RolesAllowed({"admin", "teller"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        LOGGER.info(String.format("Transferring %s from account %d to account %d", 
                                 amount, fromAccountId, toAccountId));
        
        // Validate
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        // Find accounts
        Account fromAccount = em.find(Account.class, fromAccountId);
        Account toAccount = em.find(Account.class, toAccountId);
        
        if (fromAccount == null) {
            throw new EJBException("Source account not found: " + fromAccountId);
        }
        if (toAccount == null) {
            throw new EJBException("Destination account not found: " + toAccountId);
        }
        
        // Perform transfer (atomic operation)
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
        
        // Record transactions
        Transaction debitTxn = new Transaction(
            fromAccount,
            TransactionType.TRANSFER_OUT,
            amount,
            fromAccount.getBalance(),
            "Transfer to " + toAccount.getAccountNumber()
        );
        em.persist(debitTxn);
        
        Transaction creditTxn = new Transaction(
            toAccount,
            TransactionType.TRANSFER_IN,
            amount,
            toAccount.getBalance(),
            "Transfer from " + fromAccount.getAccountNumber()
        );
        em.persist(creditTxn);
        
        LOGGER.info("Transfer successful");
    }
    
    /**
     * Get account balance.
     * 
     * @param accountId The account ID
     * @return The current balance
     */
    @PermitAll
    public BigDecimal getBalance(Long accountId) {
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            throw new EJBException("Account not found: " + accountId);
        }
        return account.getBalance();
    }
    
    /**
     * Get transaction history for an account.
     * 
     * @param accountId The account ID
     * @return List of transactions
     */
    @PermitAll
    public List<Transaction> getTransactionHistory(Long accountId) {
        return em.createNamedQuery("Transaction.findByAccount", Transaction.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }
    
    /**
     * Close an account.
     * Only allowed if balance is zero.
     * 
     * @param accountId The account ID
     * @throws IllegalStateException if account has non-zero balance
     */
    @RolesAllowed({"admin"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void closeAccount(Long accountId) {
        LOGGER.info("Closing account: " + accountId);
        
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            throw new EJBException("Account not found: " + accountId);
        }
        
        account.close();
        LOGGER.info("Account closed: " + accountId);
    }
    
    /**
     * Update account.
     * 
     * @param account The account to update
     * @return The updated account
     */
    @RolesAllowed({"admin", "teller"})
    public Account updateAccount(Account account) {
        LOGGER.info("Updating account: " + account.getId());
        return em.merge(account);
    }
    
    /**
     * Delete account.
     * Only for testing purposes.
     * 
     * @param accountId The account ID
     */
    @RolesAllowed({"admin"})
    public void deleteAccount(Long accountId) {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            em.remove(account);
            LOGGER.info("Account deleted: " + accountId);
        }
    }
    
    /**
     * Get total number of accounts.
     * 
     * @return The count
     */
    @PermitAll
    public long getAccountCount() {
        return em.createNamedQuery("Account.count", Long.class)
                .getSingleResult();
    }
}

// Made with Bob