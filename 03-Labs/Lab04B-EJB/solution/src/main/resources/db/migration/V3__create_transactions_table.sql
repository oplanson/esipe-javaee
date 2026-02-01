-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

-- Drop table if exists to ensure clean state
DROP TABLE IF EXISTS transactions CASCADE;

-- Create transactions table for EJB lab
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    balance_after DECIMAL(19, 2) NOT NULL,
    description VARCHAR(255),
    reference_number VARCHAR(50) UNIQUE,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_by VARCHAR(100),
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_timestamp ON transactions(timestamp);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_reference ON transactions(reference_number);

-- Insert sample transactions with balance_after values
-- Account 1 (ACC001) starts with 5000.00
INSERT INTO transactions (account_id, type, amount, balance_after, description, reference_number, processed_by) VALUES
    (1, 'DEPOSIT', 1000.00, 6000.00, 'Initial deposit', 'TXN-001', 'system'),
    (1, 'WITHDRAWAL', 500.00, 5500.00, 'ATM withdrawal', 'TXN-002', 'system'),
-- Account 2 (ACC002) starts with 15000.00
    (2, 'DEPOSIT', 5000.00, 20000.00, 'Salary deposit', 'TXN-003', 'system'),
-- Account 3 (ACC003) starts with 3500.00
    (3, 'DEPOSIT', 2000.00, 5500.00, 'Transfer from savings', 'TXN-004', 'system'),
    (3, 'WITHDRAWAL', 300.00, 5200.00, 'Online purchase', 'TXN-005', 'system'),
-- Account 4 (ACC004) starts with 25000.00
    (4, 'DEPOSIT', 10000.00, 35000.00, 'Investment return', 'TXN-006', 'system'),
-- Account 5 (ACC005) starts with 7500.00
    (5, 'DEPOSIT', 3000.00, 10500.00, 'Client payment', 'TXN-007', 'system'),
    (5, 'WITHDRAWAL', 1500.00, 9000.00, 'Bill payment', 'TXN-008', 'system');

-- Made with Bob