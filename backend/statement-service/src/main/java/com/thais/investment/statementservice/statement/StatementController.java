package com.thais.investment.statementservice.statement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statements")
public class StatementController {

    private final StatementService statementService;

    public StatementController(StatementService statementService) {
        this.statementService = statementService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatementResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(statementService.findById(id));
    }

    @GetMapping("/settlement/{settlementId}")
    public ResponseEntity<StatementResponse> findBySettlementId(@PathVariable String settlementId) {
        return ResponseEntity.ok(statementService.findBySettlementId(settlementId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<StatementResponse>> findByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(statementService.findByCustomerId(customerId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<StatementResponse>> findByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(statementService.findByOrderId(orderId));
    }
}