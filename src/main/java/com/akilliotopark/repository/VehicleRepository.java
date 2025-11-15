package com.akilliotopark.repository;

import com.akilliotopark.entity.User;
import com.akilliotopark.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Vehicle findByPlateNumber(String plateNumber);

    List<Vehicle> findByOwner(User owner);
}
