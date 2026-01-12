package com.bank.listener;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Session Counter Listener - Exercise 3
 * 
 * TODO: Implement session listener that:
 * 1. Tracks active user sessions
 * 2. Counts total sessions created
 * 3. Logs session lifecycle events
 * 4. Stores statistics in ServletContext
 * 
 * @author Your Name
 * @version 1.0
 */
@WebListener
public class SessionCounterListener implements HttpSessionListener, HttpSessionAttributeListener {
    
    private static final Logger LOGGER = Logger.getLogger(SessionCounterListener.class.getName());
    
    // TODO: Create thread-safe counters using AtomicInteger
    private static final AtomicInteger activeSessions = null; // TODO: Initialize
    private static final AtomicInteger totalSessions = null; // TODO: Initialize
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        
        // TODO: Increment counters
        int active = 0; // TODO: Increment activeSessions
        int total = 0; // TODO: Increment totalSessions
        
        // TODO: Store statistics in ServletContext
        
        // TODO: Log session creation with ID and counters
        
        // TODO: Set session timeout (30 minutes)
        
        // TODO: Store creation time in session
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        
        // TODO: Decrement active sessions counter
        int active = 0; // TODO: Decrement activeSessions
        
        // TODO: Update ServletContext
        
        // TODO: Calculate session duration
        Long creationTime = null; // TODO: Get creation time from session
        long duration = 0; // TODO: Calculate duration in seconds
        
        // TODO: Log session destruction with ID, active count, and duration
        
        // TODO: Log user logout if authenticated (check "user" attribute)
    }
    
    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        String name = se.getName();
        Object value = se.getValue();
        
        // TODO: Log user login if attribute name is "user"
        
        // TODO: Log attribute addition (use FINE level)
    }
    
    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        String name = se.getName();
        Object value = se.getValue();
        
        // TODO: Log user logout if attribute name is "user"
        
        // TODO: Log attribute removal (use FINE level)
    }
    
    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        String name = se.getName();
        Object oldValue = se.getValue();
        Object newValue = se.getSession().getAttribute(name);
        
        // TODO: Log attribute replacement (use FINE level)
    }
    
    /**
     * TODO: Implement method to get the number of active sessions
     */
    public static int getActiveSessions() {
        return 0; // TODO: Return activeSessions count
    }
    
    /**
     * TODO: Implement method to get the total number of sessions created
     */
    public static int getTotalSessions() {
        return 0; // TODO: Return totalSessions count
    }
    
    /**
     * TODO: Implement method to reset counters (for testing)
     */
    public static void resetCounters() {
        // TODO: Reset both counters to 0
        // TODO: Log reset action
    }
}

// Made with Bob
