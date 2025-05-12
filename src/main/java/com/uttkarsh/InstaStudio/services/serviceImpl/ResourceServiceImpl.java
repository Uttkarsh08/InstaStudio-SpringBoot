package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.member.MemberResponseDTO;
import com.uttkarsh.InstaStudio.dto.resource.ResourceRequestDTO;
import com.uttkarsh.InstaStudio.dto.resource.ResourceResponseDTO;
import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.Resource;
import com.uttkarsh.InstaStudio.entities.Studio;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.exceptions.EventNotAssignedException;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.ResourceRepository;
import com.uttkarsh.InstaStudio.repositories.StudioRepository;
import com.uttkarsh.InstaStudio.services.ResourceService;
import com.uttkarsh.InstaStudio.services.ValidationService;
import com.uttkarsh.InstaStudio.utils.mappers.Resource.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final StudioRepository studioRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ValidationService validationService;

    @Override
    public ResourceResponseDTO createResource(ResourceRequestDTO requestDTO) {
        Studio studio = studioRepository.findById(requestDTO.getStudioId())
                .orElseThrow(()-> new ResourceNotFoundException("Studio can't be found with id:" + requestDTO.getStudioId()));

       Resource resource = Resource.builder()
               .resourceName(requestDTO.getResourceName())
               .resourcePrice(requestDTO.getResourcePrice())
               .resourceRegisteredAt(requestDTO.getResourceRegisteredAt())
               .studio(studio)
               .build();
       resourceRepository.save(resource);
       return resourceMapper.toResponseDTO(resource);
    }

    @Override
    public ResourceResponseDTO getResourceById(Long studioId, Long resourceId) {
        validationService.isStudioValid(studioId);

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(()-> new ResourceNotFoundException("Resource can't be found with id:" + resourceId));

        return resourceMapper.toResponseDTO(resource);
    }

    @Override
    public Page<ResourceResponseDTO> getAllResourcesForStudio(Long studioId, Pageable pageable) {
        validationService.isStudioValid(studioId);

        Page<Resource> resources = resourceRepository.findAllByStudio_StudioId(studioId, pageable);

        return resources.map(resourceMapper::toResponseDTO);

    }

    @Override
    public ResourceResponseDTO updateResourceById(Long studioId, Long resourceId, ResourceRequestDTO requestDTO) {
        validationService.isStudioValid(studioId);

        Long associatedStudioId = resourceRepository.findStudioIdByResourceId(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Studio can't be found with id: " + studioId));

        if (!associatedStudioId.equals(studioId) || !associatedStudioId.equals(requestDTO.getStudioId())) {
            throw new EventNotAssignedException("Resource doesn't belong to this studio");
        }

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource can't be found with id: " + resourceId));

        resource.setResourceName(requestDTO.getResourceName());
        resource.setResourcePrice(requestDTO.getResourcePrice());

        Resource updatedresource = resourceRepository.save(resource);
        return resourceMapper.toResponseDTO(updatedresource);

    }

    @Override
    public void deleteResourceById(Long studioId, Long resourceId) {
        validationService.isStudioValid(studioId);

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(()-> new ResourceNotFoundException("Resource can't be found with id:" + resourceId));

        if (!resource.getStudio().getStudioId().equals(studioId)) {
            throw new EventNotAssignedException("Resource doesn't belong to this studio");
        }

        for (Event event : new LinkedHashSet<>(resource.getEvents())) {
            event.getResources().remove(resource);
        }

        resourceRepository.delete(resource);
    }
}
