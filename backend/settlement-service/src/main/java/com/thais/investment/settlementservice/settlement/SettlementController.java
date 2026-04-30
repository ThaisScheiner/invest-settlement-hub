package com.thais.investment.settlementservice.settlement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/settlements")
public class SettlementController {

    private final SettlementService service;

    public SettlementController(SettlementService service) {
        this.service = service;
    }

    @Operation(
            summary = "Busca uma liquidação por ID",
            description = "Retorna os dados de uma liquidação a partir do identificador da liquidação."
    )
    @ApiResponse(responseCode = "200", description = "Liquidação encontrada")
    @ApiResponse(responseCode = "404", description = "Liquidação não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<SettlementResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Busca uma liquidacao pelo ID da ordem",
            description = "Retorna a liquidacao gerada a partir de uma ordem de investimento."
    )
    @ApiResponse(responseCode = "200", description = "Liquidacao encontrada")
    @ApiResponse(responseCode = "404", description = "Liquidacao nao encontrada")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<SettlementResponse> findByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(service.findByOrderId(orderId));
    }

    @Operation(
            summary = "Lista liquidacoes por cliente",
            description = "Retorna todas as liquidacoes vinculadas a um cliente."
    )
    @ApiResponse(responseCode = "200", description = "Lista de liquidacoes retornada com sucesso")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SettlementResponse>> findByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(service.findByCustomerId(customerId));
    }
}