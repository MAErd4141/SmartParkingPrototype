package com.akilliotopark.service;

import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.entity.Reservation;
import com.akilliotopark.repository.ParkingSpotRepository;
import com.akilliotopark.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final ReservationRepository reservationRepository;
    private final Random random = new Random();

    @Scheduled(fixedRate = 15000)
    @Transactional
    public void simulateOccupancy() {
        List<ParkingSpot> spots = parkingSpotRepository.findByOccupiedFalse();
        if (spots.isEmpty()) return;

        ParkingSpot s = spots.get(random.nextInt(spots.size()));

        if (reservationRepository.count() > 0) return;

        s.setOccupied(true);
        parkingSpotRepository.save(s);
    }
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void simulateCharging() {
        List<Reservation> activeEvReservations = reservationRepository.findActiveEvReservations();

        for (Reservation r : activeEvReservations) {
            if (r.getCurrentChargeLevel() != null && r.getCurrentChargeLevel() < 100) {
                r.setCurrentChargeLevel(r.getCurrentChargeLevel() + 5);
                reservationRepository.save(r);
                log.info("🔋 Şarj Ediliyor: {} -> %{}", r.getVehicle().getPlateNumber(), r.getCurrentChargeLevel());
            }
        }
    }
}