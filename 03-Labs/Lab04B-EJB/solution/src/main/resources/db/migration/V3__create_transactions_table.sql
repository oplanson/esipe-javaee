-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

-- Drop table if exists to ensure clean state
DROP TABLE IF EXISTS transactions CASCADE;

-- Create transactions table for EJB lab
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    description VARCHAR(500),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_type ON transactions(transaction_type);

-- Insert sample transactions
INSERT INTO transactions (account_id, transaction_type, amount, description) VALUES
    (1, 'DEPOSIT', 1000.00, 'Initial deposit'),
    (1, 'WITHDRAWAL', 500.00, 'ATM withdrawal'),
    (2, 'DEPOSIT', 5000.00, 'Salary deposit'),
    (3, 'DEPOSIT', 2000.00, 'Transfer from savings'),
    (3, 'WITHDRAWAL', 300.00, 'Online purchase'),
    (4, 'DEPOSIT', 10000.00, 'Investment return'),
    (5, 'DEPOSIT', 3000.00, 'Client payment'),
    (5, 'WITHDRAWAL', 1500.00, 'Bill payment');

-- Made with Bob