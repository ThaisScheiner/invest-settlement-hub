package com.thais.investment.orderservice.order;

import com.thais.investment.orderservice.exception.OrderNotFoundException;
import com.thais.investment.orderservice.messaging.OrderCreatedEvent;
import com.thais.investment.orderservice.messaging.OrderEventPublisher;
import com.thais.investment.orderservice.metrics.OrderMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository repository;
    private final OrderEventPublisher publisher;
    private final OrderMetrics orderMetrics;

    public OrderService(
            OrderRepository repository,
            OrderEventPublisher publisher,
            OrderMetrics orderMetrics
    ) {
        this.repository = repository;
        this.publisher = publisher;
        this.orderMetrics = orderMetrics;
    }

    public OrderResponse create(OrderRequest request) {
        try {
            log.info(
                    "Creating order: customerId={}, assetCode={}, operationType={}, quantity={}, unitPrice={}",
                    request.getCustomerId(),
                    request.getAssetCode(),
                    request.getOperationType(),
                    request.getQuantity(),
                    request.getUnitPrice()
            );

            BigDecimal totalAmount = request.getUnitPrice()
                    .multiply(BigDecimal.valueOf(request.getQuantity()));

            Order order = Order.builder()
                    .customerId(request.getCustomerId())
                    .assetCode(request.getAssetCode().toUpperCase())
                    .operationType(request.getOperationType())
                    .quantity(request.getQuantity())
                    .unitPrice(request.getUnitPrice())
                    .totalAmount(totalAmount)
                    .status(OrderStatus.CREATED)
                    .build();

            Order savedOrder = repository.save(order);

            orderMetrics.incrementOrderCreated();

            log.info(
                    "Order created successfully: orderId={}, customerId={}, totalAmount={}",
                    savedOrder.getId(),
                    savedOrder.getCustomerId(),
                    savedOrder.getTotalAmount()
            );

            String correlationId = UUID.randomUUID().toString();

            OrderCreatedEvent event = new OrderCreatedEvent(
                    correlationId,
                    savedOrder.getId(),
                    savedOrder.getCustomerId(),
                    savedOrder.getAssetCode(),
                    savedOrder.getOperationType().name(),
                    savedOrder.getQuantity(),
                    savedOrder.getUnitPrice(),
                    savedOrder.getTotalAmount(),
                    savedOrder.getCreatedAt()
            );

            log.info(
                    "Publishing order event: correlationId={}, orderId={}",
                    correlationId,
                    savedOrder.getId()
            );

            publisher.publish(event);

            return OrderResponse.fromEntity(savedOrder);

        } catch (Exception exception) {
            orderMetrics.incrementOrderCreationError();

            log.error(
                    "Error creating order: customerId={}, assetCode={}",
                    request.getCustomerId(),
                    request.getAssetCode(),
                    exception
            );

            throw exception;
        }
    }

    public OrderResponse findById(String id) {
        log.info("Searching order by id: {}", id);

        Order order = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found: id={}", id);
                    return new OrderNotFoundException(id);
                });

        return OrderResponse.fromEntity(order);
    }

    public List<OrderResponse> findByCustomerId(String customerId) {
        log.info("Searching orders by customerId: {}", customerId);

        return repository.findByCustomerId(customerId)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }
}