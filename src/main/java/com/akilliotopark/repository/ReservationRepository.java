package com.akilliotopark.repository;

import com.akilliotopark.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.parkingSpot ps " +
            "JOIN FETCH ps.parkingLot pl " +
            "JOIN FETCH r.user u " +
            "WHERE u.id = :userId")
    List<Reservation> findByUserId(@Param("userId") UUID userId);
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.parkingSpot.id = :spotId " +
            "AND r.active = true " +
            "AND (r.reservedStart < :end AND r.reservedEnd > :start)")
    List<Reservation> findOverlappingReservations(
            @Param("spotId") UUID spotId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}