package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.event.*;
import com.uttkarsh.InstaStudio.services.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class EventController {

    private final int PAGE_SIZE = 5;

    private final EventService eventService;

    @PostMapping("/register/event")
    public ResponseEntity<EventCreationResponseDTO> createEvent(
            @RequestBody EventCreationRequestDTO requestDTO
    ){
        EventCreationResponseDTO responseDTO = eventService.createEvent(requestDTO);
        return  ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/register/subevent")
    public ResponseEntity<SubEventCreationResponseDTO> createSubEvent(
            @RequestBody SubEventCreationRequestDTO requestDTO
    ){
        SubEventCreationResponseDTO responseDTO = eventService.createSubEvent(requestDTO);
        return  ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/allevents/{studioId}")
    public ResponseEntity<Page<EventListResponseDTO>> getAllEvents(
            @PathVariable Long studioId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.getAllEventsForStudio(studioId, pageable));
    }

}
