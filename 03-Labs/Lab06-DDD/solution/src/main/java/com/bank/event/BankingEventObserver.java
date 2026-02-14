package com.bank.event;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.config.Premium;
import com.bank.config.Standard;
import com.bank.service.NotificationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.logging.Logger;

/**
 * CDI Event Observer that listens to banking events.
 * Demonstrates synchronous event observation and CDI Qualifiers.
 *
 * Lab 06 - DDD: Event Observers + Qualifiers (from Lab 04)
 *
 * This observer logs all banking events and selects the appropriate
 * notification service (Premium or Standard) based on client status.
 */
@ApplicationScoped
public class BankingEventObserver {
    
    @Inject
    private Logger logger;
    
    /**
     * Premium notification service injected with @Premium qualifier.
     * Used for clients with premium status.
     */
    @Inject
    @Premium
    private NotificationService premiumNotificationService;
    
    /**
     * Standard notification service injected with @Standard qualifier.
     * Used for regular clients.
     */
    @Inject
    @Standard
    private NotificationService standardNotificationService;
    
    /**
     * Observes client creation events.
     * Logs when a new client is created.
     * 
     * @param event The client created event
     */
    public void onClientCreated(@Observes ClientCreatedEvent event) {
        logger.info("📢 EVENT OBSERVED: Client created - " + event);
        logger.info("   Client ID: " + event.client().getId());
        logger.info("   Client Name: " + event.client().getName());
        logger.info("   Client Status: " + (event.client().isPremium() ? "PREMIUM" : "STANDARD"));
        logger.info("   Created By: " + event.createdBy());
        
        // Select notification service based on client status
        NotificationService notificationService = selectNotificationService(event.client().isPremium());
        logger.info("   Notification Service: " + notificationService.getServiceLevel());
        
        // Send welcome notification using selected service
        notificationService.sendWelcomeNotification(event.client());
        
        // Here you could also:
        // - Create audit log entry
        // - Trigger business rules
        // - Update statistics
    }
    
    /**
     * Observes account creation events.
     * Logs when a new account is created.
     * 
     * @param event The account created event
     */
    public void onAccountCreated(@Observes AccountCreatedEvent event) {
        logger.info("📢 EVENT OBSERVED: Account created - " + event);
        logger.info("   Account ID: " + event.account().getId());
        logger.info("   Account Number: " + event.account().getNumber());
        logger.info("   Account Type: " + event.account().getType());
        logger.info("   Client ID: " + event.clientId());
        logger.info("   Created By: " + event.createdBy());
        
        // Select notification service based on client status
        boolean isPremium = event.account().getClient() != null && event.account().getClient().isPremium();
        NotificationService notificationService = selectNotificationService(isPremium);
        logger.info("   Client Status: " + (isPremium ? "PREMIUM" : "STANDARD"));
        logger.info("   Notification Service: " + notificationService.getServiceLevel());
        
        // Send account creation notification using selected service
        notificationService.sendAccountCreatedNotification(event.account());
        
        // Here you could also:
        // - Generate account documents
        // - Set up default services
        // - Notify compliance team
    }
    
    /**
     * Observes transaction events.
     * Logs all financial transactions.
     * 
     * @param event The transaction event
     */
    public void onTransaction(@Observes TransactionEvent event) {
        logger.info("📢 EVENT OBSERVED: Transaction - " + event);
        logger.info("   Account ID: " + event.account().getId());
        logger.info("   Transaction Type: " + event.type());
        logger.info("   Amount: $" + String.format("%.2f", event.amount()));
        logger.info("   New Balance: $" + String.format("%.2f", event.account().getBalanceAsDouble()));
        
        if (event.targetAccountId() != null) {
            logger.info("   Target Account ID: " + event.targetAccountId());
        }
        
        logger.info("   Performed By: " + event.performedBy());
        
        // Select notification service based on client status
        boolean isPremium = event.account().getClient() != null && event.account().getClient().isPremium();
        NotificationService notificationService = selectNotificationService(isPremium);
        logger.info("   Client Status: " + (isPremium ? "PREMIUM" : "STANDARD"));
        logger.info("   Notification Service: " + notificationService.getServiceLevel());
        
        // Send transaction notification using selected service
        notificationService.sendTransactionNotification(
            event.account(),
            event.type().toString(),
            event.amount()
        );
        
        // Here you could also:
        // - Check for fraud patterns
        // - Update account statistics
        // - Trigger compliance checks
        // - Update real-time dashboards
    }
    
    /**
     * Observes large transactions (amount > 10000).
     * Demonstrates conditional event observation.
     * 
     * @param event The transaction event
     */
    public void onLargeTransaction(@Observes TransactionEvent event) {
        if (event.amount() > 10000) {
            logger.warning("⚠️  LARGE TRANSACTION DETECTED!");
            logger.warning("   Account ID: " + event.account().getId());
            logger.warning("   Amount: $" + String.format("%.2f", event.amount()));
            logger.warning("   Type: " + event.type());
            
            // Here you could:
            // - Alert compliance team
            // - Require additional verification
            // - Log for regulatory reporting
            // - Trigger anti-money laundering checks
        }
    }
    
    /**
     * Selects the appropriate notification service based on client status.
     * Demonstrates dynamic service selection using CDI Qualifiers.
     *
     * @param isPremium Whether the client has premium status
     * @return The appropriate notification service (Premium or Standard)
     */
    private NotificationService selectNotificationService(boolean isPremium) {
        return isPremium ? premiumNotificationService : standardNotificationService;
    }
}

// Made with Bob
