-- Check all users and their passwords
SELECT id, email, password, is_active FROM users;

-- Check user-role mappings
SELECT u.id, u.email, r.name as role
FROM users u
JOIN users_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
ORDER BY u.email;

-- Create a new test admin with raw password - DON'T USE FOR PRODUCTION
INSERT INTO users (first_name, last_name, email, password, is_active, created_at, updated_at) 
VALUES ('Test', 'Admin', 'testadmin@example.com', 'admin123', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE password='admin123', is_active=1, updated_at=NOW();

-- Add roles to test admin
INSERT INTO users_roles (user_id, role_id)
SELECT 
    (SELECT id FROM users WHERE email = 'testadmin@example.com'), 
    (SELECT id FROM roles WHERE name = 'USER')
ON DUPLICATE KEY UPDATE user_id = user_id;

INSERT INTO users_roles (user_id, role_id)
SELECT 
    (SELECT id FROM users WHERE email = 'testadmin@example.com'), 
    (SELECT id FROM roles WHERE name = 'ADMIN')
ON DUPLICATE KEY UPDATE user_id = user_id;

-- Check database structure to ensure columns are correctly defined
DESCRIBE users;
DESCRIBE roles;
DESCRIBE users_roles; 