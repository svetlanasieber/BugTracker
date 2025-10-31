-- Insert default roles
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');

-- Insert default admin user with BCrypt encoded password ('admin')
INSERT INTO users (first_name, last_name, email, password, created_at, updated_at, is_active)
VALUES ('Admin', 'User', 'admin@example.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', NOW(), NOW(), TRUE);

-- Assign both USER and ADMIN roles to admin user
INSERT INTO users_roles (user_id, role_id)
SELECT (SELECT id FROM users WHERE email = 'admin@example.com'), (SELECT id FROM roles WHERE name = 'USER');

INSERT INTO users_roles (user_id, role_id)
SELECT (SELECT id FROM users WHERE email = 'admin@example.com'), (SELECT id FROM roles WHERE name = 'ADMIN');

-- Insert a sample project
INSERT INTO projects (name, description, start_date, end_date, is_active, created_at, updated_at)
VALUES ('Sample Project', 'This is a sample project for demonstration purposes', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE, NOW(), NOW()); 