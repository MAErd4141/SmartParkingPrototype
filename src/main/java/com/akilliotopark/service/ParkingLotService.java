package com.akilliotopark.service;

import com.akilliotopark.dto.ParkingLotResponse;
import com.akilliotopark.dto.ParkingLotStatusResponse;
import com.akilliotopark.dto.ParkingSpotResponse;
import com.akilliotopark.entity.ParkingLot;
import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.mapper.ParkingLotMapper;
import com.akilliotopark.mapper.ParkingSpotMapper;
import com.akilliotopark.repository.ParkingLotRepository;
import com.akilliotopark.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingLotMapper parkingLotMapper;
    private final ParkingSpotMapper parkingSpotMapper;

    /**
     * Tüm otoparkları getir (genel liste)
     */
    public List<ParkingLotResponse> getAllLots() {
        List<ParkingLot> lots = parkingLotRepository.findAll();
        return parkingLotMapper.toResponseDtoList(lots);
    }

    /**
     * Aynı işi yapan alternatif isimli method (istersen kullanma):
     * Controller’da getAll() kullanıyorsan bozulmasın diye ekledim.
     */
    public List<ParkingLotResponse> getAll() {
        return getAllLots();
    }

    /**
     * İl + ilçe bazlı otopark listesi
     */
    public List<ParkingLotResponse> getLotsByDistrictAndProvince(String district, String province) {
        List<ParkingLot> lots =
                parkingLotRepository.findByDistrictAndProvince(district, province);

        return parkingLotMapper.toResponseDtoList(lots);
    }

    /**
     * Tek otopark detayı
     */
    public ParkingLotResponse getById(UUID id) {
        ParkingLot lot = findLotOrThrow(id);
        return parkingLotMapper.toResponseDto(lot);
    }

    /**
     * Bir otoparka ait tüm park yerleri
     */
    public List<ParkingSpotResponse> getSpotsByLot(UUID lotId) {
        ParkingLot lot = findLotOrThrow(lotId);
        List<ParkingSpot> spots = parkingSpotRepository.findByParkingLot(lot);
        return parkingSpotMapper.toResponseDtoList(spots);
    }

    /**
     * Doluluk / boşluk durumu (%)
     */
    public ParkingLotStatusResponse getStatus(UUID lotId) {
        ParkingLot lot = findLotOrThrow(lotId);

        long total = parkingSpotRepository.countByParkingLot(lot);
        long occupied = parkingSpotRepository.countByParkingLotAndOccupiedTrue(lot);
        long free = total - occupied;
        double occupancyRate = (total == 0) ? 0.0 : (occupied * 100.0 / total);

        return ParkingLotStatusResponse.builder()
                .parkingLotId(lot.getId())
                .totalSpots(total)
                .occupiedSpots(occupied)
                .freeSpots(free)
                .occupancyRate(occupancyRate)
                .build();
    }
    private ParkingLot findLotOrThrow(UUID id) {
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Otopark bulunamadı: " + id));
    }
}
