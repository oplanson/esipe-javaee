package com.bank.listener;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Application Lifecycle Listener
 * 
 * TODO: Implement application lifecycle listener that:
 * 1. Monitors application startup and shutdown
 * 2. Initializes application configuration
 * 3. Stores application metadata in ServletContext
 * 4. Tracks application uptime
 * 5. Performs cleanup on shutdown
 * 
 * @author Your Name
 * @version 1.0
 */
@WebListener
public class ApplicationLifecycleListener implements ServletContextListener {
    
    private static final Logger LOGGER = Logger.getLogger(ApplicationLifecycleListener.class.getName());
    
    // TODO: Create date formatter for logging
    private static final DateTimeFormatter DATE_FORMATTER = null; // TODO: Initialize
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        // TODO: Record startup time (System.currentTimeMillis())
        long startupTime = 0; // TODO: Get current time
        LocalDateTime startupDateTime = null; // TODO: Get current date/time
        
        // TODO: Store startup information in ServletContext
        
        // TODO: Get application information
        String appName = null; // TODO: Get servlet context name
        String contextPath = null; // TODO: Get context path
        int majorVersion = 0; // TODO: Get major version
        int minorVersion = 0; // TODO: Get minor version
        
        // TODO: Store application metadata in ServletContext
        // - appName
        // - appVersion (1.0.0)
        // - servletVersion
        
        // TODO: Initialize session statistics
        // - activeSessions = 0
        // - totalSessions = 0
        
        // TODO: Log startup information
        LOGGER.info("========================================");
        LOGGER.info("APPLICATION STARTUP");
        LOGGER.info("========================================");
        // TODO: Log application details
        LOGGER.info("========================================");
        
        // TODO: Initialize application resources
        initializeResources(context);
        
        LOGGER.info("Application initialized successfully");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        // TODO: Calculate uptime
        Long startupTime = null; // TODO: Get startup time from context
        long uptime = 0; // TODO: Calculate uptime in seconds
        
        // TODO: Get session statistics from ServletContext
        Integer activeSessions = null; // TODO: Get activeSessions
        Integer totalSessions = null; // TODO: Get totalSessions
        
        // TODO: Log shutdown information
        LOGGER.info("========================================");
        LOGGER.info("APPLICATION SHUTDOWN");
        LOGGER.info("========================================");
        // TODO: Log shutdown details and statistics
        LOGGER.info("========================================");
        
        // TODO: Cleanup resources
        cleanupResources(context);
        
        LOGGER.info("Application shutdown complete");
    }
    
    /**
     * TODO: Implement method to initialize application resources
     */
    private void initializeResources(ServletContext context) {
        LOGGER.info("Initializing application resources...");
        
        // TODO: Initialize any required resources
        // - Database connections
        // - Thread pools
        // - Caches
        // - Configuration
        
        LOGGER.info("Application resources initialized");
    }
    
    /**
     * TODO: Implement method to cleanup application resources
     */
    private void cleanupResources(ServletContext context) {
        LOGGER.info("Cleaning up application resources...");
        
        // TODO: Cleanup resources
        // - Close database connections
        // - Shutdown thread pools
        // - Clear caches
        
        // TODO: Clear ServletContext attributes
        
        LOGGER.info("Application resources cleaned up");
    }
    
    /**
     * TODO: Implement method to format uptime in human-readable format
     */
    private String formatUptime(long seconds) {
        // TODO: Calculate days, hours, minutes, seconds
        // TODO: Format as "X days, Y hours, Z minutes, W seconds"
        return "";
    }
}

// Made with Bob
