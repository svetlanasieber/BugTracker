-- Add project_type column to projects table
ALTER TABLE projects ADD COLUMN IF NOT EXISTS project_type VARCHAR(50) AFTER description;

-- Update existing projects to have a default type (can be modified later by users)
UPDATE projects SET project_type = 'SOFTWARE' WHERE project_type IS NULL; 