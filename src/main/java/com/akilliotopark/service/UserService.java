package com.akilliotopark.service;

import com.akilliotopark.entity.User;
import com.akilliotopark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /** 🔹 Tüm kullanıcıları getirir */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** 🔹 ID ile kullanıcı bulur */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /** 🔹 Yeni kullanıcı oluşturur veya mevcut kullanıcıyı günceller */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /** 🔹 Kullanıcıyı siler */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /** 🔹 Email’e göre kullanıcı arar (login için kullanılabilir) */
    public Optional<User> findByEmail(String email) {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
