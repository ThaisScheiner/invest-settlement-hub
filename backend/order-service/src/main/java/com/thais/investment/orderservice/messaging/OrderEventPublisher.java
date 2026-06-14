package com.thais.investment.orderservice.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thais.investment.orderservice.metrics.OrderMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final OrderMetrics orderMetrics;
    private final String queueUrl;

    public OrderEventPublisher(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            OrderMetrics orderMetrics,
            @Value("${aws.sqs.order-created-queue-url}") String queueUrl
    ) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.orderMetrics = orderMetrics;
        this.queueUrl = queueUrl;
    }

    public void publish(OrderCreatedEvent event) {
        try {
            log.info(
                    "Publishing order event to SQS: orderId={}, queueUrl={}",
                    event.orderId(),
                    queueUrl
            );

            String messageBody = objectMapper.writeValueAsString(event);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(request);

            orderMetrics.incrementOrderEventPublished();

            log.info(
                    "Order event published successfully: orderId={}",
                    event.orderId()
            );

        } catch (JsonProcessingException exception) {
            log.error(
                    "Error serializing order event: orderId={}",
                    event.orderId(),
                    exception
            );

            throw new RuntimeException("Error serializing order event", exception);

        } catch (Exception exception) {
            log.error(
                    "Error publishing order event to SQS: orderId={}",
                    event.orderId(),
                    exception
            );

            throw new RuntimeException("Error publishing order event to SQS", exception);
        }
    }
}