package com.example.seabattle.controllers;

import com.example.seabattle.dtos.TakeAShotRequest;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.Field;
import com.example.seabattle.models.Shot;
import com.example.seabattle.models.User;
import com.example.seabattle.services.FieldService;
import com.example.seabattle.services.ShipService;
import com.example.seabattle.services.ShotService;
import com.example.seabattle.services.UserService;
import com.example.seabattle.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final UserService userService;
    private final FieldService fieldService;
    private final ShotService shotService;
    private final ShipService shipService;

    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/game/take_a_shot")
    public ResponseEntity<?> takeAShot(
            @RequestHeader("Authorization") String authorization,
            @RequestBody TakeAShotRequest takeAShotRequest) {
        if (!authorization.startsWith("Bearer "))
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Wrong authorization token"), HttpStatus.BAD_REQUEST);
        String token = authorization.substring(7);
        Long userId = jwtTokenUtils.getId(token);
        Optional<User> cur_user = userService.findById(userId);
        if (cur_user.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No user with this id"), HttpStatus.BAD_REQUEST);
        Optional<Field> cur_field = fieldService.findById(takeAShotRequest.getFieldId());
        if (cur_field.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No field with this id"), HttpStatus.BAD_REQUEST);
        User user = cur_user.get();
        Field field = cur_field.get();
        Optional<Shot> cur_shot = shotService.findByUserAndField(user, field);
        if (cur_shot.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No invitation with this id"), HttpStatus.BAD_REQUEST);
        
    }
}
