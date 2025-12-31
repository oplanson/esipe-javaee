package com.bank.service;

import com.bank.model.Client;
import com.bank.model.Account;

/**
 * Interface for notification services.
 * Demonstrates CDI Qualifiers for type-safe dependency injection.
 * 
 * Lab 04 - Advanced CDI: Qualifiers
 * 
 * Different implementations can be injected using @Premium or @Standard qualifiers.
 */
public interface NotificationService {
    
    /**
     * Send a welcome notification to a new client.
     * 
     * @param client The client to notify
     */
    void sendWelcomeNotification(Client client);
    
    /**
     * Send an account creation notification.
     * 
     * @param account The newly created account
     */
    void sendAccountCreatedNotification(Account account);
    
    /**
     * Send a transaction notification.
     * 
     * @param account The account involved in the transaction
     * @param transactionType The type of transaction
     * @param amount The transaction amount
     */
    void sendTransactionNotification(Account account, String transactionType, double amount);
    
    /**
     * Get the service level name.
     * 
     * @return The service level (e.g., "Premium", "Standard")
     */
    String getServiceLevel();
}

// Made with Bob