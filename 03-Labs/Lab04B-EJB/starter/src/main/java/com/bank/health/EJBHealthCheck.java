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
 * TODO: EJB readiness health check
 * 
 * Instructions:
 * 1. Add @Readiness annotation
 * 2. Add @ApplicationScoped annotation
 * 3. Inject EJB beans
 * 4. Verify beans are accessible
 */
// TODO: Add @Readiness and @ApplicationScoped annotations

public class EJBHealthCheck implements HealthCheck {

    // TODO: Inject AccountServiceBean with @EJB
    private AccountServiceBean accountService;
    
    // TODO: Inject ConfigServiceBean with @EJB
    private ConfigServiceBean configService;

    @Override
    public HealthCheckResponse call() {
        // TODO: Implement EJB health check
        // Verify beans are not null
        // Test a simple operation
        // Return UP if successful, DOWN if exception
        throw new UnsupportedOperationException("TODO: Implement health check");
    }
}

// Made with Bob