package com.uttkarsh.InstaStudio.services.event;

import com.uttkarsh.InstaStudio.dto.event.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface EventService {
    EventCreationResponseDTO createEvent(EventCreationRequestDTO requestDTO);

    SubEventCreationResponseDTO createSubEvent(SubEventCreationRequestDTO requestDTO);

    Page<EventListResponseDTO> getAllEventsForStudio(Long studioId, Pageable pageable);
}
