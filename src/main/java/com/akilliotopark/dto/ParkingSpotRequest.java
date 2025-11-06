package com.akilliotopark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParkingSpotRequest {

    @NotBlank(message = "Park yeri kodu boş bırakılamaz.")
    private String spotCode;

    @NotNull(message = "Doluluk durumu belirtilmelidir.")
    private boolean occupied;
}