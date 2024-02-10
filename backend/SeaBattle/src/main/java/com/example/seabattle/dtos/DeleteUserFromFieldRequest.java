package com.example.seabattle.dtos;

import lombok.Data;

@Data
public class DeleteUserFromFieldRequest {
    private Long userId;
    private Long fieldId;
}
