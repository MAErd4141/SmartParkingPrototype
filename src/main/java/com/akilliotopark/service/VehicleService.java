package com.akilliotopark.service;

import com.akilliotopark.entity.Vehicle;
import com.akilliotopark.entity.User;
import com.akilliotopark.repository.VehicleRepository;
import com.akilliotopark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    /** 🔹 Tüm araçları getirir */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /** 🔹 ID ile araç bulur */
    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    /** 🔹 Yeni araç oluşturur */
    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    /** 🔹 Belirli bir kullanıcıya ait araçları getirir */
    public List<Vehicle> getVehiclesByUserId(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(vehicleRepository::findByOwner).orElse(List.of());
    }

    /** 🔹 Araç silme işlemi */
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}
