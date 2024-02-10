package com.example.seabattle;
import static org.mockito.Mockito.*;

import com.example.seabattle.controllers.AuthorizationController;
import com.example.seabattle.dtos.AuthorizationRequest;
import com.example.seabattle.dtos.AuthorizationResponse;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.User;
import com.example.seabattle.services.UserService;
import com.example.seabattle.utils.JwtTokenUtils;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

public class AuthorizationControllerTest {

    private AuthorizationController authorizationController;
    private UserService userService;
    private JwtTokenUtils jwtTokenUtils;
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        jwtTokenUtils = mock(JwtTokenUtils.class);
        authenticationManager = mock(AuthenticationManager.class);
        authorizationController = new AuthorizationController(userService, jwtTokenUtils, authenticationManager);
    }

    @Test
    void testAuthenticateSuccess() {
        AuthorizationRequest request = new AuthorizationRequest("testUser", "testPassword");
        User user = new User();
        user.setUsername("testUser");
        String token = "testToken";

        when(userService.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(jwtTokenUtils.generateToken(user)).thenReturn(token);

        ResponseEntity<?> responseEntity = authorizationController.authenticate(request);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AuthorizationResponse);
        AuthorizationResponse responseBody = (AuthorizationResponse) responseEntity.getBody();
        Assertions.assertEquals(token, responseBody.getToken());
    }

    @Test
    void testAuthenticateBadCredentials() {
        AuthorizationRequest request = new AuthorizationRequest("testUser", "testPassword");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<?> responseEntity = authorizationController.authenticate(request);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }
}

