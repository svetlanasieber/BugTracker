package com.bugtracker.bugtracker.notification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a notification in the system.
 * 
 * This entity stores information about notifications sent to users
 * including email and SMS notifications for bug tracking events.
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of notification (EMAIL, SMS, PUSH)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    /**
     * Recipient of the notification (email address or phone number)
     */
    @Column(nullable = false)
    private String recipient;

    /**
     * Subject of the notification
     */
    @Column(nullable = false)
    private String subject;

    /**
     * Message content of the notification
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    /**
     * Status of the notification (PENDING, SENT, FAILED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    /**
     * Bug ID this notification is related to (from main application)
     */
    @Column(name = "bug_id")
    private Long bugId;

    /**
     * User ID this notification is for (from main application)
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Timestamp when notification was created
     */
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Timestamp when notification was sent (if successful)
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /**
     * Error message if notification failed
     */
    @Column(name = "error_message")
    private String errorMessage;
} 