/*
 * © Copyright 2026 Olivier Planson. All rights reserved.
 * Reproduction prohibited. Made with IBM Bob.
 */
package com.bank.web;

import com.bank.service.BatchTransferService;
import com.bank.service.BatchTransferService.TransferRequest;
import com.bank.service.BatchTransferService.BatchTransferResult;
import com.bank.service.TransactionComparisonService;
import com.bank.service.TimeoutTestService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet for testing Bean-Managed Transactions (BMT).
 * Provides a web interface to test batch processing, CMT vs BMT comparison,
 * and transaction timeout scenarios.
 */
@WebServlet("/test-transactions")
public class TransactionTestServlet extends HttpServlet {
    
    @Inject
    private BatchTransferService batchService;
    
    @Inject
    private TransactionComparisonService comparisonService;
    
    @Inject
    private TimeoutTestService timeoutService;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String testType = req.getParameter("test");
        
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<title>Transaction Tests - BMT Demo</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }");
        out.println("h1 { color: #333; }");
        out.println("h2 { color: #666; margin-top: 30px; }");
        out.println(".success { color: green; }");
        out.println(".error { color: red; }");
        out.println(".info { color: blue; }");
        out.println(".test-section { background: white; padding: 20px; margin: 20px 0; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        out.println(".nav { margin: 20px 0; }");
        out.println(".nav a { margin-right: 15px; padding: 10px 15px; background: #007bff; color: white; text-decoration: none; border-radius: 3px; }");
        out.println(".nav a:hover { background: #0056b3; }");
        out.println("pre { background: #f8f9fa; padding: 15px; border-radius: 3px; overflow-x: auto; }");
        out.println("</style>");
        out.println("</head><body>");
        
        out.println("<h1>🔄 BMT Transaction Tests</h1>");
        
        // Navigation
        out.println("<div class='nav'>");
        out.println("<a href='?test=batch'>Batch Processing</a>");
        out.println("<a href='?test=comparison'>CMT vs BMT</a>");
        out.println("<a href='?test=timeout'>Timeout Tests</a>");
        out.println("<a href='/index.html'>Back to Home</a>");
        out.println("</div>");
        
        if (testType == null || testType.isEmpty()) {
            showWelcome(out);
        } else {
            switch (testType) {
                case "batch":
                    testBatchProcessing(out);
                    break;
                case "comparison":
                    testComparison(out);
                    break;
                case "timeout":
                    testTimeout(out);
                    break;
                default:
                    showWelcome(out);
            }
        }
        
        out.println("</body></html>");
    }
    
    private void showWelcome(PrintWriter out) {
        out.println("<div class='test-section'>");
        out.println("<h2>Welcome to Transaction Management Tests</h2>");
        out.println("<p>This page demonstrates Bean-Managed Transactions (BMT) with UserTransaction.</p>");
        out.println("<h3>Available Tests:</h3>");
        out.println("<ul>");
        out.println("<li><strong>Batch Processing:</strong> Process multiple transfers with individual transaction boundaries</li>");
        out.println("<li><strong>CMT vs BMT:</strong> Compare Container-Managed vs Bean-Managed transactions</li>");
        out.println("<li><strong>Timeout Tests:</strong> Test transaction timeout behavior</li>");
        out.println("</ul>");
        out.println("<p>Select a test from the navigation above to begin.</p>");
        out.println("</div>");
    }
    
    private void testBatchProcessing(PrintWriter out) {
        out.println("<div class='test-section'>");
        out.println("<h2>Test 1: Batch Transfer Processing</h2>");
        out.println("<p>Processing multiple transfers with individual transaction boundaries. Each transfer is independent.</p>");
        
        // Create test transfers
        List<TransferRequest> requests = new ArrayList<>();
        requests.add(new TransferRequest("T1", 1L, 2L, new BigDecimal("100.00")));
        requests.add(new TransferRequest("T2", 2L, 3L, new BigDecimal("50.00")));
        requests.add(new TransferRequest("T3", 1L, 3L, new BigDecimal("999999.00"))); // Will fail - insufficient funds
        requests.add(new TransferRequest("T4", 3L, 1L, new BigDecimal("25.00")));
        requests.add(new TransferRequest("T5", 999L, 1L, new BigDecimal("10.00"))); // Will fail - account not found
        
        try {
            BatchTransferResult result = batchService.processBatch(requests);
            
            out.println("<h3>Results:</h3>");
            out.println("<p><strong>Total Requests:</strong> " + requests.size() + "</p>");
            out.println("<p><strong>Successful:</strong> <span class='success'>" + result.getSuccessCount() + "</span></p>");
            out.println("<p><strong>Failed:</strong> <span class='error'>" + result.getFailureCount() + "</span></p>");
            
            if (!result.getSuccessful().isEmpty()) {
                out.println("<h4 class='success'>✓ Successful Transfers:</h4>");
                out.println("<ul>");
                for (String msg : result.getSuccessful()) {
                    out.println("<li class='success'>" + msg + "</li>");
                }
                out.println("</ul>");
            }
            
            if (!result.getFailed().isEmpty()) {
                out.println("<h4 class='error'>✗ Failed Transfers:</h4>");
                out.println("<ul>");
                for (String msg : result.getFailed()) {
                    out.println("<li class='error'>" + msg + "</li>");
                }
                out.println("</ul>");
            }
            
            out.println("<div class='info'>");
            out.println("<h4>Key Observations:</h4>");
            out.println("<ul>");
            out.println("<li>Each transfer has its own transaction boundary</li>");
            out.println("<li>Failed transfers don't affect successful ones (partial success)</li>");
            out.println("<li>Useful for batch processing where some failures are acceptable</li>");
            out.println("</ul>");
            out.println("</div>");
            
        } catch (Exception e) {
            out.println("<p class='error'>Error during batch processing: " + e.getMessage() + "</p>");
        }
        
        out.println("</div>");
    }
    
    private void testComparison(PrintWriter out) {
        out.println("<div class='test-section'>");
        out.println("<h2>Test 2: CMT vs BMT Comparison</h2>");
        out.println("<p>Comparing Container-Managed Transactions (@Transactional) with Bean-Managed Transactions (UserTransaction).</p>");
        
        try {
            String result = comparisonService.comparePerformance(1L, 2L, new BigDecimal("10.00"));
            
            out.println("<h3>Performance Results:</h3>");
            out.println("<pre>" + result + "</pre>");
            
            out.println("<h3>Code Comparison:</h3>");
            
            out.println("<h4>CMT Approach (@Transactional):</h4>");
            out.println("<pre>");
            out.println("@Transactional\n");
            out.println("public void transfer(Long fromId, Long toId, BigDecimal amount) {\n");
            out.println("    Account from = em.find(Account.class, fromId);\n");
            out.println("    Account to = em.find(Account.class, toId);\n");
            out.println("    \n");
            out.println("    from.setBalance(from.getBalance().subtract(amount));\n");
            out.println("    to.setBalance(to.getBalance().add(amount));\n");
            out.println("    \n");
            out.println("    // Transaction automatically managed!\n");
            out.println("}");
            out.println("</pre>");
            out.println("<p class='success'>✓ Simple, clean, automatic rollback</p>");
            
            out.println("<h4>BMT Approach (UserTransaction):</h4>");
            out.println("<pre>");
            out.println("public void transfer(Long fromId, Long toId, BigDecimal amount) throws Exception {\n");
            out.println("    try {\n");
            out.println("        utx.begin();\n");
            out.println("        \n");
            out.println("        Account from = em.find(Account.class, fromId);\n");
            out.println("        Account to = em.find(Account.class, toId);\n");
            out.println("        \n");
            out.println("        from.setBalance(from.getBalance().subtract(amount));\n");
            out.println("        to.setBalance(to.getBalance().add(amount));\n");
            out.println("        \n");
            out.println("        utx.commit();\n");
            out.println("    } catch (Exception e) {\n");
            out.println("        utx.rollback();\n");
            out.println("        throw e;\n");
            out.println("    }\n");
            out.println("}");
            out.println("</pre>");
            out.println("<p class='info'>ℹ More control, more code, manual rollback required</p>");
            
            out.println("<div class='info'>");
            out.println("<h4>When to Use Each:</h4>");
            out.println("<ul>");
            out.println("<li><strong>CMT:</strong> 95% of use cases - simple CRUD operations</li>");
            out.println("<li><strong>BMT:</strong> Batch processing, multiple transaction boundaries, complex error recovery</li>");
            out.println("</ul>");
            out.println("</div>");
            
        } catch (Exception e) {
            out.println("<p class='error'>Error during comparison: " + e.getMessage() + "</p>");
        }
        
        out.println("</div>");
    }
    
    private void testTimeout(PrintWriter out) {
        out.println("<div class='test-section'>");
        out.println("<h2>Test 3: Transaction Timeout Tests</h2>");
        out.println("<p>Testing transaction timeout behavior with different timeout values.</p>");
        
        try {
            String results = timeoutService.runAllTests();
            out.println("<pre>" + results + "</pre>");
            
            out.println("<div class='info'>");
            out.println("<h4>Configuration:</h4>");
            out.println("<p>Transaction timeouts can be configured in <code>server.xml</code>:</p>");
            out.println("<pre>");
            out.println("<transaction\n");
            out.println("    totalTranLifetimeTimeout=\"120s\"\n");
            out.println("    maxTransactionTimeout=\"300s\"\n");
            out.println("    heuristicRetryInterval=\"10s\"\n");
            out.println("    heuristicRetryLimit=\"5\"/>");
            out.println("</pre>");
            out.println("<p>Or programmatically with <code>UserTransaction.setTransactionTimeout(seconds)</code></p>");
            out.println("</div>");
            
        } catch (Exception e) {
            out.println("<p class='error'>Error during timeout tests: " + e.getMessage() + "</p>");
        }
        
        out.println("</div>");
    }
}

// Made with Bob
