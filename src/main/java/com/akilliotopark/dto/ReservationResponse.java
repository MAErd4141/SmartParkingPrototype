package com.akilliotopark.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationResponse {

    private Long id;

    // İlişkisel ID'ler
    private Long userId;
    private Long parkingSpotId;

    // Zaman ve QR Kodu
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reservedStart;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reservedEnd;

    private String qrCode;
    private boolean active;
    private boolean confirmed;
}