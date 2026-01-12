package com.bank.ejb;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Singleton Session Bean for application configuration.
 * 
 * Key Features:
 * - Singleton: Single instance per application
 * - Shared across all clients
 * - Initialized at startup with @Startup
 * - Thread-safe with container-managed concurrency
 * - Uses @Lock annotations for read/write access control
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class ConfigServiceBean {
    
    private static final Logger LOGGER = Logger.getLogger(ConfigServiceBean.class.getName());
    
    // Thread-safe configuration storage
    private final Map<String, String> config = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> limits = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        LOGGER.info("Initializing ConfigService...");
        
        // Load default configuration
        loadDefaultConfig();
        loadDefaultLimits();
        
        LOGGER.info("ConfigService initialized successfully");
    }
    
    /**
     * Load default configuration values.
     */
    private void loadDefaultConfig() {
        // Application settings
        config.put("app.name", "Banking EJB Application");
        config.put("app.version", "1.0.0");
        config.put("app.environment", "development");
        
        // Feature flags
        config.put("feature.transfers.enabled", "true");
        config.put("feature.batch.enabled", "true");
        config.put("feature.notifications.enabled", "true");
        
        // Business rules
        config.put("business.min.deposit", "1.00");
        config.put("business.min.withdrawal", "1.00");
        config.put("business.overdraft.allowed", "false");
        
        // System settings
        config.put("system.timezone", "UTC");
        config.put("system.currency", "USD");
        config.put("system.date.format", "yyyy-MM-dd");
        
        LOGGER.info("Loaded " + config.size() + " configuration entries");
    }
    
    /**
     * Load default transaction limits.
     */
    private void loadDefaultLimits() {
        // Daily limits
        limits.put("daily.withdrawal.limit", new BigDecimal("5000.00"));
        limits.put("daily.transfer.limit", new BigDecimal("10000.00"));
        limits.put("daily.deposit.limit", new BigDecimal("50000.00"));
        
        // Per-transaction limits
        limits.put("max.transfer.amount", new BigDecimal("10000.00"));
        limits.put("max.withdrawal.amount", new BigDecimal("2000.00"));
        limits.put("max.deposit.amount", new BigDecimal("50000.00"));
        
        // Minimum balances
        limits.put("min.checking.balance", new BigDecimal("0.00"));
        limits.put("min.savings.balance", new BigDecimal("100.00"));
        
        // Fees
        limits.put("overdraft.fee", new BigDecimal("35.00"));
        limits.put("monthly.maintenance.fee", new BigDecimal("10.00"));
        limits.put("atm.fee", new BigDecimal("2.50"));
        
        LOGGER.info("Loaded " + limits.size() + " limit entries");
    }
    
    /**
     * Get a configuration value.
     * Multiple threads can read simultaneously (READ lock).
     * 
     * @param key The configuration key
     * @return The configuration value or null if not found
     */
    @Lock(LockType.READ)
    public String getConfig(String key) {
        return config.get(key);
    }
    
    /**
     * Get a configuration value with default.
     * 
     * @param key The configuration key
     * @param defaultValue The default value if key not found
     * @return The configuration value or default
     */
    @Lock(LockType.READ)
    public String getConfig(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }
    
    /**
     * Get a boolean configuration value.
     * 
     * @param key The configuration key
     * @return The boolean value or false if not found
     */
    @Lock(LockType.READ)
    public boolean getBooleanConfig(String key) {
        String value = config.get(key);
        return value != null && Boolean.parseBoolean(value);
    }
    
    /**
     * Get an integer configuration value.
     * 
     * @param key The configuration key
     * @param defaultValue The default value if key not found or invalid
     * @return The integer value
     */
    @Lock(LockType.READ)
    public int getIntConfig(String key, int defaultValue) {
        String value = config.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid integer config for key: " + key);
            return defaultValue;
        }
    }
    
    /**
     * Update a configuration value.
     * Only one thread can write at a time (WRITE lock).
     * 
     * @param key The configuration key
     * @param value The configuration value
     */
    @Lock(LockType.WRITE)
    public void updateConfig(String key, String value) {
        String oldValue = config.put(key, value);
        LOGGER.info(String.format("Config updated: %s = %s (was: %s)", key, value, oldValue));
    }
    
    /**
     * Remove a configuration entry.
     * 
     * @param key The configuration key
     */
    @Lock(LockType.WRITE)
    public void removeConfig(String key) {
        String removed = config.remove(key);
        if (removed != null) {
            LOGGER.info("Config removed: " + key);
        }
    }
    
    /**
     * Get all configuration entries.
     * 
     * @return Map of all configuration entries
     */
    @Lock(LockType.READ)
    public Map<String, String> getAllConfig() {
        return new ConcurrentHashMap<>(config);
    }
    
    /**
     * Get a limit value.
     * 
     * @param key The limit key
     * @return The limit value or null if not found
     */
    @Lock(LockType.READ)
    public BigDecimal getLimit(String key) {
        return limits.get(key);
    }
    
    /**
     * Get a limit value with default.
     * 
     * @param key The limit key
     * @param defaultValue The default value if key not found
     * @return The limit value or default
     */
    @Lock(LockType.READ)
    public BigDecimal getLimit(String key, BigDecimal defaultValue) {
        return limits.getOrDefault(key, defaultValue);
    }
    
    /**
     * Update a limit value.
     * 
     * @param key The limit key
     * @param value The limit value
     */
    @Lock(LockType.WRITE)
    public void updateLimit(String key, BigDecimal value) {
        BigDecimal oldValue = limits.put(key, value);
        LOGGER.info(String.format("Limit updated: %s = %s (was: %s)", key, value, oldValue));
    }
    
    /**
     * Get all limits.
     * 
     * @return Map of all limits
     */
    @Lock(LockType.READ)
    public Map<String, BigDecimal> getAllLimits() {
        return new ConcurrentHashMap<>(limits);
    }
    
    /**
     * Check if a feature is enabled.
     * 
     * @param featureName The feature name (without "feature." prefix)
     * @return true if enabled
     */
    @Lock(LockType.READ)
    public boolean isFeatureEnabled(String featureName) {
        return getBooleanConfig("feature." + featureName + ".enabled");
    }
    
    /**
     * Enable or disable a feature.
     * 
     * @param featureName The feature name (without "feature." prefix)
     * @param enabled true to enable, false to disable
     */
    @Lock(LockType.WRITE)
    public void setFeatureEnabled(String featureName, boolean enabled) {
        updateConfig("feature." + featureName + ".enabled", String.valueOf(enabled));
    }
    
    /**
     * Validate if an amount is within limits.
     * 
     * @param limitKey The limit key
     * @param amount The amount to validate
     * @return true if within limit
     */
    @Lock(LockType.READ)
    public boolean isWithinLimit(String limitKey, BigDecimal amount) {
        BigDecimal limit = limits.get(limitKey);
        if (limit == null) {
            LOGGER.warning("Limit not found: " + limitKey);
            return true; // Allow if limit not configured
        }
        return amount.compareTo(limit) <= 0;
    }
    
    /**
     * Get application name.
     * 
     * @return The application name
     */
    @Lock(LockType.READ)
    public String getApplicationName() {
        return getConfig("app.name", "Banking Application");
    }
    
    /**
     * Get application version.
     * 
     * @return The application version
     */
    @Lock(LockType.READ)
    public String getApplicationVersion() {
        return getConfig("app.version", "1.0.0");
    }
    
    /**
     * Get environment name.
     * 
     * @return The environment name
     */
    @Lock(LockType.READ)
    public String getEnvironment() {
        return getConfig("app.environment", "production");
    }
    
    /**
     * Check if running in production.
     * 
     * @return true if production environment
     */
    @Lock(LockType.READ)
    public boolean isProduction() {
        return "production".equalsIgnoreCase(getEnvironment());
    }
    
    /**
     * Reload configuration from defaults.
     * Useful for testing or resetting configuration.
     */
    @Lock(LockType.WRITE)
    public void reloadDefaults() {
        LOGGER.info("Reloading default configuration...");
        config.clear();
        limits.clear();
        loadDefaultConfig();
        loadDefaultLimits();
        LOGGER.info("Configuration reloaded");
    }
    
    @PreDestroy
    public void cleanup() {
        LOGGER.info("ConfigService shutting down...");
        config.clear();
        limits.clear();
    }
}

// Made with Bob