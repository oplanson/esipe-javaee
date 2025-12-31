package com.bank.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Health check for web application readiness.
 * Checks if the application is ready to serve requests.
 * 
 * TODO: Implement the readiness check:
 * 1. Verify configuration is loaded
 * 2. Check if services are initialized
 * 3. Return UP if ready
 * 4. Return DOWN if not ready
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
     * TODO: Implement this method
     * Steps:
     * 1. Create HealthCheckResponseBuilder with name "web-application-readiness"
     * 2. Check if configuration is loaded (appName not null/empty)
     * 3. Check if services are ready
     * 4. Return UP with data if ready
     * 5. Return DOWN with error info if not ready
     */
    @Override
    public HealthCheckResponse call() {
        
        // TODO: Create response builder
        HealthCheckResponseBuilder builder = HealthCheckResponse
            .named("web-application-readiness");
        
        try {
            // TODO: Check if configuration is loaded
            boolean configLoaded = appName != null && !appName.isEmpty();
            
            // TODO: Check if services are initialized
            // For now, assume services are ready if config is loaded
            boolean servicesReady = configLoaded;
            
            // TODO: Build and return response
            // If ready:
            //   return builder.up()
            //     .withData("configuration", "loaded")
            //     .withData("services", "initialized")
            //     .withData("app_name", appName)
            //     .withData("environment", environment)
            //     .build();
            // If not ready:
            //   return builder.down()
            //     .withData("configuration", configLoaded ? "loaded" : "not loaded")
            //     .withData("services", servicesReady ? "ready" : "not ready")
            //     .build();
            
            return null; // TODO: Replace with actual response
            
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
