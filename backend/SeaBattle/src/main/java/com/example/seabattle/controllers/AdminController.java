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

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final FieldService fieldService;
    private final ShipService shipService;
    private final ShotService shotService;

    @PostMapping("/field/create")
    public ResponseEntity<?> createField(@RequestBody FieldCreateRequest fieldCreateRequest) {
        try {
            Field field = new Field();
            field.setSize(fieldCreateRequest.getSize());
            field.setName(fieldCreateRequest.getName());
            field.setDescription(fieldCreateRequest.getDescription());
            fieldService.createNewField(field);
            log.info(String.format("Field %d has been successfully created.", field.getId()));
            return ResponseEntity.ok(new FieldCreateResponse("Field has been created successfully", field.getId()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/ship/create")
    public ResponseEntity<?> createShip(@ModelAttribute CreateShipRequest createShipRequest) {
        try {
            Ship ship = new Ship();
            ship.setName(createShipRequest.getName());
            ship.setDescription(createShipRequest.getDescription());
            ship.setX(-1);
            ship.setY(-1);
            shipService.createNewShip(ship, createShipRequest.getFile());
            return ResponseEntity.ok(new AddShipResponse("Ship has been created successfully.", ship.getId()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/ship/delete")
    public ResponseEntity<?> deleteShip(@RequestBody DeleteShipRequest deleteShipRequest) {
        try {
            Optional<Ship> cur_ship = shipService.findById(deleteShipRequest.getId());
            if (cur_ship.isEmpty())
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No ship with this id"), HttpStatus.BAD_REQUEST);
            Ship ship = cur_ship.get();
            shipService.deleteShip(ship);
            return ResponseEntity.ok("The ship has been deleted successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/field/assign_ship")
    public ResponseEntity<?> assignShip(@RequestBody AssignShipRequest assignShipRequest) {
        try {
            Optional<Field> cur_field = fieldService.findById(assignShipRequest.getFieldId());
            if (cur_field.isEmpty())
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No field with this id"), HttpStatus.BAD_REQUEST);
            Field field = cur_field.get();
            Optional<Ship> cur_ship = shipService.findById(assignShipRequest.getShipId());
            if (cur_ship.isEmpty())
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No ship with this id"), HttpStatus.BAD_REQUEST);
            Ship ship = cur_ship.get();
            Optional<Ship> ship_on_cord = shipService.findByCoordinatesOnField(
                    assignShipRequest.getX(),
                    assignShipRequest.getY(),
                    field
            );
            if (ship_on_cord.isPresent())
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "The ship on given coordinates and field is already existing"), HttpStatus.BAD_REQUEST);
            shipService.assignShipToField(ship, field, assignShipRequest.getX(), assignShipRequest.getY());
            return ResponseEntity.ok("The ship has been assigned successfully");

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/field/invite_user")
    public ResponseEntity<?> inviteUserToField(@RequestBody InviteUserToFieldRequest inviteUserToFieldRequest) {
        try {
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

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/field/remove_user")
    public ResponseEntity<?> deleteUserFromField(@RequestBody DeleteUserFromFieldRequest deleteUserFromFieldRequest) {
        try {
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
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
