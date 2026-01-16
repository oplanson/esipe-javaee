// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.web;

import com.bank.event.TransactionEvent;
import com.bank.producer.TransactionEventProducer;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Test servlet for JMS messaging functionality.
 * Provides endpoints to test different messaging scenarios.
 */
@WebServlet(urlPatterns = {"/test-messaging", "/test-messaging/*"})
public class MessagingTestServlet extends HttpServlet {
    
    @Inject
    private Logger logger;
    
    @Inject
    private TransactionEventProducer producer;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        String pathInfo = req.getPathInfo();
        
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>JMS Messaging Test</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println("h1 { color: #333; }");
            out.println(".success { color: green; font-weight: bold; }");
            out.println(".error { color: red; font-weight: bold; }");
            out.println(".info { background: #f0f0f0; padding: 15px; margin: 10px 0; border-radius: 5px; }");
            out.println("a { display: inline-block; margin: 10px 10px 10px 0; padding: 10px 20px; ");
            out.println("    background: #007bff; color: white; text-decoration: none; border-radius: 5px; }");
            out.println("a:hover { background: #0056b3; }");
            out.println("</style></head><body>");
            
            out.println("<h1>JMS Messaging Test</h1>");
            
            if (pathInfo == null || pathInfo.equals("/")) {
                showMenu(out);
            } else {
                handleTest(pathInfo, out);
            }
            
