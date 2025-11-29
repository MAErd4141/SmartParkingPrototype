package com.akilliotopark.service;

import com.akilliotopark.dto.ParkingLotRequest;
import com.akilliotopark.dto.ParkingLotResponse;
import com.akilliotopark.dto.ParkingLotStatusResponse;
import com.akilliotopark.dto.ParkingSpotResponse;
import com.akilliotopark.entity.*;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.mapper.ParkingLotMapper;
import com.akilliotopark.mapper.ParkingSpotMapper;
import com.akilliotopark.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final TariffRepository tariffRepository;
    private final TariffRuleRepository tariffRuleRepository;
    private final ParkingLotMapper parkingLotMapper;
    private final ParkingSpotMapper parkingSpotMapper;

    /**
     * Tüm otoparkları getir.
     * REDIS: İlk istekte veritabanından çeker, sonrakileri RAM'den (Redis) getirir.
     */
    @Cacheable(value = "parking_lots", key = "'all'") // <-- REDIS DEVREDE
    public List<ParkingLotResponse> getAll() {
        // Konsola yazalım ki Redis'in çalıştığını (buraya girmediğinde) anlayalım
        System.out.println("📡 Veritabanından Otopark Listesi Çekiliyor...");
        return parkingLotMapper.toResponseDtoList(parkingLotRepository.findAll());
    }

    /**
     * ID'ye göre otopark getir.
     * (Bunu da cacheleyebiliriz ama şimdilik gerek yok, detay sürekli değişebilir)
     */
    public ParkingLotResponse getById(UUID id) {
        return parkingLotMapper.toResponseDto(findLotOrThrow(id));
    }

    public List<ParkingSpotResponse> getSpotsByLot(UUID id) {
        return parkingSpotMapper.toResponseDtoList(
                parkingSpotRepository.findByParkingLot(findLotOrThrow(id))
        );
    }

    public ParkingLotStatusResponse getStatus(UUID id) {
        ParkingLot lot = findLotOrThrow(id);
        long total = parkingSpotRepository.countByParkingLot(lot);
        long occupied = parkingSpotRepository.countByParkingLotAndOccupiedTrue(lot);
        return ParkingLotStatusResponse.builder()
                .parkingLotId(lot.getId())
                .totalSpots(total)
                .occupiedSpots(occupied)
                .freeSpots(total - occupied)
                .occupancyRate(total == 0 ? 0 : (double) occupied / total * 100)
                .build();
    }

    /**
     * Admin Panelinden Otopark Ekleme.
     * REDIS: Yeni veri eklendiği için eski cache'i temizlemeliyiz.
     */
    @Transactional
    @CacheEvict(value = "parking_lots", allEntries = true) // <-- REDIS TEMİZLİK
    public ParkingLot createParkingLot(ParkingLotRequest request) {

        // 1. Tarife Oluştur
        Tariff tariff = Tariff.builder()
                .name(request.getName() + " Standart Tarifesi")
                .build();
        tariffRepository.save(tariff);

        // 2. Kural Oluştur (0-24 saat için adminin girdiği fiyat)
        TariffRule rule = TariffRule.builder()
                .tariff(tariff)
                .minMinutes(0)
                .maxMinutes(1440)
                .price(request.getHourlyRate())
                .build();
        tariffRuleRepository.save(rule);

        // 3. Otoparkı Kaydet
        ParkingLot lot = ParkingLot.builder()
                .code(request.getCode())
                .name(request.getName())
                .address(request.getAddress())
                .district(request.getDistrict())
                .province(request.getProvince())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .tariff(tariff)
                .build();

        ParkingLot savedLot = parkingLotRepository.save(lot);

        // 4. Park Yerlerini Oluştur
        for (int i = 1; i <= request.getCapacity(); i++) {
            ParkingSpot spot = ParkingSpot.builder()
                    .spotCode("P-" + i)
                    .occupied(false)
                    .parkingLot(savedLot)
                    .type(SpotType.STANDARD)
                    .hasCharger(false)
                    .build();
            parkingSpotRepository.save(spot);
        }

        return savedLot;
    }

    private ParkingLot findLotOrThrow(UUID id) {
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Otopark bulunamadı: " + id));
    }
}