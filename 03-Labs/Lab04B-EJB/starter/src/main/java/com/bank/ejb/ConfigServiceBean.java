package com.bank.ejb;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO: Part 3 - Singleton Session Bean
 * 
 * Instructions:
 * 1. Add @Singleton annotation
 * 2. Add @Startup for eager initialization
 * 3. Add @ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
 * 4. Use @Lock annotations for read/write methods
 * 5. Implement configuration storage
 */
// TODO: Add @Singleton and @Startup annotations

public class ConfigServiceBean {

    private final Map<String, String> config = new ConcurrentHashMap<>();

    /**
     * TODO: Initialize configuration on startup
     * - Add @PostConstruct annotation
     * - Load default configuration values
     */
    // TODO: Add @PostConstruct
    public void init() {
        // TODO: Initialize with default config
        config.put("app.name", "Lab 04B - EJB Banking");
        config.put("app.version", "1.0.0");
    }

    /**
     * TODO: Get configuration value
     * - Add @Lock(LockType.READ) for concurrent reads
     * - Return value from config map
     */
    // TODO: Add @Lock(LockType.READ)
    public String getConfig(String key) {
        // TODO: Implement get config
        throw new UnsupportedOperationException("TODO: Implement getConfig");
    }

    /**
     * TODO: Update configuration value
     * - Add @Lock(LockType.WRITE) for exclusive writes
     * - Put value in config map
     */
    // TODO: Add @Lock(LockType.WRITE)
    public void updateConfig(String key, String value) {
        // TODO: Implement update config
        throw new UnsupportedOperationException("TODO: Implement updateConfig");
    }

    /**
     * TODO: Get all configuration
     * - Add @Lock(LockType.READ)
     * - Return copy of config map
     */
    // TODO: Add @Lock(LockType.READ)
    public Map<String, String> getAllConfig() {
        // TODO: Implement get all config
        throw new UnsupportedOperationException("TODO: Implement getAllConfig");
    }
}

// Made with Bob