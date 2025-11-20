package com.akilliotopark.mapper;

import com.akilliotopark.dto.ParkingLotResponse;
import com.akilliotopark.entity.ParkingLot;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParkingLotMapper {

    ParkingLotResponse toResponseDto(ParkingLot entity);

    List<ParkingLotResponse> toResponseDtoList(List<ParkingLot> entities);
}
