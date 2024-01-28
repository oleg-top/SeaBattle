package com.example.seabattle.controllers;

import com.example.seabattle.dtos.TakeAShotRequest;
import com.example.seabattle.dtos.TakeAShotResponse;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.*;
import com.example.seabattle.services.*;
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
    private final PrizeService prizeService;

    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/game/take_a_shot")
    public ResponseEntity<?> takeAShot(
            @RequestHeader("Authorization") String authorization,
            @RequestBody TakeAShotRequest takeAShotRequest) {
        try {
            if (!jwtTokenUtils.validateToken(authorization)) {
                log.error("Incorrect jwt token");
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Wrong authorization token"), HttpStatus.BAD_REQUEST);
            }
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
            Shot shot = cur_shot.get();
            if (!shotService.correctAmount(shot))
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "The amount of shots is 0"), HttpStatus.BAD_REQUEST);
            shotService.updateAmount(shot, shot.getAmount() - 1);
            Optional<Ship> cur_ship = shipService.findByCoordinatesOnField(
                    takeAShotRequest.getX(),
                    takeAShotRequest.getY(),
                    field);
            if (cur_ship.isEmpty()) {
                shipService.createNewEmptyShip(
                        takeAShotRequest.getX(),
                        takeAShotRequest.getY(),
                        field
                );
                return ResponseEntity.ok(new TakeAShotResponse("MISS", null));
            }
            Ship ship = cur_ship.get();
            if (!shipService.isActive(ship) && !shipService.isEmpty(ship)) {
                shotService.updateAmount(shot, shot.getAmount() + 1);
                return ResponseEntity.ok(new TakeAShotResponse("INACTIVE", null));
            }
            if (!shipService.isActive(ship) && shipService.isEmpty(ship)) {
                shotService.updateAmount(shot, shot.getAmount() + 1);
                return ResponseEntity.ok(new TakeAShotResponse("EMPTY", null));
            }
            Prize prize = new Prize();
            prize.setUser(user);
            prize.setShip(ship);
            prizeService.createNewPrize(prize);
            shipService.deactivateShip(ship);
            return ResponseEntity.ok(new TakeAShotResponse("HIT", prize));
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
