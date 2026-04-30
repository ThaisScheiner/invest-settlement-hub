package com.thais.investment.settlementservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI settlementServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Settlement Service API")
                        .description("Microservico responsavel pelo processamento e consulta de liquidacoes de investimentos.")
                        .version("1.0.0"));
    }
}