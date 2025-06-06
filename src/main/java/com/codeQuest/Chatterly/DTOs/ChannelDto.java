package com.codeQuest.Chatterly.DTOs;

import com.codeQuest.Chatterly.Enums.ChannelType;
import lombok.Data;

@Data
public class ChannelDto {
    private String name;
    private ChannelType type;
    private Long categoryId;
    private Integer position;
}
