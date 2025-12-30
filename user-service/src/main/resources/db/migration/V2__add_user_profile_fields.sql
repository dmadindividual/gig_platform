-- Add profile fields to users table
ALTER TABLE users ADD COLUMN bio VARCHAR(500);
ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);
ALTER TABLE users ADD COLUMN profile_image_url VARCHAR(500);

-- Add index on phone number for potential lookups
CREATE INDEX idx_users_phone ON users(phone_number);