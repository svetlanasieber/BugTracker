-- Create notifications table for integrated notification service (MySQL version)
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    bug_id BIGINT,
    user_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP NULL,
    error_message VARCHAR(500),
    
    -- Add constraints
    CONSTRAINT chk_notification_type CHECK (type IN ('EMAIL', 'SMS', 'PUSH')),
    CONSTRAINT chk_notification_status CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
    
    -- Add indexes for better performance
    INDEX idx_notifications_recipient (recipient),
    INDEX idx_notifications_status (status),
    INDEX idx_notifications_type (type),
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_bug_id (bug_id),
    INDEX idx_notifications_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 