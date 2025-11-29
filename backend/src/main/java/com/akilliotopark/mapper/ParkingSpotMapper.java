package com.akilliotopark.mapper;

import com.akilliotopark.dto.ParkingSpotRequest;
import com.akilliotopark.dto.ParkingSpotResponse;
import com.akilliotopark.entity.ParkingSpot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ParkingSpotMapper {

    @Mapping(target = "parkingLotId", source = "parkingLot.id")
    @Mapping(target = "type", expression = "java(entity.getType().name())")
    ParkingSpotResponse toResponseDto(ParkingSpot entity);

    List<ParkingSpotResponse> toResponseDtoList(List<ParkingSpot> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parkingLot", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "hasCharger", ignore = true)
    ParkingSpot toEntity(ParkingSpotRequest request);
}