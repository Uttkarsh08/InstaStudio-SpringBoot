package com.uttkarsh.InstaStudio.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String clientName;

    private String clientPhoneNo;

    private String eventType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference  // Prevent serialization of the parent event when serializing sub-events
    private Event parentEvent;

    @OneToMany(mappedBy = "parentEvent", cascade = CascadeType.ALL)
    @JsonManagedReference  // Serialize sub-events when serializing the main event
    private Set<Event> subEvents = new LinkedHashSet<>();

    private LocalDateTime eventStartDate;

    private LocalDateTime eventEndDate;

    private String eventLocation;

    private String eventCity;

    private String eventState;

    private boolean evenIsSaved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studio_id")
    @JsonBackReference
    private Studio studio;

    @ManyToMany
    @JoinTable(
            name = "event_member",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<MemberProfile> members = new LinkedHashSet<>();

}
