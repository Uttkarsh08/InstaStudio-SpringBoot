package com.uttkarsh.InstaStudio.dto.studio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudioCreationRequestDTO {

    private String studioName;

    private String studioAddress;

    private String studioCity;

    private String studioState;

    private String studioPinCode;

    private String imageDataBase64;
}
