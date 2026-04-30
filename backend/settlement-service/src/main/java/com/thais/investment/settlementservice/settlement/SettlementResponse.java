package com.thais.investment.settlementservice.settlement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record SettlementResponse(
        String id,
        String orderId,
        String customerId,
        String assetCode,
        BigDecimal grossAmount,
        BigDecimal fees,
        BigDecimal netAmount,
        LocalDate settlementDate,
        SettlementStatus status,
        OffsetDateTime createdAt
) {
    public static SettlementResponse fromEntity(Settlement settlement) {
        return new SettlementResponse(
                settlement.getId(),
                settlement.getOrderId(),
                settlement.getCustomerId(),
                settlement.getAssetCode(),
                settlement.getGrossAmount(),
                settlement.getFees(),
                settlement.getNetAmount(),
                settlement.getSettlementDate(),
                settlement.getStatus(),
                settlement.getCreatedAt()
        );
    }
}