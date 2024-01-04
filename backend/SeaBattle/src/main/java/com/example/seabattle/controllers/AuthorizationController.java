package com.example.seabattle.controllers;

import com.example.seabattle.dtos.AuthorizationRequest;
import com.example.seabattle.dtos.AuthorizationResponse;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.User;
import com.example.seabattle.services.UserService;
import com.example.seabattle.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthorizationController {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody AuthorizationRequest authorizationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authorizationRequest.getUsername(), authorizationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            log.error(String.format("User with username '%s' wasn't authenticated - Bad credentials", authorizationRequest.getUsername()));
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(),"Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        User user = userService.findByUsername(authorizationRequest.getUsername()).get();
        String token = jwtTokenUtils.generateToken(user);
        log.info(String.format("User '%s' has successfully authenticated", authorizationRequest.getUsername()));
        return ResponseEntity.ok(new AuthorizationResponse(token));
    }
}
