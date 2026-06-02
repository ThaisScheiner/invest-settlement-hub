package com.thais.investment.statementservice.statement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StatementResponse(
        String id,
        String settlementId,
        String orderId,
        String customerId,
        String assetCode,
        BigDecimal netAmount,
        LocalDate settlementDate,
        StatementType statementType,
        String documentKey,
        StatementStatus status,
        LocalDateTime createdAt
) {

    public static StatementResponse fromEntity(Statement statement) {
        return new StatementResponse(
                statement.getId(),
                statement.getSettlementId(),
                statement.getOrderId(),
                statement.getCustomerId(),
                statement.getAssetCode(),
                statement.getNetAmount(),
                statement.getSettlementDate(),
                statement.getStatementType(),
                statement.getDocumentKey(),
                statement.getStatus(),
                statement.getCreatedAt()
        );
    }
}