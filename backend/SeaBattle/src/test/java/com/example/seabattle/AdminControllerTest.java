package com.example.seabattle;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.seabattle.controllers.AdminController;
import com.example.seabattle.dtos.*;
import com.example.seabattle.exceptions.AppError;
import com.example.seabattle.models.*;
import com.example.seabattle.services.*;
import org.junit.jupiter.api.*;
import org.springframework.http.*;

import java.util.*;

public class AdminControllerTest {

    private AdminController adminController;
    private UserService userService;
    private FieldService fieldService;
    private ShipService shipService;
    private ShotService shotService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        fieldService = mock(FieldService.class);
        shipService = mock(ShipService.class);
        shotService = mock(ShotService.class);
        adminController = new AdminController(userService, fieldService, shipService, shotService);
    }

    @Test
    void testCreateFieldSuccess() {
        FieldCreateRequest request = new FieldCreateRequest();
        request.setSize(10);
        request.setName("Test Field");
        request.setDescription("Test Description");

        ResponseEntity<?> responseEntity = adminController.createField(request);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof FieldCreateResponse);
    }

    @Test
    void testCreateFieldError() {
        FieldCreateRequest request = new FieldCreateRequest();
        request.setSize(10);
        request.setName("Test Field");
        request.setDescription("Test Description");

        when(fieldService.createNewField(any(Field.class))).thenThrow(new RuntimeException("Field creation failed"));

        ResponseEntity<?> responseEntity = adminController.createField(request);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testCreateShipSuccess() {
        CreateShipRequest request = new CreateShipRequest();
        request.setName("Test Ship");
        request.setDescription("Test Description");

        ResponseEntity<?> responseEntity = adminController.createShip(request);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AddShipResponse);
    }

    @Test
    void testCreateShipError() {
        CreateShipRequest request = new CreateShipRequest();
        request.setName("Test Ship");
        request.setDescription("Test Description");

        when(shipService.createNewShip(any(Ship.class), any())).thenThrow(new RuntimeException("Ship creation failed"));

        ResponseEntity<?> responseEntity = adminController.createShip(request);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testDeleteShipNotFound() {
        DeleteShipRequest request = new DeleteShipRequest();
        request.setId(999L);

        when(shipService.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = adminController.deleteShip(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testAssignShipShipNotFound() {
        AssignShipRequest request = new AssignShipRequest();
        request.setFieldId(1L);
        request.setShipId(999L);
        request.setX(5);
        request.setY(5);

        when(shipService.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = adminController.assignShip(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testAssignShipFieldNotFound() {
        AssignShipRequest request = new AssignShipRequest();
        request.setFieldId(999L);
        request.setShipId(1L);
        request.setX(5);
        request.setY(5);

        when(fieldService.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = adminController.assignShip(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testAssignShipConflict() {
        AssignShipRequest request = new AssignShipRequest();
        request.setFieldId(1L);
        request.setShipId(1L);
        request.setX(5);
        request.setY(5);

        when(shipService.findByCoordinatesOnField(anyInt(), anyInt(), any(Field.class))).thenReturn(Optional.of(new Ship()));

        ResponseEntity<?> responseEntity = adminController.assignShip(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testInviteUserToFieldUserNotFound() {
        InviteUserToFieldRequest request = new InviteUserToFieldRequest();
        request.setUserId(999L);
        request.setFieldId(1L);
        request.setAmount(10);

        when(userService.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = adminController.inviteUserToField(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testInviteUserToFieldFieldNotFound() {
        InviteUserToFieldRequest request = new InviteUserToFieldRequest();
        request.setUserId(1L);
        request.setFieldId(999L);
        request.setAmount(10);

        when(fieldService.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = adminController.inviteUserToField(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testInviteUserToFieldConflict() {
        InviteUserToFieldRequest request = new InviteUserToFieldRequest();
        request.setUserId(1L);
        request.setFieldId(1L);
        request.setAmount(10);

        when(shotService.createNewShot(any(Shot.class))).thenReturn(false);

        ResponseEntity<?> responseEntity = adminController.inviteUserToField(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testDeleteUserFromFieldUserNotFound() {
        DeleteUserFromFieldRequest request = new DeleteUserFromFieldRequest();
        request.setUserId(999L);
        request.setFieldId(1L);

        when(userService.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = adminController.deleteUserFromField(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testDeleteUserFromFieldFieldNotFound() {
        DeleteUserFromFieldRequest request = new DeleteUserFromFieldRequest();
        request.setUserId(1L);
        request.setFieldId(999L);

        when(fieldService.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = adminController.deleteUserFromField(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }

    @Test
    void testDeleteUserFromFieldInvitationNotFound() {
        DeleteUserFromFieldRequest request = new DeleteUserFromFieldRequest();
        request.setUserId(1L);
        request.setFieldId(1L);

        when(shotService.findByUserAndField(any(User.class), any(Field.class))).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = adminController.deleteUserFromField(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody() instanceof AppError);
    }
}
