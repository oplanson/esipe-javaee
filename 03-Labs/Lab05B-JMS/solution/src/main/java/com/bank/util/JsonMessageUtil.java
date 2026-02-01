// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.util;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for JSON serialization/deserialization of JMS messages.
 * Provides secure alternative to Java Object Serialization.
 * 
 * Security Benefits:
 * - No deserialization vulnerabilities (no gadget chains)
 * - Type-safe deserialization with explicit class specification
 * - Human-readable message format for debugging
 * - Language-agnostic (can be consumed by non-Java services)
 */
public class JsonMessageUtil {
    
    private static final Logger LOGGER = Logger.getLogger(JsonMessageUtil.class.getName());
    private static final Jsonb jsonb = JsonbBuilder.create();
    
    /**
     * Serialize an object to JSON string.
     * 
     * @param object The object to serialize
     * @return JSON string representation
     * @throws JsonSerializationException if serialization fails
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        
        try {
            return jsonb.toJson(object);
        } catch (JsonbException e) {
            LOGGER.log(Level.SEVERE, "Failed to serialize object to JSON: " + e.getMessage(), e);
            throw new JsonSerializationException("Failed to serialize object to JSON", e);
        }
    }
    
    /**
     * Deserialize JSON string to specified type.
     * 
     * @param <T> The target type
     * @param json The JSON string
     * @param type The class of the target type
     * @return Deserialized object
     * @throws JsonSerializationException if deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> type) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Target type cannot be null");
        }
        
        try {
            return jsonb.fromJson(json, type);
        } catch (JsonbException e) {
            LOGGER.log(Level.SEVERE, 
                "Failed to deserialize JSON to " + type.getName() + ": " + e.getMessage(), e);
            throw new JsonSerializationException(
                "Failed to deserialize JSON to " + type.getName(), e);
        }
    }
    
    /**
     * Safely attempt to deserialize JSON, returning null on failure.
     * Useful for non-critical deserialization where failure should not stop processing.
     * 
     * @param <T> The target type
     * @param json The JSON string
     * @param type The class of the target type
     * @return Deserialized object or null if deserialization fails
     */
    public static <T> T fromJsonSafe(String json, Class<T> type) {
        try {
            return fromJson(json, type);
        } catch (JsonSerializationException e) {
            LOGGER.warning("Safe deserialization failed, returning null: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate that a string is valid JSON.
     * 
     * @param json The string to validate
     * @return true if valid JSON, false otherwise
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        
        try {
            jsonb.fromJson(json, Object.class);
            return true;
        } catch (JsonbException e) {
            return false;
        }
    }
    
    /**
     * Custom exception for JSON serialization/deserialization errors.
     */
    public static class JsonSerializationException extends RuntimeException {
        public JsonSerializationException(String message) {
            super(message);
        }
        
        public JsonSerializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

// Made with Bob