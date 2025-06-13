package com.codeQuest.Chatterly.Configurations;

import com.codeQuest.Chatterly.Entities.Users;
import com.codeQuest.Chatterly.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Configuration
@Profile("dev")
public class UserConfig {
    
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public UserConfig(PasswordEncoder passwordEncoder, Validator validator) {
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return args -> {
            try {
                if (userRepository.count() == 0) {
                    // Test data
                    registerUser(userRepository, "Dery James", "0250000000", "james@gmail.com", "test123");
                    registerUser(userRepository, "Kuusofaa David", "0550000000", "david@gmail.com", "test123");
                    registerUser(userRepository, "Sanker Protus", "0240000000", "protus@gmail.com", "test123");
                }
            } catch (DataIntegrityViolationException e) {
                throw new DataIntegrityViolationException("Database constraint violation while initializing users: " + e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Validation failed while initializing users: " + e.getMessage(), e);
            }
        };
    }

    @Transactional
    private void registerUser(UserRepository userRepository, String username, String phoneNumber, 
                            String email, String rawPassword) {
        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);

        Users user = Users.builder()
                .username(username)
                .email(email)
                .phoneNumber(phoneNumber)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .build();

        validateUser(user);
        userRepository.save(user);
    }

    private void validateUser(Users user) {
        var violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + violations);
        }
    }
}