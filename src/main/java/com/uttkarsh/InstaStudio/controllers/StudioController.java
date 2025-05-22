package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.advices.ApiResponse;
import com.uttkarsh.InstaStudio.dto.studio.StudioCreationRequestDTO;
import com.uttkarsh.InstaStudio.dto.studio.StudioCreationResponseDTO;
import com.uttkarsh.InstaStudio.services.StudioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public void assignAdminToStudio(
            @PathVariable Long studioId,
            @PathVariable Long userId

    ){
        studioService.assignAdminToStudio(studioId, userId);
        ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/image/{studioId}")
    public ResponseEntity<?> getImageForStudio(
            @PathVariable Long studioId
    ){
        String base64Image = studioService.getImageForStudio(studioId);
        return ResponseEntity.ok(new ApiResponse<>(base64Image));
    }
}
