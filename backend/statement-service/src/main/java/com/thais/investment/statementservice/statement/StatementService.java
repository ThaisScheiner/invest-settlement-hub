package com.thais.investment.statementservice.statement;

import com.thais.investment.statementservice.messaging.SettlementCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StatementService {

    private static final Logger log = LoggerFactory.getLogger(StatementService.class);

    private final StatementRepository repository;
    private final StatementStorageService storageService;

    public StatementService(
            StatementRepository repository,
            StatementStorageService storageService
    ) {
        this.repository = repository;
        this.storageService = storageService;
    }

    public void generateFromSettlement(SettlementCompletedEvent event) {
        log.info(
                "Generating statement for settlementId={}, orderId={}",
                event.settlementId(),
                event.orderId()
        );

        if (repository.existsBySettlementId(event.settlementId())) {
            log.warn(
                    "Statement already exists for settlementId={}. Skipping duplicated event.",
                    event.settlementId()
            );
            return;
        }

        Statement statement = Statement.create(
                event.settlementId(),
                event.orderId(),
                event.customerId(),
                event.assetCode(),
                event.netAmount(),
                LocalDate.parse(event.settlementDate()),
                StatementType.SETTLEMENT_RECEIPT
        );

        String documentKey = generateDocumentKey(statement);

        statement.setDocumentKey(documentKey);
        statement.setStatus(StatementStatus.GENERATED);

        storageService.uploadStatement(statement);

        statement.setStatus(StatementStatus.STORED);

        repository.save(statement);

        log.info(
                "Statement generated, uploaded to S3 and stored successfully: statementId={}, settlementId={}, customerId={}, documentKey={}",
                statement.getId(),
                statement.getSettlementId(),
                statement.getCustomerId(),
                statement.getDocumentKey()
        );
    }

    private String generateDocumentKey(Statement statement) {
        return "statements/"
                + statement.getCustomerId()
                + "/"
                + statement.getSettlementId()
                + "-settlement-receipt.json";
    }
}