package com.uttkarsh.InstaStudio.dto.event;

import com.uttkarsh.InstaStudio.entities.Event;
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
public class EventResponseDTO {

    private Long eventId;

    private String clientName;

    private String clientPhoneNo;

    private Set<Event> subEvents;

    private LocalDateTime eventStartDate;

    private LocalDateTime eventEndDate;

    private String eventLocation;

    private String eventCity;

    private String eventState;

    private boolean evenIsSaved;

}
