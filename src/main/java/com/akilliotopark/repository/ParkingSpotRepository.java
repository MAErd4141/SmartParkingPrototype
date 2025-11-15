package com.akilliotopark.repository;

import com.akilliotopark.entity.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, UUID> {

    ParkingSpot findBySpotCode(String spotCode);

    List<ParkingSpot> findByParkingLotId(UUID parkingLotId);
}
