package com.akilliotopark.service;

import com.akilliotopark.dto.ReservationRequest;
import com.akilliotopark.entity.*;
import com.akilliotopark.exception.ConflictException;
import com.akilliotopark.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private UserRepository userRepository;
    @Mock private ParkingSpotRepository parkingSpotRepository;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private QrTokenService qrTokenService;
    @Mock private AsyncLogService logService;
    @Mock private SubscriptionService subscriptionService;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void createReservation_ShouldSuccess_WhenValidRequest() {
        String email = "test@user.com";
        UUID userId = UUID.randomUUID();
        UUID spotId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();

        User user = User.builder().id(userId).email(email).build();

        Tariff tariff = Tariff.builder().rules(Collections.emptyList()).build();
        ParkingLot lot = ParkingLot.builder().tariff(tariff).build();

        ParkingSpot spot = ParkingSpot.builder()
                .id(spotId)
                .parkingLot(lot)
                .type(SpotType.STANDARD)
                .build();

        Vehicle vehicle = Vehicle.builder()
                .id(vehicleId)
                .owner(user)
                .type(VehicleType.STANDARD)
                .plateNumber("34TEST34")
                .build();

        ReservationRequest request = new ReservationRequest();
        request.setParkingSpotId(spotId);
        request.setVehicleId(vehicleId);
        request.setReservedStart(LocalDateTime.now().plusHours(1));
        request.setReservedEnd(LocalDateTime.now().plusHours(3));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(spot));
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(reservationRepository.findOverlappingReservations(any(), any(), any())).thenReturn(Collections.emptyList());
        when(subscriptionService.hasActiveSubscription(any(), any())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);
        when(qrTokenService.generateToken(any(), any())).thenReturn("mock-qr-token");

        Reservation result = reservationService.createReservation(email, request);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals("mock-qr-token", result.getQrCode());
        assertTrue(result.getTotalPrice().compareTo(BigDecimal.ZERO) > 0);

        verify(logService).saveLog(anyString(), anyString(), anyString(), any());
    }

    @Test
    void createReservation_ShouldThrowConflict_WhenSpotOccupied() {
        String email = "test@user.com";
        UUID spotId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).email(email).build();
        ParkingSpot spot = ParkingSpot.builder().id(spotId).build();
        Vehicle vehicle = Vehicle.builder().id(vehicleId).owner(user).build();

        ReservationRequest request = new ReservationRequest();
        request.setParkingSpotId(spotId);
        request.setVehicleId(vehicleId);
        request.setReservedStart(LocalDateTime.now().plusHours(1));
        request.setReservedEnd(LocalDateTime.now().plusHours(2));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(spot));
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        when(reservationRepository.findOverlappingReservations(any(), any(), any()))
                .thenReturn(Collections.singletonList(new Reservation()));

        assertThrows(ConflictException.class, () -> reservationService.createReservation(email, request));

        verify(reservationRepository, never()).save(any());
    }
}