package com.uttkarsh.InstaStudio.dto.resource;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class ResourceRequestDTO {

    private String resourceName;

    private Long resourcePrice;

    private LocalDateTime resourceRegisteredAt;

    private Long studioId;

}
