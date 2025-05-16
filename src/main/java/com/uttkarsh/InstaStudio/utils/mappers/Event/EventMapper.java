package com.uttkarsh.InstaStudio.utils.mappers.Event;

import com.uttkarsh.InstaStudio.dto.event.EventResponseDTO;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import com.uttkarsh.InstaStudio.entities.Resource;
import org.springframework.context.annotation.Configuration;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class EventMapper {

    public EventResponseDTO toEventDTO(Event event) {
        if (event == null) return null;

        EventResponseDTO dto = new EventResponseDTO();
        dto.setEventId(event.getEventId());
        dto.setClientName(event.getClientName());
        dto.setClientPhoneNo(event.getClientPhoneNo());
        dto.setEventStartDate(event.getEventStartDate());
        dto.setEventEndDate(event.getEventEndDate());
        dto.setEventLocation(event.getEventLocation());
        dto.setEventCity(event.getEventCity());
        dto.setEventState(event.getEventState());
        dto.setEventType(event.getEventType());

        if(event.getParentEvent() != null){
            dto.setClientName(event.getParentEvent().getClientName());
            dto.setClientPhoneNo(event.getParentEvent().getClientPhoneNo());
        }

        //Sub-Events
        Set<Event> subEvents = event.getSubEvents()
                .stream()
                .sorted(Comparator.comparing(Event::getEventStartDate))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        dto.setSubEvents(subEvents);

        //Members
        Set<MemberProfile> members = new LinkedHashSet<>(event.getMembers());
        dto.setMembers(members);

        //Resources
        Set<Resource> resources = new LinkedHashSet<>(event.getResources());
        dto.setResources(resources);

        return dto;
    }
}