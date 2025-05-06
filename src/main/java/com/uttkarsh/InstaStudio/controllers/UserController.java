package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.user.UserRequestDTO;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.services.JwtService;
import com.uttkarsh.InstaStudio.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register/user")
    public ResponseEntity<?> createUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserRequestDTO requestDTO) throws Exception {

        String token = authHeader.split("Bearer ")[1];
        String firebaseId = jwtService.getFireBaseIdFromToken(token);

        if(!firebaseId.equals(requestDTO.getFirebaseId())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token does not match Firebase ID");
        }

        if (userService.existsByFirebaseId(requestDTO.getFirebaseId())) {
            return ResponseEntity.badRequest().body("User already registered.");
        }

        User newUser = userService.createUser(requestDTO);
        return ResponseEntity.ok(newUser);
    }
}
