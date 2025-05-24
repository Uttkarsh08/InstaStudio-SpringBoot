package com.uttkarsh.InstaStudio.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.uttkarsh.InstaStudio.dto.auth.LoginRequestDTO;
import com.uttkarsh.InstaStudio.dto.auth.LoginResponseDTO;
import com.uttkarsh.InstaStudio.dto.auth.TokenRefreshRequestDTO;
import com.uttkarsh.InstaStudio.dto.auth.TokenRefreshResponse;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.exceptions.InvalidTokenException;
import com.uttkarsh.InstaStudio.services.JwtService;
import com.uttkarsh.InstaStudio.services.UserService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
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
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) throws FirebaseAuthException {
        FirebaseToken decoded;
        try {
            decoded = FirebaseAuth.getInstance().verifyIdToken(loginRequestDTO.getFirebaseToken());
            log.info("Firebase ID: {}", decoded.getUid());
        } catch (FirebaseAuthException e) {
            log.error("Token verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String firebaseId = decoded.getUid();
        String username = decoded.getName();
        String userEmail = decoded.getEmail();
        UserType loginType = loginRequestDTO.getLoginType();

        log.info("FirebaseId: {}", firebaseId);
        log.info("loginType: {}", loginType);

        User user = userService.getUserByFirebaseId(firebaseId);
        boolean isRegistered = (user != null);

        String accessToken = jwtService.generateAccessToken(firebaseId, isRegistered, loginType);
        String refreshToken = jwtService.generateRefreshToken(firebaseId, isRegistered, loginType);

        LoginResponseDTO response = new LoginResponseDTO(accessToken, refreshToken, username, userEmail, firebaseId, loginType, isRegistered);
        log.info("Returning response: {}", response);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.validateToken(refreshToken)) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        String firebaseId = jwtService.getFireBaseIdFromToken(refreshToken);
        Claims claims =  jwtService.getAllClaims(refreshToken);

        String userType = claims.get("userType", String.class);
        boolean isRegistered = userService.existsByFirebaseId(firebaseId);

        String newAccessToken = jwtService.generateAccessToken(firebaseId, isRegistered, UserType.valueOf(userType));
        String newRefreshToken = jwtService.generateRefreshToken(firebaseId, isRegistered, UserType.valueOf(userType));

        return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, newRefreshToken));
    }


}
