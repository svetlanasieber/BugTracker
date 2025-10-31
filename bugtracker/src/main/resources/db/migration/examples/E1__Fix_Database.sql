-- Fix database script for BugTracker application
-- This script will:
-- 1. Ensure admin users exist
-- 2. Ensure roles are properly set up
-- 3. Create sample projects if none exist
-- 4. Link admin users to projects

-- Create admin user if not exists
INSERT IGNORE INTO users (first_name, last_name, email, password, created_at, updated_at, is_active)
VALUES 
('Admin', 'User', 'admin@bugtracker.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', NOW(), NOW(), TRUE),
('System', 'Admin', 'admin@example.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', NOW(), NOW(), TRUE);

-- Create roles if not exist
INSERT IGNORE INTO roles (name) VALUES ('USER'), ('ADMIN');

-- Assign admin role to admin users
INSERT IGNORE INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r 
WHERE u.email IN ('admin@bugtracker.com', 'admin@example.com') 
AND r.name = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM users_roles ur 
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);

-- Create sample projects if none exist
INSERT INTO projects (name, description, is_active, created_at, updated_at)
SELECT 'Bug Tracker Development', 'Internal project for developing the bug tracking application', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM projects LIMIT 1);

-- Create a second project if only one exists
INSERT INTO projects (name, description, is_active, created_at, updated_at)
SELECT 'Website Redesign', 'Project to redesign and modernize the company website', TRUE, NOW(), NOW()
WHERE (SELECT COUNT(*) FROM projects) = 1;

-- Link admin users to projects
INSERT IGNORE INTO project_members (project_id, user_id)
SELECT p.id, u.id FROM projects p, users u
WHERE u.email IN ('admin@bugtracker.com', 'admin@example.com')
AND NOT EXISTS (
    SELECT 1 FROM project_members pm 
    WHERE pm.project_id = p.id AND pm.user_id = u.id
);

-- Display status
SELECT 'Database fix completed successfully!' AS status;
SELECT COUNT(*) AS admin_user_count FROM users u 
JOIN users_roles ur ON u.id = ur.user_id 
JOIN roles r ON ur.role_id = r.id
WHERE r.name = 'ADMIN';
SELECT COUNT(*) AS project_count FROM projects;
SELECT COUNT(*) AS project_members FROM project_members; 