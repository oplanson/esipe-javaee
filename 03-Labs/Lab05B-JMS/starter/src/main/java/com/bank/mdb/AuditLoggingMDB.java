// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.mdb;

import com.bank.event.TransactionEvent;
import com.bank.model.AuditLog;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message-Driven Bean for audit logging.
 * Subscribes to the audit topic and persists audit logs to database.
 * Uses durable subscription to ensure no audit events are lost.
 * 
 * TODO: Complete the MDB configuration for durable topic subscription
 */
@MessageDriven(
    name = "AuditLoggingMDB",
    activationConfig = {
        // TODO: Configure destination type for Topic
        // Hint: propertyName = "destinationType", propertyValue = "jakarta.jms.Topic"
        
        // TODO: Configure destination
        // Hint: propertyName = "destination", propertyValue = "jms/auditTopic"
        
        // TODO: Configure durable subscription
        // Hint: propertyName = "subscriptionDurability", propertyValue = "Durable"
        
        // TODO: Configure client ID
        // Hint: propertyName = "clientId", propertyValue = "AuditClient"
        
        // TODO: Configure subscription name
        // Hint: propertyName = "subscriptionName", propertyValue = "AuditSubscription"
        
        // TODO: Configure acknowledge mode
        // Hint: propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"
    }
)
public class AuditLoggingMDB implements MessageListener {
    
    @Inject
    private Logger logger;
    
    // TODO: Inject EntityManager with @PersistenceContext
    // Hint: @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;
    
    /**
     * Process incoming audit messages and persist to database.
     * 
     * @param message The JMS message containing transaction event
     */
    @Override
    public void onMessage(Message message) {
        try {
            logger.info("AuditLoggingMDB: Received audit message");
            
            // TODO: Verify message type
            // Hint: if (!(message instanceof ObjectMessage)) { ... return; }
            
            // TODO: Get payload from ObjectMessage
            ObjectMessage objectMessage = (ObjectMessage) message;
            Object payload = null; // TODO: Get object from message
            
            // TODO: Verify payload type
            // Hint: if (!(payload instanceof TransactionEvent)) { ... return; }
            
            TransactionEvent event = (TransactionEvent) payload;
            
            // TODO: Get message properties
            // Hint: String eventType = message.getStringProperty("eventType");
            // Hint: String transactionType = message.getStringProperty("transactionType");
            // Hint: long timestamp = message.getLongProperty("timestamp");
            
            logger.info(String.format(
                "Processing audit log: Transaction=%d",
                event.getTransactionId()
            ));
            
            // TODO: Create audit log entity
            AuditLog auditLog = new AuditLog();
            // TODO: Set properties from event
            // Hint: auditLog.setTransactionId(event.getTransactionId());
            // Hint: auditLog.setAccountNumber(event.getAccountNumber());
            // Hint: auditLog.setAmount(event.getAmount());
            // Hint: auditLog.setTransactionType(event.getType());
            // Hint: auditLog.setTimestamp(event.getTimestamp());
            // Hint: auditLog.setStatus(event.getStatus());
            // Hint: auditLog.setDescription(event.getDescription());
            
            // TODO: Persist to database
            // Hint: em.persist(auditLog);
            
            logger.info("Audit log persisted successfully: ID=" + auditLog.getId() 
                       + ", Transaction=" + event.getTransactionId());
            
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "JMS error processing audit message: " 
                      + e.getMessage(), e);
            // Message will be redelivered
            throw new RuntimeException("Failed to process audit message", e);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing audit message: " 
                      + e.getMessage(), e);
            // Message will be redelivered
            throw new RuntimeException("Failed to process audit message", e);
        }
    }
}

// Made with Bob