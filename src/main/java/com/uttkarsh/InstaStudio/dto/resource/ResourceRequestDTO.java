package com.uttkarsh.InstaStudio.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResourceRequestDTO {

    private String resourceName;

    private Long resourcePrice;

    private LocalDateTime resourceRegisteredAt;

    private Long studioId;

}
