package com.bugtracker.notification.controller;

import com.bugtracker.notification.dto.NotificationRequest;
import com.bugtracker.notification.dto.NotificationResponse;
import com.bugtracker.notification.model.NotificationStatus;
import com.bugtracker.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Notification Service", description = "REST API for notification management")
public class NotificationController {

    private final NotificationService notificationService;

    
    @PostMapping
    @Operation(summary = "Send a new notification", 
               description = "Creates and sends a notification of the specified type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notification sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        
        log.info("Received request to send notification: {}", request);
        
        try {
            NotificationResponse response = notificationService.sendNotification(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
            throw e;
        }
    }

    
    @GetMapping
    @Operation(summary = "Get all notifications", 
               description = "Retrieves all notifications with pagination support")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<NotificationResponse>> getAllNotifications(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        log.info("Retrieving notifications - page: {}, size: {}", page, size);
        
        Page<NotificationResponse> notifications = notificationService.getAllNotifications(page, size);
        return ResponseEntity.ok(notifications);
    }

    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get notifications by status", 
               description = "Retrieves notifications filtered by their status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NotificationResponse>> getNotificationsByStatus(
            @Parameter(description = "Notification status") @PathVariable NotificationStatus status) {
        
        log.info("Retrieving notifications with status: {}", status);
        
        List<NotificationResponse> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }

    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications for a user", 
               description = "Retrieves all notifications for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        log.info("Retrieving notifications for user: {}", userId);
        
        List<NotificationResponse> notifications = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    
    @GetMapping("/bug/{bugId}")
    @Operation(summary = "Get notifications for a bug", 
               description = "Retrieves all notifications related to a specific bug")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Bug not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NotificationResponse>> getNotificationsByBug(
            @Parameter(description = "Bug ID") @PathVariable Long bugId) {
        
        log.info("Retrieving notifications for bug: {}", bugId);
        
        List<NotificationResponse> notifications = notificationService.getNotificationsByBug(bugId);
        return ResponseEntity.ok(notifications);
    }

    
    @GetMapping("/stats")
    @Operation(summary = "Get notification statistics", 
               description = "Retrieves statistics about notification system performance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationService.NotificationStats> getNotificationStats() {
        
        log.info("Retrieving notification statistics");
        
        NotificationService.NotificationStats stats = notificationService.getNotificationStats();
        return ResponseEntity.ok(stats);
    }

    
    @GetMapping("/health")
    @Operation(summary = "Health check", 
               description = "Checks if the notification service is running")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notification Service is running");
    }
} 