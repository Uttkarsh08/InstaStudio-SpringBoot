package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.event.*;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import com.uttkarsh.InstaStudio.entities.Studio;
import com.uttkarsh.InstaStudio.exceptions.*;
import com.uttkarsh.InstaStudio.repositories.EventRepository;
import com.uttkarsh.InstaStudio.repositories.MemberRepository;
import com.uttkarsh.InstaStudio.repositories.StudioRepository;
import com.uttkarsh.InstaStudio.services.EventService;
import com.uttkarsh.InstaStudio.services.StudioService;
import com.uttkarsh.InstaStudio.utils.mappers.Event.EventListMapper;
import com.uttkarsh.InstaStudio.utils.mappers.Event.EventMapper;
import com.uttkarsh.InstaStudio.utils.mappers.Event.SubEventMapper;
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
    private final SubEventMapper subEventMapper;
    private final StudioRepository studioRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;


    @Override
    public EventResponseDTO createEvent(EventRequestDTO requestDTO) {

        Studio studio = studioRepository.findById(requestDTO.getStudioId())
                .orElseThrow(() -> new ResourceNotFoundException("Studio can't be found with id: "+ requestDTO.getStudioId()));

        Set<Event> subEvents = new LinkedHashSet<>(eventRepository.findAllByEventIdInOrderByEventStartDateAsc(requestDTO.getSubEventsIds()));
        Set<MemberProfile> members = memberRepository.findAllByMemberIdInAndUser_Studio_StudioId(requestDTO.getMemberIds(), requestDTO.getStudioId());

        //Sub-Events
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

        //Members
        if(!requestDTO.getMemberIds().isEmpty() && members.isEmpty()){
            throw new BadCredentialsException("Found Wrong Member Id");
        }
        if (members.size() != requestDTO.getMemberIds().size()) {
            throw new IllegalArgumentException("Some members do not belong to the specified studio");
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
                .members(new LinkedHashSet<>())
                .studio(studio)
                .build();

        //Sub-Events
        for(Event sub: subEvents){
            sub.setParentEvent(mainEvent);
            mainEvent.getSubEvents().add(sub);
        }

        //Members
        mainEvent.setMembers(members);

        Event savedEvent = eventRepository.save(mainEvent);
        return eventMapper.toEventDTO(savedEvent);
    }

    @Override
    public SubEventResponseDTO createSubEvent(SubEventRequestDTO requestDTO) {
        Studio studio = studioRepository.findById(requestDTO.getStudioId())
                .orElseThrow(() -> new ResourceNotFoundException("Studio can't be found with id: "+ requestDTO.getStudioId()));

        Set<MemberProfile> members = memberRepository.findAllByMemberIdInAndUser_Studio_StudioId(requestDTO.getMemberIds(), requestDTO.getStudioId());

        if(!requestDTO.getMemberIds().isEmpty() && members.isEmpty()){
            throw new BadCredentialsException("Found Wrong Member Id");
        }
        if (members.size() != requestDTO.getMemberIds().size()) {
            throw new IllegalArgumentException("Some members do not belong to the specified studio");
        }

        Event subEvent = Event.builder()
                .eventType(requestDTO.getEventType())
                .eventLocation(requestDTO.getEventLocation())
                .eventState(requestDTO.getEventState())
                .eventEndDate(requestDTO.getEventEndDate())
                .eventCity(requestDTO.getEventCity())
                .eventStartDate(requestDTO.getEventStartDate())
                .studio(studio)
                .build();

        subEvent.setMembers(members);
        eventRepository.save(subEvent);

        return subEventMapper.toSubEventDTO(subEvent);
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


        return subEventMapper.toSubEventDTO(event);
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
        Set<MemberProfile> members = memberRepository.findAllByMemberIdInAndUser_Studio_StudioId(requestDTO.getMemberIds(), requestDTO.getStudioId());

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

        if(!requestDTO.getMemberIds().isEmpty() && members.isEmpty()){
            throw new BadCredentialsException("Found Wrong Member Id");
        }
        if (members.size() != requestDTO.getMemberIds().size()) {
            throw new IllegalArgumentException("Some members do not belong to the specified studio");
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

        //clearing old memebers
        event.getMembers().clear();

        // Assigning new sub-events
        for (Event sub : subEvents) {
            sub.setParentEvent(event);
            event.getSubEvents().add(sub);
        }

        //Assigning new Members
        for(MemberProfile member: members){
            event.getMembers().add(member);
        }

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toEventDTO(updatedEvent);

    }

    @Override
    public SubEventResponseDTO updateSubEventById(Long studioId, Long eventId, SubEventRequestDTO dto) {
        Event subEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id:"+eventId));

        Set<MemberProfile> members = memberRepository.findAllByMemberIdInAndUser_Studio_StudioId(dto.getMemberIds(), dto.getStudioId());

        if(subEvent.getParentEvent() == null){
            throw new EventIsSubEventException( "Event with id: " + eventId + " is not present or is a MainEvent");
        }

        if (!subEvent.getStudio().getStudioId().equals(studioId)) {
            throw new EventNotAssignedException("Event doesn't belong to this studio");
        }

        if(!dto.getMemberIds().isEmpty() && members.isEmpty()){
            throw new BadCredentialsException("Found Wrong Member Id");
        }
        if (members.size() != dto.getMemberIds().size()) {
            throw new IllegalArgumentException("Some members do not belong to the specified studio");
        }

        subEvent.setEventType(dto.getEventType());
        subEvent.setEventStartDate(dto.getEventStartDate());
        subEvent.setEventEndDate(dto.getEventEndDate());
        subEvent.setEventLocation(dto.getEventLocation());
        subEvent.setEventCity(dto.getEventCity());
        subEvent.setEventState(dto.getEventState());

        subEvent.getMembers().clear();
        for(MemberProfile member: members){
            subEvent.getMembers().add(member);
        }

        Event updatedEvent = eventRepository.save(subEvent);
        return subEventMapper.toSubEventDTO(updatedEvent);
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
