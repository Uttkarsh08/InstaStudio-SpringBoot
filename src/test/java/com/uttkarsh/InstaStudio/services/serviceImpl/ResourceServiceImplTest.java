package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.member.MemberReviewRequestDTO;
import com.uttkarsh.InstaStudio.dto.member.MemberReviewResponseDTO;
import com.uttkarsh.InstaStudio.dto.resource.ResourceRequestDTO;
import com.uttkarsh.InstaStudio.dto.resource.ResourceResponseDTO;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import com.uttkarsh.InstaStudio.entities.Rating;
import com.uttkarsh.InstaStudio.entities.Resource;
import com.uttkarsh.InstaStudio.entities.Studio;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.exceptions.EventNotAssignedException;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.*;
import com.uttkarsh.InstaStudio.services.ValidationService;
import com.uttkarsh.InstaStudio.utils.mappers.Member.MemberReviewMapper;
import com.uttkarsh.InstaStudio.utils.mappers.Resource.ResourceMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    StudioRepository studioRepository;

    @Mock
    ResourceRepository resourceRepository;

    @Mock
    ResourceMapper resourceMapper;

    @Mock
    ValidationService validationService;

    @Mock
    EventRepository eventRepository;

    @InjectMocks
    ResourceServiceImpl resourceService;

    private final Long studioId = 1L;
    private final Long resourceId = 2L;

    private ResourceRequestDTO sampleRequestDTO() {
        return ResourceRequestDTO.builder()
                .studioId(studioId)
                .resourceName("Camera")
                .resourcePrice(1000L)
                .resourceRegisteredAt(LocalDateTime.now())
                .build();
    }

    private Resource sampleResource() {
        Resource r = new Resource();
        r.setResourceId(resourceId);
        r.setResourceName("Camera");
        r.setResourcePrice(1000L);
        r.setResourceRegisteredAt(LocalDateTime.now());
        r.setStudio(new Studio(studioId));
        r.setEvents(new LinkedHashSet<>());
        return r;
    }

    private ResourceResponseDTO sampleResponseDTO() {
        ResourceResponseDTO dto = new ResourceResponseDTO();
        dto.setResourceId(resourceId);
        dto.setResourceName("Camera");
        dto.setResourcePrice(1000L);
        dto.setResourceRegisteredAt(LocalDateTime.now());
        return dto;
    }

    // =======================
    // createResource tests
    // =======================
    @Test
    void createResource_WhenStudioExists_ShouldSaveAndReturnDTO() {
        ResourceRequestDTO dto = sampleRequestDTO();
        Studio studio = new Studio(studioId);

        when(studioRepository.findById(studioId)).thenReturn(Optional.of(studio));
        when(resourceRepository.save(any(Resource.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(resourceMapper.toResponseDTO(any(Resource.class))).thenReturn(sampleResponseDTO());

        ResourceResponseDTO response = resourceService.createResource(dto);

        assertNotNull(response);
        assertEquals(dto.getResourceName(), response.getResourceName());
        verify(studioRepository).findById(studioId);
        verify(resourceRepository).save(any(Resource.class));
        verify(resourceMapper).toResponseDTO(any(Resource.class));
    }

    @Test
    void createResource_WhenStudioNotFound_ShouldThrowResourceNotFoundException() {
        ResourceRequestDTO dto = sampleRequestDTO();

        when(studioRepository.findById(studioId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> resourceService.createResource(dto));

        assertEquals("Studio can't be found with id:" + studioId, ex.getMessage());
        verify(studioRepository).findById(studioId);
        verifyNoMoreInteractions(resourceRepository, resourceMapper);
    }

    // =======================
    // getResourceById tests
    // =======================
    @Test
    void getResourceById_WhenValidStudioAndResourceExists_ShouldReturnDTO() {
        Resource resource = sampleResource();
        ResourceResponseDTO dto = sampleResponseDTO();

        doNothing().when(validationService).isStudioValid(studioId);
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(resourceMapper.toResponseDTO(resource)).thenReturn(dto);

        ResourceResponseDTO response = resourceService.getResourceById(studioId, resourceId);

        assertNotNull(response);
        assertEquals(resource.getResourceName(), response.getResourceName());

        verify(validationService).isStudioValid(studioId);
        verify(resourceRepository).findById(resourceId);
        verify(resourceMapper).toResponseDTO(resource);
    }

    @Test
    void getResourceById_WhenStudioInvalid_ShouldThrowResourceNotFoundException() {
        doThrow(new ResourceNotFoundException("Studio invalid")).when(validationService).isStudioValid(studioId);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> resourceService.getResourceById(studioId, resourceId));

        assertEquals("Studio invalid", ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verifyNoMoreInteractions(resourceRepository, resourceMapper);
    }

    @Test
    void getResourceById_WhenResourceNotFound_ShouldThrowResourceNotFoundException() {
        doNothing().when(validationService).isStudioValid(studioId);
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> resourceService.getResourceById(studioId, resourceId));

        assertEquals("Resource can't be found with id:" + resourceId, ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verify(resourceRepository).findById(resourceId);
        verifyNoMoreInteractions(resourceMapper);
    }

    // =======================
    // getAllResourcesForStudio tests
    // =======================
    @Test
    void getAllResourcesForStudio_WhenValidStudio_ShouldReturnPagedDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Resource> resourceList = List.of(sampleResource());
        Page<Resource> resourcePage = new PageImpl<>(resourceList);

        doNothing().when(validationService).isStudioValid(studioId);
        when(resourceRepository.findAllByStudio_StudioId(studioId, pageable)).thenReturn(resourcePage);
        when(resourceMapper.toResponseDTO(any(Resource.class))).thenReturn(sampleResponseDTO());

        Page<ResourceResponseDTO> responsePage = resourceService.getAllResourcesForStudio(studioId, pageable);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        verify(validationService).isStudioValid(studioId);
        verify(resourceRepository).findAllByStudio_StudioId(studioId, pageable);
        verify(resourceMapper, times(1)).toResponseDTO(any(Resource.class));
    }

    @Test
    void getAllResourcesForStudio_WhenStudioInvalid_ShouldThrowException() {
        Pageable pageable = PageRequest.of(0, 10);
        doThrow(new ResourceNotFoundException("Studio invalid")).when(validationService).isStudioValid(studioId);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> resourceService.getAllResourcesForStudio(studioId, pageable));

        assertEquals("Studio invalid", ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verifyNoMoreInteractions(resourceRepository, resourceMapper);
    }

    // =======================
    // updateResourceById tests
    // =======================
    @Test
    void updateResourceById_WhenValidStudioResourceAndDTO_ShouldUpdateAndReturnDTO() {
        ResourceRequestDTO requestDTO = sampleRequestDTO();
        Resource existingResource = sampleResource();
        Resource updatedResource = sampleResource();
        updatedResource.setResourceName(requestDTO.getResourceName());
        updatedResource.setResourcePrice(requestDTO.getResourcePrice());

        doNothing().when(validationService).isStudioValid(studioId);
        when(resourceRepository.findStudioIdByResourceId(resourceId)).thenReturn(Optional.of(studioId));
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(existingResource));
        when(resourceRepository.save(existingResource)).thenReturn(updatedResource);
        when(resourceMapper.toResponseDTO(updatedResource)).thenReturn(sampleResponseDTO());

        ResourceResponseDTO response = resourceService.updateResourceById(studioId, resourceId, requestDTO);

        assertNotNull(response);
        assertEquals(requestDTO.getResourceName(), response.getResourceName());
        verify(validationService).isStudioValid(studioId);
        verify(resourceRepository).findStudioIdByResourceId(resourceId);
        verify(resourceRepository).findById(resourceId);
        verify(resourceRepository).save(existingResource);
        verify(resourceMapper).toResponseDTO(updatedResource);
    }

    @Test
    void updateResourceById_WhenStudioInvalid_ShouldThrowResourceNotFoundException() {
        ResourceRequestDTO requestDTO = sampleRequestDTO();
        doThrow(new ResourceNotFoundException("Studio invalid")).when(validationService).isStudioValid(studioId);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> resourceService.updateResourceById(studioId, resourceId, requestDTO));

        assertEquals("Studio invalid", ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verifyNoMoreInteractions(resourceRepository, resourceMapper);
    }

    @Test
    void updateResourceById_WhenResourceStudioMismatch_ShouldThrowEventNotAssignedException() {
        ResourceRequestDTO requestDTO = sampleRequestDTO();

        doNothing().when(validationService).isStudioValid(studioId);
        when(resourceRepository.findStudioIdByResourceId(resourceId)).thenReturn(Optional.of(999L)); // different studio
        // studioId != 999L, so mismatch

        EventNotAssignedException ex = assertThrows(EventNotAssignedException.class,
                () -> resourceService.updateResourceById(studioId, resourceId, requestDTO));

        assertEquals("Resource doesn't belong to this studio", ex.getMessage());
        verify(validationService).isStudioValid(studioId);
        verify(resourceRepository).findStudioIdByResourceId(resourceId);
        verifyNoMoreInteractions(resourceRepository, resourceMapper);
    }

    @Test
    void updateResourceById_WhenResourceNotFound_ShouldThrowResourceNotFoundException() {
        ResourceRequestDTO requestDTO = sampleRequestDTO();

        doNothing().when(validationService).isStudioValid(studioId);
        when(resourceRepository.findStudioIdByResourceId(resourceId)).thenReturn(Optional.of(studioId));
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> resourceService.updateResourceById(studioId, resourceId, requestDTO));

    }
}
