package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.event.EventCreationResponseDTO;
import com.uttkarsh.InstaStudio.dto.event.EventListResponseDTO;
import com.uttkarsh.InstaStudio.dto.studio.StudioCreationRequestDTO;
import com.uttkarsh.InstaStudio.dto.studio.StudioCreationResponseDTO;
import com.uttkarsh.InstaStudio.services.studio.StudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    @PostMapping("/register/studio")
    public ResponseEntity<StudioCreationResponseDTO> createStudio(
            @RequestBody StudioCreationRequestDTO requestDTO
    ){
        StudioCreationResponseDTO responseDTO = studioService.createStudio(requestDTO);
        return  ResponseEntity.ok(responseDTO);
    }

    @PostMapping(path = "/register/{studioId}/assignAdmin/{userId}")
    public ResponseEntity<StudioCreationResponseDTO> assignAdminToStudio(
            @PathVariable Long studioId,
            @PathVariable Long userId

    ){
        StudioCreationResponseDTO responseDTO = studioService.assignAdminToStudio(studioId, userId);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping(path = "/register/{studioId}/addEvent/{eventId}")
    public ResponseEntity<EventCreationResponseDTO> addEventToStudio(
            @PathVariable Long studioId,
            @PathVariable Long eventId

    ){
        EventCreationResponseDTO responseDTO = studioService.addEventToStudio(studioId, eventId);
        return ResponseEntity.ok(responseDTO);
    }

}
