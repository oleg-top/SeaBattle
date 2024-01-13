package com.example.seabattle.dtos;

import lombok.Data;

@Data
public class InviteUserToFieldRequest {
    private Long userId;
    private Long fieldId;
    private Integer amount;
}
