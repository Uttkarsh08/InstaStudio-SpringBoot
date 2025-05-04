package com.uttkarsh.InstaStudio.services;

import com.uttkarsh.InstaStudio.dto.StudioCreationRequestDTO;
import com.uttkarsh.InstaStudio.dto.StudioCreationResponseDTO;
import com.uttkarsh.InstaStudio.entities.Studio;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface StudioService {

    StudioCreationResponseDTO createStudio(StudioCreationRequestDTO requestDTO);

    StudioCreationResponseDTO assignAdminToStudio(Long studioId, Long userId);
}
