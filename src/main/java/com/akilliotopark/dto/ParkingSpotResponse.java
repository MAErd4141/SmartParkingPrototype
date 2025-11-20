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
public class ParkingSpotResponse {

    private UUID id;

    private String spotCode;
    private boolean occupied;

    private UUID parkingLotId;
}
