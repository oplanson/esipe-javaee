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
 */
@MessageDriven(
    name = "AuditLoggingMDB",
    activationConfig = {
        @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "jakarta.jms.Topic"
        ),
        @ActivationConfigProperty(
            propertyName = "destination",
            propertyValue = "jms/auditTopic"
        ),
        @ActivationConfigProperty(
            propertyName = "subscriptionDurability",
            propertyValue = "Durable"
        ),
        @ActivationConfigProperty(
            propertyName = "clientId",
            propertyValue = "AuditClient"
        ),
        @ActivationConfigProperty(
            propertyName = "subscriptionName",
            propertyValue = "AuditSubscription"
        ),
        @ActivationConfigProperty(
            propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge"
        )
    }
)
public class AuditLoggingMDB implements MessageListener {
    
    @Inject
    private Logger logger;
    
    @PersistenceContext(unitName = "bankingPU")
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
            
            // Verify message type
            if (!(message instanceof ObjectMessage)) {
                logger.warning("Received non-ObjectMessage, ignoring");
                return;
            }
            
            ObjectMessage objectMessage = (ObjectMessage) message;
            Object payload = objectMessage.getObject();
            
            // Verify payload type
            if (!(payload instanceof TransactionEvent)) {
                logger.warning("Received non-TransactionEvent object, ignoring");
                return;
            }
            
            TransactionEvent event = (TransactionEvent) payload;
            
            // Log message properties
            String eventType = message.getStringProperty("eventType");
            String transactionType = message.getStringProperty("transactionType");
            
            logger.info(String.format(
                "Processing audit log: EventType=%s, TransactionType=%s, Transaction=%d",
                eventType, transactionType, event.getTransactionId()
            ));
            
            // Create audit log entity
            AuditLog auditLog = new AuditLog();
            auditLog.setTransactionId(event.getTransactionId());
            auditLog.setAccountNumber(event.getAccountNumber());
            auditLog.setAmount(event.getAmount());
            auditLog.setTransactionType(event.getType());
            auditLog.setTimestamp(event.getTimestamp());
            auditLog.setStatus(event.getStatus());
            auditLog.setDescription(event.getDescription());
            
            // Persist to database
            em.persist(auditLog);
            
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