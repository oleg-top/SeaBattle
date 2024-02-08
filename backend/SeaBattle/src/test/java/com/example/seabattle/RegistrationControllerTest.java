package com.example.seabattle;
import static org.mockito.Mockito.*;

import com.example.seabattle.controllers.RegistrationController;
import com.example.seabattle.dtos.RegistrationRequest;
import com.example.seabattle.dtos.RegistrationResponse;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.User;
import com.example.seabattle.services.UserService;
import com.example.seabattle.utils.JwtTokenUtils;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class RegistrationControllerTest {

    private RegistrationController registrationController;
    private UserService userService;
    private JwtTokenUtils jwtTokenUtils;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        jwtTokenUtils = mock(JwtTokenUtils.class);
        registrationController = new RegistrationController(userService, jwtTokenUtils);
    }

    @Test
    void testRegistrationSuccess() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setIsAdmin(false);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        String token = "testToken";
        RegistrationResponse expectedResponse = new RegistrationResponse(token);

        when(userService.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(jwtTokenUtils.generateToken(user)).thenReturn(token);

        ResponseEntity<?> responseEntity = registrationController.registration(request);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof RegistrationResponse);
        RegistrationResponse responseBody = (RegistrationResponse) responseEntity.getBody();
        Assertions.assertEquals(expectedResponse, responseBody);
    }

    @Test
    void testRegistrationUserAlreadyExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("existingUser");
        request.setPassword("testPassword");
        request.setIsAdmin(false);

        when(userService.findByUsername(request.getUsername())).thenReturn(Optional.of(new User()));

        ResponseEntity<?> responseEntity = registrationController.registration(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    // Add more tests to cover other scenarios
}

