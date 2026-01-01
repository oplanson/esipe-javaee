-- Migration V3: Add account status column
-- This migration demonstrates schema evolution with Flyway

-- Add status column to accounts table
ALTER TABLE accounts ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Add check constraint for valid status values
ALTER TABLE accounts ADD CONSTRAINT chk_accounts_status 
    CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED'));

-- Create index on status for filtering
CREATE INDEX idx_accounts_status ON accounts(status);

-- Update existing accounts to have ACTIVE status (already default, but explicit)
UPDATE accounts SET status = 'ACTIVE' WHERE status IS NULL;

-- Made with Bob