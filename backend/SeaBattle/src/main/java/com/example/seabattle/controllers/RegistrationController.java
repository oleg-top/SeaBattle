package com.example.seabattle.controllers;

import com.example.seabattle.dtos.RegistrationRequest;
import com.example.seabattle.dtos.RegistrationResponse;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.User;
import com.example.seabattle.services.UserService;
import com.example.seabattle.utils.JwtTokenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@AllArgsConstructor
@Slf4j
public class RegistrationController {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody RegistrationRequest registrationRequest) {
        if (userService.findByUsername(registrationRequest.getUsername()).isPresent()) {
            log.error(String.format("User with username '%s' already exists", registrationRequest.getUsername()));
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),"Пользователь с таким именем уже существует"), HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(registrationRequest.getPassword());
        userService.createNewUser(user);
        log.info(String.format("User '%s' has been created successfully", registrationRequest.getUsername()));

        String token = jwtTokenUtils.generateToken(user);
        return ResponseEntity.ok(new RegistrationResponse(token));
    }
}