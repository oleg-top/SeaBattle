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
import com.example.seabattle.utils.FileStorageUtils;
import com.example.seabattle.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;
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
    private final FileStorageUtils fileStorageUtils;

    @PostMapping("/field/create")
    public ResponseEntity<?> createField(@RequestBody FieldCreateRequest fieldCreateRequest) {
        Field field = new Field();
        field.setSize(fieldCreateRequest.getSize());
        field.setDescription(fieldCreateRequest.getDescription());
        fieldService.createNewField(field);
        return ResponseEntity.ok(new FieldCreateResponse("Field has been created successfully", field.getId()));
    }

    @PostMapping("/field/add_ship")
    public ResponseEntity<?> addShip(@ModelAttribute AddShipRequest addShipRequest) {
        Ship ship = new Ship();
        ship.setDescription(addShipRequest.getDescription());
        ship.setName(addShipRequest.getName());
        Optional<Field> cur_field = fieldService.findById(addShipRequest.getFieldId());
        if (cur_field.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No field with this id"), HttpStatus.BAD_REQUEST);
        Optional<Ship> cur_ship = shipService.findByCoordinatesOnField(
                addShipRequest.getX(),
                addShipRequest.getY(),
                cur_field.get());
        if (cur_ship.isPresent())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Ship on given coordinates and field is already existing"), HttpStatus.BAD_REQUEST);
        ship.setField(cur_field.get());
        ship.setX(addShipRequest.getX());
        ship.setY(addShipRequest.getY());
        shipService.createNewShip(ship, addShipRequest.getFile());
        return ResponseEntity.ok(new AddShipResponse("Ship has been added successfully.", ship.getId()));
    }

    @PostMapping("/field/delete_ship")
    public ResponseEntity<?> deleteShip(@RequestBody DeleteShipRequest deleteShipRequest) {
        Optional<Ship> cur_ship = shipService.findById(deleteShipRequest.getId());
        if (cur_ship.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No ship with this id"), HttpStatus.BAD_REQUEST);
        Ship ship = cur_ship.get();
        shipService.deleteShip(ship);
        return ResponseEntity.ok("The ship has been deleted successfully");
    }

    @PostMapping("/field/invite_user")
    public ResponseEntity<?> inviteUserToField(@RequestBody InviteUserToFieldRequest inviteUserToFieldRequest) {
        Optional<User> req_user = userService.findById(inviteUserToFieldRequest.getUserId());
        Optional<Field> req_field = fieldService.findById(inviteUserToFieldRequest.getFieldId());
        if (req_user.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No user with this id"), HttpStatus.BAD_REQUEST);
        if (req_field.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No field with this id"), HttpStatus.BAD_REQUEST);
        Shot shot = new Shot();
        shot.setAmount(inviteUserToFieldRequest.getAmount());
        shot.setUser(req_user.get());
        shot.setField(req_field.get());
        boolean hasCreated = shotService.createNewShot(shot);
        if (!hasCreated)
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "The invitation already exists"), HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(new InviteUserResponse("Invitation has been created successfully", shot.getId()));
    }

    @PostMapping("/field/delete_user")
    public ResponseEntity<?> deleteUserFromField(@RequestBody DeleteUserFromFieldRequest deleteUserFromFieldRequest) {
        Optional<User> cur_user = userService.findById(deleteUserFromFieldRequest.getUserId());
        if (cur_user.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No user with this id"), HttpStatus.BAD_REQUEST);
        User user = cur_user.get();
        Optional<Field> cur_field = fieldService.findById(deleteUserFromFieldRequest.getFieldId());
        if (cur_field.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No field with this id"), HttpStatus.BAD_REQUEST);
        Field field = cur_field.get();
        Optional<Shot> cur_shot = shotService.findByUserAndField(user, field);
        if (cur_shot.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No invitation for this user on this field"), HttpStatus.BAD_REQUEST);
        Shot shot = cur_shot.get();
        shotService.delete(shot);
        return ResponseEntity.ok("Successfully deleted");
    }
}
