package com.codeQuest.Chatterly.Configurations;

import com.codeQuest.Chatterly.Entities.Users;
import com.codeQuest.Chatterly.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private Validator validator;

    @Bean
    @Transactional
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return args -> {
            try {
                if (userRepository.count() == 0) {
                    // Simulate user registration with test data
                    registerUser(userRepository, "Dery James", "james@gmail.com", "james123");
                    registerUser(userRepository, "Kuusofaa David", "david@gmail.com", "david123");
                    registerUser(userRepository, "Sanker Protus", "protus@gmail.com", "protus123");
                }
            } catch (DataIntegrityViolationException e) {
                throw new RuntimeException("Database constraint violation while initializing users", e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Validation failed while initializing users", e);
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error while initializing users", e);
            }
        };
    }

    private void registerUser(UserRepository userRepository, String username, String email, String rawPassword) {
        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);
        
        // 1. Create user
        Users user = Users.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .build();

        // Validate the user
        validateUser(user);

        // Save the user
        userRepository.save(user);
    }

    private void validateUser(Users user) {
        var violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + violations);
        }
    }
}