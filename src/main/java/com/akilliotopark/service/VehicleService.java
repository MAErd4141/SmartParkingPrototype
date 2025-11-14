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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;

    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Araç bulunamadı: " + id));
        return vehicleMapper.toResponseDto(vehicle);
    }

    public VehicleResponse saveVehicle(VehicleRequest vehicleRequest) {
        Vehicle existingByPlate = vehicleRepository.findByPlateNumber(vehicleRequest.getPlateNumber());
        if (existingByPlate != null) {
            throw new ConflictException("Bu plaka numarası zaten kayıtlı: " + vehicleRequest.getPlateNumber());
        }
        User owner = userRepository.findById(vehicleRequest.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Araç sahibi kullanıcı bulunamadı: " + vehicleRequest.getOwnerId()));

        Vehicle vehicle = vehicleMapper.toEntity(vehicleRequest);
        vehicle.setOwner(owner);

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }

    public VehicleResponse updateVehicle(Long id, VehicleRequest vehicleRequest) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Araç bulunamadı: " + id));
        Vehicle byPlate = vehicleRepository.findByPlateNumber(vehicleRequest.getPlateNumber());
        if (byPlate != null && !byPlate.getId().equals(id)) {
            throw new ConflictException("Bu plaka numarası başka bir araca ait: " + vehicleRequest.getPlateNumber());
        }

        User owner = userRepository.findById(vehicleRequest.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Araç sahibi kullanıcı bulunamadı: " + vehicleRequest.getOwnerId()));

        existing.setPlateNumber(vehicleRequest.getPlateNumber());
        existing.setOwner(owner);

        Vehicle saved = vehicleRepository.save(existing);
        return vehicleMapper.toResponseDto(saved);
    }

    public List<VehicleResponse> getVehiclesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + userId));

        return vehicleRepository.findByOwner(user).stream()
                .map(vehicleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new NotFoundException("Araç bulunamadı: " + id);
        }
        vehicleRepository.deleteById(id);
    }
}
