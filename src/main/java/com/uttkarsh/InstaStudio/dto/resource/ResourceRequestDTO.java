package com.uttkarsh.InstaStudio.dto.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class ResourceRequestDTO {

    @NotBlank(message = "Resource name can't be blank")
    private String resourceName;

    @NotNull(message = "Resource price can't be null")
    @Positive(message = "Resource price must be positive")
    private Long resourcePrice;

    @NotNull(message = "Studio Id is required")
    @Positive(message = "Studio Id must be positive")
    private Long studioId;

}
