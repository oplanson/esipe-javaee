-- Migration V1: Create clients table
-- This is the initial database schema for the banking application

CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX idx_clients_email ON clients(email);

-- Create index on name for search functionality
CREATE INDEX idx_clients_name ON clients(name);

-- Insert sample data
INSERT INTO clients (name, email) VALUES
    ('Jean Dupont', 'jean.dupont@example.com'),
    ('Marie Martin', 'marie.martin@example.com'),
    ('Pierre Durand', 'pierre.durand@example.com'),
    ('Sophie Bernard', 'sophie.bernard@example.com'),
    ('Luc Petit', 'luc.petit@example.com');

-- Made with Bob