package com.akilliotopark.service;

import com.akilliotopark.dto.VehicleRequest;
import com.akilliotopark.dto.VehicleResponse;
import com.akilliotopark.entity.User;
import com.akilliotopark.entity.Vehicle;
import com.akilliotopark.entity.VehicleType;
import com.akilliotopark.exception.ConflictException;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.mapper.VehicleMapper;
import com.akilliotopark.repository.UserRepository;
import com.akilliotopark.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;

    public List<VehicleResponse> getAllVehicles() {
        return vehicleMapper.toResponseDtoList(vehicleRepository.findAll());
    }

    public VehicleResponse getVehicleById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new NotFoundException("Araç bulunamadı"));
        return vehicleMapper.toResponseDto(vehicle);
    }

    @Transactional
    public VehicleResponse createVehicleForUser(String ownerEmail, VehicleRequest request) {
        if (vehicleRepository.findByPlateNumber(request.getPlateNumber()) != null) {
            throw new ConflictException("Bu plaka zaten kayıtlı.");
        }

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı."));

        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber(request.getPlateNumber());
        vehicle.setOwner(owner);


        try {
            if (request.getType() != null && !request.getType().isEmpty()) {
                vehicle.setType(VehicleType.valueOf(request.getType().toUpperCase()));
            } else {
                vehicle.setType(VehicleType.STANDARD);
            }
        } catch (IllegalArgumentException e) {
            vehicle.setType(VehicleType.STANDARD);
        }

        return vehicleMapper.toResponseDto(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(UUID id) {
        Vehicle v = vehicleRepository.findById(id).orElseThrow(() -> new NotFoundException("Araç bulunamadı"));
        vehicleRepository.delete(v);
    }

    @Transactional
    public VehicleResponse updateVehicle(UUID id, VehicleRequest request) {
        Vehicle v = vehicleRepository.findById(id).orElseThrow(() -> new NotFoundException("Araç bulunamadı"));
        v.setPlateNumber(request.getPlateNumber());
        return vehicleMapper.toResponseDto(vehicleRepository.save(v));
    }

    public List<VehicleResponse> getVehiclesByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return vehicleMapper.toResponseDtoList(vehicleRepository.findByOwner(user));
    }
}