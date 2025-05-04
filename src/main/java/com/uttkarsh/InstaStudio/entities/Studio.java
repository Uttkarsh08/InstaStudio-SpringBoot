package com.uttkarsh.InstaStudio.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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

    private String studioName;

    private String studioAddress;

    private String studioCity;

    private String studioState;

    private String studioPinCode;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    @OneToMany(mappedBy = "studio", cascade = CascadeType.ALL)
    private Set<User> admins = new HashSet<>();

}
