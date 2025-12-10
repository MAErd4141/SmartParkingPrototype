package com.akilliotopark.repository;

import com.akilliotopark.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByUserId(UUID userId);
    @Query("SELECT r FROM Reservation r WHERE " +
            "r.active = true AND " +
            "(r.reservedStart < :endTime AND r.reservedEnd > :startTime)")
    List<Reservation> findConflictingReservations(@Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);
    @Query("SELECT r FROM Reservation r WHERE " +
            "r.parkingSpot.id = :spotId AND " +
            "r.active = true AND " +
            "(r.reservedStart < :endTime AND r.reservedEnd > :startTime)")
    List<Reservation> findOverlappingReservations(@Param("spotId") UUID spotId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);
    @Query("SELECT r FROM Reservation r WHERE " +
            "r.parkingSpot.parkingLot.id = :lotId AND " +
            "r.active = true AND " +
            "(r.reservedStart < :endTime AND r.reservedEnd > :startTime)")
    List<Reservation> findActiveReservationsInLot(@Param("lotId") UUID lotId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);
}