package com.bugtracker.notification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    
    @Column(nullable = false)
    private String recipient;

    
    @Column(nullable = false)
    private String subject;

    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    
    @Column(name = "bug_id")
    private Long bugId;

    
    @Column(name = "user_id")
    private Long userId;

    
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    
    @Column(name = "error_message")
    private String errorMessage;
} 