package com.bank.ejb;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Stateful Session Bean for transaction batch processing.
 * 
 * Key Features:
 * - Stateful: Maintains conversational state per client
 * - Dedicated instance per client session
 * - Useful for multi-step workflows
 * - Must be explicitly removed after use with @Remove
 * - Supports batch commit/rollback operations
 */
@Stateful
@StatefulTimeout(value = 30, unit = java.util.concurrent.TimeUnit.MINUTES)
public class TransactionBatchBean {
    
    private static final Logger LOGGER = Logger.getLogger(TransactionBatchBean.class.getName());
    
    @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;
    
    // Conversational state - maintained across method calls
    private List<PendingTransaction> pendingTransactions;
    private String batchId;
    private int transactionCount;
    
    @PostConstruct
    public void init() {
        pendingTransactions = new ArrayList<>();
        batchId = "BATCH-" + System.currentTimeMillis();
        transactionCount = 0;
        LOGGER.info("TransactionBatch created: " + batchId);
    }
    
    /**
     * Add a deposit transaction to the batch.
     * 
     * @param accountId The account ID
     * @param amount The deposit amount
     */
    public void addDeposit(Long accountId, BigDecimal amount) {
        validateAmount(amount);
        PendingTransaction txn = new PendingTransaction(
            accountId, 
            TransactionOperation.DEPOSIT, 
            amount
        );
        pendingTransactions.add(txn);
        transactionCount++;
        LOGGER.info(String.format("Added deposit to batch %s: account=%d, amount=%s", 
                                 batchId, accountId, amount));
    }
    
    /**
     * Add a withdrawal transaction to the batch.
     * 
     * @param accountId The account ID
     * @param amount The withdrawal amount
     */
    public void addWithdrawal(Long accountId, BigDecimal amount) {
        validateAmount(amount);
        PendingTransaction txn = new PendingTransaction(
            accountId, 
            TransactionOperation.WITHDRAWAL, 
            amount
        );
        pendingTransactions.add(txn);
        transactionCount++;
        LOGGER.info(String.format("Added withdrawal to batch %s: account=%d, amount=%s", 
                                 batchId, accountId, amount));
    }
    
