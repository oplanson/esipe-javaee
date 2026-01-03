// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

/**
 * Liveness Health Check for API Gateway
 * Checks if the gateway is alive and running
 */
@Liveness
@ApplicationScoped
public class GatewayLivenessCheck implements HealthCheck {
    
    @Override
    public HealthCheckResponse call() {
        // Simple liveness check - if this code runs, the gateway is alive
        return HealthCheckResponse.named("api-gateway-liveness")
                .up()
                .withData("service", "alive")
                .withData("status", "running")
                .build();
    }
}

// Made with Bob