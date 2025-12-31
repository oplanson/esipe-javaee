package com.bank.config;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI Qualifier for Premium services.
 * Used to distinguish premium implementations from standard ones.
 * 
 * Lab 04 - Advanced CDI: Qualifiers
 * 
 * Example usage:
 * <pre>
 * {@code
 * @Inject
 * @Premium
 * private NotificationService notificationService;
 * }
 * </pre>
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface Premium {
}

// Made with Bob