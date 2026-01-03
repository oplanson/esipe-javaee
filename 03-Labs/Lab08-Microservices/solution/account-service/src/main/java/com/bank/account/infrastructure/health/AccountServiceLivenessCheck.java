// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.account.infrastructure.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

/**
 * Liveness Health Check for Account Service
 * Checks if the service is alive and running
 */
@Liveness
@ApplicationScoped
public class AccountServiceLivenessCheck implements HealthCheck {
    
    @Override
    public HealthCheckResponse call() {
        // Simple liveness check - if this code runs, the service is alive
        return HealthCheckResponse.named("account-service-liveness")
                .up()
                .withData("service", "alive")
                .withData("status", "running")
                .build();
    }
}

// Made with Bob