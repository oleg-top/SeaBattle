package com.example.seabattle.dtos;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String password;
    private Boolean isAdmin;
}
