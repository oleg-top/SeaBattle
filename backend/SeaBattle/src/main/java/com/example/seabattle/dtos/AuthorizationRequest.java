package com.example.seabattle.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorizationRequest {
    private String username;
    private String password;
}
