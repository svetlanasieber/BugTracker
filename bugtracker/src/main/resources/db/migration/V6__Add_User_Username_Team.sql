-- Add username and team columns to users table
-- This migration adds support for username and team fields

-- Add username column
ALTER TABLE users 
ADD COLUMN username VARCHAR(30) UNIQUE;

-- Add team column as ENUM
ALTER TABLE users 
ADD COLUMN team ENUM('DEVELOPER', 'QA_ENGINEER', 'PROJECT_MANAGER', 'BASIC_USER');

-- Create index for username for faster lookups
CREATE INDEX idx_users_username ON users(username);

-- Create index for team for faster lookups by team
CREATE INDEX idx_users_team ON users(team);

-- Update existing users to have a username based on their email
-- This ensures no null values for existing data
UPDATE users SET username = 
    CASE 
        WHEN email IS NOT NULL THEN SUBSTRING(email FROM '^([^@]+)')
        ELSE CONCAT('user_', id)
    END
WHERE username IS NULL;

-- Note: New users registered through the registration form will have their username set automatically
