package com.akilliotopark.service;

import com.akilliotopark.dto.ReservationRequest;
import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.entity.Reservation;
import com.akilliotopark.entity.User;
import com.akilliotopark.exception.BusinessValidationException;
import com.akilliotopark.exception.ConflictException;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.repository.ParkingSpotRepository;
import com.akilliotopark.repository.ReservationRepository;
import com.akilliotopark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final QrTokenService qrTokenService;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional
    public Reservation createReservation(ReservationRequest request) {

        if (request.getReservedStart() == null || request.getReservedEnd() == null) {
            throw new BusinessValidationException("Geçersiz zaman aralığı: Başlangıç ve bitiş saatleri boş bırakılamaz.");
        }

        if (!request.getReservedEnd().isAfter(request.getReservedStart())) {
            throw new BusinessValidationException("Geçersiz zaman aralığı: Bitiş saati, başlangıç saatinden sonra olmalıdır.");
        }

        // ChronoUnit yerine Duration kullanıyoruz
        long durationMinutes = Duration.between(
                request.getReservedStart(),
                request.getReservedEnd()
        ).toMinutes();

        if (durationMinutes < 15) {
            throw new BusinessValidationException("Geçersiz zaman aralığı: Minimum rezervasyon süresi 15 dakikadır.");
        }

        UUID userId = request.getUserId();
        UUID spotId = request.getParkingSpotId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + userId));

        ParkingSpot spot = parkingSpotRepository.findById(spotId)
                .orElseThrow(() -> new NotFoundException("Park alanı bulunamadı: " + spotId));

        boolean hasOverlap = !reservationRepository
                .findByParkingSpotIdAndReservedEndAfterAndReservedStartBefore(
                        spotId,
                        request.getReservedStart(),
                        request.getReservedEnd()
                ).isEmpty();

        if (hasOverlap) {
            throw new ConflictException("Bu zaman aralığında park alanı zaten rezerve edilmiş.");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .parkingSpot(spot)
                .reservedStart(request.getReservedStart())
                .reservedEnd(request.getReservedEnd())
                .active(request.isActive())
                .confirmed(request.isConfirmed())
                .build();

        Reservation saved = reservationRepository.save(reservation);
        String qrToken = qrTokenService.generateToken(saved.getId(), user.getEmail());
        saved.setQrCode(qrToken);

        return reservationRepository.save(saved);
    }
    @Transactional
    public void confirmReservation(UUID id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezervasyon bulunamadı: " + id));
        r.setConfirmed(true);
        reservationRepository.save(r);
    }

    @Transactional
    public void completeReservation(UUID id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezervasyon bulunamadı: " + id));
        r.setActive(false);
        reservationRepository.save(r);
    }

    @Transactional
    public void cancelReservation(UUID id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezervasyon bulunamadı: " + id));
        r.setActive(false);
        reservationRepository.save(r);
    }

    public List<Reservation> getReservationsByUser(UUID userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getActiveReservationsAt(LocalDateTime now) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.isActive()
                        && (r.getReservedStart().isBefore(now) || r.getReservedStart().isEqual(now))
                        && (r.getReservedEnd().isAfter(now) || r.getReservedEnd().isEqual(now)))
                .toList();
    }

    public Reservation getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezervasyon bulunamadı: " + id));
    }
    public List<Reservation> getReservationsByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + email));
        return reservationRepository.findByUserId(user.getId());
    }
}
