package com.uttkarsh.InstaStudio.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubEventRequestDTO {

    private String eventType;

    private LocalDateTime eventStartDate;

    private LocalDateTime eventEndDate;

    private String eventLocation;

    private String eventCity;

    private String eventState;

    private Long studioId;

    private Set<Long> memberIds;

}