package com.example.seabattle.dtos;

import lombok.Data;

@Data
public class AuthorizationRequest {
    private String username;
    private String password;
}
