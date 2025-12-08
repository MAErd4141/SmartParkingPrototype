package com.akilliotopark.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SensorDataDto implements Serializable {
    private String spotCode;
    private boolean occupied;
    private int distanceCm;
}