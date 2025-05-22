package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.studio.StudioCreationRequestDTO;
import com.uttkarsh.InstaStudio.dto.studio.StudioCreationResponseDTO;
import com.uttkarsh.InstaStudio.entities.Studio;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.exceptions.*;
import com.uttkarsh.InstaStudio.repositories.EventRepository;
import com.uttkarsh.InstaStudio.repositories.StudioRepository;
import com.uttkarsh.InstaStudio.repositories.UserRepository;
import com.uttkarsh.InstaStudio.services.StudioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor

public class StudioServiceImpl implements StudioService {

    private final StudioRepository studioRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public StudioCreationResponseDTO createStudio(StudioCreationRequestDTO requestDTO) {
        byte[] imageBytes = null;
        if (requestDTO.getImageDataBase64() != null) {
            imageBytes = Base64.getDecoder().decode(requestDTO.getImageDataBase64());
            log.info("image", requestDTO.getImageDataBase64());
        }else{
            System.out.println("image null");
        }

        Studio studio = Studio.builder()
                .studioName(requestDTO.getStudioName())
                .studioAddress(requestDTO.getStudioAddress())
                .studioCity(requestDTO.getStudioCity())
                .studioState(requestDTO.getStudioState())
                .studioPinCode(requestDTO.getStudioPinCode())
                .imageData(imageBytes)
                .build();

        Studio savedStudio = studioRepository.save(studio);
        return mapper.map(savedStudio, StudioCreationResponseDTO.class);

    }

    @Override
    public void assignAdminToStudio(Long studioId, Long userId) {

        Studio studio = studioRepository.findById(studioId)
                .orElseThrow(()-> new ResourceNotFoundException("Studio not found with ID: " + studioId));

        User user = userRepository.findByUserIdAndUserType(userId, UserType.ADMIN)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with ID: " + userId));

        if(user.getStudio() != null){
            throw new AdminAlreadyAssignedException("Admin is already assigned to a studio.");
        }

        user.setStudio(studio);
        userRepository.save(user);

    }

    @Override
    public String getImageForStudio(Long studioId) {

        Studio studio = studioRepository.findById(studioId)
                .orElseThrow(()-> new ResourceNotFoundException("Studio not found with ID: " + studioId));

        byte[] imageBytes = studio.getImageData();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
