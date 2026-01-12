<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Filters and Listeners Implementation Guide

## Overview

This guide documents the 5 new exercises added to Lab02-ServletsJSP focusing on Filters and Listeners - advanced Servlet features for implementing cross-cutting concerns and monitoring application lifecycle.

## 📋 Exercises Summary

### Exercise 1: Authentication Filter (20 minutes)
**File:** `com.bank.filter.AuthenticationFilter`

**Purpose:** Protect URLs requiring authentication and redirect unauthorized users to login page.

**Key Features:**
- URL pattern matching for public and protected resources
- Session-based authentication checking
- Automatic redirect to login with original URL preservation
- Configurable public URLs (CSS, JS, images, login page)
- Configurable protected URLs (/clients/*, /admin/*)

**Learning Objectives:**
- Understand filter lifecycle and chain processing
- Implement URL pattern matching
- Manage HTTP sessions
- Handle request/response manipulation

---

### Exercise 2: Logging Filter (20 minutes)
**File:** `com.bank.filter.LoggingFilter`

**Purpose:** Log all HTTP requests and responses with detailed timing information.

**Key Features:**
- Request logging (method, URI, parameters, session info)
- Response logging (status code, content type)
- Execution time measurement
- Performance warnings for slow requests (>1 second)
- Response wrapper to capture status codes

**Learning Objectives:**
- Implement request/response logging
- Measure performance metrics
- Create response wrappers
- Use Java logging framework

---

### Exercise 3: Session Counter Listener (15 minutes)
**File:** `com.bank.listener.SessionCounterListener`

**Purpose:** Track active user sessions and provide application-wide statistics.

**Key Features:**
- Thread-safe session counting with AtomicInteger
- Active sessions tracking
- Total sessions created tracking
- Session lifecycle logging (creation/destruction)
- User login/logout detection
- Statistics stored in ServletContext

**Learning Objectives:**
- Implement HttpSessionListener
- Implement HttpSessionAttributeListener
- Use thread-safe counters
- Store application-wide data in ServletContext

---

### Exercise 4: CORS Filter (15 minutes)
**File:** `com.bank.filter.CorsFilter`

**Purpose:** Handle Cross-Origin Resource Sharing for REST API endpoints.

**Key Features:**
- CORS headers configuration (Access-Control-Allow-*)
- Preflight request handling (OPTIONS method)
- Origin validation
- Configurable allowed origins, methods, and headers
- Credentials support

**Learning Objectives:**
- Understand CORS mechanism
- Handle preflight requests
- Configure security headers
- Support cross-origin API calls

---

### Exercise 5: Compression Filter (20 minutes)
**File:** `com.bank.filter.CompressionFilter`

**Purpose:** Compress HTTP responses using GZIP to optimize bandwidth usage.

**Key Features:**
- Client capability detection (Accept-Encoding header)
- Content type filtering (text/html, CSS, JS, JSON, XML)
- Minimum size threshold (1KB)
- GZIP compression
- Response wrapping and content capture
- Compression statistics logging

**Learning Objectives:**
- Implement response compression
- Create custom response wrappers
- Handle binary data
- Optimize web application performance

---

## 🏗️ Architecture

### Package Structure

```
src/main/java/com/bank/
├── filter/
│   ├── AuthenticationFilter.java      # Exercise 1
│   ├── LoggingFilter.java             # Exercise 2
│   ├── CorsFilter.java                # Exercise 4
│   └── CompressionFilter.java         # Exercise 5
└── listener/
    ├── SessionCounterListener.java    # Exercise 3
    └── ApplicationLifecycleListener.java
```

### Filter Execution Order

Filters are executed in the order they are loaded by the container. The typical order is:

1. **LoggingFilter** - Logs all requests (first to capture everything)
2. **CorsFilter** - Handles CORS for /api/* endpoints
3. **AuthenticationFilter** - Checks authentication
4. **CompressionFilter** - Compresses responses (last to compress final output)

### Listener Execution

Listeners are notified of events as they occur:

1. **ApplicationLifecycleListener** - Application startup/shutdown
2. **SessionCounterListener** - Session creation/destruction and attribute changes

---

## 🔧 Configuration

### web.xml Documentation

The `web.xml` file includes comprehensive documentation about all filters and listeners:

```xml
<!-- Filters are configured using @WebFilter annotations -->
<!-- Listeners are configured using @WebListener annotations -->
```

### Annotations Used

**Filters:**
```java
@WebFilter(filterName = "FilterName", urlPatterns = "/*")
```

**Listeners:**
```java
@WebListener
```

---

## 🧪 Testing

### Authentication Filter Testing

1. Try accessing `/clients/list` without authentication → should redirect to `/login`
2. Access public URLs (/, /css/*, /js/*) → should work without authentication
3. Login and access protected URLs → should work

### Logging Filter Testing

1. Check server logs for request/response entries
2. Verify timing information is displayed
3. Test slow requests to see performance warnings

### Session Listener Testing

1. Check logs for session creation messages
2. Verify active session count in ServletContext
3. Test user login/logout logging

### CORS Filter Testing

1. Use browser developer tools to check CORS headers
2. Test preflight requests (OPTIONS method)
3. Verify different origins are handled correctly

### Compression Filter Testing

1. Check response headers for `Content-Encoding: gzip`
2. Compare response sizes with/without compression
3. Verify only text-based content is compressed

---

## 📊 Key Concepts

### Filter Chain

Filters form a chain where each filter can:
- Process the request before passing it to the next filter
- Process the response after the next filter returns
- Short-circuit the chain by not calling `chain.doFilter()`

```java
@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
    // Pre-processing
    chain.doFilter(request, response); // Continue chain
    // Post-processing
}
```

### Response Wrapping

To capture or modify response content, create a wrapper:

```java
class ResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream outputStream;
    
    @Override
    public ServletOutputStream getOutputStream() {
        // Return custom output stream
    }
    
    public byte[] getCapturedContent() {
        return outputStream.toByteArray();
    }
}
```

### Thread Safety

When tracking application-wide statistics, use thread-safe classes:

```java
private static final AtomicInteger counter = new AtomicInteger(0);
int value = counter.incrementAndGet(); // Thread-safe increment
```

---

## 💡 Best Practices

1. **Filter Order Matters:** Consider the execution order when implementing multiple filters
2. **Always Call chain.doFilter():** Unless you intentionally want to short-circuit
3. **Use Appropriate Log Levels:** INFO for important events, FINE for details
4. **Handle Exceptions:** Wrap filter logic in try-finally blocks
5. **Test Thoroughly:** Filters affect all requests, so test edge cases
6. **Document Configuration:** Clearly document filter URL patterns and behavior
7. **Performance:** Be mindful of filter performance impact on every request

---

## 🚀 Extensions

Students can extend these exercises by:

1. **Authentication Filter:**
   - Add role-based access control
   - Implement remember-me functionality
   - Add brute-force protection

2. **Logging Filter:**
   - Add request/response body logging
   - Implement log rotation
   - Add metrics collection

3. **Session Listener:**
   - Add session timeout warnings
   - Implement session persistence
   - Add user activity tracking

4. **CORS Filter:**
   - Make configuration external (properties file)
   - Add dynamic origin validation
   - Implement CORS policy per endpoint

5. **Compression Filter:**
   - Add Brotli compression support
   - Implement compression level configuration
   - Add compression statistics dashboard

---

## 📚 Resources

- **Jakarta Servlet Specification:** https://jakarta.ee/specifications/servlet/
- **Filter Tutorial:** https://jakarta.ee/learn/docs/jakartaee-tutorial/current/web/servlets/servlets.html#filters
- **Listener Tutorial:** https://jakarta.ee/learn/docs/jakartaee-tutorial/current/web/servlets/servlets.html#listeners
- **CORS Specification:** https://www.w3.org/TR/cors/
- **GZIP Compression:** https://www.ietf.org/rfc/rfc1952.txt

---

**Created:** 2026-01-12  
**Author:** Olivier Planson  
**Lab:** Lab02-ServletsJSP - Filters and Listeners