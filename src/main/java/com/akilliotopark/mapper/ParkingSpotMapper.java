package com.akilliotopark.mapper;

import com.akilliotopark.dto.ParkingSpotRequest;
import com.akilliotopark.entity.ParkingSpot;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ParkingSpotMapper {
    ParkingSpot toEntity(ParkingSpotRequest request);
    ParkingSpotRequest toRequestDto(ParkingSpot entity);
}