-- ==========================================
-- V8: Change Project ID from BIGINT to UUID
-- ==========================================

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Drop foreign key constraint from bugs table
ALTER TABLE bugs DROP FOREIGN KEY bugs_ibfk_1;

-- Drop foreign key constraint from project_members table  
ALTER TABLE project_members DROP FOREIGN KEY project_members_ibfk_1;

-- Create temporary table with UUID
CREATE TABLE projects_temp (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    project_type VARCHAR(50),
    start_date DATETIME,
    end_date DATETIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Copy data with generated UUIDs
INSERT INTO projects_temp (id, name, description, project_type, start_date, end_date, is_active, created_at, updated_at)
SELECT 
    UUID() as id,
    name,
    description,
    project_type,
    start_date,
    end_date,
    is_active,
    created_at,
    updated_at
FROM projects;

-- Create mapping table for old ID to new UUID
CREATE TEMPORARY TABLE project_id_mapping (
    old_id BIGINT,
    new_id VARCHAR(36)
);

-- Store mapping (this assumes sequential processing)
-- Note: In production, you'd want to preserve the mapping more carefully
INSERT INTO project_id_mapping (old_id, new_id)
SELECT p_old.id as old_id, p_temp.id as new_id
FROM projects p_old
CROSS JOIN projects_temp p_temp
WHERE p_old.name = p_temp.name AND p_old.created_at = p_temp.created_at
ORDER BY p_old.id
LIMIT (SELECT COUNT(*) FROM projects);

-- Update bugs table with new UUIDs
ALTER TABLE bugs MODIFY COLUMN project_id VARCHAR(36);

UPDATE bugs b
INNER JOIN project_id_mapping pim ON b.project_id = pim.old_id
SET b.project_id = pim.new_id;

-- Update project_members table with new UUIDs
ALTER TABLE project_members MODIFY COLUMN project_id VARCHAR(36);

UPDATE project_members pm
INNER JOIN project_id_mapping pim ON pm.project_id = pim.old_id
SET pm.project_id = pim.new_id;

-- Drop old projects table
DROP TABLE projects;

-- Rename temp table to projects
RENAME TABLE projects_temp TO projects;

-- Re-add foreign key constraints
ALTER TABLE bugs
ADD CONSTRAINT fk_bugs_project
FOREIGN KEY (project_id) REFERENCES projects(id)
ON DELETE CASCADE;

ALTER TABLE project_members
ADD CONSTRAINT fk_project_members_project
FOREIGN KEY (project_id) REFERENCES projects(id)
ON DELETE CASCADE;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Add indexes for performance
CREATE INDEX idx_projects_name ON projects(name);
CREATE INDEX idx_projects_active ON projects(is_active);
CREATE INDEX idx_projects_type ON projects(project_type);





