package com.codeQuest.Chatterly.Controllers;

import com.codeQuest.Chatterly.DTOs.LoginDto;
import com.codeQuest.Chatterly.DTOs.SignUpDto;
import com.codeQuest.Chatterly.Services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthControllers {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        return userService.login(loginDto.getPhoneNumber(), loginDto.getEmail(), loginDto.getPassword());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpDto signUpDto) {
        return userService.signUp(signUpDto);
    }
}
