package com.bank.service;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Transaction;
import com.bank.model.Account;
import com.bank.config.Logged;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for managing transactions using CDI and JPA.
 * Provides methods to create and query transaction history.
 */
@ApplicationScoped
public class TransactionService {
    
    @Inject
    private Logger logger;
    
    @Inject
    private EntityManager em;
    
    /**
     * Retrieve all transactions.
     * 
     * @return List of all transactions ordered by date (most recent first)
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    @Logged
    public List<Transaction> findAll() {
        logger.info("Finding all transactions");
        return em.createNamedQuery("Transaction.findAll", Transaction.class)
                .getResultList();
    }
    
    /**
     * Find a transaction by ID.
     * 
     * @param id The transaction ID
     * @return The transaction if found, null otherwise
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    @Logged
    public Transaction findById(Long id) {
        logger.info("Finding transaction by ID: " + id);
        return em.find(Transaction.class, id);
    }
    
    /**
     * Find all transactions for a specific account.
     * 
     * @param accountId The account ID
     * @return List of transactions for the account
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    @Logged
    public List<Transaction> findByAccount(Long accountId) {
        logger.info("Finding transactions for account: " + accountId);
        
        TypedQuery<Transaction> query = em.createNamedQuery("Transaction.findByAccount", Transaction.class);
        query.setParameter("accountId", accountId);
        
        return query.getResultList();
    }
    
    /**
     * Find transactions by type.
     * 
     * @param type The transaction type
     * @return List of transactions of the specified type
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    @Logged
    public List<Transaction> findByType(Transaction.TransactionType type) {
        logger.info("Finding transactions by type: " + type);
        
        TypedQuery<Transaction> query = em.createNamedQuery("Transaction.findByType", Transaction.class);
        query.setParameter("type", type);
        
        return query.getResultList();
    }
    
    /**
     * Find transactions within a date range.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return List of transactions within the date range
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    @Logged
    public List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Finding transactions between " + startDate + " and " + endDate);
        
        TypedQuery<Transaction> query = em.createNamedQuery("Transaction.findByDateRange", Transaction.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        return query.getResultList();
    }
    
    /**
     * Create a new transaction record.
     * This is typically called internally by AccountService.
     * 
     * @param transaction The transaction to create
     * @return The created transaction with ID set
     */
    @Transactional
    @Logged
    public Transaction create(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        
        logger.info("Creating transaction: " + transaction.getType() + " for account: " + transaction.getAccountId());
        
        em.persist(transaction);
        em.flush();
        
        logger.info("Transaction created with ID: " + transaction.getId());
        
        return transaction;
    }
    
    /**
     * Create a deposit transaction.
     * 
     * @param account The account
     * @param amount The deposit amount
     * @param balanceBefore The balance before deposit
     * @param balanceAfter The balance after deposit
     * @return The created transaction
     */
    @Transactional
    @Logged
    public Transaction createDeposit(Account account, double amount, double balanceBefore, double balanceAfter) {
        Transaction transaction = new Transaction(
            Transaction.TransactionType.DEPOSIT,
            amount,
            balanceBefore,
            balanceAfter,
            account
        );
        transaction.setDescription("Deposit of " + amount);
        
        return create(transaction);
    }
    
    /**
     * Create a withdrawal transaction.
     * 
     * @param account The account
     * @param amount The withdrawal amount
     * @param balanceBefore The balance before withdrawal
     * @param balanceAfter The balance after withdrawal
     * @return The created transaction
     */
    @Transactional
    @Logged
    public Transaction createWithdrawal(Account account, double amount, double balanceBefore, double balanceAfter) {
        Transaction transaction = new Transaction(
            Transaction.TransactionType.WITHDRAWAL,
            amount,
            balanceBefore,
            balanceAfter,
            account
        );
        transaction.setDescription("Withdrawal of " + amount);
        
        return create(transaction);
    }
    
    /**
     * Create transfer transactions (both outgoing and incoming).
     * 
     * @param fromAccount The source account
     * @param toAccount The destination account
     * @param amount The transfer amount
     * @param fromBalanceBefore The source account balance before transfer
     * @param fromBalanceAfter The source account balance after transfer
     * @param toBalanceBefore The destination account balance before transfer
     * @param toBalanceAfter The destination account balance after transfer
     */
    @Transactional
    @Logged
    public void createTransfer(Account fromAccount, Account toAccount, double amount,
                              double fromBalanceBefore, double fromBalanceAfter,
                              double toBalanceBefore, double toBalanceAfter) {
        // Create outgoing transaction
        Transaction transferOut = new Transaction(
            Transaction.TransactionType.TRANSFER_OUT,
            amount,
            fromBalanceBefore,
            fromBalanceAfter,
            fromAccount,
            toAccount.getId(),
            "Transfer to account " + toAccount.getNumber()
        );
        create(transferOut);
        
        // Create incoming transaction
        Transaction transferIn = new Transaction(
            Transaction.TransactionType.TRANSFER_IN,
            amount,
            toBalanceBefore,
            toBalanceAfter,
            toAccount,
            fromAccount.getId(),
            "Transfer from account " + fromAccount.getNumber()
        );
        create(transferIn);
        
        logger.info("Transfer transactions created: " + amount + " from account " + 
                   fromAccount.getId() + " to account " + toAccount.getId());
    }
    
    /**
     * Get the total number of transactions.
     * 
     * @return The number of transactions
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public long count() {
        logger.info("Counting transactions");
        return em.createNamedQuery("Transaction.count", Long.class)
                .getSingleResult();
    }
    
    /**
     * Get recent transactions for an account (last N transactions).
     * 
     * @param accountId The account ID
     * @param limit The maximum number of transactions to return
     * @return List of recent transactions
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    @Logged
    public List<Transaction> findRecentByAccount(Long accountId, int limit) {
        logger.info("Finding last " + limit + " transactions for account: " + accountId);
        
        TypedQuery<Transaction> query = em.createNamedQuery("Transaction.findByAccount", Transaction.class);
        query.setParameter("accountId", accountId);
        query.setMaxResults(limit);
        
        return query.getResultList();
    }
}

// Made with Bob