package com.thais.investment.orderservice.order;

import com.thais.investment.orderservice.exception.OrderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public OrderResponse create(OrderRequest request) {
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

        log.info(
                "Order created successfully: orderId={}, customerId={}, totalAmount={}",
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getTotalAmount()
        );

        return OrderResponse.fromEntity(savedOrder);
    }

    public OrderResponse findById(String id) {
        log.info("Searching order by id: {}", id);

        Order order = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found: id={}", id);
                    return new OrderNotFoundException(id);
                });

        log.info("Order found: id={}, status={}", order.getId(), order.getStatus());

        return OrderResponse.fromEntity(order);
    }

    public List<OrderResponse> findByCustomerId(String customerId) {
        log.info("Searching orders by customerId: {}", customerId);

        List<OrderResponse> orders = repository.findByCustomerId(customerId)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();

        log.info("Orders found for customerId={}: count={}", customerId, orders.size());

        return orders;
    }
}