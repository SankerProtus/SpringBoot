package com.codeQuest.Chatterly.Controllers;

import com.codeQuest.Chatterly.DTOs.UpdateUserRequest;
import com.codeQuest.Chatterly.Entities.Users;
import com.codeQuest.Chatterly.Services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    // Get all users
    @GetMapping
    public ResponseEntity<List<Users>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    // Get user by username or ID
    @GetMapping("/search")
    public ResponseEntity<?> getUserByUsernameOrId(@RequestParam(required = false) String username,
                                                   @RequestParam(required = false) Long id) {
        return userService.findByUsernameOrId(username, id);
    }



    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@Valid @PathVariable Long id,
                                      @Valid @RequestBody UpdateUserRequest updateRequest) {
        return userService.updateUser(id, updateRequest);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@Valid @PathVariable Long id) {
        return userService.deleteUser(id);
    }
}