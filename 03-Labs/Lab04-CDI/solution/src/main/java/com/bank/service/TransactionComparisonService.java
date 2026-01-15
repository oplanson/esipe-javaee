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
import jakarta.transaction.Transactional;
import jakarta.transaction.UserTransaction;
import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Service comparing Container-Managed Transactions (CMT) vs Bean-Managed Transactions (BMT).
 * Demonstrates the differences in code complexity and control.
 */
@ApplicationScoped
public class TransactionComparisonService {
    
    @Inject
    private EntityManager em;
    
    @Resource
    private UserTransaction utx;
    
    @Inject
    private Logger logger;
    
    /**
     * CMT Approach: Simple and clean.
     * Container automatically manages transaction boundaries.
     * 
     * @param fromId Source account ID
     * @param toId Destination account ID
     * @param amount Amount to transfer
     */
    @Transactional
    public void transferCMT(Long fromId, Long toId, BigDecimal amount) {
        Account from = em.find(Account.class, fromId);
        Account to = em.find(Account.class, toId);
        
        if (from.getBalance() < amount.doubleValue()) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        from.setBalance(from.getBalance() - amount.doubleValue());
        to.setBalance(to.getBalance() + amount.doubleValue());
        
        // Transaction automatically committed or rolled back
        logger.info("CMT transfer completed");
    }
    
    /**
     * BMT Approach: More control, more code.
     * Developer manually manages transaction boundaries.
     * 
     * @param fromId Source account ID
     * @param toId Destination account ID
     * @param amount Amount to transfer
     * @throws Exception if transfer fails
     */
    public void transferBMT(Long fromId, Long toId, BigDecimal amount) throws Exception {
        try {
            utx.begin();
            
            Account from = em.find(Account.class, fromId);
            Account to = em.find(Account.class, toId);
            
            if (from.getBalance() < amount.doubleValue()) {
                utx.rollback();
                throw new IllegalStateException("Insufficient funds");
            }
            
            from.setBalance(from.getBalance() - amount.doubleValue());
            to.setBalance(to.getBalance() + amount.doubleValue());
            
            utx.commit();
            logger.info("BMT transfer completed");
            
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                logger.severe("Rollback failed: " + ex.getMessage());
            }
            throw e;
        }
    }
    
    /**
     * Measure execution time for CMT approach.
     * 
     * @param fromId Source account ID
     * @param toId Destination account ID
     * @param amount Amount to transfer
     * @return Execution time in nanoseconds
     */
    public long measureCMT(Long fromId, Long toId, BigDecimal amount) {
        long start = System.nanoTime();
        try {
            transferCMT(fromId, toId, amount);
        } catch (Exception e) {
            logger.warning("CMT transfer failed: " + e.getMessage());
        }
        long duration = System.nanoTime() - start;
        logger.info("CMT execution time: " + (duration / 1_000_000) + " ms");
        return duration;
    }
    
    /**
     * Measure execution time for BMT approach.
     * 
     * @param fromId Source account ID
     * @param toId Destination account ID
     * @param amount Amount to transfer
     * @return Execution time in nanoseconds
     */
    public long measureBMT(Long fromId, Long toId, BigDecimal amount) {
        long start = System.nanoTime();
        try {
            transferBMT(fromId, toId, amount);
        } catch (Exception e) {
            logger.warning("BMT transfer failed: " + e.getMessage());
        }
        long duration = System.nanoTime() - start;
        logger.info("BMT execution time: " + (duration / 1_000_000) + " ms");
        return duration;
    }
    
    /**
     * Compare CMT vs BMT performance.
     * 
     * @param fromId Source account ID
     * @param toId Destination account ID
     * @param amount Amount to transfer
     * @return Comparison result
     */
    public String comparePerformance(Long fromId, Long toId, BigDecimal amount) {
        long cmtTime = measureCMT(fromId, toId, amount);
        long bmtTime = measureBMT(fromId, toId, amount);
        
        double cmtMs = (double) cmtTime / 1_000_000.0;
        double bmtMs = (double) bmtTime / 1_000_000.0;
        double difference = Math.abs(cmtMs - bmtMs);
        
        return String.format(
            "CMT: %.2f ms | BMT: %.2f ms | Difference: %.2f ms | Winner: %s",
            cmtMs, bmtMs, difference,
            cmtTime < bmtTime ? "CMT" : "BMT"
        );
    }
}

// Made with Bob
