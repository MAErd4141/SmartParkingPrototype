package com.akilliotopark.controller;

import com.akilliotopark.dto.*;
import com.akilliotopark.entity.ParkingLot;
import com.akilliotopark.mapper.ParkingLotMapper;
import com.akilliotopark.service.ParkingLotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/parking-lots")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotService parkingLotService;
    private final ParkingLotMapper parkingLotMapper;

    @GetMapping
    public ResponseEntity<List<ParkingLotResponse>> getAll() {
        return ResponseEntity.ok(parkingLotService.getAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ParkingLotResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(parkingLotService.getById(id));
    }
    @GetMapping("/{id}/spots")
    public ResponseEntity<List<ParkingSpotResponse>> getSpots(@PathVariable UUID id) {
        return ResponseEntity.ok(parkingLotService.getSpotsByLot(id));
    }
    @GetMapping("/{id}/status")
    public ResponseEntity<ParkingLotStatusResponse> getStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(parkingLotService.getStatus(id));
    }
    @GetMapping("/{id}/availability")
    public ResponseEntity<List<SpotAvailabilityResponse>> checkAvailability(
            @PathVariable UUID id,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(parkingLotService.checkAvailability(id, start, end));
    }
    @PreAuthorize("hasRole('SUPERVISOR')")
    @PostMapping
    public ResponseEntity<ParkingLotResponse> createParkingLot(
            @Valid @RequestBody ParkingLotRequest request) {

        ParkingLot createdLot = parkingLotService.createParkingLot(request);
        return ResponseEntity.ok(parkingLotMapper.toResponseDto(createdLot));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @PostMapping("/{id}/spots/add")
    public ResponseEntity<String> addSpotsToLot(
            @PathVariable UUID id,
            @RequestParam int count,
            Authentication auth) {

        parkingLotService.validateAdminAccess(id, auth.getName());
        parkingLotService.addSpots(id, count);

        return ResponseEntity.ok(count + " adet yeni park yeri eklendi.");
    }
}