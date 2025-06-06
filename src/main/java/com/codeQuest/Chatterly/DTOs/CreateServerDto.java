package com.codeQuest.Chatterly.DTOs;

import lombok.Data;

@Data
public class CreateServerDto {
    private String name;
    private String description;
    private String serverIcon;
    private Long ownerId;
}
