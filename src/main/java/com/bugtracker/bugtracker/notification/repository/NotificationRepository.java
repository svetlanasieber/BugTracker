package com.bugtracker.bugtracker.notification.repository;

import com.bugtracker.bugtracker.notification.model.Notification;
import com.bugtracker.bugtracker.notification.model.NotificationStatus;
import com.bugtracker.bugtracker.notification.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Notification entities.
 * Provides data access methods for notification management.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications by recipient
     */
    List<Notification> findByRecipient(String recipient);

    /**
     * Find notifications by status
     */
    List<Notification> findByStatus(NotificationStatus status);

    /**
     * Find notifications by type
     */
    List<Notification> findByType(NotificationType type);

    /**
     * Find notifications by user ID
     */
    List<Notification> findByUserId(Long userId);

    /**
     * Find notifications by bug ID
     */
    List<Notification> findByBugId(Long bugId);

    /**
     * Find notifications created after a specific date
     */
    List<Notification> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find notifications with pagination
     */
    Page<Notification> findByStatusOrderByCreatedAtDesc(NotificationStatus status, Pageable pageable);

    /**
     * Count notifications by status
     */
    long countByStatus(NotificationStatus status);

    /**
     * Find recent notifications for a user
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotificationsByUser(@Param("userId") Long userId, Pageable pageable);
} 