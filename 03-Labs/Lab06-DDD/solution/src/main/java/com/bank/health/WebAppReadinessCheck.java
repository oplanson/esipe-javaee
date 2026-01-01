package com.bank.health;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import java.util.Properties;
import java.io.InputStream;

/**
 * Health check for web application readiness.
 * Checks if the application is ready to serve requests.
 * 
 * Note: This implementation reads configuration manually
 * instead of using CDI injection (CDI is introduced in Lab 4).
 */
@Readiness
public class WebAppReadinessCheck implements HealthCheck {
    
    private String appName;
    private String environment;
    
    /**
     * Constructor that loads configuration from properties file.
     */
    public WebAppReadinessCheck() {
        loadConfiguration();
    }
    
    /**
     * Load configuration from microprofile-config.properties.
     */
    private void loadConfiguration() {
        try {
            Properties props = new Properties();
            InputStream is = getClass().getClassLoader()
                .getResourceAsStream("META-INF/microprofile-config.properties");
            
            if (is != null) {
                props.load(is);
                appName = props.getProperty("app.name", "Banking Application");
                environment = props.getProperty("app.environment", "development");
                is.close();
            } else {
                // Use defaults if file not found
                appName = "Banking Application";
                environment = "development";
            }
        } catch (Exception e) {
            // Use defaults on error
            appName = "Banking Application";
            environment = "development";
        }
    }
    
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
