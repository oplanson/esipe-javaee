package com.bank.service;

import com.bank.config.Standard;
import com.bank.model.Account;
import com.bank.model.Client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.logging.Logger;

/**
 * Standard implementation of NotificationService.
 * Provides basic notifications.
 * 
 * Lab 04 - Advanced CDI: Qualifiers
 * 
 * This implementation is selected when injecting with @Standard qualifier:
 * <pre>
 * {@code
 * @Inject
 * @Standard
 * private NotificationService notificationService;
 * }
 * </pre>
 */
@ApplicationScoped
@Standard
public class StandardNotificationService implements NotificationService {
    
    @Inject
    private Logger logger;
    
    @Override
    public void sendWelcomeNotification(Client client) {
        logger.info("📧 STANDARD: Sending welcome email to " + client.getName());
        logger.info("   Including: Basic account information");
        // In real implementation: send simple email
    }
    
    @Override
    public void sendAccountCreatedNotification(Account account) {
        logger.info("📧 STANDARD: Sending account creation notification");
        logger.info("   Account: " + account.getNumber());
        logger.info("   Type: " + account.getType());
        logger.info("   Channel: Email only");
        // In real implementation: send email notification
    }
    
    @Override
    public void sendTransactionNotification(Account account, String transactionType, double amount) {
        // Standard service: only notify for large transactions
        if (amount > 1000) {
            logger.info("📧 STANDARD: Sending transaction notification");
            logger.info("   Account: " + account.getNumber());
            logger.info("   Type: " + transactionType);
            logger.info("   Amount: $" + String.format("%.2f", amount));
            logger.info("   Channel: Email only");
            // In real implementation: send email for large transactions
        }
    }
    
    @Override
    public String getServiceLevel() {
        return "Standard";
    }
}

// Made with Bob