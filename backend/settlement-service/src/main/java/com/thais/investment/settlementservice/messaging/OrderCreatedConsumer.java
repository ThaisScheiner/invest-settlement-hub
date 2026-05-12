package com.thais.investment.settlementservice.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thais.investment.settlementservice.settlement.SettlementService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class OrderCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    private static final int MAX_RETRY_ATTEMPTS = 3;

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final SettlementService settlementService;
    private final String queueUrl;

    public OrderCreatedConsumer(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            SettlementService settlementService,
            @Value("${aws.sqs.order-created-queue-url}") String queueUrl
    ) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.settlementService = settlementService;
        this.queueUrl = queueUrl;
    }

    @PostConstruct
    public void startConsumer() {
        log.info("Starting SQS consumer for queueUrl={}", queueUrl);

        Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(this::consumeMessages, 5, 5, TimeUnit.SECONDS);
    }

    private void consumeMessages() {
        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(5)
                    .waitTimeSeconds(5)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(request).messages();

            for (Message message : messages) {
                processMessage(message);
            }

        } catch (Exception exception) {
            log.error("Error receiving messages from SQS", exception);
        }
    }

    private void processMessage(Message message) {
        log.info("Received order event from SQS: messageId={}", message.messageId());

        try {
            OrderCreatedEvent event = objectMapper.readValue(
                    message.body(),
                    OrderCreatedEvent.class
            );

            log.info(
                    "Processing message: correlationId={}, orderId={}",
                    event.correlationId(),
                    event.orderId()
            );

            processWithRetry(event);

            deleteMessage(message);

            log.info("Message processed and deleted from SQS: messageId={}", message.messageId());

        } catch (JsonProcessingException exception) {
            log.error(
                    "Invalid JSON message. Deleting messageId={} to avoid infinite retry. Body={}",
                    message.messageId(),
                    message.body(),
                    exception
            );

            deleteMessage(message);

        } catch (Exception exception) {
            log.error(
                    "Message processing failed after retries. Keeping message for SQS redrive policy. messageId={}",
                    message.messageId(),
                    exception
            );
        }
    }

    private void processWithRetry(OrderCreatedEvent event) {
        RuntimeException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                log.info(
                        "Processing settlement attempt {}/{} for orderId={}",
                        attempt,
                        MAX_RETRY_ATTEMPTS,
                        event.orderId()
                );

                settlementService.process(event);

                return;

            } catch (RuntimeException exception) {
                lastException = exception;

                log.warn(
                        "Error processing settlement attempt {}/{} for orderId={}",
                        attempt,
                        MAX_RETRY_ATTEMPTS,
                        event.orderId(),
                        exception
                );

                if (attempt < MAX_RETRY_ATTEMPTS) {
                    sleepBeforeNextAttempt(attempt);
                }
            }
        }

        throw lastException;
    }

    private void sleepBeforeNextAttempt(int attempt) {
        try {
            long delaySeconds = attempt;

            log.info("Waiting {} seconds before next retry", delaySeconds);

            TimeUnit.SECONDS.sleep(delaySeconds);

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", exception);
        }
    }

    private void deleteMessage(Message message) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build());
    }
}