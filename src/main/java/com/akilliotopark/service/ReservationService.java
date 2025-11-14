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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
        long durationMinutes = ChronoUnit.MINUTES.between(
                request.getReservedStart(),
                request.getReservedEnd()
        );
        if (durationMinutes < 15) {
            throw new BusinessValidationException("Geçersiz zaman aralığı: Minimum rezervasyon süresi 15 dakikadır.");
        }

        Long userId = request.getUserId();
        Long spotId = request.getParkingSpotId();

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
    public void confirmReservation(Long id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezervasyon bulunamadı: " + id));
        r.setConfirmed(true);
        reservationRepository.save(r);
    }

    @Transactional
    public void completeReservation(Long id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezervasyon bulunamadı: " + id));
        r.setActive(false);
        reservationRepository.save(r);
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezervasyon bulunamadı: " + id));
        r.setActive(false);
        reservationRepository.save(r);
    }

    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getActiveReservationsAt(LocalDateTime now) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.isActive()
                        && (r.getReservedStart().isBefore(now) || r.getReservedStart().isEqual(now))
                        && (r.getReservedEnd().isAfter(now) || r.getReservedEnd().isEqual(now)))
                .toList();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezervasyon bulunamadı: " + id));
    }
}
