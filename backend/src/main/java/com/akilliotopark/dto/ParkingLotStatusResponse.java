package com.akilliotopark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingLotStatusResponse {

    private UUID parkingLotId;

    private long totalSpots;
    private long occupiedSpots;
    private long freeSpots;

    private double occupancyRate;
}
