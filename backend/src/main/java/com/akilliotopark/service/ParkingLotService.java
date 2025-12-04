package com.akilliotopark.service;

import com.akilliotopark.dto.ParkingLotRequest;
import com.akilliotopark.dto.ParkingLotResponse;
import com.akilliotopark.dto.ParkingLotStatusResponse;
import com.akilliotopark.dto.ParkingSpotResponse;
import com.akilliotopark.dto.SpotAvailabilityResponse;
import com.akilliotopark.entity.*;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.mapper.ParkingLotMapper;
import com.akilliotopark.mapper.ParkingSpotMapper;
import com.akilliotopark.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final TariffRepository tariffRepository;
    private final TariffRuleRepository tariffRuleRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ParkingLotMapper parkingLotMapper;
    private final ParkingSpotMapper parkingSpotMapper;

    @Cacheable(value = "parking_lots", key = "'all'")
    public List<ParkingLotResponse> getAll() {
        log.info("📡 Veritabanından Otopark Listesi Çekiliyor...");
        return parkingLotMapper.toResponseDtoList(parkingLotRepository.findAll());
    }

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

    public List<SpotAvailabilityResponse> checkAvailability(UUID lotId, LocalDateTime start, LocalDateTime end) {
        List<ParkingSpot> allSpots = parkingSpotRepository.findByParkingLotId(lotId);
        List<Reservation> activeReservations = reservationRepository.findActiveReservationsInLot(lotId, start, end);

        List<SpotAvailabilityResponse> response = new ArrayList<>();

        for (ParkingSpot spot : allSpots) {
            boolean isReserved = activeReservations.stream()
                    .anyMatch(r -> r.getParkingSpot().getId().equals(spot.getId()));

            response.add(SpotAvailabilityResponse.builder()
                    .spotId(spot.getId())
                    .spotCode(spot.getSpotCode())
                    .type(spot.getType().name())
                    .isAvailable(!isReserved)
                    .isOccupiedNow(spot.isOccupied())
                    .build());
        }

        return response;
    }
    @Transactional
    @CacheEvict(value = "parking_lots", allEntries = true)
    public ParkingLot createParkingLot(ParkingLotRequest request) {

        Tariff tariff = Tariff.builder()
                .name(request.getName() + " Standart Tarifesi")
                .build();
        tariffRepository.save(tariff);

        TariffRule rule = TariffRule.builder()
                .tariff(tariff)
                .minMinutes(0)
                .maxMinutes(1440)
                .price(request.getHourlyRate())
                .build();
        tariffRuleRepository.save(rule);

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
        addSpotsToLotEntity(savedLot, request.getCapacity());

        return savedLot;
    }
    @Transactional
    public void addSpots(UUID lotId, int count) {
        ParkingLot lot = findLotOrThrow(lotId);
        addSpotsToLotEntity(lot, count);
    }
    private void addSpotsToLotEntity(ParkingLot lot, int count) {
        long currentCount = parkingSpotRepository.countByParkingLot(lot);

        for (int i = 1; i <= count; i++) {
            ParkingSpot spot = ParkingSpot.builder()
                    .spotCode("P-" + (currentCount + i)) // Basit isimlendirme (P-1, P-2...)
                    .occupied(false)
                    .parkingLot(lot)
                    .type(SpotType.STANDARD)
                    .hasCharger(false)
                    .build();
            parkingSpotRepository.save(spot);
        }
    }
    private ParkingLot findLotOrThrow(UUID id) {
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Otopark bulunamadı: " + id));
    }
    public void validateAdminAccess(UUID parkingLotId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı"));
        if (user.getRole() == UserRole.SUPERVISOR) {
            return;
        }
        if (user.getRole() == UserRole.ADMIN) {
            if (user.getManagedParkingLot() == null) {
                throw new AccessDeniedException("Bu Admin'e atanmış bir otopark yok!");
            }
            if (!user.getManagedParkingLot().getId().equals(parkingLotId)) {
                throw new AccessDeniedException("Bu otoparkı yönetme yetkiniz yok! Sadece kendi otoparkınıza işlem yapabilirsiniz.");
            }
            return;
        }
        throw new AccessDeniedException("Yetkisiz Erişim");
    }
}