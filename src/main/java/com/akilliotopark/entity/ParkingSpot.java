package com.akilliotopark.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_spots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String spotCode; // Örn: A1, B5, C2

    @Column(nullable = false)
    private boolean occupied; // true = dolu, false = boş

    public boolean isOccupied() {
        return occupied;
    }
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

}
