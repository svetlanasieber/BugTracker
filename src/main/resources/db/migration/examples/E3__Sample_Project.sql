-- Insert a sample project
INSERT INTO projects (name, description, is_active, created_at, updated_at)
VALUES ('Bug Tracker Development', 'Internal project for developing and maintaining the Bug Tracker application', TRUE, NOW(), NOW());

-- Get the admin user ID and project ID
SET @admin_id = (SELECT id FROM users WHERE email = 'admin@bugtracker.com');
SET @project_id = (SELECT id FROM projects WHERE name = 'Bug Tracker Development');

-- Link the admin user to the project as a member
INSERT INTO project_members (project_id, user_id)
VALUES (@project_id, @admin_id); 