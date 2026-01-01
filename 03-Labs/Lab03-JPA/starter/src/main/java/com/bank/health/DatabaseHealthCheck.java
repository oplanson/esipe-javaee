package com.bank.health;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Health check for database connectivity.
 * 
 * TODO: Implement the health check:
 * 1. Check if database is available
 * 2. Return UP if connected
 * 3. Return DOWN if not connected
 * 4. Include relevant data in response
 */
@Liveness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {
    
    /**
     * Perform the health check.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Create HealthCheckResponseBuilder with name "database-connection"
     * 2. Call checkDatabase() to test connection
     * 3. If available, return UP with data
     * 4. If not available, return DOWN with error info
     */
    @Override
    public HealthCheckResponse call() {
        
        // TODO: Create response builder
        HealthCheckResponseBuilder builder = HealthCheckResponse
            .named("database-connection");
        
        // TODO: Check database availability
        boolean databaseAvailable = checkDatabase();
        
        // TODO: Build and return response
        // If available:
        //   return builder.up()
        //     .withData("status", "connected")
        //     .withData("type", "in-memory")
        //     .build();
        // If not available:
        //   return builder.down()
        //     .withData("status", "disconnected")
        //     .build();
        
        return null; // TODO: Replace with actual response
    }
    
    /**
     * Check if database is available.
     * 
     * TODO: Implement actual database check
     * For now, simulate a successful connection.
     * In Lab 3, this will check real database connectivity.
     * 
     * @return true if database is available, false otherwise
     */
    private boolean checkDatabase() {
        try {
            // TODO: For now, simulate successful connection
            // In Lab 3, replace with actual database check:
            // - Try to get a connection
            // - Execute a simple query
            // - Return true if successful
            
            return true; // Simulated success
            
        } catch (Exception e) {
            // Log error
            System.err.println("Database health check failed: " + e.getMessage());
            return false;
        }
    }
}

// Made with Bob
