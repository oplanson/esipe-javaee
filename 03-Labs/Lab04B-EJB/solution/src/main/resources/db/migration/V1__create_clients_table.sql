-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

-- Drop table if exists to ensure clean state
DROP TABLE IF EXISTS clients CASCADE;

-- Create clients table
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX idx_clients_email ON clients(email);

-- Insert sample data
INSERT INTO clients (first_name, last_name, email, phone, address) VALUES
    ('John', 'Doe', 'john.doe@example.com', '+33 1 23 45 67 89', '123 Main St, Paris, France'),
    ('Jane', 'Smith', 'jane.smith@example.com', '+33 1 98 76 54 32', '456 Oak Ave, Lyon, France'),
    ('Bob', 'Johnson', 'bob.johnson@example.com', '+33 1 11 22 33 44', '789 Pine Rd, Marseille, France'),
    ('Alice', 'Williams', 'alice.williams@example.com', '+33 1 55 66 77 88', '321 Elm St, Toulouse, France'),
    ('Charlie', 'Brown', 'charlie.brown@example.com', '+33 1 99 88 77 66', '654 Maple Dr, Nice, France');

-- Made with Bob