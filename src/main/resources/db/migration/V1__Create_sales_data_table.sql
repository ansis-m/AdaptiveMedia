CREATE TABLE sales_data (
                            id BIGSERIAL PRIMARY KEY,
                            tracking_id VARCHAR(255) NOT NULL UNIQUE,
                            visit_date TIMESTAMP NOT NULL,
                            sale_date TIMESTAMP,
                            sale_price DECIMAL(15,2),
                            product VARCHAR(500),
                            commission_amount DECIMAL(15,2),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sales_data_tracking_id ON sales_data(tracking_id);

CREATE INDEX idx_sales_data_visit_date ON sales_data(visit_date);

CREATE INDEX idx_sales_data_sale_date ON sales_data(sale_date);

COMMENT ON TABLE sales_data IS 'Sales tracking data received from external APIs';
COMMENT ON COLUMN sales_data.tracking_id IS 'Unique identifier for tracking visitor to sale conversion';
COMMENT ON COLUMN sales_data.visit_date IS 'When the visitor first arrived';
COMMENT ON COLUMN sales_data.sale_date IS 'When the sale was completed (null if no sale yet)';
COMMENT ON COLUMN sales_data.sale_price IS 'Final sale price in USD';
COMMENT ON COLUMN sales_data.product IS 'Product name or identifier';
COMMENT ON COLUMN sales_data.commission_amount IS 'Commission earned from this sale';