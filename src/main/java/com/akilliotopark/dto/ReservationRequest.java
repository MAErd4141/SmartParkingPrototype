package com.akilliotopark.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReservationRequest {

    @NotNull(message = "Kullanıcı ID'si zorunludur.")
    private UUID userId;

    @NotNull(message = "Park yeri ID'si zorunludur.")
    private UUID parkingSpotId;

    @NotNull(message = "Başlangıç saati zorunludur.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reservedStart;

    @NotNull(message = "Bitiş saati zorunludur.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reservedEnd;

    private boolean active = true;
    private boolean confirmed = false;
}
