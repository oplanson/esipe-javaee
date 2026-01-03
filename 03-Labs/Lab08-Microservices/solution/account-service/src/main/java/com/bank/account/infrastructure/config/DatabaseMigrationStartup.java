// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.infrastructure.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.util.logging.Logger;

/**
 * Startup bean that runs Flyway database migrations on application startup
 */
@Singleton
@Startup
public class DatabaseMigrationStartup {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseMigrationStartup.class.getName());
    
    @Resource(lookup = "jdbc/accountDS")
    private DataSource dataSource;
    
    @PostConstruct
    public void init() {
        LOGGER.info("Starting Flyway database migration...");
        
        try {
            // Get database connection details from environment or system properties
            String dbHost = System.getenv().getOrDefault("DB_HOST", "banking-account-db");
            String dbPort = System.getenv().getOrDefault("DB_PORT", "5432");
            String dbName = System.getenv().getOrDefault("DB_NAME", "banking_account_db");
            String dbUser = System.getenv().getOrDefault("DB_USER", "bankuser");
            String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "bankpass");
            
            String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbName);
            
            LOGGER.info("Flyway connecting to: " + jdbcUrl);
            
            // Configure Flyway with direct JDBC connection (non-JTA)
            Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, dbUser, dbPassword)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
            
            int migrationsExecuted = flyway.migrate().migrationsExecuted;
            
            LOGGER.info("Flyway migration completed successfully. Migrations executed: " + migrationsExecuted);
            
        } catch (Exception e) {
            LOGGER.severe("Flyway migration failed: " + e.getMessage());
            throw new RuntimeException("Database migration failed", e);
        }
    }
}

// Made with Bob
