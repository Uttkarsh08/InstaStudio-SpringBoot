package com.uttkarsh.InstaStudio.dto.resource;

import com.uttkarsh.InstaStudio.entities.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResourceResponseDTO {

    private Long resourceId;

    private String resourceName;

    private Long resourcePrice;

    private LocalDateTime resourceRegisteredAt;

    private Event resourceLastUsedEvent;

}
