package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.services.JwtService;
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
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String firebaseId, boolean isRegistered, UserType userType) {

        return Jwts.builder()
                .subject(firebaseId)
                .claim("isRegistered", isRegistered)
                .claim("userType", userType.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 36000000000L))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(String firebaseId, boolean isRegistered, UserType userType) {
        return Jwts.builder()
                .subject(firebaseId)
                .claim("isRegistered", isRegistered)
                .claim("userType", userType.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 604800000L))
                .signWith(getSecretKey())
                .compact();
    }

    public  boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getFireBaseIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
