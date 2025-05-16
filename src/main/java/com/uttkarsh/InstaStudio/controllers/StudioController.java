package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.studio.StudioCreationRequestDTO;
import com.uttkarsh.InstaStudio.dto.studio.StudioCreationResponseDTO;
import com.uttkarsh.InstaStudio.services.StudioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/studio")
    public ResponseEntity<StudioCreationResponseDTO> createStudio(
            @Valid @RequestBody StudioCreationRequestDTO requestDTO
    ){
        StudioCreationResponseDTO responseDTO = studioService.createStudio(requestDTO);
        return  ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/{studioId}/assignAdmin/{userId}")
    public ResponseEntity<StudioCreationResponseDTO> assignAdminToStudio(
            @PathVariable Long studioId,
            @PathVariable Long userId

    ){
        StudioCreationResponseDTO responseDTO = studioService.assignAdminToStudio(studioId, userId);
        return ResponseEntity.ok(responseDTO);
    }

}
