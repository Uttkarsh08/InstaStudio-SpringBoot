package com.uttkarsh.InstaStudio.dto.studio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudioCreationRequestDTO {

    @NotBlank(message = "Studio name can't be blank")
    @Size(max = 20, message = "Studio name must be at most 100 characters")
    private String studioName;

    @NotBlank(message = "Studio Address can't be blank")
    @Size(max = 20, message = "Studio Address must be at most 200 characters")
    private String studioAddress;

    @NotBlank(message = "Studio city can't be blank")
    @Size(max = 20, message = "Studio city must be at most 50 characters")
    private String studioCity;

    @NotBlank(message = "Studio state can't be blank")
    @Size(max = 20, message = "Studio state must be at most 50 characters")
    private String studioState;

    @NotBlank(message = "Studio pinCode can't be blank")
    @Pattern(regexp = "\\d{6}", message = "Studio pinCode must be exactly 6 digits")
    private String studioPinCode;

    private String imageDataBase64;

}
