package com.example.coreapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personal Finance Tracker - Core API")
                        .version("1.0.0")
                        .termsOfService("#")
                        .license(new License().name("#").url("#"))
                        .description("Core API: Auth, Accounts, Transactions (money movement)."));
    }

}