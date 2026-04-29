package com.thais.investment.orderservice.order;

import com.thais.investment.orderservice.exception.OrderNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderService service;

    @Test
    void shouldCreateOrderSuccessfully() {
        OrderRequest request = new OrderRequest();
        request.setCustomerId("customer-001");
        request.setAssetCode("itub4");
        request.setOperationType(OperationType.BUY);
        request.setQuantity(100);
        request.setUnitPrice(BigDecimal.valueOf(32.50));

        Order savedOrder = Order.builder()
                .id("order-001")
                .customerId("customer-001")
                .assetCode("ITUB4")
                .operationType(OperationType.BUY)
                .quantity(100)
                .unitPrice(BigDecimal.valueOf(32.50))
                .totalAmount(BigDecimal.valueOf(3250.00))
                .status(OrderStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();

        when(repository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponse response = service.create(request);

        assertEquals("order-001", response.getId());
        assertEquals("customer-001", response.getCustomerId());
        assertEquals("ITUB4", response.getAssetCode());
        assertEquals(OperationType.BUY, response.getOperationType());
        assertEquals(100, response.getQuantity());
        assertEquals(BigDecimal.valueOf(3250.00), response.getTotalAmount());
        assertEquals(OrderStatus.CREATED, response.getStatus());

        verify(repository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldFindOrderByIdSuccessfully() {
        Order order = Order.builder()
                .id("order-001")
                .customerId("customer-001")
                .assetCode("ITUB4")
                .operationType(OperationType.BUY)
                .quantity(100)
                .unitPrice(BigDecimal.valueOf(32.50))
                .totalAmount(BigDecimal.valueOf(3250.00))
                .status(OrderStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();

        when(repository.findById("order-001")).thenReturn(Optional.of(order));

        OrderResponse response = service.findById("order-001");

        assertEquals("order-001", response.getId());
        assertEquals("ITUB4", response.getAssetCode());
        assertEquals(OrderStatus.CREATED, response.getStatus());

        verify(repository, times(1)).findById("order-001");
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        when(repository.findById("not-found")).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.findById("not-found"));

        verify(repository, times(1)).findById("not-found");
    }

    @Test
    void shouldFindOrdersByCustomerId() {
        Order order1 = Order.builder()
                .id("order-001")
                .customerId("customer-001")
                .assetCode("ITUB4")
                .operationType(OperationType.BUY)
                .quantity(100)
                .unitPrice(BigDecimal.valueOf(32.50))
                .totalAmount(BigDecimal.valueOf(3250.00))
                .status(OrderStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();

        Order order2 = Order.builder()
                .id("order-002")
                .customerId("customer-001")
                .assetCode("PETR4")
                .operationType(OperationType.SELL)
                .quantity(50)
                .unitPrice(BigDecimal.valueOf(38.00))
                .totalAmount(BigDecimal.valueOf(1900.00))
                .status(OrderStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();

        when(repository.findByCustomerId("customer-001"))
                .thenReturn(List.of(order1, order2));

        List<OrderResponse> response = service.findByCustomerId("customer-001");

        assertEquals(2, response.size());
        assertEquals("ITUB4", response.get(0).getAssetCode());
        assertEquals("PETR4", response.get(1).getAssetCode());

        verify(repository, times(1)).findByCustomerId("customer-001");
    }
}