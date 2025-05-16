package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.user.UserRequestDTO;
import com.uttkarsh.InstaStudio.services.JwtService;
import com.uttkarsh.InstaStudio.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

//    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER', 'CUSTOMER')")
//    @PostMapping("/register/user")
//    public ResponseEntity<?> createUser(
//            @RequestHeader("Authorization") String authHeader,
//            @Valid @RequestBody UserRequestDTO requestDTO) throws Exception {
//
//        String token = authHeader.split("Bearer ")[1];
//        String firebaseId = jwtService.getFireBaseIdFromToken(token);
//
//        if(!firebaseId.equals(requestDTO.getFirebaseId())){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token does not match Firebase ID");
//        }
//
//        if (userService.existsByFirebaseId(requestDTO.getFirebaseId())) {
//            return ResponseEntity.badRequest().body("User already registered.");
//        }
//
//        userService.createUser(requestDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(null);
//    }

//    Return tokens for Postman testing

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER', 'CUSTOMER')")
    @PostMapping("/register/user")
    public ResponseEntity<?> createUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid UserRequestDTO requestDTO) throws Exception {

        String token = authHeader.split("Bearer ")[1];
        String firebaseId = jwtService.getFireBaseIdFromToken(token);

        if (!firebaseId.equals(requestDTO.getFirebaseId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token does not match Firebase ID");
        }

        if (userService.existsByFirebaseId(requestDTO.getFirebaseId())) {
            return ResponseEntity.badRequest().body("User already registered.");
        }

        userService.createUser(requestDTO);

        // Generate new tokens with isRegistered = true and userType from requestDTO
        String newAccessToken = jwtService.generateAccessToken(firebaseId, true, requestDTO.getUserType());
        String newRefreshToken = jwtService.generateRefreshToken(firebaseId, true, requestDTO.getUserType());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }
}
