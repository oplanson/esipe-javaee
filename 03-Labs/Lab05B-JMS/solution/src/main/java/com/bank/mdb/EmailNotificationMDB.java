// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.mdb;

import com.bank.event.TransactionEvent;
import com.bank.service.EmailService;
import com.bank.util.JsonMessageUtil;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message-Driven Bean for processing email notifications.
 * Listens to the email queue and sends email notifications for transactions.
 *
 * Security: Uses JSON deserialization from TextMessage instead of ObjectMessage
 * to prevent Java deserialization vulnerabilities.
 */
@MessageDriven(
    name = "EmailNotificationMDB",
    activationConfig = {
        @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue"
        ),
        @ActivationConfigProperty(
            propertyName = "destination",
            propertyValue = "jms/emailQueue"
        ),
        @ActivationConfigProperty(
            propertyName = "messageSelector",
            propertyValue = "notificationType = 'EMAIL'"
        ),
        @ActivationConfigProperty(
            propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge"
        )
    }
)
public class EmailNotificationMDB implements MessageListener {
    
    @Inject
    private Logger logger;
    
    @Inject
    private EmailService emailService;
    
    /**
     * Process incoming email notification messages.
     * Securely deserializes JSON from TextMessage.
     *
     * @param message The JMS message containing transaction event
     */
    @Override
    public void onMessage(Message message) {
        try {
            logger.info("EmailNotificationMDB: Received message");
            
            // Verify message type - expect TextMessage with JSON
            if (!(message instanceof TextMessage)) {
                logger.warning("Received non-TextMessage, ignoring. Type: " +
                             message.getClass().getName());
                return;
            }
            
            TextMessage textMessage = (TextMessage) message;
            String jsonPayload = textMessage.getText();
            
            // Verify JSON format
            String messageFormat = message.getStringProperty("messageFormat");
            if (!"JSON".equals(messageFormat)) {
                logger.warning("Message format is not JSON, ignoring");
                return;
            }
            
            // Securely deserialize JSON to TransactionEvent
            TransactionEvent event = JsonMessageUtil.fromJson(jsonPayload, TransactionEvent.class);
            
            if (event == null) {
                logger.warning("Failed to deserialize transaction event from JSON");
                return;
            }
            
            // Log message properties
            String notificationType = message.getStringProperty("notificationType");
            String customerEmail = message.getStringProperty("customerEmail");
            
            logger.info(String.format(
                "Processing email notification: Type=%s, Email=%s, Transaction=%d",
                notificationType, customerEmail, event.getTransactionId()
            ));
            
            // Send email notification
            emailService.sendTransactionNotification(event);
            
            logger.info("Email notification processed successfully for transaction: "
                       + event.getTransactionId());
            
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "JMS error processing email notification: "
                      + e.getMessage(), e);
            // Message will be redelivered
            throw new RuntimeException("Failed to process email notification", e);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing email notification: "
                      + e.getMessage(), e);
            // Message will be redelivered
            throw new RuntimeException("Failed to process email notification", e);
        }
    }
}

// Made with Bob