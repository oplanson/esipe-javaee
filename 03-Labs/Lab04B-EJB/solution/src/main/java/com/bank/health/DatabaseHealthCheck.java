package com.bank.health;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

/**
 * Database liveness health check.
 * Verifies that the database connection is alive.
 */
@Liveness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

    @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;

    @Override
    public HealthCheckResponse call() {
        try {
            // Execute a simple query to verify database connectivity
            em.createNativeQuery("SELECT 1").getSingleResult();
            
            return HealthCheckResponse
                .named("database-connection")
                .up()
                .withData("database", "PostgreSQL")
                .withData("status", "connected")
                .build();
                
        } catch (Exception e) {
            return HealthCheckResponse
                .named("database-connection")
                .down()
                .withData("error", e.getMessage())
                .withData("status", "disconnected")
                .build();
        }
    }
}

// Made with Bob