package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.user.UserRequestDTO;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.services.JwtService;
import com.uttkarsh.InstaStudio.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;


    @Test
    void createUser_shouldFailIfFirebaseIdMismatch() throws Exception {
        String firebaseIdFromToken = "firebase123";
        String firebaseIdFromRequest = "firebase456";
        String token = "Bearer valid.jwt.token";

        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setFirebaseId(firebaseIdFromRequest);

        when(jwtService.getFireBaseIdFromToken("valid.jwt.token")).thenReturn(firebaseIdFromToken);

        ResponseEntity<?> response = userController.createUser(token, requestDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token does not match Firebase ID", response.getBody());
    }

    @Test
    void createUser_shouldFailIfUserAlreadyRegistered() throws Exception {
        String firebaseId = "firebase123";
        String token = "Bearer valid.jwt.token";

        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setFirebaseId(firebaseId);

        when(jwtService.getFireBaseIdFromToken("valid.jwt.token")).thenReturn(firebaseId);
        when(userService.existsByFirebaseId(firebaseId)).thenReturn(true);

        ResponseEntity<?> response = userController.createUser(token, requestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already registered.", response.getBody());
    }
}
