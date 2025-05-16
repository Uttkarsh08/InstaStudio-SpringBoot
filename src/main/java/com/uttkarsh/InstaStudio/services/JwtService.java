package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.entities.enums.UserType;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public interface JwtService {

    String generateAccessToken(String firebaseId, boolean isRegistered, UserType userType);

    String generateRefreshToken(String firebaseId, boolean isRegistered, UserType userType);

    boolean validateToken(String token);

    String getFireBaseIdFromToken(String token);

    Claims getAllClaims(String token);

}
