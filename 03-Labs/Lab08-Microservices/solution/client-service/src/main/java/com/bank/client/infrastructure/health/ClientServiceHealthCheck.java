// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.client.infrastructure.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * Readiness Health Check for Client Service
 * Checks if the service is ready to accept requests
 */
@Readiness
@ApplicationScoped
public class ClientServiceHealthCheck implements HealthCheck {
    
    @PersistenceContext(unitName = "clientPU")
    private EntityManager entityManager;
    
    @Override
    public HealthCheckResponse call() {
        try {
            // Check database connectivity
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            
            return HealthCheckResponse.named("client-service-readiness")
                    .up()
                    .withData("database", "connected")
                    .withData("service", "ready")
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse.named("client-service-readiness")
                    .down()
                    .withData("database", "disconnected")
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}

// Made with Bob
