package com.bugtracker.notification.repository;

import com.bugtracker.notification.model.Notification;
import com.bugtracker.notification.model.NotificationStatus;
import com.bugtracker.notification.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient(String recipient);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByType(NotificationType type);

    List<Notification> findByUserId(Long userId);

    List<Notification> findByBugId(UUID bugId);

    List<Notification> findByCreatedAtAfter(LocalDateTime date);

    Page<Notification> findByStatusOrderByCreatedAtDesc(NotificationStatus status, Pageable pageable);

    long countByStatus(NotificationStatus status);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotificationsByUser(@Param("userId") Long userId, Pageable pageable);
} 