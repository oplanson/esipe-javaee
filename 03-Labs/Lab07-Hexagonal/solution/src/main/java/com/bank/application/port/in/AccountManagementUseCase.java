/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.port.in;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.command.OpenAccountCommand;
import com.bank.application.dto.AccountDTO;
import com.bank.domain.valueobject.AccountNumber;

import java.util.List;

/**
 * Primary port (driving) - Account management use case interface.
 * Defines what the application can do regarding account management.
 * Implemented by use case services in the application layer.
 */
public interface AccountManagementUseCase {
    
    /**
     * Open a new account
     */
    AccountDTO openAccount(OpenAccountCommand command);
    
    /**
     * Close an account
     */
    void closeAccount(Long accountId);
    
    /**
     * Reactivate a closed account
     */
    void reactivateAccount(Long accountId);
    
    /**
     * Get account details by ID
     */
    AccountDTO getAccount(Long accountId);
    
    /**
     * Get account details by account number
     */
    AccountDTO getAccountByNumber(AccountNumber accountNumber);
    
    /**
     * Get all accounts for a client
     */
    List<AccountDTO> getClientAccounts(Long clientId);
    
    /**
     * Get all accounts
     */
    List<AccountDTO> getAllAccounts();
}

// Made with Bob
