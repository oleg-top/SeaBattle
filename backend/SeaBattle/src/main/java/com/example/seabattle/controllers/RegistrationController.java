package com.example.seabattle.controllers;

import com.example.seabattle.dtos.RegistrationRequest;
import com.example.seabattle.dtos.RegistrationResponse;
import com.example.seabattle.models.User;
import com.example.seabattle.services.UserService;
import com.example.seabattle.utils.JwtTokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody RegistrationRequest registrationRequest) {
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(registrationRequest.getPassword());
        userService.createNewUser(user);

        String token = jwtTokenUtils.generateToken(user);
        return ResponseEntity.ok(new RegistrationResponse(token));
    }
}