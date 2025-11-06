package com.akilliotopark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRequest {

    @NotBlank(message = "Plaka numarası zorunludur.")
    private String plateNumber;

    @NotNull(message = "Kullanıcı ID'si zorunludur.")
    private Long ownerId;
}