package com.akilliotopark.controller;

import com.akilliotopark.dto.SensorReadingRequest;
import com.akilliotopark.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sensor")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @PostMapping("/reading")
    public Map<String, Object> ingest(@RequestBody SensorReadingRequest req) {
        sensorService.handleReading(req);
        return Map.of("ok", true);
    }
}
