package com.akilliotopark.controller;

import com.akilliotopark.dto.ReservationRequest;
import com.akilliotopark.dto.ReservationResponse;
import com.akilliotopark.mapper.ReservationMapper;
import com.akilliotopark.service.ReservationService;
import com.akilliotopark.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return reservationMapper.toResponseDtoList(reservations);
    }

    @GetMapping("/{id}")
    public ReservationResponse getReservationById(@PathVariable Long id) {
        Reservation entity = reservationService.getReservationById(id);
        return reservationMapper.toResponseDto(entity);
    }

    @PostMapping
    public ReservationResponse createReservation(@RequestBody ReservationRequest reservationRequest) {
        Reservation entity = reservationService.createReservation(reservationRequest);
        return reservationMapper.toResponseDto(entity);
    }

    @PostMapping("/{id}/confirm")
    public void confirmReservation(@PathVariable Long id) {
        reservationService.confirmReservation(id);
    }

    @PostMapping("/{id}/complete")
    public void completeReservation(@PathVariable Long id) {
        reservationService.completeReservation(id);
    }

    @PostMapping("/{id}/cancel")
    public void cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
    }

    @GetMapping("/user/{userId}")
    public List<ReservationResponse> getReservationsByUser(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getReservationsByUser(userId);
        return reservationMapper.toResponseDtoList(reservations);
    }
}