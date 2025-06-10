-- Make sure the roles exist
INSERT INTO roles (name) VALUES ('USER') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO roles (name) VALUES ('ADMIN') ON DUPLICATE KEY UPDATE name = name;

-- Create a new admin account with password 'admin123'
-- The password is BCrypt encoded: $2a$10$zVnLb.3Uo4YdJZA2YUW2DuqXIPV./NTdnCG7vPrYpM639R42n0uky
INSERT INTO users (first_name, last_name, email, password, is_active, created_at, updated_at) 
VALUES ('Super', 'Admin', 'superadmin@example.com', '$2a$10$zVnLb.3Uo4YdJZA2YUW2DuqXIPV./NTdnCG7vPrYpM639R42n0uky', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  password='$2a$10$zVnLb.3Uo4YdJZA2YUW2DuqXIPV./NTdnCG7vPrYpM639R42n0uky', 
  is_active=1, 
  updated_at=NOW();

-- Add roles to the admin user
INSERT INTO users_roles (user_id, role_id)
SELECT 
    (SELECT id FROM users WHERE email = 'superadmin@example.com'), 
    (SELECT id FROM roles WHERE name = 'USER')
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

INSERT INTO users_roles (user_id, role_id)
SELECT 
    (SELECT id FROM users WHERE email = 'superadmin@example.com'), 
    (SELECT id FROM roles WHERE name = 'ADMIN')
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id); 