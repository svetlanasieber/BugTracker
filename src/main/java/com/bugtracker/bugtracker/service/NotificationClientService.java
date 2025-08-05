package com.bugtracker.bugtracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service for integrating with the Notification Microservice.
 * Provides methods to send notifications via the external notification service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationClientService {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url:http://localhost:8081/notification-service}")
    private String notificationServiceUrl;

    /**
     * Send email notification about bug assignment.
     * 
     * @param userEmail recipient email
     * @param bugId bug ID
     * @param bugTitle bug title
     * @param userId user ID
     */
    public void sendBugAssignmentNotification(String userEmail, Long bugId, String bugTitle, Long userId) {
        log.info("Sending bug assignment notification to {} for bug {}", userEmail, bugId);
        
        NotificationRequest request = NotificationRequest.builder()
                .type("EMAIL")
                .recipient(userEmail)
                .subject("New Bug Assigned: " + bugTitle)
                .message(String.format("You have been assigned a new bug: %s (ID: %d). Please review and take appropriate action.", bugTitle, bugId))
                .bugId(bugId)
                .userId(userId)
                .build();

        sendNotification(request);
    }

    /**
     * Send email notification about bug status update.
     * 
     * @param userEmail recipient email
     * @param bugId bug ID
     * @param bugTitle bug title
     * @param newStatus new bug status
     * @param userId user ID
     */
    public void sendBugStatusUpdateNotification(String userEmail, Long bugId, String bugTitle, String newStatus, Long userId) {
        log.info("Sending bug status update notification to {} for bug {}", userEmail, bugId);
        
        NotificationRequest request = NotificationRequest.builder()
                .type("EMAIL")
                .recipient(userEmail)
                .subject("Bug Status Updated: " + bugTitle)
                .message(String.format("Bug %s (ID: %d) status has been changed to: %s", bugTitle, bugId, newStatus))
                .bugId(bugId)
                .userId(userId)
                .build();

        sendNotification(request);
    }

    /**
     * Send email notification about new comment on bug.
     * 
     * @param userEmail recipient email
     * @param bugId bug ID
     * @param bugTitle bug title
     * @param commenterName name of person who commented
     * @param userId user ID
     */
    public void sendNewCommentNotification(String userEmail, Long bugId, String bugTitle, String commenterName, Long userId) {
        log.info("Sending new comment notification to {} for bug {}", userEmail, bugId);
        
        NotificationRequest request = NotificationRequest.builder()
                .type("EMAIL")
                .recipient(userEmail)
                .subject("New Comment on Bug: " + bugTitle)
                .message(String.format("%s has added a new comment to bug %s (ID: %d). Check it out!", commenterName, bugTitle, bugId))
                .bugId(bugId)
                .userId(userId)
                .build();

        sendNotification(request);
    }

    /**
     * Send SMS notification (for urgent bugs).
     * 
     * @param phoneNumber recipient phone
     * @param bugId bug ID
     * @param bugTitle bug title
     * @param userId user ID
     */
    public void sendUrgentBugSmsNotification(String phoneNumber, Long bugId, String bugTitle, Long userId) {
        log.info("Sending urgent bug SMS notification to {} for bug {}", phoneNumber, bugId);
        
        NotificationRequest request = NotificationRequest.builder()
                .type("SMS")
                .recipient(phoneNumber)
                .subject("URGENT Bug Alert")
                .message(String.format("URGENT: Bug %s (ID: %d) requires immediate attention!", bugTitle, bugId))
                .bugId(bugId)
                .userId(userId)
                .build();

        sendNotification(request);
    }

    /**
     * Generic method to send notification to the microservice.
     * 
     * @param request notification request
     */
    private void sendNotification(NotificationRequest request) {
        try {
            String url = notificationServiceUrl + "/api/notifications";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<NotificationRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<NotificationResponse> response = restTemplate.postForEntity(
                    url, entity, NotificationResponse.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Notification sent successfully: {}", response.getBody());
            } else {
                log.warn("Failed to send notification. Status: {}", response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error sending notification to microservice: {}", e.getMessage(), e);
            // Don't throw exception - notification failure shouldn't break main functionality
        }
    }

    /**
     * DTO for notification requests (matching microservice contract).
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NotificationRequest {
        private String type;
        private String recipient;
        private String subject;
        private String message;
        private Long bugId;
        private Long userId;
    }

    /**
     * DTO for notification responses (matching microservice contract).
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NotificationResponse {
        private Long id;
        private String type;
        private String recipient;
        private String subject;
        private String message;
        private String status;
        private Long bugId;
        private Long userId;
        private String createdAt;
        private String sentAt;
        private String errorMessage;
    }
} 