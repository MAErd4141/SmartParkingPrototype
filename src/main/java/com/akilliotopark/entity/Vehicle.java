package com.akilliotopark.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles") // <--- BU SATIRI EKLE!
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String plateNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner; // <-- Repository'de findByOwner(...) ile eşleşir
}