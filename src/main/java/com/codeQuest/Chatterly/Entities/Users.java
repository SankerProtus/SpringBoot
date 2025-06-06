package com.codeQuest.Chatterly.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "User_Table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Email
    @Column(unique = true)
    private String email;

    private String passwordHash;

    @Pattern(regexp = "^(\\+233|0)[2-5]\\d{8}$", message = "Invalid phone number format")
    private String phoneNumber;

    //  private String otp;
    //  private String profile_picture_url;
    //  private LocalDateTime otpExpiry;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private LocalDateTime updatedAt;
}
