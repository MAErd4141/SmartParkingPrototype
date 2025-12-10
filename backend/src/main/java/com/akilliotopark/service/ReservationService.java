package com.akilliotopark.service;

import com.akilliotopark.dto.ReservationRequest;
import com.akilliotopark.entity.*;
import com.akilliotopark.exception.BusinessValidationException;
import com.akilliotopark.exception.ConflictException;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.repository.ParkingSpotRepository;
import com.akilliotopark.repository.ReservationRepository;
import com.akilliotopark.repository.UserRepository;
import com.akilliotopark.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final VehicleRepository vehicleRepository;
    private final QrTokenService qrTokenService;
    private final AsyncLogService logService;
    private final SubscriptionService subscriptionService;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    public List<Reservation> findConflictingReservations(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new BusinessValidationException("Bitiş saati, başlangıç saatinden önce olamaz.");
        }
        return reservationRepository.findConflictingReservations(startTime, endTime);
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

        if (spot.getType() == SpotType.EV_CHARGING && vehicle.getType() != VehicleType.EV) {
            throw new ConflictException("HATA: Elektrikli şarj istasyonuna sadece Elektrikli Araçlar (EV) rezervasyon yapabilir.");
        }
        if (spot.getType() == SpotType.MOTORCYCLE && vehicle.getType() != VehicleType.MOTORCYCLE) {
            throw new ConflictException("HATA: Bu alan sadece motosikletler içindir.");
        }

        BigDecimal calculatedPrice;
        boolean isSubscriber = subscriptionService.hasActiveSubscription(user, spot.getParkingLot());

        if (isSubscriber) {
            calculatedPrice = BigDecimal.ZERO;
        } else {
            calculatedPrice = calculateDynamicPrice(spot.getParkingLot().getTariff(), durationMinutes);
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .parkingSpot(spot)
                .vehicle(vehicle)
                .reservedStart(request.getReservedStart())
                .reservedEnd(request.getReservedEnd())
                .totalPrice(calculatedPrice)
                .currentChargeLevel(vehicle.getType() == VehicleType.EV ? 20 : null)
                .active(true)
                .confirmed(false)
                .build();

        Reservation saved = reservationRepository.save(reservation);

        String qrToken = qrTokenService.generateToken(saved.getId(), vehicle.getPlateNumber());
        saved.setQrCode(qrToken);

        Map<String, Object> logData = new HashMap<>();
        logData.put("reservationId", saved.getId());
        logData.put("plate", vehicle.getPlateNumber());
        logData.put("totalPrice", saved.getTotalPrice());
        logData.put("isSubscriber", isSubscriber);

        logService.saveLog(
                "ReservationService",
                "RESERVATION_CREATED",
                "Yeni Rezervasyon Oluşturuldu",
                logData
        );
        return reservationRepository.save(saved);
    }
    private BigDecimal calculateDynamicPrice(Tariff tariff, long durationMinutes) {
        if (tariff == null || tariff.getRules() == null || tariff.getRules().isEmpty()) {
            double hours = Math.ceil(durationMinutes / 60.0);
            return BigDecimal.valueOf(50.0 * hours);
        }
        for (TariffRule rule : tariff.getRules()) {
            if (durationMinutes > rule.getMinMinutes() && durationMinutes <= rule.getMaxMinutes()) {
                return rule.getPrice();
            }
        }
        return BigDecimal.valueOf(100.0);
    }
    @Transactional
    public Reservation updateReservation(UUID id, ReservationRequest req, String email, boolean isAdmin) {
        Reservation r = reservationRepository.findById(id).orElseThrow();
        if (!isAdmin && !r.getUser().getEmail().equals(email)) throw new ConflictException("Yetkisiz işlem");
        if (!r.isActive()) throw new ConflictException("Pasif rezervasyon güncellenemez");

        long durationMinutes = Duration.between(req.getReservedStart(), req.getReservedEnd()).toMinutes();
        r.setReservedStart(req.getReservedStart());
        r.setReservedEnd(req.getReservedEnd());
        r.setTotalPrice(calculateDynamicPrice(r.getParkingSpot().getParkingLot().getTariff(), durationMinutes));
        return reservationRepository.save(r);
    }
    @Transactional
    public void completeReservation(UUID id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezervasyon bulunamadı: " + id));

        if (!r.isActive()) return;

        if (r.getTotalPrice().compareTo(BigDecimal.ZERO) > 0) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(r.getReservedEnd())) {
                long overstay = Duration.between(r.getReservedEnd(), now).toMinutes();
                if (overstay > 5) {
                    BigDecimal penalty = BigDecimal.valueOf(overstay * 2.0);
                    r.setTotalPrice(r.getTotalPrice().add(penalty));
                }
            }
        }
        r.setActive(false);
        reservationRepository.save(r);

        Map<String, Object> logData = new HashMap<>();
        logData.put("finalPrice", r.getTotalPrice());
        logService.saveLog("ReservationService", "EXIT_COMPLETED", "Çıkış Yapıldı", logData);
    }
    @Transactional
    public void cancelReservation(UUID id, String email, boolean isAdmin) {
        Reservation r = reservationRepository.findById(id).orElseThrow();
        if (!isAdmin && !r.getUser().getEmail().equals(email)) throw new ConflictException("Yetkisiz işlem");
        r.setActive(false);
        reservationRepository.save(r);
    }
    @Transactional
    public void confirmReservation(UUID id) {
        Reservation r = reservationRepository.findById(id).orElseThrow();
        r.setConfirmed(true);
        reservationRepository.save(r);
    }
    public List<Reservation> getReservationsByUserEmail(String email) {
        User u = userRepository.findByEmail(email).orElseThrow();
        return reservationRepository.findByUserId(u.getId());
    }
    public Reservation getReservationById(UUID id) {
        return reservationRepository.findById(id).orElseThrow();
    }
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCompleteExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> expiredReservations = reservationRepository.findAll().stream()
                .filter(r -> r.isActive() && r.getReservedEnd().isBefore(now))
                .toList();

        for (Reservation r : expiredReservations) {
            r.setActive(false);
            logService.saveLog("System", "AUTO_COMPLETE", "Süresi dolan rezervasyon kapatıldı: " + r.getId(), null);
        }

        if (!expiredReservations.isEmpty()) {
            reservationRepository.saveAll(expiredReservations);
            log.info("{} adet süresi dolmuş rezervasyon kapatıldı.", expiredReservations.size());
        }
    }
}