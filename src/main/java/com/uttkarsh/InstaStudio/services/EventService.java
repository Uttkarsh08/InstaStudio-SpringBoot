package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.dto.event.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface EventService {
    EventResponseDTO createEvent(EventRequestDTO requestDTO);

    SubEventResponseDTO createSubEvent(SubEventRequestDTO requestDTO);

    Page<EventListResponseDTO> getAllEventsForStudio(Long studioId, Pageable pageable);

    Page<EventListResponseDTO> getAllUpcomingEventsForStudio(Long studioId, Pageable pageable);

    Page<EventListResponseDTO> getAllCompletedEventsForStudio(Long studioId, Pageable pageable);

    void saveAllEventsForStudio(Long studioId);

    EventResponseDTO getEventById(Long studioId, Long eventId);

    SubEventResponseDTO getSubEventById(Long studioId, Long eventId);

    void saveEventById(Long studioId, Long eventId);

    EventResponseDTO getNextUpcomingEventForStudio(Long studioId);

    EventResponseDTO updateEventById(Long eventId, @Valid EventRequestDTO updateEventDTO);

    SubEventResponseDTO updateSubEventById(Long eventId, @Valid SubEventRequestDTO updateEventDTO);

    void deleteSubEventById(Long studioId, Long eventId);

    void deleteEventById(Long studioId, Long eventId);

    Page<EventListResponseDTO> getAllEventsForMember(Long studioId, Long memberId, Pageable pageable);

    EventResponseDTO getNextUpcomingEventForMember(Long studioId, Long memberId);

    Page<EventListResponseDTO> getUpcomingEventsForMember(Long studioId, Long memberId, Pageable pageable);

    Page<EventListResponseDTO>  getCompletedEventsForMember(Long studioId, Long memberId, Pageable pageable);

    Page<EventListResponseDTO> searchAllEvents(Long studioId, String query, Pageable pageable);

    Page<EventListResponseDTO> searchUpcomingEvents(Long studioId, String query, Pageable pageable);

    Page<EventListResponseDTO> searchCompletedEvents(Long studioId, String query, Pageable pageable);

    Page<EventListResponseDTO> searchAllEventsForMember(Long studioId, Long memberId, String query, Pageable pageable);

    Page<EventListResponseDTO> searchUpcomingEventsForMember(Long studioId, Long memberId, String query, Pageable pageable);

    Page<EventListResponseDTO> searchCompletedEventsForMember(Long studioId, Long memberId, String query, Pageable pageable);
}
