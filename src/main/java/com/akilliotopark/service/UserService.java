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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + id));
        return userMapper.toResponseDto(user);
    }

    public UserResponse createUser(UserRequest request) {

        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new ConflictException("Bu e-posta adresi zaten kullanımda: " + request.getEmail());
                });

        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        return userMapper.toResponseDto(saved);
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + id));

        // Eğer email değişiyorsa, çakışma kontrolü
        if (!existing.getEmail().equalsIgnoreCase(request.getEmail())) {
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(u -> {
                        throw new ConflictException("Bu e-posta adresi başka bir kullanıcıya ait: " + request.getEmail());
                    });
        }

        existing.setEmail(request.getEmail());
        existing.setFullName(request.getFullName());

        User saved = userRepository.save(existing);
        return userMapper.toResponseDto(saved);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Kullanıcı bulunamadı: " + id);
        }
        userRepository.deleteById(id);
    }

    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponseDto);
    }
}
