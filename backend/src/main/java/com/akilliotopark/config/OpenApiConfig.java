package com.akilliotopark.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * 1. GRUP: MOBİL UYGULAMA (Normal Kullanıcı)
     * Sadece 'BASIC' rolündeki bir kullanıcının erişebileceği ve ihtiyaç duyduğu uçlar.
     */
    @Bean
    public GroupedOpenApi mobileApi() {
        return GroupedOpenApi.builder()
                .group("1-mobil-app-api")
                .pathsToMatch(
                        // Auth
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/logout",

                        // App & Profil (Kullanıcının Görmesi Gereken Yer Burası)
                        "/api/app/init",
                        "/api/users/me",  // <--- ÖNEMLİ: Sadece 'me' endpointi burada

                        // Araçlar (Kendi araçları)
                        "/api/vehicles",      // Ekleme
                        "/api/vehicles/my",   // Listeleme
                        "/api/vehicles/{id}", // Silme/Güncelleme

                        // Otoparklar (Herkes görebilir)
                        "/api/parking-lots",
                        "/api/parking-lots/{id}",
                        "/api/parking-lots/{id}/spots",

                        // Rezervasyon (Kendi işlemleri)
                        "/api/reservations",          // Oluştur
                        "/api/reservations/my",       // Listele
                        "/api/reservations/{id}/cancel", // İptal Et

                        // Giriş Simülasyonu
                        "/api/entry/verify"
                )
                .build();
    }

    /**
     * 2. GRUP: ADMİN PANELİ
     * Yöneticinin ihtiyaç duyduğu Dashboard ve Yönetim uçları.
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("2-admin-panel-api")
                .pathsToMatch(
                        // Dashboard
                        "/api/admin/**",

                        // Kullanıcı Yönetimi (Hepsini gör, sil, düzenle)
                        "/api/users/**",

                        // Otopark Yönetimi (Ekleme dahil)
                        "/api/parking-lots/**",

                        // Rezervasyon Yönetimi (Hepsini gör, müdahale et)
                        "/api/reservations/**",

                        // Araç Yönetimi (Tüm araçları gör)
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