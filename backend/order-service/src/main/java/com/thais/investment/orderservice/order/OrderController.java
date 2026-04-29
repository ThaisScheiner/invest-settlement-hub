package com.thais.investment.orderservice.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @Operation(
            summary = "Cria uma nova ordem de investimento",
            description = "Recebe os dados de uma ordem, calcula o valor total e registra a ordem com status CREATED."
    )
    @ApiResponse(responseCode = "201", description = "Ordem criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Requisicao invalida")
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = service.create(request);

        return ResponseEntity
                .created(URI.create("/orders/" + response.getId()))
                .body(response);
    }

    @Operation(
            summary = "Busca uma ordem por ID",
            description = "Retorna os dados de uma ordem a partir do identificador."
    )
    @ApiResponse(responseCode = "200", description = "Ordem encontrada")
    @ApiResponse(responseCode = "404", description = "Ordem nao encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Lista ordens por cliente",
            description = "Retorna todas as ordens vinculadas a um cliente."
    )
    @ApiResponse(responseCode = "200", description = "Lista de ordens retornada com sucesso")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> findByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(service.findByCustomerId(customerId));
    }
}