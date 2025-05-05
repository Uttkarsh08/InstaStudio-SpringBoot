package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.event.*;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.Studio;
import com.uttkarsh.InstaStudio.exceptions.*;
import com.uttkarsh.InstaStudio.repositories.EventRepository;
import com.uttkarsh.InstaStudio.repositories.StudioRepository;
import com.uttkarsh.InstaStudio.services.EventService;
import com.uttkarsh.InstaStudio.utils.mappers.Event.EventListMapper;
import com.uttkarsh.InstaStudio.utils.mappers.Event.EventMapper;
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

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final ModelMapper mapper;
    private final EventListMapper eventListMapper;
    private final EventMapper eventMapper;
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
            if(!sub.getSubEvents().isEmpty()){
                throw new EventAlreadyAssignedException("Sub Event Can't contain a Main Event");
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
        return eventMapper.toEventDTO(savedEvent);
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
        Event event = eventRepository.findByEventIdAndStudio_StudioIdAndParentEventIsNullAndClientNameIsNotNull(eventId, studioId)
                .orElseThrow(() -> new EventIsSubEventException(
                        "Event with id: " + eventId + " is not present or is a SubEvent"));

        return mapper.map(event, EventResponseDTO.class);
    }

    @Override
    public SubEventResponseDTO getSubEventById(Long studioId, Long eventId) {
        Event event = eventRepository.findByEventIdAndStudio_StudioIdAndParentEventIsNotNull(eventId, studioId)
                .orElseThrow(() -> new EventIsParentEventException(
                        "Event with id: " + eventId + " is not present or is a MainEvent"));


        return mapper.map(event, SubEventResponseDTO.class);
    }

    @Override
    public EventResponseDTO getNextUpcomingEventForStudio(Long studioId) {
        Event event =  eventRepository.findFirstByStudio_StudioIdAndParentEventIsNullAndClientNameIsNotNullAndEventStartDateAfterOrderByEventStartDate(studioId, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No Upcoming Event associated with studio id: " + studioId));
        return mapper.map(event, EventResponseDTO.class);

    }


    @Override
    public Page<EventListResponseDTO> getAllEventsForStudio(Long studioId, Pageable pageable) {
        Page<Event> events = eventRepository.findAllByStudio_StudioIdAndParentEventIsNullAndClientNameIsNotNullOrderByEventStartDate(studioId, pageable);

        return events.map(eventListMapper::toEventListDTO);
    }

    @Override
    public Page<EventListResponseDTO> getAllUpcomingEventsForStudio(Long studioId, Pageable pageable) {
        Page<Event>  upcomingEvents = eventRepository.findAllByStudio_StudioIdAndParentEventIsNullAndClientNameIsNotNullAndEventStartDateAfterOrderByEventStartDate(
                studioId,
                LocalDateTime.now(),
                pageable);
        return upcomingEvents.map(eventListMapper::toEventListDTO);
    }

    @Override
    public Page<EventListResponseDTO> getAllCompletedEventsForStudio(Long studioId, Pageable pageable) {
        Page<Event>  completedEvents = eventRepository.findAllByStudio_StudioIdAndParentEventIsNullAndClientNameIsNotNullAndEventStartDateBeforeOrderByEventStartDateDesc(
                studioId,
                LocalDateTime.now(),
                pageable);
        return completedEvents.map(eventListMapper::toEventListDTO);
    }

    @Override
    public void saveEventById(Long studioId, Long eventId) {
        Event event = eventRepository.findByEventIdAndStudio_StudioIdAndParentEventIsNull(eventId, studioId)
                .orElseThrow(() -> new EventIsSubEventException(
                        "Event with id: " + eventId + " is not present or is a SubEvent"));

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


    @Override
    public EventResponseDTO updateEventById(Long studioId, Long eventId, EventRequestDTO requestDTO) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id:"+eventId));

        if(event.getParentEvent() != null || event.getClientName() == null){
            throw new EventIsSubEventException( "Event with id: " + eventId + " is not present or is a SubEvent");
        }

        Set<Event> subEvents = new LinkedHashSet<>(eventRepository.findAllByEventIdInOrderByEventStartDateAsc(requestDTO.getSubEventsIds()));

        if(!requestDTO.getSubEventsIds().isEmpty() && subEvents.isEmpty()){
            throw new BadCredentialsException("Found Wrong SubEvent Id");
        }
        if (subEvents.size() != requestDTO.getSubEventsIds().size()) {
            throw new BadCredentialsException("Some sub-event IDs are invalid.");
        }
        for(Event sub: subEvents){
            if(sub.getParentEvent() != null && !sub.getParentEvent().getSubEvents().contains(sub)){
                throw new EventAlreadyAssignedException("This Sub Event is Already a part of Event");
            }
            if(!sub.getSubEvents().isEmpty()){
                throw new EventAlreadyAssignedException("Sub Event Can't contain a Main Event");
            }
        }

        if (!event.getStudio().getStudioId().equals(studioId)) {
            throw new EventNotAssignedException("Event doesn't belong to this studio");
        }
        if(event.getParentEvent() != null){
            throw new EventIsSubEventException("This event is parent Event");
        }

        event.setClientName(requestDTO.getClientName());
        event.setClientPhoneNo(requestDTO.getClientPhoneNo());
        event.setEventType(requestDTO.getEventType());
        event.setEventLocation(requestDTO.getEventLocation());
        event.setEventState(requestDTO.getEventState());
        event.setEventCity(requestDTO.getEventCity());
        event.setEventStartDate(requestDTO.getEventStartDate());
        event.setEventEndDate(requestDTO.getEventEndDate());
        event.setEvenIsSaved(requestDTO.isEvenIsSaved());

        // Clearing old sub-events
        event.getSubEvents().forEach(sub -> sub.setParentEvent(null));
        event.getSubEvents().clear();

        // Assigning new sub-events
        for (Event sub : subEvents) {
            sub.setParentEvent(event);
            event.getSubEvents().add(sub);
        }

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toEventDTO(updatedEvent);

    }

    @Override
    public SubEventResponseDTO updateSubEventById(Long studioId, Long eventId, SubEventRequestDTO dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id:"+eventId));

        if(event.getParentEvent() == null){
            throw new EventIsSubEventException( "Event with id: " + eventId + " is not present or is a MainEvent");
        }

        if (!event.getStudio().getStudioId().equals(studioId)) {
            throw new EventNotAssignedException("Event doesn't belong to this studio");
        }

        event.setEventType(dto.getEventType());
        event.setEventStartDate(dto.getEventStartDate());
        event.setEventEndDate(dto.getEventEndDate());
        event.setEventLocation(dto.getEventLocation());
        event.setEventCity(dto.getEventCity());
        event.setEventState(dto.getEventState());

        Event updatedEvent = eventRepository.save(event);
        return mapper.map(updatedEvent, SubEventResponseDTO.class);
    }

        @Override
        public void deleteSubEventById(Long studioId, Long eventId) {
            Event event = eventRepository.findByEventIdAndStudio_StudioIdAndParentEventIsNotNull(eventId, studioId)
                    .orElseThrow(() -> new EventIsParentEventException(
                            "Event with id: " + eventId + " is not present or is a MainEvent"));

            if (!event.getStudio().getStudioId().equals(studioId)) {
                throw new EventNotAssignedException("Event does not belong to the specified studio");
            }
            eventRepository.delete(event);
        }

    @Override
    public void deleteEventById(Long studioId, Long eventId) {
        Event event = eventRepository.findByEventIdAndStudio_StudioIdAndParentEventIsNullAndClientNameIsNotNull(eventId, studioId)
                .orElseThrow(() -> new EventIsSubEventException(
                        "Event with id: " + eventId + " is not present or is a SubEvent"));

        if (!event.getStudio().getStudioId().equals(studioId)) {
            throw new EventNotAssignedException("Event does not belong to the specified studio");
        }
        Set<Event> subEvents = event.getSubEvents();
        for(Event sub: subEvents){
            eventRepository.delete(sub);
        }

        eventRepository.delete(event);
    }


}
