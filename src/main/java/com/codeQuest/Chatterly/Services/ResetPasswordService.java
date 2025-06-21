package com.codeQuest.Chatterly.Services;

import com.codeQuest.Chatterly.Entities.ResetPassword;
import com.codeQuest.Chatterly.Entities.User;
import com.codeQuest.Chatterly.Repositories.ResetPasswordRepository;
import com.codeQuest.Chatterly.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final UserRepository userRepository;
    private final ResetPasswordRepository tokenRepository;
    private final JavaMailSender mailSender;

    public void createAndSendResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        ResetPassword resetToken = new ResetPassword(token, user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(resetToken);

        String resetLink = "https://chatterly.com/reset-password?token=" + token;
        sendResetEmail(email, resetLink);
    }

    public void sendResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset your Chatterly password");
        message.setText("Click the link below to reset your password:\n" + resetLink);
        mailSender.send(message);
    }
}