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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redisTemplate;
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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new org.springframework.security.authentication.BadCredentialsException("Kullanıcı veya şifre hatalı"));
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        System.out.println("DEBUG LOGIN >> email=" + request.getEmail()
                + " raw=" + request.getPassword()
                + " hash=" + user.getPassword()
                + " length=" + user.getPassword().length()
                + " matches=" + matches);

        if (!matches) {
            throw new org.springframework.security.authentication.BadCredentialsException("Kullanıcı veya şifre hatalı");
        }
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token != null) {
            redisTemplate.opsForValue().set(token, "logout", Duration.ofSeconds(3600));
        }
    }
}
