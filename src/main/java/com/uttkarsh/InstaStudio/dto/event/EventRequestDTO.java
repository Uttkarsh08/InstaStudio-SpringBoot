package com.uttkarsh.InstaStudio.dto.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventRequestDTO {

    private String clientName;

    private String clientPhoneNo;

    private String eventType;

    private LocalDateTime eventStartDate;

    private LocalDateTime eventEndDate;

    private String eventLocation;

    private String eventCity;

    private String eventState;

    private boolean evenIsSaved;

    private Long studioId;

    private Set<Long> subEventsIds;

    private Set<Long> memberIds;

}
