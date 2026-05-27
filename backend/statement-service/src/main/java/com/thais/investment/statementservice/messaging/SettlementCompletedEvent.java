package com.thais.investment.statementservice.messaging;

import java.math.BigDecimal;

public record SettlementCompletedEvent(
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