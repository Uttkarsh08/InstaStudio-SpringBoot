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
public class MemberResponseDTO {

    private Long memberId;

    private String memberName;

    private String memberEmail;

    private String memberPhoneNo;

    private Long salary;

    private String specialization;

    private Long memberAverageRating;

}
