package com.bank.config;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Database Migration Startup Bean
 *
 * This singleton EJB runs Flyway migrations at application startup.
 * It ensures the database schema is up-to-date before the application starts.
 *
 * Uses Bean-Managed Transactions (BMT) to avoid conflicts with JTA transactions.
 *
 * @author Olivier Planson
 * @version 1.0
 */
@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
public class DatabaseMigrationStartup {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseMigrationStartup.class.getName());
    
    @Resource(lookup = "jdbc/bankingDS")
    private DataSource dataSource;
    
    @PostConstruct
    public void init() {
        LOGGER.info("Starting database migration...");
        
        Connection connection = null;
        try {
            // Get a connection and disable auto-commit for manual transaction control
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            
            // Configure Flyway
            FluentConfiguration config = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .cleanDisabled(false)  // Allow clean for development
                    .baselineVersion("0")  // Start from version 0 so all migrations run
                    .baselineOnMigrate(true);  // Create baseline if needed
            
            Flyway flyway = config.load();
            
            // Clean the database first (for development - removes all objects)
            LOGGER.info("Cleaning database...");
            flyway.clean();
            
            // Run migrations
            LOGGER.info("Running migrations...");
            int migrationsApplied = flyway.migrate().migrationsExecuted;
            
            // Commit the transaction
            connection.commit();
            
            LOGGER.info("Database migration completed successfully. " +
                       migrationsApplied + " migration(s) applied.");
            
        } catch (Exception e) {
            LOGGER.severe("Database migration failed: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.severe("Failed to rollback: " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Failed to migrate database", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeEx) {
                    LOGGER.warning("Failed to close connection: " + closeEx.getMessage());
                }
            }
        }
    }
}

// Made with Bob