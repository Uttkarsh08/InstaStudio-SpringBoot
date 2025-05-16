package com.uttkarsh.InstaStudio.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.uttkarsh.InstaStudio.dto.resource.ResourceRequestDTO;
import com.uttkarsh.InstaStudio.dto.resource.ResourceResponseDTO;
import com.uttkarsh.InstaStudio.services.ResourceService;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "PAGE_SIZE")
class ResourceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private ResourceController resourceController;

    private ObjectMapper objectMapper;

    private ResourceResponseDTO sampleResponseDTO;
    private ResourceRequestDTO sampleRequestDTO;

    private final Long studioId = 1L;
    private final Long resourceId = 10L;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        sampleRequestDTO = new ResourceRequestDTO();
        sampleRequestDTO.setResourceName("Camera");
        sampleRequestDTO.setResourcePrice(5000L);
        sampleRequestDTO.setResourceRegisteredAt(LocalDateTime.now());
        sampleRequestDTO.setStudioId(studioId);

        sampleResponseDTO = new ResourceResponseDTO();
        sampleResponseDTO.setResourceId(resourceId);
        sampleResponseDTO.setResourceName("Camera");
        sampleResponseDTO.setResourcePrice(5000L);
        sampleResponseDTO.setResourceRegisteredAt(LocalDateTime.now());
    }

    @Test
    void createResource_ReturnsOkAndResourceResponse() throws Exception {
        when(resourceService.createResource(any(ResourceRequestDTO.class))).thenReturn(sampleResponseDTO);

        mockMvc.perform(post("/api/v1/register/resource")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceId").value(resourceId))
                .andExpect(jsonPath("$.resourceName").value("Camera"));

        verify(resourceService).createResource(any(ResourceRequestDTO.class));
    }

    @Test
    void getResourceById_ReturnsOkAndResourceResponse() throws Exception {
        when(resourceService.getResourceById(studioId, resourceId)).thenReturn(sampleResponseDTO);

        mockMvc.perform(get("/api/v1/{studioId}/resource/{resourceId}", studioId, resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceId").value(resourceId))
                .andExpect(jsonPath("$.resourceName").value("Camera"));

        verify(resourceService).getResourceById(studioId, resourceId);
    }

    @Test
    void getAllResources_ReturnsOkAndPagedResources() throws Exception {
        Pageable pageable = PageRequest.of(0, 10); // simulate your controller's pagination
        Page<ResourceResponseDTO> page = new PageImpl<>(List.of(sampleResponseDTO), pageable, 1);

        when(resourceService.getAllResourcesForStudio(eq(studioId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/{studioId}/all-resources", studioId)
                        .param("PageNumber", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].resourceId").value(resourceId));

        verify(resourceService).getAllResourcesForStudio(eq(studioId), any(Pageable.class));
    }

    @Test
    void updateResourceById_ReturnsOkAndUpdatedResource() throws Exception {
        when(resourceService.updateResourceById(eq(studioId), eq(resourceId), any(ResourceRequestDTO.class))).thenReturn(sampleResponseDTO);

        mockMvc.perform(put("/api/v1/{studioId}/edit-resource/{resourceId}", studioId, resourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceId").value(resourceId));

        verify(resourceService).updateResourceById(eq(studioId), eq(resourceId), any(ResourceRequestDTO.class));
    }

    @Test
    void deleteResourceById_ReturnsNoContent() throws Exception {
        doNothing().when(resourceService).deleteResourceById(studioId, resourceId);

        mockMvc.perform(delete("/api/v1/{studioId}/delete-resource/{resourceId}", studioId, resourceId))
                .andExpect(status().isNoContent());

        verify(resourceService).deleteResourceById(studioId, resourceId);
    }

    @Test
    void getAvailableResources_ReturnsOkAndList() throws Exception {
        var start = LocalDateTime.now().minusDays(1);
        var end = LocalDateTime.now().plusDays(1);
        List<ResourceResponseDTO> list = List.of(sampleResponseDTO);
        when(resourceService.getALlAvailableResources(eq(studioId), eq(start), eq(end))).thenReturn(list);

        mockMvc.perform(get("/api/v1/{studioId}/available-resources", studioId)
                        .param("startDate", start.toString())
                        .param("endDate", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resourceId").value(resourceId));

        verify(resourceService).getALlAvailableResources(eq(studioId), eq(start), eq(end));
    }

    @Test
    void searchAllResources_ReturnsOkAndPagedResources() throws Exception {
        String query = "cam";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ResourceResponseDTO> page = new PageImpl<>(List.of(sampleResponseDTO), pageable, 1);
        when(resourceService.searchAllResources(eq(studioId), eq(query), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/{studioId}/search/resources", studioId)
                        .param("query", query)
                        .param("PageNumber", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].resourceId").value(resourceId));

        verify(resourceService).searchAllResources(eq(studioId), eq(query), any(Pageable.class));
    }

    @Test
    void createResource_InvalidBody_ReturnsBadRequest() throws Exception {
        ResourceRequestDTO invalidRequest = new ResourceRequestDTO(); // missing required fields

        mockMvc.perform(post("/api/v1/register/resource")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateResourceById_InvalidBody_ReturnsBadRequest() throws Exception {
        ResourceRequestDTO invalidRequest = new ResourceRequestDTO(); // missing required fields

        mockMvc.perform(put("/api/v1/{studioId}/edit-resource/{resourceId}", studioId, resourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
