package com.akilliotopark.mapper;

import com.akilliotopark.dto.ParkingSpotRequest;
import com.akilliotopark.entity.ParkingSpot;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ParkingSpotMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parkingLot", ignore = true)
    @Mapping(target = "occupied", expression = "java(request.getOccupied() != null && request.getOccupied())")
    ParkingSpot toEntity(ParkingSpotRequest request);
}
