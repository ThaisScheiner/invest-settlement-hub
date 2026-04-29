package com.thais.investment.settlementservice.settlement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, String> {

    List<Settlement> findByCustomerId(String customerId);

    boolean existsByOrderId(String orderId);
}