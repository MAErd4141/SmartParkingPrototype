package com.akilliotopark.controller;

import com.akilliotopark.dto.ParkingSpotRequest;
import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.service.ParkingSpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/parkingspots")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    @GetMapping
    public ResponseEntity<List<ParkingSpot>> getAllSpots() {
        return ResponseEntity.ok(parkingSpotService.getAllSpots());
    }

    @GetMapping("/lot/{lotId}")
    public ResponseEntity<List<ParkingSpot>> getSpotsByLot(@PathVariable UUID lotId) {
        return ResponseEntity.ok(parkingSpotService.getSpotsByLot(lotId));
    }

    @PostMapping
    public ResponseEntity<ParkingSpot> createSpot(
            @Valid @RequestBody ParkingSpotRequest request) {

        ParkingSpot saved = parkingSpotService.saveSpot(request);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingSpot> updateSpot(
            @PathVariable UUID id,
            @Valid @RequestBody ParkingSpotRequest request) {

        ParkingSpot updated = parkingSpotService.updateSpot(id, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{spotCode}/status")
    public ResponseEntity<ParkingSpot> updateSpotStatus(
            @PathVariable String spotCode,
            @RequestParam boolean occupied) {

        ParkingSpot updated = parkingSpotService.updateSpotStatus(spotCode, occupied);
        return ResponseEntity.ok(updated);
    }
}
