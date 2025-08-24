-- CONVERSION TESTS date range 2024-01-15 - 2024-01-18

INSERT INTO sales_data (id, tracking_id, tracking_code, visit_date, sale_date, sale_price, product, commission_amount) VALUES
(1, 'ABB-001', 'ABB', '2024-01-15 10:00:00', '2024-01-15 11:30:00', 99.99, 'Premium Widget', 9.99),
(2, 'ABB-002', 'ABB', '2024-01-15 14:00:00', '2024-01-15 09:15:00', 149.99, 'Deluxe Widget', 22.50),
(3, 'ABB-003', 'ABB', '2024-01-15 16:30:00', '2024-01-15 17:45:00', 79.99, 'Basic Widget', 7.99),

(4, 'ABB-002', 'ABB', '2024-01-16 14:00:00', null, null, 'Deluxe Widget', null),
(5, 'TBS-002', 'TBS', '2024-01-16 14:00:00', null, null, null, null),
(6, 'EKW-002', 'EKW', '2024-01-16 14:00:00', '2024-01-16 14:00:00', 10, 'Deluxe Widget', 1.0),
(7, 'ABB-003', 'ABB', '2024-01-18 14:00:00', '2024-01-18 14:00:00', null, null, null);


-- TOTAL COMMISSION TESTS date range 2025-01-15 - 2025-01-18

INSERT INTO sales_data (id, tracking_id, tracking_code, visit_date, sale_date, sale_price, product, commission_amount) VALUES
(8, 'ABB-001', 'ABB', '2025-01-15 10:00:00', '2025-01-15 11:30:00', 99.99, 'Premium Widget', 9.99),
(9, 'ABB-002', 'ABB', '2025-01-15 14:00:00', '2025-01-15 09:15:00', 149.99, 'Deluxe Widget', 22.50),
(10, 'ABB-003', 'ABB', '2025-01-15 16:30:00', '2025-01-15 17:45:00', 79.99, 'Basic Widget', 7.99),

(11, 'ABB-002', 'ABB', '2025-01-16 14:00:00', null, null, 'Deluxe Widget', null),
(12, 'TBS-002', 'TBS', '2025-01-16 14:00:00', null, null, null, null),
(13, 'EKW-002', 'EKW', '2025-01-16 14:00:00', '2025-01-16 14:00:00', 10, 'Deluxe Widget', 1.33),
(14, 'ABB-003', 'ABB', '2025-01-18 14:00:00', '2025-01-18 14:00:00', null, null, null);




-- PRODUCT CONVERSION TESTS