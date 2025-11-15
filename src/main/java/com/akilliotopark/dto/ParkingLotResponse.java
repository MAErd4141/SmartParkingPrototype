// ParkingLotResponse.java
package com.akilliotopark.dto;

import lombok.Data;

@Data
public class ParkingLotResponse {

    private String id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer capacity;
    private Integer available;
    private String address;
}
