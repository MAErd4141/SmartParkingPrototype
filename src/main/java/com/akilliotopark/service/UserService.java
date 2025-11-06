package com.akilliotopark.service;

import com.akilliotopark.dto.UserRequest;
import com.akilliotopark.dto.UserResponse;
import com.akilliotopark.entity.User;
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

    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponseDto);
    }

    public UserResponse saveUser(UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);

        User savedUser = userRepository.save(user);

        return userMapper.toResponseDto(savedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .map(userMapper::toResponseDto);
    }
}