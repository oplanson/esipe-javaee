/*
 * Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited.
 */
package com.bank.application.command;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.domain.valueobject.Email;

/**
 * Command to update client information
 */
public record UpdateClientCommand(
    Long clientId,
    String name,
    Email email,
    Boolean premium
) {}

// Made with Bob
