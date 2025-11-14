package com.akilliotopark.controller;

import com.akilliotopark.dto.ParkingSpotRequest;
import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.service.ParkingSpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/parkingspots")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    @GetMapping
    public ResponseEntity<List<ParkingSpot>> getAllSpots() {
        List<ParkingSpot> spots = parkingSpotService.getAllSpots();
        return ResponseEntity.ok(spots);
    }

    @GetMapping("/available")
    public ResponseEntity<List<ParkingSpot>> getAvailableSpots() {
        List<ParkingSpot> spots = parkingSpotService.getAvailableSpots();
        return ResponseEntity.ok(spots);
    }

    @PostMapping
    public ResponseEntity<ParkingSpot> createSpot(@Valid @RequestBody ParkingSpotRequest request) {
        ParkingSpot created = parkingSpotService.saveSpot(request);
        return ResponseEntity
                .created(URI.create("/api/parkingspots/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingSpot> updateSpot(@PathVariable Long id,
                                                  @Valid @RequestBody ParkingSpotRequest request) {
        ParkingSpot updated = parkingSpotService.updateSpot(id, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{spotCode}/status")
    public ResponseEntity<ParkingSpot> updateSpotStatus(@PathVariable String spotCode,
                                                        @RequestParam("occupied") boolean occupied) {
        ParkingSpot updated = parkingSpotService.updateSpotStatus(spotCode, occupied);
        return ResponseEntity.ok(updated);
    }
}
