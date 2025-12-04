package com.akilliotopark;

import com.akilliotopark.document.SystemLog;
import com.akilliotopark.repository.mongo.LogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@Slf4j
public class AkilliOtoparkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AkilliOtoparkBackendApplication.class, args);
    }
    @Bean
    CommandLineRunner testMongoConnection(LogRepository logRepository) {
        return args -> {
            log.info("========================================");
            log.info("🧪 MONGO DB TESTİ BAŞLIYOR...");

            try {
                SystemLog testLog = SystemLog.builder()
                        .serviceName("TEST_RUNNER")
                        .type("STARTUP_CHECK")
                        .message("Bu bir otomatik test kaydıdır.")
                        .timestamp(LocalDateTime.now())
                        .build();

                logRepository.save(testLog);

                log.info("✅ MONGO DB TESTİ BAŞARILI! Kayıt atıldı.");

            } catch (Exception e) {
                log.error("❌ MONGO DB TESTİ BAŞARISIZ!", e);
            }
            log.info("========================================");
        };
    }
}