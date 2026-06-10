package com.thais.investment.statementservice.statement;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @GetMapping("/{id}/download")
    public ResponseEntity<String> downloadStatement(@PathVariable String id) {
        String content = statementService.downloadStatement(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=statement-" + id + ".json"
                )
                .body(content);
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