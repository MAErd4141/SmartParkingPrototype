package com.akilliotopark.dto;

public record SensorReadingRequest(
        String deviceId,
        int slotId,
        double distanceCm
) {}
