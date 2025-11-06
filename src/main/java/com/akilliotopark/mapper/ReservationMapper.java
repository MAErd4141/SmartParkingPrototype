package com.akilliotopark.mapper;

import com.akilliotopark.dto.ReservationRequest;
import com.akilliotopark.dto.ReservationResponse;
import com.akilliotopark.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parkingSpot", ignore = true)
    Reservation toEntity(ReservationRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "parkingSpot.id", target = "parkingSpotId")
    ReservationResponse toResponseDto(Reservation entity);

    List<ReservationResponse> toResponseDtoList(List<Reservation> entityList);
}