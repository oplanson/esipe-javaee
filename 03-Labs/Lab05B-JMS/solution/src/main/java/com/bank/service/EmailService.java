// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.service;

import com.bank.event.TransactionEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.logging.Logger;

/**
 * Service for sending email notifications.
 * In a real application, this would integrate with an email service provider.
 */
@ApplicationScoped
public class EmailService {
    
    @Inject
    private Logger logger;
    
    /**
     * Send transaction notification email to customer.
     * 
     * @param event The transaction event
     */
    public void sendTransactionNotification(TransactionEvent event) {
        logger.info("=== Sending Email Notification ===");
        logger.info("To: " + event.getCustomerEmail());
        logger.info("Subject: Transaction Notification - " + event.getType());
        logger.info("Transaction ID: " + event.getTransactionId());
        logger.info("Account: " + event.getAccountNumber());
        logger.info("Amount: " + event.getAmount());
        logger.info("Status: " + event.getStatus());
        logger.info("Timestamp: " + event.getTimestamp());
        
        // Simulate email sending delay
        simulateEmailSending();
        
        logger.info("Email sent successfully to: " + event.getCustomerEmail());
    }
    
    /**
     * Send deposit confirmation email.
     * 
     * @param event The transaction event
     */
    public void sendDepositConfirmation(TransactionEvent event) {
        logger.info("Sending deposit confirmation email");
        logger.info("Dear Customer,");
        logger.info("Your deposit of " + event.getAmount() + " has been processed successfully.");
        logger.info("Account: " + event.getAccountNumber());
        logger.info("Transaction ID: " + event.getTransactionId());
        
        simulateEmailSending();
        
        logger.info("Deposit confirmation email sent");
    }
    
    /**
     * Send withdrawal confirmation email.
     * 
     * @param event The transaction event
     */
    public void sendWithdrawalConfirmation(TransactionEvent event) {
        logger.info("Sending withdrawal confirmation email");
        logger.info("Dear Customer,");
        logger.info("Your withdrawal of " + event.getAmount() + " has been processed successfully.");
        logger.info("Account: " + event.getAccountNumber());
        logger.info("Transaction ID: " + event.getTransactionId());
        
        simulateEmailSending();
        
        logger.info("Withdrawal confirmation email sent");
    }
    
    /**
     * Send transfer confirmation email.
     * 
     * @param event The transaction event
     */
    public void sendTransferConfirmation(TransactionEvent event) {
        logger.info("Sending transfer confirmation email");
        logger.info("Dear Customer,");
        logger.info("Your transfer of " + event.getAmount() + " has been processed successfully.");
        logger.info("Account: " + event.getAccountNumber());
        logger.info("Transaction ID: " + event.getTransactionId());
        
        simulateEmailSending();
        
        logger.info("Transfer confirmation email sent");
    }
    
    /**
     * Send failed transaction alert email.
     * 
     * @param event The transaction event
     */
    public void sendFailedTransactionAlert(TransactionEvent event) {
        logger.warning("Sending failed transaction alert email");
        logger.warning("Dear Customer,");
        logger.warning("Your transaction could not be processed.");
        logger.warning("Transaction ID: " + event.getTransactionId());
        logger.warning("Reason: " + event.getDescription());
        
        simulateEmailSending();
        
        logger.warning("Failed transaction alert email sent");
    }
    
    /**
     * Simulate email sending delay.
     * In a real application, this would be actual email service API call.
     */
    private void simulateEmailSending() {
        try {
            // Simulate network delay and email service processing
            Thread.sleep(500); // 500ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Email sending interrupted: " + e.getMessage());
        }
    }
}

// Made with Bob