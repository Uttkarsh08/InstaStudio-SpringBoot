package com.uttkarsh.InstaStudio.dto.user;

import com.uttkarsh.InstaStudio.dto.studio.StudioCreationRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminProfileSetupResponseDTO {

    private Long studioId;

    private Long userId;

}