package com.bank.config;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI Qualifier for Standard services.
 * Used to distinguish standard implementations from premium ones.
 * 
 * Lab 04 - Advanced CDI: Qualifiers
 * 
 * Example usage:
 * <pre>
 * {@code
 * @Inject
 * @Standard
 * private NotificationService notificationService;
 * }
 * </pre>
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface Standard {
}

// Made with Bob