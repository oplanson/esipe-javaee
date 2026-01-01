package com.bank.health;

/* © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Health check for database connectivity.
 * Implements MicroProfile Health liveness probe.
 *
 * This check verifies that the database connection is available.
 * - Returns UP if database is connected
 * - Returns DOWN if database is not available
 * - Includes connection status and type in response data
 *
 * Note: In Lab 2, this simulates a database connection.
 * In Lab 3, this will check actual PostgreSQL connectivity.
 */
@Liveness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {
    
    /**
     * Perform the health check.
     *
     * Creates a health check response indicating database connectivity status.
     *
     * @return HealthCheckResponse with UP/DOWN status and connection details
     */
    @Override
    public HealthCheckResponse call() {
        
        // Create response builder
        HealthCheckResponseBuilder builder = HealthCheckResponse
            .named("database-connection");
        
        // Check database availability
        boolean databaseAvailable = checkDatabase();
        
        // Build and return response
        if (databaseAvailable) {
            return builder.up()
                .withData("status", "connected")
                .withData("type", "in-memory")
                .build();
        } else {
            return builder.down()
                .withData("status", "disconnected")
                .build();
        }
    }
    
    /**
     * Check if database is available.
     *
     * In Lab 2, this simulates a successful database connection.
     * In Lab 3, this will be replaced with actual PostgreSQL connectivity check:
     * - Obtain a connection from the DataSource
     * - Execute a simple validation query (SELECT 1)
     * - Return true if successful, false otherwise
     *
     * @return true if database is available, false otherwise
     */
    private boolean checkDatabase() {
        try {
            // Simulate successful connection for Lab 2
            // This will be replaced with actual database check in Lab 3
            return true;
            
        } catch (Exception e) {
            System.err.println("Database health check failed: " + e.getMessage());
            return false;
        }
    }
}

// Made with Bob
