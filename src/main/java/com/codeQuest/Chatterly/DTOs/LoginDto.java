package com.codeQuest.Chatterly.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginDto {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Phone Number is required")
    @Pattern(regexp = "^(\\+233|0)[2-5]\\d{8}$", message = "Invalid phone number format")
    private String phoneNumber;


    @NotBlank(message = "Password is required")
    private String password;
}