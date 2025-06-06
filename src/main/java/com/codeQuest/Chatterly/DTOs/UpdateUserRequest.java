package com.codeQuest.Chatterly.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @Size(min = 8, message = "Password must be at least 8 characters")
    String password
) {}