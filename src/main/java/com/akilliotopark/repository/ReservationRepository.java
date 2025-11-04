package com.akilliotopark.repository;

import com.akilliotopark.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Aynı park yerinde çakışan rezervasyon var mı?
    List<Reservation> findByParkingSpotIdAndReservedEndAfterAndReservedStartBefore(
            Long parkingSpotId,
            LocalDateTime start,
            LocalDateTime end
    );

    // Kullanıcıya göre liste
    List<Reservation> findByUserId(Long userId);
}
