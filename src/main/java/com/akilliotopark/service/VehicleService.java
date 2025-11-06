package com.akilliotopark.service;

import com.akilliotopark.dto.VehicleRequest;
import com.akilliotopark.dto.VehicleResponse;
import com.akilliotopark.entity.User;
import com.akilliotopark.entity.Vehicle;
import com.akilliotopark.mapper.VehicleMapper;
import com.akilliotopark.repository.UserRepository;
import com.akilliotopark.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;

    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public VehicleResponse saveVehicle(VehicleRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Araç sahibi kullanıcı bulunamadı: " + request.getOwnerId()));

        Vehicle vehicle = vehicleMapper.toEntity(request);

        vehicle.setOwner(owner);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(savedVehicle);
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public List<VehicleResponse> getVehiclesByUserId(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        return userOpt.map(user -> vehicleRepository.findByOwner(user).stream()
                        .map(vehicleMapper::toResponseDto)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}