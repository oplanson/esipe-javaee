package com.bank.health;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.ejb.AccountServiceBean;
import com.bank.ejb.ConfigServiceBean;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * EJB readiness health check.
 * Verifies that EJB components are ready to handle requests.
 */
@Readiness
@ApplicationScoped
public class EJBHealthCheck implements HealthCheck {

    @EJB
    private AccountServiceBean accountService;
    
    @EJB
    private ConfigServiceBean configService;

    @Override
    public HealthCheckResponse call() {
        try {
            // Verify EJB beans are accessible
            if (accountService == null || configService == null) {
                return HealthCheckResponse
                    .named("ejb-container")
                    .down()
                    .withData("error", "EJB beans not injected")
                    .build();
            }
            
            // Test configuration service
            String testValue = configService.getConfig("app.name");
            
            return HealthCheckResponse
                .named("ejb-container")
                .up()
                .withData("accountService", "available")
                .withData("configService", "available")
                .withData("status", "ready")
                .build();
                
        } catch (Exception e) {
            return HealthCheckResponse
                .named("ejb-container")
                .down()
                .withData("error", e.getMessage())
                .withData("status", "not ready")
                .build();
        }
    }
}

// Made with Bob