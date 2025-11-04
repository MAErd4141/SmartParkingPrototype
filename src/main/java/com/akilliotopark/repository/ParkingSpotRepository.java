package com.akilliotopark.repository;

import com.akilliotopark.entity.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    ParkingSpot findBySpotCode(String spotCode);


}
