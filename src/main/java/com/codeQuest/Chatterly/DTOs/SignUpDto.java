package com.codeQuest.Chatterly.DTOs;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUpDto {
    @NotBlank(message = "Username is required")
    @Size(min = 2, message = "User name must be 2 or more characters")
    private String userName;

    @NotNull(message = "Phone Number is required")
    @Pattern(regexp = "^(\\+233|0)[2-5]\\d{8}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;


    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String confirmPassword;

}

