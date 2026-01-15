package com.bank.web;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.config.JndiConfigService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet for validating transaction amounts using JNDI configuration.
 * Demonstrates how to use JNDI environment entries in a real-world scenario.
 * 
 * This servlet validates transaction amounts against the maximum allowed
 * amount configured in web.xml (app/maxTransactionAmount).
 */
@WebServlet(
    name = "TransactionValidatorServlet",
    urlPatterns = {"/validate-transaction", "/transaction/validate"}
)
public class TransactionValidatorServlet extends HttpServlet {
    
    private JndiConfigService configService;
    
    /**
     * Initialize the servlet and get the JNDI configuration service.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        this.configService = JndiConfigService.getInstance();
        log("TransactionValidatorServlet initialized with max amount: " + 
            configService.getMaxTransactionAmount());
    }
    
    /**
     * Handle GET requests - show validation form and information.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        // Get configuration values
        Double maxAmount = configService.getMaxTransactionAmount();
        String supportEmail = configService.getSupportEmail();
        Integer maxLoginAttempts = configService.getMaxLoginAttempts();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("    <title>Transaction Validator - JNDI Demo</title>");
        out.println("    <link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class='container'>");
        out.println("        <header class='hero'>");
        out.println("            <h1>💰 Transaction Validator</h1>");
        out.println("            <p class='subtitle'>JNDI Configuration Demo</p>");
        out.println("        </header>");
        out.println("        <main>");
        
        // Configuration Info Section
        out.println("            <section class='card'>");
        out.println("                <h2>📋 Current JNDI Configuration</h2>");
        out.println("                <div class='info-grid'>");
        out.println("                    <div class='info-item'>");
        out.println("                        <strong>Max Transaction Amount:</strong>");
        out.println("                        <span class='highlight'>€" + String.format("%.2f", maxAmount) + "</span>");
        out.println("                    </div>");
        out.println("                    <div class='info-item'>");
        out.println("                        <strong>Support Email:</strong>");
        out.println("                        <span>" + supportEmail + "</span>");
        out.println("                    </div>");
        out.println("                    <div class='info-item'>");
        out.println("                        <strong>Max Login Attempts:</strong>");
        out.println("                        <span>" + maxLoginAttempts + "</span>");
        out.println("                    </div>");
        out.println("                </div>");
        out.println("                <p class='note'>These values are configured via JNDI in web.xml</p>");
        out.println("            </section>");
        
        // Validation Form
        out.println("            <section class='card'>");
        out.println("                <h2>🔍 Validate Transaction Amount</h2>");
        out.println("                <form method='POST' action='validate-transaction'>");
        out.println("                    <div class='form-group'>");
        out.println("                        <label for='amount'>Transaction Amount (€):</label>");
        out.println("                        <input type='number' id='amount' name='amount' ");
        out.println("                               step='0.01' min='0.01' required ");
        out.println("                               placeholder='Enter amount...'>");
        out.println("                    </div>");
        out.println("                    <div class='form-group'>");
        out.println("                        <label for='description'>Description (optional):</label>");
        out.println("                        <input type='text' id='description' name='description' ");
        out.println("                               placeholder='e.g., Payment to supplier'>");
        out.println("                    </div>");
        out.println("                    <button type='submit' class='btn btn-primary'>Validate Transaction</button>");
        out.println("                </form>");
        out.println("            </section>");
        
        // Examples Section
        out.println("            <section class='card'>");
        out.println("                <h2>💡 Try These Examples</h2>");
        out.println("                <div class='examples'>");
        out.println("                    <p><strong>Valid amounts:</strong> €100.00, €5,000.00, €" + String.format("%.2f", maxAmount) + "</p>");
        out.println("                    <p><strong>Invalid amounts:</strong> €" + String.format("%.2f", maxAmount + 0.01) + ", €50,000.00, €100,000.00</p>");
        out.println("                </div>");
        out.println("            </section>");
        
        // API Testing Section
        out.println("            <section class='card'>");
        out.println("                <h2>🔧 API Testing</h2>");
        out.println("                <p>You can also test via curl:</p>");
        out.println("                <pre><code>curl -X POST http://localhost:9080/validate-transaction \\");
        out.println("  -d 'amount=5000.00&description=Test'</code></pre>");
        out.println("                <pre><code>curl -X POST http://localhost:9080/validate-transaction \\");
        out.println("  -d 'amount=15000.00&description=Too high'</code></pre>");
        out.println("            </section>");
        
        out.println("            <div class='nav-links'>");
        out.println("                <a href='.' class='btn btn-secondary'>← Back to Home</a>");
        out.println("            </div>");
        out.println("        </main>");
        out.println("        <footer>");
        out.println("            <p>&copy; 2026 Banking Application | Lab 03 - JNDI Demo</p>");
        out.println("        </footer>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }
    
    /**
     * Handle POST requests - validate transaction amount.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String amountParam = req.getParameter("amount");
        String description = req.getParameter("description");
        
        // Check if this is an API call (no HTML response expected)
        boolean isApiCall = "application/json".equals(req.getHeader("Accept")) ||
                           req.getHeader("X-Requested-With") != null;
        
        if (amountParam == null || amountParam.trim().isEmpty()) {
            if (isApiCall) {
                sendJsonResponse(resp, false, 0.0, "Amount parameter is required", null);
            } else {
                sendHtmlError(resp, "Amount parameter is required");
            }
            return;
        }
        
        try {
            Double amount = Double.parseDouble(amountParam);
            
            if (amount <= 0) {
                if (isApiCall) {
                    sendJsonResponse(resp, false, amount, "Amount must be positive", description);
                } else {
                    sendHtmlError(resp, "Amount must be positive: €" + String.format("%.2f", amount));
                }
                return;
            }
            
            // Validate using JNDI configuration
            boolean isValid = configService.isValidTransactionAmount(amount);
            Double maxAmount = configService.getMaxTransactionAmount();
            
            log(String.format("Transaction validation: amount=%.2f, valid=%b, max=%.2f", 
                amount, isValid, maxAmount));
            
            if (isApiCall) {
                sendJsonResponse(resp, isValid, amount, 
                    isValid ? "Transaction amount is valid" : 
                             "Transaction amount exceeds maximum allowed: €" + String.format("%.2f", maxAmount),
                    description);
            } else {
                sendHtmlResponse(resp, isValid, amount, maxAmount, description);
            }
            
        } catch (NumberFormatException e) {
            if (isApiCall) {
                sendJsonResponse(resp, false, 0.0, "Invalid amount format", description);
            } else {
                sendHtmlError(resp, "Invalid amount format: " + amountParam);
            }
        }
    }
    
    /**
     * Send HTML response with validation result.
     */
    private void sendHtmlResponse(HttpServletResponse resp, boolean isValid, 
                                  Double amount, Double maxAmount, String description)
            throws IOException {
        
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("    <title>Validation Result</title>");
        out.println("    <link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class='container'>");
        out.println("        <header class='hero'>");
        out.println("            <h1>💰 Transaction Validation Result</h1>");
        out.println("        </header>");
        out.println("        <main>");
        out.println("            <section class='card'>");
        
        if (isValid) {
            out.println("                <div class='alert alert-success'>");
            out.println("                    <h2>✅ Transaction Valid</h2>");
            out.println("                    <p>The transaction amount is within the allowed limit.</p>");
            out.println("                </div>");
        } else {
            out.println("                <div class='alert alert-error'>");
            out.println("                    <h2>❌ Transaction Invalid</h2>");
            out.println("                    <p>The transaction amount exceeds the maximum allowed limit.</p>");
            out.println("                </div>");
        }
        
        out.println("                <div class='result-details'>");
        out.println("                    <p><strong>Amount:</strong> €" + String.format("%.2f", amount) + "</p>");
        if (description != null && !description.trim().isEmpty()) {
            out.println("                    <p><strong>Description:</strong> " + description + "</p>");
        }
        out.println("                    <p><strong>Maximum Allowed:</strong> €" + String.format("%.2f", maxAmount) + "</p>");
        out.println("                    <p><strong>Status:</strong> <span class='" + 
                    (isValid ? "status-valid" : "status-invalid") + "'>" + 
                    (isValid ? "APPROVED" : "REJECTED") + "</span></p>");
        out.println("                </div>");
        
        if (!isValid) {
            out.println("                <div class='help-text'>");
            out.println("                    <p>💡 <strong>Tip:</strong> For transactions above €" + 
                        String.format("%.2f", maxAmount) + 
                        ", please contact " + configService.getSupportEmail() + "</p>");
            out.println("                </div>");
        }
        
        out.println("            </section>");
        out.println("            <div class='nav-links'>");
        out.println("                <a href='validate-transaction' class='btn btn-primary'>← Validate Another</a>");
        out.println("                <a href='.' class='btn btn-secondary'>Home</a>");
        out.println("            </div>");
        out.println("        </main>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }
    
    /**
     * Send JSON response for API calls.
     */
    private void sendJsonResponse(HttpServletResponse resp, boolean isValid, 
                                  Double amount, String message, String description)
            throws IOException {
        
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("{");
        out.println("  \"valid\": " + isValid + ",");
        out.println("  \"amount\": " + amount + ",");
        out.println("  \"maxAmount\": " + configService.getMaxTransactionAmount() + ",");
        out.println("  \"message\": \"" + message + "\"");
        if (description != null && !description.trim().isEmpty()) {
            out.println("  ,\"description\": \"" + description + "\"");
        }
        out.println("}");
    }
    
    /**
     * Send HTML error response.
     */
    private void sendHtmlError(HttpServletResponse resp, String errorMessage)
            throws IOException {
        
        resp.setContentType("text/html;charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        PrintWriter out = resp.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <title>Validation Error</title>");
        out.println("    <link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class='container'>");
        out.println("        <div class='alert alert-error'>");
        out.println("            <h2>❌ Validation Error</h2>");
        out.println("            <p>" + errorMessage + "</p>");
        out.println("        </div>");
        out.println("        <a href='validate-transaction' class='btn btn-primary'>← Try Again</a>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }
    
    @Override
    public void destroy() {
        log("TransactionValidatorServlet destroyed");
        super.destroy();
    }
}

// Made with Bob