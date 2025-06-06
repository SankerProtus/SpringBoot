package com.codeQuest.Chatterly.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {
    private String email;
    private String otp;
    private String confirmPassword;
}