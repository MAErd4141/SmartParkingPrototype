package com.akilliotopark.controller;

import com.akilliotopark.dto.VehicleRequest;
import com.akilliotopark.dto.VehicleResponse;
import com.akilliotopark.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PreAuthorize("hasAnyRole('BASIC','ADMIN')")
    @GetMapping("/my")
    public ResponseEntity<List<VehicleResponse>> getMyVehicles(Authentication auth) {
        return ResponseEntity.ok(
                vehicleService.getVehiclesByEmail(auth.getName())
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PreAuthorize("hasAnyRole('BASIC','ADMIN')")
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(
            Authentication auth,
            @Valid @RequestBody VehicleRequest request
    ) {
        return ResponseEntity.ok(
                vehicleService.createVehicleForUser(auth.getName(), request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleRequest request
    ) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable UUID id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}