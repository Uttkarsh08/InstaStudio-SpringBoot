package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.StudioCreationRequestDTO;
import com.uttkarsh.InstaStudio.dto.StudioCreationResponseDTO;
import com.uttkarsh.InstaStudio.entities.Studio;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.exceptions.AdminAlreadyAssignedException;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.StudioRepository;
import com.uttkarsh.InstaStudio.repositories.UserRepository;
import com.uttkarsh.InstaStudio.services.StudioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Base64;

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
    public StudioCreationResponseDTO assignAdminToStudio(Long studioId, Long userId) {

        Studio studio = studioRepository.findById(studioId)
                .orElseThrow(()-> new ResourceNotFoundException("Studio not found with ID: " + studioId));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with ID: " + userId));

        if(studio.getAdmins().contains(user)){
            throw new AdminAlreadyAssignedException("Can't assign same user to a Studio Twice");
        }
        if(user.getStudio() != null){
            throw new AdminAlreadyAssignedException("Admin is already assigned to a studio.");
        }

        user.setStudio(studio);
        userRepository.save(user);

        return mapper.map(studio, StudioCreationResponseDTO.class);
    }
}
