package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.event.EventListResponseDTO;
import com.uttkarsh.InstaStudio.dto.event.EventResponseDTO;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.services.EventService;
import com.uttkarsh.InstaStudio.services.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    @Mock
    private ValidationService validationService;

    @Mock
    private Pageable pageable;

    private Long studioId = 1L;
    private Long memberId = 2L;
    private String query = "searchQuery";

    private EventListResponseDTO sampleEventListDTO;
    private EventResponseDTO sampleEventResponseDTO;

    private Page<EventListResponseDTO> samplePage;

    @BeforeEach
    void setup() {
        sampleEventListDTO = new EventListResponseDTO();
        sampleEventListDTO.setEventId(10L);
        sampleEventListDTO.setClientName("Client X");

        sampleEventResponseDTO = new EventResponseDTO();
        sampleEventResponseDTO.setEventId(20L);
        sampleEventResponseDTO.setClientName("Client Y");

        samplePage = new PageImpl<>(List.of(sampleEventListDTO));
    }

    // --------- getAllEventsForMember ---------
    @Test
    void getAllEventsForMember_ReturnsPage() {
        when(eventService.getAllEventsForMember(eq(studioId), eq(memberId), any(Pageable.class))).thenReturn(samplePage);

        ResponseEntity<Page<EventListResponseDTO>> response = eventController.getEventsByMemberId(studioId, memberId, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(eventService).getAllEventsForMember(studioId, memberId, pageable);
    }

    @Test
    void getAllEventsForMember_InvalidStudio_ThrowsException() {
        doThrow(new ResourceNotFoundException("Invalid Studio")).when(validationService).isStudioValid(studioId);

        assertThrows(ResourceNotFoundException.class,
            () -> eventController.getEventsByMemberId(studioId, memberId, 0));
    }

    // --------- getNextUpcomingEventForMember ---------
    @Test
    void getNextUpcomingEventForMember_ReturnsEvent() {
        when(eventService.getNextUpcomingEventForMember(studioId, memberId)).thenReturn(sampleEventResponseDTO);

        ResponseEntity<EventResponseDTO> response = eventController.getNextEventForMember(studioId, memberId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleEventResponseDTO.getEventId(), response.getBody().getEventId());
        verify(eventService).getNextUpcomingEventForMember(studioId, memberId);
    }

    @Test
    void getNextUpcomingEventForMember_NoUpcomingEvent_ThrowsException() {
        when(eventService.getNextUpcomingEventForMember(studioId, memberId))
            .thenThrow(new ResourceNotFoundException("No upcoming event"));

        assertThrows(ResourceNotFoundException.class,
            () -> eventController.getNextEventForMember(studioId, memberId));
    }

    // --------- getUpcomingEventsForMember ---------
    @Test
    void getUpcomingEventsForMember_ReturnsPage() {
        when(eventService.getUpcomingEventsForMember(studioId, memberId, pageable)).thenReturn(samplePage);

        ResponseEntity<Page<EventListResponseDTO>> response = eventController.getUpcomingEventsForMember(studioId, memberId, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(eventService).getUpcomingEventsForMember(studioId, memberId, pageable);
    }

    // --------- getCompletedEventsForMember ---------
    @Test
    void getCompletedEventsForMember_ReturnsPage() {
        when(eventService.getCompletedEventsForMember(studioId, memberId, pageable)).thenReturn(samplePage);

        ResponseEntity<Page<EventListResponseDTO>> response = eventController.getCompletedEventsForMember(studioId, memberId, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(eventService).getCompletedEventsForMember(studioId, memberId, pageable);
    }

    // --------- searchAllEvents ---------
    @Test
    void searchAllEvents_ReturnsPage() {
        when(eventService.searchAllEvents(studioId, query, pageable)).thenReturn(samplePage);

        ResponseEntity<Page<EventListResponseDTO>> response = eventController.searchAllEvents(studioId, query, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(eventService).searchAllEvents(studioId, query, pageable);
    }

    // --------- searchUpcomingEvents ---------
    @Test
    void searchUpcomingEvents_ReturnsPage() {
        when(eventService.searchUpcomingEvents(studioId, query, pageable)).thenReturn(samplePage);

        ResponseEntity<Page<EventListResponseDTO>> response = eventController.searchUpcomingEvents(studioId, query, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).searchUpcomingEvents(studioId, query, pageable);
    }

    // --------- searchCompletedEvents ---------
    @Test
    void searchCompletedEvents_ReturnsPage() {
        when(eventService.searchCompletedEvents(studioId, query, pageable)).thenReturn(samplePage);

        ResponseEntity<Page<EventListResponseDTO>> response = eventController.searchCompletedEvents(studioId, query, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).searchCompletedEvents(studioId, query, pageable);
    }

    // --------- searchAllEventsForMember ---------
    @Test
    void searchAllEventsForMember_ReturnsPage() {
        when(eventService.searchAllEventsForMember(studioId, memberId, query, pageable)).thenReturn(samplePage);

        ResponseEntity<Page<EventListResponseDTO>> response = eventController.searchAllEventsForMember(studioId, memberId, query, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).searchAllEventsForMember(studioId, memberId, query, pageable);
    }

    // --------- searchUpcomingEventsForMember ---------
    @Test
    void searchUpcomingEventsForMember_ReturnsPage() {
        when(eventService.searchUpcomingEventsForMember(studioId, memberId, query, pageable)).thenReturn(samplePage);

        ResponseEntity<Page<EventListResponseDTO>> response = eventController.searchUpcomingEventsForMember(studioId, memberId, query, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).searchUpcomingEventsForMember(studioId, memberId, query, pageable);
    }

    // --------- searchCompletedEventsForMember ---------
    @Test
    void searchCompletedEventsForMember_ReturnsPage() {
        when(eventService.searchCompletedEventsForMember(studioId, memberId, query, pageable)).thenReturn(samplePage);

        ResponseEntity<Page<EventListResponseDTO>> response = eventController.searchCompletedEventsForMember(studioId, memberId, query, 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).searchCompletedEventsForMember(studioId, memberId, query, pageable);
    }

    // --------- Edge cases: null returns ---------
    @Test
    void getNextUpcomingEventForMember_ReturnsNullHandledGracefully() {
        when(eventService.getNextUpcomingEventForMember(studioId, memberId)).thenReturn(null);

        ResponseEntity<EventResponseDTO> response = eventController.getNextEventForMember(studioId, memberId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void searchAllEvents_ReturnsEmptyPage() {
        Page<EventListResponseDTO> emptyPage = Page.empty();
        when(eventService.searchAllEvents(studioId, query, pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<EventListResponseDTO>> response = eventController.searchAllEvents(studioId, query, 0);

        assertTrue(response.getBody().isEmpty());
    }

    // --------- Verify validation calls ---------
    @Test
    void validationIsCalledForMemberMethods() {
        when(eventService.getAllEventsForMember(studioId, memberId, pageable)).thenReturn(samplePage);

        eventController.getEventsByMemberId(studioId, memberId, 0);

        verify(validationService).isStudioValid(studioId);
        verify(validationService).isMemberValid(studioId, memberId);
    }

    @Test
    void validationIsCalledForStudioOnlyMethods() {
        when(eventService.searchAllEvents(studioId, query, pageable)).thenReturn(samplePage);

        eventController.searchAllEvents(studioId, query, 0);

        verify(validationService).isStudioValid(studioId);
        verify(validationService, never()).isMemberValid(anyLong(), anyLong());
    }
}
