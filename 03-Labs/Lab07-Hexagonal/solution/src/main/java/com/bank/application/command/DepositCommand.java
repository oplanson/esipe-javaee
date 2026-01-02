/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.command;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.Money;

/**
 * Command to deposit money into an account
 */
public record DepositCommand(
    Long accountId,
    Money amount
) {}

// Made with Bob
