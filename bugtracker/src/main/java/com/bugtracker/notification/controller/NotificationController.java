package com.bugtracker.notification.controller;

import com.bugtracker.notification.dto.NotificationRequest;
import com.bugtracker.notification.dto.NotificationResponse;
import com.bugtracker.notification.model.NotificationStatus;
import com.bugtracker.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        log.info("Received request to send notification: {}", request);
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Retrieving notifications - page: {}, size: {}", page, size);
        Page<NotificationResponse> notifications = notificationService.getAllNotifications(page, size);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        log.info("Retrieving notifications with status: {}", status);
        List<NotificationResponse> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUser(@PathVariable Long userId) {
        log.info("Retrieving notifications for user: {}", userId);
        List<NotificationResponse> notifications = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/bug/{bugId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByBug(@PathVariable Long bugId) {
        log.info("Retrieving notifications for bug: {}", bugId);
        List<NotificationResponse> notifications = notificationService.getNotificationsByBug(bugId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/stats")
    public ResponseEntity<NotificationService.NotificationStats> getNotificationStats() {
        log.info("Retrieving notification statistics");
        NotificationService.NotificationStats stats = notificationService.getNotificationStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notification Service is running");
    }
}
