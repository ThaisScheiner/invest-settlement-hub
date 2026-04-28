CREATE TABLE orders (
    id VARCHAR(36) NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    asset_code VARCHAR(10) NOT NULL,
    operation_type VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_orders PRIMARY KEY (id)
);