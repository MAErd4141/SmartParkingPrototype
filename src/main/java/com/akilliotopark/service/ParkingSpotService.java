package com.akilliotopark.service;

import com.akilliotopark.dto.ParkingSpotRequest;
import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.exception.BusinessValidationException;
import com.akilliotopark.exception.ConflictException;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.mapper.ParkingSpotMapper;
import com.akilliotopark.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingSpotMapper parkingSpotMapper;

    public List<ParkingSpot> getAllSpots() {
        return parkingSpotRepository.findAll();
    }

    @Transactional
    public ParkingSpot saveSpot(ParkingSpotRequest request) {
        ParkingSpot spot = parkingSpotMapper.toEntity(request);
        if (request.getOccupied() == null) {
            throw new BusinessValidationException("Doluluk durumu belirtilmelidir.");
        }
        spot.setOccupied(request.getOccupied());
        validateSpot(spot);
        return parkingSpotRepository.save(spot);
    }
    @Transactional
    public ParkingSpot updateSpot(Long id, ParkingSpotRequest request) {
        ParkingSpot existing = parkingSpotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Park alanı bulunamadı: " + id));
        existing.setSpotCode(request.getSpotCode());
        if (request.getOccupied() == null) {
            throw new BusinessValidationException("Doluluk durumu belirtilmelidir.");
        }
        existing.setOccupied(request.getOccupied());
        validateSpot(existing);
        return parkingSpotRepository.save(existing);
    }
    @Transactional
    public ParkingSpot updateSpotStatus(String spotCode, boolean occupied) {
        ParkingSpot spot = parkingSpotRepository.findBySpotCode(spotCode);
        if (spot == null) {
            throw new NotFoundException("Park alanı bulunamadı: " + spotCode);
        }
        spot.setOccupied(occupied);
        return parkingSpotRepository.save(spot);
    }
    public List<ParkingSpot> getAvailableSpots() {
        return parkingSpotRepository.findAll().stream()
                .filter(spot -> !spot.isOccupied())
                .toList();
    }
    private void validateSpot(ParkingSpot spot) {
        if (spot == null) {
            throw new BusinessValidationException("ParkingSpot nesnesi null olamaz.");
        }
        if (spot.getSpotCode() == null || spot.getSpotCode().isBlank()) {
            throw new BusinessValidationException("spotCode boş olamaz.");
        }
        ParkingSpot existing = parkingSpotRepository.findBySpotCode(spot.getSpotCode());
        if (existing != null && !Objects.equals(existing.getId(), spot.getId())) {
            throw new ConflictException("Bu spotCode zaten kullanımda: " + spot.getSpotCode());
        }
    }
}
