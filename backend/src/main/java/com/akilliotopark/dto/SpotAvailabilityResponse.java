package com.akilliotopark.dto;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class SpotAvailabilityResponse {
    private UUID spotId;
    private String spotCode;
    private String type;
    private boolean isAvailable;
    private boolean isOccupiedNow;
}