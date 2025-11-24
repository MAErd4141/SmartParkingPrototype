package com.akilliotopark.service;

import com.akilliotopark.dto.ReservationRequest;
import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.entity.Reservation;
import com.akilliotopark.entity.User;
import com.akilliotopark.entity.Vehicle;
import com.akilliotopark.exception.BusinessValidationException;
import com.akilliotopark.exception.ConflictException;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.repository.ParkingSpotRepository;
import com.akilliotopark.repository.ReservationRepository;
import com.akilliotopark.repository.UserRepository;
import com.akilliotopark.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final VehicleRepository vehicleRepository;
    private final QrTokenService qrTokenService;
    private final AsyncLogService logService;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional
    public Reservation createReservation(String userEmail, ReservationRequest request) {

        if (request.getReservedStart() == null || request.getReservedEnd() == null) {
            throw new BusinessValidationException("Saatler boş olamaz.");
        }
        if (!request.getReservedEnd().isAfter(request.getReservedStart())) {
            throw new BusinessValidationException("Bitiş saati başlangıçtan sonra olmalıdır.");
        }
        long durationMinutes = Duration.between(request.getReservedStart(), request.getReservedEnd()).toMinutes();

        if (durationMinutes < 15) {
            throw new BusinessValidationException("Minimum 15 dakika seçilmelidir.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + userEmail));

        ParkingSpot spot = parkingSpotRepository.findById(request.getParkingSpotId())
                .orElseThrow(() -> new NotFoundException("Park yeri bulunamadı."));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Araç bulunamadı."));

        if (!vehicle.getOwner().getId().equals(user.getId())) {
            throw new ConflictException("Bu araç size ait değil!");
        }

        List<Reservation> overlaps = reservationRepository.findOverlappingReservations(
                spot.getId(), request.getReservedStart(), request.getReservedEnd()
        );
        if (!overlaps.isEmpty()) {
            throw new ConflictException("Bu saatler arasında park yeri dolu.");
        }

        Double hourlyRate = spot.getParkingLot().getHourlyRate();

        double hours = Math.ceil(durationMinutes / 60.0);
        if (hours == 0) hours = 1.0;

        double calculatedPrice = hours * hourlyRate;

        Reservation reservation = Reservation.builder()
                .user(user)
                .parkingSpot(spot)
                .vehicle(vehicle)
                .reservedStart(request.getReservedStart())
                .reservedEnd(request.getReservedEnd())
                .totalPrice(calculatedPrice)
                .active(true)
                .confirmed(false)
                .build();

        Reservation saved = reservationRepository.save(reservation);

        String qrToken = qrTokenService.generateToken(saved.getId(), vehicle.getPlateNumber());
        saved.setQrCode(qrToken);

        Map<String, Object> logData = new HashMap<>();
        logData.put("reservationId", saved.getId());
        logData.put("userEmail", user.getEmail());
        logData.put("plate", vehicle.getPlateNumber());
        logData.put("spot", spot.getSpotCode());
        logData.put("totalPrice", saved.getTotalPrice());

        logService.saveLog(
                "ReservationService",
                "RESERVATION_CREATED",
                "Yeni Rezervasyon Oluşturuldu",
                logData // Entity yerine temiz Map gönderiyoruz
        );
        return reservationRepository.save(saved);
    }
    @Transactional
    public void confirmReservation(UUID id) {
        Reservation r = reservationRepository.findById(id).orElseThrow();
        r.setConfirmed(true);
        reservationRepository.save(r);
    }
    @Transactional
    public void completeReservation(UUID id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezervasyon bulunamadı: " + id));

        if (!r.isActive()) return;

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(r.getReservedEnd())) {
            long overstayMinutes = Duration.between(r.getReservedEnd(), now).toMinutes();

            if (overstayMinutes > 5) {
                Double hourlyRate = r.getParkingSpot().getParkingLot().getHourlyRate();
                double penaltyRate = hourlyRate * 1.5;
                double penaltyHours = Math.ceil(overstayMinutes / 60.0);
                double penaltyAmount = penaltyHours * penaltyRate;

                r.setTotalPrice(r.getTotalPrice() + penaltyAmount);
                System.out.println("CEZA KESİLDİ: " + penaltyAmount + " TL. Yeni Tutar: " + r.getTotalPrice());
            }
        }

        r.setActive(false);
        reservationRepository.save(r);

        Map<String, Object> exitData = new HashMap<>();
        exitData.put("reservationId", r.getId());
        exitData.put("finalPrice", r.getTotalPrice());
        exitData.put("endInfo", "Kullanıcı çıkış yaptı.");

        logService.saveLog(
                "ReservationService",
                "EXIT_COMPLETED",
                "Çıkış İşlemi ve Ödeme",
                exitData
        );
    }

    @Transactional
    public void cancelReservation(UUID id) {
        Reservation r = reservationRepository.findById(id).orElseThrow();
        r.setActive(false);
        reservationRepository.save(r);
    }

    public List<Reservation> getReservationsByUser(UUID userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getReservationsByUserEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return reservationRepository.findByUserId(user.getId());
    }

    public Reservation getReservationById(UUID id) {
        return reservationRepository.findById(id).orElseThrow();
    }

    public List<Reservation> getActiveReservationsAt(LocalDateTime now) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.isActive()
                        && (r.getReservedStart().isBefore(now) || r.getReservedStart().isEqual(now))
                        && (r.getReservedEnd().isAfter(now) || r.getReservedEnd().isEqual(now)))
                .toList();
    }
}