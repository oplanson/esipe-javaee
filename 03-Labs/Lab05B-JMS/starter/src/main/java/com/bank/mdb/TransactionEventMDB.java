// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.mdb;

import com.bank.event.TransactionEvent;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message-Driven Bean for processing transaction events.
 * Listens to the transaction queue and processes transaction events.
 */
@MessageDriven(
    name = "TransactionEventMDB",
    activationConfig = {
        @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue"
        ),
        @ActivationConfigProperty(
            propertyName = "destination",
            propertyValue = "jms/transactionQueue"
        ),
        @ActivationConfigProperty(
            propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge"
        ),
        @ActivationConfigProperty(
            propertyName = "maxConcurrency",
            propertyValue = "10"
        )
    }
)
public class TransactionEventMDB implements MessageListener {
    
    @Inject
    private Logger logger;
    
    /**
     * Process incoming transaction event messages.
     * 
     * @param message The JMS message containing transaction event
     */
    @Override
    public void onMessage(Message message) {
        try {
            logger.info("TransactionEventMDB: Received message");
            
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
            String transactionType = message.getStringProperty("transactionType");
            String status = message.getStringProperty("status");
            long accountId = message.getLongProperty("accountId");
            
            logger.info(String.format(
                "Processing transaction event: Type=%s, Status=%s, AccountId=%d, Transaction=%d",
                transactionType, status, accountId, event.getTransactionId()
            ));
            
            // Process based on transaction type
            processTransactionEvent(event);
            
            logger.info("Transaction event processed successfully: " + event.getTransactionId());
            
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "JMS error processing transaction event: " 
                      + e.getMessage(), e);
            // Message will be redelivered
            throw new RuntimeException("Failed to process transaction event", e);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing transaction event: " 
                      + e.getMessage(), e);
            // Message will be redelivered
            throw new RuntimeException("Failed to process transaction event", e);
        }
    }
    
    /**
     * Process transaction event based on type.
     * 
     * @param event The transaction event
     */
    private void processTransactionEvent(TransactionEvent event) {
        String type = event.getType();
        
        logger.info("Processing " + type + " transaction: " + event.getTransactionId());
        
        // Simulate processing time
        try {
            Thread.sleep(100); // 100ms processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Additional processing based on transaction type
        switch (type.toUpperCase()) {
            case "DEPOSIT":
                processDeposit(event);
                break;
            case "WITHDRAWAL":
                processWithdrawal(event);
                break;
            case "TRANSFER":
                processTransfer(event);
                break;
            default:
                logger.warning("Unknown transaction type: " + type);
        }
    }
    
    /**
     * Process deposit transaction.
     * 
     * @param event The transaction event
     */
    private void processDeposit(TransactionEvent event) {
        logger.info("Processing deposit: Amount=" + event.getAmount() 
                   + ", Account=" + event.getAccountNumber());
        // Additional deposit processing logic here
    }
    
    /**
     * Process withdrawal transaction.
     * 
     * @param event The transaction event
     */
    private void processWithdrawal(TransactionEvent event) {
        logger.info("Processing withdrawal: Amount=" + event.getAmount() 
                   + ", Account=" + event.getAccountNumber());
        // Additional withdrawal processing logic here
    }
    
    /**
     * Process transfer transaction.
     * 
     * @param event The transaction event
     */
    private void processTransfer(TransactionEvent event) {
        logger.info("Processing transfer: Amount=" + event.getAmount() 
                   + ", Account=" + event.getAccountNumber());
        // Additional transfer processing logic here
    }
}

// Made with Bob