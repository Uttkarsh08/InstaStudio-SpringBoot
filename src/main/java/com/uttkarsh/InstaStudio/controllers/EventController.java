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

import java.util.List;

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
            @PathVariable Long studioId
    ){
        eventService.saveAllEventsForStudio(studioId);
    }

    @GetMapping("/{studioId}/next-event")
    public ResponseEntity<EventResponseDTO> getNextUpcomingEvent(
            @PathVariable Long studioId
    ){
        return ResponseEntity.ok(eventService.getNextUpcomingEventForStudio(studioId));
    }

    @PutMapping("/edit-event/{eventId}")
    public ResponseEntity<EventResponseDTO> updateEventById(
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestDTO updateEventDTO
    ){
        EventResponseDTO updatedEvent = eventService.updateEventById(eventId, updateEventDTO);
        return ResponseEntity.ok(updatedEvent);
    }

    @PutMapping("/edit-sub-event/{eventId}")
    public ResponseEntity<SubEventResponseDTO> updateSubEventById(
            @PathVariable Long eventId,
            @RequestBody @Valid SubEventRequestDTO updateEventDTO
    ){
        SubEventResponseDTO updatedSubEvent = eventService.updateSubEventById(eventId, updateEventDTO);
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

    @GetMapping("{studioId}/member/{memberId}/all-events")
    public ResponseEntity<Page<EventListResponseDTO>> getEventsByMemberId(
            @PathVariable Long studioId,
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.getAllEventsForMember(studioId, memberId, pageable));
    }

    @GetMapping("{studioId}/member/{memberId}/next-event")
    public ResponseEntity<EventResponseDTO> getNextEventForMember(
            @PathVariable Long studioId,
            @PathVariable Long memberId
    ){
        return ResponseEntity.ok(eventService.getNextUpcomingEventForMember(studioId, memberId));
    }

    @GetMapping("{studioId}/member/{memberId}/upcoming-event")
    public ResponseEntity<Page<EventListResponseDTO>> getUpcomingEventsForMember(
            @PathVariable Long studioId,
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.getUpcomingEventsForMember(studioId, memberId, pageable));
    }

    @GetMapping("{studioId}/member/{memberId}/completed-event")
    public ResponseEntity<Page<EventListResponseDTO>> getCompletedEventsForMember(
            @PathVariable Long studioId,
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.getCompletedEventsForMember(studioId, memberId, pageable));
    }


    //SEARCHING

    @GetMapping("{studioId}/search/all-events")
    public ResponseEntity<Page<EventListResponseDTO>> searchAllEvents(
            @PathVariable Long studioId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.searchAllEvents(studioId, query, pageable));
    }

    @GetMapping("/{studioId}/search/upcoming-events")
    public ResponseEntity<Page<EventListResponseDTO>> searchUpcomingEvents(
            @PathVariable Long studioId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") Integer PageNumber) {

        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.searchUpcomingEvents(studioId, query, pageable));
    }

    @GetMapping("/{studioId}/search/completed-events")
    public ResponseEntity<Page<EventListResponseDTO>> searchCompletedEvents(
            @PathVariable Long studioId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") Integer PageNumber) {

        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.searchCompletedEvents(studioId, query, pageable));
    }

    @GetMapping("{studioId}/member/{memberId}/search/all-events")
    public ResponseEntity<Page<EventListResponseDTO>> searchAllEventsForMember(
            @PathVariable Long studioId,
            @PathVariable Long memberId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") Integer PageNumber
    ){
        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.searchAllEventsForMember(studioId, memberId, query, pageable));
    }

    @GetMapping("/{studioId}/member/{memberId}/search/upcoming-events")
    public ResponseEntity<Page<EventListResponseDTO>> searchUpcomingEventsForMember(
            @PathVariable Long studioId,
            @PathVariable Long memberId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") Integer PageNumber) {

        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.searchUpcomingEventsForMember(studioId, memberId, query, pageable));
    }

    @GetMapping("/{studioId}/member/{memberId}/search/completed-events")
    public ResponseEntity<Page<EventListResponseDTO>> searchCompletedEventsForMember(
            @PathVariable Long studioId,
            @PathVariable Long memberId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") Integer PageNumber) {

        Pageable pageable = PageRequest.of(PageNumber, PAGE_SIZE);
        return ResponseEntity.ok(eventService.searchCompletedEventsForMember(studioId, memberId, query, pageable));
    }

}
