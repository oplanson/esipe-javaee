// © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.gateway.health;

import com.bank.gateway.client.AccountServiceClient;
import com.bank.gateway.client.ClientServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.logging.Logger;

/**
 * Health check for API Gateway / BFF
 * Verifies connectivity to backend microservices
 */
@Readiness
@ApplicationScoped
public class ServicesHealthCheck implements HealthCheck {
    
    private static final Logger LOGGER = Logger.getLogger(ServicesHealthCheck.class.getName());
    
    @Inject
    @RestClient
    private ClientServiceClient clientServiceClient;
    
    @Inject
    @RestClient
    private AccountServiceClient accountServiceClient;
    
    @Override
    public HealthCheckResponse call() {
        boolean clientServiceUp = checkClientService();
        boolean accountServiceUp = checkAccountService();
        
        boolean allServicesUp = clientServiceUp && accountServiceUp;
        
        if (allServicesUp) {
            return HealthCheckResponse
                    .named("backend-services-health")
                    .withData("client-service", "UP")
                    .withData("account-service", "UP")
                    .up()
                    .build();
        } else {
            return HealthCheckResponse
                    .named("backend-services-health")
                    .withData("client-service", clientServiceUp ? "UP" : "DOWN")
                    .withData("account-service", accountServiceUp ? "UP" : "DOWN")
                    .down()
                    .build();
        }
    }
    
    private boolean checkClientService() {
        try {
            clientServiceClient.getAllClients();
            LOGGER.fine("Client Service is UP");
            return true;
        } catch (Exception e) {
            LOGGER.warning("Client Service is DOWN: " + e.getMessage());
            return false;
        }
    }
    
    private boolean checkAccountService() {
        try {
            accountServiceClient.getAllAccounts();
            LOGGER.fine("Account Service is UP");
            return true;
        } catch (Exception e) {
            LOGGER.warning("Account Service is DOWN: " + e.getMessage());
            return false;
        }
    }
}

// Made with Bob
