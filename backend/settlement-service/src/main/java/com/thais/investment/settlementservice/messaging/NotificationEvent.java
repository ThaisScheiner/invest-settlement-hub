package com.thais.investment.settlementservice.messaging;

import java.math.BigDecimal;

public record NotificationEvent(
        String settlementId,
        String orderId,
        String customerId,
        String assetCode,
        BigDecimal netAmount,
        String settlementDate,
        String status,
        String message
) {
}