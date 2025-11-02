package com.bugtracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationClientService {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url:http://localhost:8082}")
    private String notificationServiceUrl;

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
        }
    }

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