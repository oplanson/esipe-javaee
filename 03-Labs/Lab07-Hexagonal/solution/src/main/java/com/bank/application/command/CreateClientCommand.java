/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.command;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.Email;

/**
 * Command to create a new client
 */
public record CreateClientCommand(
    String name,
    Email email,
    boolean premium
) {}

// Made with Bob
