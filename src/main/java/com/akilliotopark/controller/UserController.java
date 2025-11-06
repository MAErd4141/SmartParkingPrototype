// src/main/java/com/akilliotopark/controller/UserController.java
package com.akilliotopark.controller;

import com.akilliotopark.dto.UserRequest;
import com.akilliotopark.dto.UserResponse;
import com.akilliotopark.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest userRequest) {
        return userService.saveUser(userRequest);
    }
}