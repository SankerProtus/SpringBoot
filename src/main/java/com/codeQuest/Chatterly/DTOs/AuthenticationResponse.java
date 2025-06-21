package com.codeQuest.Chatterly.DTOs;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;

//    @Column(nullable = false, unique = true)
//    private String username;
//
//    @Email
//    @NotBlank(message = "Email is required")
//    @Column(nullable = false, unique = true)
//    private String email;

    private String message;
}
