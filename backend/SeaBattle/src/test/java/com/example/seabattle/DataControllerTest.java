package com.example.seabattle;

import static org.mockito.Mockito.*;

import com.example.seabattle.controllers.DataController;
import com.example.seabattle.dtos.*;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.*;
import com.example.seabattle.services.*;
import com.example.seabattle.utils.FileStorageUtils;
import com.example.seabattle.utils.JwtTokenUtils;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

public class DataControllerTest {

    private DataController dataController;
    private UserService userService;
    private PrizeService prizeService;
    private ShipService shipService;
    private FieldService fieldService;
    private ShotService shotService;
    private JwtTokenUtils jwtTokenUtils;
    private FileStorageUtils fileStorageUtils;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        prizeService = mock(PrizeService.class);
        shipService = mock(ShipService.class);
        fieldService = mock(FieldService.class);
        shotService = mock(ShotService.class);
        jwtTokenUtils = mock(JwtTokenUtils.class);
        fileStorageUtils = mock(FileStorageUtils.class);
        dataController = new DataController(userService, prizeService, shipService, fieldService, shotService, jwtTokenUtils, fileStorageUtils);
    }

    @Test
    void testGetUserDataSuccess() {
        String authorization = "Bearer token";
        String username = "testUser";
        String role = "ROLE_USER";
        Long id = 1L;
        List<Prize> prizes = new ArrayList<>();
        List<Shot> shots = new ArrayList<>();
        GetUserDataResponse expectedResponse = new GetUserDataResponse(id, username, role, prizes, shots);

        when(jwtTokenUtils.validateToken(authorization)).thenReturn(true);
        when(jwtTokenUtils.getUsername(anyString())).thenReturn(username);
        when(jwtTokenUtils.getRole(anyString())).thenReturn(role);
        when(jwtTokenUtils.getId(anyString())).thenReturn(id);
        when(userService.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(prizeService.allUserPrizes(username)).thenReturn(prizes);
        when(shotService.findByUser(any(User.class))).thenReturn(shots);

        ResponseEntity<?> responseEntity = dataController.getUserData(authorization);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof GetUserDataResponse);
        GetUserDataResponse responseBody = (GetUserDataResponse) responseEntity.getBody();
        Assertions.assertEquals(expectedResponse, responseBody);
    }

    @Test
    void testGetUserDataInvalidToken() {
        String authorization = "Bearer token";

        when(jwtTokenUtils.validateToken(authorization)).thenReturn(false);

        ResponseEntity<?> responseEntity = dataController.getUserData(authorization);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testGetShipDataByIdSuccess() {
        Long id = 1L;
        Ship ship = new Ship();
        ship.setId(id);

        when(shipService.findById(id)).thenReturn(Optional.of(ship));

        ResponseEntity<?> responseEntity = dataController.getShipDataById(id);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(ship, responseEntity.getBody());
    }

    @Test
    void testGetShipDataByIdNotFound() {
        Long id = 999L;

        when(shipService.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = dataController.getShipDataById(id);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testGetFieldDataByIdSuccess() {
        Long id = 1L;
        Field field = new Field();
        field.setId(id);
        Ship ship = new Ship();
        ship.setId(1L);
        List<Ship> ships = Collections.singletonList(ship);

        when(fieldService.findById(id)).thenReturn(Optional.of(field));
        when(shipService.findByField(field)).thenReturn(ships);

        ResponseEntity<?> responseEntity = dataController.getFieldDataById(id);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof GetFieldDataByIdResponse);
        GetFieldDataByIdResponse responseBody = (GetFieldDataByIdResponse) responseEntity.getBody();
        Assertions.assertEquals(field, responseBody.getField());
        Assertions.assertEquals(ships, responseBody.getShips());
    }

    @Test
    void testGetFieldDataByIdNotFound() {
        Long id = 999L;

        when(fieldService.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = dataController.getFieldDataById(id);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testGetInvitationDataByIdSuccess() {
        Long id = 1L;
        Shot shot = new Shot();
        shot.setId(id);

        when(shotService.findById(id)).thenReturn(Optional.of(shot));

        ResponseEntity<?> responseEntity = dataController.getShotDataById(id);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(shot, responseEntity.getBody());
    }

    @Test
    void testGetInvitationDataByIdNotFound() {
        Long id = 999L;

        when(shotService.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = dataController.getShotDataById(id);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    // По аналогии напишите остальные тесты для остальных методов
}

