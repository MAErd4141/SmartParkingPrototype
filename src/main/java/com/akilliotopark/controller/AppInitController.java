package com.akilliotopark.controller;

import com.akilliotopark.dto.AppInitResponse;
import com.akilliotopark.dto.UserProfileDto;
import com.akilliotopark.entity.User;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppInitController {

    private final UserRepository userRepository;

    @GetMapping("/init/{userId}")
    public AppInitResponse init(@PathVariable UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + userId));

        UserProfileDto profile = UserProfileDto.builder()
                .userID(user.getId().toString())
                .fullName(user.getFullName())
                .avatarImageName(user.getAvatarImageName())
                .role(user.getRole().name().toLowerCase()) // "basic"
                .build();

        return AppInitResponse.builder()
                .version(1)
                .user(profile)
                .district(user.getDistrict())
                .province(user.getProvince())
                .build();
    }
}
