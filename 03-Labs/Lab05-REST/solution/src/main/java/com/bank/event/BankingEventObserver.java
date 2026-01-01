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
 * Lab 05 - JAX-RS: Event Observers + Qualifiers (from Lab 04)
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
        logger.info("   Client ID: " + event.getClient().getId());
        logger.info("   Client Name: " + event.getClient().getName());
        logger.info("   Client Status: " + (event.getClient().isPremium() ? "PREMIUM" : "STANDARD"));
        logger.info("   Created By: " + event.getCreatedBy());
        
        // Select notification service based on client status
        NotificationService notificationService = selectNotificationService(event.getClient().isPremium());
        logger.info("   Notification Service: " + notificationService.getServiceLevel());
        
        // Send welcome notification using selected service
        notificationService.sendWelcomeNotification(event.getClient());
        
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
        logger.info("   Account ID: " + event.getAccount().getId());
        logger.info("   Account Number: " + event.getAccount().getNumber());
        logger.info("   Account Type: " + event.getAccount().getType());
        logger.info("   Client ID: " + event.getClientId());
        logger.info("   Created By: " + event.getCreatedBy());
        
        // Select notification service based on client status
        boolean isPremium = event.getAccount().getClient() != null && event.getAccount().getClient().isPremium();
        NotificationService notificationService = selectNotificationService(isPremium);
        logger.info("   Client Status: " + (isPremium ? "PREMIUM" : "STANDARD"));
        logger.info("   Notification Service: " + notificationService.getServiceLevel());
        
        // Send account creation notification using selected service
        notificationService.sendAccountCreatedNotification(event.getAccount());
        
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
        logger.info("   Account ID: " + event.getAccount().getId());
        logger.info("   Transaction Type: " + event.getType());
        logger.info("   Amount: $" + String.format("%.2f", event.getAmount()));
        logger.info("   New Balance: $" + String.format("%.2f", event.getAccount().getBalance()));
        
        if (event.getTargetAccountId() != null) {
            logger.info("   Target Account ID: " + event.getTargetAccountId());
        }
        
        logger.info("   Performed By: " + event.getPerformedBy());
        
        // Select notification service based on client status
        boolean isPremium = event.getAccount().getClient() != null && event.getAccount().getClient().isPremium();
        NotificationService notificationService = selectNotificationService(isPremium);
        logger.info("   Client Status: " + (isPremium ? "PREMIUM" : "STANDARD"));
        logger.info("   Notification Service: " + notificationService.getServiceLevel());
        
        // Send transaction notification using selected service
        notificationService.sendTransactionNotification(
            event.getAccount(),
            event.getType().toString(),
            event.getAmount()
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
        if (event.getAmount() > 10000) {
            logger.warning("⚠️  LARGE TRANSACTION DETECTED!");
            logger.warning("   Account ID: " + event.getAccount().getId());
            logger.warning("   Amount: $" + String.format("%.2f", event.getAmount()));
            logger.warning("   Type: " + event.getType());
            
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
