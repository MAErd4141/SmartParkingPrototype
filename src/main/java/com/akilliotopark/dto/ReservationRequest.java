package com.akilliotopark.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationRequest {

    @NotNull(message = "Kullanıcı ID'si zorunludur.")
    private Long userId;

    @NotNull(message = "Park Yeri ID'si zorunludur.")
    private Long parkingSpotId;

    @NotNull(message = "Başlangıç saati zorunludur.")

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reservedStart;

    @NotNull(message = "Bitiş saati zorunludur.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reservedEnd;

    private boolean active = true;
    private boolean confirmed = false;
}