package com.akilliotopark.mapper;

import com.akilliotopark.dto.VehicleRequest;
import com.akilliotopark.dto.VehicleResponse;
import com.akilliotopark.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {

    @Mapping(target = "owner", ignore = true)
    Vehicle toEntity(VehicleRequest request);

    @Mapping(source = "owner.id", target = "ownerId")
    VehicleResponse toResponseDto(Vehicle entity);

    List<VehicleResponse> toResponseDtoList(List<Vehicle> entityList);
}