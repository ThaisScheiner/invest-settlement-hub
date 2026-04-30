ALTER TABLE settlements
ADD CONSTRAINT uk_settlements_order_id UNIQUE (order_id);