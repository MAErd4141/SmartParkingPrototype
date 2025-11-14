package com.akilliotopark.controller;

import com.akilliotopark.dto.ReservationRequest;
import com.akilliotopark.dto.ReservationResponse;
import com.akilliotopark.entity.Reservation;
import com.akilliotopark.mapper.ReservationMapper;
import com.akilliotopark.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        List<ReservationResponse> responseList = reservationMapper.toResponseDtoList(reservations);
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        ReservationResponse response = reservationMapper.toResponseDto(reservation);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByUser(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getReservationsByUser(userId);
        List<ReservationResponse> responseList = reservationMapper.toResponseDtoList(reservations);
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ReservationResponse>> getActiveReservations(
            @RequestParam(value = "at", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime at) {

        LocalDateTime queryTime = (at != null) ? at : LocalDateTime.now();
        List<Reservation> reservations = reservationService.getActiveReservationsAt(queryTime);
        List<ReservationResponse> responseList = reservationMapper.toResponseDtoList(reservations);
        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        Reservation created = reservationService.createReservation(request);
        ReservationResponse response = reservationMapper.toResponseDto(created);
        return ResponseEntity
                .created(URI.create("/api/reservations/" + created.getId()))
                .body(response);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmReservation(@PathVariable Long id) {
        reservationService.confirmReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeReservation(@PathVariable Long id) {
        reservationService.completeReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
