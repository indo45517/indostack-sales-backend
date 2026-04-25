-- Insert admin users for BillBharat Sales App
-- Password for all users: 123456
-- BCrypt hash: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lL2qMqKaAqAFGYsG6

-- 1. OWNER Role - Main Admin User
INSERT INTO users (id, name, phone_number, password, role, employee_id, email, is_active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'Vishal',
    '9999999999',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lL2qMqKaAqAFGYsG6',
    'OWNER',
    'EMP001',
    'vishal@billbharat.com',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 2. SALES_LEAD Role - Team Lead
INSERT INTO users (id, name, phone_number, password, role, employee_id, email, is_active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'Vishal Lead',
    '9999999991',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lL2qMqKaAqAFGYsG6',
    'SALES_LEAD',
    'EMP002',
    'vishal.lead@billbharat.com',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 3. SALES_EXECUTIVE Role - Field Executive
INSERT INTO users (id, name, phone_number, password, role, employee_id, email, is_active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'Vishal Executive',
    '9999999992',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lL2qMqKaAqAFGYsG6',
    'SALES_EXECUTIVE',
    'EMP003',
    'vishal.exec@billbharat.com',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Verify the inserted records
-- SELECT id, name, phone_number, role, employee_id, email, is_active FROM users WHERE employee_id IN ('EMP001', 'EMP002', 'EMP003');
