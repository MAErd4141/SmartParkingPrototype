package com.akilliotopark.dto;

import lombok.Data;

@Data
public class VehicleResponse {

    private String id;
    private String plateNumber;
    private String ownerId;
    private String ownerEmail;
    private String ownerFullName;
}
