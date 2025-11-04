package com.akilliotopark.service;

import com.akilliotopark.entity.Reservation;
import com.akilliotopark.entity.User;
import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.repository.ReservationRepository;
import com.akilliotopark.repository.UserRepository;
import com.akilliotopark.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit; // ✅ ChronoUnit import edildi!
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final QrTokenService qrTokenService;

    /**
     * 🔹 Tüm rezervasyonları döndürür
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * 🔹 Yeni rezervasyon oluşturur + QR token üretir
     */
    public Reservation createReservation(Reservation reservation) {
        // 🔸 Zaman kontrolü - 1: Boş kontrolü
        if (reservation.getReservedStart() == null || reservation.getReservedEnd() == null) {
            throw new IllegalArgumentException("Geçersiz zaman aralığı: Başlangıç ve Bitiş saatleri boş bırakılamaz.");
        }

        // 🔸 Zaman kontrolü - 2: Bitiş, başlangıçtan önce olamaz
        if (!reservation.getReservedEnd().isAfter(reservation.getReservedStart())) {
            throw new IllegalArgumentException("Geçersiz zaman aralığı: Bitiş saati, Başlangıç saatinden önce olamaz.");
        }

        // 🔸 Zaman kontrolü - 3: Minimum süre kuralı (15 dakika) - ✅ EKLENDİ
        long durationMinutes = ChronoUnit.MINUTES.between(
                reservation.getReservedStart(),
                reservation.getReservedEnd()
        );

        if (durationMinutes < 15) {
            throw new IllegalArgumentException("Geçersiz zaman aralığı: Minimum rezervasyon süresi 15 dakikadır.");
        }

        // 🔸 Kullanıcı ve park yeri doğrulama
        Long userId = reservation.getUser().getId();
        Long spotId = reservation.getParkingSpot().getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        ParkingSpot spot = parkingSpotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("Park alanı bulunamadı: " + spotId));

        // 🔸 Aynı zaman aralığında başka rezervasyon var mı?
        boolean hasOverlap = !reservationRepository
                .findByParkingSpotIdAndReservedEndAfterAndReservedStartBefore(
                        spotId,
                        reservation.getReservedStart(),
                        reservation.getReservedEnd()
                ).isEmpty();

        if (hasOverlap) {
            throw new RuntimeException("Bu zaman aralığında park alanı zaten rezerve edilmiş.");
        }

        // 🔸 Bilgileri atayıp rezervasyonu aktif hale getir
        reservation.setUser(user);
        reservation.setParkingSpot(spot);
        reservation.setActive(true);
        reservation.setConfirmed(false);

        // 🔸 Kaydı ilk defa DB'ye yaz
        Reservation saved = reservationRepository.save(reservation);

        // ✅ QR token üret
        String qrToken = qrTokenService.generateToken(saved.getId(), user.getEmail());
        saved.setQrCode(qrToken);

        // 🔸 QR eklendikten sonra tekrar kaydet
        return reservationRepository.save(saved);
    }

    /**
     * 🔹 Rezervasyonu onaylar (admin veya sistem tarafından)
     */
    public void confirmReservation(Long id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı: " + id));
        r.setConfirmed(true);
        reservationRepository.save(r);
    }

    /**
     * 🔹 Rezervasyonu tamamlanmış olarak işaretler
     */
    public void completeReservation(Long id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı: " + id));
        r.setActive(false);
        reservationRepository.save(r);
    }

    /**
     * 🔹 Rezervasyonu iptal eder
     */
    public void cancelReservation(Long id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı: " + id));
        r.setActive(false);
        reservationRepository.save(r);
    }

    /**
     * 🔹 Belirli bir kullanıcının tüm rezervasyonlarını döndürür
     */
    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    /**
     * 🔹 Şu anda aktif rezervasyonları listeler (opsiyonel)
     */
    public List<Reservation> getActiveReservationsAt(LocalDateTime now) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.isActive()
                        && (r.getReservedStart().isBefore(now) || r.getReservedStart().isEqual(now))
                        && (r.getReservedEnd().isAfter(now) || r.getReservedEnd().isEqual(now)))
                .toList();
    }
}