package com.akilliotopark.mapper;

import com.akilliotopark.dto.ReservationResponse;
import com.akilliotopark.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "parkingSpot.id", target = "parkingSpotId")
    @Mapping(source = "parkingSpot.parkingLot.id", target = "parkingLotId")
    @Mapping(source = "parkingSpot.parkingLot.name", target = "parkingLotName")
    @Mapping(source = "parkingSpot.spotCode", target = "parkingSpotCode")
    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "vehicle.plateNumber", target = "vehiclePlate")

    ReservationResponse toResponseDto(Reservation reservation);

    List<ReservationResponse> toResponseDtoList(List<Reservation> reservations);
}