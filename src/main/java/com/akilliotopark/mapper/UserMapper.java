package com.akilliotopark.mapper;

import com.akilliotopark.dto.UserResponse;
import com.akilliotopark.dto.UserRequest;
import com.akilliotopark.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponse toResponseDto(User entity);
    User toEntity(UserResponse dto);
    User toEntity(UserRequest dto);
}