package com.akilliotopark.repository;

import com.akilliotopark.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.parkingSpot.id = :spotId " +
            "AND r.active = true " +
            "AND (r.reservedStart < :end AND r.reservedEnd > :start)")
    List<Reservation> findOverlappingReservations(
            @Param("spotId") UUID spotId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT r FROM Reservation r WHERE r.active = true AND r.parkingSpot.type = 'EV_CHARGING'")
    List<Reservation> findActiveEvReservations();

    @Query("SELECT EXTRACT(MONTH FROM r.reservedStart) as month, SUM(r.totalPrice) as revenue " +
            "FROM Reservation r " +
            "WHERE r.active = false " +
            "GROUP BY EXTRACT(MONTH FROM r.reservedStart) " +
            "ORDER BY month ASC")
    List<Object[]> getMonthlyRevenue();

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.parkingSpot.parkingLot.id = :lotId " +
            "AND r.active = true " +
            "AND (r.reservedStart < :end AND r.reservedEnd > :start)")
    List<Reservation> findActiveReservationsInLot(
            @Param("lotId") UUID lotId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}