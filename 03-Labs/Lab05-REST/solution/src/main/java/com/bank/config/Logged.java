package com.bank.config;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interceptor binding annotation for logging.
 * 
 * Apply this annotation to classes or methods to enable automatic logging
 * of method entry and exit.
 * 
 * Example usage:
 * <pre>
 * {@code
 * @Logged
 * @ApplicationScoped
 * public class ClientService {
 *     // All methods will be logged
 * }
 * 
 * // Or on specific methods:
 * @ApplicationScoped
 * public class ClientService {
 *     @Logged
 *     public void create(Client client) {
 *         // Only this method will be logged
 *     }
 * }
 * }
 * </pre>
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Logged {
}

// Made with Bob
