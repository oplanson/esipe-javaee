/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.port.in;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.application.command.DepositCommand;
import com.bank.application.command.TransferCommand;
import com.bank.application.command.WithdrawCommand;

/**
 * Primary port (driving) - Money operations use case interface.
 * Defines what the application can do regarding money operations.
 */
public interface MoneyOperationsUseCase {
    
    /**
     * Deposit money into an account
     */
    void deposit(DepositCommand command);
    
    /**
     * Withdraw money from an account
     */
    void withdraw(WithdrawCommand command);
    
    /**
     * Transfer money between accounts
     */
    void transfer(TransferCommand command);
}

// Made with Bob
