package com.akilliotopark.service;

import com.akilliotopark.config.RabbitMQConfig;
import com.akilliotopark.dto.OcrResultDto;
import com.akilliotopark.dto.SensorDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingMessageListener {

    private final ParkingSpotService parkingSpotService;
    private final AsyncLogService asyncLogService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CAMERA)
    public void handleCameraMessage(OcrResultDto message) {
        log.info("📷 Kamera Verisi Alındı: Plaka={}, Güven={}%, Spot={}",
                message.getPlateText(), message.getConfidence(), message.getSpotCode());

        asyncLogService.saveLog(
                "RabbitMQ-Camera",
                "PLATE_DETECTED",
                "Plaka okundu: " + message.getPlateText(),
                message
        );
    }
    @RabbitListener(queues = RabbitMQConfig.QUEUE_IOT)
    public void handleIotMessage(SensorDataDto message) {
        log.info("📡 Sensör Verisi: Spot={}, Dolu mu={}", message.getSpotCode(), message.isOccupied());

        try {
            parkingSpotService.updateSpotStatus(message.getSpotCode(), message.isOccupied());
            asyncLogService.saveLog(
                    "RabbitMQ-IoT",
                    "STATUS_CHANGE",
                    "Park yeri durumu güncellendi: " + message.getSpotCode(),
                    message
            );
        } catch (Exception e) {
            log.error("❌ Sensör verisi işlenirken hata: {}", e.getMessage());
        }
    }
}