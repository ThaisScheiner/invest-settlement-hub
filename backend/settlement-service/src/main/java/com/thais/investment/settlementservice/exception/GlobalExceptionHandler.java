package com.thais.investment.settlementservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SettlementNotFoundException.class)
    public ResponseEntity<Void> handleSettlementNotFound(SettlementNotFoundException exception) {
        log.warn("Settlement not found: {}", exception.getMessage());
        return ResponseEntity.notFound().build();
    }
}