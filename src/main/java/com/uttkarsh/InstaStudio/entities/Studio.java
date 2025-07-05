package com.uttkarsh.InstaStudio.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity(name = "studio")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Studio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studioId;

    @Column(nullable = false)
    private String studioName;

    @Column(nullable = false)
    private String studioAddress;

    @Column(nullable = false)
    private String studioCity;

    @Column(nullable = false)
    private String studioState;

    @Column(nullable = false)
    private String studioPinCode;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data")
    private byte[] imageData;

    @Builder.Default
    @OneToMany(mappedBy = "studio", cascade = CascadeType.ALL)
    private Set<User> users = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "studio", cascade = CascadeType.ALL)
    private Set<Event> events = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "studio", cascade = CascadeType.ALL)
    private Set<Resource> resources = new LinkedHashSet<>();

}
