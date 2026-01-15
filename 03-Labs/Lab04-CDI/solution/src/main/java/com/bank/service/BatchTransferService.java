/*
 * © Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited. Made with IBM Bob.
 */
package com.bank.service;

import com.bank.model.Account;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Batch transfer service demonstrating Bean-Managed Transactions (BMT).
 * Uses UserTransaction for fine-grained control over transaction boundaries.
 */
@ApplicationScoped
public class BatchTransferService {
    
    @Inject
    private EntityManager em;
    
    @Resource
    private UserTransaction utx;
    
    @Inject
    private Logger logger;
    
    /**
     * Process multiple transfers with individual transaction boundaries.
     * Each transfer is committed independently - partial success is possible.
     * 
     * @param requests List of transfer requests to process
     * @return BatchTransferResult with successful and failed transfers
     */
    public BatchTransferResult processBatch(List<TransferRequest> requests) {
        List<String> successful = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        
        for (TransferRequest request : requests) {
            try {
                // Set timeout for each transaction
                utx.setTransactionTimeout(30);
                utx.begin();
                
                // Find accounts
                Account from = em.find(Account.class, request.getFromId());
                Account to = em.find(Account.class, request.getToId());
                
                if (from == null || to == null) {
                    utx.rollback();
                    failed.add("Transfer " + request.getId() + ": Account not found");
                    continue;
                }
                
                // Validate balance
                if (from.getBalance() < request.getAmount().doubleValue()) {
                    utx.rollback();
                    failed.add("Transfer " + request.getId() + ": Insufficient funds");
                    continue;
                }
                
                // Execute transfer
                from.setBalance(from.getBalance() - request.getAmount().doubleValue());
                to.setBalance(to.getBalance() + request.getAmount().doubleValue());
                
                // Commit this transaction
                utx.commit();
                successful.add("Transfer " + request.getId() + ": Success");
                
                logger.info(String.format("Batch transfer completed: %d -> %d amount %s", 
                          request.getFromId(), request.getToId(), request.getAmount()));
                
            } catch (Exception e) {
                try {
                    if (utx.getStatus() == Status.STATUS_ACTIVE) {
                        utx.rollback();
                    }
                } catch (Exception ex) {
                    logger.severe("Error during rollback: " + ex.getMessage());
                }
                failed.add("Transfer " + request.getId() + ": " + e.getMessage());
                logger.warning("Batch transfer failed: " + e.getMessage());
            }
        }
        
        return new BatchTransferResult(successful, failed);
    }
    
    /**
     * Process transfers with retry logic for optimistic lock failures.
     * 
     * @param fromId Source account ID
     * @param toId Destination account ID
     * @param amount Amount to transfer
     * @param maxRetries Maximum number of retry attempts
     * @return TransferResult indicating success or failure
     */
    public TransferResult transferWithRetry(Long fromId, Long toId, 
                                           BigDecimal amount, int maxRetries) {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < maxRetries) {
            try {
                utx.setTransactionTimeout(30);
                utx.begin();
                
                Account from = em.find(Account.class, fromId);
                Account to = em.find(Account.class, toId);
                
                if (from == null || to == null) {
                    utx.rollback();
                    return TransferResult.error("Account not found");
                }
                
                if (from.getBalance() < amount.doubleValue()) {
                    utx.rollback();
                    return TransferResult.error("Insufficient funds");
                }
                
                from.setBalance(from.getBalance() - amount.doubleValue());
                to.setBalance(to.getBalance() + amount.doubleValue());
                
                utx.commit();
                
                logger.info("Transfer with retry successful after " + (attempts + 1) + " attempts");
                return TransferResult.success();
                
            } catch (Exception e) {
                attempts++;
                lastException = e;
                
                try {
                    if (utx.getStatus() == Status.STATUS_ACTIVE) {
                        utx.rollback();
                    }
                    // Exponential backoff
                    Thread.sleep(100 * attempts);
                } catch (Exception ex) {
                    logger.severe("Error during retry: " + ex.getMessage());
                }
                
                logger.warning("Transfer attempt " + attempts + " failed: " + e.getMessage());
            }
        }
        
        return TransferResult.error("Max retries exceeded: " + 
                                   (lastException != null ? lastException.getMessage() : "Unknown error"));
    }
    
    /**
     * Transfer request data class.
     */
    public static class TransferRequest {
        private String id;
        private Long fromId;
        private Long toId;
        private BigDecimal amount;
        
        public TransferRequest(String id, Long fromId, Long toId, BigDecimal amount) {
            this.id = id;
            this.fromId = fromId;
            this.toId = toId;
            this.amount = amount;
        }
        
        public String getId() { return id; }
        public Long getFromId() { return fromId; }
        public Long getToId() { return toId; }
        public BigDecimal getAmount() { return amount; }
    }
    
    /**
     * Batch transfer result containing successful and failed transfers.
     */
    public static class BatchTransferResult {
        private List<String> successful;
        private List<String> failed;
        
        public BatchTransferResult(List<String> successful, List<String> failed) {
            this.successful = successful;
            this.failed = failed;
        }
        
        public List<String> getSuccessful() { return successful; }
        public List<String> getFailed() { return failed; }
        public int getSuccessCount() { return successful.size(); }
        public int getFailureCount() { return failed.size(); }
    }
    
    /**
     * Transfer result indicating success or failure.
     */
    public static class TransferResult {
        private boolean success;
        private String message;
        
        private TransferResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public static TransferResult success() {
            return new TransferResult(true, "Transfer successful");
        }
        
        public static TransferResult error(String message) {
            return new TransferResult(false, message);
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}

// Made with Bob
