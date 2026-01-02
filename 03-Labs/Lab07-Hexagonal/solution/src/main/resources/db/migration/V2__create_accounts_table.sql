-- Migration V2: Create accounts table
-- This migration adds the accounts table with foreign key to clients

CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(34) NOT NULL UNIQUE,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    type VARCHAR(20) NOT NULL CHECK (type IN ('CHECKING', 'SAVINGS')),
    client_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_client FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- Create index on client_id for faster joins
CREATE INDEX idx_accounts_client_id ON accounts(client_id);

-- Create index on account number for faster lookups
CREATE INDEX idx_accounts_number ON accounts(number);

-- Create index on type for filtering
CREATE INDEX idx_accounts_type ON accounts(type);

-- Insert sample accounts for existing clients
INSERT INTO accounts (number, balance, type, client_id) VALUES
    -- Jean Dupont (client_id = 1)
    ('FR7612345678901234567890123', 1500.00, 'CHECKING', 1),
    ('FR7698765432109876543210987', 5000.00, 'SAVINGS', 1),
    
    -- Marie Martin (client_id = 2)
    ('FR7611111111111111111111111', 2500.00, 'CHECKING', 2),
    
    -- Pierre Durand (client_id = 3)
    ('FR7622222222222222222222222', 3000.00, 'CHECKING', 3),
    ('FR7633333333333333333333333', 10000.00, 'SAVINGS', 3),
    
    -- Luc Petit (client_id = 5)
    ('FR7644444444444444444444444', 750.00, 'CHECKING', 5);

-- Made with Bob