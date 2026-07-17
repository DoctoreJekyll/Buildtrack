package com.jose.buildtrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuración general de la documentación OpenAPI.
 *
 * - nombre de la API
 * - versión
 * - descripción
 * - autor/contacto
 * - licencia
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI buildTrackOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BuildTrack API")
                        .version("1.0.0")
                        .description("""
                                REST API for managing software builds, validation issues
                                and release readiness workflows.
                                """)
                        .contact(new Contact()
                                .name("José Antonio Rodríguez")
                                .url("https://github.com/DoctoreJekyll/Buildtrack"))
                        .license(new License()
                                .name("MIT License")));
    }
}