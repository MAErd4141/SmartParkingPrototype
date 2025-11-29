package com.akilliotopark.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VehicleRequest {

    @NotBlank(message = "Plaka numarası zorunludur.")
    private String plateNumber;

    private String type;
}