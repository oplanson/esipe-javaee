-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

-- Create transactions table
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_OUT', 'TRANSFER_IN')),
    amount DECIMAL(15, 2) NOT NULL CHECK (amount > 0),
    balance_before DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    description VARCHAR(500),
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    account_id BIGINT NOT NULL,
    target_account_id BIGINT,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_date ON transactions(transaction_date DESC);
CREATE INDEX idx_transactions_target_account ON transactions(target_account_id) WHERE target_account_id IS NOT NULL;

-- Add comment to table
COMMENT ON TABLE transactions IS 'Stores the history of all financial transactions (deposits, withdrawals, transfers)';

-- Add comments to columns
COMMENT ON COLUMN transactions.type IS 'Type of transaction: DEPOSIT, WITHDRAWAL, TRANSFER_OUT, or TRANSFER_IN';
COMMENT ON COLUMN transactions.amount IS 'Transaction amount (always positive)';
COMMENT ON COLUMN transactions.balance_before IS 'Account balance before the transaction';
COMMENT ON COLUMN transactions.balance_after IS 'Account balance after the transaction';
COMMENT ON COLUMN transactions.target_account_id IS 'For transfers: ID of the target account (null for deposits/withdrawals)';

-- Made with Bob