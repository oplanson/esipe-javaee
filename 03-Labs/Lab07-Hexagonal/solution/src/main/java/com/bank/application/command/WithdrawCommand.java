/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.command;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.Money;

/**
 * Command to withdraw money from an account
 */
public record WithdrawCommand(
    Long accountId,
    Money amount
) {}

// Made with Bob
