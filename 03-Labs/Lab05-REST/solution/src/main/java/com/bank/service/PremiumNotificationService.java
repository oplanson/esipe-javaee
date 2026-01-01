package com.bank.service;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.config.Premium;
import com.bank.model.Account;
import com.bank.model.Client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.logging.Logger;

/**
 * Premium implementation of NotificationService.
 * Provides enhanced notifications with additional features.
 *
 * Lab 05 - JAX-RS: Qualifiers (from Lab 04)
 *
 * This implementation is selected when injecting with @Premium qualifier:
 * <pre>
 * {@code
 * @Inject
 * @Premium
 * private NotificationService notificationService;
 * }
 * </pre>
 */
@ApplicationScoped
@Premium
public class PremiumNotificationService implements NotificationService {
    
    @Inject
    private Logger logger;
    
    @Override
    public void sendWelcomeNotification(Client client) {
        logger.info("🌟 PREMIUM: Sending personalized welcome email to " + client.getName());
        logger.info("   Including: Welcome gift, Premium benefits guide, Dedicated support contact");
        // In real implementation: send email via SMTP, SMS, push notification
    }
    
    @Override
    public void sendAccountCreatedNotification(Account account) {
        logger.info("🌟 PREMIUM: Sending account creation notification");
        logger.info("   Account: " + account.getNumber());
        logger.info("   Type: " + account.getType());
        logger.info("   Including: Premium card delivery, Mobile app setup guide, Concierge service info");
        // In real implementation: send multi-channel notification
    }
    
    @Override
    public void sendTransactionNotification(Account account, String transactionType, double amount) {
        logger.info("🌟 PREMIUM: Sending real-time transaction alert");
        logger.info("   Account: " + account.getNumber());
        logger.info("   Type: " + transactionType);
        logger.info("   Amount: $" + String.format("%.2f", amount));
        logger.info("   Channels: Email + SMS + Push + In-app");
        // In real implementation: send instant multi-channel alert
    }
    
    @Override
    public String getServiceLevel() {
        return "Premium";
    }
}

// Made with Bob
