-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

-- Drop table if exists to ensure clean state
DROP TABLE IF EXISTS accounts CASCADE;

-- Create accounts table
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    account_type VARCHAR(20) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    client_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_accounts_client FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_accounts_client_id ON accounts(client_id);
CREATE INDEX idx_accounts_account_number ON accounts(account_number);
CREATE INDEX idx_accounts_status ON accounts(status);

-- Insert sample accounts
INSERT INTO accounts (account_number, account_type, balance, client_id, status) VALUES
    ('ACC001', 'CHECKING', 5000.00, 1, 'ACTIVE'),
    ('ACC002', 'SAVINGS', 15000.00, 1, 'ACTIVE'),
    ('ACC003', 'CHECKING', 3500.00, 2, 'ACTIVE'),
    ('ACC004', 'SAVINGS', 25000.00, 2, 'ACTIVE'),
    ('ACC005', 'CHECKING', 7500.00, 3, 'ACTIVE'),
    ('ACC006', 'CHECKING', 2000.00, 4, 'ACTIVE'),
    ('ACC007', 'SAVINGS', 50000.00, 4, 'ACTIVE'),
    ('ACC008', 'CHECKING', 1500.00, 5, 'ACTIVE');

-- Made with Bob