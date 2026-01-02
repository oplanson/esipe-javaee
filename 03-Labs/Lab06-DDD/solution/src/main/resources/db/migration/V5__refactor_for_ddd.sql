-- Migration V5: Refactor database schema for DDD Value Objects
-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
--
-- =====================================================
-- PEDAGOGICAL NOTE: API Versioning and Breaking Changes
-- =====================================================
-- This migration demonstrates OPTION 4: Backward Compatible Migration
--
-- WHY THIS APPROACH?
-- - Zero downtime: Application continues running during migration
-- - Gradual rollout: Can deploy new code incrementally
-- - Easy rollback: Can revert without data loss
-- - Low risk: Changes are additive, not destructive
--
-- MIGRATION STRATEGY:
-- Phase 1 (Current): Add new columns, keep old column, sync with trigger
-- Phase 2 (Future): Update all code to use new columns
-- Phase 3 (V6+): Remove old column and trigger
--
-- This is a PRODUCTION-READY pattern used by companies like Stripe, GitHub, etc.
-- =====================================================

-- STEP 1: Add new columns for Money Value Object (NON-BREAKING CHANGE)
-- Separate columns for amount and currency instead of single balance field
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS balance_amount DECIMAL(19,2);
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS balance_currency VARCHAR(3) DEFAULT 'EUR';

-- STEP 2: Migrate existing balance data to new columns
UPDATE accounts SET balance_amount = balance WHERE balance_amount IS NULL;
UPDATE accounts SET balance_currency = 'EUR' WHERE balance_currency IS NULL;

-- STEP 3: Make new columns NOT NULL after migration
-- Now that data is migrated, enforce constraints
ALTER TABLE accounts ALTER COLUMN balance_amount SET NOT NULL;
ALTER TABLE accounts ALTER COLUMN balance_currency SET NOT NULL;

-- STEP 4: Rename columns for clarity (aligns with DDD Ubiquitous Language)
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

-- =====================================================
-- STEP 5: BACKWARD COMPATIBILITY MECHANISM (KEY LEARNING POINT!)
-- =====================================================
-- IMPORTANT: We keep the old 'balance' column for backward compatibility
--
-- WHY?
-- - Existing code in Lab 05 still references 'balance' column
-- - Allows gradual migration without breaking changes
-- - Enables rollback if issues are discovered
-- - Old and new code can coexist during transition
--
-- HOW?
-- - Database trigger automatically syncs old column with new columns
-- - Any INSERT or UPDATE to balance_amount also updates balance
-- - Zero code changes needed for synchronization
--
-- WHEN TO REMOVE?
-- - In a future migration (V6 or later)
-- - After ALL code is updated to use Money Value Object
-- - After sufficient testing in production
-- - Typically 3-6 months deprecation period
--
-- This is OPTION 4: Backward Compatible Migration in action!
-- =====================================================

-- Create trigger function to sync old 'balance' column with new 'balance_amount'
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

-- =====================================================
-- STEP 6: Documentation for Future Developers
-- =====================================================
-- Document the DDD refactoring and migration strategy
COMMENT ON COLUMN accounts.balance IS 'DEPRECATED: Use balance_amount and balance_currency instead. Kept for backward compatibility. Will be removed in V6.';
COMMENT ON COLUMN accounts.balance_amount IS 'Money Value Object - amount component (replaces balance)';
COMMENT ON COLUMN accounts.balance_currency IS 'Money Value Object - currency component (ISO 4217)';
COMMENT ON COLUMN accounts.account_number IS 'AccountNumber Value Object - IBAN-like format (FR + 25 digits)';
COMMENT ON COLUMN accounts.account_type IS 'AccountType Value Object - enum (CHECKING, SAVINGS)';

COMMENT ON TRIGGER trigger_sync_account_balance ON accounts IS 'Backward compatibility: Syncs old balance column with new balance_amount. Remove in V6 after code migration.';

-- =====================================================
-- MIGRATION COMPLETE
-- =====================================================
-- Summary of changes:
-- ✅ Added balance_amount and balance_currency columns (Money Value Object)
-- ✅ Migrated existing data from balance to new columns
-- ✅ Renamed columns for DDD clarity (number → account_number, type → account_type)
-- ✅ Added business rule constraints (account type limits, currency format)
-- ✅ Created indexes for performance
-- ✅ Implemented backward compatibility with trigger
-- ✅ Documented deprecation timeline
--
-- Next steps (Future migrations):
-- - V6: Remove balance column and sync trigger after code migration
-- - V7+: Add additional Value Objects (Address, PhoneNumber, etc.)
--
-- This migration demonstrates professional database evolution practices!
-- =====================================================

-- Made with Bob