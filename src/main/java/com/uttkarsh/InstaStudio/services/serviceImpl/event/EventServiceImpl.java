package com.uttkarsh.InstaStudio.services.serviceImpl.event;

import com.uttkarsh.InstaStudio.dto.event.*;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.Studio;
import com.uttkarsh.InstaStudio.exceptions.EventAlreadyAssignedException;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.EventRepository;
import com.uttkarsh.InstaStudio.repositories.StudioRepository;
import com.uttkarsh.InstaStudio.services.event.EventService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final ModelMapper mapper;
    private final StudioRepository studioRepository;
    private final EventRepository eventRepository;

    @Override
    public EventCreationResponseDTO createEvent(EventCreationRequestDTO requestDTO) {

        Studio studio = studioRepository.findById(requestDTO.getStudioId())
                .orElseThrow(() -> new ResourceNotFoundException("Studio can't be found with id: "+ requestDTO.getStudioId()));

        Set<Event> subEvents = new HashSet<>(eventRepository.findAllById(requestDTO.getSubEventsIds()));

        if(!requestDTO.getSubEventsIds().isEmpty() && subEvents.isEmpty()){
            throw new BadCredentialsException("Found Wrong SubEvent Id");
        }
        for(Event sub: subEvents){
            if(sub.getParentEvent() != null){
                throw new EventAlreadyAssignedException("This Sub Event is Already a part of Event");
            }
        }

        Event mainEvent = Event.builder()
                .clientName(requestDTO.getClientName())
                .clientPhoneNo(requestDTO.getClientPhoneNo())
                .eventType(requestDTO.getEventType())
                .eventLocation(requestDTO.getEventLocation())
                .eventState(requestDTO.getEventState())
                .eventEndDate(requestDTO.getEventEndDate())
                .eventCity(requestDTO.getEventCity())
                .eventStartDate(requestDTO.getEventStartDate())
                .evenIsSaved(requestDTO.isEvenIsSaved())
                .subEvents(new HashSet<>())
                .studio(studio)
                .build();

        for(Event sub: subEvents){
            sub.setParentEvent(mainEvent);
            mainEvent.getSubEvents().add(sub);
        }
        Event savedEvent = eventRepository.save(mainEvent);
        return mapper.map(savedEvent, EventCreationResponseDTO.class);
    }

    @Override
    public SubEventCreationResponseDTO createSubEvent(SubEventCreationRequestDTO requestDTO) {
        Studio studio = studioRepository.findById(requestDTO.getStudioId())
                .orElseThrow(() -> new ResourceNotFoundException("Studio can't be found with id: "+ requestDTO.getStudioId()));

        Event subEvent = Event.builder()
                .eventType(requestDTO.getEventType())
                .eventLocation(requestDTO.getEventLocation())
                .eventState(requestDTO.getEventState())
                .eventEndDate(requestDTO.getEventEndDate())
                .eventCity(requestDTO.getEventCity())
                .eventStartDate(requestDTO.getEventStartDate())
                .studio(studio)
                .build();
        eventRepository.save(subEvent);

        return mapper.map(subEvent, SubEventCreationResponseDTO.class);
    }

    @Override
    public Page<EventListResponseDTO> getAllEventsForStudio(Long studioId, Pageable pageable) {
        return eventRepository.findAllByStudio_StudioIdAndParentEventIsNull(studioId, pageable);
    }
}
