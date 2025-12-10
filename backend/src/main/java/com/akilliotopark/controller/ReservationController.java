package com.akilliotopark.controller;

import com.akilliotopark.dto.ReservationRequest;
import com.akilliotopark.dto.ReservationResponse;
import com.akilliotopark.mapper.ReservationMapper;
import com.akilliotopark.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService service;
    private final ReservationMapper mapper;

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMy(Authentication auth) {
        return ResponseEntity.ok(mapper.toResponseDtoList(service.getReservationsByUserEmail(auth.getName())));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAll() {
        return ResponseEntity.ok(mapper.toResponseDtoList(service.getAllReservations()));
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(Authentication auth, @Valid @RequestBody ReservationRequest req) {
        return ResponseEntity.ok(mapper.toResponseDto(service.createReservation(auth.getName(), req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> update(@PathVariable UUID id, @RequestBody ReservationRequest req, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(mapper.toResponseDto(service.updateReservation(id, req, auth.getName(), isAdmin)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable UUID id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        service.cancelReservation(id, auth.getName(), isAdmin);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable UUID id) {
        service.completeReservation(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/unavailable")
    public ResponseEntity<List<ReservationResponse>> getUnavailableSpots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    )
    {
        return ResponseEntity.ok(mapper.toResponseDtoList(service.findConflictingReservations(startTime, endTime)));
    }
}