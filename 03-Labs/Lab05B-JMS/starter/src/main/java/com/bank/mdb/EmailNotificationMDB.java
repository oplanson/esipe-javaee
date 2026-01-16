// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.mdb;

import com.bank.event.TransactionEvent;
import com.bank.service.EmailService;
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
 * TODO: Complete the MDB configuration and implementation
 */
@MessageDriven(
    name = "EmailNotificationMDB",
    activationConfig = {
        // TODO: Configure destination type
        // Hint: propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"
        
        // TODO: Configure destination
        // Hint: propertyName = "destination", propertyValue = "jms/emailQueue"
        
        // TODO: Configure message selector
        // Hint: propertyName = "messageSelector", propertyValue = "notificationType = 'EMAIL'"
        
        // TODO: Configure acknowledge mode
        // Hint: propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"
    }
)
public class EmailNotificationMDB implements MessageListener {
    
    @Inject
    private Logger logger;
    
    @Inject
    private EmailService emailService;
    
    /**
     * Process incoming email notification messages.
     * 
     * @param message The JMS message containing transaction event
     */
    @Override
    public void onMessage(Message message) {
        try {
            logger.info("EmailNotificationMDB: Received message");
            
            // TODO: Verify message type is ObjectMessage
            // Hint: if (!(message instanceof ObjectMessage)) { ... return; }
            
            // TODO: Cast to ObjectMessage and get payload
            // Hint: ObjectMessage objectMessage = (ObjectMessage) message;
            // Hint: Object payload = objectMessage.getObject();
            
            // TODO: Verify payload is TransactionEvent
            // Hint: if (!(payload instanceof TransactionEvent)) { ... return; }
            
            // TODO: Cast payload to TransactionEvent
            TransactionEvent event = null;
            
            // TODO: Get message properties
            // Hint: String notificationType = message.getStringProperty("notificationType");
            // Hint: String customerEmail = message.getStringProperty("customerEmail");
            
            logger.info(String.format(
                "Processing email notification: Transaction=%d",
                event.getTransactionId()
            ));
            
            // TODO: Send email notification using emailService
            // Hint: emailService.sendTransactionNotification(event);
            
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