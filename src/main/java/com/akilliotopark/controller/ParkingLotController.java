package com.akilliotopark.controller;

import com.akilliotopark.dto.ParkingLotResponse;
import com.akilliotopark.dto.ParkingLotStatusResponse;
import com.akilliotopark.dto.ParkingSpotResponse;
import com.akilliotopark.service.ParkingLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/parking-lots")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

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
}
