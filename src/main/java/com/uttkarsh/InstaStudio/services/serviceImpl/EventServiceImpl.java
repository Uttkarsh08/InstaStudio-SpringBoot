package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.event.*;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.Studio;
import com.uttkarsh.InstaStudio.exceptions.EventAlreadyAssignedException;
import com.uttkarsh.InstaStudio.exceptions.EventNotAssignedException;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.EventRepository;
import com.uttkarsh.InstaStudio.repositories.StudioRepository;
import com.uttkarsh.InstaStudio.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final ModelMapper mapper;
    private final StudioRepository studioRepository;
    private final EventRepository eventRepository;

    @Override
    public EventResponseDTO createEvent(EventRequestDTO requestDTO) {

        Studio studio = studioRepository.findById(requestDTO.getStudioId())
                .orElseThrow(() -> new ResourceNotFoundException("Studio can't be found with id: "+ requestDTO.getStudioId()));

        Set<Event> subEvents = new LinkedHashSet<>(eventRepository.findAllByEventIdInOrderByEventStartDateAsc(requestDTO.getSubEventsIds()));

        if(!requestDTO.getSubEventsIds().isEmpty() && subEvents.isEmpty()){
            throw new BadCredentialsException("Found Wrong SubEvent Id");
        }
        if (subEvents.size() != requestDTO.getSubEventsIds().size()) {
            throw new BadCredentialsException("Some sub-event IDs are invalid.");
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
                .subEvents(new LinkedHashSet<>())
                .studio(studio)
                .build();

        for(Event sub: subEvents){
            sub.setParentEvent(mainEvent);
            mainEvent.getSubEvents().add(sub);
        }
        Event savedEvent = eventRepository.save(mainEvent);
        return mapper.map(savedEvent, EventResponseDTO.class);
    }

    @Override
    public SubEventResponseDTO createSubEvent(SubEventRequestDTO requestDTO) {
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

        return mapper.map(subEvent, SubEventResponseDTO.class);
    }

    @Override
    public EventResponseDTO getEventById(Long studioId, Long eventId) {
        Event event = eventRepository.findByEventIdAndStudio_StudioId(eventId, studioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event with id: " + eventId + " is not associated with studio id: " + studioId));

        return mapper.map(event, EventResponseDTO.class);
    }

    @Override
    public EventResponseDTO getNextUpcomingEventForStudio(Long studioId) {
        Event event =  eventRepository.findFirstByStudio_StudioIdAndParentEventIsNullAndEventStartDateAfterOrderByEventStartDate(studioId, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No Upcoming Event associated with studio id: " + studioId));
        return mapper.map(event, EventResponseDTO.class);

    }

    @Override
    public Page<EventListResponseDTO> getAllEventsForStudio(Long studioId, Pageable pageable) {
        Page<Event> events = eventRepository.findAllByStudio_StudioIdAndParentEventIsNullOrderByEventStartDate(studioId, pageable);

        return events.map(event -> {
            EventListResponseDTO dto = new EventListResponseDTO();
            dto.setEventId(event.getEventId());
            dto.setClientName(event.getClientName());
            dto.setClientPhoneNo(event.getClientPhoneNo());
            dto.setEventStartDate(event.getEventStartDate());
            dto.setEventEndDate(event.getEventEndDate());
            dto.setEventLocation(event.getEventLocation());
            dto.setEventCity(event.getEventCity());
            dto.setEventState(event.getEventState());
            dto.setEventIsSaved(event.isEvenIsSaved());

            Set<Long> subEventIds = event.getSubEvents()
                    .stream()
                    .map(Event::getEventId)
                    .collect(Collectors.toSet());
            dto.setSubEventsIds(subEventIds);

            return dto;
        });
    }

    @Override
    public Page<EventListResponseDTO> getAllUpcomingEventsForStudio(Long studioId, Pageable pageable) {
        Page<Event>  upcomingEvents = eventRepository.findAllByStudio_StudioIdAndParentEventIsNullAndEventStartDateAfterOrderByEventStartDate(
                studioId,
                LocalDateTime.now(),
                pageable);
        return upcomingEvents.map(event -> {
            EventListResponseDTO dto = new EventListResponseDTO();
            dto.setEventId(event.getEventId());
            dto.setClientName(event.getClientName());
            dto.setClientPhoneNo(event.getClientPhoneNo());
            dto.setEventStartDate(event.getEventStartDate());
            dto.setEventEndDate(event.getEventEndDate());
            dto.setEventLocation(event.getEventLocation());
            dto.setEventCity(event.getEventCity());
            dto.setEventState(event.getEventState());
            dto.setEventIsSaved(event.isEvenIsSaved());

            Set<Long> subEventIds = event.getSubEvents()
                    .stream()
                    .map(Event::getEventId)
                    .collect(Collectors.toSet());
            dto.setSubEventsIds(subEventIds);

            return dto;
        });
    }

    @Override
    public Page<EventListResponseDTO> getAllCompletedEventsForStudio(Long studioId, Pageable pageable) {
        Page<Event>  completedEvents = eventRepository.findAllByStudio_StudioIdAndParentEventIsNullAndEventStartDateBeforeOrderByEventStartDateDesc(
                studioId,
                LocalDateTime.now(),
                pageable);
        return completedEvents.map(event -> {
            EventListResponseDTO dto = new EventListResponseDTO();
            dto.setEventId(event.getEventId());
            dto.setClientName(event.getClientName());
            dto.setClientPhoneNo(event.getClientPhoneNo());
            dto.setEventStartDate(event.getEventStartDate());
            dto.setEventEndDate(event.getEventEndDate());
            dto.setEventLocation(event.getEventLocation());
            dto.setEventCity(event.getEventCity());
            dto.setEventState(event.getEventState());
            dto.setEventIsSaved(event.isEvenIsSaved());

            Set<Long> subEventIds = event.getSubEvents()
                    .stream()
                    .map(Event::getEventId)
                    .collect(Collectors.toSet());
            dto.setSubEventsIds(subEventIds);

            return dto;
        });
    }

    @Override
    public void saveEventById(Long studioId, Long eventId) {
        Event event = eventRepository.findByEventIdAndStudio_StudioId(eventId, studioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event with id: " + eventId + " is not associated with studio id: " + studioId));

        if(event.isEvenIsSaved()){
            throw new BadCredentialsException("Event iS Already Saved");
        }
        event.setEvenIsSaved(true);
        eventRepository.save(event);
    }

    @Transactional
    @Override
    public void saveAllEventsForStudio(Long studioId) {
        List<Event> allUnsavedEvents = eventRepository
                .findAllByStudio_StudioIdAndParentEventIsNullAndEvenIsSavedFalseAndEventStartDateAfterOrderByEventStartDate(studioId, LocalDateTime.now());

        for(Event event: allUnsavedEvents){
            event.setEvenIsSaved(true);
        }

        eventRepository.saveAll(allUnsavedEvents);
    }



}
