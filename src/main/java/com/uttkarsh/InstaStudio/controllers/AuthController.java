package com.uttkarsh.InstaStudio.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.uttkarsh.InstaStudio.dto.LoginRequestDTO;
import com.uttkarsh.InstaStudio.dto.LoginResponseDTO;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.services.JwtService;
import com.uttkarsh.InstaStudio.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/v1")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) throws FirebaseAuthException {
        FirebaseToken decoded;
        try {
            decoded = FirebaseAuth.getInstance().verifyIdToken(loginRequestDTO.firebaseToken);
            log.info("Firebase ID: {}", decoded.getUid());
        } catch (FirebaseAuthException e) {
            log.error("Token verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String firebaseId = decoded.getUid();
        String username = decoded.getName();
        String userEmail = decoded.getEmail();
        UserType loginType = loginRequestDTO.loginType;

        log.info("FirebaseId: {}", firebaseId);
        log.info("loginType: {}", loginType);

        User user = userService.getUserByFirebaseIdAndUserType(firebaseId, loginType);
        boolean isRegistered = (user != null);

        String accessToken = jwtService.generateAccessToken(firebaseId, isRegistered, loginType);
        String refreshToken = jwtService.generateRefreshToken(firebaseId, isRegistered, loginType);

        LoginResponseDTO response = new LoginResponseDTO(accessToken, refreshToken, isRegistered, username, userEmail, firebaseId, loginType);
        log.info("Returning response: {}", response);
        return ResponseEntity.ok(response);

    }

}
