package com.thais.investment.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange

                        // Autenticação
                        .pathMatchers("/auth/**").permitAll()

                        // Actuator/Prometheus: apenas plataforma/admin
                        .pathMatchers("/actuator/**").hasAnyRole("PLATFORM_ENGINEER", "ADMIN")

                        // Orders
                        .pathMatchers(HttpMethod.POST, "/orders/**")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        .pathMatchers(HttpMethod.GET, "/orders/**")
                        .hasAnyRole("CUSTOMER", "ADMIN", "PLATFORM_ENGINEER")

                        // Settlements
                        .pathMatchers(HttpMethod.GET, "/settlements/**")
                        .hasAnyRole("CUSTOMER", "ADMIN", "PLATFORM_ENGINEER")

                        // Statements
                        .pathMatchers(HttpMethod.GET, "/statements/**")
                        .hasAnyRole("CUSTOMER", "ADMIN", "PLATFORM_ENGINEER")

                        // Qualquer outra rota exige autenticação
                        .anyExchange().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public MapReactiveUserDetailsService users() {

        UserDetails customer = User.withDefaultPasswordEncoder()
                .username("customer")
                .password("customer123")
                .roles("CUSTOMER")
                .build();

        UserDetails platform = User.withDefaultPasswordEncoder()
                .username("platform")
                .password("platform123")
                .roles("PLATFORM_ENGINEER")
                .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN")
                .build();

        return new MapReactiveUserDetailsService(customer, platform, admin);
    }
}