// © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a failed JMS message.
 * Stores messages that failed after maximum redelivery attempts.
 */
@Entity
@Table(name = "failed_messages")
public class FailedMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "message_id", length = 255, nullable = false)
    private String messageId;
    
    @Column(name = "delivery_count")
    private Integer deliveryCount;
    
    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "failure_time", nullable = false)
    private LocalDateTime failureTime;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @Column(name = "processed", nullable = false)
    private boolean processed = false;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Lifecycle callback
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (failureTime == null) {
            failureTime = LocalDateTime.now();
        }
    }
    
    // Default constructor
    public FailedMessage() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Integer getDeliveryCount() {
        return deliveryCount;
    }

    public void setDeliveryCount(Integer deliveryCount) {
        this.deliveryCount = deliveryCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(LocalDateTime failureTime) {
        this.failureTime = failureTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "FailedMessage{" +
                "id=" + id +
                ", messageId='" + messageId + '\'' +
                ", deliveryCount=" + deliveryCount +
                ", failureTime=" + failureTime +
                ", processed=" + processed +
                '}';
    }
}

// Made with Bob