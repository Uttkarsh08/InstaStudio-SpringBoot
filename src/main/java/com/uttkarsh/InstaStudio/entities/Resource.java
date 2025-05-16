package com.uttkarsh.InstaStudio.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;

    private String resourceName;

    private LocalDateTime resourceRegisteredAt;

    private Long resourcePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studio_id")
    @JsonBackReference
    private Studio studio;

    @ManyToMany(mappedBy = "resources", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private Set<Event> events = new LinkedHashSet<>();
}
