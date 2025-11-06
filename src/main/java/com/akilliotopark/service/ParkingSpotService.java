package com.akilliotopark.service;

import com.akilliotopark.dto.ParkingSpotRequest;
import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.mapper.ParkingSpotMapper;
import com.akilliotopark.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingSpotMapper parkingSpotMapper;


    /** Tüm park yerleri */
    public List<ParkingSpot> getAllSpots() {
        return parkingSpotRepository.findAll();
    }

    @Transactional
    public ParkingSpot saveSpot(ParkingSpotRequest request) {
        ParkingSpot spot = parkingSpotMapper.toEntity(request);

        validateSpot(spot);
        return parkingSpotRepository.save(spot);
    }

    @Deprecated
    public ParkingSpot createSpot(ParkingSpot spot) {
        return parkingSpotRepository.save(spot);
    }

    @Transactional
    public ParkingSpot updateSpotStatus(String code, boolean occupied) {
        ParkingSpot spot = parkingSpotRepository.findBySpotCode(code);
        if (spot == null) {
            throw new RuntimeException("Park yeri bulunamadı: " + code);
        }
        spot.setOccupied(occupied);
        return parkingSpotRepository.save(spot);
    }

    public List<ParkingSpot> getAvailableSpots() {
        return parkingSpotRepository.findAll()
                .stream()
                .filter(s -> !Boolean.TRUE.equals(s.isOccupied()))
                .collect(Collectors.toList());
    }

    public ParkingSpot getByCode(String code) {
        ParkingSpot spot = parkingSpotRepository.findBySpotCode(code);
        if (spot == null) {
            throw new RuntimeException("Park yeri bulunamadı: " + code);
        }
        return spot;
    }

    private void validateSpot(ParkingSpot spot) {
        if (spot == null) {
            throw new IllegalArgumentException("ParkingSpot nesnesi null olamaz.");
        }
        if (spot.getSpotCode() == null || spot.getSpotCode().isBlank()) {
            throw new IllegalArgumentException("spotCode boş olamaz.");
        }
        ParkingSpot existing = parkingSpotRepository.findBySpotCode(spot.getSpotCode());
        if (existing != null && !Objects.equals(existing.getId(), spot.getId())) {
            throw new IllegalStateException("Bu spotCode zaten kullanımda: " + spot.getSpotCode());
        }
    }
}