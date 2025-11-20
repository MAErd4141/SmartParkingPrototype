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
public class ParkingLotResponse {

    private UUID id;
    private String code;
    private String name;

    private Double hourlyRate;
    private String address;
    private String province;
    private String district;

    private Double latitude;
    private Double longitude;
}