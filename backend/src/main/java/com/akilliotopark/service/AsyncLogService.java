package com.akilliotopark.service;

import com.akilliotopark.document.SystemLog;
import com.akilliotopark.repository.mongo.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncLogService {

    private final LogRepository logRepository;

    @Async
    public void saveLog(String serviceName, String type, String message, Object metadata) {
        try {
            SystemLog logEntry = SystemLog.builder()
                    .serviceName(serviceName)
                    .type(type)
                    .message(message)
                    .metadata(metadata)
                    .timestamp(LocalDateTime.now())
                    .build();

            logRepository.save(logEntry);
            log.info("✅ LOG YAZILDI: {}", message);
        } catch (Exception e) {
            log.error("❌ LOG HATASI: {}", e.getMessage());
        }
    }
}