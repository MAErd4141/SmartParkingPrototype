package com.akilliotopark.mapper;

import com.akilliotopark.dto.VehicleRequest;
import com.akilliotopark.dto.VehicleResponse;
import com.akilliotopark.entity.Vehicle;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    @Mapping(target = "ownerId", expression = "java(entity.getOwner().getId().toString())")
    @Mapping(target = "ownerEmail", source = "owner.email")
    @Mapping(target = "ownerFullName", source = "owner.fullName")
    @Mapping(target = "type", expression = "java(entity.getType().name())") // Enum -> String
    VehicleResponse toResponseDto(Vehicle entity);

    List<VehicleResponse> toResponseDtoList(List<Vehicle> entityList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "type", ignore = true)
    Vehicle toEntity(VehicleRequest request);
}