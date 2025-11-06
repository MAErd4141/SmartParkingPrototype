package com.akilliotopark.repository;

import com.akilliotopark.entity.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    ParkingSpot findBySpotCode(String spotCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ParkingSpot> findById(Long id);
}