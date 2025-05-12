package com.uttkarsh.InstaStudio.utils.mappers.Resource;

import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.dto.resource.ResourceResponseDTO;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.Resource;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.repositories.EventRepository;
import com.uttkarsh.InstaStudio.repositories.RatingRepository;
import com.uttkarsh.InstaStudio.repositories.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class ResourceMapper {

    private final ResourceRepository resourceRepository;
    private final EventRepository eventRepository;

    public ResourceResponseDTO toResponseDTO(Resource resource) {
        if (resource == null) return null;

        ResourceResponseDTO dto = new ResourceResponseDTO();
        dto.setResourceId(resource.getResourceId());
        dto.setResourceName(resource.getResourceName());
        dto.setResourcePrice(resource.getResourcePrice());
        dto.setResourceRegisteredAt(resource.getResourceRegisteredAt());

        if(resource.getEvents() != null){
            Event lastUsedEvent = eventRepository.findFirstByResources_ResourceIdAndParentEventIsNullAndClientNameIsNotNullAndEventStartDateBeforeOrderByEventStartDateDesc(resource.getResourceId(), LocalDateTime.now());
            dto.setResourceLastUsedEvent(lastUsedEvent);
        }

        return dto;
    }
}
