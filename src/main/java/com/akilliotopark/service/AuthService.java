package com.akilliotopark.service;

import com.akilliotopark.dto.AuthLoginRequest;
import com.akilliotopark.dto.AuthRegisterRequest;
import com.akilliotopark.dto.AuthResponse;
import com.akilliotopark.entity.User;
import com.akilliotopark.entity.UserRole;
import com.akilliotopark.exception.ConflictException;
import com.akilliotopark.repository.UserRepository;
import com.akilliotopark.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(AuthRegisterRequest request) {

        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new ConflictException("Bu e-posta zaten kayıtlı: " + request.getEmail());
        });

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .avatarImageName(request.getAvatarImageName())
                .district(request.getDistrict())
                .province(request.getProvince())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.BASIC)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthLoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken(request.getEmail());
        return new AuthResponse(token);
    }
}
