package com.bugtracker.notification.dto;

import com.bugtracker.notification.model.NotificationStatus;
import com.bugtracker.notification.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private String recipient;
    private String subject;
    private String message;
    private NotificationStatus status;
    private Long bugId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private String errorMessage;
} 