-- Add attachment support to comments table
-- This migration adds screenshot/file attachment functionality to comments

-- Add attachment filename column (stores UUID-based filename)
ALTER TABLE comments 
ADD COLUMN attachment_filename VARCHAR(255);

-- Add original filename column (stores user's original filename)
ALTER TABLE comments 
ADD COLUMN attachment_original_name VARCHAR(255);

-- Create index for faster lookups of comments with attachments
CREATE INDEX idx_comments_with_attachments ON comments(attachment_filename) 
WHERE attachment_filename IS NOT NULL;

-- Create comments_attachments directory (handled by FileStorageConfig)




