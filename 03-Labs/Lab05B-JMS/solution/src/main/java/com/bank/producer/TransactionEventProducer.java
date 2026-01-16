// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.producer;

import com.bank.event.TransactionEvent;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JMS Producer for sending transaction events.
 * Uses CDI and JMS 3.1 simplified API.
 */
@ApplicationScoped
public class TransactionEventProducer {
    
    @Inject
    private Logger logger;
    
    @Inject
    @JMSConnectionFactory("jms/connectionFactory")
    private JMSContext context;
    
    @Resource(lookup = "jms/transactionQueue")
    private Queue transactionQueue;
    
    @Resource(lookup = "jms/emailQueue")
    private Queue emailQueue;
    
    @Resource(lookup = "jms/auditTopic")
    private Topic auditTopic;
    
    /**
     * Send transaction event to the transaction queue.
     * 
     * @param event The transaction event to send
     */
    public void sendTransactionEvent(TransactionEvent event) {
        try {
            logger.info("Sending transaction event to queue: " + event);
            
            // Create message
            ObjectMessage message = context.createObjectMessage(event);
            
            // Set message properties for filtering
            message.setStringProperty("transactionType", event.getType());
            message.setStringProperty("status", event.getStatus());
            message.setLongProperty("accountId", event.getAccountId());
            
            // Set priority based on transaction type
            int priority = getPriority(event.getType());
            
            // Send message with priority
            context.createProducer()
                   .setPriority(priority)
                   .setDeliveryMode(DeliveryMode.PERSISTENT)
                   .send(transactionQueue, message);
            
            logger.info("Transaction event sent successfully: ID=" + event.getTransactionId());
            
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Error sending transaction event: " + e.getMessage(), e);
            throw new RuntimeException("Failed to send transaction event", e);
        }
    }
    
    /**
     * Send email notification event.
     * 
     * @param event The transaction event for email notification
     */
    public void sendEmailNotification(TransactionEvent event) {
        try {
            logger.info("Sending email notification for transaction: " + event.getTransactionId());
            
            // Create message
            ObjectMessage message = context.createObjectMessage(event);
            
            // Set message properties
            message.setStringProperty("notificationType", "EMAIL");
            message.setStringProperty("customerEmail", event.getCustomerEmail());
            
            // Send message
            context.createProducer()
                   .setDeliveryMode(DeliveryMode.PERSISTENT)
                   .send(emailQueue, message);
            
            logger.info("Email notification sent successfully");
            
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Error sending email notification: " + e.getMessage(), e);
            // Don't throw exception - email is not critical
        }
    }
    
    /**
     * Publish audit event to topic (multiple subscribers).
     * 
     * @param event The transaction event for audit
     */
    public void publishAuditEvent(TransactionEvent event) {
        try {
            logger.info("Publishing audit event for transaction: " + event.getTransactionId());
            
            // Create message
            ObjectMessage message = context.createObjectMessage(event);
            
            // Set message properties
            message.setStringProperty("eventType", "AUDIT");
            message.setStringProperty("transactionType", event.getType());
            message.setLongProperty("timestamp", System.currentTimeMillis());
            
            // Publish to topic
            context.createProducer()
                   .setDeliveryMode(DeliveryMode.PERSISTENT)
                   .send(auditTopic, message);
            
            logger.info("Audit event published successfully");
            
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Error publishing audit event: " + e.getMessage(), e);
            // Don't throw exception - audit is logged separately
        }
    }
    
    /**
     * Send all events (transaction, email, audit) in one call.
     * 
     * @param event The transaction event
     */
    public void sendAllEvents(TransactionEvent event) {
        sendTransactionEvent(event);
        
        // Send email notification if customer email is provided
        if (event.getCustomerEmail() != null && !event.getCustomerEmail().isEmpty()) {
            sendEmailNotification(event);
        }
        
        // Always publish audit event
        publishAuditEvent(event);
    }
    
    /**
     * Get message priority based on transaction type.
     * 
     * @param type Transaction type
     * @return Priority (0-9, where 9 is highest)
     */
    private int getPriority(String type) {
        if (type == null) {
            return 4; // Default priority
        }
        
        return switch (type.toUpperCase()) {
            case "TRANSFER" -> 7; // High priority
            case "WITHDRAWAL" -> 6; // Medium-high priority
            case "DEPOSIT" -> 5; // Medium priority
            default -> 4; // Default priority
        };
    }
}

// Made with Bob