package com.akilliotopark.mapper;

import com.akilliotopark.dto.VehicleRequest;
import com.akilliotopark.dto.VehicleResponse;
import com.akilliotopark.entity.Vehicle;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    @Mapping(target = "ownerId", expression = "java(entity.getOwner().getId().toString())")
    @Mapping(target = "ownerEmail", source = "owner.email")
    @Mapping(target = "ownerFullName", source = "owner.fullName")
    VehicleResponse toResponseDto(Vehicle entity);

    List<VehicleResponse> toResponseDtoList(List<Vehicle> entityList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Vehicle toEntity(VehicleRequest request);

    default String map(UUID id) {
        return id != null ? id.toString() : null;
    }
}
