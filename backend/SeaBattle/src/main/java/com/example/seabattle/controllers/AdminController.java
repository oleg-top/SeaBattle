package com.example.seabattle.controllers;

import com.example.seabattle.dtos.*;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.Field;
import com.example.seabattle.models.Ship;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final FieldService fieldService;
    private final ShipService shipService;
    private final ShotService shotService;

    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/field/create")
    public ResponseEntity<?> createField(@RequestBody FieldCreateRequest fieldCreateRequest) {
        Field field = new Field();
        field.setSize(fieldCreateRequest.getSize());
        fieldService.createNewField(field);
        return ResponseEntity.ok(new FieldCreateResponse("Field has been created successfully", field.getId()));
    }

    @PostMapping("/field/add_ship")
    public ResponseEntity<?> addShip(@RequestBody AddShipRequest addShipRequest) {
        Ship ship = new Ship();
        ship.setDescription(addShipRequest.getDescription());
        ship.setName(addShipRequest.getName());
        Optional<Field> cur = fieldService.findById(addShipRequest.getFieldId());
        if (cur.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No field with this id"), HttpStatus.BAD_REQUEST);
        ship.setField(cur.get());
        ship.setX(addShipRequest.getX());
        ship.setY(addShipRequest.getY());
        shipService.createNewShip(ship);
        return ResponseEntity.ok(new AddShipResponse("Ship has been added successfully.", ship.getId()));
    }

    @PostMapping("/field/invite_user")
    public ResponseEntity<?> inviteUser(@RequestBody InviteUserRequest inviteUserRequest) {
        Shot shot = new Shot();
        shot.setAmount(inviteUserRequest.getAmount());
        Optional<User> req_user = userService.findById(inviteUserRequest.getUserId());
        Optional<Field> req_field = fieldService.findById(inviteUserRequest.getFieldId());
        if (req_user.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No user with this id"), HttpStatus.BAD_REQUEST);
        if (req_field.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No field with this id"), HttpStatus.BAD_REQUEST);
        shot.setUser(req_user.get());
        shot.setField(req_field.get());
        shotService.createNewShot(shot);
        return ResponseEntity.ok(new InviteUserResponse("Invitation has been created successfully", shot.getId()));
    }
}
