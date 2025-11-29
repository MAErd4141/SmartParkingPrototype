package com.akilliotopark.controller;

import com.akilliotopark.dto.DashboardStatsResponse;
import com.akilliotopark.repository.ParkingLotRepository;
import com.akilliotopark.repository.ReservationRepository; // Repository eklendi
import com.akilliotopark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ParkingLotRepository parkingLotRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalLots = parkingLotRepository.count();
        long totalReservations = reservationRepository.count();
        double estimatedRevenue = totalReservations * 50.0;

        long todaysReservations = 0;

        return ResponseEntity.ok(DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalParkingLots(totalLots)
                .totalRevenue(estimatedRevenue)
                .todaysReservations(todaysReservations)
                .build());
    }
}