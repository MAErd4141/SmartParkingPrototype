package com.akilliotopark.service;

import com.akilliotopark.dto.UserRequest;
import com.akilliotopark.dto.UserResponse;
import com.akilliotopark.entity.User;
import com.akilliotopark.exception.ConflictException;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.mapper.UserMapper;
import com.akilliotopark.repository.UserRepository;
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

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseDtoList(users);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Bu e-posta ile kullanıcı bulunamadı: " + email));
        return userMapper.toResponseDto(user);
    }
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + id));
        return userMapper.toResponseDto(user);
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new ConflictException("Bu e-posta zaten kayıtlı: " + request.getEmail());
        });

        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        return userMapper.toResponseDto(saved);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + id));

        if (!existing.getEmail().equals(request.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
                throw new ConflictException("Bu e-posta zaten kullanımda: " + request.getEmail());
            });
        }

        existing.setEmail(request.getEmail());
        existing.setFullName(request.getFullName());
        existing.setAvatarImageName(request.getAvatarImageName());
        existing.setDistrict(request.getDistrict());
        existing.setProvince(request.getProvince());

        User saved = userRepository.save(existing);
        return userMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + id));
        userRepository.delete(existing);
    }
}
