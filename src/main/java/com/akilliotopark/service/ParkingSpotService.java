package com.akilliotopark.service;

import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.repository.ParkingSpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;

    // ✅ Elle yazılmış constructor
    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    /** Tüm park yerleri */
    public List<ParkingSpot> getAllSpots() {
        return parkingSpotRepository.findAll();
    }

    /** Controller’ın beklediği isim: saveSpot */
    public ParkingSpot saveSpot(ParkingSpot spot) {
        validateSpot(spot);
        return parkingSpotRepository.save(spot);
    }

    /** Mevcut kodlarda kullanıldıysa bozulmasın diye: createSpot -> saveSpot delegasyonu */
    @Deprecated
    public ParkingSpot createSpot(ParkingSpot spot) {
        return saveSpot(spot);
    }

    /** Kod ile durum güncelleme */
    @Transactional
    public ParkingSpot updateSpotStatus(String code, boolean occupied) {
        ParkingSpot spot = parkingSpotRepository.findBySpotCode(code);
        if (spot == null) {
            throw new RuntimeException("Park yeri bulunamadı: " + code);
        }
        spot.setOccupied(occupied);
        // @Transactional olduğundan explicit save şart değil ama tutarlılık için bırakalım:
        return parkingSpotRepository.save(spot);
    }

    /** Müsait park yerleri */
    public List<ParkingSpot> getAvailableSpots() {
        return parkingSpotRepository.findAll()
                .stream()
                .filter(s -> !Boolean.TRUE.equals(s.isOccupied()))
                .collect(Collectors.toList()); // JDK8-11 uyumlu
    }

    /** İsteğe bağlı yardımcı: koda göre tek park yeri getir */
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
        // Aynı koddan var mı? (Repository'nizde unique constraint varsa bu opsiyonel)
        ParkingSpot existing = parkingSpotRepository.findBySpotCode(spot.getSpotCode());
        if (existing != null && !Objects.equals(existing.getId(), spot.getId())) {
            throw new IllegalStateException("Bu spotCode zaten kullanımda: " + spot.getSpotCode());
        }
    }
}
