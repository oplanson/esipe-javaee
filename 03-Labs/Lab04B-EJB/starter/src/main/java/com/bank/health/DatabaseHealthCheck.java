package com.bank.health;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

/**
 * TODO: Database liveness health check
 * 
 * Instructions:
 * 1. Add @Liveness annotation
 * 2. Add @ApplicationScoped annotation
 * 3. Inject EntityManager
 * 4. Implement health check logic
 */
// TODO: Add @Liveness and @ApplicationScoped annotations

public class DatabaseHealthCheck implements HealthCheck {

    // TODO: Inject EntityManager with @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;

    @Override
    public HealthCheckResponse call() {
        // TODO: Implement database health check
        // Execute simple query: SELECT 1
        // Return UP if successful, DOWN if exception
        throw new UnsupportedOperationException("TODO: Implement health check");
    }
}

// Made with Bob