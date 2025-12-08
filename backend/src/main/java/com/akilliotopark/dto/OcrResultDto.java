package com.akilliotopark.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class OcrResultDto implements Serializable {
    private String spotCode;
    private String plateText;
    private double confidence;
    private String timestamp;
}