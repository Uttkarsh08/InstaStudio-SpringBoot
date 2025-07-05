package com.uttkarsh.InstaStudio.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
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

    @Builder.Default
    @OneToMany(mappedBy = "parentEvent", cascade = CascadeType.ALL)
    @JsonManagedReference  // Serialize sub-events when serializing the main event
    private Set<Event> subEvents = new LinkedHashSet<>();

    @Column(nullable = false)
    private LocalDateTime eventStartDate;

    @Column(nullable = false)
    private LocalDateTime eventEndDate;

    @Column(nullable = false)
    private String eventLocation;

    @Column(nullable = false)
    private String eventCity;

    @Column(nullable = false)
    private String eventState;

    private boolean evenIsSaved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studio_id")
    @JsonBackReference
    private Studio studio;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "event_member",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<MemberProfile> members = new LinkedHashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "event_resource",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private Set<Resource> resources = new LinkedHashSet<>();

}
