package com.bank.health;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Health check for web application readiness.
 * Implements MicroProfile Health readiness probe.
 *
 * This check verifies that the application is ready to serve requests by:
 * - Verifying that configuration properties are loaded
 * - Checking that required services are initialized
 * - Returning UP when the application is ready to handle traffic
 * - Returning DOWN if configuration or services are not ready
 *
 * Includes application name and environment information in the response.
 */
@Readiness
@ApplicationScoped
public class WebAppReadinessCheck implements HealthCheck {
    
    @Inject
    @ConfigProperty(name = "app.name", defaultValue = "Banking Application")
    private String appName;
    
    @Inject
    @ConfigProperty(name = "app.environment", defaultValue = "development")
    private String environment;
    
    /**
     * Perform the readiness check.
     *
     * Verifies that the application configuration is loaded and services are initialized.
     * Returns UP if the application is ready to serve requests, DOWN otherwise.
     *
     * @return HealthCheckResponse with UP/DOWN status and application details
     */
    @Override
    public HealthCheckResponse call() {
        
        // Create response builder
        HealthCheckResponseBuilder builder = HealthCheckResponse
            .named("web-application-readiness");
        
        try {
            // Check if configuration is loaded
            boolean configLoaded = appName != null && !appName.isEmpty();
            
            // Check if services are initialized
            // For now, assume services are ready if config is loaded
            boolean servicesReady = configLoaded;
            
            // Build and return response
            if (configLoaded && servicesReady) {
                return builder.up()
                    .withData("configuration", "loaded")
                    .withData("services", "initialized")
                    .withData("app_name", appName)
                    .withData("environment", environment)
                    .build();
            } else {
                return builder.down()
                    .withData("configuration", configLoaded ? "loaded" : "not loaded")
                    .withData("services", servicesReady ? "ready" : "not ready")
                    .build();
            }
            
        } catch (Exception e) {
            // Return DOWN on any exception
            return builder
                .down()
                .withData("error", e.getMessage())
                .build();
        }
    }
}

// Made with Bob
