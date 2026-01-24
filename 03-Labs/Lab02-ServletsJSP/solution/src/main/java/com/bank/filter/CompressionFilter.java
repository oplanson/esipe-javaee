package com.bank.filter;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 * Compression Filter - Exercise 5
 * 
 * This filter compresses HTTP responses using GZIP to optimize bandwidth usage.
 * It demonstrates:
 * - Response wrapping and manipulation
 * - GZIP compression
 * - Content-Encoding header handling
 * - Accept-Encoding header checking
 * - Performance optimization
 * 
 * The filter only compresses responses when:
 * - Client supports GZIP (Accept-Encoding: gzip)
 * - Response is text-based (HTML, CSS, JS, JSON, XML)
 * - Response size is above minimum threshold (1KB)
 * 
 * @author Olivier Planson
 * @version 1.0
 */
@WebFilter(filterName = "CompressionFilter", urlPatterns = "/*")
public class CompressionFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(CompressionFilter.class.getName());
    
    // Minimum size to compress (1KB)
    private static final int MIN_COMPRESSION_SIZE = 1024;
    
    // Compressible content types
    private static final String[] COMPRESSIBLE_TYPES = {
        "text/html",
        "text/css",
        "text/javascript",
        "application/javascript",
        "application/json",
        "application/xml",
        "text/xml",
        "text/plain"
    };
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("CompressionFilter initialized");
        LOGGER.info("Minimum compression size: " + MIN_COMPRESSION_SIZE + " bytes");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Check if client accepts GZIP compression
        String acceptEncoding = httpRequest.getHeader("Accept-Encoding");
        boolean supportsGzip = acceptEncoding != null && acceptEncoding.toLowerCase().contains("gzip");
        
        if (supportsGzip) {
            LOGGER.fine("Client supports GZIP compression");
            
            // Wrap response to capture output
            CompressionResponseWrapper wrappedResponse = new CompressionResponseWrapper(httpResponse);
            
            try {
                // Continue with filter chain
                chain.doFilter(request, wrappedResponse);
                
                // Get the captured output
                byte[] content = wrappedResponse.getCapturedContent();
                String contentType = wrappedResponse.getContentType();
                
                // Check if compression should be applied
                if (shouldCompress(content, contentType)) {
                    // Compress the content
                    byte[] compressedContent = compress(content);
                    
                    // Set compression headers BEFORE writing content
                    httpResponse.setHeader("Content-Encoding", "gzip");
                    httpResponse.setContentLength(compressedContent.length);
                    
                    // Preserve content type from wrapped response
                    if (contentType != null) {
                        httpResponse.setContentType(contentType);
                    }
                    
                    // Write compressed content
                    httpResponse.getOutputStream().write(compressedContent);
                    
                    LOGGER.info(String.format("Response compressed: %d bytes -> %d bytes (%.1f%% reduction)",
                        content.length, compressedContent.length,
                        (1 - (double) compressedContent.length / content.length) * 100));
                } else {
                    // Preserve content type from wrapped response
                    if (contentType != null) {
                        httpResponse.setContentType(contentType);
                    }
                    
                    // Write original content without compression
                    httpResponse.getOutputStream().write(content);
                    
                    if (content.length < MIN_COMPRESSION_SIZE) {
                        LOGGER.fine("Content too small for compression: " + content.length + " bytes");
                    } else {
                        LOGGER.fine("Content type not compressible: " + contentType);
                    }
                }
            } finally {
                wrappedResponse.finishResponse();
            }
        } else {
            // Client doesn't support GZIP, continue normally
            LOGGER.fine("Client does not support GZIP compression");
            chain.doFilter(request, response);
        }
    }
    
    @Override
    public void destroy() {
        LOGGER.info("CompressionFilter destroyed");
    }
    
    /**
     * Check if content should be compressed
     */
    private boolean shouldCompress(byte[] content, String contentType) {
        // Check minimum size
        if (content.length < MIN_COMPRESSION_SIZE) {
            return false;
        }
        
        // Check content type
        if (contentType == null) {
            return false;
        }
        
        String lowerContentType = contentType.toLowerCase();
        for (String type : COMPRESSIBLE_TYPES) {
            if (lowerContentType.contains(type)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Compress content using GZIP
     */
    private byte[] compress(byte[] content) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(content);
        }
        return byteStream.toByteArray();
    }
    
    /**
     * Response wrapper to capture output for compression
     */
    private static class CompressionResponseWrapper extends HttpServletResponseWrapper {
        private ByteArrayOutputStream outputStream;
        private PrintWriter writer;
        private ServletOutputStream servletOutputStream;
        
        public CompressionResponseWrapper(HttpServletResponse response) {
            super(response);
            outputStream = new ByteArrayOutputStream();
        }
        
        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called");
            }
            
            if (servletOutputStream == null) {
                servletOutputStream = new ServletOutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        outputStream.write(b);
                    }
                    
                    @Override
                    public boolean isReady() {
                        return true;
                    }
                    
                    @Override
                    public void setWriteListener(WriteListener writeListener) {
                        // Not implemented for this example
                    }
                };
            }
            
            return servletOutputStream;
        }
        
        @Override
        public PrintWriter getWriter() throws IOException {
            if (servletOutputStream != null) {
                throw new IllegalStateException("getOutputStream() has already been called");
            }
            
            if (writer == null) {
                String encoding = getCharacterEncoding();
                if (encoding == null) {
                    encoding = "UTF-8";
                }
                writer = new PrintWriter(new OutputStreamWriter(outputStream, encoding));
            }
            
            return writer;
        }
        
        @Override
        public void flushBuffer() throws IOException {
            // Don't flush to prevent committing the response
            if (writer != null) {
                writer.flush();
            }
        }
        
        @Override
        public void setContentLength(int len) {
            // Don't set content length on wrapped response
        }
        
        @Override
        public void setContentLengthLong(long len) {
            // Don't set content length on wrapped response
        }
        
        @Override
        public void setHeader(String name, String value) {
            // Allow headers to be set on the wrapper but don't propagate
            if (!"Content-Length".equalsIgnoreCase(name) && !"Content-Encoding".equalsIgnoreCase(name)) {
                super.setHeader(name, value);
            }
        }
        
        public byte[] getCapturedContent() {
            if (writer != null) {
                writer.flush();
            }
            return outputStream.toByteArray();
        }
        
        public void finishResponse() {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

// Made with Bob
