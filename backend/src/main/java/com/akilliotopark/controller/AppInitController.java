package com.akilliotopark.controller;

import com.akilliotopark.dto.AppInitResponse;
import com.akilliotopark.dto.UserProfileDto;
import com.akilliotopark.entity.User;
import com.akilliotopark.exception.NotFoundException;
import com.akilliotopark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppInitController {

    private final UserRepository userRepository;

    @GetMapping("/init")
    public AppInitResponse init(Authentication auth) {
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı: " + email));

        UserProfileDto profile = UserProfileDto.builder()
                .userID(user.getId().toString())
                .fullName(user.getFullName())
                .avatarImageName(user.getAvatarImageName())
                .role(user.getRole().name().toLowerCase())
                .build();

        return AppInitResponse.builder()
                .version(1)
                .user(profile)
                .district(user.getDistrict())
                .province(user.getProvince())
                .build();
    }
}