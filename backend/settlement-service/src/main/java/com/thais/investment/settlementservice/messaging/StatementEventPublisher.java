package com.thais.investment.settlementservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thais.investment.settlementservice.metrics.SettlementMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class StatementEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(StatementEventPublisher.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final SettlementMetrics settlementMetrics;

    @Value("${aws.sqs.statement-queue-url}")
    private String statementQueueUrl;

    public StatementEventPublisher(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            SettlementMetrics settlementMetrics
    ) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.settlementMetrics = settlementMetrics;
    }

    public void publish(NotificationEvent event) {
        try {
            String body = objectMapper.writeValueAsString(event);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(statementQueueUrl)
                    .messageBody(body)
                    .build();

            sqsClient.sendMessage(request);

            settlementMetrics.incrementStatementEventPublished();

            log.info(
                    "Statement event published successfully: orderId={}, customerId={}, queueUrl={}",
                    event.orderId(),
                    event.customerId(),
                    statementQueueUrl
            );

        } catch (Exception exception) {
            log.error("Error publishing statement event for orderId={}", event.orderId(), exception);
            throw new RuntimeException("Error publishing statement event", exception);
        }
    }
}