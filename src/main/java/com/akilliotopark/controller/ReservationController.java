package com.akilliotopark.controller;

import com.akilliotopark.entity.Reservation;
import com.akilliotopark.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationService.createReservation(reservation);
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
    public List<Reservation> getReservationsByUser(@PathVariable Long userId) {
        return reservationService.getReservationsByUser(userId);
    }
}
