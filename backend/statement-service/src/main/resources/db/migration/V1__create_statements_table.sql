CREATE TABLE statements (
    id VARCHAR(36) NOT NULL,
    settlement_id VARCHAR(36) NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    asset_code VARCHAR(20) NOT NULL,
    net_amount DECIMAL(19, 2) NOT NULL,
    settlement_date DATE NOT NULL,
    statement_type VARCHAR(50) NOT NULL,
    document_key VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_statements_settlement_id (settlement_id),
    INDEX idx_statements_customer_id (customer_id),
    INDEX idx_statements_order_id (order_id)
);