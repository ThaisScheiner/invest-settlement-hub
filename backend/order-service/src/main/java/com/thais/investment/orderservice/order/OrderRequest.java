package com.thais.investment.orderservice.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {

    @NotBlank(message = "customerId is required")
    private String customerId;

    @NotBlank(message = "assetCode is required")
    private String assetCode;

    @NotNull(message = "operationType is required")
    private OperationType operationType;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "unitPrice is required")
    @DecimalMin(value = "0.01", message = "unitPrice must be greater than zero")
    private BigDecimal unitPrice;

}