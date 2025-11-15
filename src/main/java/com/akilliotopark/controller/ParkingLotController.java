package com.akilliotopark.controller;

import com.akilliotopark.dto.ParkingLotResponse;
import com.akilliotopark.service.ParkingLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-lots")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    @GetMapping
    public List<ParkingLotResponse> getAll() {
        return parkingLotService.getAllLots();
    }

    @GetMapping("/search")
    public List<ParkingLotResponse> getByDistrictAndProvince(
            @RequestParam String district,
            @RequestParam String province
    ) {
        return parkingLotService.getLotsByDistrictAndProvince(district, province);
    }
}
