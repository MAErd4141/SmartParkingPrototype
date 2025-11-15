package com.akilliotopark.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppInitResponse {

    private int version;
    private UserProfileDto user;
    private String district;
    private String province;
}
