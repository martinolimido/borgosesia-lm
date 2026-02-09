package it.borgosesiaspa.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // Definisci il componente di sicurezza (Bearer Token)
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Aggiungi la configurazione di sicurezza agli endpoint
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("BearerAuth");
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("BearerAuth", securityScheme))
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("Borgosesia Lease Management API")
                        .version("1.0")
                        .description("API Borgosesia Lease Management per la gestione dei contratti di affitto"));
    }
}
