package com.bank.config;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Configuration service using JNDI lookups.
 * Demonstrates how to access resources and environment entries via JNDI.
 * 
 * This service shows:
 * - Looking up DataSource via JNDI
 * - Reading environment entries
 * - Caching JNDI lookups for performance
 * - Proper error handling
 */
@ApplicationScoped
public class JndiConfigService {
    
    private static final Logger logger = Logger.getLogger(JndiConfigService.class.getName());
    
    // Singleton instance
    private static JndiConfigService instance;
    
    // Cached DataSource from JNDI lookup
    private DataSource dataSource;
    
    // Configuration values from environment entries
    private Integer maxLoginAttempts;
    private String supportEmail;
    private Double maxTransactionAmount;
    
    /**
     * Get singleton instance of JndiConfigService.
     * Creates and initializes the instance on first call.
     *
     * @return singleton instance
     */
    public static synchronized JndiConfigService getInstance() {
        if (instance == null) {
            instance = new JndiConfigService();
            try {
                instance.init();
            } catch (Exception e) {
                logger.severe("Failed to initialize JndiConfigService: " + e.getMessage());
                throw new RuntimeException("JndiConfigService initialization failed", e);
            }
        }
        return instance;
    }
    
    /**
     * Initialize JNDI lookups at application startup.
     * This method is called once when the bean is created.
     */
    @PostConstruct
    public void init() {
        logger.info("Initializing JNDI Configuration Service...");
        
        try {
            // Lookup DataSource
            lookupDataSource();
            
            // Lookup environment entries
            lookupEnvironmentEntries();
            
            logger.info("JNDI Configuration Service initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize JNDI Configuration Service: " + e.getMessage());
            throw new RuntimeException("JNDI initialization failed", e);
        }
    }
    
    /**
     * Lookup DataSource using JNDI.
     * Uses portable JNDI name: java:comp/env/jdbc/bankingDS
     */
    private void lookupDataSource() throws NamingException, SQLException {
        InitialContext ctx = null;
        try {
            ctx = new InitialContext();
            
            // Portable JNDI name (works across all Jakarta EE servers)
            String jndiName = "java:comp/env/jdbc/bankingDS";
            
            dataSource = (DataSource) ctx.lookup(jndiName);
            logger.info("DataSource successfully looked up via JNDI: " + jndiName);
            
            // Test the connection
            try (var conn = dataSource.getConnection()) {
                logger.info("DataSource connection test successful");
            }
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    logger.warning("Failed to close InitialContext: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Lookup environment entries from JNDI.
     * Environment entries are configured in web.xml.
     */
    private void lookupEnvironmentEntries() throws NamingException {
        InitialContext ctx = null;
        try {
            ctx = new InitialContext();
            
            // Lookup max login attempts
            try {
                maxLoginAttempts = (Integer) ctx.lookup("java:comp/env/app/maxLoginAttempts");
                logger.info("Max login attempts: " + maxLoginAttempts);
            } catch (NamingException e) {
                logger.warning("app/maxLoginAttempts not found, using default: 3");
                maxLoginAttempts = 3;
            }
            
            // Lookup support email
            try {
                supportEmail = (String) ctx.lookup("java:comp/env/app/supportEmail");
                logger.info("Support email: " + supportEmail);
            } catch (NamingException e) {
                logger.warning("app/supportEmail not found, using default");
                supportEmail = "support@bank.com";
            }
            
            // Lookup max transaction amount
            try {
                maxTransactionAmount = (Double) ctx.lookup("java:comp/env/app/maxTransactionAmount");
                logger.info("Max transaction amount: " + maxTransactionAmount);
            } catch (NamingException e) {
                logger.warning("app/maxTransactionAmount not found, using default: 10000.0");
                maxTransactionAmount = 10000.0;
            }
            
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    logger.warning("Failed to close InitialContext: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Get the DataSource obtained via JNDI lookup.
     * This is cached for performance.
     * 
     * @return DataSource instance
     */
    public DataSource getDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource not initialized");
        }
        return dataSource;
    }
    
    /**
     * Get maximum number of login attempts allowed.
     * 
     * @return max login attempts
     */
    public Integer getMaxLoginAttempts() {
        return maxLoginAttempts;
    }
    
    /**
     * Get support email address.
     * 
     * @return support email
     */
    public String getSupportEmail() {
        return supportEmail;
    }
    
    /**
     * Get maximum transaction amount allowed.
     * 
     * @return max transaction amount
     */
    public Double getMaxTransactionAmount() {
        return maxTransactionAmount;
    }
    
    /**
     * Check if a transaction amount is within limits.
     * 
     * @param amount transaction amount to check
     * @return true if amount is valid, false otherwise
     */
    public boolean isValidTransactionAmount(Double amount) {
        if (amount == null || amount <= 0) {
            return false;
        }
        return amount <= maxTransactionAmount;
    }
    
    /**
     * Get all configuration as a formatted string.
     * Useful for debugging and health checks.
     * 
     * @return configuration summary
     */
    public String getConfigurationSummary() {
        return String.format(
            "JNDI Configuration:%n" +
            "  - DataSource: %s%n" +
            "  - Max Login Attempts: %d%n" +
            "  - Support Email: %s%n" +
            "  - Max Transaction Amount: %.2f",
            (dataSource != null ? "Configured" : "Not configured"),
            maxLoginAttempts,
            supportEmail,
            maxTransactionAmount
        );
    }
}

// Made with Bob
