package com.akilliotopark.controller;

import com.akilliotopark.dto.ReservationRequest;
import com.akilliotopark.dto.ReservationResponse;
import com.akilliotopark.entity.Reservation;
import com.akilliotopark.mapper.ReservationMapper;
import com.akilliotopark.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @PreAuthorize("hasAnyRole('BASIC','ADMIN')")
    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(Authentication auth) {
        List<Reservation> reservations =
                reservationService.getReservationsByUserEmail(auth.getName());

        return ResponseEntity.ok(
                reservationMapper.toResponseDtoList(reservations)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservationMapper.toResponseDtoList(reservations));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable UUID id) {
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationMapper.toResponseDto(reservation));
    }
    @PreAuthorize("hasAnyRole('BASIC','ADMIN')")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            Authentication auth,
            @Valid @RequestBody ReservationRequest request
    ) {
        Reservation created = reservationService.createReservation(auth.getName(), request);

        return ResponseEntity.ok(reservationMapper.toResponseDto(created));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable UUID id) {
        reservationService.confirmReservation(id);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable UUID id) {
        reservationService.completeReservation(id);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasAnyRole('BASIC','ADMIN')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }
}