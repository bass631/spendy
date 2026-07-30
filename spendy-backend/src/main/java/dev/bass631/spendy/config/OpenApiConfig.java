package dev.bass631.spendy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI spendyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spendy API")
                        .description("REST API for expense tracking application")
                        .version("1.0.0"));
    }
}
