package com.bank.mdb;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.util.logging.Logger;

/**
 * TODO: Part 4 - Message-Driven Bean
 * 
 * Instructions:
 * 1. Add @MessageDriven annotation with activation config
 * 2. Set destinationType to "jakarta.jms.Queue"
 * 3. Set destination to "jms/notificationQueue"
 * 4. Implement MessageListener interface
 * 5. Process messages in onMessage() method
 */
// TODO: Add @MessageDriven annotation with activation config

public class NotificationMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(NotificationMDB.class.getName());

    /**
     * TODO: Process incoming JMS messages
     * - Cast message to TextMessage
     * - Extract text content
     * - Log the notification
     * - Handle exceptions appropriately
     */
    @Override
    public void onMessage(Message message) {
        // TODO: Implement message processing
        try {
            // TODO: Cast to TextMessage and get text
            // TODO: Log the notification
            LOGGER.info("TODO: Process notification message");
        } catch (Exception e) {
            LOGGER.severe("Error processing message: " + e.getMessage());
        }
    }
}

// Made with Bob