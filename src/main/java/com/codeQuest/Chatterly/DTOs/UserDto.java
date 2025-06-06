package com.codeQuest.Chatterly.DTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
}
