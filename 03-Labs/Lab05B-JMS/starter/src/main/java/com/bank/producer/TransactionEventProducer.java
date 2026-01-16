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
 * 
 * TODO: Complete the implementation of this JMS producer
 */
@ApplicationScoped
public class TransactionEventProducer {
    
    @Inject
    private Logger logger;
    
    // TODO: Inject JMSContext with @JMSConnectionFactory annotation
    // Hint: Use @JMSConnectionFactory("jms/connectionFactory")
    private JMSContext context;
    
    // TODO: Inject Queue resource for transaction queue
    // Hint: Use @Resource(lookup = "jms/transactionQueue")
    private Queue transactionQueue;
    
    // TODO: Inject Queue resource for email queue
    // Hint: Use @Resource(lookup = "jms/emailQueue")
    private Queue emailQueue;
    
    // TODO: Inject Topic resource for audit topic
    // Hint: Use @Resource(lookup = "jms/auditTopic")
    private Topic auditTopic;
    
    /**
     * Send transaction event to the transaction queue.
     * 
     * @param event The transaction event to send
     */
    public void sendTransactionEvent(TransactionEvent event) {
        try {
            logger.info("Sending transaction event to queue: " + event);
            
            // TODO: Create ObjectMessage from event
            // Hint: Use context.createObjectMessage(event)
            ObjectMessage message = null;
            
            // TODO: Set message properties for filtering
            // Hint: message.setStringProperty("transactionType", event.getType())
            // Hint: message.setStringProperty("status", event.getStatus())
            // Hint: message.setLongProperty("accountId", event.getAccountId())
            
            // TODO: Get priority based on transaction type
            int priority = getPriority(event.getType());
            
            // TODO: Send message with priority and persistent delivery
            // Hint: context.createProducer()
            //              .setPriority(priority)
            //              .setDeliveryMode(DeliveryMode.PERSISTENT)
            //              .send(transactionQueue, message);
            
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
            
            // TODO: Create ObjectMessage
            ObjectMessage message = null;
            
            // TODO: Set message properties
            // Hint: message.setStringProperty("notificationType", "EMAIL")
            // Hint: message.setStringProperty("customerEmail", event.getCustomerEmail())
            
            // TODO: Send message to email queue
            // Hint: context.createProducer()
            //              .setDeliveryMode(DeliveryMode.PERSISTENT)
            //              .send(emailQueue, message);
            
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
            
            // TODO: Create ObjectMessage
            ObjectMessage message = null;
            
            // TODO: Set message properties
            // Hint: message.setStringProperty("eventType", "AUDIT")
            // Hint: message.setStringProperty("transactionType", event.getType())
            // Hint: message.setLongProperty("timestamp", System.currentTimeMillis())
            
            // TODO: Publish to topic
            // Hint: context.createProducer()
            //              .setDeliveryMode(DeliveryMode.PERSISTENT)
            //              .send(auditTopic, message);
            
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