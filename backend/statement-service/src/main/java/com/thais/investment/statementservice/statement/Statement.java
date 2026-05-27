package com.thais.investment.statementservice.statement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "statements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statement {

    @Id
    private String id;

    @Column(name = "settlement_id", nullable = false, unique = true)
    private String settlementId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "asset_code", nullable = false)
    private String assetCode;

    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "statement_type", nullable = false)
    private StatementType statementType;

    @Column(name = "document_key")
    private String documentKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatementStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Statement create(
            String settlementId,
            String orderId,
            String customerId,
            String assetCode,
            BigDecimal netAmount,
            LocalDate settlementDate,
            StatementType statementType
    ) {

        return Statement.builder()
                .id(UUID.randomUUID().toString())
                .settlementId(settlementId)
                .orderId(orderId)
                .customerId(customerId)
                .assetCode(assetCode)
                .netAmount(netAmount)
                .settlementDate(settlementDate)
                .statementType(statementType)
                .status(StatementStatus.GENERATED)
                .createdAt(LocalDateTime.now())
                .build();
    }
}