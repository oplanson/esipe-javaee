-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

-- Insert sample transactions for demonstration purposes
-- These transactions show different types: DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN

-- Deposits for Alice's checking account (id=1)
INSERT INTO transactions (type, amount, balance_before, balance_after, description, transaction_date, account_id, target_account_id)
VALUES 
    ('DEPOSIT', 1000.00, 0.00, 1000.00, 'Initial deposit', CURRENT_TIMESTAMP - INTERVAL '30 days', 1, NULL),
    ('DEPOSIT', 500.00, 1000.00, 1500.00, 'Salary payment', CURRENT_TIMESTAMP - INTERVAL '15 days', 1, NULL),
    ('DEPOSIT', 200.00, 1500.00, 1700.00, 'Freelance payment', CURRENT_TIMESTAMP - INTERVAL '7 days', 1, NULL);

-- Withdrawals from Alice's checking account (id=1)
INSERT INTO transactions (type, amount, balance_before, balance_after, description, transaction_date, account_id, target_account_id)
VALUES 
    ('WITHDRAWAL', 150.00, 1700.00, 1550.00, 'ATM withdrawal', CURRENT_TIMESTAMP - INTERVAL '5 days', 1, NULL),
    ('WITHDRAWAL', 50.00, 1550.00, 1500.00, 'Grocery shopping', CURRENT_TIMESTAMP - INTERVAL '3 days', 1, NULL);

-- Deposits for Alice's savings account (id=2)
INSERT INTO transactions (type, amount, balance_before, balance_after, description, transaction_date, account_id, target_account_id)
VALUES 
    ('DEPOSIT', 5000.00, 0.00, 5000.00, 'Initial savings', CURRENT_TIMESTAMP - INTERVAL '25 days', 2, NULL),
    ('DEPOSIT', 1000.00, 5000.00, 6000.00, 'Monthly savings', CURRENT_TIMESTAMP - INTERVAL '10 days', 2, NULL);

-- Transfer from Alice's checking (id=1) to savings (id=2)
INSERT INTO transactions (type, amount, balance_before, balance_after, description, transaction_date, account_id, target_account_id)
VALUES 
    ('TRANSFER_OUT', 300.00, 1500.00, 1200.00, 'Transfer to savings', CURRENT_TIMESTAMP - INTERVAL '2 days', 1, 2),
    ('TRANSFER_IN', 300.00, 6000.00, 6300.00, 'Transfer from checking', CURRENT_TIMESTAMP - INTERVAL '2 days', 2, 1);

-- Deposits for Bob's checking account (id=3)
INSERT INTO transactions (type, amount, balance_before, balance_after, description, transaction_date, account_id, target_account_id)
VALUES 
    ('DEPOSIT', 2500.00, 0.00, 2500.00, 'Initial deposit', CURRENT_TIMESTAMP - INTERVAL '20 days', 3, NULL),
    ('DEPOSIT', 800.00, 2500.00, 3300.00, 'Salary payment', CURRENT_TIMESTAMP - INTERVAL '8 days', 3, NULL);

-- Withdrawals from Bob's checking account (id=3)
INSERT INTO transactions (type, amount, balance_before, balance_after, description, transaction_date, account_id, target_account_id)
VALUES 
    ('WITHDRAWAL', 200.00, 3300.00, 3100.00, 'Rent payment', CURRENT_TIMESTAMP - INTERVAL '6 days', 3, NULL),
    ('WITHDRAWAL', 100.00, 3100.00, 3000.00, 'Utilities', CURRENT_TIMESTAMP - INTERVAL '4 days', 3, NULL);

-- Transfer from Bob's checking (id=3) to Alice's checking (id=1)
INSERT INTO transactions (type, amount, balance_before, balance_after, description, transaction_date, account_id, target_account_id)
VALUES 
    ('TRANSFER_OUT', 500.00, 3000.00, 2500.00, 'Payment to Alice', CURRENT_TIMESTAMP - INTERVAL '1 day', 3, 1),
    ('TRANSFER_IN', 500.00, 1200.00, 1700.00, 'Payment from Bob', CURRENT_TIMESTAMP - INTERVAL '1 day', 1, 3);

-- Recent transactions for demonstration
INSERT INTO transactions (type, amount, balance_before, balance_after, description, transaction_date, account_id, target_account_id)
VALUES 
    ('DEPOSIT', 100.00, 1700.00, 1800.00, 'Cash deposit', CURRENT_TIMESTAMP - INTERVAL '12 hours', 1, NULL),
    ('WITHDRAWAL', 50.00, 2500.00, 2450.00, 'Coffee shop', CURRENT_TIMESTAMP - INTERVAL '6 hours', 3, NULL),
    ('DEPOSIT', 250.00, 6300.00, 6550.00, 'Interest payment', CURRENT_TIMESTAMP - INTERVAL '3 hours', 2, NULL);

-- Made with Bob