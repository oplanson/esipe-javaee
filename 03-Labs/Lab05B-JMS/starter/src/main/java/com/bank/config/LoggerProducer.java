// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import java.util.logging.Logger;

/**
 * CDI Producer for Logger instances.
 * Produces logger instances based on the injection point's class name.
 */
@ApplicationScoped
public class LoggerProducer {
    
    /**
     * Produces a Logger instance for the requesting bean.
     * 
     * @param injectionPoint The CDI injection point
     * @return Logger instance for the requesting class
     */
    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
}

// Made with Bob