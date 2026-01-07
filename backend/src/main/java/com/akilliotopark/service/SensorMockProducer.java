package com.akilliotopark.service;

import com.akilliotopark.dto.SensorReadingRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@ConditionalOnProperty(prefix = "sensor.mock", name = "enabled", havingValue = "true")
public class SensorMockProducer {

    private final SensorService sensorService;
    private boolean toggle = false;

    public SensorMockProducer(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @Scheduled(fixedDelayString = "${sensor.mock.period-ms:2000}")
    public void produce() {
        toggle = !toggle;
        double distance = toggle ? 15.0 : 80.0;

        sensorService.handleReading(new SensorReadingRequest(
                "mock-esp32",
                1,
                distance
        ));
    }
}
