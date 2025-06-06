package com.codeQuest.Chatterly.Services;

import com.codeQuest.Chatterly.DTOs.RegisterRequest;
import com.codeQuest.Chatterly.DTOs.UpdateUserRequest;
import com.codeQuest.Chatterly.Entities.Users;
import com.codeQuest.Chatterly.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity<?> findByUsername(String username) {
        try {
            Optional<Users> users = userRepository.findByUsername(username);
            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<?> getUserById(Long id) {
        try {
            Optional<Users> user = userRepository.findById(id);
            if (user.isPresent()) {
                return userRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<?> signUp(RegisterRequest registerRequest) {
        try {

            // Input validation
            if (registerRequest.getUserName() == null || registerRequest.getEmail() == null ||
                    registerRequest.getPhoneNumber() == null || registerRequest.getPassword() == null) {
                throw new IllegalArgumentException("All fields are required");
            }

            // Validate if passwords match
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Passwords do not match");
            }

            Optional<Users> signUpDtoOptional = userRepository.findByPhoneNumber(registerRequest.getPhoneNumber());
            if (signUpDtoOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Username already exists");
            }

            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Email already exists");
            }

            Users newUser = new Users();
            newUser.setUsername(registerRequest.getUserName());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPhoneNumber(registerRequest.getPhoneNumber());
            String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
            newUser.setPasswordHash(hashedPassword);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());

            userRepository.save(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<Users> updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getEmail().equals(updateUserRequest.email())) {
                if (userRepository.existsByEmail(updateUserRequest.email())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }

            user.setUsername(updateUserRequest.username());
            user.setEmail(updateUserRequest.email());

            // Only update password if provided
            if (updateUserRequest.password() != null && !updateUserRequest.password().isBlank()) {
                String hashedPassword = passwordEncoder.encode(updateUserRequest.password());
                user.setPasswordHash(hashedPassword);
            }

            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Verify password
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public ResponseEntity<?> deleteUser(Long userId) {
        try {
            Optional<Users> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found.");
            }

            userRepository.deleteById(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    public ResponseEntity<?> login(String phoneNumber, String email, String password) {
        try {
            Optional<Users> user = userRepository.findByPhoneNumber(phoneNumber);
            boolean userByEmail = userRepository.existsByEmail(email);

            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Login or password is invalid");
            }

            if (!userByEmail) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Login or password is invalid");
            }

            if (!verifyPassword(password, user.get().getPasswordHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Incorrect password");
            }

            // Create a response DTO that excludes sensitive information
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.get().getId());
            response.put("username", user.get().getUsername());
            response.put("email", user.get().getEmail());
            response.put("createdAt", user.get().getCreatedAt());
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
    }
}