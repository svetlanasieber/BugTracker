-- Add project_type column to projects table
ALTER TABLE projects ADD COLUMN project_type VARCHAR(50);

-- Update existing projects to have default type
UPDATE projects SET project_type = 'SOFTWARE' WHERE project_type IS NULL;

-- Create project_types table if needed for future expansion
CREATE TABLE IF NOT EXISTS project_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert initial project types
INSERT INTO project_types (name, description) VALUES 
('SOFTWARE', 'Software Development Project'),
('HARDWARE', 'Hardware Development Project'),
('MOBILE', 'Mobile Application Project'),
('WEB', 'Web Application Project'),
('DESKTOP', 'Desktop Application Project'),
('INTEGRATION', 'Integration Project'),
('RESEARCH', 'Research & Development Project'),
('OTHER', 'Other Project Type'); 