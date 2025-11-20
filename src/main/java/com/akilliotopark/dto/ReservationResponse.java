package com.akilliotopark.dto;

import lombok.Data;

@Data
public class ReservationResponse {

    private String id;
    private String userId;

    private String vehicleId;
    private String vehiclePlate;

    private String parkingSpotId;
    private String parkingLotId;
    private String parkingLotName;
    private String parkingSpotCode;

    private Double totalPrice;

    private String reservedStart;
    private String reservedEnd;

    private boolean active;
    private boolean confirmed;
    private String qrCode;
}