package com.akilliotopark.controller;

import com.akilliotopark.dto.ParkingSpotRequest;
import com.akilliotopark.dto.ParkingSpotResponse;
import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.mapper.ParkingSpotMapper;
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
    private final ParkingSpotMapper parkingSpotMapper;

    @GetMapping
    public ResponseEntity<List<ParkingSpotResponse>> getAllSpots() {
        List<ParkingSpot> spots = parkingSpotService.getAllSpots();
        return ResponseEntity.ok(parkingSpotMapper.toResponseDtoList(spots));
    }

    @GetMapping("/lot/{lotId}")
    public ResponseEntity<List<ParkingSpotResponse>> getSpotsByLot(@PathVariable UUID lotId) {
        List<ParkingSpot> spots = parkingSpotService.getSpotsByLot(lotId);
        return ResponseEntity.ok(parkingSpotMapper.toResponseDtoList(spots));
    }

    @PostMapping
    public ResponseEntity<ParkingSpotResponse> createSpot(
            @Valid @RequestBody ParkingSpotRequest request) {

        ParkingSpot saved = parkingSpotService.saveSpot(request);
        return ResponseEntity.ok(parkingSpotMapper.toResponseDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingSpotResponse> updateSpot(
            @PathVariable UUID id,
            @Valid @RequestBody ParkingSpotRequest request) {

        ParkingSpot updated = parkingSpotService.updateSpot(id, request);
        return ResponseEntity.ok(parkingSpotMapper.toResponseDto(updated));
    }
    @PostMapping("/{spotCode}/status")
    public ResponseEntity<ParkingSpotResponse> updateSpotStatus(
            @PathVariable String spotCode,
            @RequestParam boolean occupied) {

        ParkingSpot updated = parkingSpotService.updateSpotStatus(spotCode, occupied);
        return ResponseEntity.ok(parkingSpotMapper.toResponseDto(updated));
    }
}