package com.akilliotopark.mapper;

import com.akilliotopark.dto.ReservationResponse;
import com.akilliotopark.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "id", expression = "java(reservation.getId().toString())")
    @Mapping(target = "userId", expression = "java(reservation.getUser().getId().toString())")
    @Mapping(target = "parkingSpotId", expression = "java(reservation.getParkingSpot().getId().toString())")
    @Mapping(target = "parkingLotId", expression = "java(reservation.getParkingSpot().getParkingLot().getId().toString())")
    @Mapping(target = "parkingLotName", expression = "java(reservation.getParkingSpot().getParkingLot().getName())")
    @Mapping(target = "parkingSpotCode", expression = "java(reservation.getParkingSpot().getSpotCode())")
    @Mapping(target = "reservedStart", expression = "java(reservation.getReservedStart().toString())")
    @Mapping(target = "reservedEnd", expression = "java(reservation.getReservedEnd().toString())")
    ReservationResponse toResponseDto(Reservation reservation);

    List<ReservationResponse> toResponseDtoList(List<Reservation> reservations);

    default String map(UUID id) {
        return id != null ? id.toString() : null;
    }
}
