package com.akilliotopark.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDto {

    private String userID;
    private String fullName;
    private String avatarImageName;
    private String role;
}
