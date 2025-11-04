package com.akilliotopark.controller;

import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.service.ParkingSpotService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parkingspots")
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @GetMapping
    public List<ParkingSpot> getAll() {
        return parkingSpotService.getAllSpots();
    }

    @PostMapping
    public ParkingSpot addSpot(@RequestBody ParkingSpot spot) {
        return parkingSpotService.saveSpot(spot);
    }
}
