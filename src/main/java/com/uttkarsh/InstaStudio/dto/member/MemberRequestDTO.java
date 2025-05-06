package com.uttkarsh.InstaStudio.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberRequestDTO {

    private String memberEmail;

    private Long salary;

    private String specialization;

    private Long studioId;
}
