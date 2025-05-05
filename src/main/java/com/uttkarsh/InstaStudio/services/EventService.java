package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.dto.event.*;
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

    void saveEventById(Long studioId, Long eventId);

    EventResponseDTO getNextUpcomingEventForStudio(Long studioId);
}
