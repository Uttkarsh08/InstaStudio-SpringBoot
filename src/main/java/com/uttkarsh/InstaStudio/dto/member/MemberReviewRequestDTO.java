package com.uttkarsh.InstaStudio.dto.member;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberReviewRequestDTO {

    @NotNull(message = "Rating value can't be null")
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer ratingValue;

    @Size(max = 50, message = "Review must be at most 50 characters long")
    private String review;

    @NotBlank(message = "Member reviewer is required")
    private String reviewBy;

}
