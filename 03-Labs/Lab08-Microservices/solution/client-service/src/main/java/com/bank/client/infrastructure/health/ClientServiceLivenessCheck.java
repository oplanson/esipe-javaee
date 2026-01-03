// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.client.infrastructure.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

/**
 * Liveness Health Check for Client Service
 * Checks if the service is alive and running
 */
@Liveness
@ApplicationScoped
public class ClientServiceLivenessCheck implements HealthCheck {
    
    @Override
    public HealthCheckResponse call() {
        // Simple liveness check - if this code runs, the service is alive
        return HealthCheckResponse.named("client-service-liveness")
                .up()
                .withData("service", "alive")
                .withData("status", "running")
                .build();
    }
}

// Made with Bob