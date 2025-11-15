package com.akilliotopark.service;

import com.akilliotopark.dto.VehicleRequest;
import com.akilliotopark.dto.VehicleResponse;
import com.akilliotopark.entity.User;
import com.akilliotopark.entity.Vehicle;
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
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicleMapper.toResponseDtoList(vehicles);
    }

    public VehicleResponse getVehicleById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Araç bulunamadı: " + id));
        return vehicleMapper.toResponseDto(vehicle);
    }
    @Transactional
    public VehicleResponse createVehicle(String ownerEmail, VehicleRequest request) {
        // Plaka benzersiz olsun
        Vehicle exists = vehicleRepository.findByPlateNumber(request.getPlateNumber());
        if (exists != null) {
            throw new ConflictException("Bu plaka zaten kayıtlı: " + request.getPlateNumber());
        }

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + ownerEmail));

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setOwner(owner);

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }
    @Transactional
    public VehicleResponse createVehicle(VehicleRequest request) {
        Vehicle exists = vehicleRepository.findByPlateNumber(request.getPlateNumber());
        if (exists != null) {
            throw new ConflictException("Bu plaka zaten kayıtlı: " + request.getPlateNumber());
        }

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + request.getOwnerId()));

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setOwner(owner);

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }
    @Transactional
    public VehicleResponse updateVehicle(UUID id, VehicleRequest request) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Araç bulunamadı: " + id));
        if (!existing.getPlateNumber().equals(request.getPlateNumber())) {
            Vehicle exists = vehicleRepository.findByPlateNumber(request.getPlateNumber());
            if (exists != null && !exists.getId().equals(id)) {
                throw new ConflictException("Bu plaka zaten kullanımda: " + request.getPlateNumber());
            }
        }

        existing.setPlateNumber(request.getPlateNumber());

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + request.getOwnerId()));
        existing.setOwner(owner);

        Vehicle saved = vehicleRepository.save(existing);
        return vehicleMapper.toResponseDto(saved);
    }
    @Transactional
    public void deleteVehicle(UUID id) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Araç bulunamadı: " + id));
        vehicleRepository.delete(existing);
    }
    public List<VehicleResponse> getVehiclesByUser(UUID userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + userId));

        List<Vehicle> vehicles = vehicleRepository.findByOwner(owner);
        return vehicleMapper.toResponseDtoList(vehicles);
    }
    public List<VehicleResponse> getVehiclesByEmail(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + email));

        List<Vehicle> vehicles = vehicleRepository.findByOwner(owner);
        return vehicleMapper.toResponseDtoList(vehicles);
    }
    public VehicleResponse getVehicleByPlate(String plate) {
        Vehicle vehicle = vehicleRepository.findByPlateNumber(plate);
        if (vehicle == null) {
            throw new NotFoundException("Bu plakaya ait araç bulunamadı: " + plate);
        }
        return vehicleMapper.toResponseDto(vehicle);
    }
}
