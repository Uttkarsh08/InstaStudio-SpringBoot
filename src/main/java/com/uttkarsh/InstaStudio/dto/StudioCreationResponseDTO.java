package com.uttkarsh.InstaStudio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudioCreationResponseDTO {

    private Long studioId;

    private String studioName;

    private String studioAddress;

    private String studioCity;

    private String studioState;

    private String studioPinCode;

}