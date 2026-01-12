package com.bank.listener;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Session Counter Listener - Exercise 3
 * 
 * This listener tracks active user sessions and provides statistics.
 * It demonstrates:
 * - Session lifecycle monitoring
 * - Thread-safe counter management
 * - Session creation and destruction events
 * - Application-wide statistics tracking
 * 
 * Features:
 * - Count active sessions
 * - Track total sessions created
 * - Log session lifecycle events
 * - Store statistics in ServletContext
 * 
 * @author Olivier Planson
 * @version 1.0
 */
@WebListener
public class SessionCounterListener implements HttpSessionListener, HttpSessionAttributeListener {
    
    private static final Logger LOGGER = Logger.getLogger(SessionCounterListener.class.getName());
    
    // Thread-safe counters
    private static final AtomicInteger activeSessions = new AtomicInteger(0);
    private static final AtomicInteger totalSessions = new AtomicInteger(0);
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        
        // Increment counters
        int active = activeSessions.incrementAndGet();
        int total = totalSessions.incrementAndGet();
        
        // Store statistics in ServletContext
        session.getServletContext().setAttribute("activeSessions", active);
        session.getServletContext().setAttribute("totalSessions", total);
        
        // Log session creation
        LOGGER.info(String.format("Session created: %s | Active: %d | Total: %d",
            session.getId(), active, total));
        
        // Set session timeout (30 minutes)
        session.setMaxInactiveInterval(30 * 60);
        
        // Store creation time
        session.setAttribute("creationTime", System.currentTimeMillis());
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        
        // Decrement active sessions counter
        int active = activeSessions.decrementAndGet();
        
        // Update ServletContext
        session.getServletContext().setAttribute("activeSessions", active);
        
        // Calculate session duration
        Long creationTime = (Long) session.getAttribute("creationTime");
        long duration = 0;
        if (creationTime != null) {
            duration = (System.currentTimeMillis() - creationTime) / 1000; // in seconds
        }
        
        // Log session destruction
        LOGGER.info(String.format("Session destroyed: %s | Active: %d | Duration: %d seconds",
            session.getId(), active, duration));
        
        // Log user logout if authenticated
        String username = (String) session.getAttribute("user");
        if (username != null) {
            LOGGER.info("User logged out: " + username);
        }
    }
    
    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        String name = se.getName();
        Object value = se.getValue();
        
        // Log user login
        if ("user".equals(name)) {
            LOGGER.info(String.format("User logged in: %s (Session: %s)",
                value, se.getSession().getId()));
        }
        
        LOGGER.fine(String.format("Session attribute added: %s = %s (Session: %s)",
            name, value, se.getSession().getId()));
    }
    
    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        String name = se.getName();
        Object value = se.getValue();
        
        // Log user logout
        if ("user".equals(name)) {
            LOGGER.info(String.format("User logged out: %s (Session: %s)",
                value, se.getSession().getId()));
        }
        
        LOGGER.fine(String.format("Session attribute removed: %s = %s (Session: %s)",
            name, value, se.getSession().getId()));
    }
    
    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        String name = se.getName();
        Object oldValue = se.getValue();
        Object newValue = se.getSession().getAttribute(name);
        
        LOGGER.fine(String.format("Session attribute replaced: %s | Old: %s | New: %s (Session: %s)",
            name, oldValue, newValue, se.getSession().getId()));
    }
    
    /**
     * Get the number of active sessions
     */
    public static int getActiveSessions() {
        return activeSessions.get();
    }
    
    /**
     * Get the total number of sessions created
     */
    public static int getTotalSessions() {
        return totalSessions.get();
    }
    
    /**
     * Reset counters (for testing purposes)
     */
    public static void resetCounters() {
        activeSessions.set(0);
        totalSessions.set(0);
        LOGGER.info("Session counters reset");
    }
}

// Made with Bob
