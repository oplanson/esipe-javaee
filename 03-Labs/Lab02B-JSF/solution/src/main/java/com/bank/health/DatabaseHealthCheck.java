// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
package com.bank.health;

import com.bank.service.ClientService;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Liveness health check for Lab 02B - JSF Client Management
 */
@Liveness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {
    
    @Inject
    private ClientService clientService;
    
    @Override
    public HealthCheckResponse call() {
        try {
            int clientCount = clientService.getClientCount();
            return HealthCheckResponse
                    .named("Client Service Health")
                    .up()
                    .withData("clientCount", clientCount)
                    .withData("status", "operational")
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse
                    .named("Client Service Health")
                    .down()
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}

// Made with Bob
