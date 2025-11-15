package com.akilliotopark.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRegisterRequest {

    @Email(message = "Geçerli bir e-posta giriniz.")
    @NotBlank(message = "E-posta zorunludur.")
    private String email;

    @NotBlank(message = "Şifre zorunludur.")
    private String password;

    @NotBlank(message = "İsim-soyisim zorunludur.")
    private String fullName;

    private String avatarImageName;
    private String district;
    private String province;
}
