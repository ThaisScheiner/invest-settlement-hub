package com.thais.investment.orderservice.order;

import com.thais.investment.orderservice.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public OrderResponse create(OrderRequest request) {
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

        return OrderResponse.fromEntity(savedOrder);
    }

    public OrderResponse findById(String id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return OrderResponse.fromEntity(order);
    }

    public List<OrderResponse> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }
}