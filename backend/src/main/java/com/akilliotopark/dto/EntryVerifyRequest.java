package com.akilliotopark.dto;

import lombok.Data;

@Data
public class EntryVerifyRequest {
    private String qr;
    private String plate;
}