package com.akilliotopark.controller;

import com.akilliotopark.dto.VehicleRequest;
import com.akilliotopark.dto.VehicleResponse;
import com.akilliotopark.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public List<VehicleResponse> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @PostMapping
    public VehicleResponse addVehicle(@RequestBody VehicleRequest vehicleRequest) {
        return vehicleService.saveVehicle(vehicleRequest);
    }


    @GetMapping("/user/{userId}")
    public List<VehicleResponse> getVehiclesByUserId(@PathVariable Long userId) {
        return vehicleService.getVehiclesByUserId(userId);
    }
}