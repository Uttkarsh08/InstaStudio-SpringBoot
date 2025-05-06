package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.event.*;
import com.uttkarsh.InstaStudio.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class EventController {

    @Value("${PAGE_SIZE}")
    private int PAGE_SIZE;

    private final EventService eventService;

    @PostMapping("/register/event")
    public ResponseEntity<EventResponseDTO> createEvent(
            @RequestBody EventRequestDTO requestDTO
    ){
        EventResponseDTO responseDTO = eventService.createEvent(requestDTO);
        return  ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/register/sub-event")
    public ResponseEntity<SubEventResponseDTO> createSubEvent(
            @RequestBody SubEventRequestDTO requestDTO
    ){
        SubEventResponseDTO responseDTO = eventService.createSubEvent(requestDTO);
        return  ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{studioId}/event/{eventId}")
    public ResponseEntity<EventResponseDTO> getEventById(
            @PathVariable Long studioId,
            @PathVariable Long eventId
    ){
        return ResponseEntity.ok(eventService.getEventById(studioId, eventId));

    }

    @GetMapping("/{studioId}/sub-event/{eventId}")
    public ResponseEntity<SubEventResponseDTO> getSubEventById(
            @PathVariable Long studioId,
            @PathVariable Long eventId
    ){
        return ResponseEntity.ok(eventService.getSubEventById(studioId, eventId));

    }

    @GetMapping("/{studioId}/all-events")
    public ResponseEntity<Page<EventListResponseDTO>> getAllEvents(
            @PathVariable Long studioId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.getAllEventsForStudio(studioId, pageable));
    }

    @GetMapping("/{studioId}/upcoming-events")
    public ResponseEntity<Page<EventListResponseDTO>> getAllUpcomingEvents(
            @PathVariable Long studioId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.getAllUpcomingEventsForStudio(studioId, pageable));
    }

    @GetMapping("/{studioId}/completed-events")
    public ResponseEntity<Page<EventListResponseDTO>> getAllCompletedEvents(
            @PathVariable Long studioId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.getAllCompletedEventsForStudio(studioId, pageable));
    }

    @PutMapping("/{studioId}/save-event/{eventId}")
    public ResponseEntity<Void> saveEventById(
            @PathVariable Long studioId,
            @PathVariable Long eventId
    ){
        eventService.saveEventById(studioId, eventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{studioId}/save-all-events")
    public void saveAllEvents(
            @PathVariable Long studioId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        eventService.saveAllEventsForStudio(studioId);
    }

    @GetMapping("/{studioId}/next-event")
    public ResponseEntity<EventResponseDTO> getNextUpcomingEvent(
            @PathVariable Long studioId
    ){
        return ResponseEntity.ok(eventService.getNextUpcomingEventForStudio(studioId));
    }

    @PutMapping("/{studioId}/edit-event/{eventId}")
    public ResponseEntity<EventResponseDTO> updateEventById(
            @PathVariable Long studioId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestDTO updateEventDTO
    ){
        EventResponseDTO updatedEvent = eventService.updateEventById(studioId, eventId, updateEventDTO);
        return ResponseEntity.ok(updatedEvent);
    }

    @PutMapping("/{studioId}/edit-sub-event/{eventId}")
    public ResponseEntity<SubEventResponseDTO> updateSubEventById(
            @PathVariable Long studioId,
            @PathVariable Long eventId,
            @RequestBody @Valid SubEventRequestDTO updateEventDTO
    ){
        SubEventResponseDTO updatedSubEvent = eventService.updateSubEventById(studioId, eventId, updateEventDTO);
        return ResponseEntity.ok(updatedSubEvent);
    }

    @DeleteMapping("/{studioId}/delete-sub-event/{eventId}")
    public ResponseEntity<Void> deleteSubEventById(
            @PathVariable Long studioId,
            @PathVariable Long eventId
    ){
        eventService.deleteSubEventById(studioId, eventId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{studioId}/delete-event/{eventId}")
    public ResponseEntity<Void> deleteEventById(
            @PathVariable Long studioId,
            @PathVariable Long eventId
    ){
        eventService.deleteEventById(studioId, eventId);
        return ResponseEntity.noContent().build();
    }


}
