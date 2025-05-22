package com.uttkarsh.InstaStudio.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.uttkarsh.InstaStudio.controllers.AuthController;
import com.uttkarsh.InstaStudio.dto.auth.LoginRequestDTO;
import com.uttkarsh.InstaStudio.dto.auth.LoginResponseDTO;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.services.JwtService;
import com.uttkarsh.InstaStudio.services.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private FirebaseToken firebaseToken;

    private MockedStatic<FirebaseAuth> firebaseAuthStaticMock;

    @BeforeEach
    void setUp() {
        // Mock the static method FirebaseAuth.getInstance() to return our mock firebaseAuth
        firebaseAuthStaticMock = Mockito.mockStatic(FirebaseAuth.class);
        firebaseAuthStaticMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
    }

    @AfterEach
    void tearDown() {
        // Close static mock to avoid interference between tests
        firebaseAuthStaticMock.close();
    }

    @Test
    void testLogin_whenUserExists_returnsSuccessResponse() throws Exception {
        // Arrange
        String validToken = "valid-firebase-token";
        UserType loginType = UserType.CUSTOMER;
        User user = new User();
        user.setFirebaseId("firebaseId123");
        user.setUserName("Test User");
        user.setUserEmail("user@test.com");

        when(firebaseAuth.verifyIdToken(validToken)).thenReturn(firebaseToken);
        when(firebaseToken.getUid()).thenReturn("firebaseId123");
        when(firebaseToken.getName()).thenReturn("Test User");
        when(firebaseToken.getEmail()).thenReturn("user@test.com");

        when(userService.getUserByFirebaseId("firebaseId123")).thenReturn(user);

        when(jwtService.generateAccessToken(anyString(), eq(true), eq(loginType))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(anyString(), eq(true), eq(loginType))).thenReturn("refresh-token");

        LoginRequestDTO requestDTO = new LoginRequestDTO(validToken, loginType);

        // Act
        ResponseEntity<LoginResponseDTO> response = authController.login(requestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        LoginResponseDTO body = response.getBody();

        assertEquals("access-token", body.getAccessToken());
        assertEquals("refresh-token", body.getRefreshToken());
        assertTrue(body.isRegistered());
        assertEquals("Test User", body.getUserName());
        assertEquals("user@test.com", body.getUserEmail());
        assertEquals("firebaseId123", body.getFirebaseId());
        assertEquals(loginType, body.getUserType());
    }

    @Test
    void testLogin_whenUserNotExists_returnsSuccessResponse() throws Exception {
        // Arrange
        String validToken = "valid-firebase-token";
        UserType loginType = UserType.CUSTOMER;

        when(firebaseAuth.verifyIdToken(validToken)).thenReturn(firebaseToken);
        when(firebaseToken.getUid()).thenReturn("newFirebaseId");
        when(firebaseToken.getName()).thenReturn("New User");
        when(firebaseToken.getEmail()).thenReturn("newuser@test.com");

        when(userService.getUserByFirebaseId("newFirebaseId")).thenReturn(null);  // User not found

        when(jwtService.generateAccessToken(anyString(), eq(false), eq(loginType))).thenReturn("access-token-new");
        when(jwtService.generateRefreshToken(anyString(), eq(false), eq(loginType))).thenReturn("refresh-token-new");

        LoginRequestDTO requestDTO = new LoginRequestDTO(validToken, loginType);

        // Act
        ResponseEntity<LoginResponseDTO> response = authController.login(requestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        LoginResponseDTO body = response.getBody();

        assertEquals("access-token-new", body.getAccessToken());
        assertEquals("refresh-token-new", body.getRefreshToken());
        assertFalse(body.isRegistered());
        assertEquals("New User", body.getUserName());
        assertEquals("newuser@test.com", body.getUserEmail());
        assertEquals("newFirebaseId", body.getFirebaseId());
        assertEquals(loginType, body.getUserType());
    }

}
