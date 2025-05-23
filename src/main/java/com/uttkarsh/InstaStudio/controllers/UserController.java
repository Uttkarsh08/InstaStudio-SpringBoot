package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.studio.StudioCreationResponseDTO;
import com.uttkarsh.InstaStudio.dto.user.AdminProfileSetupRequestDTO;
import com.uttkarsh.InstaStudio.dto.user.AdminProfileSetupResponseDTO;
import com.uttkarsh.InstaStudio.dto.user.UserRequestDTO;
import com.uttkarsh.InstaStudio.dto.user.UserResponseDTO;
import com.uttkarsh.InstaStudio.exceptions.InvalidTokenException;
import com.uttkarsh.InstaStudio.services.JwtService;
import com.uttkarsh.InstaStudio.services.StudioService;
import com.uttkarsh.InstaStudio.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final StudioService studioService;
    private final JwtService jwtService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER', 'CUSTOMER')")
    @PostMapping("/register/user")
    public ResponseEntity<?> createUser(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserRequestDTO requestDTO) {

        String token = authHeader.split("Bearer ")[1];
        String firebaseId = jwtService.getFireBaseIdFromToken(token);

        if (!firebaseId.equals(requestDTO.getFirebaseId())) {
            throw new InvalidTokenException("Token does not match Firebase ID");
        }

        UserResponseDTO responseDTO = userService.createUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/adminProfileSetup")
    @Transactional
    public ResponseEntity<?> createAdminProfileSetup(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AdminProfileSetupRequestDTO requestDTO) throws Exception {


        UserResponseDTO userResponse = userService.createUser(requestDTO.getUser());

        StudioCreationResponseDTO studioResponse = studioService.createStudio(requestDTO.getStudio());

        studioService.assignAdminToStudio(studioResponse.getStudioId(), userResponse.getUserId());

        AdminProfileSetupResponseDTO responseDTO = new AdminProfileSetupResponseDTO(studioResponse.getStudioId(), userResponse.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

}
