package com.codeQuest.Chatterly.Controllers;

import com.codeQuest.Chatterly.DTOs.ResetPasswordRequest;
import com.codeQuest.Chatterly.Entities.ResetPassword;
import com.codeQuest.Chatterly.Entities.User;
import com.codeQuest.Chatterly.Repositories.ResetPasswordRepository;
import com.codeQuest.Chatterly.Repositories.UserRepository;
import com.codeQuest.Chatterly.Services.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ResetPasswordController {
    private final UserRepository userRepository;
    private final ResetPasswordRepository tokenRepository;
    private final ResetPasswordService resetPasswordService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            resetPasswordService.createAndSendResetToken(email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("If the email exists, a password reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        ResetPassword resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.delete(resetToken);

        return ResponseEntity.ok("Password successfully reset");
    }
}