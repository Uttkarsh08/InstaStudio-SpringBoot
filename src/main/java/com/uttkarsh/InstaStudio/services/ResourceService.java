package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.dto.resource.ResourceRequestDTO;
import com.uttkarsh.InstaStudio.dto.resource.ResourceResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface ResourceService {
    ResourceResponseDTO createResource(ResourceRequestDTO requestDTO);

    ResourceResponseDTO getResourceById(Long studioId, Long resourceId);

    Page<ResourceResponseDTO> getAllResourcesForStudio(Long studioId, Pageable pageable);

    ResourceResponseDTO updateResourceById(Long studioId, Long resourceId, @Valid ResourceRequestDTO requestDTO);

    void deleteResourceById(Long studioId, Long resourceId);

    List<ResourceResponseDTO> getALlAvailableResources(Long studioId, LocalDateTime startDate, LocalDateTime endDate);

    Page<ResourceResponseDTO> searchAllResources(Long studioId, String query, Pageable pageable);
}
