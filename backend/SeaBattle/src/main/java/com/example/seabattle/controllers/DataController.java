package com.example.seabattle.controllers;

import com.example.seabattle.dtos.GetAllShipsResponse;
import com.example.seabattle.dtos.GetFieldDataByIdResponse;
import com.example.seabattle.dtos.GetUserDataResponse;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.*;
import com.example.seabattle.services.*;
import com.example.seabattle.utils.FileStorageUtils;
import com.example.seabattle.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    private final FileStorageUtils fileStorageUtils;

    @GetMapping("/data/get_current_user_data")
    public ResponseEntity<?> getUserData(@RequestHeader("Authorization") String authorization) {
        if (!jwtTokenUtils.validateToken(authorization)) {
            log.error("Incorrect jwt token");
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Wrong authorization token"), HttpStatus.BAD_REQUEST);
        }
        String token = authorization.substring(7);
        String username = jwtTokenUtils.getUsername(token);
        String role = jwtTokenUtils.getRole(token);
        Long id = jwtTokenUtils.getId(token);
        User user = userService.findByUsername(username).get();
        List<Prize> prizes = prizeService.allUserPrizes(username);
        List<Shot> shots = shotService.findByUser(user);
        return ResponseEntity.ok(new GetUserDataResponse(id, username, role, prizes, shots));
    }

    @GetMapping("/data/get_ship_data_by_id/{id}")
    public ResponseEntity<?> getShipDataById(@PathVariable("id") Long id) {
        Optional<Ship> cur_ship = shipService.findById(id);
        if (cur_ship.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No ship with this id"), HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(cur_ship.get());
    }

    @GetMapping("/data/get_all_ships")
    public ResponseEntity<?> getAllShips() {
        Iterable<Ship> ships = shipService.getAllShips();
        List<String> names = new ArrayList<>(),
                descriptions = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (Ship ship: ships) {
            names.add(ship.getName());
            descriptions.add(ship.getDescription());
            ids.add(ship.getId());
        }
        return ResponseEntity.ok(new GetAllShipsResponse(names, descriptions, ids));
    }

    @GetMapping("/data/get_field_data_by_id/{id}")
    public ResponseEntity<?> getFieldDataById(@PathVariable("id") Long id) {
        Optional<Field> cur_field = fieldService.findById(id);
        if (cur_field.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No field with this id"), HttpStatus.BAD_REQUEST);
        Field field = cur_field.get();
        List<Ship> ships = shipService.findByField(field);
        return ResponseEntity.ok(new GetFieldDataByIdResponse(field, ships));
    }

    @GetMapping("/data/get_invitation_data_by_id/{id}")
    public ResponseEntity<?> getShotDataById(@PathVariable("id") Long id) {
        Optional<Shot> cur_shot = shotService.findById(id);
        if (cur_shot.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No invitation with this id"), HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(cur_shot.get());
    }

    @GetMapping("/data/images/ships/{id}")
    public ResponseEntity<?> getImage(@PathVariable Long id) {
        Optional<Ship> cur_ship = shipService.findById(id);
        if (cur_ship.isEmpty())
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "No ship with this id"), HttpStatus.BAD_REQUEST);
        Ship ship = cur_ship.get();
        String filename = ship.getImage();
        log.debug(filename);
        Resource file = fileStorageUtils.load(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
