package com.acme.employeehub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeHubOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Employee Hub API")
                .description("REST API for employee records from the SQLite employees database")
                .version("1.0.0"));
    }
}
