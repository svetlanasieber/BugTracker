package com.bugtracker.bugtracker.notification.dto;

import com.bugtracker.bugtracker.notification.model.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating notification requests.
 * Used by the main Bug Tracker application to request notifications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    /**
     * Type of notification to send
     */
    @NotNull(message = "Notification type is required")
    private NotificationType type;

    /**
     * Recipient email address or phone number
     */
    @NotBlank(message = "Recipient is required")
    private String recipient;

    /**
     * Subject of the notification
     */
    @NotBlank(message = "Subject is required")
    private String subject;

    /**
     * Message content
     */
    @NotBlank(message = "Message is required")
    private String message;

    /**
     * Bug ID this notification relates to (optional)
     */
    private Long bugId;

    /**
     * User ID this notification is for (optional)
     */
    private Long userId;
} 