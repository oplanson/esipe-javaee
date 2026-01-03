<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Banking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>⚠️ Error</h1>
            <p class="subtitle">Something went wrong</p>
        </header>
        
        <main>
            <div class="error-message">
                <h2>An error occurred</h2>
                <c:choose>
                    <c:when test="${not empty error}">
                        <p><strong>Error:</strong> ${error}</p>
                    </c:when>
                    <c:when test="${not empty pageContext.exception}">
                        <p><strong>Error:</strong> ${pageContext.exception.message}</p>
                    </c:when>
                    <c:otherwise>
                        <p>An unexpected error occurred. Please try again later.</p>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div class="info-section" style="margin-top: 30px;">
                <h3>Possible Causes</h3>
                <ul>
                    <li><strong>Service Unavailable:</strong> One or more backend services may be temporarily unavailable</li>
                    <li><strong>Network Issue:</strong> Communication between services may have failed</li>
                    <li><strong>Invalid Data:</strong> The request may contain invalid or incomplete data</li>
                    <li><strong>Circuit Breaker Open:</strong> Too many failures triggered the circuit breaker</li>
                </ul>
            </div>
            
            <div class="info-section" style="margin-top: 30px;">
                <h3>What to do?</h3>
                <ul>
                    <li>Try refreshing the page</li>
                    <li>Go back and try again</li>
                    <li>Check if all services are running</li>
                    <li>Wait a moment and retry (circuit breaker may be open)</li>
                </ul>
            </div>
            
            <div class="actions" style="margin-top: 30px; justify-content: center;">
                <a href="javascript:history.back()" class="button secondary">← Go Back</a>
                <a href="${pageContext.request.contextPath}/" class="button">🏠 Home</a>
                <a href="${pageContext.request.contextPath}/clients" class="button">👥 Clients</a>
            </div>
            
            <div class="info-section" style="margin-top: 30px; background: #f8f9fa; padding: 20px; border-radius: 10px;">
                <h3>🛡️ Fault Tolerance</h3>
                <p>This application implements comprehensive fault tolerance:</p>
                <ul style="margin-top: 10px;">
                    <li><strong>Circuit Breaker:</strong> Prevents cascading failures</li>
                    <li><strong>Retry:</strong> Automatically retries failed requests</li>
                    <li><strong>Timeout:</strong> Prevents hanging requests</li>
                    <li><strong>Fallback:</strong> Provides default responses when services fail</li>
                </ul>
                <p style="margin-top: 15px;">
                    Even when backend services are unavailable, the application continues to function 
                    with degraded capabilities thanks to these fault tolerance patterns.
                </p>
            </div>
            
            <c:if test="${pageContext.request.serverName == 'localhost' || pageContext.request.serverName == '127.0.0.1'}">
                <div class="info-section" style="margin-top: 30px; background: #fff3cd; padding: 20px; border-radius: 10px; border-left: 4px solid #ffc107;">
                    <h3>🔧 Debug Information (Development Mode)</h3>
                    <c:if test="${not empty pageContext.exception}">
                        <p><strong>Exception Type:</strong> ${pageContext.exception.class.name}</p>
                        <p><strong>Message:</strong> ${pageContext.exception.message}</p>
                        <c:if test="${not empty pageContext.exception.cause}">
                            <p><strong>Cause:</strong> ${pageContext.exception.cause.message}</p>
                        </c:if>
                    </c:if>
                    <p><strong>Request URI:</strong> ${pageContext.request.requestURI}</p>
                    <p><strong>Method:</strong> ${pageContext.request.method}</p>
                </div>
            </c:if>
        </main>
        
        <footer>
            <p>&copy; 2025-2026 Olivier Planson - Banking Microservices Lab 08</p>
        </footer>
    </div>
</body>
</html>