package com.bugtracker.bugtracker.notification.controller;

import com.bugtracker.bugtracker.notification.dto.NotificationRequest;
import com.bugtracker.bugtracker.notification.dto.NotificationResponse;
import com.bugtracker.bugtracker.notification.model.NotificationStatus;
import com.bugtracker.bugtracker.notification.service.NotificationService;
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

/**
 * REST Controller for managing notifications.
 * Provides endpoints for sending and retrieving notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Service", description = "REST API for notification management")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * POST endpoint: Send a new notification.
     * This is one of the mandatory endpoints required by the course.
     * 
     * @param request the notification request
     * @return the created notification response
     */
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

    /**
     * GET endpoint: Retrieve all notifications with pagination.
     * This is one of the mandatory endpoints required by the course.
     * 
     * @param page page number (default 0)
     * @param size page size (default 10)
     * @return paginated list of notifications
     */
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

    /**
     * GET endpoint: Retrieve notifications by status.
     * Additional useful endpoint beyond the mandatory requirements.
     * 
     * @param status the notification status
     * @return list of notifications with the specified status
     */
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

    /**
     * GET endpoint: Retrieve notifications for a specific user.
     * Useful for the main application to get user's notification history.
     * 
     * @param userId the user ID
     * @return list of notifications for the user
     */
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

    /**
     * GET endpoint: Retrieve notifications for a specific bug.
     * Useful for tracking all notifications related to a bug.
     * 
     * @param bugId the bug ID
     * @return list of notifications for the bug
     */
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

    /**
     * GET endpoint: Get notification statistics.
     * Provides useful metrics about notification system performance.
     * 
     * @return notification statistics
     */
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

    /**
     * GET endpoint: Health check for the microservice.
     * Useful for monitoring and ensuring the service is running.
     * 
     * @return simple health status
     */
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