package com.thais.investment.settlementservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class SettlementMetrics {

    private final Counter settlementProcessedCounter;
    private final Counter settlementProcessingErrorCounter;
    private final Counter notificationEventPublishedCounter;
    private final Counter statementEventPublishedCounter;

    public SettlementMetrics(MeterRegistry meterRegistry) {
        this.settlementProcessedCounter = Counter.builder("settlement_processed_total")
                .description("Total number of settlements processed successfully")
                .register(meterRegistry);

        this.settlementProcessingErrorCounter = Counter.builder("settlement_processing_error_total")
                .description("Total number of settlement processing errors")
                .register(meterRegistry);

        this.notificationEventPublishedCounter = Counter.builder("settlement_notification_event_published_total")
                .description("Total number of notification events published after settlement")
                .register(meterRegistry);

        this.statementEventPublishedCounter = Counter.builder("settlement_statement_event_published_total")
                .description("Total number of statement events published after settlement")
                .register(meterRegistry);
    }

    public void incrementSettlementProcessed() {
        settlementProcessedCounter.increment();
    }

    public void incrementSettlementProcessingError() {
        settlementProcessingErrorCounter.increment();
    }

    public void incrementNotificationEventPublished() {
        notificationEventPublishedCounter.increment();
    }

    public void incrementStatementEventPublished() {
        statementEventPublishedCounter.increment();
    }
}