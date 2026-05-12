package com.thais.investment.settlementservice.messaging;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderCreatedEvent(
        String correlationId,
        String orderId,
        String customerId,
        String assetCode,
        String operationType,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        OffsetDateTime createdAt
) {
}