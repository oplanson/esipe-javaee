package com.bank.health;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Health check for database connectivity.
 * Tests actual database connection using JPA EntityManager.
 * 
 * Note: This implementation uses manual EntityManager creation
 * instead of CDI injection (CDI is introduced in Lab 4).
 */
@Liveness
public class DatabaseHealthCheck implements HealthCheck {
    
    private static EntityManagerFactory emf;
    
    /**
     * Get or create the EntityManagerFactory.
     */
    private static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory("bankingPU");
        }
        return emf;
    }
    
    /**
     * Perform the health check.
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
                .withData("type", "postgresql")
                .build();
        } else {
            return builder.down()
                .withData("status", "disconnected")
                .build();
        }
    }
    
    /**
     * Check if database is available by executing a simple query.
     *
     * @return true if database is available, false otherwise
     */
    private boolean checkDatabase() {
        EntityManager em = null;
        try {
            // Create EntityManager
            em = getEntityManagerFactory().createEntityManager();
            
            // Execute a simple query to test database connectivity
            em.createNativeQuery("SELECT 1").getSingleResult();
            return true;
            
        } catch (Exception e) {
            // Log error
            System.err.println("Database health check failed: " + e.getMessage());
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}

// Made with Bob
