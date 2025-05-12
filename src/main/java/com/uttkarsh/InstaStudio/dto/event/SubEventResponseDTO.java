package com.uttkarsh.InstaStudio.dto.event;

import com.uttkarsh.InstaStudio.entities.Event;
import com.uttkarsh.InstaStudio.entities.MemberProfile;
import com.uttkarsh.InstaStudio.entities.Resource;
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
public class SubEventResponseDTO {

    private Long eventId;

    private Long parentEventId;

    private String eventType;

    private LocalDateTime eventStartDate;

    private LocalDateTime eventEndDate;

    private String eventLocation;

    private String eventCity;

    private String eventState;

    private Set<MemberProfile> members;

    private Set<Resource> resources;

}