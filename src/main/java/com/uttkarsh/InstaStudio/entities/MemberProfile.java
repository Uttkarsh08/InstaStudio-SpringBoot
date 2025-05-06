package com.uttkarsh.InstaStudio.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    private Long memberSalary;

    private Double averageRating;

    private String specialization;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Rating> ratings;
}