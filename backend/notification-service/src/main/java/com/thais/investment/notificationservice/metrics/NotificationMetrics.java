package com.thais.investment.notificationservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class NotificationMetrics {

    private final Counter notificationReceivedCounter;
    private final Counter notificationSentCounter;
    private final Counter notificationErrorCounter;

    public NotificationMetrics(MeterRegistry meterRegistry) {
        this.notificationReceivedCounter = Counter.builder("notification_received_total")
                .description("Total number of notification events received")
                .register(meterRegistry);

        this.notificationSentCounter = Counter.builder("notification_sent_total")
                .description("Total number of fake notifications sent")
                .register(meterRegistry);

        this.notificationErrorCounter = Counter.builder("notification_error_total")
                .description("Total number of notification processing errors")
                .register(meterRegistry);
    }

    public void incrementNotificationReceived() {
        notificationReceivedCounter.increment();
    }

    public void incrementNotificationSent() {
        notificationSentCounter.increment();
    }

    public void incrementNotificationError() {
        notificationErrorCounter.increment();
    }
}