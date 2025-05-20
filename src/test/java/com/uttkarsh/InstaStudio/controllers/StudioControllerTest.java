package com.uttkarsh.InstaStudio.controllers;

import com.uttkarsh.InstaStudio.dto.studio.StudioCreationRequestDTO;
import com.uttkarsh.InstaStudio.dto.studio.StudioCreationResponseDTO;
import com.uttkarsh.InstaStudio.exceptions.AdminAlreadyAssignedException;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.services.StudioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudioControllerTest {

    @InjectMocks
    private StudioController studioController;

    @Mock
    private StudioService studioService;

    private StudioCreationRequestDTO validRequestDTO;
    private StudioCreationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        validRequestDTO = new StudioCreationRequestDTO(
                "Pixel Studio", "123 Main Street", "Redrapur", "Uttarakhand", "263153", null
        );

        responseDTO = new StudioCreationResponseDTO(
                1L, "Pixel Studio", "123 Main Street", "Redrapur", "Uttarakhand", "263153"
        );
    }

    @Test
    void createStudio_shouldReturn200_whenValidRequest() {
        when(studioService.createStudio(validRequestDTO)).thenReturn(responseDTO);

        ResponseEntity<StudioCreationResponseDTO> response = studioController.createStudio(validRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(studioService).createStudio(validRequestDTO);
    }

    @Test
    void createStudio_shouldHandleNullFields_gracefullyIfServiceAccepts() {
        StudioCreationRequestDTO incompleteDTO = new StudioCreationRequestDTO(
                "", "", "", "", "abc123", null
        );

        when(studioService.createStudio(incompleteDTO)).thenReturn(responseDTO);

        ResponseEntity<StudioCreationResponseDTO> response = studioController.createStudio(incompleteDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void assignAdminToStudio_shouldReturn200_whenValidIds() {
        Long studioId = 1L;
        Long userId = 10L;

        when(studioService.assignAdminToStudio(studioId, userId)).thenReturn(responseDTO);

        ResponseEntity<StudioCreationResponseDTO> response = studioController.assignAdminToStudio(studioId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(studioService).assignAdminToStudio(studioId, userId);
    }

    @Test
    void assignAdminToStudio_shouldThrow_whenStudioNotFound() {
        Long studioId = 100L;
        Long userId = 10L;

        when(studioService.assignAdminToStudio(studioId, userId))
                .thenThrow(new ResourceNotFoundException("Studio not found with ID: " + studioId));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                studioController.assignAdminToStudio(studioId, userId));

        assertEquals("Studio not found with ID: " + studioId, exception.getMessage());
    }

    @Test
    void assignAdminToStudio_shouldThrow_whenUserNotFound() {
        Long studioId = 1L;
        Long userId = 999L;

        when(studioService.assignAdminToStudio(studioId, userId))
                .thenThrow(new ResourceNotFoundException("User not found with ID: " + userId));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                studioController.assignAdminToStudio(studioId, userId));

        assertEquals("User not found with ID: " + userId, exception.getMessage());
    }

    @Test
    void assignAdminToStudio_shouldThrow_whenAdminAlreadyAssigned() {
        Long studioId = 1L;
        Long userId = 5L;

        when(studioService.assignAdminToStudio(studioId, userId))
                .thenThrow(new AdminAlreadyAssignedException("Admin is already assigned to a studio."));

        AdminAlreadyAssignedException exception = assertThrows(AdminAlreadyAssignedException.class, () ->
                studioController.assignAdminToStudio(studioId, userId));

        assertEquals("Admin is already assigned to a studio.", exception.getMessage());
    }

    @Test
    void createStudio_shouldHandleImage_whenBase64Provided() {
        String base64 = Base64.getEncoder().encodeToString("dummy image".getBytes());
        validRequestDTO.setImageDataBase64(base64);

        when(studioService.createStudio(validRequestDTO)).thenReturn(responseDTO);

        ResponseEntity<StudioCreationResponseDTO> response = studioController.createStudio(validRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

}
