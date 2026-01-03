-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

-- Create accounts table for Account Microservice
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('CHECKING', 'SAVINGS', 'BUSINESS')),
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_accounts_client_id ON accounts(client_id);
CREATE INDEX idx_accounts_account_number ON accounts(account_number);
CREATE INDEX idx_accounts_status ON accounts(status);
CREATE INDEX idx_accounts_type ON accounts(account_type);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_accounts_updated_at
    BEFORE UPDATE ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data (client_id references clients in client-service database)
INSERT INTO accounts (account_number, client_id, account_type, balance, status) VALUES
    ('ACC001', 1, 'CHECKING', 5000.00, 'ACTIVE'),
    ('ACC002', 1, 'SAVINGS', 15000.00, 'ACTIVE'),
    ('ACC003', 2, 'CHECKING', 25000.00, 'ACTIVE'),
    ('ACC004', 2, 'BUSINESS', 100000.00, 'ACTIVE'),
    ('ACC005', 3, 'CHECKING', 3500.00, 'ACTIVE'),
    ('ACC006', 3, 'SAVINGS', 8000.00, 'ACTIVE');

-- Made with Bob
