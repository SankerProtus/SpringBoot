package com.codeQuest.Chatterly.Services;

import com.codeQuest.Chatterly.DTOs.UpdateUserRequest;
import com.codeQuest.Chatterly.Entities.Users;
import com.codeQuest.Chatterly.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    public ResponseEntity<?> deleteUser(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user id"));
            }

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
}