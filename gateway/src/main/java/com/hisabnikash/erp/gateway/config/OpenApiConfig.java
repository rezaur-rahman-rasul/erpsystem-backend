package com.hisabnikash.erp.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gatewayOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ERP Gateway Service API")
                        .version("v1")
                        .description("Phase 1 API gateway routing identity, organization, and master-data services.")
                        .contact(new Contact().name("ERP Platform Team")));
    }
}
