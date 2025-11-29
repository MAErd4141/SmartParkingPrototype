package com.akilliotopark.repository;

import com.akilliotopark.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, UUID> {

    List<ParkingLot> findByDistrictAndProvince(String district, String province);
}
