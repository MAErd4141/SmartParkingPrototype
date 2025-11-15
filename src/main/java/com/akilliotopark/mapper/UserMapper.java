package com.akilliotopark.mapper;

import com.akilliotopark.dto.UserRequest;
import com.akilliotopark.dto.UserResponse;
import com.akilliotopark.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", expression = "java(entity.getId() != null ? entity.getId().toString() : null)")
    @Mapping(
            target = "role",
            expression = "java(entity.getRole() != null ? entity.getRole().name().toLowerCase(java.util.Locale.ROOT) : null)"
    )
    UserResponse toResponseDto(User entity);

    List<UserResponse> toResponseDtoList(List<User> entityList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserRequest dto);
}
