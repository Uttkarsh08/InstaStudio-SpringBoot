package com.uttkarsh.InstaStudio.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "member_profile")
public class MemberProfile {

    @Id
    private Long memberId;

    @Column(nullable = false)
    private Long memberSalary;

    @Column(nullable = false)
    private String specialization;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Rating> ratings;

    @ManyToMany(mappedBy = "members", cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<Event> events = new LinkedHashSet<>();
}