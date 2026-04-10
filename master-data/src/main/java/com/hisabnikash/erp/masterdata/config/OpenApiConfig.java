package com.hisabnikash.erp.masterdata.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI masterDataOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Master Data Service API")
                        .version("v1")
                        .description("Phase 1 master data service for currencies, units, and payment terms.")
                        .contact(new Contact().name("ERP Platform Team")));
    }
}
