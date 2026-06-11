package com.thais.investment.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange

                        .pathMatchers("/auth/**").permitAll()

                        .pathMatchers("/actuator/**")
                        .hasAnyRole("PLATFORM_ENGINEER", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/orders/**")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        .pathMatchers(HttpMethod.GET, "/orders/**")
                        .hasAnyRole("CUSTOMER", "ADMIN", "PLATFORM_ENGINEER")

                        .pathMatchers(HttpMethod.GET, "/settlements/**")
                        .hasAnyRole("CUSTOMER", "ADMIN", "PLATFORM_ENGINEER")

                        .pathMatchers(HttpMethod.GET, "/statements/**")
                        .hasAnyRole("CUSTOMER", "ADMIN", "PLATFORM_ENGINEER")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(this::jwtAuthenticationConverter))
                )
                .build();
    }

    private Mono<JwtAuthenticationToken> jwtAuthenticationConverter(Jwt jwt) {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("roles");
        converter.setAuthorityPrefix("");

        return Mono.just(new JwtAuthenticationToken(jwt, converter.convert(jwt)));
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        SecretKeySpec secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        return NimbusReactiveJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public MapReactiveUserDetailsService users(PasswordEncoder passwordEncoder) {
        UserDetails customer = User.withUsername("customer")
                .password(passwordEncoder.encode("customer123"))
                .roles("CUSTOMER")
                .build();

        UserDetails platform = User.withUsername("platform")
                .password(passwordEncoder.encode("platform123"))
                .roles("PLATFORM_ENGINEER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new MapReactiveUserDetailsService(customer, platform, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}