    /**
     * Add a transfer transaction to the batch.
     * 
     * @param fromAccountId The source account ID
     * @param toAccountId The destination account ID
     * @param amount The transfer amount
     */
    public void addTransfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        validateAmount(amount);
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        PendingTransaction txn = new PendingTransaction(
            fromAccountId, 
            toAccountId,
            TransactionOperation.TRANSFER, 
            amount
        );
        pendingTransactions.add(txn);
        transactionCount++;
        LOGGER.info(String.format("Added transfer to batch %s: from=%d, to=%d, amount=%s", 
                                 batchId, fromAccountId, toAccountId, amount));
    }
    
    /**
     * Get the number of pending transactions in the batch.
     * 
     * @return The count
     */
    public int getPendingCount() {
        return pendingTransactions.size();
    }
    
    /**
     * Get the total number of transactions processed in this batch.
     * 
     * @return The total count
     */
    public int getTotalCount() {
        return transactionCount;
    }
    
    /**
     * Get the batch ID.
     * 
     * @return The batch ID
     */
    public String getBatchId() {
        return batchId;
    }
    
    /**
     * Get list of pending transactions (for review).
     * 
     * @return List of pending transactions
     */
    public List<PendingTransaction> getPendingTransactions() {
        return new ArrayList<>(pendingTransactions);
    }
    
    /**
     * Commit all pending transactions.
     * Uses REQUIRES_NEW to ensure all operations are in a new transaction.
     * If any operation fails, all are rolled back.
     * 
     * @return Number of transactions committed
     * @throws EJBException if any transaction fails
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int commit() {
        LOGGER.info(String.format("Committing batch %s with %d transactions", 
                                 batchId, pendingTransactions.size()));
        
        if (pendingTransactions.isEmpty()) {
            LOGGER.warning("No transactions to commit");
            return 0;
        }
        
        int committed = 0;
        
        try {
            // Process all transactions
            for (PendingTransaction pending : pendingTransactions) {
                processTransaction(pending);
                committed++;
            }
            
            // Clear pending list after successful commit
            pendingTransactions.clear();
            
            LOGGER.info(String.format("Batch %s committed successfully: %d transactions", 
                                     batchId, committed));
            return committed;
            
        } catch (Exception e) {
            LOGGER.severe(String.format("Batch %s commit failed: %s", batchId, e.getMessage()));
            // Transaction will be rolled back automatically by container
            throw new EJBException("Batch commit failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Rollback (clear) all pending transactions without committing.
     */
    public void rollback() {
        int count = pendingTransactions.size();
        pendingTransactions.clear();
        LOGGER.info(String.format("Batch %s rolled back: %d transactions discarded", 
                                 batchId, count));
    }
    
    /**
     * Process a single pending transaction.
     * 
     * @param pending The pending transaction
     */
    private void processTransaction(PendingTransaction pending) {
        Account account = em.find(Account.class, pending.accountId);
        if (account == null) {
            throw new EJBException("Account not found: " + pending.accountId);
        }
        
        switch (pending.operation) {
            case DEPOSIT:
                account.deposit(pending.amount);
                recordTransaction(account, TransactionType.DEPOSIT, pending.amount, "Batch deposit");
                break;
                
            case WITHDRAWAL:
                account.withdraw(pending.amount);
                recordTransaction(account, TransactionType.WITHDRAWAL, pending.amount, "Batch withdrawal");
                break;
                
            case TRANSFER:
                Account toAccount = em.find(Account.class, pending.toAccountId);
                if (toAccount == null) {
                    throw new EJBException("Destination account not found: " + pending.toAccountId);
                }
                account.withdraw(pending.amount);
                toAccount.deposit(pending.amount);
                recordTransaction(account, TransactionType.TRANSFER_OUT, pending.amount, 
                                "Batch transfer to " + toAccount.getAccountNumber());
                recordTransaction(toAccount, TransactionType.TRANSFER_IN, pending.amount, 
                                "Batch transfer from " + account.getAccountNumber());
                break;
        }
    }
    
    /**
     * Record a transaction in the database.
     * 
     * @param account The account
     * @param type The transaction type
     * @param amount The amount
     * @param description The description
     */
    private void recordTransaction(Account account, TransactionType type, 
                                   BigDecimal amount, String description) {
        Transaction transaction = new Transaction(
            account,
            type,
            amount,
            account.getBalance(),
            description
        );
        transaction.setProcessedBy("BATCH-" + batchId);
        em.persist(transaction);
    }
    
    /**
     * Validate transaction amount.
     * 
     * @param amount The amount to validate
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
    
    /**
     * Close the batch and release resources.
     * This method must be called when done with the batch.
     */
    @Remove
    public void close() {
        if (!pendingTransactions.isEmpty()) {
            LOGGER.warning(String.format("Batch %s closed with %d uncommitted transactions", 
                                        batchId, pendingTransactions.size()));
        }
        LOGGER.info("TransactionBatch closed: " + batchId);
    }
    
    @PreDestroy
    public void cleanup() {
        LOGGER.info("TransactionBatch destroyed: " + batchId);
    }
    
    /**
     * Inner class representing a pending transaction.
     */
    public static class PendingTransaction {
        private final Long accountId;
        private final Long toAccountId; // For transfers
        private final TransactionOperation operation;
        private final BigDecimal amount;
        
        public PendingTransaction(Long accountId, TransactionOperation operation, BigDecimal amount) {
            this.accountId = accountId;
            this.toAccountId = null;
            this.operation = operation;
            this.amount = amount;
        }
        
        public PendingTransaction(Long accountId, Long toAccountId, 
                                 TransactionOperation operation, BigDecimal amount) {
            this.accountId = accountId;
            this.toAccountId = toAccountId;
            this.operation = operation;
            this.amount = amount;
        }
        
        public Long getAccountId() {
            return accountId;
        }
        
        public Long getToAccountId() {
            return toAccountId;
        }
        
        public TransactionOperation getOperation() {
            return operation;
        }
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        @Override
        public String toString() {
            return String.format("%s: account=%d, amount=%s", operation, accountId, amount);
        }
    }
    
    /**
     * Transaction operation enumeration.
     */
    public enum TransactionOperation {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER
    }
}

// Made with Bob