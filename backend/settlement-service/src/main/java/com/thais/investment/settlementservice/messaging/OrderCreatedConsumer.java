package com.thais.investment.settlementservice.messaging;

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
                log.info("Received order event from SQS: messageId={}", message.messageId());

                OrderCreatedEvent event = objectMapper.readValue(
                        message.body(),
                        OrderCreatedEvent.class
                );

                settlementService.process(event);

                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build());

                log.info("Message deleted from SQS: messageId={}", message.messageId());
            }
        } catch (Exception exception) {
            log.error("Error consuming SQS messages", exception);
        }
    }
}