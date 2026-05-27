package com.thais.investment.statementservice.statement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatementRepository extends JpaRepository<Statement, String> {

    boolean existsBySettlementId(String settlementId);

    Optional<Statement> findBySettlementId(String settlementId);

    List<Statement> findByCustomerId(String customerId);

    List<Statement> findByOrderId(String orderId);
}