CREATE TABLE settlements (
    id VARCHAR(36) NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    asset_code VARCHAR(10) NOT NULL,
    gross_amount DECIMAL(19,2) NOT NULL,
    fees DECIMAL(19,2) NOT NULL,
    net_amount DECIMAL(19,2) NOT NULL,
    settlement_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_settlements PRIMARY KEY (id)
);