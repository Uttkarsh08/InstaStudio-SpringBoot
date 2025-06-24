package com.uttkarsh.InstaStudio.utils.mappers.Event;

import com.uttkarsh.InstaStudio.dto.event.SubEventResponseDTO;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import com.uttkarsh.InstaStudio.entities.Resource;
import org.springframework.context.annotation.Configuration;
import java.util.LinkedHashSet;
import java.util.Set;

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

        if(event.getParentEvent() != null){
            dto.setParentEventId(event.getParentEvent().getEventId());
        }

        Set<MemberProfile> members = new LinkedHashSet<>(event.getMembers());
        dto.setMembers(members);

        Set<Resource> resources = new LinkedHashSet<>(event.getResources());
        dto.setResources(resources);

        return dto;
    }
}
