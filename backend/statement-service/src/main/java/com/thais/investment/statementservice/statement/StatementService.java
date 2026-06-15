package com.thais.investment.statementservice.statement;

import com.thais.investment.statementservice.messaging.SettlementCompletedEvent;
import com.thais.investment.statementservice.metrics.StatementMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatementService {

    private static final Logger log = LoggerFactory.getLogger(StatementService.class);

    private final StatementRepository repository;
    private final StatementStorageService storageService;
    private final StatementMetrics statementMetrics;

    public StatementService(
            StatementRepository repository,
            StatementStorageService storageService,
            StatementMetrics statementMetrics
    ) {
        this.repository = repository;
        this.storageService = storageService;
        this.statementMetrics = statementMetrics;
    }

    public void generateFromSettlement(SettlementCompletedEvent event) {
        try {
            log.info(
                    "Generating statement for settlementId={}, orderId={}",
                    event.settlementId(),
                    event.orderId()
            );

            if (repository.existsBySettlementId(event.settlementId())) {
                statementMetrics.incrementDuplicateSkipped();

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

            statementMetrics.incrementGenerated();
            statementMetrics.incrementSuccess();

            log.info(
                    "Statement generated, uploaded to S3 and stored successfully: statementId={}, settlementId={}, customerId={}, documentKey={}",
                    statement.getId(),
                    statement.getSettlementId(),
                    statement.getCustomerId(),
                    statement.getDocumentKey()
            );

        } catch (Exception exception) {
            statementMetrics.incrementError();

            log.error(
                    "Error generating statement for settlementId={}, orderId={}",
                    event.settlementId(),
                    event.orderId(),
                    exception
            );

            throw exception;
        }
    }

    public StatementResponse findById(String id) {
        Statement statement = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Statement not found: " + id));

        return StatementResponse.fromEntity(statement);
    }

    public StatementResponse findBySettlementId(String settlementId) {
        Statement statement = repository.findBySettlementId(settlementId)
                .orElseThrow(() -> new RuntimeException("Statement not found for settlementId: " + settlementId));

        return StatementResponse.fromEntity(statement);
    }

    public List<StatementResponse> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .map(StatementResponse::fromEntity)
                .toList();
    }

    public List<StatementResponse> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId)
                .stream()
                .map(StatementResponse::fromEntity)
                .toList();
    }

    public String downloadStatement(String id) {
        Statement statement = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Statement not found: " + id));

        return storageService.downloadStatement(statement.getDocumentKey());
    }

    private String generateDocumentKey(Statement statement) {
        return "statements/"
                + statement.getCustomerId()
                + "/"
                + statement.getSettlementId()
                + "-settlement-receipt.json";
    }
}