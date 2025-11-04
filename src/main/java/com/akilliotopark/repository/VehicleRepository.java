package com.akilliotopark.repository;

import com.akilliotopark.entity.User;
import com.akilliotopark.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // <-- EKLENDİ

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Vehicle findByPlateNumber(String plateNumber);
    List<Vehicle> findByOwner(User owner); // Vehicle.owner ile eşleşir
}
