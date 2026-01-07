package com.akilliotopark.service;

import com.akilliotopark.dto.SensorReadingRequest;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${sensor.mock.device-id:mock-esp32}")
    private String deviceId;

    // 0-index test etmek için default 0
    @Value("${sensor.mock.slot-id:0}")
    private int slotId;

    public SensorMockProducer(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @Scheduled(fixedDelayString = "${sensor.mock.period-ms:2000}")
    public void produce() {
        toggle = !toggle;
        double distance = toggle ? 15.0 : 80.0;

        sensorService.handleReading(new SensorReadingRequest(
                deviceId,
                slotId,
                distance
        ));
    }
}
