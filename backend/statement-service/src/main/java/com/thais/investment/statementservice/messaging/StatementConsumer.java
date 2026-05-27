package com.thais.investment.statementservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thais.investment.statementservice.statement.StatementService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class StatementConsumer {

    private static final Logger log = LoggerFactory.getLogger(StatementConsumer.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final StatementService statementService;

    @Value("${aws.sqs.statement-queue-url}")
    private String statementQueueUrl;

    private ScheduledExecutorService scheduler;

    public StatementConsumer(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            StatementService statementService
    ) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.statementService = statementService;
    }

    @PostConstruct
    public void startConsumer() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleWithFixedDelay(
                this::consumeMessages,
                3,
                5,
                TimeUnit.SECONDS
        );

        log.info("Statement consumer started. queueUrl={}", statementQueueUrl);
    }

    private void consumeMessages() {
        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(statementQueueUrl)
                    .maxNumberOfMessages(5)
                    .waitTimeSeconds(2)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(request).messages();

            for (Message message : messages) {
                processMessage(message);
            }

        } catch (Exception exception) {
            log.error("Error receiving statement messages from SQS", exception);
        }
    }

    private void processMessage(Message sqsMessage) {
        try {
            log.info("Received statement event from SQS: messageId={}", sqsMessage.messageId());

            SettlementCompletedEvent event = objectMapper.readValue(
                    sqsMessage.body(),
                    SettlementCompletedEvent.class
            );

            statementService.generateFromSettlement(event);

            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(statementQueueUrl)
                    .receiptHandle(sqsMessage.receiptHandle())
                    .build();

            sqsClient.deleteMessage(deleteRequest);

            log.info("Statement message processed and deleted from SQS: messageId={}", sqsMessage.messageId());

        } catch (Exception exception) {
            log.error("Error processing statement message. Message will remain in queue.", exception);
        }
    }

    @PreDestroy
    public void stopConsumer() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}