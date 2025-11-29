package com.akilliotopark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable; // 1. BU EKLENDİ
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingLotResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String code;
    private String name;

    private BigDecimal hourlyRate;

    private String address;
    private String province;
    private String district;
    private Double latitude;
    private Double longitude;
}