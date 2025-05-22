package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.studio.StudioCreationRequestDTO;
import com.uttkarsh.InstaStudio.dto.studio.StudioCreationResponseDTO;
import com.uttkarsh.InstaStudio.entities.Studio;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.exceptions.AdminAlreadyAssignedException;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.StudioRepository;
import com.uttkarsh.InstaStudio.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudioServiceImplTest {

    @InjectMocks
    private StudioServiceImpl studioService;

    @Mock
    private StudioRepository studioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper mapper;

    private StudioCreationRequestDTO validRequest;
    private Studio studio;
    private StudioCreationResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        validRequest = new StudioCreationRequestDTO(
                "Pixel Studio",
                "123 Main Street",
                "Redrapur",
                "Uttarakhand",
                "263153",
                null
        );

        studio = Studio.builder()
                .studioId(1L)
                .studioName("Pixel Studio")
                .studioAddress("123 Main Street")
                .studioCity("Redrapur")
                .studioState("Uttarakhand")
                .studioPinCode("263153")
                .build();

        responseDTO = new StudioCreationResponseDTO(
                1L,
                "Pixel Studio",
                "123 Main Street",
                "Redrapur",
                "Uttarakhand",
                "263153"
        );
    }


    @Test
    void createStudio_shouldSaveStudioAndReturnResponse_whenValidRequestWithoutImage() {
        when(studioRepository.save(any(Studio.class))).thenReturn(studio);
        when(mapper.map(studio, StudioCreationResponseDTO.class)).thenReturn(responseDTO);

        StudioCreationResponseDTO result = studioService.createStudio(validRequest);

        assertEquals(responseDTO, result);
        verify(studioRepository).save(any(Studio.class));
        verify(mapper).map(studio, StudioCreationResponseDTO.class);
    }

    @Test
    void createStudio_shouldDecodeImageAndSave_whenImageBase64Provided() {
        String base64Image = Base64.getEncoder().encodeToString("dummy image".getBytes());
        validRequest.setImageDataBase64(base64Image);

        ArgumentCaptor<Studio> studioCaptor = ArgumentCaptor.forClass(Studio.class);

        when(studioRepository.save(any(Studio.class))).thenReturn(studio);
        when(mapper.map(any(Studio.class), eq(StudioCreationResponseDTO.class))).thenReturn(responseDTO);

        StudioCreationResponseDTO result = studioService.createStudio(validRequest);

        verify(studioRepository).save(studioCaptor.capture());
        byte[] savedImage = studioCaptor.getValue().getImageData();

        assertNotNull(savedImage);
        assertArrayEquals(Base64.getDecoder().decode(base64Image), savedImage);
        assertEquals(responseDTO, result);
    }

    @Test
    void assignAdminToStudio_shouldAssignAdmin_whenValidInput() {
        Long studioId = 1L;
        Long userId = 10L;

        User user = new User();
        user.setUserId(userId);
        user.setUserType(UserType.ADMIN);
        user.setStudio(null);

        when(studioRepository.findById(studioId)).thenReturn(Optional.of(studio));
        when(userRepository.findByUserIdAndUserType(userId, UserType.ADMIN)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.map(studio, StudioCreationResponseDTO.class)).thenReturn(responseDTO);

        studioService.assignAdminToStudio(studioId, userId);
        assertEquals(studio, user.getStudio());
        verify(userRepository).save(user);
        verify(mapper).map(studio, StudioCreationResponseDTO.class);
    }

    @Test
    void assignAdminToStudio_shouldThrow_whenStudioNotFound() {
        Long studioId = 99L;
        Long userId = 10L;

        when(studioRepository.findById(studioId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                studioService.assignAdminToStudio(studioId, userId));

        assertEquals("Studio not found with ID: " + studioId, ex.getMessage());
    }

    // --------------------------------------------------------------------------------
    // 5. Assign Admin - User not found or not admin
    // --------------------------------------------------------------------------------
    @Test
    void assignAdminToStudio_shouldThrow_whenUserNotFoundOrNotAdmin() {
        Long studioId = 1L;
        Long userId = 10L;

        when(studioRepository.findById(studioId)).thenReturn(Optional.of(studio));
        when(userRepository.findByUserIdAndUserType(userId, UserType.ADMIN)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                studioService.assignAdminToStudio(studioId, userId));

        assertEquals("User not found with ID: " + userId, ex.getMessage());
    }

    @Test
    void assignAdminToStudio_shouldThrow_whenAdminAlreadyAssigned() {
        Long studioId = 1L;
        Long userId = 10L;

        User user = new User();
        user.setUserId(userId);
        user.setUserType(UserType.ADMIN);
        user.setStudio(new Studio()); // Already assigned

        when(studioRepository.findById(studioId)).thenReturn(Optional.of(studio));
        when(userRepository.findByUserIdAndUserType(userId, UserType.ADMIN)).thenReturn(Optional.of(user));

        AdminAlreadyAssignedException ex = assertThrows(AdminAlreadyAssignedException.class, () ->
                studioService.assignAdminToStudio(studioId, userId));

        assertEquals("Admin is already assigned to a studio.", ex.getMessage());
    }

    @Test
    void createStudio_shouldThrowRuntimeException_whenRepositoryFails() {
        when(studioRepository.save(any(Studio.class))).thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                studioService.createStudio(validRequest));

        assertEquals("DB error", ex.getMessage());
    }

    @Test
    void assignAdminToStudio_shouldPropagateException_whenMappingFails() {
        Long studioId = 1L;
        Long userId = 10L;

        User user = new User();
        user.setUserId(userId);
        user.setUserType(UserType.ADMIN);
        user.setStudio(null);

        when(studioRepository.findById(studioId)).thenReturn(Optional.of(studio));
        when(userRepository.findByUserIdAndUserType(userId, UserType.ADMIN)).thenReturn(Optional.of(user));
        when(mapper.map(any(Studio.class), eq(StudioCreationResponseDTO.class)))
                .thenThrow(new RuntimeException("Mapping failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                studioService.assignAdminToStudio(studioId, userId));

        assertEquals("Mapping failed", ex.getMessage());
    }
}
