package com.bugtracker.notification.controller;

import com.bugtracker.notification.model.Notification;
import com.bugtracker.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @Test
    void createNotification_ShouldReturnCreatedNotification() throws Exception {
        // Arrange
        Notification notification = new Notification();
        notification.setRecipient("test@example.com");
        notification.setSubject("Test Subject");
        notification.setContent("Test Content");
        notification.setType("EMAIL");

        when(notificationService.createNotification(any(Notification.class)))
                .thenReturn(notification);

        // Act & Assert
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipient").value("test@example.com"))
                .andExpect(jsonPath("$.subject").value("Test Subject"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.type").value("EMAIL"));
    }

    @Test
    void getNotificationsForUser_ShouldReturnUserNotifications() throws Exception {
        // Arrange
        String recipient = "test@example.com";
        List<Notification> notifications = Arrays.asList(
            createNotification("Subject 1", recipient),
            createNotification("Subject 2", recipient)
        );

        when(notificationService.getNotificationsForUser(recipient))
                .thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user/{recipient}", recipient))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("Subject 1"))
                .andExpect(jsonPath("$[1].subject").value("Subject 2"))
                .andExpect(jsonPath("$[0].recipient").value(recipient))
                .andExpect(jsonPath("$[1].recipient").value(recipient));
    }

    @Test
    void markAsSent_ShouldUpdateNotification() throws Exception {
        // Arrange
        Long notificationId = 1L;
        Notification notification = createNotification("Test Subject", "test@example.com");
        notification.setId(notificationId);
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());

        when(notificationService.markAsSent(notificationId))
                .thenReturn(notification);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/{id}/mark-sent", notificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sent").value(true))
                .andExpect(jsonPath("$.sentAt").exists());
    }

    private Notification createNotification(String subject, String recipient) {
        Notification notification = new Notification();
        notification.setSubject(subject);
        notification.setRecipient(recipient);
        notification.setContent("Test Content");
        notification.setType("EMAIL");
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }
} 