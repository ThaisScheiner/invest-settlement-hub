package com.thais.investment.gatewayservice.auth;

public record LoginRequest(
        String username,
        String password
) {
}