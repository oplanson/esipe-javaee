/*
 * © Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited. Made with IBM Bob.
 */
package com.bank.service;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import java.util.logging.Logger;

/**
 * Service for testing transaction timeout behavior.
 * Demonstrates how transaction timeouts work and how to configure them.
 */
@ApplicationScoped
public class TimeoutTestService {
    
    @Resource
    private UserTransaction utx;
    
    @Inject
    private Logger logger;
    
    /**
     * Test transaction timeout with short timeout (2 seconds).
     * This test should fail because the operation takes longer than the timeout.
     * 
     * @return Result message indicating success or expected timeout
     */
    public String testShortTimeout() {
        try {
            // Set very short timeout (2 seconds)
            utx.setTransactionTimeout(2);
            utx.begin();
            
            logger.info("Transaction started with 2 second timeout");
            
            // Simulate long operation (5 seconds)
            Thread.sleep(5000);
            
            utx.commit();
            return "SUCCESS: Transaction committed (unexpected!)";
            
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                // Ignore rollback errors
            }
            logger.warning("Transaction timed out as expected: " + e.getMessage());
            return "EXPECTED: Transaction timed out - " + e.getMessage();
        }
    }
    
    /**
     * Test transaction with adequate timeout (10 seconds).
     * This test should succeed because the operation completes within the timeout.
     * 
     * @return Result message indicating success or failure
     */
    public String testAdequateTimeout() {
        try {
            // Set adequate timeout (10 seconds)
            utx.setTransactionTimeout(10);
            utx.begin();
            
            logger.info("Transaction started with 10 second timeout");
            
            // Simulate operation (2 seconds)
            Thread.sleep(2000);
            
            utx.commit();
            logger.info("Transaction committed successfully within timeout");
            return "SUCCESS: Transaction committed within timeout";
            
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                // Ignore rollback errors
            }
            logger.severe("Transaction failed unexpectedly: " + e.getMessage());
            return "ERROR: Transaction failed - " + e.getMessage();
        }
    }
    
    /**
     * Test transaction with no explicit timeout (uses server default).
     * 
     * @return Result message
     */
    public String testDefaultTimeout() {
        try {
            // No explicit timeout - uses server default
            utx.begin();
            
            logger.info("Transaction started with default timeout");
            
            // Simulate operation (1 second)
            Thread.sleep(1000);
            
            utx.commit();
            logger.info("Transaction committed with default timeout");
            return "SUCCESS: Transaction committed with default timeout";
            
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (Exception ex) {
                // Ignore rollback errors
            }
            logger.severe("Transaction failed: " + e.getMessage());
            return "ERROR: Transaction failed - " + e.getMessage();
        }
    }
    
    /**
     * Run all timeout tests and return results.
     * 
     * @return Combined results of all tests
     */
    public String runAllTests() {
        StringBuilder results = new StringBuilder();
        
        results.append("=== Transaction Timeout Tests ===\n\n");
        
        results.append("Test 1: Short Timeout (2s with 5s operation)\n");
        results.append(testShortTimeout()).append("\n\n");
        
        // Wait a bit between tests
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        results.append("Test 2: Adequate Timeout (10s with 2s operation)\n");
        results.append(testAdequateTimeout()).append("\n\n");
        
        // Wait a bit between tests
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        results.append("Test 3: Default Timeout (1s operation)\n");
        results.append(testDefaultTimeout()).append("\n");
        
        return results.toString();
    }
}

// Made with Bob
