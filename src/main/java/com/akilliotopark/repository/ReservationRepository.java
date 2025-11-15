package com.akilliotopark.repository;

import com.akilliotopark.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByParkingSpotIdAndReservedEndAfterAndReservedStartBefore(
            UUID parkingSpotId,
            LocalDateTime start,
            LocalDateTime end
    );
    List<Reservation> findByUserId(UUID userId);
}
