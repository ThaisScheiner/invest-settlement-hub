package com.thais.investment.gatewayservice.auth;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final String jwtSecret;

    public AuthController(@Value("${jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        UserAuthData userAuthData = authenticate(request);

        SecretKeySpec secretKey = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        NimbusJwtEncoder encoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(secretKey)
        );

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("invest-settlement-hub")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(userAuthData.username())
                .claim("roles", userAuthData.roles())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = encoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new LoginResponse(
                token,
                "Bearer",
                userAuthData.roles()
        );
    }

    private UserAuthData authenticate(LoginRequest request) {

        if ("customer".equals(request.username()) && "customer123".equals(request.password())) {
            return new UserAuthData(
                    "customer",
                    List.of("ROLE_CUSTOMER")
            );
        }

        if ("platform".equals(request.username()) && "platform123".equals(request.password())) {
            return new UserAuthData(
                    "platform",
                    List.of("ROLE_PLATFORM_ENGINEER")
            );
        }

        if ("admin".equals(request.username()) && "admin123".equals(request.password())) {
            return new UserAuthData(
                    "admin",
                    List.of("ROLE_ADMIN")
            );
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid credentials"
        );
    }

    private record UserAuthData(
            String username,
            List<String> roles
    ) {
    }
}