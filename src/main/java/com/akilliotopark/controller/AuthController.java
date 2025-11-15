package com.akilliotopark.controller;

import com.akilliotopark.dto.AuthLoginRequest;
import com.akilliotopark.dto.AuthRegisterRequest;
import com.akilliotopark.dto.AuthResponse;
import com.akilliotopark.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody AuthRegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthLoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}
