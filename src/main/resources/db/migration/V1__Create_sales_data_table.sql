CREATE TABLE sales_data (
                            id BIGINT PRIMARY KEY,
                            tracking_id VARCHAR(255) NOT NULL,
                            tracking_code VARCHAR(255) NOT NULL,
                            visit_date TIMESTAMP NOT NULL,
                            sale_date TIMESTAMP,
                            sale_price DECIMAL(15,2),
                            product VARCHAR(500),
                            commission_amount DECIMAL(15,2)
);

CREATE INDEX idx_sales_data_tracking_id ON sales_data(tracking_id);

-- For landing page code + date interval queries (APIs 1 & 2)
CREATE INDEX idx_sales_data_code_visit_sale_dates ON sales_data(tracking_code, visit_date, sale_date);

-- For product conversion rates by date interval (API 3)
CREATE INDEX idx_sales_data_visit_sale_product ON sales_data(visit_date, sale_date, product);

-- Alternative covering index for API 1 & 2 (includes commission in index)
CREATE INDEX idx_sales_data_code_dates_commission ON sales_data(tracking_code, visit_date, sale_date) INCLUDE (commission_amount);

COMMENT ON TABLE sales_data IS 'Sales tracking data received from external APIs';
COMMENT ON COLUMN sales_data.tracking_id IS 'Unique identifier for tracking visitor to sale conversion';
COMMENT ON COLUMN sales_data.visit_date IS 'When the visitor first arrived';
COMMENT ON COLUMN sales_data.sale_date IS 'When the sale was completed (null if no sale yet)';
COMMENT ON COLUMN sales_data.sale_price IS 'Final sale price in USD';
COMMENT ON COLUMN sales_data.product IS 'Product name or identifier';
COMMENT ON COLUMN sales_data.commission_amount IS 'Commission earned from this sale';