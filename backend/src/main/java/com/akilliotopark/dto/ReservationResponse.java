package com.akilliotopark.dto;

import lombok.Data;
import java.math.BigDecimal;

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

    private BigDecimal totalPrice;

    private Integer currentChargeLevel;

    private String reservedStart;
    private String reservedEnd;
    private boolean active;
    private boolean confirmed;
    private String qrCode;
}