package com.thais.investment.orderservice.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderResponse {

    private String id;
    private String customerId;
    private String assetCode;
    private OperationType operationType;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private OffsetDateTime createdAt;

    public OrderResponse(
            String id,
            String customerId,
            String assetCode,
            OperationType operationType,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal totalAmount,
            OrderStatus status,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.assetCode = assetCode;
        this.operationType = operationType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getAssetCode(),
                order.getOperationType(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}