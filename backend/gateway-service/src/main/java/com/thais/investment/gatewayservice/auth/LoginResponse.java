package com.thais.investment.gatewayservice.auth;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String tokenType,
        List<String> roles
) {
}