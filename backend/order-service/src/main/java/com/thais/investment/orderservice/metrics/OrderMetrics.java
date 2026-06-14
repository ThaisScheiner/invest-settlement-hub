package com.thais.investment.orderservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderMetrics {

    private final Counter orderCreatedCounter;
    private final Counter orderCreationErrorCounter;
    private final Counter orderEventPublishedCounter;

    public OrderMetrics(MeterRegistry meterRegistry) {
        this.orderCreatedCounter = Counter.builder("order_created_total")
                .description("Total number of orders created")
                .register(meterRegistry);

        this.orderCreationErrorCounter = Counter.builder("order_creation_error_total")
                .description("Total number of order creation errors")
                .register(meterRegistry);

        this.orderEventPublishedCounter = Counter.builder("order_event_published_total")
                .description("Total number of order events published to SQS")
                .register(meterRegistry);
    }

    public void incrementOrderCreated() {
        orderCreatedCounter.increment();
    }

    public void incrementOrderCreationError() {
        orderCreationErrorCounter.increment();
    }

    public void incrementOrderEventPublished() {
        orderEventPublishedCounter.increment();
    }
}