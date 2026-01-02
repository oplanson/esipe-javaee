/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.command;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.Money;

/**
 * Command to transfer money between accounts
 */
public record TransferCommand(
    Long fromAccountId,
    Long toAccountId,
    Money amount
) {}

// Made with Bob
