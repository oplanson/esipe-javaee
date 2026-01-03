// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.infrastructure.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * Health check for Account Service readiness
 * Verifies database connectivity and service availability
 */
@Readiness
@ApplicationScoped
public class AccountServiceHealthCheck implements HealthCheck {
    
    @PersistenceContext(unitName = "accountPU")
    private EntityManager entityManager;
    
    @Override
    public HealthCheckResponse call() {
        try {
            // Test database connectivity
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            
            // Test account table exists
            Long count = entityManager.createQuery(
                "SELECT COUNT(a) FROM AccountEntity a", 
                Long.class
            ).getSingleResult();
            
            return HealthCheckResponse.named("account-service-readiness")
                .up()
                .withData("database", "connected")
                .withData("accounts_count", count)
                .withData("service", "ready")
                .build();
                
        } catch (Exception e) {
            return HealthCheckResponse.named("account-service-readiness")
                .down()
                .withData("database", "disconnected")
                .withData("error", e.getMessage())
                .withData("service", "not ready")
                .build();
        }
    }
}

// Made with Bob
