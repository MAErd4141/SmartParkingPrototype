package com.akilliotopark.dto;

import lombok.Data;

@Data
public class UserResponse {

    private String id;
    private String email;
    private String fullName;
    private String avatarImageName;
    private String role;
    private String district;
    private String province;
}
