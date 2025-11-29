package com.akilliotopark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ParkingLotRequest {

    @NotBlank(message = "Otopark kodu zorunludur (Örn: TR-34-05).")
    private String code;

    @NotBlank(message = "Otopark adı zorunludur.")
    private String name;

    @NotNull(message = "Saatlik ücret zorunludur.")
    private BigDecimal hourlyRate; // Double -> BigDecimal

    private String address;
    private String district;
    private String province;

    private Double latitude = 41.0082;
    private Double longitude = 28.9784;

    private int capacity = 10;
}