package com.example.seabattle.controllers;

import com.example.seabattle.dtos.GetFieldDataByIdResponse;
import com.example.seabattle.dtos.GetUserDataResponse;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.Field;
import com.example.seabattle.models.Prize;
import com.example.seabattle.models.Ship;
import com.example.seabattle.models.Shot;
import com.example.seabattle.services.*;
import com.example.seabattle.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DataController {

    private final UserService userService;
    private final PrizeService prizeService;
    private final ShipService shipService;
    private final FieldService fieldService;
    private final ShotService shotService;

    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/data/get_current_user_data")
    public ResponseEntity<?> getUserData(@RequestHeader("Authorization") String authorization) {
        if (!authorization.startsWith("Bearer "))
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Wrong authorization token"), HttpStatus.BAD_REQUEST);
        String token = authorization.substring(7);
        String username = jwtTokenUtils.getUsername(token);
        String role = jwtTokenUtils.getRole(token);
        Long id = jwtTokenUtils.getId(token);
        List<Prize> prizes = prizeService.allUserPrizes(username);
        return ResponseEntity.ok(new GetUserDataResponse(id, username, role, prizes));
    }

    @GetMapping("/data/get_ship_data_by_id/{id}")
    public ResponseEntity<?> getShipDataById(@PathVariable("id") Long id) {
        Optional<Ship> cur_ship = shipService.findById(id);
        if (cur_ship.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No ship with this id"), HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(cur_ship.get());
    }

    @GetMapping("/data/get_field_data_by_id/{id}")
    public ResponseEntity<?> getFieldDataById(@PathVariable("id") Long id) {
        Optional<Field> cur_field = fieldService.findById(id);
        if (cur_field.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No field with this id"), HttpStatus.BAD_REQUEST);
        Field field = cur_field.get();
        List<Ship> ships = shipService.findByField(field);
        Integer size = field.getSize();
        return ResponseEntity.ok(new GetFieldDataByIdResponse(size, ships));
    }

    @GetMapping("/data/get_invitation_data_by_id/{id}")
    public ResponseEntity<?> getShotDataById(@PathVariable("id") Long id) {
        Optional<Shot> cur_shot = shotService.findById(id);
        if (cur_shot.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No invitation with this id"), HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(cur_shot.get());
    }
}