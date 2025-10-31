package com.bugtracker.notification.service;

import com.bugtracker.notification.dto.NotificationRequest;
import com.bugtracker.notification.dto.NotificationResponse;
import com.bugtracker.notification.model.Notification;
import com.bugtracker.notification.model.NotificationStatus;
import com.bugtracker.notification.model.NotificationType;
import com.bugtracker.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;

    
    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        log.info("Sending notification of type {} to {}", request.getType(), request.getRecipient());

        
        Notification notification = Notification.builder()
                .type(request.getType())
                .recipient(request.getRecipient())
                .subject(request.getSubject())
                .message(request.getMessage())
                .bugId(request.getBugId())
                .userId(request.getUserId())
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        
        notification = notificationRepository.save(notification);

        
        try {
            switch (request.getType()) {
                case EMAIL:
                    sendEmailNotification(notification);
                    break;
                case SMS:
                    sendSmsNotification(notification);
                    break;
                case PUSH:
                    sendPushNotification(notification);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported notification type: " + request.getType());
            }

            
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            
        } catch (Exception e) {
            log.error("Failed to send notification {}: {}", notification.getId(), e.getMessage());
            
            
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
        }

        
        notification = notificationRepository.save(notification);

        return convertToResponse(notification);
    }

    
    public Page<NotificationResponse> getAllNotifications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(this::convertToResponse);
    }

    
    public List<NotificationResponse> getNotificationsByStatus(NotificationStatus status) {
        List<Notification> notifications = notificationRepository.findByStatus(status);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    
    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    
    public List<NotificationResponse> getNotificationsByBug(Long bugId) {
        List<Notification> notifications = notificationRepository.findByBugId(bugId);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    
    public NotificationStats getNotificationStats() {
        long total = notificationRepository.count();
        long sent = notificationRepository.countByStatus(NotificationStatus.SENT);
        long failed = notificationRepository.countByStatus(NotificationStatus.FAILED);
        long pending = notificationRepository.countByStatus(NotificationStatus.PENDING);

        return NotificationStats.builder()
                .total(total)
                .sent(sent)
                .failed(failed)
                .pending(pending)
                .build();
    }

    
    private void sendEmailNotification(Notification notification) {
        try {
            if (mailSender == null) {
                log.warn("JavaMailSender not configured - simulating email notification to {}", notification.getRecipient());
                log.info("SIMULATED EMAIL - To: {}, Subject: {}, Message: {}", 
                    notification.getRecipient(), notification.getSubject(), notification.getMessage());
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getRecipient());
            message.setSubject(notification.getSubject());
            message.setText(notification.getMessage());
            message.setFrom("noreply@bugtracker.com");

            mailSender.send(message);
            log.info("Email notification sent successfully to {}", notification.getRecipient());
            
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", notification.getRecipient(), e.getMessage());
            throw new RuntimeException("Failed to send email notification", e);
        }
    }

    
    private void sendSmsNotification(Notification notification) {
        
        log.info("SMS notification sent to {}: {}", 
                notification.getRecipient(), notification.getMessage());
        
        
        if (Math.random() < 0.1) {
            throw new RuntimeException("SMS service temporarily unavailable");
        }
    }

    
    private void sendPushNotification(Notification notification) {
        
        log.info("Push notification sent to device {}: {}", 
                notification.getRecipient(), notification.getMessage());
    }

    
    private NotificationResponse convertToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .recipient(notification.getRecipient())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .bugId(notification.getBugId())
                .userId(notification.getUserId())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .errorMessage(notification.getErrorMessage())
                .build();
    }

    
    @lombok.Builder
    @lombok.Data
    public static class NotificationStats {
        private long total;
        private long sent;
        private long failed;
        private long pending;
    }
} 