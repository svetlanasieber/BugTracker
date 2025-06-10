-- Update the existing account you normally use (replace with your actual email)
UPDATE users 
SET password = 'plainpassword123' 
WHERE email = 'svetlana.sieber99@gmail.com';

-- Also update the admin account with a plain password
UPDATE users 
SET password = 'plainpassword123' 
WHERE email = 'superadmin@example.com';

-- If you want to check current users:
SELECT id, email, password, is_active FROM users;

-- Check user roles
SELECT u.id, u.email, r.name as role
FROM users u
JOIN users_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
ORDER BY u.email; 