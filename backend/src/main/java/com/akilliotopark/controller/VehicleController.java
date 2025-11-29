package com.akilliotopark.controller;

import com.akilliotopark.dto.VehicleRequest;
import com.akilliotopark.dto.VehicleResponse;
import com.akilliotopark.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService service;

    @GetMapping("/my")
    public ResponseEntity<List<VehicleResponse>> getMy(Authentication auth) {
        return ResponseEntity.ok(service.getVehiclesByEmail(auth.getName()));
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> create(Authentication auth, @Valid @RequestBody VehicleRequest req) {
        return ResponseEntity.ok(service.createVehicleForUser(auth.getName(), req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}