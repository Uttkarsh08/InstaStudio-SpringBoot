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
public class MemberReviewResponseDTO {

    private Long ratingId;

    private Integer ratingValue;

    private String review;

    private String reviewBy;

    private LocalDateTime createdAt;
}
