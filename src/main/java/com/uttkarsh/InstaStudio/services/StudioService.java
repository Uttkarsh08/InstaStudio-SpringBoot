package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.dto.studio.StudioCreationRequestDTO;
import com.uttkarsh.InstaStudio.dto.studio.StudioCreationResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface StudioService {

    StudioCreationResponseDTO createStudio(StudioCreationRequestDTO requestDTO);

    void assignAdminToStudio(Long studioId, Long userId);

    String getImageForStudio(Long studioId);

}
