package com.thais.investment.notificationservice.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.notification-queue-url}")
    private String notificationQueueUrl;

    private ScheduledExecutorService scheduler;

    public NotificationConsumer(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
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

        log.info("Notification consumer started. queueUrl={}", notificationQueueUrl);
    }

    private void consumeMessages() {
        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(notificationQueueUrl)
                    .maxNumberOfMessages(5)
                    .waitTimeSeconds(2)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(request).messages();

            for (Message message : messages) {
                processMessage(message);
            }

        } catch (Exception exception) {
            log.error("Error receiving notification messages from SQS", exception);
        }
    }

    private void processMessage(Message sqsMessage) {
        try {
            log.info("Received notification event from SQS: messageId={}", sqsMessage.messageId());

            NotificationEvent event = objectMapper.readValue(
                    sqsMessage.body(),
                    NotificationEvent.class
            );

            log.info(
                    "Sending fake notification: customerId={}, orderId={}, settlementId={}, assetCode={}, netAmount={}, status={}, message={}",
                    event.customerId(),
                    event.orderId(),
                    event.settlementId(),
                    event.assetCode(),
                    event.netAmount(),
                    event.status(),
                    event.message()
            );

            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(notificationQueueUrl)
                    .receiptHandle(sqsMessage.receiptHandle())
                    .build();

            sqsClient.deleteMessage(deleteRequest);

            log.info("Notification message processed and deleted from SQS: messageId={}", sqsMessage.messageId());

        } catch (Exception exception) {
            log.error("Error processing notification message. Message will remain in queue.", exception);
        }
    }

    @PreDestroy
    public void stopConsumer() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}