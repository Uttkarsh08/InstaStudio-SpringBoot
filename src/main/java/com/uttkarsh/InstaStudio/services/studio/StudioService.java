package com.uttkarsh.InstaStudio.services.studio;

import com.uttkarsh.InstaStudio.dto.event.EventCreationResponseDTO;
import com.uttkarsh.InstaStudio.dto.event.EventListResponseDTO;
import com.uttkarsh.InstaStudio.dto.studio.StudioCreationRequestDTO;
import com.uttkarsh.InstaStudio.dto.studio.StudioCreationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface StudioService {

    StudioCreationResponseDTO createStudio(StudioCreationRequestDTO requestDTO);

    StudioCreationResponseDTO assignAdminToStudio(Long studioId, Long userId);

    EventCreationResponseDTO addEventToStudio(Long studioId, Long eventId);


}
