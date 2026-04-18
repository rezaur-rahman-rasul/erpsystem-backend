package com.hishabnikash.erp.organization.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI enterpriseStructureOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise Structure Service API")
                        .version("v1")
                        .description("Production-oriented ERP enterprise structure service starter.")
                        .contact(new Contact().name("ERP Platform Team")));
    }
}
