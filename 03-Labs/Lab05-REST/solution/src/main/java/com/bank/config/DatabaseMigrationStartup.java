package com.bank.config;

import org.flywaydb.core.Flyway;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import java.util.logging.Logger;

/**
 * Servlet context listener that runs Flyway database migrations on application startup.
 * Ensures database schema is created and sample data is loaded.
 * Uses ServletContextListener instead of CDI for Lab 3 (CDI is introduced in Lab 4).
 */
@WebListener
public class DatabaseMigrationStartup implements ServletContextListener {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseMigrationStartup.class.getName());
    
    /**
     * Run Flyway migrations when the application context is initialized.
     * This ensures the database schema is up-to-date before the application starts serving requests.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Starting Flyway database migration...");
        
        try {
            // Lookup DataSource from JNDI
            InitialContext ctx = new InitialContext();
            DataSource dataSource = (DataSource) ctx.lookup("jdbc/bankingDS");
            
            // Configure and run Flyway
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .load();
            
            // Run migrations
            int migrationsExecuted = flyway.migrate().migrationsExecuted;
            
            LOGGER.info("Flyway migration completed successfully. Migrations executed: " + migrationsExecuted);
            
            // Log migration info
            flyway.info().all();
            
        } catch (Exception e) {
            LOGGER.severe("Flyway migration failed: " + e.getMessage());
            e.printStackTrace();
            // Don't throw exception - let application start even if migrations fail
            // This allows for manual database setup if needed
        }
    }
    
    /**
     * Cleanup when the application context is destroyed.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Application context destroyed");
    }
}

// Made with Bob