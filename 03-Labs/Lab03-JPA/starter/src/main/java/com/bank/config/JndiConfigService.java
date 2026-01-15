package com.bank.config;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.logging.Logger;

/**
 * Configuration service using JNDI lookups.
 * 
 * TODO: Complete this service to demonstrate JNDI resource lookups.
 * 
 * Learning objectives:
 * - Understand JNDI naming contexts
 * - Lookup DataSource programmatically
 * - Access environment entries
 * - Handle JNDI exceptions properly
 */
@ApplicationScoped
public class JndiConfigService {
    
    private static final Logger logger = Logger.getLogger(JndiConfigService.class.getName());
    
    // TODO 1: Declare a private DataSource field to cache the JNDI lookup
    // private DataSource dataSource;
    
    // TODO 2: Declare fields for configuration values from environment entries
    // private Integer maxLoginAttempts;
    // private String supportEmail;
    // private Double maxTransactionAmount;
    
    /**
     * Initialize JNDI lookups at application startup.
     * 
     * TODO 3: Implement this method to:
     * - Call lookupDataSource()
     * - Call lookupEnvironmentEntries()
     * - Log success or failure
     * - Throw RuntimeException if initialization fails
     */
    @PostConstruct
    public void init() {
        logger.info("Initializing JNDI Configuration Service...");
        
        // TODO: Implement initialization logic
        
    }
    
    /**
     * Lookup DataSource using JNDI.
     * 
     * TODO 4: Implement this method to:
     * - Create an InitialContext
     * - Lookup the DataSource using "java:comp/env/jdbc/bankingDS"
     * - Store it in the dataSource field
     * - Test the connection
     * - Close the InitialContext in a finally block
     * - Handle NamingException appropriately
     * 
     * Hint: Use try-finally to ensure InitialContext is closed
     */
    private void lookupDataSource() throws NamingException {
        InitialContext ctx = null;
        try {
            // TODO: Create InitialContext
            // ctx = new InitialContext();
            
            // TODO: Lookup DataSource
            // String jndiName = "java:comp/env/jdbc/bankingDS";
            // dataSource = (DataSource) ctx.lookup(jndiName);
            
            // TODO: Log success
            
            // TODO: Test the connection
            // try (var conn = dataSource.getConnection()) {
            //     logger.info("DataSource connection test successful");
            // }
            
        } finally {
            // TODO: Close InitialContext
            
        }
    }
    
    /**
     * Lookup environment entries from JNDI.
     * 
     * TODO 5: Implement this method to lookup:
     * - java:comp/env/app/maxLoginAttempts (Integer, default: 3)
     * - java:comp/env/app/supportEmail (String, default: "support@bank.com")
     * - java:comp/env/app/maxTransactionAmount (Double, default: 10000.0)
     * 
     * Hint: Use try-catch for each lookup to provide default values if not found
     */
    private void lookupEnvironmentEntries() throws NamingException {
        InitialContext ctx = null;
        try {
            // TODO: Create InitialContext
            
            // TODO: Lookup maxLoginAttempts
            // try {
            //     maxLoginAttempts = (Integer) ctx.lookup("java:comp/env/app/maxLoginAttempts");
            // } catch (NamingException e) {
            //     maxLoginAttempts = 3; // default
            // }
            
            // TODO: Lookup supportEmail
            
            // TODO: Lookup maxTransactionAmount
            
        } finally {
            // TODO: Close InitialContext
            
        }
    }
    
    /**
     * Get the DataSource obtained via JNDI lookup.
     * 
     * TODO 6: Implement this getter
     * - Check if dataSource is null and throw IllegalStateException
     * - Return the dataSource
     */
    public DataSource getDataSource() {
        // TODO: Implement
        return null;
    }
    
    /**
     * Get maximum number of login attempts allowed.
     * 
     * TODO 7: Implement this getter
     */
    public Integer getMaxLoginAttempts() {
        // TODO: Implement
        return null;
    }
    
    /**
     * Get support email address.
     * 
     * TODO 8: Implement this getter
     */
    public String getSupportEmail() {
        // TODO: Implement
        return null;
    }
    
    /**
     * Get maximum transaction amount allowed.
     * 
     * TODO 9: Implement this getter
     */
    public Double getMaxTransactionAmount() {
        // TODO: Implement
        return null;
    }
    
    /**
     * Check if a transaction amount is within limits.
     * 
     * TODO 10: Implement this validation method
     * - Return false if amount is null or <= 0
     * - Return true if amount <= maxTransactionAmount
     */
    public boolean isValidTransactionAmount(Double amount) {
        // TODO: Implement
        return false;
    }
    
    /**
     * Get all configuration as a formatted string.
     * 
     * TODO 11: Implement this method to return a summary of all configuration
     * Use String.format() to create a nicely formatted output
     */
    public String getConfigurationSummary() {
        // TODO: Implement
        return "Not implemented";
    }
}

// Made with Bob
