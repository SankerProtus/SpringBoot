package com.codeQuest.Chatterly.DTOs;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
    private String newPasswordConfirm;
}
