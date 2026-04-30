package com.thais.investment.settlementservice.settlement;

import com.thais.investment.settlementservice.exception.SettlementNotFoundException;
import com.thais.investment.settlementservice.messaging.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

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
            log.warn("Settlement already exists for orderId={}. Skipping duplicated event.", event.orderId());
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

        try {
            repository.save(settlement);
            log.info(
                    "Settlement completed for orderId={}, netAmount={}, fees={}",
                    event.orderId(),
                    netAmount,
                    fees
            );

        } catch (Exception exception) {
            log.error("Error saving settlement for orderId={}", event.orderId(), exception);
            throw exception;
        }

    }

    public SettlementResponse findById(String id) {
        Settlement settlement = repository.findById(id)
                .orElseThrow(() -> new SettlementNotFoundException("Settlement id not found: " + id));

        return SettlementResponse.fromEntity(settlement);
    }

    public SettlementResponse findByOrderId(String orderId) {
        Settlement settlement = repository.findByOrderId(orderId)
                .orElseThrow(() -> new SettlementNotFoundException("Settlement orderId not found: " + orderId));

        return SettlementResponse.fromEntity(settlement);
    }

    public List<SettlementResponse> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .map(SettlementResponse::fromEntity)
                .toList();
    }
}