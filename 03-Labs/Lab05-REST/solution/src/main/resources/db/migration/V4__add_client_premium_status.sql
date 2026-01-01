-- Migration V4: Add premium status to clients
-- This allows differentiation between Premium and Standard clients
-- for demonstrating CDI Qualifiers (@Premium and @Standard)

ALTER TABLE clients 
ADD COLUMN is_premium BOOLEAN DEFAULT FALSE NOT NULL;

-- Add comment for documentation
COMMENT ON COLUMN clients.is_premium IS 'Indicates if client has premium status (true) or standard status (false)';

-- Update some existing clients to premium for testing
-- This will demonstrate both notification services in action
UPDATE clients 
SET is_premium = TRUE 
WHERE id IN (
    SELECT id 
    FROM clients 
    ORDER BY created_at 
    LIMIT 2
);

-- Made with Bob
