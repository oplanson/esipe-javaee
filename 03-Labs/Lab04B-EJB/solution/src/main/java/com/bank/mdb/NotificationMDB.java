package com.bank.mdb;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.util.logging.Logger;

/**
 * Message-Driven Bean for processing notification messages.
 * 
 * Key Features:
 * - Listens to JMS queue asynchronously
 * - Container-managed concurrency (multiple instances)
 * - Automatic message acknowledgment
 * - No client-visible interface
 * - Processes messages in background
 * 
 * Configuration:
 * - destinationType: jakarta.jms.Queue
 * - destination: jms/notificationQueue
 * - acknowledgeMode: Auto-acknowledge
 */
@MessageDriven(
    name = "NotificationMDB",
    activationConfig = {
        @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue"
        ),
        @ActivationConfigProperty(
            propertyName = "destination",
            propertyValue = "jms/notificationQueue"
        ),
        @ActivationConfigProperty(
            propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge"
        ),
        @ActivationConfigProperty(
            propertyName = "maxSessions",
            propertyValue = "10"
        )
    }
)
public class NotificationMDB implements MessageListener {
    
    private static final Logger LOGGER = Logger.getLogger(NotificationMDB.class.getName());
    
    /**
     * Process incoming notification messages.
     * This method is called automatically by the container when a message arrives.
     * 
     * @param message The JMS message
     */
    @Override
    public void onMessage(Message message) {
        try {
            LOGGER.info("Received notification message");
            
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String content = textMessage.getText();
                
                // Parse message content
                NotificationMessage notification = parseMessage(content);
                
                // Process notification based on type
                processNotification(notification);
                
                LOGGER.info("Notification processed successfully: " + notification.getType());
                
            } else {
                LOGGER.warning("Received non-text message: " + message.getClass().getName());
            }
            
        } catch (JMSException e) {
            LOGGER.severe("Error processing JMS message: " + e.getMessage());
            // Message will be redelivered or moved to dead letter queue
            throw new RuntimeException("Failed to process notification", e);
            
        } catch (Exception e) {
            LOGGER.severe("Error processing notification: " + e.getMessage());
            throw new RuntimeException("Failed to process notification", e);
        }
    }
    
    /**
     * Parse message content into NotificationMessage object.
     * Expected format: TYPE|RECIPIENT|SUBJECT|BODY
     * 
     * @param content The message content
     * @return The parsed notification
     */
    private NotificationMessage parseMessage(String content) {
        String[] parts = content.split("\\|", 4);
        
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid message format: " + content);
        }
        
        NotificationType type = NotificationType.valueOf(parts[0]);
        String recipient = parts[1];
        String subject = parts[2];
        String body = parts[3];
        
        return new NotificationMessage(type, recipient, subject, body);
    }
    
    /**
     * Process notification based on type.
     * 
     * @param notification The notification to process
     */
    private void processNotification(NotificationMessage notification) {
        switch (notification.getType()) {
            case EMAIL:
                sendEmail(notification);
                break;
                
            case SMS:
                sendSMS(notification);
                break;
                
            case PUSH:
                sendPushNotification(notification);
                break;
                
            case ALERT:
                sendAlert(notification);
                break;
                
            default:
                LOGGER.warning("Unknown notification type: " + notification.getType());
        }
    }
    
    /**
     * Send email notification.
     * 
     * @param notification The notification
     */
    private void sendEmail(NotificationMessage notification) {
        LOGGER.info(String.format("Sending email to %s: %s", 
                                 notification.getRecipient(), 
                                 notification.getSubject()));
        
        // Simulate email sending
        try {
            Thread.sleep(100); // Simulate network delay
            LOGGER.info("Email sent successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // In production, use JavaMail API:
        // Session session = Session.getInstance(props);
        // MimeMessage message = new MimeMessage(session);
        // message.setFrom(new InternetAddress("noreply@bank.com"));
        // message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        // message.setSubject(subject);
        // message.setText(body);
        // Transport.send(message);
    }
    
    /**
     * Send SMS notification.
     * 
     * @param notification The notification
     */
    private void sendSMS(NotificationMessage notification) {
        LOGGER.info(String.format("Sending SMS to %s: %s", 
                                 notification.getRecipient(), 
                                 notification.getBody()));
        
        // Simulate SMS sending
        try {
            Thread.sleep(50); // Simulate network delay
            LOGGER.info("SMS sent successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // In production, use SMS gateway API:
        // TwilioClient.sendSMS(recipient, body);
    }
    
    /**
     * Send push notification.
     * 
     * @param notification The notification
     */
    private void sendPushNotification(NotificationMessage notification) {
        LOGGER.info(String.format("Sending push notification to %s: %s", 
                                 notification.getRecipient(), 
                                 notification.getSubject()));
        
        // Simulate push notification
        try {
            Thread.sleep(30); // Simulate network delay
            LOGGER.info("Push notification sent successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // In production, use Firebase Cloud Messaging or similar:
        // FirebaseMessaging.getInstance().send(message);
    }
    
    /**
     * Send alert notification.
     * 
     * @param notification The notification
     */
    private void sendAlert(NotificationMessage notification) {
        LOGGER.warning(String.format("ALERT for %s: %s - %s", 
                                    notification.getRecipient(), 
                                    notification.getSubject(),
                                    notification.getBody()));
        
        // In production, integrate with monitoring system:
        // MonitoringService.sendAlert(notification);
    }
    
    /**
     * Inner class representing a notification message.
     */
    private static class NotificationMessage {
        private final NotificationType type;
        private final String recipient;
        private final String subject;
        private final String body;
        
        public NotificationMessage(NotificationType type, String recipient, 
                                  String subject, String body) {
            this.type = type;
            this.recipient = recipient;
            this.subject = subject;
            this.body = body;
        }
        
        public NotificationType getType() {
            return type;
        }
        
        public String getRecipient() {
            return recipient;
        }
        
        public String getSubject() {
            return subject;
        }
        
        public String getBody() {
            return body;
        }
    }
    
    /**
     * Notification type enumeration.
     */
    private enum NotificationType {
        EMAIL,
        SMS,
        PUSH,
        ALERT
    }
}

// Made with Bob