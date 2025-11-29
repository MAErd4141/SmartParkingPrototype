package com.akilliotopark.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {

    @Email(message = "Geçerli bir e-posta adresi giriniz.")
    @NotBlank(message = "E-posta boş bırakılamaz.")
    private String email;

    @NotBlank(message = "Tam ad boş bırakılamaz.")
    private String fullName;

    private String avatarImageName;

    private String district;
    private String province;
}
