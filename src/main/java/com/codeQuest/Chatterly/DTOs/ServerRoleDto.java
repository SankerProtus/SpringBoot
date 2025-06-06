package com.codeQuest.Chatterly.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class ServerRoleDto {
    private String name;
    private String color;
    private List<String> permissions;
}
