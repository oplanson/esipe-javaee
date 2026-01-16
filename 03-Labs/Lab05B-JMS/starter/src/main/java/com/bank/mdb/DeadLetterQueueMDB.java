// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.mdb;

import com.bank.model.FailedMessage;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message-Driven Bean for handling messages in the Dead Letter Queue.
 * Logs failed messages and stores them in the database for later analysis.
 */
@MessageDriven(
    name = "DeadLetterQueueMDB",
    activationConfig = {
        @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue"
        ),
        @ActivationConfigProperty(
            propertyName = "destination",
            propertyValue = "jms/deadLetterQueue"
        ),
        @ActivationConfigProperty(
            propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge"
        )
    }
)
public class DeadLetterQueueMDB implements MessageListener {
    
    @Inject
    private Logger logger;
    
    @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;
    
    /**
     * Process messages that have failed after maximum redelivery attempts.
     * 
     * @param message The failed JMS message
     */
    @Override
    public void onMessage(Message message) {
        try {
            String messageId = message.getJMSMessageID();
            int deliveryCount = getDeliveryCount(message);
            
            logger.severe(String.format(
                "Dead Letter Queue: Message failed after %d delivery attempts. MessageID=%s",
                deliveryCount, messageId
            ));
            
            // Extract message content
            String content = extractMessageContent(message);
            
            // Get error information if available
            String errorMessage = getErrorMessage(message);
            
            // Create failed message entity
            FailedMessage failedMessage = new FailedMessage();
            failedMessage.setMessageId(messageId);
            failedMessage.setDeliveryCount(deliveryCount);
            failedMessage.setContent(content);
            failedMessage.setFailureTime(LocalDateTime.now());
            failedMessage.setErrorMessage(errorMessage);
            
            // Persist to database
            em.persist(failedMessage);
            
            logger.severe("Failed message stored in database: ID=" + failedMessage.getId());
            
            // TODO: Send alert notification to administrators
            sendAlertNotification(failedMessage);
            
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Error processing DLQ message: " + e.getMessage(), e);
            // Don't throw exception - we don't want DLQ messages to fail
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in DLQ handler: " + e.getMessage(), e);
            // Don't throw exception - we don't want DLQ messages to fail
        }
    }
    
    /**
     * Get delivery count from message.
     * 
     * @param message The JMS message
     * @return Delivery count
     */
    private int getDeliveryCount(Message message) {
        try {
            if (message.propertyExists("JMSXDeliveryCount")) {
                return message.getIntProperty("JMSXDeliveryCount");
            }
        } catch (JMSException e) {
            logger.warning("Could not get delivery count: " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Extract message content as string.
     * 
     * @param message The JMS message
     * @return Message content
     */
    private String extractMessageContent(Message message) {
        try {
            if (message instanceof TextMessage) {
                return ((TextMessage) message).getText();
                
            } else if (message instanceof ObjectMessage) {
                Object obj = ((ObjectMessage) message).getObject();
                return obj != null ? obj.toString() : "null";
                
            } else if (message instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) message;
                byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(bytes);
                return Base64.getEncoder().encodeToString(bytes);
                
            } else {
                return "Unknown message type: " + message.getClass().getName();
            }
            
        } catch (Exception e) {
            logger.warning("Could not extract message content: " + e.getMessage());
            return "Error extracting content: " + e.getMessage();
        }
    }
    
    /**
     * Get error message from message properties.
     * 
     * @param message The JMS message
     * @return Error message or null
     */
    private String getErrorMessage(Message message) {
        try {
            if (message.propertyExists("JMS_IBM_ExceptionMessage")) {
                return message.getStringProperty("JMS_IBM_ExceptionMessage");
            }
            if (message.propertyExists("JMS_IBM_ExceptionReason")) {
                return message.getStringProperty("JMS_IBM_ExceptionReason");
            }
        } catch (JMSException e) {
            logger.warning("Could not get error message: " + e.getMessage());
        }
        return "Unknown error";
    }
    
    /**
     * Send alert notification to administrators.
     * 
     * @param failedMessage The failed message entity
     */
    private void sendAlertNotification(FailedMessage failedMessage) {
        // TODO: Implement alert notification (email, SMS, monitoring system)
        logger.severe(String.format(
            "ALERT: Message failed permanently. ID=%d, MessageID=%s, DeliveryCount=%d",
            failedMessage.getId(),
            failedMessage.getMessageId(),
            failedMessage.getDeliveryCount()
        ));
    }
}

// Made with Bob