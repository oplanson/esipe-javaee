-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

-- Create clients table for Client Microservice
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address VARCHAR(500),
    is_premium BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX idx_clients_email ON clients(email);

-- Create index on premium status
CREATE INDEX idx_clients_premium ON clients(is_premium);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_clients_updated_at
    BEFORE UPDATE ON clients
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data
INSERT INTO clients (first_name, last_name, email, phone, address, is_premium) VALUES
    ('Jean', 'Dupont', 'jean.dupont@email.com', '0601020304', '123 Rue de Paris, 75001 Paris', false),
    ('Marie', 'Martin', 'marie.martin@email.com', '0602030405', '456 Avenue des Champs, 75008 Paris', true),
    ('Pierre', 'Bernard', 'pierre.bernard@email.com', '0603040506', '789 Boulevard Saint-Germain, 75006 Paris', false);

-- Made with Bob
