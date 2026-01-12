// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Readiness health check for Lab 02B - JSF Client Management
 */
@Readiness
@ApplicationScoped
public class WebAppReadinessCheck implements HealthCheck {
    
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse
                .named("JSF Application Readiness")
                .up()
                .withData("status", "ready")
                .withData("application", "Lab 02B - JSF Client Management")
                .build();
    }
}

// Made with Bob
