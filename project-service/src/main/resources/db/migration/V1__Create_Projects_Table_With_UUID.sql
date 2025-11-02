-- ==========================================
-- V1: Create Projects Table with UUID
-- ==========================================

CREATE TABLE IF NOT EXISTS projects (
    id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT 'UUID for unique project identification',
    name VARCHAR(255) NOT NULL,
    description TEXT,
    project_type VARCHAR(50),
    start_date DATETIME,
    end_date DATETIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by_user_id BIGINT COMMENT 'User ID from main application',
    created_by_username VARCHAR(255),
    member_user_ids TEXT COMMENT 'Comma-separated list of member user IDs',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_projects_name (name),
    INDEX idx_projects_active (is_active),
    INDEX idx_projects_created_by (created_by_user_id),
    INDEX idx_projects_type (project_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Projects table for microservice with UUID primary key';





