package com.akilliotopark.dto;

import lombok.Data;

@Data
public class VehicleResponse {

    private Long id;
    private String plateNumber;
    private Long ownerId;
}