package com.akilliotopark.controller;

import com.akilliotopark.dto.ParkingSpotRequest;
import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parkingspots")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;


    @GetMapping
    public List<ParkingSpot> getAll() {
        return parkingSpotService.getAllSpots();
    }

    @PostMapping

    public ParkingSpot addSpot(@RequestBody ParkingSpotRequest spotRequest) {
        return parkingSpotService.saveSpot(spotRequest);
    }
}