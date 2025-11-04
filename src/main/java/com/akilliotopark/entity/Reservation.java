package com.akilliotopark.entity;

import jakarta.persistence.*;
import lombok.*;

// ✅ JACKSON ANOTASYONU İÇİN EKLENDİ
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kullanıcı ilişkisi
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Park yeri ilişkisi
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parking_spot_id", nullable = false)
    private ParkingSpot parkingSpot;

    // Rezervasyon zamanları
    @Column(nullable = false)
    // ✅ DÜZELTME: JSON okuma formatı zorunlu kılındı
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reservedStart;

    @Column(nullable = false)
    // ✅ DÜZELTME: JSON okuma formatı zorunlu kılındı
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reservedEnd;

    // QR token (JWT string) – uzun olabildiği için length geniş
    @Column(name = "qr_code", length = 1024)
    private String qrCode;

    // Durum alanları
    @Column(nullable = false)
    private boolean active = true; // true = aktif rezervasyon

    @Column(nullable = false)
    private boolean confirmed = false; // true = kullanıcı giriş için onayladı
}