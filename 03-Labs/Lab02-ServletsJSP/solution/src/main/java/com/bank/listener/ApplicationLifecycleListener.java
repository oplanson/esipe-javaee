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
 * This listener monitors the application lifecycle and performs initialization/cleanup tasks.
 * It demonstrates:
 * - Application startup and shutdown events
 * - ServletContext initialization
 * - Application-wide configuration
 * - Resource management
 * - Logging and monitoring
 * 
 * Features:
 * - Log application startup/shutdown
 * - Initialize application configuration
 * - Store application metadata
 * - Track application uptime
 * - Perform cleanup on shutdown
 * 
 * @author Olivier Planson
 * @version 1.0
 */
@WebListener
public class ApplicationLifecycleListener implements ServletContextListener {
    
    private static final Logger LOGGER = Logger.getLogger(ApplicationLifecycleListener.class.getName());
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        // Record startup time
        long startupTime = System.currentTimeMillis();
        LocalDateTime startupDateTime = LocalDateTime.now();
        
        // Store startup information in ServletContext
        context.setAttribute("startupTime", startupTime);
        context.setAttribute("startupDateTime", startupDateTime.format(DATE_FORMATTER));
        
        // Get application information
        String appName = context.getServletContextName();
        String contextPath = context.getContextPath();
        int majorVersion = context.getMajorVersion();
        int minorVersion = context.getMinorVersion();
        
        // Store application metadata
        context.setAttribute("appName", appName != null ? appName : "Banking Application");
        context.setAttribute("appVersion", "1.0.0");
        context.setAttribute("servletVersion", majorVersion + "." + minorVersion);
        
        // Initialize session statistics
        context.setAttribute("activeSessions", 0);
        context.setAttribute("totalSessions", 0);
        
        // Log startup information
        LOGGER.info("========================================");
        LOGGER.info("APPLICATION STARTUP");
        LOGGER.info("========================================");
        LOGGER.info("Application Name: " + (appName != null ? appName : "Banking Application"));
        LOGGER.info("Context Path: " + contextPath);
        LOGGER.info("Servlet Version: " + majorVersion + "." + minorVersion);
        LOGGER.info("Startup Time: " + startupDateTime.format(DATE_FORMATTER));
        LOGGER.info("Java Version: " + System.getProperty("java.version"));
        LOGGER.info("Java Vendor: " + System.getProperty("java.vendor"));
        LOGGER.info("OS Name: " + System.getProperty("os.name"));
        LOGGER.info("OS Version: " + System.getProperty("os.version"));
        LOGGER.info("========================================");
        
        // Initialize application resources
        initializeResources(context);
        
        LOGGER.info("Application initialized successfully");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        // Calculate uptime
        Long startupTime = (Long) context.getAttribute("startupTime");
        long uptime = 0;
        if (startupTime != null) {
            uptime = (System.currentTimeMillis() - startupTime) / 1000; // in seconds
        }
        
        // Get session statistics
        Integer activeSessions = (Integer) context.getAttribute("activeSessions");
        Integer totalSessions = (Integer) context.getAttribute("totalSessions");
        
        // Log shutdown information
        LOGGER.info("========================================");
        LOGGER.info("APPLICATION SHUTDOWN");
        LOGGER.info("========================================");
        LOGGER.info("Shutdown Time: " + LocalDateTime.now().format(DATE_FORMATTER));
        LOGGER.info("Uptime: " + formatUptime(uptime));
        LOGGER.info("Active Sessions: " + (activeSessions != null ? activeSessions : 0));
        LOGGER.info("Total Sessions: " + (totalSessions != null ? totalSessions : 0));
        LOGGER.info("========================================");
        
        // Cleanup resources
        cleanupResources(context);
        
        LOGGER.info("Application shutdown complete");
    }
    
    /**
     * Initialize application resources
     */
    private void initializeResources(ServletContext context) {
        LOGGER.info("Initializing application resources...");
        
        // Initialize any required resources here
        // For example: database connections, thread pools, caches, etc.
        
        // Set default configuration values
        if (context.getInitParameter("web.pagination.default.size") == null) {
            LOGGER.info("Setting default pagination size: 10");
        }
        
        LOGGER.info("Application resources initialized");
    }
    
    /**
     * Cleanup application resources
     */
    private void cleanupResources(ServletContext context) {
        LOGGER.info("Cleaning up application resources...");
        
        // Cleanup any resources here
        // For example: close database connections, shutdown thread pools, clear caches, etc.
        
        // Clear ServletContext attributes
        context.removeAttribute("startupTime");
        context.removeAttribute("startupDateTime");
        context.removeAttribute("activeSessions");
        context.removeAttribute("totalSessions");
        
        LOGGER.info("Application resources cleaned up");
    }
    
    /**
     * Format uptime in human-readable format
     */
    private String formatUptime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" day").append(days > 1 ? "s" : "").append(", ");
        }
        if (hours > 0 || days > 0) {
            sb.append(hours).append(" hour").append(hours != 1 ? "s" : "").append(", ");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            sb.append(minutes).append(" minute").append(minutes != 1 ? "s" : "").append(", ");
        }
        sb.append(secs).append(" second").append(secs != 1 ? "s" : "");
        
        return sb.toString();
    }
}

// Made with Bob
