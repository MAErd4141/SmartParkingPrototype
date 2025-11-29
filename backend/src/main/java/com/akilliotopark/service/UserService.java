package com.akilliotopark.service;

import com.akilliotopark.dto.UserRequest;
import com.akilliotopark.dto.UserResponse;
import com.akilliotopark.entity.User;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.mapper.UserMapper;
import com.akilliotopark.repository.ReservationRepository;
import com.akilliotopark.repository.UserRepository;
import com.akilliotopark.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;

    public List<UserResponse> getAllUsers() { return userMapper.toResponseDtoList(userRepository.findAll()); }
    public UserResponse getUserByEmail(String email) { return userMapper.toResponseDto(userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Kullanıcı yok"))); }
    public UserResponse getUserById(UUID id) { return userMapper.toResponseDto(userRepository.findById(id).orElseThrow()); }

    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Kullanıcı yok"));

        reservationRepository.deleteAll(reservationRepository.findByUserId(user.getId()));
        vehicleRepository.deleteAll(vehicleRepository.findByOwner(user));

        userRepository.delete(user);
    }

    @Transactional
    public void deleteUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow();
        deleteUserByEmail(user.getEmail());
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserRequest request) {
        User user = userRepository.findById(id).orElseThrow();
        user.setFullName(request.getFullName());
        user.setDistrict(request.getDistrict());
        user.setProvince(request.getProvince());
        return userMapper.toResponseDto(userRepository.save(user));
    }
}