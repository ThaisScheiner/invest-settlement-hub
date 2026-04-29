package com.thais.investment.settlementservice.settlement;

import com.thais.investment.settlementservice.messaging.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class SettlementService {

    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    private final SettlementRepository repository;

    public SettlementService(SettlementRepository repository) {
        this.repository = repository;
    }

    public void process(OrderCreatedEvent event) {
        log.info("Processing settlement for orderId={}", event.orderId());

        if (repository.existsByOrderId(event.orderId())) {
            log.warn("Settlement already exists for orderId={}", event.orderId());
            return;
        }

        BigDecimal fees = event.totalAmount()
                .multiply(BigDecimal.valueOf(0.001))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal netAmount = event.totalAmount().subtract(fees);

        Settlement settlement = Settlement.builder()
                .orderId(event.orderId())
                .customerId(event.customerId())
                .assetCode(event.assetCode())
                .grossAmount(event.totalAmount())
                .fees(fees)
                .netAmount(netAmount)
                .settlementDate(LocalDate.now().plusDays(2))
                .status(SettlementStatus.COMPLETED)
                .build();

        repository.save(settlement);

        log.info(
                "Settlement completed for orderId={}, netAmount={}, fees={}",
                event.orderId(),
                netAmount,
                fees
        );
    }
}