package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.entities.enums.UserType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey", "MyVerySecretKeyThatIsLongEnough123456");
    }

    @Test
    void shouldGenerateAndValidateAccessToken() {
        String token = jwtService.generateAccessToken("firebase123", true, UserType.ADMIN);
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void shouldGenerateAndValidateRefreshToken() {
        String token = jwtService.generateRefreshToken("firebase456", false, UserType.MEMBER);
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.jwt.token";
        assertFalse(jwtService.validateToken(invalidToken));
    }

    @Test
    void shouldExtractFirebaseIdFromToken() {
        String firebaseId = "firebase789";
        String token = jwtService.generateAccessToken(firebaseId, true, UserType.CUSTOMER);
        String extractedId = jwtService.getFireBaseIdFromToken(token);
        assertEquals(firebaseId, extractedId);
    }

    @Test
    void shouldReturnFalseForExpiredToken() {
        String firebaseId = "expired-user";
        String expiredToken = Jwts.builder()
                .subject(firebaseId)
                .issuedAt(new Date(System.currentTimeMillis() - 60000)) // issued 1 min ago
                .expiration(new Date(System.currentTimeMillis() - 1000)) // expired 1 second ago
                .signWith(jwtService.getSecretKey(), SignatureAlgorithm.HS256.HS256)
                .compact();

        boolean isValid = jwtService.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void shouldReturnFalseForTamperedToken() {
        String firebaseId = "valid-user";
        String validToken = jwtService.generateAccessToken(firebaseId, false, UserType.ADMIN);
        String tamperedToken = validToken.substring(0, validToken.length() - 2) + "xx";

        boolean isValid = jwtService.validateToken(tamperedToken);

        assertFalse(isValid);
    }

}