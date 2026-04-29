package com.thais.investment.orderservice.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderRepository repository;

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
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

        String body = """
                {
                  "customerId": "customer-001",
                  "assetCode": "itub4",
                  "operationType": "BUY",
                  "quantity": 100,
                  "unitPrice": 32.50
                }
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("order-001"))
                .andExpect(jsonPath("$.customerId").value("customer-001"))
                .andExpect(jsonPath("$.assetCode").value("ITUB4"))
                .andExpect(jsonPath("$.totalAmount").value(3250.00))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        String body = """
                {
                  "customerId": "",
                  "assetCode": "ITUB4",
                  "operationType": "BUY",
                  "quantity": 0,
                  "unitPrice": 0
                }
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindOrderByIdSuccessfully() throws Exception {
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

        mockMvc.perform(get("/orders/order-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("order-001"))
                .andExpect(jsonPath("$.assetCode").value("ITUB4"));
    }

    @Test
    void shouldReturn404WhenOrderDoesNotExist() throws Exception {
        when(repository.findById("not-found")).thenReturn(Optional.empty());

        mockMvc.perform(get("/orders/not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindOrdersByCustomerId() throws Exception {
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

        when(repository.findByCustomerId("customer-001")).thenReturn(List.of(order));

        mockMvc.perform(get("/orders/customer/customer-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("order-001"))
                .andExpect(jsonPath("$[0].assetCode").value("ITUB4"));
    }
}