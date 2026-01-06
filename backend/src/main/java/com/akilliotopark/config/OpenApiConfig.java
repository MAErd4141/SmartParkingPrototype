package com.akilliotopark.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi mobileApi() {
        return GroupedOpenApi.builder()
                .group("1-mobil-app-api")
                .pathsToMatch(
                        // Auth
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/logout",

                        // App Init & Profile
                        "/api/app/init",
                        "/api/users/me",

                        // Vehicles
                        "/api/vehicles",
                        "/api/vehicles/my",
                        "/api/vehicles/{id}",

                        // Parking lots
                        "/api/parking-lots",
                        "/api/parking-lots/{id}",
                        "/api/parking-lots/{id}/spots",

                        // Reservations
                        "/api/reservations",
                        "/api/reservations/my",
                        "/api/reservations/unavailable",
                        "/api/reservations/{id}/cancel",

                        // Entry
                        "/api/entry/verify",

                        // IoT ingest (new)
                        "/api/iot-ingest/camera",
                        "/api/iot-ingest/sensor"
                )
                .build();
    }
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("2-admin-panel-api")
                .pathsToMatch(
                        "/api/admin/**",
                        "/api/users/**",
                        "/api/parking-lots/**",
                        "/api/reservations/**",
                        "/api/vehicles/**"
                )
                .build();
    }
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Akıllı Otopark API")
                        .version("1.0")
                        .description("Mobil ve Web Paneli için ayrıştırılmış API Dokümantasyonu")
                        .contact(new Contact()
                                .name("Backend Ekibi")
                                .email("destek@akilliotopark.com")));
    }
}