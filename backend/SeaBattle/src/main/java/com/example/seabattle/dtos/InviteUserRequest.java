package com.example.seabattle.dtos;

import lombok.Data;

@Data
public class InviteUserRequest {
    private Long userId;
    private Long fieldId;
    private Integer amount;
}
