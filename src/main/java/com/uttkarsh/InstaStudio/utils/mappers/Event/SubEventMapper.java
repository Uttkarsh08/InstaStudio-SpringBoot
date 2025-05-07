package com.uttkarsh.InstaStudio.utils.mappers.Event;

import com.uttkarsh.InstaStudio.dto.event.EventResponseDTO;
import com.uttkarsh.InstaStudio.dto.event.SubEventResponseDTO;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class SubEventMapper {

    public SubEventResponseDTO toSubEventDTO(Event event) {

        if (event == null) return null;

        SubEventResponseDTO dto = new SubEventResponseDTO();
        dto.setEventId(event.getEventId());
        dto.setEventStartDate(event.getEventStartDate());
        dto.setEventEndDate(event.getEventEndDate());
        dto.setEventLocation(event.getEventLocation());
        dto.setEventCity(event.getEventCity());
        dto.setEventState(event.getEventState());
        dto.setEventType(event.getEventType());


        Set<Long> membersIds = event.getMembers()
                .stream()
                .map(MemberProfile::getMemberId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        dto.setMemberIds(membersIds);

        return dto;
    }
}
