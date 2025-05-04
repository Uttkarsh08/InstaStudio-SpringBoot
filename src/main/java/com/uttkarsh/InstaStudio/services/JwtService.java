package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.entities.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public interface JwtService {

    String generateAccessToken(String firebaseId, boolean isRegistered, UserType userType);

    String generateRefreshToken(String firebaseId, boolean isRegistered, UserType userType);

    boolean validateToken(String token);

    String getFireBaseIdFromToken(String token);



}
