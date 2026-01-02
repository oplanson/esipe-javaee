/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.command;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.AccountNumber;
import com.bank.domain.valueobject.AccountType;
import com.bank.domain.valueobject.Money;

/**
 * Command to open a new account
 */
public record OpenAccountCommand(
    Long clientId,
    AccountNumber accountNumber,
    Money initialBalance,
    AccountType accountType,
    String currency
) {}

// Made with Bob
