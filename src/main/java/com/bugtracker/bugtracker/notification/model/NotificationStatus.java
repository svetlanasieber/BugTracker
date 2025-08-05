package com.bugtracker.bugtracker.notification.model;

/**
 * Enumeration for notification status.
 */
public enum NotificationStatus {
    /**
     * Notification is pending to be sent
     */
    PENDING,
    
    /**
     * Notification has been successfully sent
     */
    SENT,
    
    /**
     * Notification failed to send
     */
    FAILED
} 