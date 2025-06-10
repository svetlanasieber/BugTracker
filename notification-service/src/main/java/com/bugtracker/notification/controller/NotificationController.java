package com.bugtracker.notification.controller;

import com.bugtracker.notification.model.Notification;
import com.bugtracker.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.createNotification(notification));
    }

    @GetMapping("/user/{recipient}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable String recipient) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(recipient));
    }

    @PutMapping("/{id}/mark-sent")
    public ResponseEntity<Notification> markAsSent(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsSent(id));
    }
} 