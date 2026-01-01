-- Migration V5: Refactor database schema for DDD Value Objects
-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

-- Modify accounts table to support Money Value Object
-- Add separate columns for amount and currency
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS balance_amount DECIMAL(19,2);
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS balance_currency VARCHAR(3) DEFAULT 'EUR';

-- Migrate existing balance data to new columns
UPDATE accounts SET balance_amount = balance WHERE balance_amount IS NULL;
UPDATE accounts SET balance_currency = 'EUR' WHERE balance_currency IS NULL;

-- Make new columns NOT NULL after migration
ALTER TABLE accounts ALTER COLUMN balance_amount SET NOT NULL;
ALTER TABLE accounts ALTER COLUMN balance_currency SET NOT NULL;

-- Rename number column to account_number for clarity
ALTER TABLE accounts RENAME COLUMN number TO account_number;

-- Rename type column to account_type for clarity
ALTER TABLE accounts RENAME COLUMN type TO account_type;

-- Update account_type to use enum values (CHECKING, SAVINGS)
-- Data is already in correct format from previous migrations

-- Add check constraint for account_type
ALTER TABLE accounts DROP CONSTRAINT IF EXISTS accounts_account_type_check;
ALTER TABLE accounts ADD CONSTRAINT accounts_account_type_check 
    CHECK (account_type IN ('CHECKING', 'SAVINGS'));

-- Add check constraint for balance_currency (ISO 4217 codes)
ALTER TABLE accounts ADD CONSTRAINT accounts_balance_currency_check 
    CHECK (balance_currency ~ '^[A-Z]{3}$');

-- Add check constraint for balance_amount (must respect account type limits)
-- CHECKING accounts can go negative (overdraft), SAVINGS cannot
ALTER TABLE accounts ADD CONSTRAINT accounts_balance_amount_check 
    CHECK (
        (account_type = 'CHECKING' AND balance_amount >= -500.0) OR
        (account_type = 'SAVINGS' AND balance_amount >= 0.0)
    );

-- Create index on account_number for faster lookups
CREATE INDEX IF NOT EXISTS idx_accounts_account_number ON accounts(account_number);

-- Create index on account_type for filtering
CREATE INDEX IF NOT EXISTS idx_accounts_account_type ON accounts(account_type);

-- Note: We keep the old 'balance' column for now to avoid breaking existing code
-- It will be removed in a future migration after all code is updated
-- For now, we'll use a trigger to keep it in sync

-- Create trigger function to sync balance with balance_amount
CREATE OR REPLACE FUNCTION sync_account_balance()
RETURNS TRIGGER AS $$
BEGIN
    NEW.balance = NEW.balance_amount;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically sync balance
DROP TRIGGER IF EXISTS trigger_sync_account_balance ON accounts;
CREATE TRIGGER trigger_sync_account_balance
    BEFORE INSERT OR UPDATE ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION sync_account_balance();

-- Add comment to document the DDD refactoring
COMMENT ON COLUMN accounts.balance_amount IS 'Money Value Object - amount component';
COMMENT ON COLUMN accounts.balance_currency IS 'Money Value Object - currency component (ISO 4217)';
COMMENT ON COLUMN accounts.account_number IS 'AccountNumber Value Object - IBAN-like format';
COMMENT ON COLUMN accounts.account_type IS 'AccountType Value Object - enum (CHECKING, SAVINGS)';

-- Made with Bob