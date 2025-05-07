package com.uttkarsh.InstaStudio.utils.mappers.Event;

import com.uttkarsh.InstaStudio.dto.event.EventListResponseDTO;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class EventListMapper {

    public EventListResponseDTO toEventListDTO(Event event) {
        if (event == null) return null;

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
        dto.setEventType(event.getEventType());

        if(event.getParentEvent() != null){
            dto.setClientName(event.getParentEvent().getClientName());
            dto.setClientPhoneNo(event.getParentEvent().getClientPhoneNo());
        }

        Set<Long> subEventIds = event.getSubEvents()
                .stream()
                .sorted(Comparator.comparing(Event::getEventStartDate))
                .map(Event::getEventId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<Long> membersIds = event.getMembers()
                .stream()
                .map(MemberProfile::getMemberId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        dto.setSubEventsIds(subEventIds);
        dto.setMemberIds(membersIds);

        return dto;
    }
}