            out.println("<hr>");
            out.println("<p><a href='/test-messaging'>Back to Menu</a></p>");
            out.println("<p><a href='/'>Home</a></p>");
            out.println("</body></html>");
            
        } catch (Exception e) {
            logger.severe("Error in test servlet: " + e.getMessage());
            out.println("<p class='error'>Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }
    
    /**
     * Show test menu.
     */
    private void showMenu(PrintWriter out) {
        out.println("<h2>Available Tests</h2>");
        out.println("<div class='info'>");
        out.println("<p>Select a test to execute:</p>");
        out.println("<a href='/test-messaging/deposit'>Test Deposit Event</a>");
        out.println("<a href='/test-messaging/withdrawal'>Test Withdrawal Event</a>");
        out.println("<a href='/test-messaging/transfer'>Test Transfer Event</a>");
        out.println("<a href='/test-messaging/email'>Test Email Notification</a>");
        out.println("<a href='/test-messaging/audit'>Test Audit Event</a>");
        out.println("<a href='/test-messaging/all'>Test All Events</a>");
        out.println("<a href='/test-messaging/batch'>Test Batch (10 messages)</a>");
        out.println("</div>");
    }
    
    /**
     * Handle specific test.
     */
    private void handleTest(String pathInfo, PrintWriter out) {
        out.println("<h2>Test Results</h2>");
        
        switch (pathInfo) {
            case "/deposit":
                testDeposit(out);
                break;
            case "/withdrawal":
                testWithdrawal(out);
                break;
            case "/transfer":
                testTransfer(out);
                break;
            case "/email":
                testEmail(out);
                break;
            case "/audit":
                testAudit(out);
                break;
            case "/all":
                testAllEvents(out);
                break;
            case "/batch":
                testBatch(out);
                break;
            default:
                out.println("<p class='error'>Unknown test: " + pathInfo + "</p>");
        }
    }
    
    /**
     * Test deposit event.
     */
    private void testDeposit(PrintWriter out) {
        TransactionEvent event = createEvent(1001L, "DEPOSIT", new BigDecimal("500.00"));
        producer.sendTransactionEvent(event);
        
        out.println("<div class='info'>");
        out.println("<p class='success'>✓ Deposit event sent successfully!</p>");
        out.println("<p>Transaction ID: " + event.getTransactionId() + "</p>");
        out.println("<p>Amount: $" + event.getAmount() + "</p>");
        out.println("<p>Check Liberty logs for MDB processing.</p>");
        out.println("</div>");
    }
    
    /**
     * Test withdrawal event.
     */
    private void testWithdrawal(PrintWriter out) {
        TransactionEvent event = createEvent(1002L, "WITHDRAWAL", new BigDecimal("200.00"));
        producer.sendTransactionEvent(event);
        
        out.println("<div class='info'>");
        out.println("<p class='success'>✓ Withdrawal event sent successfully!</p>");
        out.println("<p>Transaction ID: " + event.getTransactionId() + "</p>");
        out.println("<p>Amount: $" + event.getAmount() + "</p>");
        out.println("<p>Check Liberty logs for MDB processing.</p>");
        out.println("</div>");
    }
    
    /**
     * Test transfer event.
     */
    private void testTransfer(PrintWriter out) {
        TransactionEvent event = createEvent(1003L, "TRANSFER", new BigDecimal("1000.00"));
        producer.sendTransactionEvent(event);
        
        out.println("<div class='info'>");
        out.println("<p class='success'>✓ Transfer event sent successfully!</p>");
        out.println("<p>Transaction ID: " + event.getTransactionId() + "</p>");
        out.println("<p>Amount: $" + event.getAmount() + "</p>");
        out.println("<p>Check Liberty logs for MDB processing.</p>");
        out.println("</div>");
    }
    
    /**
     * Test email notification.
     */
    private void testEmail(PrintWriter out) {
        TransactionEvent event = createEvent(1004L, "DEPOSIT", new BigDecimal("300.00"));
        event.setCustomerEmail("customer@example.com");
        producer.sendEmailNotification(event);
        
        out.println("<div class='info'>");
        out.println("<p class='success'>✓ Email notification sent successfully!</p>");
        out.println("<p>Transaction ID: " + event.getTransactionId() + "</p>");
        out.println("<p>Email: " + event.getCustomerEmail() + "</p>");
        out.println("<p>Check Liberty logs for EmailNotificationMDB processing.</p>");
        out.println("</div>");
    }
    
    /**
     * Test audit event.
     */
    private void testAudit(PrintWriter out) {
        TransactionEvent event = createEvent(1005L, "DEPOSIT", new BigDecimal("750.00"));
        producer.publishAuditEvent(event);
        
        out.println("<div class='info'>");
        out.println("<p class='success'>✓ Audit event published successfully!</p>");
        out.println("<p>Transaction ID: " + event.getTransactionId() + "</p>");
        out.println("<p>Check Liberty logs for AuditLoggingMDB processing.</p>");
        out.println("<p>Check database for audit_logs table entry.</p>");
        out.println("</div>");
    }
    
    /**
     * Test all events.
     */
    private void testAllEvents(PrintWriter out) {
        TransactionEvent event = createEvent(1006L, "TRANSFER", new BigDecimal("2500.00"));
        event.setCustomerEmail("customer@example.com");
        producer.sendAllEvents(event);
        
        out.println("<div class='info'>");
        out.println("<p class='success'>✓ All events sent successfully!</p>");
        out.println("<p>Transaction ID: " + event.getTransactionId() + "</p>");
        out.println("<p>Sent to: Transaction Queue, Email Queue, Audit Topic</p>");
        out.println("<p>Check Liberty logs for all MDB processing.</p>");
        out.println("</div>");
    }
    
    /**
     * Test batch processing.
     */
    private void testBatch(PrintWriter out) {
        out.println("<div class='info'>");
        out.println("<p class='success'>✓ Sending 10 messages...</p>");
        
        for (int i = 1; i <= 10; i++) {
            TransactionEvent event = createEvent(2000L + i, "DEPOSIT", 
                                                new BigDecimal(100 * i));
            producer.sendTransactionEvent(event);
            out.println("<p>Message " + i + " sent: Transaction ID " + event.getTransactionId() + "</p>");
        }
        
        out.println("<p class='success'>✓ All 10 messages sent successfully!</p>");
        out.println("<p>Check Liberty logs for MDB processing.</p>");
        out.println("</div>");
    }
    
    /**
     * Create a test transaction event.
     */
    private TransactionEvent createEvent(Long txId, String type, BigDecimal amount) {
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(txId);
        event.setAccountId(12345L);
        event.setAccountNumber("ACC-" + System.currentTimeMillis());
        event.setAmount(amount);
        event.setType(type);
        event.setTimestamp(LocalDateTime.now());
        event.setStatus("SUCCESS");
        event.setDescription("Test " + type + " transaction");
        return event;
    }
}

// Made with Bob