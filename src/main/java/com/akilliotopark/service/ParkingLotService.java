package com.akilliotopark.service;

import com.akilliotopark.dto.ParkingLotResponse;
import com.akilliotopark.entity.ParkingLot;
import com.akilliotopark.repository.ParkingLotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;

    public List<ParkingLotResponse> getAllLots() {
        return parkingLotRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ParkingLotResponse> getLotsByDistrictAndProvince(String district, String province) {
        return parkingLotRepository.findByDistrictAndProvince(district, province).stream()
                .map(this::toResponse)
                .toList();
    }

    private ParkingLotResponse toResponse(ParkingLot lot) {
        ParkingLotResponse dto = new ParkingLotResponse();
        dto.setId(lot.getId().toString());
        dto.setName(lot.getName());
        dto.setLatitude(lot.getLatitude());
        dto.setLongitude(lot.getLongitude());
        dto.setAddress(lot.getAddress());

        int capacity = lot.getSpots() != null ? lot.getSpots().size() : 0;
        int available = lot.getSpots() == null ? 0 :
                (int) lot.getSpots().stream().filter(s -> !s.isOccupied()).count();

        dto.setCapacity(capacity);
        dto.setAvailable(available);

        return dto;
    }
}
