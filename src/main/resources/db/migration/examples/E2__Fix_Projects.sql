-- Check if admin exists, if not create it
INSERT IGNORE INTO users (first_name, last_name, email, password, created_at, updated_at, is_active)
VALUES ('Admin', 'User', 'admin@bugtracker.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', NOW(), NOW(), TRUE);

-- Check if roles exist, if not create them
INSERT IGNORE INTO roles (name) VALUES ('USER');
INSERT IGNORE INTO roles (name) VALUES ('ADMIN');

-- Assign admin role to admin user if not already assigned
INSERT IGNORE INTO users_roles (user_id, role_id)
SELECT (SELECT id FROM users WHERE email = 'admin@bugtracker.com'), (SELECT id FROM roles WHERE name = 'ADMIN');

-- Add a project if none exists
INSERT INTO projects (name, description, is_active, created_at, updated_at)
SELECT 'Bug Tracker Development', 'Internal project for developing the bug tracking application', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM projects LIMIT 1);

-- Get the admin user ID and most recent project ID
SET @admin_id = (SELECT id FROM users WHERE email = 'admin@bugtracker.com');
SET @project_id = (SELECT id FROM projects ORDER BY id DESC LIMIT 1);

-- Link admin to project if not already linked
INSERT IGNORE INTO project_members (project_id, user_id)
VALUES (@project_id, @admin_id);

-- Create a second sample project
INSERT INTO projects (name, description, is_active, created_at, updated_at)
SELECT 'Website Redesign', 'Project to redesign and modernize the company website', TRUE, NOW(), NOW()
WHERE (SELECT COUNT(*) FROM projects) < 2;

-- Get the second project ID
SET @project2_id = (SELECT id FROM projects ORDER BY id DESC LIMIT 1);

-- Link admin to second project
INSERT IGNORE INTO project_members (project_id, user_id)
VALUES (@project2_id, @admin_id); 