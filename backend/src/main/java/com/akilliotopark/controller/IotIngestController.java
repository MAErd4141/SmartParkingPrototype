package com.akilliotopark.controller;

import com.akilliotopark.config.RabbitMQConfig;
import com.akilliotopark.dto.OcrResultDto;
import com.akilliotopark.dto.SensorDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/iot-ingest")
@RequiredArgsConstructor
public class IotIngestController {

    private final AmqpTemplate amqpTemplate;

    @PostMapping("/camera")
    public ResponseEntity<Map<String, Object>> camera(@RequestBody OcrResultDto dto) {
        amqpTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "parking.camera.entry", dto);
        return ResponseEntity.accepted().body(Map.of("received", true));
    }
    @PostMapping("/sensor")
    public ResponseEntity<Map<String, Object>> sensor(@RequestBody SensorDataDto dto) {
        amqpTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "parking.sensor.occupancy", dto);
        return ResponseEntity.accepted().body(Map.of("received", true));
    }
}
