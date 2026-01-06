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
    private final QrTokenService qrTokenService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CAMERA)
    public void handleCameraMessage(OcrResultDto message) {
        String plate = message.getPlateText();
        double conf = message.getConfidence();
        String spotCode = message.getSpotCode();
        String qr = message.getQr();

        log.info("📷 Kamera Verisi Alındı: Plaka={}, Güven={}%, Spot={}, QR={}, Device={}",
                plate,
                conf,
                spotCode,
                (qr != null && !qr.isBlank()) ? "OK" : "NULL",
                message.getDeviceId()
        );

        asyncLogService.saveLog(
                "RabbitMQ-Camera",
                "PLATE_DETECTED",
                "Plaka okundu: " + plate,
                message
        );

        if (qr == null || qr.isBlank()) {
            asyncLogService.saveLog(
                    "RabbitMQ-Camera",
                    "QR_MISSING",
                    "QR gelmedi. Plaka: " + plate,
                    message
            );
            return;
        }

        try {
            boolean valid = qrTokenService.validateToken(qr, plate);

            if (valid) {
                asyncLogService.saveLog(
                        "RabbitMQ-Camera",
                        "ENTRY_SUCCESS",
                        "Giriş onaylandı ✅ Plaka: " + plate,
                        message
                );
            } else {
                asyncLogService.saveLog(
                        "RabbitMQ-Camera",
                        "SECURITY_ALERT",
                        "Yetkisiz giriş denemesi ⛔ Plaka: " + plate,
                        message
                );
            }
        } catch (Exception e) {
            log.error("❌ QR doğrulama hatası: {}", e.getMessage());
            asyncLogService.saveLog(
                    "RabbitMQ-Camera",
                    "ENTRY_VERIFY_ERROR",
                    e.getMessage(),
                    message
            );
        }
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
            asyncLogService.saveLog(
                    "RabbitMQ-IoT",
                    "SENSOR_ERROR",
                    e.getMessage(),
                    message
            );
        }
    }
}